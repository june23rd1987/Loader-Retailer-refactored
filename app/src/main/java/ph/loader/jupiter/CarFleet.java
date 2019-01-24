package com.inducesmile.retailer;

import java.util.ArrayList;
import java.util.List;

public class CarFleet {

    private List<Car> cars = new ArrayList<Car>();


    public List<Car> getCars() {
        return cars;
    }

    public void setCars(List<Car> cars) {
        this.cars = cars;
    }


    @Override
    public String toString() {
        return "CarFleet [cars=" + cars + "]";
    }
}

