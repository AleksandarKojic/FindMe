package com.alexandar.android.findme;

import android.content.Context;
import android.location.Location;
import android.preference.PreferenceManager;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Alexandar on 2/16/2016.
 */
public class SharedPreferences {
    private static final String LATITUDE = "latitude";
    private static final String LONGITUDE = "longitude";
    private static final String SMS_RECEIVED_LATITUDE = "sms_received_latitude";
    private static final String SMS_RECEIVED_LONGITUDE = "sms_received_longitude";

    public static void setStoredLocation (Context context, Location location){
        String latitude = Double.toString(location.getLatitude());
        String longitude = Double.toString(location.getLongitude());
        PreferenceManager.getDefaultSharedPreferences(context).edit().putString(LATITUDE,latitude).putString(LONGITUDE, longitude).apply();
    }

    public static Location getStoredLocation (Context context){
        double latitude = Double.parseDouble( PreferenceManager.getDefaultSharedPreferences(context).getString(LATITUDE , "firstRun")); // String "firstRun" vraca ako SharedPreferences ne sadrzi nista u mapi.
        double longitude = Double.parseDouble(PreferenceManager.getDefaultSharedPreferences(context).getString(LONGITUDE, "firstRun"));
        Location lastLocation = new Location("");
        lastLocation.setLatitude(latitude);
        lastLocation.setLongitude(longitude);
        return lastLocation;
    }

    public static String checkFirstRun (Context context) {
        String check = PreferenceManager.getDefaultSharedPreferences(context).getString(LATITUDE, "firstRun");
        return check;
    }



    public static void setSmsReceivedLocation (Context context, Location location){
        String latitude = Double.toString(location.getLatitude());
        String longitude = Double.toString(location.getLongitude());
        PreferenceManager.getDefaultSharedPreferences(context).edit().putString(SMS_RECEIVED_LATITUDE,latitude).putString(SMS_RECEIVED_LONGITUDE, longitude).apply();
    }

    public static Location getSmsReceivedLocatio (Context context){
        double latitude = Double.parseDouble(PreferenceManager.getDefaultSharedPreferences(context).getString(SMS_RECEIVED_LATITUDE, "noReceivedSms"));
        double longitude = Double.parseDouble(PreferenceManager.getDefaultSharedPreferences(context).getString(SMS_RECEIVED_LONGITUDE, "noReceivedSms"));
        Location lastLocation = new Location("");
        lastLocation.setLatitude(latitude);
        lastLocation.setLongitude(longitude);
        return lastLocation;
    }

    //Metod koji proverava da li postoji primljeni SMS sa lokacijom. Koristi se u FindMeActivity kada se pokrene preko notifikacije za proveru da li je primljena SMS lokacija. Razmisli treba li to uopste...
    public static String checkReceivedSMS (Context context) {
        String check = PreferenceManager.getDefaultSharedPreferences(context).getString(LATITUDE, "firstRun");
        return check;
    }
}





//  TODO :  Sada iskoristi ove snimljene lokacije. Recimo da je snima u onStop a cita u onStart? Ili vidi koji drugi lifecycle method odgovara...