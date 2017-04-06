package com.warehouse.data.trans;

import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * ${DESCRIPTION}
 * package com.warehouse.data.trans
 *
 * @author zli [liz@yyft.com]
 * @version v1.0
 * @create 2017-03-23 13:58
 **/
public class ExportSqlFileThread {
    private ExecutorService executorService;

    public ExportSqlFileThread(int nThreads) {
        this.executorService = Executors.newFixedThreadPool(nThreads);
    }


    public void execute() {
        //通行证基本信息
        String fileName = "passport_base.sql";
        executorService.execute(new ConversionStandard.StreamRunner(new ConversionStandard.ProcessFile(fileName)) {
            @Override
            public String handle(Map result) {
                StringBuffer buffer = new StringBuffer();
                buffer.append("(").append(result.get("id")).append(",");
                buffer.append("'").append(result.get("username")).append("',");
                buffer.append("'").append(result.get("userpwd")).append("',");
                buffer.append("'").append(result.get("userpwd")).append("',");
                buffer.append(1).append(",");
                buffer.append("'").append(result.get("regtime")).append("'),");
                return buffer.toString();
            }

            @Override
            public String getTable() {
                StringBuffer buffer = new StringBuffer();
                buffer.append("insert into `passport_base`");
                buffer.append("(`uid`,");
                buffer.append("`username`,");
                buffer.append("`password`,");
                buffer.append("`old_password`,");
                buffer.append("`state`,");
                buffer.append("`create_time`)");
                buffer.append(" values ");
                return buffer.toString();
            }
        });

        fileName = "passport_bind.sql";
        executorService.execute(new ConversionStandard.StreamRunner(new ConversionStandard.ProcessFile(fileName)) {
            @Override
            public String handle(Map result) {
                StringBuffer buffer = new StringBuffer();
                buffer.append("(").append(result.get("id")).append(",");
                buffer.append("'").append(result.get("username")).append("'),");
                return buffer.toString();
            }

            @Override
            public String getTable() {
                StringBuffer buffer = new StringBuffer();
                buffer.append("insert into `passport_bind`");
                buffer.append("(`passport_uid`,");
                buffer.append("`mobile`)");
                buffer.append(" values ");
                return buffer.toString();
            }
        });


        fileName = "passport_info.sql";
        executorService.execute(new ConversionStandard.StreamRunner(new ConversionStandard.ProcessFile(fileName)) {
            @Override
            public String handle(Map result) {
                StringBuffer buffer = new StringBuffer();
                buffer.append("(").append(result.get("id")).append("),");
                return buffer.toString();
            }

            @Override
            public String getTable() {
                StringBuffer buffer = new StringBuffer();
                buffer.append("insert into `passport_info`");
                buffer.append("(`passport_uid`)");
                buffer.append(" values ");
                return buffer.toString();
            }
        });


        fileName = "passport_detail.sql";
        executorService.execute(new ConversionStandard.StreamRunner(new ConversionStandard.ProcessFile(fileName)) {
            @Override
            public String handle(Map result) {
                StringBuffer buffer = new StringBuffer();
                buffer.append("(").append(result.get("id")).append(",");
                buffer.append("'").append(result.get("pay_pwd")).append("',");
                buffer.append(result.get("integral")).append(",");
                buffer.append("'").append(result.get("qq")).append("',");
                buffer.append(result.get("grade")).append(",");
                if (result.get("nickname") == null)
                    buffer.append(result.get("nickname")).append("),");
                else
                    buffer.append("'").append(result.get("nickname")).append("'),");
                return buffer.toString();
            }

            @Override
            public String getTable() {
                StringBuffer buffer = new StringBuffer();
                buffer.append("insert into `passport_detail`");
                buffer.append("(`passport_uid`,");
                buffer.append("`pay_pwd`,");
                buffer.append("`integral`,");
                buffer.append("`qq`,");
                buffer.append("`grade`,");
                buffer.append("`nickname`)");
                buffer.append(" values ");
                return buffer.toString();
            }
        });

        fileName = "passport_memo.sql";
        executorService.execute(new ConversionStandard.StreamRunner(new ConversionStandard.ProcessFile(fileName)) {
            @Override
            public String handle(Map result) {
                StringBuffer buffer = new StringBuffer();
                buffer.append("(").append(result.get("id")).append(",");
                buffer.append(2130706433).append(",");
                buffer.append(1).append(",");
                buffer.append("'").append(result.get("regtime")).append("'),");
                return buffer.toString();
            }

            @Override
            public String getTable() {
                StringBuffer buffer = new StringBuffer();
                buffer.append("insert into `passport_memo`");
                buffer.append("(`passport_uid`,");
                buffer.append("`ip`,");
                buffer.append("`source`,");
                buffer.append("`create_time`)");
                buffer.append(" values ");
                return buffer.toString();
            }
        });
    }


}
