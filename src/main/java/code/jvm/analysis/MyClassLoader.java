package code.jvm.analysis;

import java.net.URL;
import java.net.URLClassLoader;

/**
 * ${DESCRIPTION}
 * package code.jvm.analysis
 *
 * @author zli [liz@yyft.com]
 * @version v1.0
 * @create 2017-06-05 17:30
 **/
public class MyClassLoader extends URLClassLoader {


    public MyClassLoader(URL[] urls) {
        super(urls);
    }


    @Override
    protected synchronized Class<?> loadClass(String name, boolean resolve)
            throws ClassNotFoundException {
        Class c = findLoadedClass(name);
        if (c == null) {
            try {
                c = findClass(name);
            } catch (Exception e) {
            }
        }
        if (c == null) {
            c = super.loadClass(name, resolve);
        }
        return c;
    }
}
