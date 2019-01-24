package com.inducesmile.retailer;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

public class SplashGatewayActivity extends AppCompatActivity {

    private static final String TAG = SplashGatewayActivity.class.getSimpleName();

    private Context context = this;

    private String loadertypeval;
    private String gatewayval;

    private SharedPreferences.Editor editor1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_select_gateway);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        ActionBar actionBar = getSupportActionBar();
        if (null != actionBar) {
            actionBar.hide();
        }


        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        editor1 = settings.edit();
        //editor1.putString("posLat","xxx");
        //editor1.commit();





        final Resources resources = getResources();

        final Spinner gatewaySpinner = (Spinner) findViewById(R.id.gateway_spinner);
        ArrayAdapter<CharSequence> gatewayadapter = ArrayAdapter.createFromResource(this,
                R.array.settings_list_preference_network_title, android.R.layout.simple_spinner_item);
        gatewayadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        gatewaySpinner.setAdapter(gatewayadapter);
        gatewaySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String gatewayitem = (String) parent.getItemAtPosition(position);
                Log.d("JPTRbranditem", String.valueOf(position));
                Log.d("JPTRbranditem", gatewayitem);

                gatewayval = resources.getStringArray(R.array.settings_list_preference_network_values)[position];
                Log.d("JPTRbranditem", gatewayval);

                editor1.putString("gateway",gatewayval);
                //editor1.apply();

            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //TODO nothing
            }
        });






        final Spinner loadertypeSpinner = (Spinner) findViewById(R.id.loadertype_spinner);
        ArrayAdapter<CharSequence> loadertypeadapter = ArrayAdapter.createFromResource(this,
                R.array.settings_list_preference_loader_title, android.R.layout.simple_spinner_item);
        loadertypeadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        loadertypeSpinner.setAdapter(loadertypeadapter);
        loadertypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String loadertypeitem = (String) parent.getItemAtPosition(position);
                Log.d("JPTRbranditem", loadertypeitem);
                Log.d("JPTRbranditem", String.valueOf(position));


                loadertypeval = resources.getStringArray(R.array.settings_list_preference_loader_values)[position];
                Log.d("JPTRbranditem", loadertypeval);

                editor1.putString("gateway",loadertypeval);
                //editor1.apply();

            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //TODO nothing
            }
        });


        Button mupdatebutton = (Button) findViewById(R.id.update_gateway_btn);
        mupdatebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("splasher", "clicked gatewayval: " + gatewayval + " " + "loadertypeval: " + loadertypeval);
                editor1.apply();
                startActivity(new Intent(SplashGatewayActivity.this, SplashActivity.class));
            }
        });


    }


}
