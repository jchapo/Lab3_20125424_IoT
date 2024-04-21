package com.example.lab3_iot_20125424.dto;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class DtoRatings {
    @SerializedName("Source")
    private String source;
    @SerializedName("Value")
    private String value;

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}


