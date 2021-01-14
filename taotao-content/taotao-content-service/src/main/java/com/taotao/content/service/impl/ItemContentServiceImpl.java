package com.taotao.content.service.impl;

import com.taotao.content.service.ItemContentService;
import com.taotao.mapper.TbContentCategoryMapper;
import com.taotao.mapper.TbContentMapper;
import com.taotao.pojo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class ItemContentServiceImpl implements ItemContentService {
    @Autowired
    private TbContentCategoryMapper tbContentCategoryMapper;
    @Autowired
    private TbContentMapper tbContentMapper;

    @Override
    public List<ZtreeResult> getZtreeResult(Long id) {
        List<ZtreeResult> results=new ArrayList<>();
        List<TbContentCategory> tbContentCategories=tbContentCategoryMapper.findContentByParenid(id);
        for (TbContentCategory tbcc:tbContentCategories) {
            ZtreeResult ztreeResult=new ZtreeResult();
            ztreeResult.setId(tbcc.getId());
            ztreeResult.setName(tbcc.getName());
            ztreeResult.setIsParent(tbcc.getIsParent());
            results.add(ztreeResult);

        }

        return results;
    }

    @Override
    public LayuiResult findContentByCategoryId(Long categoryId, Integer page, Integer limit) {
        LayuiResult result=new LayuiResult();
        result.setCode(0);
        result.setMsg("");
        int count=tbContentMapper.findContentByCount(categoryId);
        result.setCount(count);
        List<TbContent> data=tbContentMapper.findContentByPage(categoryId,(page-1)*limit,limit);
        result.setData(data);
        return result;
    }

    @Override
    public LayuiResult deleteContentByCategoryId(List<TbContent> tbContents, Integer page, Integer limit) {
        LayuiResult result=new LayuiResult();
        result.setCode(0);
        result.setMsg("");
        result.setCount(0);
        result.setData(null);
        if (tbContents.size()<=0){
            return result;
        }
        int i=tbContentMapper.deleteContentByCategoryId(tbContents);
        if (i<=0){
            return result;
        }

        Long categoryId=tbContents.get(0).getCategoryId();

        int count=tbContentMapper.findContentByCount(categoryId);

        if (count<=0){
            return result;
        }
        result.setCount(count);

        List<TbContent> data=tbContentMapper.findContentByPage(categoryId,(page-1)*limit,limit);
        result.setData(data);

        return result;
    }

    @Override
    public LayuiResult addContent(TbContent tbContent, Integer page, Integer limit) {
        LayuiResult result=new LayuiResult();
        result.setCode(0);
        result.setMsg("");

        Date date=new Date();
        tbContent.setCreated(date);
        tbContent.setUpdated(date);
       tbContentMapper.addContent(tbContent);
       Long categoryId=tbContent.getCategoryId();
        int count=tbContentMapper.findContentByCount(categoryId);
        List<TbContent> data=tbContentMapper.findContentByPage(categoryId,(page-1)*limit,limit);
        result.setCount(count);
        result.setData(data);

        return result;
    }

    @Override
    public List<Ad1Node> showAd1ode() {
        List<Ad1Node> ad1Nodes=new ArrayList<>();
        List<TbContent> tbContents= tbContentMapper.findContentByPage(89L,0,10);
        for (TbContent content:tbContents) {
            Ad1Node node=new Ad1Node();
            node.setSrcB(content.getPic2());
            node.setHeight(240);
            node.setAlt(content.getTitleDesc());
            node.setWidth(670);
            node.setSrc(content.getPic());
            node.setWidthB(550);
            node.setHref(content.getUrl());
            node.setHeightB(240);
            ad1Nodes.add(node);
        }

       return ad1Nodes;
    }
}
