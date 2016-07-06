package com.alexandar.android.findme;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by Alexandar on 3/31/2016.
 *
 * Klasa preuzeta sa http://codetheory.in/android-sms/  tj. kasniji deo za obradu ovog intenta u SmsService.java je preuzet odatle.
 */
public class StartupBinaryDataSMS extends BroadcastReceiver {

    public static final String NEW_DATA_SMS = "StartupBinaryDataSMS";
    public static final String DATA_SMS_RECEIVED_ACTION = "android.intent.action.DATA_SMS_RECEIVED";

//    private String TAG = StartupBinaryDataSMS.class.getSimpleName(); // nacin za dobijanje stringa naziva klase


    @Override
    public void onReceive(Context context, Intent intent) {  // TODO : vidi kroz debuger sta dolazi kao Context ovde. Citaj malo o Contextima uopste. razlika izmedju Context i Activity...
        String action = intent.getAction();

        Log.i(NEW_DATA_SMS, "Received broadcast intent" + action);

        if (DATA_SMS_RECEIVED_ACTION.equals(action)) {
            Intent i = SmsService.newIntent(context);
            i.putExtra(NEW_DATA_SMS, intent);
            context.startService(i);
        }
    }



}
