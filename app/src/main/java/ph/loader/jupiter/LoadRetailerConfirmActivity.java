package com.inducesmile.retailer;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
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
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.text.TextUtils;
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

import com.inducesmile.retailer.adapters.SimUtil;
import com.inducesmile.retailer.adapters.TelephonyInfo;

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

@SuppressLint("Registered")
public class LoadRetailerConfirmActivity extends AppCompatActivity {

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

    private String tViewProdDescStrfinal;


    private String ProdNameIntent;
    private String ProdCodeIntent;
    private String  ProdAmountIntent;
    //private String customerphoneIntent;
    private String dealerphoneIntent;
    private String strGatewayIntent;
    //private String tViewProdDescStrIntent;

    private String strGateway;

    private TextView customerphone;

    private boolean isDualSIM;
    private String dualsimID;

    private Context context = this;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dealer_loadretailer_confirm);


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // toolbar fancy stuff
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
            Objects.requireNonNull(getSupportActionBar()).setTitle(R.string.home_label);
        }

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);





        SharedPreferences prefs = (SharedPreferences) PreferenceManager.getDefaultSharedPreferences(this);
        prefs.getBoolean("checkbox", false);
        //String strRingtone = prefs.getString("ringtone", "<unset>");
        String strText = prefs.getString("text", "<unset>");
        String strList = prefs.getString("list", "<unset>");
        strGateway = prefs.getString("gateway", "<unset>");
        String strSIMID = prefs.getString("dualsim", "Select you SIM");

        Log.d("JPTRpref", strText + " " + strList + " " + strGateway + " " + strSIMID);

        ProdNameIntent = getIntent().getStringExtra("ProdName");
        ProdCodeIntent = getIntent().getStringExtra("ProdCode");
        ProdAmountIntent = getIntent().getStringExtra("ProdAmount");
        dealerphoneIntent = getIntent().getStringExtra("dealerphone");
        strGatewayIntent = getIntent().getStringExtra("strGateway");
        //tViewProdDescStrIntent = getIntent().getStringExtra("tViewProdDescStr");

        TextView tViewProdDesc = (TextView) findViewById(R.id.proddescription);
        tViewProdDescStrfinal = "";
        tViewProdDescStrfinal = tViewProdDescStrfinal + System.getProperty("line.separator") + "Load Retailer: " + dealerphoneIntent + System.getProperty("line.separator");
        //if(ProdNameIntent.equals("Flexiload") &&  Integer.parseInt(ProdAmountIntent) >=10  &&  Integer.parseInt(ProdAmountIntent) <=1000 )
        if(ProdNameIntent.equals("Flexiload"))
        {
            Log.d("Flexiload", "Flexi" + ProdAmountIntent );
            ProdCodeIntent = "MYLOAD" + ProdAmountIntent;
            ProdNameIntent = ProdNameIntent + ProdAmountIntent;
        }

        //tViewProdDescStrfinal = tViewProdDescStrfinal + System.getProperty("line.separator") + "Product Name: " + ProdNameIntent + System.getProperty("line.separator");
        tViewProdDescStrfinal = tViewProdDescStrfinal + System.getProperty("line.separator") + "Amount: ₱"  + ProdAmountIntent + ".00" + System.getProperty("line.separator");
        tViewProdDescStrfinal = tViewProdDescStrfinal + System.getProperty("line.separator") + "Product Code: " + ProdCodeIntent + System.getProperty("line.separator");
        //tViewProdDescStrfinal = tViewProdDescStrfinal + System.getProperty("line.separator") + tViewProdDescStrIntent;

        tViewProdDesc.setText(tViewProdDescStrfinal);
        tViewProdDesc.setSingleLine(false);

        /*
        Button mbackbutton = (Button) findViewById(R.id.backbutton);
        mbackbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent backIntent = new Intent(LoadRetailerConfirmActivity.this, LoadRetailerActivity.class);

                backIntent.putExtra("ConfirmdealerphoneIntent", dealerphoneIntent);
                backIntent.putExtra("ConfirmProdAmountIntent", ProdAmountIntent);

                startActivity(backIntent);
                overridePendingTransition(R.anim.slideinright, R.anim.slideinleft);

            }
        });
        */

        Button msendLoadbutton = (Button) findViewById(R.id.loadbutton);
        msendLoadbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("JPTRbutton", "msendloadButton has been clicked");
                transferLoad(v);
            }
        });




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
        //String version_control_local = rootobj_local.get("version_control").getAsString(); //just grab the zipcode                // input stream to read file - with 8k buffer

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
            Log.d("JSONException", jsonfile_local);e.printStackTrace();
        }

        jsonObjproductList = null;
        try {
            jsonObjproductList = forecast.getJSONObject("product_list");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //parse string, long and double objects within jsonObjCurrently accordingly



        /**
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
         **/
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



        /**
         try {
         JSONObject brandName = jsonObjproductList.getJSONObject("Cignal");
         Log.d("JPTRBrandname", String.valueOf(brandName));
         } catch (JSONException e) {
         e.printStackTrace();
         }
         **/



        //TODO SMS ONCREATE
        TelephonyInfo telephonyInfo = TelephonyInfo.getInstance(this);
        isDualSIM = telephonyInfo.isDualSIM();
        if (isDualSIM) {
            ////SET SMS FOR DEFAULT SIM
            // then you use
            dualsimID = prefs.getString("dualsim", "Select you SIM");

            Intent simIntent = new Intent("android.intent.action.SMS_DEFAULT_SIM");
            simIntent.putExtra("simid", dualsimID);

            ContentValues val = new ContentValues();
            val.put("value", dualsimID);
            getContentResolver().update(Uri.parse("content://settings/system"), val, "name='sms_sim_setting'", null);
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

            Log.v("JPTR", phonenum);

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




    public void transferLoad(View view) {
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
        if (ActivityCompat.checkSelfPermission(LoadRetailerConfirmActivity.this,
                Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            return;
        }


        String customerphoneStr = dealerphoneIntent;
        customerphoneStr = customerphoneStr.replaceAll("[^0-9]","");

        boolean valid = isValidPhone(customerphoneStr);

        if(valid) {
            //Toast.makeText(getApplicationContext(), "Phone number is valid " + customerphoneIntent, Toast.LENGTH_SHORT).show();


            String message = "RETLOAD " + ProdCodeIntent;
            String sendTo = strGateway;
            Log.d("JPTR", "sendSmsByManager()");
            Log.d("JPTRSMS", sendTo + " - " + message);
            try {


                /*
                // Get the default instance of the SmsManager
                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage(
                        sendTo.toString(),
                        null,
                        message,
                        null,
                        null
                );
                */

                sendSMS(sendTo, message);






                //Toast.makeText(getApplicationContext(), "Load Transfer Successful! Please wait for the SMS details!", Toast.LENGTH_LONG).show();

            } catch (Exception ex) {
                Toast.makeText(getApplicationContext(), "Your sms has failed...",
                        Toast.LENGTH_LONG).show();
                ex.printStackTrace();
            }
        }
        else {
            Log.d("JPTR", "Phone not valid");
            //Toast.makeText(getApplicationContext(),"Phone number is not valid "  + customerphoneIntent ,Toast.LENGTH_SHORT).show();
            //TODO nothing
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
            check = false;
        }
        return check;
    }




    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        Log.d("JPTRback", "back pressed");

        Intent confirmToLoad = new Intent(LoadRetailerConfirmActivity.this, LoadRetailerActivity.class);

        confirmToLoad.putExtra("activityName", "LoadRetailer");
        confirmToLoad.putExtra("search_retailernumber", dealerphoneIntent);
        confirmToLoad.putExtra("search_productamount", ProdAmountIntent);

        startActivity(confirmToLoad);

        overridePendingTransition(R.anim.slideinright, R.anim.slideinleft);
        return true;
    }

















    //SMS
    BroadcastReceiver sendBroadcastReceiver = new SentReceiver();
    BroadcastReceiver deliveryBroadcastReciever = new DeliverReceiver();
    //SMS
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();

        try {
            unregisterReceiver(sendBroadcastReceiver);
            unregisterReceiver(deliveryBroadcastReciever);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    //SMS
    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        try {
            unregisterReceiver(sendBroadcastReceiver);
            unregisterReceiver(deliveryBroadcastReciever);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }





    private void sendSMS(String phoneNumber, String message) {

        Log.d("SMSMANAGER", "sendSMS used");
        String SENT = "SMS_SENT";
        String DELIVERED = "SMS_DELIVERED";

        PendingIntent sentPI = PendingIntent.getBroadcast(this, 0, new Intent(
                SENT), 0);
        PendingIntent deliveredPI = PendingIntent.getBroadcast(this, 0,
                new Intent(DELIVERED), 0);
        registerReceiver(sendBroadcastReceiver, new IntentFilter(SENT));
        registerReceiver(deliveryBroadcastReciever, new IntentFilter(DELIVERED));

        if (isDualSIM) {
            Log.d("SMSMANAGER", "DUAL SIM USED");
            Log.d("SMSMANAGER", "DUAL SIM ID " + dualsimID);
            //SimUtilsendSMS(this, dualsimID, phoneNumber, null, message, sentPI, deliveredPI);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
                SubscriptionManager localSubscriptionManager = SubscriptionManager.from(context);
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                if (localSubscriptionManager.getActiveSubscriptionInfoCount() > 1) {
                    List localList = localSubscriptionManager.getActiveSubscriptionInfoList();

                    SubscriptionInfo simInfo = (SubscriptionInfo) localList.get(Integer.parseInt(dualsimID));
                    /*
                    SubscriptionInfo simInfo1 = (SubscriptionInfo) localList.get(0);
                    SubscriptionInfo simInfo2 = (SubscriptionInfo) localList.get(1);
                    */
                    //SendSMS From SELECTED SIM
                    SmsManager.getSmsManagerForSubscriptionId(simInfo.getSubscriptionId()).sendTextMessage(phoneNumber, null, message, sentPI, deliveredPI);

                    /*
                    //SendSMS From SIM One
                    SmsManager.getSmsManagerForSubscriptionId(simInfo1.getSubscriptionId()).sendTextMessage(phoneNumber, null, message, sentPI, deliveredPI);
                    //SendSMS From SIM Two
                    SmsManager.getSmsManagerForSubscriptionId(simInfo2.getSubscriptionId()).sendTextMessage(phoneNumber, null, message, sentPI, deliveredPI);
                    */
                }
            }
        }
        else {
            Log.d("SMSMANAGER", "SINGLE SIM USED");
            Log.d("SMSMANAGER", "DUAL SIM ID " + dualsimID);
            SmsManager sms = SmsManager.getDefault();
            sms.sendTextMessage(phoneNumber, null, message, sentPI, deliveredPI);
        }
    }


    class DeliverReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent arg1) {
            Log.d("SMSMANAGER", "getResultCode:" + String.valueOf(getResultCode()));
            switch (getResultCode()) {
                case Activity.RESULT_OK:
                    Log.d("SMSMANAGER", "SMS DELIVERED DeliverReceiver");
                    Toast.makeText(getBaseContext(), "SMS DELIVERED DeliverReceiver",
                            Toast.LENGTH_SHORT).show();
                    break;
                case Activity.RESULT_CANCELED:
                    Log.d("SMSMANAGER", "SMS NOT DELIVERED DeliverReceiver");
                    Toast.makeText(getBaseContext(), "SMS NOT DELIVERED DeliverReceiver",
                            Toast.LENGTH_SHORT).show();
                    break;
            }

        }
    }

    //TODO CHANGE THIS ON EVERY SMS ACTIVITY
    class SentReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent arg1) {
            Log.d("SMSMANAGER", "getResultCode:" + String.valueOf(getResultCode()));
            String title;
            String messager;
            switch (getResultCode()) {
                case Activity.RESULT_OK:
                    Log.d("SMSMANAGER", "SMS SENT SentReceiver");

                    //Toast.makeText(getBaseContext(), "SMS SENT SentReceiver", Toast.LENGTH_SHORT).show();
                    title = "Load Transfer Successful!";
                    messager = "Please wait for the confirmation SMS of Load Transfer";
                    new AlertDialog.Builder(LoadRetailerConfirmActivity.this)
                            .setTitle(title)
                            .setMessage(messager)
                            .setPositiveButton("OK",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            // TODO Auto-generated method stub
                                            startActivity(new Intent(LoadRetailerConfirmActivity.this, LoadRetailerActivity.class));
                                        }
                                    }
                            )
                            .show();
                    //startActivity(new Intent(SendSMS.this, ChooseOption.class));
                    //overridePendingTransition(R.anim.animation, R.anim.animation2);
                    break;
                case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                    Log.d("SMSMANAGER", "Generic failure");
                    //Toast.makeText(getBaseContext(), "Generic failure",Toast.LENGTH_SHORT).show();
                    title = "Sending Failed!";
                    messager = getString(R.string.RESULT_ERROR_GENERIC_FAILURE);
                    new AlertDialog.Builder(LoadRetailerConfirmActivity.this)
                            .setTitle(title)
                            .setMessage(messager)
                            .setPositiveButton("OK", null)
                            .show();
                    break;
                case SmsManager.RESULT_ERROR_NO_SERVICE:
                    Log.d("SMSMANAGER", "No service");
                    //Toast.makeText(getBaseContext(), "No service",Toast.LENGTH_SHORT).show();
                    title = "Sending Failed!";
                    messager = getString(R.string.RESULT_ERROR_NO_SERVICE);
                    new AlertDialog.Builder(LoadRetailerConfirmActivity.this)
                            .setTitle(title)
                            .setMessage(messager)
                            .setPositiveButton("OK", null)
                            .show();
                    break;
                case SmsManager.RESULT_ERROR_NULL_PDU:
                    Log.d("SMSMANAGER", "Null PDU");
                    //Toast.makeText(getBaseContext(), "Null PDU", Toast.LENGTH_SHORT).show();
                    title = "Sending Failed!";
                    messager = getString(R.string.RESULT_ERROR_NULL_PDU);
                    new AlertDialog.Builder(LoadRetailerConfirmActivity.this)
                            .setTitle(title)
                            .setMessage(messager)
                            .setPositiveButton("OK", null)
                            .show();
                    break;
                case SmsManager.RESULT_ERROR_RADIO_OFF:
                    Log.d("SMSMANAGER", "Radio off");
                    //Toast.makeText(getBaseContext(), "Radio off", Toast.LENGTH_SHORT).show();
                    title = "Sending Failed!";
                    messager = getString(R.string.RESULT_ERROR_RADIO_OFF);
                    new AlertDialog.Builder(LoadRetailerConfirmActivity.this)
                            .setTitle(title)
                            .setMessage(messager)
                            .setPositiveButton("OK", null)
                            .show();
                    break;
            }

        }
    }




}
