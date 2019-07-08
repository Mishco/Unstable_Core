package com.mishco.core;

public class ControlRods {

    private Integer id;
    private Double percentInCore;


    public ControlRods(Integer id, Double length) {
        this.id = id;
        this.percentInCore = length;

    }

    public Integer getId() {
        return id;
    }

    public Double getPercentInCore() {
        return percentInCore;
    }

    public void setPercentInCore(Double percentInCore) {
        this.percentInCore = percentInCore;
    }


}
