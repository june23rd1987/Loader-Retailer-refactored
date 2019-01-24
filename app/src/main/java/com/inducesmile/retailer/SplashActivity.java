package com.inducesmile.retailer;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.multidex.MultiDex;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;


import com.inducesmile.retailer.adapters.TelephonyInfo;
import com.yarolegovich.lovelydialog.LovelyDialogCompat;
import com.yarolegovich.lovelydialog.LovelySaveStateHandler;
import com.yarolegovich.lovelydialog.LovelyStandardDialog;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Objects;

import static android.Manifest.permission.ACCESS_NETWORK_STATE;
import static android.Manifest.permission.ACCESS_WIFI_STATE;
import static android.Manifest.permission.CALL_PHONE;
import static android.Manifest.permission.INTERNET;
import static android.Manifest.permission.READ_CONTACTS;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.READ_PHONE_STATE;
import static android.Manifest.permission.SEND_SMS;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class SplashActivity extends AppCompatActivity {

    private static final String TAG = SplashActivity.class.getSimpleName();


    //DIALOG BOX
    private LovelySaveStateHandler saveStateHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        ActionBar actionBar = getSupportActionBar();
        if(null != actionBar){
            actionBar.hide();
        }

        //PERMISSION EXECUTE START
        boolean chkperm = checkPermission();
        Log.d("splasher", "permission " + String.valueOf(chkperm));
        if (!chkperm) {
            Log.d("splasher", "permission chkperm false openActivity()");
            openActivity();
        } else {
            Log.d("splasher", "permission chkperm true");
            if (checkPermission()) {
                Log.d("splasher", "checkPermission requestPermissionAndContinue openActivity()");
                requestPermissionAndContinue();
                openActivity();
            } else {
                Log.d("splasher", "checkPermission openActivity");
                openActivity();
            }
        }
        //PERMISSION EXECUTE END

        /***
        int SPLASH_DISPLAY_LENGTH = 3000;
        new Handler().postDelayed(new Runnable(){
            @Override
            public void run(){
                Intent startActivityIntent = new Intent(SplashActivity.this, MainMenuActivity.class);
                startActivity(startActivityIntent);
                SplashActivity.this.finish();
            }
        }, SPLASH_DISPLAY_LENGTH);
        ***/


        //INITIATE DIALOG BOX
        saveStateHandler = new LovelySaveStateHandler();





        //CHECK INTERNET CONNECTION
        boolean connected = false;
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            //we are connected to a network
            connected = true;
            Log.d("splasher", "Network Connected");
        }
        else {
            Log.d("splasher", "Network NOT Connected");
        }


        /*
        if(connected)
        {
            boolean online = internetIsConnected();
            if(online)
            {
                Log.d("JPTR", "Internet Connected");
            }
            else
            {
                Log.d("JPTR", "Internet NOT Connected");
            }
        }
         */


    }














    public boolean internetIsConnected() {
        try {
            String command = "ping -c 1 8.8.8.8";
            return (Runtime.getRuntime().exec(command).waitFor() == 0);
        } catch (Exception e) {
            return false;
        }
    }




    //PERMISSION START
    private static final int PERMISSION_REQUEST_CODE = 200;
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)

    private boolean checkPermission() {
        return     ContextCompat.checkSelfPermission(this, WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, READ_EXTERNAL_STORAGE)  != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, CALL_PHONE)             != PackageManager.PERMISSION_GRANTED
                //PERMISSION BELOW ARE BUGGY DONT ENABLE THEM
                //&& ContextCompat.checkSelfPermission(this, INTERNET)               != PackageManager.PERMISSION_GRANTED
                //&& ContextCompat.checkSelfPermission(this, ACCESS_NETWORK_STATE)   != PackageManager.PERMISSION_GRANTED
                // ContextCompat.checkSelfPermission(this, ACCESS_WIFI_STATE)      != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, READ_CONTACTS)          != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, SEND_SMS)               != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, READ_PHONE_STATE)       != PackageManager.PERMISSION_GRANTED
                ;
    }

    private void requestPermissionAndContinue() {
        if (       ContextCompat.checkSelfPermission(this, WRITE_EXTERNAL_STORAGE)  != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, READ_EXTERNAL_STORAGE)   != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, CALL_PHONE)              != PackageManager.PERMISSION_GRANTED
                //PERMISSION BELOW ARE BUGGY DONT ENABLE THEM
                //&& ContextCompat.checkSelfPermission(this, INTERNET)                != PackageManager.PERMISSION_GRANTED
                //&& ContextCompat.checkSelfPermission(this, ACCESS_NETWORK_STATE)    != PackageManager.PERMISSION_GRANTED
                //&& ContextCompat.checkSelfPermission(this, ACCESS_WIFI_STATE)       != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, READ_CONTACTS)           != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, SEND_SMS)                != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, READ_PHONE_STATE)        != PackageManager.PERMISSION_GRANTED
                ) {

            Log.d("JPTRPERM", "ContextCompat.checkSelfPermission");

            if (       ActivityCompat.shouldShowRequestPermissionRationale(this, WRITE_EXTERNAL_STORAGE)
                    && ActivityCompat.shouldShowRequestPermissionRationale(this, READ_EXTERNAL_STORAGE)
                    && ActivityCompat.shouldShowRequestPermissionRationale(this, CALL_PHONE)
                    //PERMISSION BELOW ARE BUGGY DONT ENABLE THEM
                    //&& ActivityCompat.shouldShowRequestPermissionRationale(this, INTERNET)
                    //&& ActivityCompat.shouldShowRequestPermissionRationale(this, ACCESS_NETWORK_STATE)
                    //&& ActivityCompat.shouldShowRequestPermissionRationale(this, ACCESS_WIFI_STATE)
                    && ActivityCompat.shouldShowRequestPermissionRationale(this, READ_CONTACTS)
                    && ActivityCompat.shouldShowRequestPermissionRationale(this, SEND_SMS)
                    && ActivityCompat.shouldShowRequestPermissionRationale(this, READ_PHONE_STATE)
                    ) {

                Log.v("JPTRPERM", "ActivityCompat.shouldShowRequestPermissionRationale");

                AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
                alertBuilder.setCancelable(true);
                alertBuilder.setTitle(getString(R.string.permission_necessary));
                alertBuilder.setMessage(R.string.storage_permission_is_encessary_to_wrote_event);
                alertBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions(SplashActivity.this, new String[]{
                                WRITE_EXTERNAL_STORAGE
                                ,READ_EXTERNAL_STORAGE
                                ,CALL_PHONE
                                //PERMISSION BELOW ARE BUGGY DONT ENABLE THEM
                                //,INTERNET
                                //,ACCESS_NETWORK_STATE
                                //,ACCESS_WIFI_STATE
                                ,READ_CONTACTS
                                ,SEND_SMS
                                ,READ_PHONE_STATE
                        }, PERMISSION_REQUEST_CODE);
                    }
                });
                AlertDialog alert = alertBuilder.create();
                alert.show();
                Log.v("JPTRPERM", "permission denied, show dialog");
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    ActivityCompat.requestPermissions(SplashActivity.this, new String[]{
                             WRITE_EXTERNAL_STORAGE
                            ,WRITE_EXTERNAL_STORAGE
                            ,CALL_PHONE
                            //PERMISSION BELOW ARE BUGGY DONT ENABLE THEM
                            //,INTERNET
                            //,ACCESS_NETWORK_STATE
                            //,ACCESS_WIFI_STATE
                            ,READ_CONTACTS
                            ,SEND_SMS
                            ,READ_PHONE_STATE
                    }, PERMISSION_REQUEST_CODE);
                }
            }
        } else {
            Log.v("splasher", "!ContextCompat.checkSelfPermission openActivity()");
            openActivity();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == PERMISSION_REQUEST_CODE) {
            Log.v("splasher", "requestCode == PERMISSION_REQUEST_CODE");
            if (permissions.length > 0 && grantResults.length > 0) {
                Log.d("splasher", "permissions.length > 0 && grantResults.length > 0");

                boolean flag = true;
                Log.d("splasher", "permissions.length:" + String.valueOf(permissions.length));
                Log.d("splasher", "grantResults.length:" + String.valueOf(grantResults.length));
                for (int grantResult : grantResults) {
                    if (grantResult != PackageManager.PERMISSION_GRANTED) {
                        flag = false;
                    }
                }

                /*
                if (flag) {
                    Log.v("JPTRPERM", "flag");
                    openActivity();
                } else {
                    Log.v("JPTRPERM", "!flag");
                    //finish();
                    openActivity();
                }
                 */

            } else {
                //finish();
                Log.d("splasher", "!permissions.length > 0 && grantResults.length > 0 then openActivity()");
                openActivity();
            }
        } else {
            Log.d("splasher", "!requestCode == PERMISSION_REQUEST_CODE");
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @SuppressLint("NewApi")
    private void openActivity() {
        //add your further process after giving permission or to download images from remote server.

        SharedPreferences prefs = (SharedPreferences) PreferenceManager.getDefaultSharedPreferences(this);

        String gatewayStr = prefs.getString("gateway", "<unset>");
        String loadertypeStr = prefs.getString("loadertype", "<unset>");

        TelephonyInfo telephonyInfo = TelephonyInfo.getInstance(this);

        boolean isDualSIM = telephonyInfo.isDualSIM();

        String dualsimgateway;

        if(isDualSIM)
        {
            dualsimgateway = prefs.getString("dualsim", "<unset>");
        }
        else
        {
            dualsimgateway = prefs.getString("dualsim", "0");
        }

        assert gatewayStr != null;
        assert loadertypeStr != null;
        assert dualsimgateway != null;
        if (gatewayStr.equals("<unset>") || loadertypeStr.equals("<unset>") || dualsimgateway.equals("<unset>")) {
            Log.d("splasher","Is Null: " + loadertypeStr + " " + gatewayStr + " " + dualsimgateway);

            showLovelyDialog(null);


        } else {
            Log.d("splasher","Is NOT Null: loadertypeStr:" + loadertypeStr + " gatewayStr:" + gatewayStr + " dualsimgateway:" + dualsimgateway);
            int SPLASH_DISPLAY_LENGTH = 3000;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent startActivityIntent = new Intent(SplashActivity.this, MainMenuActivity.class);
                    startActivity(startActivityIntent);
                    //SplashActivity.this.finish();
                }
            }, SPLASH_DISPLAY_LENGTH);
        }
        //listAssetFiles("");

        try {
            copyproductlist();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    //PERMISSION END





    private void showLovelyDialog(Bundle savedInstanceState) {
        showStandardDialog(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        saveStateHandler.saveInstanceState(outState);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedState) {
        super.onRestoreInstanceState(savedState);
        if (LovelySaveStateHandler.wasDialogOnScreen(savedState)) {
            //Dialog won't be restarted automatically, so we need to call this method.
            //Each dialog knows how to restore its state
            showLovelyDialog(savedState);
        }
    }

    private void showStandardDialog(Bundle savedInstanceState) {
        new LovelyStandardDialog(this, LovelyStandardDialog.ButtonLayout.VERTICAL)
                //.setTopColorRes(R.color.indigo)
                .setButtonsColorRes(R.color.darkDeepOrange)
                //.setIcon(R.drawable.logo36dp)
                .setTitle(R.string.splash_dialog_title)
                //.setInstanceStateHandler(0, saveStateHandler)
                .setSavedInstanceState(savedInstanceState)
                .setMessage(R.string.splash_dialog_message)
                .setPositiveButton(R.string.gotosettings, LovelyDialogCompat.wrap(
                        (dialog, which) ->
                                SplashToSettings()
                ))
                //.setNeutralButton(R.string.later, null)
                .setNegativeButton(R.string.splash_dialog_exit, LovelyDialogCompat.wrap(
                        (dialog, which) ->
                                SplashExitApp()
                ))
                .setCancelable(false)
                .show();
    }

    private void SplashToSettings(){
        startActivity(new Intent(SplashActivity.this, PreferenceActivity.class));
        Log.d("splasher", "SplashToSettings");
    }

    private void SplashExitApp(){
        //exit app on cancel
        /*
        finish();
        moveTaskToBack(true);
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(1);
        */
        this.finish();
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        System.exit(1);
    }










    private boolean listAssetFiles(String path) {
        String [] list;
        try {
            list = getAssets().list(path);
            assert list != null;
            if (list.length > 0) {
                // This is a folder
                for (String file : list) {
                    if (!listAssetFiles(path + "/" + file)) {
                        return false;
                    }
                    else
                        {
                            Log.d("copyproductlist", file);
                        // This is a file
                        // TODO: add file name to an array list
                    }
                }
            }
        } catch (IOException e) {
            return false;
        }
        return true;
    }



    public void copyproductlist() throws IOException {

        String cachedir = getCacheDir().getPath() + File.separator + "brand/";
        String fName1 = "product_list.json";
        String fName2 = "product_list2.json";
        String folder = cachedir;
        //Create androiddeft folder if it does not exist
        File directory = new File(folder);
        if (!directory.exists()) {
            //noinspection ResultOfMethodCallIgnored
            directory.mkdirs();
        }

        if(Arrays.asList(getResources().getAssets().list("")).contains(fName1))
        {
            Log.d("copyproductlist", "fname1 exists");
        }
        else
        {
            Log.d("copyproductlist", "fname1 does not exists");
        }

        if(Arrays.asList(getResources().getAssets().list("")).contains(fName2))
        {
            Log.d("copyproductlist", "fname2 exists");
        }


        File f1 = new File(cachedir + fName1);
        if(!f1.exists()) {
            OutputStream myOutput1 = new FileOutputStream(cachedir + fName1);
            byte[] buffer1 = new byte[1024];
            int length1;
            InputStream myInput1 = getAssets().open(fName1);
            while ((length1 = myInput1.read(buffer1)) > 0) {
                myOutput1.write(buffer1, 0, length1);
            }
            myInput1.close();
            myOutput1.flush();
            myOutput1.close();
        }
        else {
            Log.d("file", "file1 exists");
        }

        File f2 = new File(cachedir + fName2);
        if(!f2.exists()) {
            OutputStream myOutput2 = new FileOutputStream(cachedir + fName2);
            byte[] buffer2 = new byte[1024];
            int length2;
            InputStream myInput2 = getAssets().open(fName2);
            while ((length2 = myInput2.read(buffer2)) > 0) {
                myOutput2.write(buffer2, 0, length2);
            }
            myInput2.close();
            myOutput2.flush();
            myOutput2.close();
        }
        else {
            Log.d("file", "file2 exists");
        }
    }

}
