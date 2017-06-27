package de.shm.sm.doorbell;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.Context;
import android.content.IntentFilter;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;

import java.io.IOException;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

public class DoorBellListener extends Service {

    final static String NAME = "DoorBellListener";
    public static final String DOOR_BELL_IP = "door_bell_ip";
    DoorBellBroadcastReciver dbbr;
    Thread UDPReciver;
    private boolean isOff;
    PowerManager.WakeLock wakeLock;
    //PendingIntent alarmIntent;
    //AlarmManager am;


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        dbbr = new DoorBellBroadcastReciver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_USER_PRESENT);

        registerReceiver(dbbr, filter);


        UDPReciver = new Thread(new Runnable() {
            @Override
            public void run() {
                byte[] reciveData = new byte[1024];
                PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
                wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, NAME);
                wakeLock.acquire();
                try {
                    WifiManager wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);

                    DatagramSocket reciveBroadcast = new DatagramSocket(60000);
                    WifiManager.MulticastLock mLock = wifiManager.createMulticastLock(NAME);
                    DatagramPacket recive = new DatagramPacket(reciveData, reciveData.length);
                    while (true) {
                        mLock.acquire();
                        reciveBroadcast.receive(recive);
                        mLock.release();
                        String data = new String(recive.getData());
                        data = data.split("\n")[0];
                        Log.d("SERVICE_BROADCAST", data);
                        if (data.equals("klingeling") /*&& isOff*/) {

                            Intent intent = new Intent(getApplicationContext(), DoorBellAction.class);
                            intent.putExtra(DOOR_BELL_IP, recive.getAddress());

                            getApplicationContext().startActivity(intent);
                        } //else if (data.equals("klingeling")) {}
                    }
                } catch (SocketException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        UDPReciver.start();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(dbbr);
        //am.cancel(alarmIntent);

    }

    class DoorBellBroadcastReciver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
                isOff = true;
                Log.d("SERVICE_BROADCAST", "isOff");

            } else if (intent.getAction().equals(Intent.ACTION_USER_PRESENT)) {
                isOff = false;
                Log.d("SERVICE_BROADCAST", "isNotOff");

            }
        }
    }

}
