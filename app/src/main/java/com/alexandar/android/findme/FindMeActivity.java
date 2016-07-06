package com.alexandar.android.findme;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;


public class FindMeActivity extends SingleFragmentActivity {

    private static final int REQUEST_ERROR = 0;
    private static final String SMS = "newSMSArived";

    private String smsMessage = "";

    public static Intent newIntent (Context context, String smsMessage) {
        Intent intent = new Intent (context, FindMeActivity.class);
        intent.putExtra(SMS, smsMessage);
        return intent;
    }

    @Override  // TODO  : BITNO! Proveri mozes li da overrideujes onCreate() posto je to vec uradjeno u SingleFragmentActivity abstraktnoj klasi koju ovde extendujem???
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getIntent() != null) {
            smsMessage = (String) getIntent().getSerializableExtra(SMS); // TODO : Jako bitno, nacin izvlacenja primljenog Intent-a koji sam video u CriminalIntent-u u CrimePagerActivity klasi. Proveri da li je bolje na ovaj nacin ili pomocu overrideovanja metode onNewIntent() koju imas ispod. Mozda je ona bolja u slucaju da stigne jos sms-ova?
        }


    }

    @Override
    protected Fragment createFragment() {
        return FindMeFragment.newInstance();  // TODO : Napravi da ako smsMessage != "" , onda instancira na nacin kao u CriminalIntentu za CrimeFragment i tako prosledi sms sa lokacijom u fragment. Ako je smsMessage ="", onda je activity pokrenuta pokretanjem aplikacije a ne time sto je nesto stiglo s polja i onda instancira Fragment kao sto i sada radi...
    }

    @Override
    protected void onResume() {
        super.onResume();
        int errorCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (errorCode != ConnectionResult.SUCCESS) {
            Dialog errorDialog = GooglePlayServicesUtil
                    .getErrorDialog(errorCode, this, REQUEST_ERROR,
                            new DialogInterface.OnCancelListener() {
                                @Override
                                public void onCancel(DialogInterface dialog) {
                                    // Leave if services are unavailable.
                                    finish();
                                }
                            });
            errorDialog.show();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        //handle your intent here.Note this will be called even when activity first created.so becareful to handle intents correctly.

        // TODO : Vidi kako da splitujes message ovde  na Latitude Longitude i sam tekst poruke koji si otkucao. npr "Vidimo se ovde u 19h, ponesite pivo!"

        // TODO : ovde dodaj da snima dobijenu lokaciju u SharedPreferences , pa prepravi da ucitava bas tu novu lokaciju u FindMeFragmentu-u kad ucitava mapu...
    }





}
