package com.ejn.cmov.acmecafe.mobile.data.model;

import java.io.Serializable;

public class VoucherModel implements Serializable {
    private String id;
    private Integer type;

    public VoucherModel(String id, Integer type) {
        this.id = id;
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public Integer getType() {
        return type;
    }
}
