package com.fyh.seckill.service;

import com.alibaba.druid.util.StringUtils;
import com.fyh.seckill.dao.UserDao;
import com.fyh.seckill.exception.GlobalException;
import com.fyh.seckill.po.User;
import com.fyh.seckill.redis.RedisService;
import com.fyh.seckill.redis.UserKey;
import com.fyh.seckill.result.CodeMsg;
import com.fyh.seckill.util.MD5Util;
import com.fyh.seckill.util.UUIDUtil;
import com.fyh.seckill.vo.LoginVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

@Service
public class UserService {
    @Autowired
    private UserDao userDao;

    @Autowired
    private RedisService redisService;

    public static final String COOKIE_NAME_TOKEN = "token";

    /**
     * 根据用户ID获取详细信息，先去redis缓存中查找，
     * 如果找不到则去数据库中查找，并将查找到的对象写入缓存中
     * */
    public User getById(long userId){
        User user = redisService.get(UserKey.getById, "" + userId, User.class);
        if(user != null){
            return user;
        }else{
            user = userDao.getById(userId);
            if(user != null){
                redisService.set(UserKey.getById, ""+userId, user);
            }

        }
        return user;
    }

    /**更新用户密码
    * 涉及两次加密过程*/
    public boolean updatePassword(String token, long userId, String fromPass){
        User user = getById(userId);
        if(user == null){
            throw new GlobalException(CodeMsg.MOBILE_NOT_EXIST);
        }
        User toBeUpdate = new User();
        toBeUpdate.setId(userId);
        //更新数据库，从前端页面拿到的加密密码再次进行数据库层面的加密
        toBeUpdate.setPassword(MD5Util.formPassToDBPass(fromPass,user.getSalt()));
        userDao.updatePassword(toBeUpdate);
        //更新redis缓存
        redisService.delete(UserKey.getById,""+userId);
        user.setPassword(toBeUpdate.getPassword());
        redisService.set(UserKey.token,token,user);
        return true;
    }

    /**
     * 登陆验证
     * */
    public String login(HttpServletResponse response, LoginVo loginVo){
        if(loginVo == null){
            throw new GlobalException(CodeMsg.SESSION_ERROR);
        }
        String mobile = loginVo.getMobile();
        String formPass = loginVo.getPassword();
        User user = getById(Long.parseLong(mobile));
        if(user == null){
            throw new GlobalException(CodeMsg.MOBILE_NOT_EXIST);
        }
        //将前端传来的加密过的密码与数据库中对应的混淆盐再次进行加密，得到数据库中的密码，并与数据库中的真实密码进行对比
        String dbPass = user.getPassword();
        String dbSalt = user.getSalt();
        String toDBPass = MD5Util.formPassToDBPass(formPass, dbSalt);
        if(!toDBPass.equals(dbPass)){
            throw new GlobalException(CodeMsg.PASSWORD_ERROR);
        }
        //实现分布式session
        String token = UUIDUtil.uuid();//为每个id生成唯一的标识码
        addCookie(response, token, user);
        return token;
    }

    /**
     * 将token（每个用户的唯一标识码）做为key，用户信息做为value 存入redis模拟session
     * 同时将token存入cookie，保存登录状态
     */
    public void addCookie(HttpServletResponse response, String token, User user) {
        redisService.set(UserKey.token, ""+token, user);
        Cookie cookie = new Cookie(COOKIE_NAME_TOKEN, token);
        cookie.setMaxAge(UserKey.token.getExpireSeconds());
        cookie.setPath("/");//设置到网站根目录，在同一服务器内都可以访问到
        response.addCookie(cookie);
    }

    /**
     * 根据token获取用户信息
     */
    public User getByToken(HttpServletResponse response, String token) {
        if (StringUtils.isEmpty(token)) {
            return null;
        }
        User user = redisService.get(UserKey.token, ""+token, User.class);
        //延长有效期，有效期等于最后一次操作+有效期
        if (user != null) {
            addCookie(response, token, user);
        }
        return user;
    }
    


}
