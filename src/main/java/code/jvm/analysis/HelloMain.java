package code.jvm.analysis;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;

/**
 * ${DESCRIPTION}
 * package code.jvm.analysis
 *
 * @author zli [liz@yyft.com]
 * @version v1.0
 * @create 2017-06-05 17:27
 **/
public class HelloMain {
    private final String path = "E:\\IdeaProjects\\trans-data\\target\\classes\\";
    private long lastTime;
    private MyClassLoader myClassLoader;
    private Object worker;

    public static void main(String[] args) throws Exception {
       HelloMain helloMain = new HelloMain();
        System.out.println(helloMain.getClass().getClassLoader());
       helloMain.execute();

    }


    public void execute() throws Exception {
        while (true){
            if(checkIsNeelLoad()){
                System.out.println("--------reload---------------");
                reload();
            }

            invokeMethod();
            Thread.sleep(1000);
        }
    }


    public void invokeMethod() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method method = worker.getClass().getDeclaredMethod("doit", String.class);
        method.invoke(worker, "xxxxxxxx");
    }


    public void reload() throws Exception {
        myClassLoader = new MyClassLoader(new URL[]{new URL("file:" + path)});
        worker = myClassLoader.loadClass("code.jvm.analysis.Worker").newInstance();
        System.out.println(worker.getClass().getClassLoader());
    }


    public boolean checkIsNeelLoad(){
        File file = new File(path + "code\\jvm\\analysis\\Worker.class");
        long newTime = file.lastModified();
        if(lastTime < newTime){
            lastTime = newTime;
            return true;
        }

        return false;
    }
}
