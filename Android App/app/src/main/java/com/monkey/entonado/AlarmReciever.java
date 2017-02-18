package com.monkey.entonado;


import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

public class AlarmReciever extends BroadcastReceiver
{

		@Override
		public void onReceive(Context context, Intent intent) 
		{
			System.out.println("llego a recieve   ------------------");
			
			// TODO Auto-generated method stub
			Intent in = new Intent(context, MainActivity.class);
			in.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
			PendingIntent pi = PendingIntent.getActivity(context, 1994, in, PendingIntent.FLAG_ONE_SHOT);
			
			NotificationManager manager = (NotificationManager)context.getSystemService(context.NOTIFICATION_SERVICE);
			NotificationCompat.Builder not = new NotificationCompat.Builder(context);
			not.setSmallIcon(android.R.drawable.ic_popup_sync)
			.setAutoCancel(true)
			.setContentTitle("Ya han pasado 20 minutos")
			.setContentText("Toca para pedir más canciones")
			.setTicker("Ya puedes mandar canciones!")
			.setContentIntent(pi);
			Notification noti = not.build();
			noti.flags |= Notification.FLAG_ONGOING_EVENT;
			
			manager.notify(MainActivity.TAG, 0, noti);
			
		}
		
}
