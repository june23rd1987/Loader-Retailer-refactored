package com.inducesmile.retailer.adapters;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.util.Log;

public final class TelephonyInfo {

    private static TelephonyInfo telephonyInfo;
    private String imeiSIM1;
    private String imeiSIM2;
    private String pnumSIM1;
    private String pnumSIM2;
    private String netnameSIM1;
    private String netnameSIM2;
    private Integer simidSIM1;
    private Integer simidSIM2;
    private boolean isSIM1Ready;
    private boolean isSIM2Ready;

    public String getImsiSIM1() {
        return imeiSIM1;
    }

    /*public static void setImsiSIM1(String imeiSIM1) {
        TelephonyInfo.imeiSIM1 = imeiSIM1;
    }*/

    public String getImsiSIM2() {
        return imeiSIM2;
    }


    public String getpnumSIM1() {
        return pnumSIM1;
    }

    public String getpnumSIM2() {
        return pnumSIM2;
    }

    public String getnetnameSIM1() {
        return netnameSIM1;
    }

    public String getnetnameSIM2() {
        return netnameSIM2;
    }

    public Integer getsimidSIM1() {
        return simidSIM1;
    }

    public Integer getsimidSIM2() {
        return simidSIM2;
    }


    /*public static void setImsiSIM2(String imeiSIM2) {
        TelephonyInfo.imeiSIM2 = imeiSIM2;
    }*/

    public boolean isSIM1Ready() {
        return isSIM1Ready;
    }

    /*public static void setSIM1Ready(boolean isSIM1Ready) {
        TelephonyInfo.isSIM1Ready = isSIM1Ready;
    }*/

    public boolean isSIM2Ready() {
        return isSIM2Ready;
    }

    /*public static void setSIM2Ready(boolean isSIM2Ready) {
        TelephonyInfo.isSIM2Ready = isSIM2Ready;
    }*/

    public boolean isDualSIM() {
        return imeiSIM2 != null;
    }

    private TelephonyInfo() {
    }

    @SuppressLint({"HardwareIds", "NewApi"})
    public static TelephonyInfo getInstance(Context context) {

        if (telephonyInfo == null) {

            telephonyInfo = new TelephonyInfo();

            TelephonyManager telephonyManager = ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE));


            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                //return TODO;
            }


            telephonyInfo.imeiSIM1 = telephonyManager.getDeviceId();;
            telephonyInfo.imeiSIM2 = null;

            try {
                telephonyInfo.imeiSIM1 = getDeviceIdBySlot(context, "getDeviceIdGemini", 0);
                telephonyInfo.imeiSIM2 = getDeviceIdBySlot(context, "getDeviceIdGemini", 1);
            } catch (GeminiMethodNotFoundException e) {
                e.printStackTrace();

                try {
                    telephonyInfo.imeiSIM1 = getDeviceIdBySlot(context, "getDeviceId", 0);
                    telephonyInfo.imeiSIM2 = getDeviceIdBySlot(context, "getDeviceId", 1);
                } catch (GeminiMethodNotFoundException e1) {
                    //Call here for next manufacturer's predicted method name if you wish
                    e1.printStackTrace();
                }
            }

            telephonyInfo.isSIM1Ready = telephonyManager.getSimState() == TelephonyManager.SIM_STATE_READY;
            telephonyInfo.isSIM2Ready = false;

            try {
                telephonyInfo.isSIM1Ready = getSIMStateBySlot(context, "getSimStateGemini", 0);
                telephonyInfo.isSIM2Ready = getSIMStateBySlot(context, "getSimStateGemini", 1);
            } catch (GeminiMethodNotFoundException e) {

                e.printStackTrace();

                try {
                    telephonyInfo.isSIM1Ready = getSIMStateBySlot(context, "getSimState", 0);
                    telephonyInfo.isSIM2Ready = getSIMStateBySlot(context, "getSimState", 1);
                } catch (GeminiMethodNotFoundException e1) {
                    //Call here for next manufacturer's predicted method name if you wish
                    e1.printStackTrace();
                }
            }



            //BYPASS FRO THE CRASH ON API BELOW API 22
            if(telephonyInfo.isDualSIM()) {

                final SubscriptionManager subscriptionManager = SubscriptionManager.from(context);

                ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE);

                @SuppressLint({"NewApi", "LocalSuppress"}) final List<SubscriptionInfo> activeSubscriptionInfoList = subscriptionManager.getActiveSubscriptionInfoList();
                int simCount = activeSubscriptionInfoList.size();
                Log.d("DUALSIM: ", "simCount:" + simCount);

                ArrayList<String> pnumbers = new ArrayList<>();
                ArrayList<String> netname = new ArrayList<>();
                ArrayList<Integer> simid = new ArrayList<>();
                for (SubscriptionInfo subscriptionInfo : activeSubscriptionInfoList) {
                    pnumbers.add(subscriptionInfo.getNumber());
                    netname.add((String) subscriptionInfo.getDisplayName());
                    simid.add(subscriptionInfo.getSimSlotIndex());
                    Log.d("DUALSIMX: ", "iccId :" + subscriptionInfo.getNumber() + " , name : " + subscriptionInfo.getDisplayName() + " , SIMID : " + subscriptionInfo.getSimSlotIndex());
                }

                //Log.d("DUALSIM: ", pnumbers.get(1));

                telephonyInfo.pnumSIM1 = pnumbers.get(0);
                telephonyInfo.pnumSIM2 = pnumbers.get(1);

                telephonyInfo.netnameSIM1 = netname.get(0);
                telephonyInfo.netnameSIM2 = netname.get(1);

                telephonyInfo.simidSIM1 = simid.get(0);
                telephonyInfo.simidSIM2 = simid.get(1);

            /*
            try {
                telephonyInfo.pnumSIM1 = getPhoneNumBySlot(context, "getLine1NumberGemini", 0);
                telephonyInfo.pnumSIM2 = getPhoneNumBySlot(context, "getLine1NumberGemini", 1);
            } catch (GeminiMethodNotFoundException e) {
                e.printStackTrace();

                try {
                    telephonyInfo.pnumSIM1 = getPhoneNumBySlot(context, "getLine1Number", 0);
                    telephonyInfo.pnumSIM2 = getPhoneNumBySlot(context, "getLine1Number", 1);
                } catch (GeminiMethodNotFoundException e1) {
                    //Call here for next manufacturer's predicted method name if you wish
                    e1.printStackTrace();
                }
            }
            */

            }

        }

        return telephonyInfo;
    }

    private static String getDeviceIdBySlot(Context context, String predictedMethodName, int slotID) throws GeminiMethodNotFoundException {

        String imei = null;

        TelephonyManager telephony = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

        try{

            Class<?> telephonyClass = Class.forName(telephony.getClass().getName());

            Class<?>[] parameter = new Class[1];
            parameter[0] = int.class;
            Method getSimID = telephonyClass.getMethod(predictedMethodName, parameter);

            Object[] obParameter = new Object[1];
            obParameter[0] = slotID;
            Object ob_phone = getSimID.invoke(telephony, obParameter);

            if(ob_phone != null){
                imei = ob_phone.toString();

            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new GeminiMethodNotFoundException(predictedMethodName);
        }

        return imei;
    }

    private static String getPhoneNumBySlot(Context context, String predictedMethodName, int slotID) throws GeminiMethodNotFoundException {

        String pnum = null;

        TelephonyManager telephony = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

        try{

            Class<?> telephonyClass = Class.forName(telephony.getClass().getName());

            Class<?>[] parameter = new Class[1];
            parameter[0] = int.class;
            Method getSimID = telephonyClass.getMethod(predictedMethodName, parameter);

            Object[] obParameter = new Object[1];
            obParameter[0] = slotID;
            Object ob_phone = getSimID.invoke(telephony, obParameter);

            if(ob_phone != null){
                pnum = ob_phone.toString();

            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new GeminiMethodNotFoundException(predictedMethodName);
        }

        return pnum;
    }

    private static  boolean getSIMStateBySlot(Context context, String predictedMethodName, int slotID) throws GeminiMethodNotFoundException {

        boolean isReady = false;

        TelephonyManager telephony = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

        try{

            Class<?> telephonyClass = Class.forName(telephony.getClass().getName());

            Class<?>[] parameter = new Class[1];
            parameter[0] = int.class;
            Method getSimStateGemini = telephonyClass.getMethod(predictedMethodName, parameter);

            Object[] obParameter = new Object[1];
            obParameter[0] = slotID;
            Object ob_phone = getSimStateGemini.invoke(telephony, obParameter);

            if(ob_phone != null){
                int simState = Integer.parseInt(ob_phone.toString());
                if(simState == TelephonyManager.SIM_STATE_READY){
                    isReady = true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new GeminiMethodNotFoundException(predictedMethodName);
        }

        return isReady;
    }




    private static class GeminiMethodNotFoundException extends Exception {

        private static final long serialVersionUID = -996812356902545308L;

        public GeminiMethodNotFoundException(String info) {
            super(info);
        }
    }
}