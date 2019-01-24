package com.inducesmile.retailer;

import android.app.LauncherActivity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


public class SearchFilterMainActivity extends AppCompatActivity implements SearchFilterContactsAdapter.ContactsAdapterListener {
    private static final String TAG = SearchFilterMainActivity.class.getSimpleName();
    private RecyclerView recyclerView;
    private List<SearchFilterContact> productList;
    private SearchFilterContactsAdapter mAdapter;
    private SearchView searchView;


    private List<String> brands = new ArrayList<String>();


    private String jsonfile_local = null;

    // url to fetch contacts json
    //private static final String URL = "https://api.androidhive.info/json/contacts.json";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.searchfilter_activity_main);


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // toolbar fancy stuff
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
            Objects.requireNonNull(getSupportActionBar()).setTitle(R.string.productlistlabel);
        }

        //internal cache dir
        String cachedir = getCacheDir().getPath() + File.separator + "brand/";
        Log.d("JPTRX", "cache: " + cachedir);
        String folder = cachedir;
        //Create androiddeft folder if it does not exist
        File directory = new File(folder);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        String fileName_local = "product_list2.json";
        String filePath_local = folder + fileName_local;
        /////////////////LOCAL JSON/////////////////
        Log.e("filePath_local", filePath_local);

        try {
            jsonfile_local = getStringFromFile(filePath_local);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.d("jsonfile_local", jsonfile_local);




        /****
        JSONObject forecast = null;
        try {
            forecast = new JSONObject(jsonfile_local);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        String versionControl = null;
        try {
            assert forecast != null;
            versionControl = forecast.getString("version_control");
        } catch (JSONException e) {
            Log.d("JSONException", jsonfile_local);
            e.printStackTrace();
        }
        JSONObject jsonObjproductList;
        jsonObjproductList = null;
        try {
            jsonObjproductList = forecast.getJSONObject("product_list");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //parse string, long and double objects within jsonObjCurrently accordingly

        Log.d("versionControl", versionControl);


        assert jsonObjproductList != null;
        for(int i = 0; i<jsonObjproductList.length(); i++){
            String brandName = null;
            try {
                brandName = jsonObjproductList.names().getString(i);
                JSONObject productNames = jsonObjproductList.getJSONObject(brandName);

                for(int j = 0; j<productNames.length(); j++) {
                    String productName = productNames.names().getString(j);
                    Log.d("productName", productName);
                    brands.add(productName);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }





        JSONObject brandjsonObject = new JSONObject();
        for (int i=0; i < brands.size(); i++) {

            JSONArray jsonArray = new JSONArray();

            jsonArray.put( "a" );
            jsonArray.put( "b" );
            jsonArray.put( "c" );

            try {
                brandjsonObject.put(brands.get(i), jsonArray);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }




        Log.d("brandjsonObject", String.valueOf(brandjsonObject));



        ****/







        recyclerView = findViewById(R.id.recycler_view);
        productList = new ArrayList<>();

        mAdapter = new SearchFilterContactsAdapter(this, productList, this);

        // white background notification bar
        whiteNotificationBar(recyclerView);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new SearchFilterMyDividerItemDecoration(this, DividerItemDecoration.VERTICAL, 36));
        recyclerView.setAdapter(mAdapter);

        fetchContacts();
    }






    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        Log.d("JPTRback", "back pressed");
        startActivity(new Intent(SearchFilterMainActivity.this, MainMenuActivity.class));
        overridePendingTransition(R.anim.slideinright, R.anim.slideinleft);
        return true;
    }


    /**
     * fetches json by making http calls
     */
    private void fetchContacts() {




        /***********************
        JsonArrayRequest request = new JsonArrayRequest(URL,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        if (response == null) {
                            Toast.makeText(getApplicationContext(), "Couldn't fetch the contacts! Pleas try again.", Toast.LENGTH_LONG).show();
                            return;
                        }

                        Log.d("brandjsonArray", "" + jsonfile_local);

                        List<SearchFilterContact> items = new Gson().fromJson(jsonfile_local, new TypeToken<List<SearchFilterContact>>() {
                        }.getType());

                        // adding contacts to contacts list
                        productList.clear();
                        productList.addAll(items);

                        //Log.d("request2", String.valueOf(productList.get));

                        // refreshing recycler view
                        mAdapter.notifyDataSetChanged();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // error in getting json
                Log.e(TAG, "Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(), "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        *************************/



        List<SearchFilterContact> items = new Gson().fromJson(jsonfile_local, new TypeToken<List<SearchFilterContact>>() {
        }.getType());

        // adding contacts to contacts list
        productList.clear();
        productList.addAll(items);

        //Log.d("request2", String.valueOf(productList.get));

        // refreshing recycler view
        mAdapter.notifyDataSetChanged();


        //Log.d("request3", String.valueOf(request));


        /**************
        try {
            SearchFilterMyApplication.getInstance().addToRequestQueue(request);
        }catch (Exception e)
        {
            Log.e("SearchErr", "Error: " + e.getMessage());
        }**************/

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.action_search)
                .getActionView();
        searchView.setSearchableInfo(searchManager
                .getSearchableInfo(getComponentName()));
        searchView.setMaxWidth(Integer.MAX_VALUE);

        // listening to search query text change
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // filter recycler view when query submitted
                mAdapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                // filter recycler view when text is changed
                mAdapter.getFilter().filter(query);
                return false;
            }
        });
        return true;
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (item.getItemId()) {
            case R.id.action_settings:
                startActivity(new Intent(SearchFilterMainActivity.this, PreferenceActivity.class));
                return true;
            case R.id.action_search:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }



    @Override
    public void onBackPressed() {
        // close search view on back button pressed
        if (!searchView.isIconified()) {
            searchView.setIconified(true);
            return;
        }
        super.onBackPressed();
    }

    private void whiteNotificationBar(View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int flags = view.getSystemUiVisibility();
            flags |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            view.setSystemUiVisibility(flags);
            getWindow().setStatusBarColor(Color.WHITE);
        }
    }

    @Override
    public void onContactSelected(SearchFilterContact contact) {

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        // then you use
        String loadertype = prefs.getString("loadertype", "retailer");

        assert loadertype != null;
        if(loadertype.equals("dealer")) {
            //TODO do nothing
        }
        else {
            /////EXECUTE
            //Toast.makeText(getApplicationContext(), "Selected: " + contact.getProdBrand() + ", " + contact.getProdName() + ", " + contact.getProdAmount() + ", " + contact.getProdCode() + ", " + contact.getProdInfo() + ", " + contact.getProdImage(), Toast.LENGTH_LONG).show();
            Intent searchToLoadIntent = new Intent(SearchFilterMainActivity.this, TransferLoadActivity.class);
            searchToLoadIntent.putExtra("activityName", "SearchFilterMain");
            searchToLoadIntent.putExtra("search_brandspinner", contact.getProdBrand());
            searchToLoadIntent.putExtra("search_productname", contact.getProdName());
            searchToLoadIntent.putExtra("search_productamount", contact.getProdAmount());
            searchToLoadIntent.putExtra("search_productcode", contact.getProdCode());
            searchToLoadIntent.putExtra("search_productedittext", contact.getProdName());
            searchToLoadIntent.putExtra("search_proddescription", contact.getProdInfo());
            searchToLoadIntent.putExtra("search_brand_image", contact.getProdImage());
            startActivity(searchToLoadIntent);
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

}
