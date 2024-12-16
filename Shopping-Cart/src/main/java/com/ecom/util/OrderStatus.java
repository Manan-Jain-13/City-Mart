package com.ecom.util;

public enum OrderStatus {
    IN_PROGRESS(1,"In Progress"),
    ORDER_RECEIVED(2,"received"),
    PRODUCT_PACKED(3,"packed"),
    OUT_FOR_SHIPPING(4,"shipped successfully"),
    DELIVER(5,"delivered"),
    CANCEL(6,"cancelled"),
    SUCCESS(7,"placed Successfully");

    private Integer id;

    private String name;

    OrderStatus(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
