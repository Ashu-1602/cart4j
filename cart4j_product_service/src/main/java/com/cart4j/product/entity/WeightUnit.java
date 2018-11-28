package com.cart4j.product.entity;

public enum WeightUnit {
    KG("kg"),
    G("g"),
    POUND("lb"),
    OUNCE("oz");

    private String value;

    WeightUnit(String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }
}
