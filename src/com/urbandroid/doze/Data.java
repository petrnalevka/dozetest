package com.urbandroid.doze;

import java.text.DecimalFormat;
import java.util.Date;

public class Data {

    private long ts;

    private float value;

    public Data(long ts, float value) {
        this.ts = ts;
        this.value = value;
    }

    public long getTs() {
        return ts;
    }

    public void setTs(long ts) {
        this.ts = ts;
    }

    public float getValue() {
        return value;
    }

    public void setValue(float value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return new Date(ts) + ": " + value;
    }
}
