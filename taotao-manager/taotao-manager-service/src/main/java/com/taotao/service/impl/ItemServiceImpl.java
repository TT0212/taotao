package com.taotao.service.impl;

import com.taotao.pojo.LayuiResult;
import com.taotao.pojo.TaotaoResult;
import com.taotao.service.ItemService;
import com.taotao.mapper.TbItemMapper;
import com.taotao.pojo.TbItem;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@Service
public class ItemServiceImpl implements ItemService {

    @Autowired
    private TbItemMapper tbItemMapper;

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


}
