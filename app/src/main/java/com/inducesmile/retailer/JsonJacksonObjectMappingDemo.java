package com.inducesmile.retailer;

import android.annotation.SuppressLint;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.inducesmile.retailer.Car;
import com.inducesmile.retailer.CarEngine;
import com.inducesmile.retailer.CarFleet;

@SuppressLint("Registered")
public class JsonJacksonObjectMappingDemo extends AppCompatActivity {

    public void main(String args[]){


        String cachedir = getCacheDir().getPath() + File.separator + "brand/";
        Log.d("JPTR", "cache: " + cachedir);
        //Create androiddeft folder if it does not exist
        File directory = new File(cachedir);
        if (!directory.exists()) {
            directory.mkdirs();
        }


        String fileName_local = "product_list.json";
        String filePath_local = cachedir + fileName_local;


        ObjectMapper mapper = new ObjectMapper();

        /**
         * Read object from file
         */
        CarFleet value = null;
        try {
            value = mapper.readValue(new File(filePath_local), CarFleet.class);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Log.d("JPTR", String.valueOf(value));

    }

}