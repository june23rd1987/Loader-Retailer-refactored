package com.inducesmile.retailer.helpers;

import android.os.Environment;

/**
 * Created by Abhi on 11 Mar 2018 011.
 */

public class CheckForSDCard {
    //Method to Check If SD Card is mounted or not
    public static boolean isSDCardPresent() {
        return Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED);
    }
}



