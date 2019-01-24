package com.inducesmile.retailer;


import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by SONU on 29/10/15.
 */
public class DownloadTask {

    private static final String TAG = "Download Task";
    private Context context;
    private Button buttonText;
    private String downloadUrl = "", downloadFileName = "", downloadMsg = "";

    public DownloadTask(Context context, String downloadUrl) {
        Button buttonText2 = null;
        this.context = context;
        this.buttonText = buttonText2;
        this.downloadUrl = downloadUrl;
        this.downloadMsg = downloadMsg;

                downloadFileName = downloadUrl.replace(Utils.mainUrl, "");//Create file name by picking download file name from URL
        downloadFileName = downloadFileName.replace(Utils.mainUrl2, "");//Create file name by picking download file name from URL
        Log.e(TAG, downloadFileName);

        //Start Downloading Task
        new DownloadingTask().execute();
    }

    @SuppressLint("StaticFieldLeak")
    private class DownloadingTask extends AsyncTask<Void, Void, Void> {

        File apkStorage = null;
        File outputFile = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //buttonText.setEnabled(false);
            //buttonText.setText(R.string.downloadStarted);//Set Button Text when download started
            Toast.makeText(context, R.string.downloadStarted, Toast.LENGTH_LONG).show();
        }

        @Override
        protected void onPostExecute(Void result) {
            try {
                if (outputFile != null) {
                    //buttonText.setEnabled(true);
                    //buttonText.setText(R.string.downloadCompleted);//If Download completed then change button text
                    Toast.makeText(context, R.string.downloadCompleted, Toast.LENGTH_LONG).show();
                } else {
                    //buttonText.setText(R.string.downloadFailed);//If download failed change button text
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            //buttonText.setEnabled(true);
                            //buttonText.setText(R.string.downloadAgain);//Change button text again after 3sec
                            Toast.makeText(context, R.string.downloadAgain, Toast.LENGTH_LONG).show();
                        }
                    }, 3000);

                    Log.e(TAG, "Download Failed");

                }
            } catch (Exception e) {
                e.printStackTrace();

                //Change button text if exception occurs
                //buttonText.setText(R.string.downloadFailed);
                Toast.makeText(context, R.string.downloadFailed, Toast.LENGTH_LONG).show();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //buttonText.setEnabled(true);
                        //buttonText.setText(R.string.downloadAgain);
                        Toast.makeText(context, R.string.downloadAgain, Toast.LENGTH_LONG).show();
                    }
                }, 3000);
                Log.e(TAG, "Download Failed with Exception - " + e.getLocalizedMessage());

            }

            super.onPostExecute(result);
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            try {
                URL url = new URL(downloadUrl);//Create Download URl
                HttpURLConnection c = (HttpURLConnection) url.openConnection();//Open Url Connection
                c.setRequestMethod("GET");//Set Request Method to "GET" since we are grtting data
                c.connect();//connect the URL Connection

                //If Connection response is not OK then show Logs
                if (c.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    Log.e(TAG, "Server returned HTTP " + c.getResponseCode()
                            + " " + c.getResponseMessage());
                }


                //Get File if SD card is present
                /*
                if (new CheckForSDCard().isSDCardPresent()) {

                    apkStorage = new File(
                            Environment.getExternalStorageDirectory() + "/"
                                    + Utils.downloadDirectory);
                } else {
                    Toast.makeText(context, "Oops!! There is no SD Card.", Toast.LENGTH_SHORT).show();
                }
                */


                String cachedir = context.getCacheDir().getPath() + File.separator + Utils.downloadDirectory;
                Log.d("cachedir", cachedir);
                apkStorage = new File(cachedir);

                //If File is not present create directory
                if (!apkStorage.exists()) {
                    apkStorage.mkdir();
                    Log.e(TAG, "Directory Created.");
                }

                outputFile = new File(apkStorage, downloadFileName);//Create Output file in Main File

                //Create New File if not present
                if (!outputFile.exists()) {
                    outputFile.createNewFile();
                    Log.e(TAG, "File Created");
                }

                FileOutputStream fos = new FileOutputStream(outputFile);//Get OutputStream for NewFile Location

                InputStream is = c.getInputStream();//Get InputStream for connection

                byte[] buffer = new byte[1024];//Set buffer type
                int len1 = 0;//init length
                while ((len1 = is.read(buffer)) != -1) {
                    fos.write(buffer, 0, len1);//Write new file
                }

                //Close all connection after doing task
                fos.close();
                is.close();

            } catch (Exception e) {

                //Read exception if something went wrong
                e.printStackTrace();
                outputFile = null;
                Log.e(TAG, "Download Error Exception " + e.getMessage());
            }

            return null;
        }
    }
}
