package com.taotao.service.impl;

import com.taotao.pojo.LayuiResult;
import com.taotao.pojo.TaotaoResult;
import com.taotao.service.ItemService;
import com.taotao.mapper.TbItemMapper;
import com.taotao.pojo.TbItem;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;


@Service
public class ItemServiceImpl implements ItemService {

    /**
     *
     */
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
    public TaotaoResult updateItem(List<TbItem> tbItems, int i, Date date) {
        return null;
    }

}
