package com.inducesmile.retailer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

@SuppressLint("Registered")
public class JSONParsingActivity extends AppCompatActivity {
    /** Called when the activity is first created. */

    TextView txtViewParsedValue;
    private JSONObject jsonObject;

    String strParsedValue = null;

    private String strJSONValue = "{\"FirstObject\":{\"attr1\":\"one value\" ,\"attr2\":\"two value\","
            + "\"sub\": { \"sub1\":[ {\"sub1_attr\":\"sub1_attr_value\" },{\"sub1_attr\":\"sub2_attr_value\" }]}}}";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_list_update);

        txtViewParsedValue = (TextView) findViewById(R.id.editTextUrl);

        try {
            parseJSON();

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void parseJSON() throws JSONException {
        jsonObject = new JSONObject(strJSONValue);

        JSONObject object = jsonObject.getJSONObject("FirstObject");
        String attr1 = object.getString("attr1");
        String attr2 = object.getString("attr2");

        strParsedValue = "Attribute 1 value => " + attr1;
        strParsedValue += "\n Attribute 2 value => " + attr2;

        JSONObject subObject = object.getJSONObject("sub");
        JSONArray subArray = subObject.getJSONArray("sub1");

        strParsedValue += "\n Array Length => " + subArray.length();

        for (int i = 0; i < subArray.length(); i++) {
            strParsedValue += "\n"
                    + subArray.getJSONObject(i).getString("sub1_attr")
                    .toString();
        }

        txtViewParsedValue.setText(strParsedValue);
    }
}

// Actual JSON Value
/*
 * { "FirstObject": { "attr1": "one value", "attr2": "two value", "sub": {
 * "sub1": [ { "sub1_attr": "sub1_attr_value" }, { "sub1_attr":
 * "sub2_attr_value" } ] } } }
 */

// Same JSON value in XML
/*
 * <FirstObject obj1="Object 1 value" obj2="Object 2 value"> <sub> <sub1
 * sub1_attr="sub1_attr_value" /> <sub1 sub1_attr="sub2_attr_value" /> </sub>
 * </FirstObject>
 */