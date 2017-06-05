package com.warehouse.data.spring;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * ${DESCRIPTION}
 * package com.warehouse.data.spring
 *
 * @author zli [liz@yyft.com]
 * @version v1.0
 * @create 2017-06-02 17:34
 **/
public class BeanFactory {

    private static Map<String, BeanDefinition> beanDefinitionMap = new ConcurrentHashMap<String, BeanDefinition>();

    public Object getBean(String name) {
        return beanDefinitionMap.get(name).getBean();
    }


    public void registerBeanDefinition(String name, BeanDefinition beanDefinition) {
        beanDefinitionMap.put(name, beanDefinition);
    }
}
