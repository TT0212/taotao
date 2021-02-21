package com.taotao.sso.controller;

import com.taotao.pojo.LayuiResult;
import com.taotao.pojo.ZtreeResult;
import com.taotao.service.ItemCatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequestMapping("/itemCat")
public class ItemCatController {

    @Autowired
    private ItemCatService itemCatService;

    /**
     * 分类树形结构
     * @param id
     * @return
     */
    @RequestMapping("/showZtree")
    @ResponseBody
    public List<ZtreeResult> showZtree(@RequestParam(value = "id",defaultValue = "0") Long id){
       List<ZtreeResult> results= itemCatService.getZtreeResult(id);
        return results;
    }


}