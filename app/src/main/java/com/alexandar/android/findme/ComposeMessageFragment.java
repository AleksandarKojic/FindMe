package com.alexandar.android.findme;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.telephony.SmsManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Alexandar on 4/18/2016.
 */
public class ComposeMessageFragment extends Fragment {

    private static final String ARG_LATITUDE_LONGITUDE = "latitude_longitude";

    private String [] latitudeLongitude;


    public static ComposeMessageFragment newInstance (String [] latitudeLongitude){
        Bundle args = new Bundle();
        args.putStringArray(ARG_LATITUDE_LONGITUDE, latitudeLongitude);
        ComposeMessageFragment fragment = new ComposeMessageFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        latitudeLongitude = getArguments().getStringArray(ARG_LATITUDE_LONGITUDE);
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_compose_message, container, false);

        return v;
    }



    // *** Slanje Binary (Data) SMS-a. Pogledaj ovaj link za detalje http://codetheory.in/android-sms/
    private void sendBinaryDataSMS() {

        String latitude = latitudeLongitude[0];
        String longitude = latitudeLongitude[1];

        // Get the default instance of SmsManager
        SmsManager smsManager = SmsManager.getDefault();
        // br. galaxy S2 mob-a
        String phoneNumber = "+381643165886"; // TODO: Ovo sredi tako da otvara kontakte i da mozes da biras medju njima kome sve saljes.VIDI U KNJIZI OD 283.str. Takodje onda moras ubaciti i mesto gde ces da kucas dodatni text poruke, tacnije smsBody koji je zakucan ovde ispod. Najbolje da ti se otvori kao pop-up prozorcic kada kliknes na "Send Location" pa da tu mozes da kucas text poruke.
        byte[] smsBody = "Let me know if you get this SMS".getBytes();
        short port = 6734;

        // Send a text based SMS
        smsManager.sendDataMessage(phoneNumber, null, port, smsBody, null, null);






        // TODO : Dodaj onaj deo gde prima da li je delivered ili ne, da meri neko vreme recimo pa ako za to vreme ne udje u taj broadcast receiver za osluskivanje, onda da salje pravi sms.
        // TODO : Takodje smisli kako da proveri da li vec postoji instalirana app i ako ne, da ima link da je skine. Recimo, ako nije primljen DataSMS, ond asalje obican sa linkom. PROVERI DA LI SE DataSMS prima i ako je iskljucen net!?
    }



}
