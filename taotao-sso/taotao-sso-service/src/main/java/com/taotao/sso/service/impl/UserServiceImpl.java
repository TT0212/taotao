package com.taotao.sso.service.impl;

import com.taotao.constant.RedisConstant;
import com.taotao.mapper.TbUserMapper;
import com.taotao.pojo.TaotaoResult;
import com.taotao.pojo.TbUser;
import com.taotao.sso.service.JedisClient;
import com.taotao.sso.service.UserService;
import com.taotao.utils.JsonUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import java.util.Date;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private TbUserMapper tbUserMapper;

    @Autowired
    private JedisClient jedisClient;
    @Override
    public TaotaoResult getCheckDateResult(String param, Integer type) {
        int i=0;
        if (type==1){
            i=tbUserMapper.checkUserName(param);
        }else if (type==2){
           i=tbUserMapper.checkPhone(param);
        }else if (type==3){
            i=tbUserMapper.checkEmail(param);
        }else {
            return TaotaoResult.build(500,"请输入合法的数字");
        }
        if (i!=0){
            return TaotaoResult.ok(false);
        }
        return TaotaoResult.ok(true);
    }

    @Override
    public TaotaoResult addregister(TbUser tbUser) {
        //username为空
        if (StringUtils.isBlank(tbUser.getUserName())){
            return TaotaoResult.build(500,"用户名不能为空");
        }
        //校验用户名是否可用
        TaotaoResult result = getCheckDateResult(tbUser.getUserName(), 1);
        if (!(boolean)result.getData()){
            return TaotaoResult.build(500,"用户名已经存在");
        }
        //password为空
        if (StringUtils.isBlank(tbUser.getPassWord())){
            return TaotaoResult.build(500,"密码不能为空");
        }

        //Phone为空
        if (StringUtils.isBlank(tbUser.getPhone())){
            return TaotaoResult.build(500,"手机号不能为空");
        }
        //校验手机号是否可用
            result=getCheckDateResult(tbUser.getPhone(),2);
            if (!(boolean)result.getData()){
                return TaotaoResult.build(500,"手机号已经被使用");
            }
        //password为空
        if (StringUtils.isBlank(tbUser.getEmail())){
            return TaotaoResult.build(500,"邮箱号不能为空");
        }

        //校验邮箱号是否可用
            result = getCheckDateResult(tbUser.getEmail(), 3);
            if (!(boolean) result.getData()) {
                return TaotaoResult.build(500, "邮箱号已经被使用");
            }
        Date date=new Date();
        tbUser.setCreated(date);
        tbUser.setUpdated(date);
        //mb5加密
        String mb5Pass= DigestUtils.md5DigestAsHex(tbUser.getPassWord().getBytes());
        tbUser.setPassWord(mb5Pass);
        int i=tbUserMapper.addregister(tbUser);
        if(i==0){
            return TaotaoResult.build(500,"注册失败");
        }
        return TaotaoResult.ok();
    }

    @Override
    public TaotaoResult getTokenNameAndPass(String userName, String passWord) {
        String token= UUID.randomUUID().toString();
        token= token.replace("-","");
        int rand=(int)(Math.random()*100)+1;
        TbUser tbUser=tbUserMapper.findUserByNameAndPass(userName,DigestUtils.md5DigestAsHex(passWord.getBytes()));
        if (tbUser==null){
            jedisClient.set(RedisConstant.USER_INFO +":"+token, JsonUtils.objectToJson(tbUser));
            jedisClient.expire(RedisConstant.USER_INFO+":"+token,RedisConstant.USER_SESSION_EXPIRE+rand);
            return TaotaoResult.build(500,"用户名或密码有误");
        }

        tbUser.setPassWord(null);
        jedisClient.set(RedisConstant.USER_INFO +":"+token, JsonUtils.objectToJson(tbUser));
        jedisClient.expire(RedisConstant.USER_INFO+":"+token,RedisConstant.USER_SESSION_EXPIRE+rand);


        return TaotaoResult.ok(token);
    }

    @Override
    public TaotaoResult getUserByToken(String token) {
        String json = jedisClient.get(RedisConstant.USER_INFO +":"+token);
        if (StringUtils.isBlank(json)){
            return TaotaoResult.build(500,"用户登录已经过期，请重新登录");
        }
        TbUser tbUser = JsonUtils.jsonToPojo(json, TbUser.class);
        return TaotaoResult.ok(tbUser);
    }

    @Override
    public TaotaoResult deleteToken(String token) {
        Long del = jedisClient.del(RedisConstant.USER_INFO + ":" + token);
        if (del<=0){
            return TaotaoResult.build(500,"退出失败");
        }
        return TaotaoResult.ok();
    }
}
