package com.inducesmile.retailer;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.preference.EditTextPreference;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.gson.JsonObject;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.inducesmile.retailer.PreferenceActivity;
import com.inducesmile.retailer.adapters.TelephonyInfo;

public class PreferenceFragmentCustom extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener {

    public PreferenceFragmentCustom ctx = this;

    public String versionControl;

    public static final String LOG_TAG = "Android Downloader by The Code Of A Ninja";


    private static String remoteurl = "https://loader.ph/api/product_list.json";
    private static String remoteurl2 = "https://loader.ph/api/product_list2.json";
    //private static String remoteurl3 = "http://speedtest.ftp.otenet.gr/files/test100Mb.db";


    //initialize our progress dialog/bar
    private ProgressDialog mProgressDialog;
    public static final int DIALOG_DOWNLOAD_PROGRESS = 0;


    private String imeiSIM1 = null;
    private String imeiSIM2 = null;
    private String pnumSIM1 = null;
    private String pnumSIM2 = null;
    private String netnameSIM1 = null;
    private String netnameSIM2 = null;
    private Integer simidSIM1 = null;
    private Integer simidSIM2 = null;
    private boolean isSIM1Ready = false;
    private boolean isSIM2Ready = false;

    @Override
    public void onCreatePreferences(Bundle bundle, String s) {
        // Load the Preferences from the XML file
        addPreferencesFromResource(R.xml.app_preferences);

        Log.d("preferencer", "PrefereceFragmentCustom Loaded");

        ///SET THE VALUE
        Preference prefgateway = findPreference("gateway");
        Log.d("preferencer", String.valueOf(((ListPreference) prefgateway).getEntry()));
        if(((ListPreference) prefgateway).getEntry() == null) {
            prefgateway.setSummary("Choose your preferred gateway");
        }
        else
        {
            prefgateway.setSummary(((ListPreference) prefgateway).getEntry());
        }


        Preference prefloadertype = findPreference("loadertype");
        Log.d("preferencer", String.valueOf(((ListPreference) prefloadertype).getEntry()));
        if(((ListPreference) prefloadertype).getEntry() == null) {
            prefloadertype.setSummary("Choose your loader type");
        }
        else
        {
            prefloadertype.setSummary(((ListPreference) prefloadertype).getEntry());
        }





        TelephonyInfo telephonyInfo = TelephonyInfo.getInstance(getContext());
        boolean isDualSIM = telephonyInfo.isDualSIM();
        boolean prefEnabled = false;

        Preference prefdualsim = findPreference("dualsim");

        SharedPreferences dualsimpreference = PreferenceManager.getDefaultSharedPreferences(getContext());
        SharedPreferences.Editor editor = dualsimpreference.edit();

        if(isDualSIM) {
            String imeiSIM1 = telephonyInfo.getImsiSIM1();
            String imeiSIM2 = telephonyInfo.getImsiSIM2();
            String pnumSIM1 = telephonyInfo.getpnumSIM1();
            String pnumSIM2 = telephonyInfo.getpnumSIM2();
            String netnameSIM1 = telephonyInfo.getnetnameSIM1();
            String netnameSIM2 = telephonyInfo.getnetnameSIM2();
            Integer simidSIM1 = telephonyInfo.getsimidSIM1();
            Integer simidSIM2 = telephonyInfo.getsimidSIM2();
            boolean isSIM1Ready = telephonyInfo.isSIM1Ready();
            boolean isSIM2Ready = telephonyInfo.isSIM2Ready();


            if(isSIM1Ready && isSIM2Ready) {
                prefEnabled = true;
            }

            //INITIALIZE PREFDUALSIM

            Log.d("DUALSIMZ", "Start");

            String defSummary = "Select Your SIM";
            if (!isSIM1Ready || !isSIM2Ready) {
                Log.d("DUALSIMZ", "1 of the SIM is not ready");
                isDualSIM = false;
                defSummary = "NO Simcard is detected";

                //DISPLAY ONE VALUE
                if (isSIM1Ready) {
                    Log.d("DUALSIMZ", "isSIM1Ready(0): YES" + simidSIM1);
                    defSummary = netnameSIM1 + " - " + pnumSIM1;
                    editor.putString("dualsim", String.valueOf(simidSIM1));
                    editor.apply();
                } else if (isSIM2Ready) {
                    Log.d("DUALSIMZ", "isSIM2Ready(1): YES" + simidSIM2);
                    defSummary = netnameSIM2 + " - " + pnumSIM2;
                    editor.putString("dualsim", String.valueOf(simidSIM2));
                    editor.apply();
                } else {
                    Log.d("DUALSIMZ", "NotReady");
                    //CLEAR IF NO SIM DETECTED
                    dualsimpreference.edit().remove("dualsim").apply();
                }

                prefEnabled = false;

                //clear if neither of the SIMS are ready
                //SharedPreferences dualprefs = PreferenceManager.getDefaultSharedPreferences(getContext());
                //dualprefs.edit().remove("dualsim").apply();
            }

            //ENABLE IF SIM IS DUAL


            ///CharSequence[] entryValues = new CharSequence[]{ "1", "2", "3" };


            List<String> dualSIMentries = new ArrayList<String>();
            dualSIMentries.add(netnameSIM1 + " - " + pnumSIM1);
            dualSIMentries.add(netnameSIM2 + " - " + pnumSIM2);
            final CharSequence[] dualSIMentriesChar = dualSIMentries.toArray(new CharSequence[0]);
            ((ListPreference) prefdualsim).setEntries(dualSIMentriesChar);


            List<String> dualSIMentryValues = new ArrayList<String>();
            dualSIMentryValues.add(simidSIM1.toString());
            dualSIMentryValues.add(simidSIM2.toString());
            final CharSequence[] dualSIMentryvaluesChar = dualSIMentryValues.toArray(new CharSequence[0]);
            ((ListPreference) prefdualsim).setEntryValues(dualSIMentryvaluesChar);


            Log.d("DUALSIM", "Entry: " + String.valueOf(((ListPreference) prefdualsim).getEntry()));
            if (((ListPreference) prefdualsim).getEntry() == null) {
                prefdualsim.setSummary(defSummary);
            } else {
                if (!isSIM1Ready || !isSIM2Ready) {
                    prefdualsim.setSummary(defSummary);

                } else {
                    prefdualsim.setSummary(((ListPreference) prefdualsim).getEntry());
                }
            }

        }
        else
        {
            prefdualsim.setSummary("Option Unavailable");
            editor.putString("dualsim", "0");
            editor.apply();
        }

        //DISABLE ENABLE DUAL SIM PEFERENCE
        prefdualsim.setEnabled(prefEnabled);




        ///get cache dir
        String dirLoc = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            dirLoc = Objects.requireNonNull(getContext()).getCacheDir() + File.separator + "brand/";
            //Toast.makeText(getContext(), dirLoc, Toast.LENGTH_LONG).show();
        }
        else
        {
            //TODO for debugging purposes
            //Toast.makeText(getContext(), "This App only works on KitKat Devices and Above", Toast.LENGTH_LONG).show();
            Log.d("null", "null");
        }

        //making sure the download directory exists
        new PreferenceActivity().checkAndCreateDirectory(dirLoc);

        String fileName_local = "product_list.json";
        String filePath_local = dirLoc + fileName_local;
        String jsonfile_local = null;
        try {
            jsonfile_local = getStringFromFile(filePath_local);
            JSONObject jsonObj = new JSONObject(jsonfile_local);
            versionControl = jsonObj.getString("version_control");
            Log.d("preferencex", versionControl);
        } catch (Exception e) {
            e.printStackTrace();
        }


        final Preference appverPref = findPreference("app_version");
        appverPref.setTitle("Version: " + versionControl);
        appverPref.setSummary("Update to the latest promos");




        appverPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {
                //open browser or intent here
                //Toast.makeText(getContext(),"Preference has been clicked...", Toast.LENGTH_LONG).show();

                appverPref.setTitle("Version: " + versionControl);

                return true;
            }
        });










    }


    @Override
    public void onDisplayPreferenceDialog(Preference preference) {

        // Try if the preference is one of our custom Preferences
        DialogFragment dialogFragment = null;
        if (preference instanceof TimePreference) {
            // Create a new instance of TimePreferenceDialogFragment with the key of the related
            // Preference
            dialogFragment = TimePreferenceDialogFragmentCompat.newInstance(preference.getKey());
        }


        if (dialogFragment != null) {
            // The dialog was created (it was one of our custom Preferences), show the dialog for it
            dialogFragment.setTargetFragment(this, 0);
            assert this.getFragmentManager() != null;
            dialogFragment.show(this.getFragmentManager(), "android.support.v7.preference" +
                    ".PreferenceFragment.DIALOG");
        } else {
            // Dialog creation could not be handled here. Try with the super method.
            super.onDisplayPreferenceDialog(preference);
        }
    }







    @Override
    public void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Preference pref = findPreference(key);
        if (pref instanceof EditTextPreference) {
            EditTextPreference editPref = (EditTextPreference) pref;
            pref.setSummary(editPref.getText());
        }

        if (pref instanceof ListPreference) {
            if (key.equals("gateway") || key.equals("loadertype") || key.equals("dualsim") ) {
                pref.setSummary(((ListPreference) pref).getEntry());
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            startActivity(new Intent(getActivity(), MainMenuActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public String getStringFromFile(String filePath) throws Exception {
        File fl = new File(filePath);
        FileInputStream fin = new FileInputStream(fl);
        String ret = convertStreamToString(fin);
        //Make sure you close all streams.
        fin.close();
        return ret;
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



}



