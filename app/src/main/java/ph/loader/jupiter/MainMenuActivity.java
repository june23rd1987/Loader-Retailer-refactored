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
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.RequiresApi;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.telephony.SmsManager;
import android.telephony.SubscriptionManager;
import android.telephony.SubscriptionInfo;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.inducesmile.retailer.adapters.SimUtil;
import com.inducesmile.retailer.adapters.TelephonyInfo;

import java.util.ArrayList;
import java.util.List;

import static android.content.Intent.ACTION_CALL;
import static com.inducesmile.retailer.adapters.SimUtil.SimUtilsendSMS;


public class MainMenuActivity extends AppCompatActivity {

    private static final String TAG = MainMenuActivity.class.getSimpleName();
    private String loadertype;

    private Context context = this;

    private boolean isDualSIM;
    private String dualsimID;

    private SharedPreferences prefs;
    @SuppressLint("RestrictedApi")
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //INITIALIZE SHARED PREFERENCE
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        // then you use
        loadertype = prefs.getString("loadertype", "retailer");

        assert loadertype != null;
        if (loadertype.equals("dealer")) {
            setContentView(R.layout.activity_dealer_main_menu_gridview);
        } else {
            setContentView(R.layout.activity_retailer_main_menu_gridview);
        }

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);


        //dialNumber("*121#");


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


        /*
        ImageView mImage = (ImageView) findViewById(R.id.transfer_load_icon);
        mImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainMenuActivity.this, TransferLoadActivity.class));
            }
        });



        ImageView mImageList = (ImageView) findViewById(R.id.product_list_icon);
        mImageList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainMenuActivity.this, SearchFilterMainActivity.class));
            }
        });
         */



        /*
        Button mupdatebutton = (Button) findViewById(R.id.settings_button);
        mupdatebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainMenuActivity.this, UpdateProductListActivity.class));
            }
        });
         */


        final GridView gridview = (GridView) findViewById(R.id.gridview);
        List<ItemObject> allItems = getAllItemObject();
        final CustomAdapter customAdapter = new CustomAdapter(MainMenuActivity.this, allItems);
        gridview.setAdapter(customAdapter);


        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


                if (loadertype.equals("dealer")) {
                    //LOAD RETAILER
                    if (position == 0) {
                        startActivity(new Intent(MainMenuActivity.this, LoadRetailerActivity.class));
                    }
                    //PASS LOAD
                    else if (position == 1) {
                        startActivity(new Intent(MainMenuActivity.this, PassLoadActivity.class));
                    }
                    //BALANCE INQUIRY
                    else if (position == 2) {
                        balanceInquiry();
                    }
                    //PRODUCT LIST
                    else if (position == 3) {
                        startActivity(new Intent(MainMenuActivity.this, SearchFilterMainActivity.class));
                    }
                    //SETTINGS
                    else if (position == 4) {
                        startActivity(new Intent(MainMenuActivity.this, PreferenceActivity.class));
                    }
                } else {

                    //LOADERTYPE
                    //TRANSFERLOAD
                    if (position == 0) {
                        startActivity(new Intent(MainMenuActivity.this, TransferLoadActivity.class));
                    }
                    //PRODUCT LIST
                    else if (position == 1) {
                        startActivity(new Intent(MainMenuActivity.this, SearchFilterMainActivity.class));
                    }
                    //BALANCE INQUIRY
                    else if (position == 2) {
                        balanceInquiry();
                    }
                    //SETTINGS
                    else if (position == 3) {
                        startActivity(new Intent(MainMenuActivity.this, PreferenceActivity.class));
                    }

                }

                //ICON POSITION DEBUGGER - UNCOMMENT TO DEBUG
                //Toast.makeText(MainMenuActivity.this, "Position: " + position, Toast.LENGTH_SHORT).show();
            }
        });


        /////MAIN MENU GRIDVIEW ADAPTER
        Integer gridviewCount = gridview.getAdapter().getCount();
        Log.d("GridViewer", String.valueOf(gridview.getAdapter().getCount()));

        //IF GRIDVIEW IS MORE THAN FOUR ITEMS DISPLAY ARROW SCROLLER
        if (gridviewCount > 4) {
            final FloatingActionButton fabup = (FloatingActionButton) findViewById(R.id.fabup);
            final FloatingActionButton fabdown = (FloatingActionButton) findViewById(R.id.fabdown);


            fabup.setVisibility(View.INVISIBLE);


            gridview.setOnScrollListener(new AbsListView.OnScrollListener() {

                public void onScrollStateChanged(AbsListView view, int scrollState) {
                    // TODO Auto-generated method stub

                }

                public void onScroll(AbsListView view, int firstVisibleItem,
                                     int visibleItemCount, int totalItemCount) {
                    // TODO Auto-generated method stub

                    Log.d("GridViewer", totalItemCount + " - " + visibleItemCount + " == " + firstVisibleItem);

                    int totminusvisible = totalItemCount - visibleItemCount;


                    if (firstVisibleItem == 0) {
                        // check if we reached the top or bottom of the list
                        View v = gridview.getChildAt(0);
                        int offset = (v == null) ? 0 : v.getTop();
                        if (offset == 0) {
                            // reached the top:
                            Log.d("GridViewer", "reached the top");

                            fabup.setVisibility(View.INVISIBLE);
                            fabdown.setVisibility(View.VISIBLE);

                            return;
                        } else {
                            // reached the bottom:
                            Log.d("GridViewer", "reached the bottom");

                            fabdown.setVisibility(View.INVISIBLE);
                            fabup.setVisibility(View.VISIBLE);

                            return;
                        }
                    } else {
                        View v = gridview.getChildAt(totalItemCount - 1);
                        int offset = (v == null) ? 0 : v.getTop();
                        Log.d("GridViewer", "offset: " + String.valueOf(offset));
                        if (offset < 500) {
                            // reached the bottom:
                            Log.d("GridViewer", "reached the bottom");
                        }
                    }

                    //Log.d("GridViewer","firstVisibleItem"+firstVisibleItem+"\nLastVisibleItem"+totalItemCount);
                }

            });


            fabup.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    ////SCROLL TO TOP
                    gridview.smoothScrollToPosition(0);
                    fabdown.setVisibility(View.VISIBLE);
                    fabup.setVisibility(View.INVISIBLE);
                    Log.d("GridViewer", "Fab Up Clicked");
                }
            });


            fabdown.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    ////SCROLL TO BOTTOM
                    gridview.smoothScrollToPosition(customAdapter.getCount());
                    fabup.setVisibility(View.VISIBLE);
                    fabdown.setVisibility(View.INVISIBLE);
                    Log.d("GridViewer", "Fab Down Clicked");
                }
            });


        }




        /*
        Button msettingsbutton = (Button) findViewById(R.id.settings2_button);
        msettingsbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainMenuActivity.this, SettingsActivity2.class));
            }
        });
         */


    }


    @Override
    public void onBackPressed() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Do you want to exit?");
        // alert.setMessage("Message");

        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //Your action here
                appExit();
                /*
                finish();
                moveTaskToBack(true);
                android.os.Process.killProcess(android.os.Process.myPid());
                System.exit(1);
                 */
            }
        });

        alert.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    }
                });

        alert.show();
    }


    public void appExit() {
        this.finish();
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        System.exit(1);
    }

    public void balanceInquiry() {
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
        if (ActivityCompat.checkSelfPermission(MainMenuActivity.this,
                Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        String message = "BALANCE";
        String sendTo = prefs.getString("gateway", "<unset>");//09989448486 or 09957001152
        Log.d("SMSMANAGER", "sendSmsByManager()");
        Log.d("SMSMANAGER", sendTo + " - " + message);
        try {

            sendSMSbalance(sendTo, message);

            /*
            if (isDualSIM) {
                Log.d("SMSMANAGER", "Sending SMS Dual SIM method");
                sendSMSbalance(sendTo, message);
            } else {
                Log.d("SMSMANAGER", "Sending SMS Normal Method");
                // Get the default instance of the SmsManager
                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage(
                        sendTo,
                        null,
                        message,
                        null,
                        null
                );
            }
            */


        } catch (Exception ex) {
            Toast.makeText(getApplicationContext(), "Your sms has failed...",
                    Toast.LENGTH_LONG).show();
            ex.printStackTrace();
        }
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



    private void sendSMSbalance(String phoneNumber, String message) {



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

                    title = "Sending Success!";
                    messager = "Please wait for the confirmation SMS of your balance inquiry";
                    new AlertDialog.Builder(MainMenuActivity.this)
                            .setTitle(title)
                            .setMessage(messager)
                            .setPositiveButton("OK", null)
                            .show();
                    //startActivity(new Intent(SendSMS.this, ChooseOption.class));
                    //overridePendingTransition(R.anim.animation, R.anim.animation2);
                    break;
                case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                    Log.d("SMSMANAGER", "Generic failure");
                    //Toast.makeText(getBaseContext(), "Generic failure",Toast.LENGTH_SHORT).show();
                    title = "Sending Failed!";
                    messager = getString(R.string.RESULT_ERROR_GENERIC_FAILURE);
                    new AlertDialog.Builder(MainMenuActivity.this)
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
                    new AlertDialog.Builder(MainMenuActivity.this)
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
                    new AlertDialog.Builder(MainMenuActivity.this)
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
                    new AlertDialog.Builder(MainMenuActivity.this)
                            .setTitle(title)
                            .setMessage(messager)
                            .setPositiveButton("OK", null)
                            .show();
                    break;
            }

        }
    }













    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private List<ItemObject> getAllItemObject(){
        ItemObject itemObject = null;
        List<ItemObject> items = new ArrayList<>();

        if(loadertype.equals("dealer")) {
            //TODO
            items.add(new ItemObject("Image One", "load_retailer"));
            //items.add(new ItemObject("Image Two", "register_retailer"));
            items.add(new ItemObject("Image Three", "pass_load"));
            items.add(new ItemObject("Image Four", "check_balance"));
            items.add(new ItemObject("Image Five", "product_list"));
            items.add(new ItemObject("Image Six", "settings"));
        }
        else
        {
            items.add(new ItemObject("Image One", "transfer_load"));
            items.add(new ItemObject("Image Two", "product_list"));
            items.add(new ItemObject("Image Three", "check_balance"));
            items.add(new ItemObject("Image Four", "settings"));
        }

        return items;
    }








    private void dialNumber(final String phoneNumber) {
        //permission is automatically granted on sdk<23 upon installation
        if (Build.VERSION.SDK_INT >= 23) {
            //Check if you have permissions to execute a call (number or USSD)
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                Log.v("TAG","Permission is granted");
                startActivity(new Intent(ACTION_CALL, Uri.fromParts("tel", phoneNumber, null)));

            } else {
                //Request permissions if we don't have them
                Log.v("TAG","Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.CALL_PHONE}, 1);
                startActivity(new Intent(ACTION_CALL, Uri.fromParts("tel", phoneNumber, null)));
            }
        }
        else {
            Log.v("TAG","Permission is granted");
            startActivity(new Intent(ACTION_CALL, Uri.fromParts("tel", phoneNumber, null)));
        }

    }









}






