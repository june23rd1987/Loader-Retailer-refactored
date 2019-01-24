package com.inducesmile.retailer;

public class CarEngine {

    private String type;
    private String power;
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
    public String getPower() {
        return power;
    }
    public void setPower(String power) {
        this.power = power;
    }

    @Override
    public String toString() {
        return "CarEngine [type=" + type + ", power=" + power + "]";
    }

}