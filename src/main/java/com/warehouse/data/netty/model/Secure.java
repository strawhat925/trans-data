package com.warehouse.data.netty.model;

import java.io.Serializable;

public class Secure implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = -2108336644101910071L;
    /**
     * 验证 token
     */
    private String token;

    private boolean autoSuccess;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public boolean isAutoSuccess() {
        return autoSuccess;
    }

    public void setAutoSuccess(boolean autoSuccess) {
        this.autoSuccess = autoSuccess;
    }


}
