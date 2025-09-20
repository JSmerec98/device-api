package com.jansmerecki.domain;

public enum DeviceType {
    GATEWAY(0),
    SWITCH(1),
    ACCESS_POINT(2);

    private final int order;

    DeviceType(int order) {
        this.order = order;
    }

    public int getOrder() {
        return order;
    }
}
