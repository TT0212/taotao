package com.taotao.content.service.impl;

import com.taotao.content.service.ItemContentService;
import com.taotao.mapper.TbContentCategoryMapper;
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
}
