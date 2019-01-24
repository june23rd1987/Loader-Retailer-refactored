package com.inducesmile.retailer;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

@SuppressLint("Registered")
public class ProductListActivity extends AppCompatActivity {

    //declare globally, this can be any int
    public final int PICK_CONTACT = 2015;


    //spinner
    private static final String[] paths = {"item 1", "item 2", "item 3"};

    //@SuppressLint("WrongViewCast")
    //final EditText et = (EditText) findViewById(R.id.retailernum);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transferload);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);



        Button mbackbutton2 = (Button) findViewById(R.id.backbutton2);
        mbackbutton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProductListActivity.this, MainMenuActivity.class));
            }
        });
        

    }





}
