package com.taotao.item.listener;

import com.taotao.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;
import javax.jms.Message;
import javax.jms.MessageListener;
import java.io.Writer;

public class MyMessageListener implements MessageListener {
    @Autowired
    private ItemService itemService;
    @Autowired
    private FreeMarkerConfigurer freeMarkerConfigurer;
    private Writer writer;
    @Override
    public void onMessage(Message message) {
//
    }
}
