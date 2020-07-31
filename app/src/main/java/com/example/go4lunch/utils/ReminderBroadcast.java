package com.example.go4lunch.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.go4lunch.R;
import com.example.go4lunch.models.Restaurant;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import static com.example.go4lunch.activities.MainActivity.userChoice;
import static com.example.go4lunch.activities.MainActivity.workmatesReference;

public class ReminderBroadcast extends BroadcastReceiver
{
    public static final int NOTIFICATION_ID = 200;
    public static final String NOTIFICATION_LUNCH = "notifyLunch";

    @Override
    public void onReceive(Context context, Intent intent) {
        String userLunchChoice = Restaurant.searchByPlaceId(userChoice).getName();
        StringBuilder workmatesEatingWithUser = new StringBuilder(context.getString(R.string.notification_desc));
        workmatesReference
                .whereEqualTo("choice", userChoice)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>()
                {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            workmatesEatingWithUser.append(document.get("name"));
                        }
                    }
                });

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, NOTIFICATION_LUNCH)
                .setSmallIcon(R.drawable.ic_baseline_notifications_24)
                .setContentTitle(context.getString(R.string.notification_name))
                .setContentText(context.getString(R.string.notification_title) + userLunchChoice)
                .setSubText(workmatesEatingWithUser.toString())
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);


        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }
}
