package com.perchtech.humraz.ambulance;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.CountDownTimer;
import android.support.v4.app.NotificationCompat;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

/**
 * Created by humra on 2/6/2017.
 */
public class MyTestService extends IntentService {
    public MyTestService() {
        super("MyTestService");
    }
    String a;
    GPSTracker gps;
    @Override
    protected void onHandleIntent(Intent intent) {
        // Do the task here
        Firebase.setAndroidContext(this);
        String url="https://adaa-45b17.firebaseio.com/PreviousLocation/";
        final Firebase ref = new Firebase(url);
      

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot usersSnapshot) {

                for (DataSnapshot userSnapshot : usersSnapshot.getChildren()) {
                    location user = userSnapshot.getValue(location.class);

                    a = user.getYes();


                }
                if (a.equalsIgnoreCase("t")) {
                    sendNotification("Help!");
                    post();
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                System.out.println("The read failed: " + firebaseError.getMessage());
            }
        });
        Log.i("MyTestService", "Service running");



    }

    public void post()

    {  String lat="";
        String lng="";
        gps = new GPSTracker(MyTestService.this);

        // check if GPS enabled
        if (gps.canGetLocation()) {
             double latitude = gps.getLatitude();
                    double longitude = gps.getLongitude();

                        lat = Double.toString(10.01);
                        lng = Double.toString(76.5);
                     } else {

                        gps.showSettingsAlert();
        }
        Firebase.setAndroidContext(this);
        location l= new location();

        l.setLat(lat);
        l.setLng(lng);
        Firebase ref = new Firebase("https://adaa-45b17.firebaseio.com/CurrentAmbulance/");
        ref.push().setValue(l);

    }
    private NotificationManager mNotificationManager;
    public static final int NOTIFICATION_ID = 1;
    private void sendNotification(String msg) {
        mNotificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, MainActivity.class), 0);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setContentTitle("Someone Has Been In An Accident And Needs Your Help!")
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(msg))
                        .setContentText(msg)
                      //  .setSound(R.raq)
                        .setSmallIcon(R.drawable.ambulance);
        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }

}
