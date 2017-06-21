package code.jvm.analysis;

import java.lang.reflect.Field;

/**
 * ${DESCRIPTION} package code.jvm.analysis
 *
 * @author zli [liz@yyft.com]
 * @version 1.0
 * @create 2017-06-2110:37
 **/
public class SubWorker extends AbstractWorker{


    public void doit(){
        System.out.println("xxxxx");
    }


    public static void main(String[] args) {
        SubWorker subWorker = new SubWorker();
        Class clazz = (Class) subWorker.getClass().getGenericSuperclass();

        try {
            Field field = clazz.getDeclaredField("t");
            field.setAccessible(true);
            Object o = field.get(subWorker);

            System.out.println(o);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

    }

}
