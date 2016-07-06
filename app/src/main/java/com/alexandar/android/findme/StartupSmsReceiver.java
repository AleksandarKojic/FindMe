package com.alexandar.android.findme;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

/**
 * Created by Alexandar on 3/12/2016.
 */

// TODO : ovo ti je standalone BroadCast receiver i sluzi da pokrene aplikaciju ako nije vec bila pokrenuta. U slucaju da jeste, najbolje je da registrujes i broadcast receiver u FindMeActivity? Ili ce ovaj standalone uvek raditi, cak i ako je vec pokrenuta aplikacija?

public class StartupSmsReceiver extends BroadcastReceiver{


    public static final String NEW_SMS = "StartupSmsReceiver";
    public static final String SMS_RECEIVED_ACTION = "android.provider.Telephony.SMS_RECEIVED";



    @Override
    public void onReceive(Context context, Intent intent) {  // TODO : vidi kroz debuger sta dolazi kao Context ovde. Citaj malo o Contextima uopste. razlika izmedju Context i Activity...
        String action = intent.getAction();

        // TODO : vidi vraca li ovo extra koji si ubacio pri slanju u FindMeFragment -> sendSMS()? Ako da, iskoristi to kao uslov da razlikujes da li je stigao obican SMS ili lokacioni...
        // TODO : iskoristis to kao uslov i ako nije lokacioni, onda stavi da se prekida dalje izvrsavanje ovde, nem potrebe da zove SmsService dalje.
        Bundle bundle = intent.getExtras();
        String extraSubject = intent.getStringExtra(Intent.EXTRA_SUBJECT);
        String proba = bundle.getString(Intent.EXTRA_SUBJECT);




        Log.i(NEW_SMS, "Received broadcast intent" + action);

        if (SMS_RECEIVED_ACTION.equals(action)) {
            Intent i = SmsService.newIntent(context);
            i.putExtra(NEW_SMS, intent);
            context.startService(i);
        }
    }
    
}
