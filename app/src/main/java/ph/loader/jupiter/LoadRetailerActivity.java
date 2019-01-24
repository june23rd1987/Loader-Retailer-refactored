package com.inducesmile.retailer;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Objects;
import java.util.regex.Pattern;

@SuppressLint("Registered")
public class LoadRetailerActivity extends AppCompatActivity {

    //declare globally, this can be any int
    public final int PICK_CONTACT = 2015;


    //spinner
    private static final String[] paths = {"item 1", "item 2", "item 3"};

    //@SuppressLint("WrongViewCast")
    //final EditText et = (EditText) findViewById(R.id.retailernum);


    //private TextView customerphone;
    private TextView dealerphone;
    private Intent sendLoadIntent;

    private String strGateway;
    private String loaderType;

    private EditText productEditText;

    private String activityName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dealer_loadretailer);


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // toolbar fancy stuff
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
            Objects.requireNonNull(getSupportActionBar()).setTitle(R.string.home_label);
        }

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        sendLoadIntent = new Intent(LoadRetailerActivity.this, LoadRetailerConfirmActivity.class);


        SharedPreferences prefs = (SharedPreferences) PreferenceManager.getDefaultSharedPreferences(this);
        prefs.getBoolean("checkbox", false);
        String strRingtone = prefs.getString("ringtone", "<unset>");
        String strText = prefs.getString("text", "<unset>");
        String strList = prefs.getString("list", "<unset>");
        strGateway = prefs.getString("gateway", "09989448486");
        loaderType = prefs.getString("loadertype", "retailer");


        ////GET INTENT INFOS FORM SEARCH FILTER
        activityName = getIntent().getStringExtra("activityName");





        /*
        Button mbackbutton = (Button) findViewById(R.id.backbutton);
        mbackbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent backIntent = new Intent(LoadRetailerActivity.this, MainMenuActivity.class);
                startActivity(backIntent);
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


        productEditText = (EditText) findViewById(R.id.amountnum);
        dealerphone = (TextView) findViewById(R.id.dealerphone);

        String ConfirmdealerphoneIntent = getIntent().getStringExtra("search_retailernumber");
        String ConfirmProdAmountIntent = getIntent().getStringExtra("search_productamount");

        if(ConfirmdealerphoneIntent != null && ConfirmProdAmountIntent != null)
        {
            dealerphone.setText(ConfirmdealerphoneIntent);
            productEditText.setText(ConfirmProdAmountIntent);
        }

        Button msendLoadbutton = (Button) findViewById(R.id.loadbutton);
        msendLoadbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String customerphoneStr = dealerphone.getText().toString();
                boolean validnum = isValidPhone(customerphoneStr);

                if(validnum) {
                    //Toast.makeText(getApplicationContext(), "Phone number is valid", Toast.LENGTH_SHORT).show();

                    String phonenum = dealerphone.getText().toString();
                    phonenum = phonenum.replaceAll("[^0-9]","");
                    String first2char = phonenum.substring(0,2);
                    if(first2char.equals("63"))
                    {
                        phonenum = "0" + phonenum.substring(2);
                    }

                    sendLoadIntent.putExtra("ProdName", "Load Retailer");
                    sendLoadIntent.putExtra("dealerphone", phonenum);
                    sendLoadIntent.putExtra("strGateway", strGateway);

                    String productAmount = productEditText.getText().toString();
                    productAmount = productAmount.replaceAll("[^0-9]","");
                    sendLoadIntent.putExtra("ProdAmount", productAmount);
                    sendLoadIntent.putExtra("ProdCode", phonenum + " " + productAmount);
                    if (!productAmount.equals("") && productAmount.matches("\\d*\\.?\\d+")) {
                            startActivity(sendLoadIntent);
                    } else {
                            Toast.makeText(getApplicationContext(), "Invalid amount", Toast.LENGTH_SHORT).show();
                    }
                } else {
                            Toast.makeText(getApplicationContext(), "Invalid Retailer Number", Toast.LENGTH_SHORT).show();
                }
            }
        });


        //Spinner brandSpinner = (Spinner) findViewById(R.id.brandspinner);

        String[] items = new String[] { "Chai Latte", "Green Tea", "Black Tea" };

        /**
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, items);
         **/


        /**
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.brand_array, android.R.layout.simple_spinner_item);


        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);


        brandSpinner.setAdapter(adapter);

        brandSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                String branditem = (String) parent.getItemAtPosition(position);
                Log.v("JPTR", branditem);

                Spinner productSpinner = (Spinner) findViewById(R.id.prductspinner);

                switch (branditem) {
                    case "Smart": {
                        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(LoadRetailerActivity.this,
                                R.array.smart_array, android.R.layout.simple_spinner_item);
                        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        productSpinner.setAdapter(adapter2);
                        Log.v("JPTR", branditem + " Selected");
                        break;
                    }
                    case "TNT": {
                        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(LoadRetailerActivity.this,
                                R.array.tnt_array, android.R.layout.simple_spinner_item);
                        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        productSpinner.setAdapter(adapter2);
                        Log.v("JPTR", branditem + " Selected");
                        break;
                    }
                    case "Sun": {
                        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(LoadRetailerActivity.this,
                                R.array.sun_array, android.R.layout.simple_spinner_item);
                        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        productSpinner.setAdapter(adapter2);
                        Log.v("JPTR", branditem + " Selected");
                        break;
                    }
                    case "Globe": {
                        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(LoadRetailerActivity.this,
                                R.array.globe_array, android.R.layout.simple_spinner_item);
                        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        productSpinner.setAdapter(adapter2);
                        Log.v("JPTR", branditem + " Selected");
                        break;
                    }
                    case "TM": {
                        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(LoadRetailerActivity.this,
                                R.array.tm_array, android.R.layout.simple_spinner_item);
                        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        productSpinner.setAdapter(adapter2);
                        Log.v("JPTR", branditem + " Selected");
                        break;
                    }
                }


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub
            }
        });
**/

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

            EditText editText = (EditText) findViewById(R.id.dealerphone);
            editText.setText(phonenum);
            //et.setText("DefaultValue");

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

        startActivity(new Intent(LoadRetailerActivity.this, MainMenuActivity.class));
        overridePendingTransition(R.anim.slideinright, R.anim.slideinleft);
        return true;
    }


}
