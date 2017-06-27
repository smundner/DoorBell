package de.shm.sm.doorbell;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent doorBellListener = new Intent(this,DoorBellListener.class);

        this.startService(doorBellListener);
    }
}
