package com.warehouse.data.trans;

import com.warehouse.data.datasource.DataSourceUtil;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

/**
 * package com.warehouse.data.trans
 *
 * @author zli [liz@yyft.com]
 * @version v1.0
 * @create 2017-03-23 10:23
 **/

public class ConversionStandard {


    public abstract static class StreamRunner implements Runnable {

        private ProcessFile processFile;
        private boolean stamp = true;

        public StreamRunner(ProcessFile processFile) {
            this.processFile = processFile;
        }

        @Override
        public void run() {
            try {
                Integer id = 0;
                DataSource dataSource = DataSourceUtil.getDataSource(DataSourceUtil.SourceType.DRUID);
                Connection connection = dataSource.getConnection();
                String table = getTable();
                //while (stamp) {
                StringBuffer buffer = new StringBuffer();
                buffer.append("select `id`,");
                buffer.append("`username`,");
                buffer.append("`userpwd`,");
                buffer.append("`regtime`,");
                buffer.append("`pay_pwd`,");
                buffer.append("`integral`,");
                buffer.append("`nickname`,");
                buffer.append("`qq`,");
                buffer.append("`grade` from `member` ");
                buffer.append("where `id` > ? order by `id` asc LIMIT 400000");
                System.out.println(buffer.toString());
                PreparedStatement ps = connection.prepareStatement(buffer.toString());
                ps.setInt(1, id);
                ResultSet rs = ps.executeQuery();
                ResultSetMetaData md = rs.getMetaData(); //得到结果集(rs)的结构信息，比如字段数、字段名等
                int columnCount = md.getColumnCount(); //返回此 ResultSet 对象中的列数
                List<String> list = new ArrayList<String>();
                buffer.delete(0, buffer.length());
                while (rs.next()) {
                    Map rowData = new HashMap(columnCount);
                    for (int i = 1; i <= columnCount; i++) {
                        rowData.put(md.getColumnName(i), rs.getObject(i));
                    }
                    String content = id == 0 ? (table + handle(rowData)) : handle(rowData);
                    id = (Integer) rowData.get("id");
                    buffer.append(content);
                }

                if (buffer.length() > 0) {
                    list.add(buffer.substring(0, buffer.length() - 1) + ";");
                    processFile.write(list);
                } else {
                    stamp = false;
                }

                // }
                connection.close();
            } catch (Exception e) {
                e.printStackTrace();
                Thread.currentThread().interrupt();
            }

        }


        public abstract String handle(Map result);

        public abstract String getTable();
    }


    public static class ProcessFile {
        private String fileName;

        public ProcessFile(String fileName) {
            this.fileName = fileName;
        }

        public void write(List<String> list) {
            try {
                File file = new File(fileName);
                if (!file.exists()) {
                    file.createNewFile();
                }
                Path path = Paths.get(fileName);
                Files.write(path, list, StandardCharsets.UTF_8, StandardOpenOption.APPEND);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

}
