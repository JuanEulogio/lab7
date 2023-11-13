package com.cs407.lab7;

import android. content.BroadcastReceiver;
import android. content. Context;
import android. content. Intent;
import android.os. Bundle;
import android.util.Log;
import android.widget. Toast;
import androidx.core.app. RemoteInput;


//How can your app be notified by the system when a
//reply action is taken? Broadcast message is the system-level mechanism of propagating
//cross-app and system-to-app events. For example, when the device starts charging, a
//broadcast message will be sent to apps that subscribes to the charging event. The case is
//similar to notification actions: you create a class that inherits from BroadcastReceiver,
//declare it in AndroidManifest.xml, and pass it to the notification action that we will
//create later
public class ReplyReceiver extends BroadcastReceiver{

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("ReplyReceiver", "HELOOO1");

        //get intent from showNotification
        int id = intent.getIntExtra("id", -1);


        Bundle remoteInput = RemoteInput.getResultsFromIntent(intent);
        Log.i("ReplyReceiver", "HELOOO2");


        ////This is what we output after the user replies with the notification Action
        if (remoteInput != null) {
            CharSequence charSequence = remoteInput .getCharSequence (NotificationHelper.TEXT_REPLY);
            if (charSequence == null) return;

            //NOTE: we get R.string.replied from our string file. Its our premade string
            Toast.makeText(context, context.getString(R.string.replied, charSequence.toString(), id), Toast. LENGTH_LONG) .show();
            Log.d("ReplyReceiver", String.valueOf(id));
            Log.i("ReplyReceiver", "HELOOO yesss");

            NotificationHelper.getInstance().showNotification(context, id);
        }
    }

}
