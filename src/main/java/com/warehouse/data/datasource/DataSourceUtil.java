package com.warehouse.data.datasource;

import com.alibaba.druid.pool.DruidDataSourceFactory;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.sql.DataSource;

/**
 * package com.warehouse.data.datasource
 *
 * @author zli [liz@yyft.com]
 * @version v1.0
 * @create 2017-03-23 10:41
 **/
public class DataSourceUtil {
    public static String confile = "druid.properties";
    public static Properties properties = null;

    static {
        properties = new Properties();
        InputStream inputStream = null;
        try {
            //java应用
            confile = DataSourceUtil.class.getClassLoader().getResource("").getPath()
                    + confile;
            System.out.println(confile);
            File file = new File(confile);
            inputStream = new BufferedInputStream(new FileInputStream(file));
            properties.load(inputStream);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 获取数据源.
     *
     * @param sourceType
     *         druid、dbcp、tomcat
     *
     * @return
     *
     * @throws Exception
     */
    public static final DataSource getDataSource(SourceType sourceType) throws Exception {
        DataSource dataSource = null;
        switch (sourceType) {
            case DRUID:
                dataSource = DruidDataSourceFactory.createDataSource(properties);
                break;
        }
        return dataSource;
    }


    public enum SourceType {
        DRUID,
        DBCP,
        TOMCAT;
    }

    public static void main(String[] args) throws Exception {
        DataSourceUtil.getDataSource(SourceType.DRUID);
    }
}
