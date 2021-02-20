package com.taotao.sso.service;

import com.taotao.pojo.TaotaoResult;
import com.taotao.pojo.TbUser;

public interface UserService {

    /**
     * 校验数据的方法
     * @param param 需要校验的数据
     * @param type 类型 1.用户名 2.手机号 3.邮箱地址
     * @return
     */
    TaotaoResult getCheckDateResult(String param, Integer type);

    /**
     * 用户注册方法
     * @param tbUser 用户信息
     * @return
     */
    TaotaoResult addregister(TbUser tbUser);

    /**
     * 登录方法
     * @param userName 用户账号
     * @param passWord 用户密码
     * @return
     */
    TaotaoResult getTokenNameAndPass(String userName, String passWord);

    /**
     * 根据token返回用户信息
     * @param token
     * @return
     */
    TaotaoResult getUserByToken(String token);

    /**
     * 删除token来退出登录
     * @param token
     * @return
     */
    TaotaoResult deleteToken(String token);
}
