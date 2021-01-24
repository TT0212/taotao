package com.taotao.search.service;

import com.taotao.pojo.SearchResult;
import com.taotao.pojo.TaotaoResult;

import java.io.UnsupportedEncodingException;

public interface SearchService {
    TaotaoResult importSolr();

    /**
     * 根据页面传递的条件搜索商品信息
     * @param query 搜索内容
     * @param page 当前页
     * @return
     */
    SearchResult findItemSearch(String query, Integer page);
}
