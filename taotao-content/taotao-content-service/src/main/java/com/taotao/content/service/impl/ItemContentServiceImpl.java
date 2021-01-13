package com.taotao.content.service.impl;

import com.taotao.content.service.ItemContentService;
import com.taotao.mapper.TbContentCategoryMapper;
import com.taotao.mapper.TbContentMapper;
import com.taotao.pojo.LayuiResult;
import com.taotao.pojo.TbContent;
import com.taotao.pojo.TbContentCategory;
import com.taotao.pojo.ZtreeResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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
}
