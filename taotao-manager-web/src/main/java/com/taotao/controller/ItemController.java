package com.taotao.controller;

import com.taotao.pojo.LayuiResult;
import com.taotao.pojo.TaotaoResult;
import com.taotao.pojo.TbItem;
import com.taotao.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/item")
public class ItemController {

    @Autowired
    private ItemService itemService;
    /**
     *  根据商品id查询商品信息
     * @param itemId
     * @return
     */
    @RequestMapping("/{itemId}")
    @ResponseBody
    public TbItem getItemById(@PathVariable Long itemId) {
        TbItem tbItem = itemService.findTbItemById(itemId);
        return tbItem;
    }

    /**
     * 查询数据库tbitem表中 的总记录条数
     * @param page
     * @param limit
     * @return
     */
    @RequestMapping("/showItemPage")
    @ResponseBody
    public LayuiResult showItemPage(Integer page, Integer limit){
        LayuiResult resuult=itemService.findTbItemBypage(page,limit);
        return resuult;
    }

    /**
     * 勾选中删除
     * @param tbItems
     * @return
     */

    @RequestMapping("/itemDelete")
    @ResponseBody
    public TaotaoResult itemDelete(@RequestBody  List<TbItem> tbItems){
        Date date=new Date();
       TaotaoResult result= itemService.updateItem(tbItems,2,date);

        return result;
    }

    /**
     *商品上架
     * @param tbItems
     * @return
     */
    @RequestMapping("/commodityUpperShelves")
    @ResponseBody
    public TaotaoResult commodityUpperShelves(@RequestBody  List<TbItem> tbItems){
        Date date=new Date();
        TaotaoResult result= itemService.updateItem(tbItems,1,date);

        return result;
    }

    /**
     * 商品下架
     * @param tbItems
     * @return
     */
    @RequestMapping("/commodityLowerShelves")
    @ResponseBody
    public TaotaoResult commodityLowerShelves(@RequestBody  List<TbItem> tbItems){
        Date date=new Date();
        TaotaoResult result= itemService.updateItem(tbItems,0,date);

        return result;
    }
    /**
     * 多条件搜索信息
     * @param page  当前页
     * @param limit  每页显示条数
     * @param title   商品名称
     * @param priceMin  商品单价最小值
     * @param priceMax 商品单价最大值
     * @param cid   分页id
     * @return
     */

    @RequestMapping("/searchItem")
    @ResponseBody
    public LayuiResult searchItem(Integer page,Integer limit,String title,Integer priceMin,Integer priceMax,Long cId){
        LayuiResult result=itemService.gitItem(page,limit,title,priceMin,priceMax,cId);

        return  result;
    }
}
