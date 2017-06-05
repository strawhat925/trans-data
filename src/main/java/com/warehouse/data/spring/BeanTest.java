package com.warehouse.data.spring;

/**
 * ${DESCRIPTION}
 * package com.warehouse.data.spring
 *
 * @author zli [liz@yyft.com]
 * @version v1.0
 * @create 2017-06-02 17:37
 **/
public class BeanTest {

    public static void main(String[] args) {
        BeanFactory beanFactory = new BeanFactory();

        BeanDefinition beanDefinition = new BeanDefinition(new HelloWorldImpl());

        beanFactory.registerBeanDefinition("helloWorld", beanDefinition);


        HelloWorld helloWorld = (HelloWorld) beanFactory.getBean("helloWorld");
        helloWorld.sysHello();
    }
}
