package com.inducesmile.retailer;

public class Player {

    private String name;
    private int img;

    Player(String name, int img) {
        // TODO Auto-generated constructor stub

        this.name = name;
        this.img = img;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    int getImg() {
        return img;
    }

    public void setImg(int img) {
        this.img = img;
    }
}

