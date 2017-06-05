package com.warehouse.data.spring;

/**
 * ${DESCRIPTION}
 * package com.warehouse.data.spring
 *
 * @author zli [liz@yyft.com]
 * @version v1.0
 * @create 2017-06-02 17:34
 **/
public class BeanDefinition {

    private Object bean;

    public BeanDefinition(){

    }

    public BeanDefinition(Object bean){
        this.bean = bean;
    }

    public Object getBean() {
        return bean;
    }

    public void setBean(Object bean) {
        this.bean = bean;
    }
}
