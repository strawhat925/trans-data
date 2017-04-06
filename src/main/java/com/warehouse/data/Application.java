package com.warehouse.data;


import com.warehouse.data.trans.ExportSqlFileThread;

/**
 * Hello world!
 */
public class Application {

    public static void main(String[] args) {
        ExportSqlFileThread exportThread = new ExportSqlFileThread(5);
        exportThread.execute();
    }
}
