package com.taotao.content.service;

import com.taotao.pojo.LayuiResult;
import com.taotao.pojo.TbContent;
import com.taotao.pojo.ZtreeResult;

import java.util.List;

public interface ItemContentService {
    List<ZtreeResult> getZtreeResult(Long id);

    /**
     * 根据内容分类id查询内容信息
     * @param categoryId 内容分类id
     * @param page 当前页
     * @param limit 每一页显示条数
     * @return
     */
    LayuiResult findContentByCategoryId(Long categoryId, Integer page, Integer limit);

    /**
     * 根据内容id删除指定内容信息
     * @param tbContents 需要删除的内容集合
     * @param page 当前页
     * @param limit 每一页显示条数
     * @return
     */
    LayuiResult deleteContentByCategoryId(List<TbContent> tbContents, Integer page, Integer limit);
}
