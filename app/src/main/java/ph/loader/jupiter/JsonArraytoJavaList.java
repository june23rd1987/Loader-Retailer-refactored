package com.inducesmile.retailer;

import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

/**
 * Java program to convert JSON array to String array in Java or List.
 *
 * @author Javin Paul
 */
public class JsonArraytoJavaList extends AppCompatActivity {

    private static Logger logger = Logger.getLogger(String.valueOf(JsonArraytoJavaList.class));


    public void main(String args[]) {
        // Converting JSON String array to Java String array

        //internal cache dir
        String cachedir = getCacheDir().getPath() + File.separator + "brand/";
        Log.d("JPTR", "cache: " + cachedir);
        //Create androiddeft folder if it does not exist
        File directory = new File(cachedir);
        if (!directory.exists()) {
            directory.mkdirs();
        }


        String fileName_local = "product_list.json";
        String filePath_local = cachedir + fileName_local;


        String jsonfile_local = null;
        try {
            jsonfile_local = getStringFromFile(filePath_local);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Convert to a JSON object to print data
        JsonParser jp_local = new JsonParser(); //from gson
        assert jsonfile_local != null;
        Log.d("JPTR", "FILE: " + jsonfile_local);
        JsonElement root_local = jp_local.parse(jsonfile_local); //Convert the input stream to a json element
        JsonObject rootobj_local = root_local.getAsJsonObject(); //May be an array, may be an object.
        String version_control_local = rootobj_local.get("version_control").getAsString(); //just grab the zipcode
        // input stream to read file - with 8k buffer

        Log.d("JPTR", "version_control_local: " + version_control_local);

        // Creating JSONArray from JSONObject
        JsonObject jsonObject_local_product_list = rootobj_local.get("product_list").getAsJsonObject();
        Log.d("JPTRjson1a", String.valueOf(jsonObject_local_product_list));


        String jsonStringArray = "[\"JSON\",\"To\",\"Java\"]";
        //String jsonStringArray = String.valueOf(jsonObject_local_product_list);

        //creating Gson instance to convert JSON array to Java array
        Gson converter = new Gson();

        Type type = new TypeToken<List<String>>(){}.getType();
        List<String> list =  converter.fromJson(jsonStringArray, type );

        //convert List to Array in Java
        String [] strArray = list.toArray(new String[0]);

        logger.info("Java List created from JSON String Array - example");
        logger.info("JSON String Array : " + jsonStringArray );
        logger.info("Java List : " + list);
        logger.info("String array : " + Arrays.toString(strArray));

        // let's now convert Java array to JSON array -
        String toJson = converter.toJson(list);
        logger.info("Json array created from Java List : " + toJson);

        // example to convert JSON int array into Java array and List of integer
        String json = "[101,201,301,401,501]";

        type = new TypeToken<List<Integer>>(){}.getType();
        List<Integer> iList = converter.fromJson(json, type);
        Integer[] iArray = iList.toArray(new Integer[0]);

        logger.info("Example to convert numeric JSON array to integer List and array in Java");
        logger.info("Numeric JSON array : " + json);
        logger.info("Java List of Integers : " + iList);
        logger.info("Integer array in Java : " + Arrays.toString(iArray));

        // Again, let's convert this Java int array back to Json numeric array
        String toJsonInt = converter.toJson(iList);
        logger.info("numeric JSON array create from Java collection : " + toJsonInt);


    }

    String convertStreamToString(InputStream is) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line = null;
        while ((line = reader.readLine()) != null) {
            sb.append(line).append("\n");
        }
        reader.close();
        return sb.toString();
    }

    public String getStringFromFile(String filePath) throws Exception {
        File fl = new File(filePath);
        FileInputStream fin = new FileInputStream(fl);
        String ret = convertStreamToString(fin);
        //Make sure you close all streams.
        fin.close();
        return ret;
    }



}
