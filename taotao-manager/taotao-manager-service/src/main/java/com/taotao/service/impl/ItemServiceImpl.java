package com.taotao.service.impl;

import com.taotao.constant.FTPConstant;
import com.taotao.constant.RedisConstant;
import com.taotao.mapper.TbItemDescMapper;
import com.taotao.mapper.TbItemMapper;
import com.taotao.mapper.TbItemParamMapper;
import com.taotao.pojo.*;
import com.taotao.service.ItemService;
import com.taotao.service.JedisClient;
import com.taotao.utils.FtpUtil;
import com.taotao.utils.IDUtils;
import com.taotao.utils.JsonUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Service;
import javax.jms.*;
import java.io.ByteArrayInputStream;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class ItemServiceImpl implements ItemService {
    @Autowired
    private TbItemMapper tbItemMapper;
    @Autowired
    private TbItemDescMapper tbItemDescMapper;
    @Autowired
    private JmsTemplate jmsTemplate;
    @Autowired
    private Destination destination;
    @Autowired
    private JedisClient jedisClient;
    @Autowired
    private TbItemParamMapper tbItemParamMapper;



    @Override
    public TbItem findTbItemById(Long itemId) {
        String json = jedisClient.get(RedisConstant.ITEM_INFO +":"+itemId);
        int rand = (int)(Math.random()*1000)+1;
        //当json不为null 有数据的时候
        if(StringUtils.isNotBlank(json)){
            if(json.equals("null")){
                return null;
            }else{
                TbItem tbItem = JsonUtils.jsonToPojo(json, TbItem.class);
                jedisClient.expire(RedisConstant.ITEM_INFO+":"+itemId,RedisConstant.REDIS_TIME_OUT+rand);
                return tbItem;
            }
        }
        TbItem tbItem = tbItemMapper.findTbItemById(itemId);
        if(tbItem==null){
            jedisClient.set(RedisConstant.ITEM_INFO +":"+itemId,"null");
            jedisClient.expire(RedisConstant.ITEM_INFO +":"+itemId,RedisConstant.REDIS_TIME_OUT);
        }else {
            //吧查询数据库得到的结果集存入到redis缓存中
            jedisClient.set(RedisConstant.ITEM_INFO +":"+itemId, JsonUtils.objectToJson(tbItem));
            jedisClient.expire(RedisConstant.ITEM_INFO +":"+itemId,RedisConstant.REDIS_TIME_OUT+rand);
        }

        return tbItem;
    }

    @Override
    public LayuiResult findTbItemBypage(int page, int limit) {
        LayuiResult result=new LayuiResult();
        result.setCode(0);
        result.setMsg("");
      int count=  tbItemMapper.findTbItemByCount();
      result.setCount(count);
     List<TbItem> data= tbItemMapper.findTbItemByPage((page-1)*limit,limit);
       result.setData(data);
        return result;
    }

    @Override
    public TaotaoResult updateItem(List<TbItem> tbItems, int type, Date date) {

       if (tbItems.size()<=0){
           return TaotaoResult.build(500,"请先勾选，在操作",null);
       }
       //需要修改的商品id
        List<Long> ids=new ArrayList<>();
        for (TbItem tbItem:tbItems) {
            ids.add(tbItem.getId());
        }
         int count=tbItemMapper.updateItemByIds(ids,type,date);
        /**
         * count>0 代表我们修改了数据库里面的数据
         */
        if (count>0 && type==0){
            return  TaotaoResult.build(200,"商品下架成功",null);
        }else if (count>0 && type==1){
            return  TaotaoResult.build(200,"商品上架成功",null);

        }else  if (count>0 && type==2){
            return  TaotaoResult.build(200,"删除成功",null);
        }
        return  TaotaoResult.build(500,"商品修改失败",null);
    }

    @Override
    public LayuiResult gitItem(Integer page, Integer limit, String title, Integer priceMin, Integer priceMax, Long cId) {
        if (priceMin==null){
            priceMin=0;
        }
        if (priceMax==null){
            priceMax=10000000;
        }
        LayuiResult result=new LayuiResult();
        result.setCode(0);
        result.setMsg("");
        int count=tbItemMapper.findTbItemByLikeConut( title,priceMin,priceMax,cId);
        //设置查询结果集的数量
        result.setCount(count);
       List<TbItem> data= tbItemMapper.findTbItemByLike( title,priceMin,priceMax,cId,(page-1)*limit,limit);
        result.setData(data);
        return result;
    }

    @Override
    public PictureResult addPicture(String fileName, byte[] bytes) {
        Date date=new Date();
        SimpleDateFormat format=new SimpleDateFormat("yy/MM/dd");
        //以每天日期为文件夹，用来存放图片
        String filePath=format.format(date);
        //随机生成一个字符串 本身的名字只要后缀名
        String filenmae= IDUtils.genImageName()+fileName.substring(fileName.lastIndexOf("."));
        ByteArrayInputStream bis=new ByteArrayInputStream(bytes);
        boolean b= FtpUtil.uploadFile(FTPConstant.FIP_ADDRERSS,FTPConstant.FTP_PORT,FTPConstant.FTP_USERNAME,FTPConstant.FTP_PASSWORD,FTPConstant.FTP_UPLOAD_PATH,filePath,filenmae,bis);
        if (b){
            PictureResult result=new PictureResult();
            result.setCode(0);
            result.setMsg("");
            PicturData data=new PicturData();
            data.setSrc(FTPConstant.IMAGE_BASE_URL+"/"+filePath+"/"+filenmae);
            result.setData(data);
            return result;
        }
        return null;
    }

    @Override
    public TaotaoResult addItem(TbItem tbItem, String itemDesc,List<Integer> paramKeyIds,List<String> paramValue) {
        //生成一个商品id
        final Long itemId=IDUtils.genItemId();
        //生成一个当前时间作为创建时间和修改时间
        Date date=new Date();
        tbItem.setId(itemId);
        tbItem.setStatus((byte)1);
        tbItem.setCreated(date);
        tbItem.setUpdated(date);
        //商品基本信息准备完成
        int i=tbItemMapper.addItem(tbItem);
        if (i<=0){
            return TaotaoResult.build(500,"添加商品基本信息失败");
        }
        TbItemDesc tbItemDesc=new TbItemDesc();
        tbItemDesc.setItemId(itemId);
        tbItemDesc.setUpdated(date);
        tbItemDesc.setUpdated(date);
        tbItemDesc.setItemDesc(itemDesc);
        //商品描述信息准备完成
        int j=tbItemDescMapper.addItemDesc(tbItemDesc);
        if (j<=0){
            return TaotaoResult.build(500,"添加商品描述失败");
        }
        for (int k = 0; k < paramKeyIds.size(); k++) {
            TbItemParamValue tbItemParamValue=new TbItemParamValue();
            tbItemParamValue.setItemId(itemId);
            tbItemParamValue.setParamId(paramKeyIds.get(k));
            tbItemParamValue.setParamValue(paramValue.get(k));
            int z = tbItemParamMapper.addGroupValue(tbItemParamValue);
            if (z<=0){
                return TaotaoResult.build(500,"添加商品规格参数失败");
            }
        }

        jmsTemplate.send(destination, new MessageCreator() {
            @Override
            public Message createMessage(Session session) throws JMSException {
                TextMessage textMessage = session.createTextMessage();
                textMessage.setText(itemId+"");
                return textMessage;
            }
        });

        return TaotaoResult.build(200,"添加商品成功");
    }

    @Override
    public TbItemDesc findTbItemDescByItemId(Long itemId) {
        int rand = (int) (Math.random() * 1000) + 1;
        String json = jedisClient.get(RedisConstant.ITEM_DESC);
        if (StringUtils.isNotBlank(json)) {
            if (json.equals(null)) {
                return null;
            } else {
                TbItemDesc tbItemDesc = JsonUtils.jsonToPojo(json, TbItemDesc.class);
                jedisClient.expire(RedisConstant.ITEM_DESC, RedisConstant.REDIS_TIME_OUT + rand);
                return tbItemDesc;
            }
        }
            TbItemDesc tbItemDesc= tbItemDescMapper.findTbItemDescByItemId(itemId);
        if (tbItemDesc==null){
            jedisClient.set(RedisConstant.ITEM_DESC,"null");
            jedisClient.expire(RedisConstant.ITEM_DESC, RedisConstant.REDIS_TIME_OUT);
        }else {
            //把数据库查询得到的结构存到redis缓存中
            jedisClient.set(RedisConstant.ITEM_DESC, JsonUtils.objectToJson(tbItemDesc));
            jedisClient.expire(RedisConstant.ITEM_DESC, RedisConstant.REDIS_TIME_OUT + rand);
        }

        return tbItemDesc;
    }

    @Override
    public List<TbItemParamGroup> findTbItemGroupByItemId(Long itemId) {
        int rand = (int) (Math.random() * 1000) + 1;
        String json = jedisClient.get(RedisConstant.ITEM_PARAM);
        if (StringUtils.isNotBlank(json)) {
            if (json.equals(null)) {
                return null;
            } else {
                List<TbItemParamGroup> tbItemParamGroups = JsonUtils.jsonToPojo(json, List.class);
                jedisClient.expire(RedisConstant.ITEM_PARAM, RedisConstant.REDIS_TIME_OUT + rand);
                return tbItemParamGroups;
            }
        }
        List<TbItemParamGroup> list= tbItemParamMapper.findTbItemGroupByItemId(itemId);
        if (list==null){
            jedisClient.set(RedisConstant.ITEM_PARAM,"null");
            jedisClient.expire(RedisConstant.ITEM_PARAM, RedisConstant.REDIS_TIME_OUT);
        }else {
            //把数据库查询得到的结构存到redis缓存中
            jedisClient.set(RedisConstant.ITEM_PARAM, JsonUtils.objectToJson(list));
            jedisClient.expire(RedisConstant.ITEM_PARAM, RedisConstant.REDIS_TIME_OUT + rand);
        }
        return list;
    }

}
