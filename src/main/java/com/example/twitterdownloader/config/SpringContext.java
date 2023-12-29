package com.example.twitterdownloader.config;

import cn.hutool.core.bean.BeanException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class SpringContext implements ApplicationContextAware {
    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext context) throws BeanException{
        applicationContext = context;
    }

    public static <T> T getBean(Class<T> beanClass){
        return applicationContext.getBean(beanClass);
    }
}
