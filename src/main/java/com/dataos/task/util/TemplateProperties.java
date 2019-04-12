package com.dataos.task.util;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Create by libing on ${date}. </br>
 **/
@Component
@ConfigurationProperties(prefix = "template")
public class TemplateProperties {

    String charset;

    public String getCharset() {
        return charset;
    }

    public void setCharset(String charset) {
        this.charset = charset;
    }
}
