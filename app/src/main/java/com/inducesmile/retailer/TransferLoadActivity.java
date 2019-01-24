package com.inducesmile.retailer;


import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.ActivityOptions;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

@SuppressWarnings("ResultOfMethodCallIgnored")
@SuppressLint("Registered")
public class TransferLoadActivity extends AppCompatActivity {

    //declare globally, this can be any int
    public final int PICK_CONTACT = 2015;


    //spinner
    private static final String[] paths = {"item 1", "item 2", "item 3"};

    //@SuppressLint("WrongViewCast")
    //final EditText et = (EditText) findViewById(R.id.retailernum);


    private String folder;
    private String cachedir;

    private String jsonfile_local;
    private String fileName_local = "product_list.json";
    private String filePath_local;

    private JSONObject productName;
    private JSONObject brandNamer;
    private String branditem;
    private String brandNames;

    private String versionControl;

    private JSONObject jsonObjproductList;
    JSONObject brandName;


    private String ProdName = null;
    private String Amount = null;
    private String ProdCode = null;
    private String Description = null;
    private String tViewProdDescStr = null;
    private String customerphoneStr;

    private String strGateway;
    private String loaderType;

    private TextView customerphone;

    private Intent sendLoadIntent;

    private TextView prod_list_label;


    private Spinner brandSpinner;
    private Spinner productSpinner;
    private EditText productEditText;

    private String fromSearchIntent;

    private ArrayAdapter<String> productsadapter;


    private String activityName;



    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transferload);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // toolbar fancy stuff
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
            Objects.requireNonNull(getSupportActionBar()).setTitle("HOME");
        }


        //Toolbar toolbar = findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);
        //Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        //getSupportActionBar().setDisplayShowHomeEnabled(true);



        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);







        sendLoadIntent = new Intent(TransferLoadActivity.this, TransferLoadConfirmActivity.class);

        SharedPreferences prefs = (SharedPreferences) PreferenceManager.getDefaultSharedPreferences(this);
        prefs.getBoolean("checkbox", false);
        String strRingtone = prefs.getString("ringtone", "<unset>");
        String strText = prefs.getString("text", "<unset>");
        String strList = prefs.getString("list", "<unset>");
        strGateway = prefs.getString("gateway", "09989448486");
        loaderType = prefs.getString("loadertype", "retailer");

        Log.d("JPTRpref", strRingtone + " " + strText + " " + strList + " " + loaderType + " Gateway: " + strGateway);


        prod_list_label = (TextView) findViewById(R.id.product_list_label);





        ////GET INTENT INFOS FORM SEARCH FILTER
        activityName = getIntent().getStringExtra("activityName");
        String search_brandspinner = getIntent().getStringExtra("search_brandspinner");
        String search_customernumber = getIntent().getStringExtra("search_customernumber");
        String search_productspinner = getIntent().getStringExtra("search_productname");
        String search_productcode = getIntent().getStringExtra("search_productcode");
        String search_productedittext = getIntent().getStringExtra("search_productedittext");
        String search_proddescription = getIntent().getStringExtra("search_proddescription");
        String search_brand_image = getIntent().getStringExtra("search_brand_image");

        /*
        Toast.makeText(getApplicationContext(),
                "Selected: " + search_brandspinner + " " + search_productcode + " " + search_productedittext + " " + search_proddescription + " " + search_brand_image,
                Toast.LENGTH_LONG).show();
                */

        Log.d("searcher", "brandspinner: " + search_brandspinner);
        Log.d("searcher", "productname: " + search_productspinner);
        Log.d("searcher", "productcode: " + search_productcode);
        Log.d("searcher", "productedittext: " + search_productedittext);
        Log.d("searcher", "productdescription: " + search_proddescription);
        Log.d("searcher", "brand_image: " + search_brand_image);





        fromSearchIntent = search_brandspinner;

        /*
        Button mbackbutton = (Button) findViewById(R.id.backbutton);
        mbackbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(fromSearchIntent != null) {
                    startActivity(new Intent(TransferLoadActivity.this, SearchFilterMainActivity.class));
                }
                else
                {
                    startActivity(new Intent(TransferLoadActivity.this, MainMenuActivity.class));
                }
                overridePendingTransition(R.anim.slideinright, R.anim.slideinleft);
            }
        });
        */


        (findViewById(R.id.contactspickerbutton)).setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
                startActivityForResult(i, PICK_CONTACT);
            }
        });




        brandSpinner = (Spinner) findViewById(R.id.brandspinner);

        productSpinner = (Spinner) findViewById(R.id.productspinner);
        productEditText = (EditText) findViewById(R.id.productedittext);



        customerphone = (TextView) findViewById(R.id.customerphone);

        if(search_customernumber != null)
        {
            customerphone.setText(search_customernumber);
        }

        Button msendLoadbutton = (Button) findViewById(R.id.loadbutton);
        msendLoadbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String customerphoneStr = customerphone.getText().toString();
                boolean validnum = isValidPhone(customerphoneStr);


                if(validnum) {
                    //Toast.makeText(getApplicationContext(), "Phone number is valid", Toast.LENGTH_SHORT).show();
                    sendLoadIntent.putExtra("ProdCode", ProdCode);

                    String phonenum = customerphone.getText().toString();
                    phonenum = phonenum.replaceAll("[^0-9]","");
                    String first2char = phonenum.substring(0,2);
                    if(first2char.equals("63"))
                    {
                        phonenum = "0" + phonenum.substring(2);
                    }

                    sendLoadIntent.putExtra("customerphone", phonenum);
                    sendLoadIntent.putExtra("strGateway", strGateway);

                    String productAmountStr = productEditText.getText().toString();

                    sendLoadIntent.putExtra("ProdAmount", productAmountStr);

                    Log.d("Flexiloader" , branditem);
                    Log.d("Flexiloader" , String.valueOf(isInteger(productAmountStr)));

                    if(branditem.equals("Smart(Flexiload)")) {
                        if(isInteger(productAmountStr)) {
                            Integer productAmountInt = Integer.valueOf(productAmountStr);
                            if (productAmountInt >= 10 && productAmountInt <= 1000) {
                                Log.d("Flexiloader", "productAmountInt >= 10 && productAmountInt <= 1000");
                                startActivity(sendLoadIntent);
                            } else {
                                Toast.makeText(getApplicationContext(), "Flexiload Amount must be between 10 and 1,000", Toast.LENGTH_SHORT).show();
                            }
                        }
                        else
                        {
                            Toast.makeText(getApplicationContext(), "Flexiload Amount is Invalid", Toast.LENGTH_SHORT).show();
                        }
                    }
                    else
                    {
                        Log.d("Flexiloader" ,"not Flexiload");
                        startActivity(sendLoadIntent);
                    }

                } else {
                    //TODO for debugging purposes
                    Toast.makeText(getApplicationContext(),"Phone number is invalid",Toast.LENGTH_SHORT).show();
                }
            }
        });

        /*
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, items);
         */

        final ArrayAdapter<CharSequence> brandadapter = ArrayAdapter.createFromResource(this,
                R.array.brand_array, android.R.layout.simple_spinner_item);


        brandadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);




        /*
        int[] spinnerImages;
        String[] spinnerTitles;
        String[] spinnerPopulation;
        spinnerTitles = new String[]{
                "Cignal",
                "Smart",
                "Smart(Flexiload)",
                "Sun",
                "KuryenteLoad"
        };
        spinnerPopulation = new String[]{
                "1",
                "2",
                "3",
                "4",
                "5"
        };
        spinnerImages = new int[]{
                R.drawable.cignal,
                R.drawable.smart,
                R.drawable.smart,
                R.drawable.sun,
                R.drawable.kuryenteload
        };
        BrandSpinnerCustomAdapter mBrandSpinnerCustomAdapter = new BrandSpinnerCustomAdapter(TransferLoadActivity.this, spinnerTitles, spinnerImages, spinnerPopulation);
        */



        brandSpinner.setAdapter(brandadapter);




        //internal cache dir
        cachedir = getCacheDir().getPath() + File.separator + "brand/";
        Log.d("JPTRX", "cache: " + cachedir);
        folder = cachedir;
        //Create androiddeft folder if it does not exist
        File directory = new File(folder);
        if (!directory.exists()) {
            directory.mkdirs();
        }


        filePath_local = folder + fileName_local;

        /////////////////LOCAL JSON/////////////////

        Log.e("filePath_local", filePath_local);

        try {
            jsonfile_local = getStringFromFile(filePath_local);
        } catch (Exception e) {
            e.printStackTrace();
        }


        // Convert to a JSON object to print data
        //JsonParser jp_local = new JsonParser(); //from gson
        //JsonElement root_local = jp_local.parse(jsonfile_local); //Convert the input stream to a json element
        //JsonObject rootobj_local = root_local.getAsJsonObject(); //May be an array, may be an object.
        //String version_control_local = rootobj_local.get("version_control").getAsString(); //just grab the zipcode
        // input stream to read file - with 8k buffer

        //Log.d("JPTRjson0", "version_control_local: " + version_control_local);

        Log.d("jsonfile_local", jsonfile_local);

        JSONObject forecast = null;
        try {
            forecast = new JSONObject(jsonfile_local);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            assert forecast != null;
            versionControl = forecast.getString("version_control");
        } catch (JSONException e) {
            Log.d("JSONException", jsonfile_local);
            e.printStackTrace();
        }

        jsonObjproductList = null;
        try {
            jsonObjproductList = forecast.getJSONObject("product_list");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //parse string, long and double objects within jsonObjCurrently accordingly



        /*
         JSONObject jsonObjMinutely= forecast.getJSONObject("minutely");
         String summary= jsonObjMinutely.getString("summary");
         String icon= jsonObjMinutely.getString("icon");
         JSONObject jsonArrayMinutelyData = jsonObjMinutely.getJSONObject("data");

         Log.d("JPTRYYY", String.valueOf(jsonArrayMinutelyData.length()));

         for(int i=0; i<jsonArrayMinutelyData.length(); i++){
         String tempData = jsonArrayMinutelyData.names().getString(i);

         //parse the remaining object pairs.

         Log.d("JPTRYYY", String.valueOf(i) + " " + tempData);
         }
         */


        assert jsonObjproductList != null;
        for(int i = 0; i<jsonObjproductList.length(); i++){
            String brandNames = null;
            try {
                brandNames = jsonObjproductList.names().getString(i);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            //parse the remaining object pairs.
            Log.d("JPTRYYY", brandNames);
        }



        /*
        try {
            JSONObject brandName = jsonObjproductList.getJSONObject("Cignal");
            Log.d("JPTRBrandname", String.valueOf(brandName));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        */




        String compareBrandValue = search_brandspinner;
        final String compareProductValue = search_productspinner;

        if (compareBrandValue != null) {
            int spinnerBrandPosition = brandadapter.getPosition(compareBrandValue);
            brandSpinner.setSelection(spinnerBrandPosition);
            Log.d("searcher", "compareBrandValue: " + search_brand_image);
        }
        else
        {
            Log.d("searcher", "compareBrandValue: null " + search_brand_image);
        }


        brandSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                branditem = (String) parent.getItemAtPosition(position);
                Log.d("JPTRbranditem", branditem);

                sendLoadIntent.putExtra("ProdBrand", branditem);

                ImageView mImageBrand = (ImageView) findViewById(R.id.brand_image);

                switch (branditem) {
                    case "Cignal": {
                        List<String> productvalues = new ArrayList<String>();

                        prod_list_label.setText("PRODUCT LIST");
                        productEditText.setText("");
                        productEditText.setVisibility(View.INVISIBLE);
                        productSpinner.setVisibility(View.VISIBLE);

                        try {
                            brandName = jsonObjproductList.getJSONObject(branditem);
                            //LIST ALL PROMOS
                            for(int i=0; i<brandName.length(); i++){
                                brandNames = brandName.names().getString(i);
                                Log.d("JPTRbrandNames", brandNames);
                                brandNamer = brandName.getJSONObject(brandNames);
                                //Log.d("JPTRbrandNamer", String.valueOf(brandNamer));
                                productvalues.add(brandNamer.getString("ProdName"));
                                /*
                                for(int j=0; j<brandNamer.length(); j++){
                                    String ProdName = brandNamer.getString("ProdName");
                                    String Amount = brandNamer.getString("Amount");
                                    String ProdCode = brandNamer.getString("ProdCode");
                                    String Description = brandNamer.getString("Description");
                                    Log.d("JPTRProdName", ProdName + " / " + Amount + " / " + ProdCode + " / " + Description);
                                }
                                 */
                            }
                            //Log.d("JPTR", String.valueOf(brandName));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        //ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(TransferLoadActivity.this, R.array.cignal_array, android.R.layout.simple_spinner_item);
                        productsadapter = new ArrayAdapter<String>(TransferLoadActivity.this, android.R.layout.simple_spinner_item, productvalues);
                        productsadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        productSpinner.setAdapter(productsadapter);
                        mImageBrand.setImageResource(R.drawable.cignal);
                        Log.d("JPTR", branditem + " Selected");

                        if (compareProductValue != null) {
                            int spinnerProductPosition = productsadapter.getPosition(compareProductValue);
                            Log.d("searcher" ,"spinnerProductPosition: " + spinnerProductPosition);
                            productSpinner.setSelection(spinnerProductPosition);
                        }

                        break;
                    }
                    case "Smart": {
                        List<String> productvalues = new ArrayList<String>();

                        prod_list_label.setText("PRODUCT LIST");
                        productEditText.setText("");
                        productEditText.setVisibility(View.INVISIBLE);
                        productSpinner.setVisibility(View.VISIBLE);

                        try {
                            brandName = jsonObjproductList.getJSONObject(branditem);
                            //LIST ALL PROMOS
                            for(int i=0; i<brandName.length(); i++){
                                brandNames = brandName.names().getString(i);
                                Log.d("JPTRbrandNames", brandNames);
                                brandNamer = brandName.getJSONObject(brandNames);
                                //Log.d("JPTRbrandNamer", String.valueOf(brandNamer));
                                productvalues.add(brandNamer.getString("ProdName"));
                                /*
                                for(int j=0; j<brandNamer.length(); j++){
                                    String ProdName = brandNamer.getString("ProdName");
                                    String Amount = brandNamer.getString("Amount");
                                    String ProdCode = brandNamer.getString("ProdCode");
                                    String Description = brandNamer.getString("Description");
                                    Log.d("JPTRProdName", ProdName + " / " + Amount + " / " + ProdCode + " / " + Description);
                                }
                                 */
                            }
                            //Log.d("JPTR", String.valueOf(brandName));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        //ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(TransferLoadActivity.this, R.array.cignal_array, android.R.layout.simple_spinner_item);
                        productsadapter = new ArrayAdapter<String>(TransferLoadActivity.this, android.R.layout.simple_spinner_item, productvalues);
                        productsadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        productSpinner.setAdapter(productsadapter);
                        mImageBrand.setImageResource(R.drawable.smart);
                        Log.d("JPTR", branditem + " Selected");

                        if (compareProductValue != null) {
                            int spinnerProductPosition = productsadapter.getPosition(compareProductValue);
                            Log.d("searcher" ,"spinnerProductPosition: " + spinnerProductPosition);
                            productSpinner.setSelection(spinnerProductPosition);
                        }

                        break;
                    }
                    case "Smart(Flexiload)": {
                        List<String> productvalues = new ArrayList<String>();

                        prod_list_label.setText("ENTER AMOUNT");
                        productEditText.setVisibility(View.VISIBLE);
                        productSpinner.setVisibility(View.INVISIBLE);

                        try {
                            brandName = jsonObjproductList.getJSONObject(branditem);
                            //LIST ALL PROMOS
                            for(int i=0; i<brandName.length(); i++){
                                brandNames = brandName.names().getString(i);
                                Log.d("JPTRbrandNames", brandNames);
                                brandNamer = brandName.getJSONObject(brandNames);
                                //Log.d("JPTRbrandNamer", String.valueOf(brandNamer));
                                productvalues.add(brandNamer.getString("ProdName"));
                                /*
                                for(int j=0; j<brandNamer.length(); j++){
                                    String ProdName = brandNamer.getString("ProdName");
                                    String Amount = brandNamer.getString("Amount");
                                    String ProdCode = brandNamer.getString("ProdCode");
                                    String Description = brandNamer.getString("Description");
                                    Log.d("JPTRProdName", ProdName + " / " + Amount + " / " + ProdCode + " / " + Description);
                                }
                                 */
                            }
                            //Log.d("JPTR", String.valueOf(brandName));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        //ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(TransferLoadActivity.this, R.array.cignal_array, android.R.layout.simple_spinner_item);
                        productsadapter = new ArrayAdapter<String>(TransferLoadActivity.this, android.R.layout.simple_spinner_item, productvalues);
                        productsadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        productSpinner.setAdapter(productsadapter);
                        mImageBrand.setImageResource(R.drawable.smart);
                        Log.d("JPTR", branditem + " Selected");

                        if (compareProductValue != null) {
                            int spinnerProductPosition = productsadapter.getPosition(compareProductValue);
                            Log.d("searcher" ,"spinnerProductPosition: " + spinnerProductPosition);
                            productSpinner.setSelection(spinnerProductPosition);
                        }

                        break;
                    }



                    /*
                    case "TNT": {
                        List<String> values = new ArrayList<String>();

                        prod_list_label.setText("PRODUCT LIST");
                        productEditText.setText("");
                        productEditText.setVisibility(View.INVISIBLE);
                        productSpinner.setVisibility(View.VISIBLE);

                        try {
                            brandName = jsonObjproductList.getJSONObject(branditem);
                            //LIST ALL PROMOS
                            for(int i=0; i<brandName.length(); i++){
                                brandNames = brandName.names().getString(i);
                                Log.d("JPTRbrandNames", brandNames);
                                brandNamer = brandName.getJSONObject(brandNames);
                                values.add(brandNamer.getString("ProdName"));

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        //ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(TransferLoadActivity.this, R.array.cignal_array, android.R.layout.simple_spinner_item);
                        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(TransferLoadActivity.this, android.R.layout.simple_spinner_item, values);
                        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        productSpinner.setAdapter(adapter2);
                        mImageBrand.setImageResource(R.drawable.tnt);
                        Log.d("JPTR", branditem + " Selected");
                        break;
                    }
                    */



                    case "Sun": {
                        List<String> productvalues = new ArrayList<String>();

                        prod_list_label.setText("PRODUCT LIST");
                        productEditText.setText("");
                        productEditText.setVisibility(View.INVISIBLE);
                        productSpinner.setVisibility(View.VISIBLE);

                        try {
                            brandName = jsonObjproductList.getJSONObject(branditem);
                            //LIST ALL PROMOS
                            for(int i=0; i<brandName.length(); i++){
                                brandNames = brandName.names().getString(i);
                                Log.d("JPTRbrandNames", brandNames);
                                brandNamer = brandName.getJSONObject(brandNames);
                                //Log.d("JPTRbrandNamer", String.valueOf(brandNamer));
                                productvalues.add(brandNamer.getString("ProdName"));
                                /*
                                for(int j=0; j<brandNamer.length(); j++){
                                    String ProdName = brandNamer.getString("ProdName");
                                    String Amount = brandNamer.getString("Amount");
                                    String ProdCode = brandNamer.getString("ProdCode");
                                    String Description = brandNamer.getString("Description");
                                    Log.d("JPTRProdName", ProdName + " / " + Amount + " / " + ProdCode + " / " + Description);
                                }
                                 */
                            }
                            //Log.d("JPTR", String.valueOf(brandName));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        //ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(TransferLoadActivity.this, R.array.cignal_array, android.R.layout.simple_spinner_item);
                        productsadapter = new ArrayAdapter<String>(TransferLoadActivity.this, android.R.layout.simple_spinner_item, productvalues);
                        productsadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        productSpinner.setAdapter(productsadapter);
                        mImageBrand.setImageResource(R.drawable.sun);
                        Log.d("JPTR", branditem + " Selected");

                        if (compareProductValue != null) {
                            int spinnerProductPosition = productsadapter.getPosition(compareProductValue);
                            Log.d("searcher" ,"spinnerProductPosition: " + spinnerProductPosition);
                            productSpinner.setSelection(spinnerProductPosition);
                        }

                        break;
                    }
                    case "Globe": {
                        List<String> productvalues = new ArrayList<String>();

                        prod_list_label.setText("PRODUCT LIST");
                        productEditText.setText("");
                        productEditText.setVisibility(View.INVISIBLE);
                        productSpinner.setVisibility(View.VISIBLE);

                        try {
                            brandName = jsonObjproductList.getJSONObject(branditem);
                            //LIST ALL PROMOS
                            for(int i=0; i<brandName.length(); i++){
                                brandNames = brandName.names().getString(i);
                                Log.d("JPTRbrandNames", brandNames);
                                brandNamer = brandName.getJSONObject(brandNames);
                                //Log.d("JPTRbrandNamer", String.valueOf(brandNamer));
                                productvalues.add(brandNamer.getString("ProdName"));
                                /*
                                for(int j=0; j<brandNamer.length(); j++){
                                    String ProdName = brandNamer.getString("ProdName");
                                    String Amount = brandNamer.getString("Amount");
                                    String ProdCode = brandNamer.getString("ProdCode");
                                    String Description = brandNamer.getString("Description");
                                    Log.d("JPTRProdName", ProdName + " / " + Amount + " / " + ProdCode + " / " + Description);
                                }
                                 */
                            }
                            //Log.d("JPTR", String.valueOf(brandName));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        //ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(TransferLoadActivity.this, R.array.cignal_array, android.R.layout.simple_spinner_item);
                        productsadapter = new ArrayAdapter<String>(TransferLoadActivity.this, android.R.layout.simple_spinner_item, productvalues);
                        productsadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        productSpinner.setAdapter(productsadapter);
                        mImageBrand.setImageResource(R.drawable.globe);
                        Log.d("JPTR", branditem + " Selected");

                        if (compareProductValue != null) {
                            int spinnerProductPosition = productsadapter.getPosition(compareProductValue);
                            Log.d("searcher" ,"spinnerProductPosition: " + spinnerProductPosition);
                            productSpinner.setSelection(spinnerProductPosition);
                        }

                        break;
                    }
                    case "TM": {
                        List<String> productvalues = new ArrayList<String>();

                        prod_list_label.setText("PRODUCT LIST");
                        productEditText.setText("");
                        productEditText.setVisibility(View.INVISIBLE);
                        productSpinner.setVisibility(View.VISIBLE);

                        try {
                            brandName = jsonObjproductList.getJSONObject(branditem);
                            //LIST ALL PROMOS
                            for(int i=0; i<brandName.length(); i++){
                                brandNames = brandName.names().getString(i);
                                Log.d("JPTRbrandNames", brandNames);
                                brandNamer = brandName.getJSONObject(brandNames);
                                //Log.d("JPTRbrandNamer", String.valueOf(brandNamer));
                                productvalues.add(brandNamer.getString("ProdName"));
                                /*
                                for(int j=0; j<brandNamer.length(); j++){
                                    String ProdName = brandNamer.getString("ProdName");
                                    String Amount = brandNamer.getString("Amount");
                                    String ProdCode = brandNamer.getString("ProdCode");
                                    String Description = brandNamer.getString("Description");
                                    Log.d("JPTRProdName", ProdName + " / " + Amount + " / " + ProdCode + " / " + Description);
                                }
                                 */
                            }
                            //Log.d("JPTR", String.valueOf(brandName));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        //ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(TransferLoadActivity.this, R.array.cignal_array, android.R.layout.simple_spinner_item);
                        productsadapter = new ArrayAdapter<String>(TransferLoadActivity.this, android.R.layout.simple_spinner_item, productvalues);
                        productsadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        productSpinner.setAdapter(productsadapter);
                        mImageBrand.setImageResource(R.drawable.tm);
                        Log.d("JPTR", branditem + " Selected");

                        if (compareProductValue != null) {
                            int spinnerProductPosition = productsadapter.getPosition(compareProductValue);
                            Log.d("searcher" ,"spinnerProductPosition: " + spinnerProductPosition);
                            productSpinner.setSelection(spinnerProductPosition);
                        }

                        break;
                    }
                    case "KuryenteLoad": {
                        List<String> productvalues = new ArrayList<String>();

                        prod_list_label.setText("PRODUCT LIST");
                        productEditText.setText("");
                        productEditText.setVisibility(View.INVISIBLE);
                        productSpinner.setVisibility(View.VISIBLE);

                        try {
                            brandName = jsonObjproductList.getJSONObject(branditem);
                            //LIST ALL PROMOS
                            for(int i=0; i<brandName.length(); i++){
                                brandNames = brandName.names().getString(i);
                                Log.d("JPTRbrandNames", brandNames);
                                brandNamer = brandName.getJSONObject(brandNames);
                                //Log.d("JPTRbrandNamer", String.valueOf(brandNamer));
                                productvalues.add(brandNamer.getString("ProdName"));
                                /*
                                for(int j=0; j<brandNamer.length(); j++){
                                    String ProdName = brandNamer.getString("ProdName");
                                    String Amount = brandNamer.getString("Amount");
                                    String ProdCode = brandNamer.getString("ProdCode");
                                    String Description = brandNamer.getString("Description");
                                    Log.d("JPTRProdName", ProdName + " / " + Amount + " / " + ProdCode + " / " + Description);
                                }
                                 */
                            }
                            //Log.d("JPTR", String.valueOf(brandName));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        //ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(TransferLoadActivity.this, R.array.cignal_array, android.R.layout.simple_spinner_item);
                        productsadapter = new ArrayAdapter<String>(TransferLoadActivity.this, android.R.layout.simple_spinner_item, productvalues);
                        productsadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        productSpinner.setAdapter(productsadapter);
                        mImageBrand.setImageResource(R.drawable.kuryenteload);
                        Log.d("JPTR", branditem + " Selected");

                        if (compareProductValue != null) {
                            int spinnerProductPosition = productsadapter.getPosition(compareProductValue);
                            Log.d("searcher" ,"spinnerProductPosition: " + spinnerProductPosition);
                            productSpinner.setSelection(spinnerProductPosition);
                        }

                        break;
                    }
                }



            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub
            }
        });




        //Log.d("searcher" ,"compareProductValue: " + compareProductValue);

        //////PRODUCT SPINNER ON SELECT
        productSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                String productitem = (String) parent.getItemAtPosition(position);
                Log.v("JPTR", productitem + " " + branditem);

                TextView tViewProdDesc = (TextView) findViewById(R.id.proddescription);

                try {

                    brandNamer = brandName.getJSONObject(brandNames);
                    JSONObject brandNamerx = brandName.getJSONObject(productitem);
                    Log.v("JPTRbrandNamerx", String.valueOf(brandNamerx));
                    ProdName = brandNamerx.getString("ProdName");
                    Amount = brandNamerx.getString("Amount");
                    ProdCode = brandNamerx.getString("ProdCode");
                    Description = brandNamerx.getString("Description");
                    Log.d("JPTRProdNamex", ProdName + " / " + Amount + " / " + ProdCode + " / " + Description);
                    tViewProdDescStr = "";
                    //tViewProdDescStr = "Product Name: " + ProdName + System.getProperty("line.separator");
                    tViewProdDescStr = tViewProdDescStr + "Amount: ₱"  + Amount + System.getProperty("line.separator");
                    //tViewProdDescStr = tViewProdDescStr + "Product Code: "  + ProdCode + System.getProperty("line.separator");
                    if(Description == null || Description.equals("null") || Description.equals("")) {
                        Description = "-";
                    }
                    tViewProdDescStr = tViewProdDescStr + ""  + Description;

                    tViewProdDesc.setText(tViewProdDescStr);
                    tViewProdDesc.setSingleLine(false);
                    sendLoadIntent.putExtra("tViewProdDescStr", tViewProdDescStr);
                    sendLoadIntent.putExtra("ProdName", ProdName);
                    sendLoadIntent.putExtra("Amount", Amount);
                    sendLoadIntent.putExtra("Description", Description);
                } catch (JSONException e) {
                    e.printStackTrace();
                }




            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub
            }

        });







    }




    public boolean isInteger( String input )
    {
        try
        {
            Integer.parseInt( input );
            return true;
        }
        catch( Exception e)
        {
            return false;
        }
    }


    //contacts picker
    @SuppressLint("SetTextI18n")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_CONTACT && resultCode == RESULT_OK) {
            Uri contactUri = data.getData();
            assert contactUri != null;
            @SuppressLint("Recycle") Cursor cursor = getContentResolver().query(contactUri, null, null, null, null);
            assert cursor != null;
            cursor.moveToFirst();
            int column = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);

            String phonenum = cursor.getString(column);

            phonenum = phonenum.replaceAll("[^0-9]","");
            String first2char = phonenum.substring(0,2);
            if(first2char.equals("63"))
            {
                phonenum = "0" + phonenum.substring(2);
            }

            Log.d("JPTRphonenum", phonenum);

            EditText editText = (EditText) findViewById(R.id.customerphone);
            editText.setText(phonenum);
            //et.setText("DefaultValue");


        }
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




    public void sendLoad(View view) {
        /*
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        String ussdCode = "*" + 121 + "*" + 6 + "*" + 1 + Uri.encode("#");
        callIntent.setData(Uri.parse("tel:" + ussdCode));

        if (ActivityCompat.checkSelfPermission(MainMenuActivity.this,
                Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        startActivity(callIntent);
        */
        if (ActivityCompat.checkSelfPermission(TransferLoadActivity.this,
                Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            return;
        }


        customerphoneStr = customerphone.getText().toString();
        customerphoneStr = customerphoneStr.replaceAll("[^0-9]","");


        boolean valid = isValidPhone(customerphoneStr);

        if(valid) {
            //Toast.makeText(getApplicationContext(), "Phone number is valid", Toast.LENGTH_SHORT).show();

            String message = ProdCode + " " + customerphoneStr;
            String sendTo = strGateway;
            Log.d("JPTR", "sendSmsByManager()");
            Log.d("JPTRSMS", sendTo + " - " + message);
            try {
                // Get the default instance of the SmsManager
                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage(sendTo.toString(),
                        null,
                        message,
                        null,
                        null);

                //Toast.makeText(getApplicationContext(), "Load Transfer Successful! Please wait for the SMS details!", Toast.LENGTH_LONG).show();


                String title = "Load Transfer Successful!";
                String messager = "Please wait for the confirmation SMS of Load Transfer";

                new AlertDialog.Builder(TransferLoadActivity.this)
                        .setTitle(title)
                        .setMessage(messager)
                        .setPositiveButton("OK", null)
                        .show();


            } catch (Exception ex) {
                Toast.makeText(getApplicationContext(), "Your sms has failed...",
                        Toast.LENGTH_LONG).show();
                ex.printStackTrace();
            }

        }
        else {
            //TODO for debugging purposes
            //Toast.makeText(getApplicationContext(),"Phone number is not valid",Toast.LENGTH_SHORT).show();
        }

    }




    private boolean isValidPhone(String phone)
    {
        boolean check = false;
        if(!Pattern.matches("[a-zA-Z]+", phone))
        {
            if(phone.length() < 6 || phone.length() > 13)
            {
                check = false;
            }
            else
            {
                check = true;
            }
        }
        else
        {
            check=false;
        }
        return check;
    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        Log.d("JPTRback", "back pressed");

        if(fromSearchIntent != null && activityName.equals("SearchFilterMain")) {
            startActivity(new Intent(TransferLoadActivity.this, SearchFilterMainActivity.class));
        }
        else
        {
            startActivity(new Intent(TransferLoadActivity.this, MainMenuActivity.class));
        }
        overridePendingTransition(R.anim.slideinright, R.anim.slideinleft);

        return true;
    }


}
