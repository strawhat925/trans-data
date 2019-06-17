package com.warehouse.data.http;

/**
 * ${DESCRIPTION}
 *
 * @author zli
 * @create 2018-03-29 11:05
 **/
public enum Method {
    GET,
    PUT,
    POST,
    DETELE,
    HEAD;

    public static Method lookup(String method) {
        if (method == null) {
            return null;
        }
        try {
            return valueOf(method);
        } catch (Exception e) {
            return null;
        }
    }
}
