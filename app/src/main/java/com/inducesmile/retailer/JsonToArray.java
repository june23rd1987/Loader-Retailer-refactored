package com.inducesmile.retailer;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.Arrays;

public class JsonToArray {

    public static void main(String args[]) throws Exception {
        try {

            String[] myArray = {"JavaFX", "HBase", "JOGL", "WebGL"};
            JSONArray jsArray = new JSONArray();
            for (String aMyArray : myArray)
            {
                jsArray.put(aMyArray);
            }
            //Log.d("JPTR", String.valueOf(jsArray));
            String[] array = new String[myArray.length];
            for (int i = 0; i < myArray.length; i++)
            {
                array[i] = (String) jsArray.get(i);
            }
            Log.d("JPTR", "Contents of the array :: " + Arrays.toString(array));
        }
        catch (JSONException e)
        {
            Log.e("JPTRcatch2", e.getMessage());
        }
    }
}
