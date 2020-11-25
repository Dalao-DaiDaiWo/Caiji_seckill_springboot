package com.fyh.seckill.dao;

import com.fyh.seckill.po.User;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;


@Repository
public interface UserDao {

    User getById(@Param("id")long id);

    void updatePassword(User toBeUpdate);
}
