package com.cs407.lab7;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class MainActivity extends AppCompatActivity {
    //Before sendint the notification, we should first request for the
    //permission of posting notifications, and then invoke showNotification() to send it.
    //In lab 4, you have already learned how to use ActivityCompat.requestPermissions
    //to request for permissions. Here, we use a different way which is introduced in higher
    //versions of Android and is more convenient. With this way, there’s no need to define
    //request code

    //we first declared the private field requestPermissionLauncher2 that acts as the
    //callback when permission is either granted or refused. We send a toast if the permission is
    //refused.
    private final ActivityResultLauncher<String> requestPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted ->{
            if(!isGranted) {
                //Note: we get the file string "please allow notification"
                Toast.makeText(this, R.string.please_allow_notification, Toast.LENGTH_LONG).show();
            }
    });

    //in requestPermission, we first check for Android version, since you don’t
    //need permission to send notifications below Android 13. Then, we check if we’ve already
    //been granted the permission on L.27, and request it with requestPermissionLauncher
    //when necessary
    private void requestPermission() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            //Notification Permission not required till Android 13 (Tiramisu)
            return;
        }

        if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.POST_NOTIFICATIONS) !=
                PackageManager.PERMISSION_GRANTED) {
            // Permission not granted
            requestPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button buttonSend = findViewById(R.id.button);
        EditText editTextSender = findViewById(R.id.editTextSender);
        EditText editTextMessage = findViewById(R.id.editTextMessage);

        //Note: on start we request here
        requestPermission();

        //make our channel
        NotificationHelper.getInstance().createNotificationChannel(getApplicationContext());

        buttonSend.setOnClickListener (view -> {
            //we get the text info and set via our setNotificaitonContent method
            NotificationHelper.getInstance().appendNotificationItem(
                    editTextSender .getText (). toString(), editTextMessage. getText () . toString ()
            );
            //we show the notification
            // id= -1 because this is a new notification in our list
            NotificationHelper.getInstance().showNotification(getApplicationContext(), -1);
        });
    }
}