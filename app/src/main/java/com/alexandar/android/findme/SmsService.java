package com.alexandar.android.findme;

import android.app.IntentService;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.NotificationCompat;
import android.telephony.SmsMessage;
import android.util.Log;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Alexandar on 3/13/2016.
 */


// TODO : Promeni ovo tako da ubacuje neki objekat u sms ili neku skrivenu identifikaciju da je za moju app. Idealno je da onda poruka i ne ide u klasicnu sms app. I da ima link za skidanje moje app ako je nema instaliranu vec...


public class SmsService extends IntentService {
    private static final String TAG = "SmsService";
    public static final String SMS_RECEIVED_ACTION = "android.provider.Telephony.SMS_RECEIVED";

    // TODO : vidi trebaju li ti ove promenljive uopste u metodi processSms
    public static final String MESSAGE_SERVICE_NUMBER = "+381655255220";
    private static final String MESSAGE_SERVICE_PREFIX = "MYSERVICE" ;

    public SmsService (){
        super(TAG); // zasto sam ovo prosledio?
    }

    public static Intent newIntent(Context context){
        return new Intent(context, SmsService.class);
    }


    @Override
    protected void onHandleIntent(Intent intent) {

        Intent startupSMSIntent = intent.getParcelableExtra(StartupSmsReceiver.NEW_SMS);
        Intent startupBinaryDataSMSIntent = intent.getParcelableExtra(StartupBinaryDataSMS.NEW_DATA_SMS);

        if(startupSMSIntent != null){
            processSms(startupSMSIntent);
        } else if (startupBinaryDataSMSIntent != null){
            processBinaryDataSms(startupBinaryDataSMSIntent);
        }

    }

    // *** NAPOMENA: nacin vadjenja SmsMessage-a iz Intenta sam preuzeo sa sajta http://codetheory.in/android-sms/ . Ima i drugi nacin u knjizi : Android Programming - Pushing the Limits pa uporedi nekad...
    private void processSms ( Intent intent ) {

        Bundle bundle = intent.getExtras();
        SmsMessage[] msgs = null;

        if (bundle != null) {
            // Retrieve the SMS Messages received
            Object[] messages = (Object[]) bundle.get("pdus");
            msgs = new SmsMessage[messages.length];

            // For every SMS message received
            for (int i = 0; i < msgs.length; i++) {
                // Convert Object array
                msgs[i] = SmsMessage.createFromPdu((byte[]) messages[i]);
            }
        }

        SmsMessage smsMessage = msgs[0]; // Ovde pretpostavljam da je stigao samo jedan sms i uzimam njega. Vodi racuna pri slanju da ne bude vise od 1 poruke.
        // TODO : vidi da sta se dobija za ove 2 vrednosti ispod i moze li da posluzi za nesto...
//        String from = smsMessage.getOriginatingAddress();
//        byte[] userData = smsMessage.getUserData();  // Ovo se izgleda koristi za DataSMS?

        String messageBody = smsMessage.getMessageBody();

        // *** Ovim postizemo da se nitifikacija postavlja samo ako je stigla poruka od moje aplikacije, ne obican sms. TODO : Smisli elegantniji nacin za ovo!
        if (messageBody.contains("FindMe Location")){

//        String [] messageParts = messageBody.split("-");
//        String friendsMessage = messageParts[0];
//        String latitude ="";
//        String longitude = "";
//        String messageText = "";
//        Pattern pattern = Pattern.compile("\\w+([0-9]+)\\w+([0-9]+)");
//        Matcher matcher = pattern.matcher(messageBody);
//        for(int i = 0 ; i < matcher.groupCount(); i++) {
//            matcher.find();
//
//        }



            showBackgroundNotification(messageBody);
        } else return;




    }

    private void processBinaryDataSms ( Intent intent ) {
        // Get the data (SMS data) bound to intent
        Bundle bundle = intent.getExtras();

        SmsMessage[] msgs = null;

        if (bundle != null){
            // Retrieve the Binary SMS data
            Object[] messages = (Object[]) bundle.get("pdus");
            msgs = new SmsMessage[messages.length];

            // For every SMS message received (although multipart is not supported with binary)
            for (int i=0; i<msgs.length; i++) {

                msgs[i] = SmsMessage.createFromPdu((byte[]) messages[i]);

                // Generally you can do away with this for loop
                // You'll just need the next for loop
//                for (int index=0; index < data.length; index++) {
//                    str += Byte.toString(data[index]);
//                }
            }

            SmsMessage smsMessage = msgs[0]; // Ovde pretpostavljam da je stigao samo jedan sms i uzimam njega. Vodi racuna pri slanju da ne bude vise od 1 poruke.
            // Return the User Data section minus the
            // User Data Header (UDH) (if there is any UDH at all)
            byte[] data = smsMessage.getUserData();
            String messageBody = "";
            for (int index=0; index < data.length; index++) {
                messageBody += Character.toString((char) data[index]);
            }

            // Dump the entire message
            // Toast.makeText(context, str, Toast.LENGTH_LONG).show();
            Log.d(TAG, messageBody);
        }



    }


    private void showBackgroundNotification (String messageBody){
        Resources resources = getResources();
        Intent i = FindMeActivity.newIntent(this , messageBody);
        PendingIntent pi = PendingIntent.getActivity(this, 0, i, 0);
        Notification notification = new NotificationCompat.Builder(this)
                .setTicker(resources.getString(R.string.new_sms))
                .setSmallIcon(android.R.drawable.ic_menu_report_image)
                .setContentTitle(resources.getString(R.string.new_sms))
                .setContentText(resources.getString(R.string.new_sms_text))
                .setContentIntent(pi)
                .setAutoCancel(true)
                .build();
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(0, notification);

    }




}
