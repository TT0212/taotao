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

    @RequestMapping("/{itemId}")
    @ResponseBody
    public TbItem getItemById(@PathVariable Long itemId) {
        //根据商品id查询商品信息
        TbItem tbItem = itemService.findTbItemById(itemId);
        return tbItem;
    }
    @RequestMapping("/showItemPage")
    @ResponseBody
    public LayuiResult showItemPage(Integer page, Integer limit){
        LayuiResult resuult=itemService.findTbItemBypage(page,limit);
        return resuult;
    }

    @RequestMapping("/itemDelete")
    @ResponseBody
    public TaotaoResult itemDelete(@RequestBody  List<TbItem> tbItems){
        Date date=new Date();
       TaotaoResult result= itemService.updateItem(tbItems,2,date);

        return result;
    }
    @RequestMapping("/commodityUpperShelves")
    @ResponseBody
    public TaotaoResult commodityUpperShelves(@RequestBody  List<TbItem> tbItems){
        Date date=new Date();
        TaotaoResult result= itemService.updateItem(tbItems,1,date);

        return result;
    }
    @RequestMapping("/commodityLowerShelves")
    @ResponseBody
    public TaotaoResult commodityLowerShelves(@RequestBody  List<TbItem> tbItems){
        Date date=new Date();
        TaotaoResult result= itemService.updateItem(tbItems,0,date);

        return result;
    }
}
