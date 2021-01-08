package com.taotao.service;

import com.taotao.pojo.LayuiResult;
import com.taotao.pojo.TaotaoResult;
import com.taotao.pojo.TbItem;

import java.util.Date;
import java.util.List;

public interface ItemService {

    TbItem findTbItemById(Long itemId);

    LayuiResult findTbItemBypage(int page, int limit);

    /**
     * 批量修改商品信息
     * @param tbItems  需要修改的商品对象集合
     * @param type  如果为0代表下架 如果为1代表上架 如果为2代表删除
     * @param date 当前更新书籍
     * @return
     */
    TaotaoResult updateItem(List<TbItem> tbItems, int type, Date date);
}
