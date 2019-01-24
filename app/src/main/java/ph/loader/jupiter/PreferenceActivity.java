package com.inducesmile.retailer;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.PowerManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.preference.EditTextPreference;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v4.app.DialogFragment;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.preference.PreferenceManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Objects;


import com.inducesmile.retailer.adapters.TelephonyInfo;
import com.novoda.merlin.Bindable;
import com.novoda.merlin.Connectable;
import com.novoda.merlin.Disconnectable;
import com.novoda.merlin.Merlin;
import com.novoda.merlin.MerlinsBeard;
import com.novoda.merlin.NetworkStatus;


import android.telephony.SubscriptionManager;
import android.telephony.SubscriptionInfo;

public class PreferenceActivity extends AppCompatActivity {


    public static final String LOG_TAG = "Android Downloader by The Code Of A Ninja";

    //initialize our progress dialog/bar

    //initialize root directory
    File rootDir = Environment.getExternalStorageDirectory();

    //defining file name and url
    public String fileName2 = "codeofaninja.db";
    public String bigfile = "http://speedtest.ftp.otenet.gr/files/test100Mb.db";

    // declare the dialog as a member field of your activity


    public Context context;


    Button btnShowProgress;
    private ProgressDialog pDialog;

    ImageView my_image;

    public static final int progress_bar_type = 0;

    //private static String file_url = "http://idsp.ak.gov.ng/images/logo.png";


    //TODO CREATE A CRON FOR JSON CHECKING ON A LIVE WEB SERVER 2MINS THIS WILL PRODUCE THE .json FILES
    //TODO LINK 1 https://loader.ph/api/product_list.php
    //TODO LINK 2 https://loader.ph/api/product_list2.php
    private static String remoteurl = "https://loader.ph/api/product_list.json";
    private static String remoteurl2 = "https://loader.ph/api/product_list2.json";
    //private static String remoteurl3 = "http://speedtest.ftp.otenet.gr/files/test100Mb.db";


    private String fileName;


    private boolean jsonNotif;


    private MerlinsBeard merlinsBeard;


    static {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_AUTO);
    }




    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preference_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        Objects.requireNonNull(getSupportActionBar()).setTitle("SETUP LOADER");


        merlinsBeard = new MerlinsBeard.Builder().build(this);


        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        // then you use
        String prefgateway = prefs.getString("gateway", "Click to choose a gateway");
        String loadertypegateway = prefs.getString("loadertype", "Choose Account Type");
        String dualsimgateway = prefs.getString("dualsim", "Select you SIM");


        TelephonyInfo telephonyInfo = TelephonyInfo.getInstance(this);

        boolean isDualSIM = telephonyInfo.isDualSIM();

        //String imeiSIM1 = telephonyInfo.getImsiSIM1();
        //String imeiSIM2 = telephonyInfo.getImsiSIM2();
        String pnumSIM1 = telephonyInfo.getpnumSIM1();
        String pnumSIM2 = telephonyInfo.getpnumSIM2();
        Integer simidSIM1 = telephonyInfo.getsimidSIM1();
        Integer simidSIM2 = telephonyInfo.getsimidSIM2();
        String netnameSIM1 = telephonyInfo.getnetnameSIM1();
        String netnameSIM2 = telephonyInfo.getnetnameSIM2();
        boolean isSIM1Ready = telephonyInfo.isSIM1Ready();
        boolean isSIM2Ready = telephonyInfo.isSIM2Ready();

        //Log.d("DUALSIM", " IME1 : " + imeiSIM1);
        //Log.d("DUALSIM", " IME2 : " + imeiSIM2);
        Log.d("DUALSIM", " NUM1 : " + pnumSIM1);
        Log.d("DUALSIM", " NUM2 : " + pnumSIM2);
        Log.d("DUALSIM", " SIMID1 : " + simidSIM1);
        Log.d("DUALSIM", " SIMID2 : " + simidSIM2);
        Log.d("DUALSIM", " NETNAME1 : " + netnameSIM1);
        Log.d("DUALSIM", " NETNAME2 : " + netnameSIM2);
        Log.d("DUALSIM", " IS SIM1 READY : " + isSIM1Ready);
        Log.d("DUALSIM", " IS SIM2 READY : " + isSIM2Ready);


        SharedPreferences dualsimpreference = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = dualsimpreference.edit();

        if(isDualSIM) {
            final SubscriptionManager subscriptionManager = SubscriptionManager.from(getApplicationContext());
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

            final List<SubscriptionInfo> activeSubscriptionInfoList = subscriptionManager.getActiveSubscriptionInfoList();
            int simCount = activeSubscriptionInfoList.size();
            Log.d("DUALSIM: ", "simCount:" + simCount);

            for (SubscriptionInfo subscriptionInfo : activeSubscriptionInfoList) {
                Log.d("DUALSIM: ", "iccId :" + subscriptionInfo.getNumber() + " , name : " + subscriptionInfo.getDisplayName());
            }


            if (!isSIM1Ready || !isSIM2Ready) {
                Log.d("DUALSIMZ", "1 of the SIM is not ready");
                isDualSIM = false;

                //DISPLAY ONE VALUE
                if (isSIM1Ready) {
                    Log.d("DUALSIMZ", "PreferenceActivity isSIM1Ready: " + simidSIM1);
                    editor.putString("dualsim", String.valueOf(simidSIM1));
                    editor.apply();
                } else if (isSIM2Ready) {
                    Log.d("DUALSIMZ", "PreferenceActivity isSIM2Ready: " + simidSIM2);
                    editor.putString("dualsim", String.valueOf(simidSIM2));
                    editor.apply();
                } else {
                    Log.d("DUALSIMZ", "NotReady");
                    //CLEAR IF NO SIM DETECTED
                    dualsimpreference.edit().remove("dualsim").apply();
                }
            }

        }
        else
        {
            Log.d("DUALSIMZ", "Dual SIM preference cleared");
            dualsimpreference.edit().remove("dualsim").apply();
        }



        /////SAVE ALL TO PREFERENCES STORAGE MUST BE AT THE END OF ONCREATE
        if (savedInstanceState == null) {
            Fragment preferenceFragment = new PreferenceFragmentCustom();
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            //display
            ft.add(R.id.pref_container, preferenceFragment);
            //save
            ft.commit();
        }




        /*
        ///get cache dir
        String dirLoc = getCacheDir() + File.separator + "brand/";

        //making sure the download directory exists
        checkAndCreateDirectory(dirLoc);

        Preference appverPref = findPreference("app_version");
        appverPref.setSummary("versionControl")
        appverPref.setTitle("Products List Update:" + versionControl)

        Log.d("JPTRX", versionControl)
        //gateway
        val gateWay = forecast.getString("gateway")
        Log.d("gateway", gateWay)
        appverPref.setOnPreferenceClickListener {
            Log.d("update clicked", "product update clicked")
            //val intent = Intent(getActivity(), UpdateProductListActivity::class.java)
            //startActivity(intent)
            val remoteurl = "https://loader.ph/api/product_list.json"
            val remoteurl2 = "https://loader.ph/api/product_list2.json"

            val fileName_local3 = "product_list3.json"
            val filePath_local3 = folder + fileName_local3

            //downloadFileJSON(remoteurl, File(filePath_local3));

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                DownloadTask(context, Utils.downloadJSON1)
                DownloadTask(context, Utils.downloadJSON2)
            }
            return true;
        }


        //executing the asynctask
        new DownloadFileAsync().execute(fileURL);
        ****/









    }



    //function to verify if directory exists
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public void checkAndCreateDirectory(String dirName){
        File new_dir = new File( rootDir + dirName );
        if( !new_dir.exists() ){
            Log.d("preferencex", "New Directory created! " + getClass());
            new_dir.mkdirs();
        } else {
                Log.d("preferencex", "Directory already exists! " + getClass());
        }
    }



    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        Log.d("JPTRback", "top back pressed");
        //startActivity(new Intent(PreferenceActivity.this, MainMenuActivity.class));
        return true;
    }


    public void onBackPressed() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String gatewayStr = prefs.getString("gateway", "<unset>");
        String loadertypeStr = prefs.getString("loadertype", "<unset>");
        String dualsimStr = prefs.getString("dualsim", "<unset>");

        Log.d("onback", "gatewayStr:" + gatewayStr + " loadertypeStr:" + loadertypeStr + " dualsimStr:" + dualsimStr);

        assert gatewayStr != null;
        assert loadertypeStr != null;
        assert dualsimStr != null;
        if(gatewayStr.equals("<unset>") || loadertypeStr.equals("<unset>") || dualsimStr.equals("<unset>"))
        {
            AlertDialog.Builder alert = new AlertDialog.Builder(this);
            alert.setTitle("Do you want to exit?");
            // alert.setMessage("Message");

            alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    //Your action here
                    /*
                    finish();
                    moveTaskToBack(true);
                    android.os.Process.killProcess(android.os.Process.myPid());
                    System.exit(1);
                    */
                    appExit();
                }
            });

            alert.setNegativeButton("Cancel",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                        }
                    });

            alert.show();
        }
        else
            {
            Log.d("JPTRback", "bottom softkey back pressed");
            startActivity(new Intent(PreferenceActivity.this, MainMenuActivity.class));
            overridePendingTransition(R.anim.slideinright, R.anim.slideinleft);
        }
    }


    public void appExit() {
        this.finish();
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        System.exit(1);
    }













    @Override
    protected Dialog onCreateDialog(int id){
        switch (id){
            case progress_bar_type:
                pDialog = new ProgressDialog(this);
                pDialog.setMessage("Downloading product list. Please wait...");
                pDialog.setIndeterminate(false);
                pDialog.setMax(100);
                pDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                pDialog.setCancelable(false);
                pDialog.show();
                return pDialog;
            default:
                return null;
        }
    }




    public void onButtonClick(View v) {

        jsonNotif = false;
        Log.d("Button", "Yeah, button was clicked");

        //new DownloadFileFromURL().execute(remoteurl3);
        //new DownloadFileFromURL().execute(file_url);
        //new DownloadFileFromURL().execute(bigfile);



        merlinsBeard.hasInternetAccess(new MerlinsBeard.InternetAccessCallback() {

            @Override
            public void onResult(boolean hasAccess) {
                Log.d("InternetCheck", "Merlin Loaded " + hasAccess);
                if(hasAccess) {
                    new DownloadFileFromURL().execute(remoteurl);
                    new DownloadFileFromURL().execute(remoteurl2);
                    Log.d("InternetCheck", "hasInternet");
                }
                else
                {
                    String title = "Update Failed!";
                    //String messager = fileName + " successfully updated!";
                    String messager = "Internet access is require for this feature";

                    new AlertDialog.Builder(PreferenceActivity.this)
                            .setTitle(title)
                            .setMessage(messager)
                            .setPositiveButton("OK", null)
                            .show();
                    Log.d("InternetCheck", "noInternet");
                }
            }
        });


        /*
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor prefEditor = sharedPref.edit();
        prefEditor.putString("userChoice","hahahaha");
        prefEditor.apply();


        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        String userChoice = sharedPref.getString("userChoice", "no userchoice set");

        Toast.makeText(this, userChoice,
                Toast.LENGTH_LONG).show();
        */

    }



    @SuppressLint("StaticFieldLeak")
    class DownloadFileFromURL extends AsyncTask<String, String, String>{

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            showDialog(progress_bar_type);
        }

        @Override
        protected  String doInBackground(String... f_url){
            int count;
            try{
                URL url = new URL(f_url[0]);
                URLConnection connection = url.openConnection();
                connection.connect();

                int lenghtOfFile = connection.getContentLength();

                InputStream input = new BufferedInputStream(url.openStream(), 8192);

                //String storageDir = Environment.getExternalStorageDirectory().getAbsolutePath();

                String cachedir = getCacheDir().getPath() + File.separator + "brand/";

                Log.d("JPTR", cachedir);

                //Create androiddeft folder if it does not exist
                File directory = new File(cachedir);
                if (!directory.exists()) {
                    Log.d("JPTR", "Cache Dir created");
                    //noinspection ResultOfMethodCallIgnored
                    directory.mkdirs();
                }
                else
                {
                    Log.d("JPTR", "Cache Dir already existed");
                }


                //String fileName = "/downloadedfile.jpg";

                String urlPath = url.getPath();
                fileName = urlPath.substring(urlPath.lastIndexOf('/') + 1);

                File imageFile = new File(cachedir + fileName);
                OutputStream output = new FileOutputStream(imageFile);

                byte data[] = new byte[1024];
                long total = 0;

                while((count = input.read(data)) != -1){
                    total += count;

                    publishProgress("" + (int)((total*100)/lenghtOfFile));

                    output.write(data, 0, count);
                    //Thread.sleep(1000);
                }
                output.flush();

                output.close();
                input.close();
            }catch (Exception e){
                Log.e("Error: ", e.getMessage());
            }

            return null;

        }

        protected void onProgressUpdate(String... progress){
            pDialog.setProgress(Integer.parseInt(progress[0]));
        }

        @Override
        protected void onPostExecute(String file_url){
            dismissDialog(progress_bar_type);


            if(!jsonNotif) {
                String title = "Success!";
                //String messager = fileName + " successfully updated!";
                String messager = "Product list successfully updated!";

                new AlertDialog.Builder(PreferenceActivity.this)
                        .setTitle(title)
                        .setMessage(messager)
                        .setPositiveButton("OK", null)
                        .show();

                //String imagePath = Environment.getExternalStorageDirectory() + "/downloadedfile.jpg";
                //my_image.setImageDrawable(Drawable.createFromPath(imagePath));

                jsonNotif = true;
            }
        }
    }





}


