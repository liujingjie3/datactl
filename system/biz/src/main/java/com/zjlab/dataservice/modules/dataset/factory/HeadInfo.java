package com.zjlab.dataservice.modules.dataset.factory;

import lombok.Data;

@Data
public class HeadInfo {
    private int id;
    private String key;
    private String value;

    public HeadInfo(int id, String key, String value) {
        this.id = id;
        this.key = key;
        this.value = value;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "HeadInfo{id=" + id + ", key='" + key + "', value='" + value + "'}";
    }
}
