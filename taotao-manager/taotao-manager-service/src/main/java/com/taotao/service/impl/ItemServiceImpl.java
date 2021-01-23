package com.taotao.service.impl;

import com.taotao.constant.FTPConstant;
import com.taotao.mapper.TbItemDescMapper;
import com.taotao.pojo.*;
import com.taotao.service.ItemService;
import com.taotao.mapper.TbItemMapper;

import com.taotao.utils.FtpUtil;
import com.taotao.utils.IDUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@Service
public class ItemServiceImpl implements ItemService {

    @Autowired
    private TbItemMapper tbItemMapper;
    @Autowired
    private TbItemDescMapper tbItemDescMapper;

    @Override
    public TbItem findTbItemById(Long itemId) {
        TbItem tbItem=tbItemMapper.findTbItemById(itemId);
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
    public TaotaoResult addItem(TbItem tbItem, String itemDesc) {
        //生成一个商品id
        Long itemId=IDUtils.genItemId();
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

        return TaotaoResult.build(200,"添加商品成功");
    }

}
