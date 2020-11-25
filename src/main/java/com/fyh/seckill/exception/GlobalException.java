package com.fyh.seckill.exception;

import com.fyh.seckill.result.CodeMsg;

/**自定义的全局异常类，返回codMsg中封装的异常码和对应的异常类型*/
public class GlobalException extends RuntimeException {
    private static final long servialVersionUID = 1L;
    private CodeMsg codeMsg;
    public GlobalException(CodeMsg codeMsg){
        super(codeMsg.toString());
        this.codeMsg = codeMsg;
    }

    public CodeMsg getCodeMsg() {
        return codeMsg;
    }
}
