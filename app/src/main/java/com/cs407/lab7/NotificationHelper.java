package com.cs407.lab7;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.app.RemoteInput;

import java.util.ArrayList;

public class NotificationHelper {

    //The constructor is set as private, meaning that an instante of this class cannot be created
    //anywhere outside of it. The static variable INSTANCE stores the only instance of this class.

    private static final NotificationHelper INSTANCE = new NotificationHelper ();


    //NOTE: The constructor is set as private, meaning that an instante of this class cannot be created
    //anywhere outside of it(our package). This is a security reason
    private NotificationHelper() {}


    //The getter getInstance returns a reference of the only instance
    public static NotificationHelper getInstance() {
        return INSTANCE;
    }



    //Note: static final. It will be the name our channel in this app notification
    public static final String CHANNEL_ID = "channel_chat";

    //used as the ID for the “Reply” textbox. We need this so we know were our reply reciever should
    //connect with in our ReplyReceriver
    public static final String TEXT_REPLY = "text_reply";




    public void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            //Note: CharSequence
            CharSequence name = context.getString(R.string.channel_name);

            String description = context.getString(R.string.channel_description);

            int importance = NotificationManager.IMPORTANCE_HIGH;

            //Note: make the channel here with "NotificationChannel"
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            //the new NotificationChannel object is registered with the Android System.
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }




    //NOTE: This is where we will store our list of notifications(?). ID= its index
    final ArrayList<NotificationItem> notificationItems = new ArrayList<>();

    public void appendNotificationItem(String sender, String message) {
        //make the notification object
        NotificationItem item = new NotificationItem (
                sender,
                message,
                notificationItems.size ()
        );
        //add new notification to list
        notificationItems.add (item);
    }


    public void showNotification(Context context, int id) {
        if (ActivityCompat.checkSelfPermission (context, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        NotificationItem item;
        if (id == -1) {
            item = notificationItems.get (notificationItems.size () - 1);
        } else {
            item = notificationItems.get(id);
        }


        //The following is to make a reply Action in our notifications
        //(note, im pretty sure this only works in more modern phones)

        //1) We first create a RemoteInput object that makes the "TEXT_REPLY" reply textbox in the
        //notification
        RemoteInput remoteInput = new RemoteInput.Builder(TEXT_REPLY)
                .setLabel (context.getString (R.string.reply))
                .build();

        //2) Then, we create the Intent that will be passed to the broadcast receiver. The
        //intent contains the ID of the notification.
        Intent replyIntent = new Intent(context, ReplyReceiver.class);
        //we send the intent input to ReplyReceiver here
        replyIntent.putExtra("id", item.getId());

        //3) The next step is creating a PendingIntent for the broadcast that will be sent
        //to our application after the user has provided the reply through the notification.
        //It contains a reference to the intent.
        PendingIntent replyPendingIntent =
                PendingIntent. getBroadcast(context,
                        item.getId(),
                        replyIntent,
                        PendingIntent .FLAG_MUTABLE | PendingIntent .FLAG_UPDATE_CURRENT);

        //4) we create the action that includes the RemoteInput and the PendingIntent,
        //and add the action to the notification.
        NotificationCompat.Action action =
                new NotificationCompat.Action. Builder (R.drawable.ic_reply_icon,
                        context.getString (R.string.reply), replyPendingIntent)
                        .addRemoteInput(remoteInput)
                        .build();


        //This is where actually compile and show the notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setVisibility(NotificationCompat.VISIBILITY_PRIVATE)
                .setContentTitle (item.getSender())
                .setContentText (item.getMessage())
                //NOTE: we also add our reply ACTION we want to do
                .addAction(action)
                .setPriority (NotificationCompat.PRIORITY_DEFAULT);

        //sends out the notification that we’ve just built
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from (context);
        notificationManager.notify(item.getId(), builder.build());
    }



}
