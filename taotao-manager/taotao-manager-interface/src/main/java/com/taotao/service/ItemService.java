package com.taotao.service;

import com.taotao.pojo.LayuiResult;
import com.taotao.pojo.PictureResult;
import com.taotao.pojo.TaotaoResult;
import com.taotao.pojo.TbItem;

import java.util.Date;
import java.util.List;

/**
 * 商品服务
 */
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


    /**
     * 多条件搜索信息
     * @param page  当前页
     * @param limit  每页显示条数
     * @param title   商品名称
     * @param priceMin  商品单价最小值
     * @param priceMax 商品单价最大值
     * @param cId   分页id
     *
     * @return
     */

    LayuiResult gitItem(Integer page, Integer limit, String title, Integer priceMin, Integer priceMax, Long cId );

    /**
     * 上传图片到图片服务器
     * @param fileName 图片名
     * @param bytes    图片的字节数组
     * @return
     */
      PictureResult addPicture(String fileName, byte[] bytes);

    /**
     * 添加商品信息到数据库
     * @param tbItem  商品基本信息
     * @param itemDesc 商品描述信息
     * @return
     */
    TaotaoResult addItem(TbItem tbItem, String itemDesc);
}
