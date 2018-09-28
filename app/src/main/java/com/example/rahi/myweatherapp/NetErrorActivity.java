package com.example.rahi.myweatherapp;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class NetErrorActivity extends AppCompatActivity {

    Button btnStartGPs;
    LocationManager locationManager ;
    boolean GpsStatus ;
    Context context;
    Intent intent1;
    TextView textview;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_net_error);

        btnStartGPs=findViewById(R.id.btnStartGPS);
        textview = (TextView)findViewById(R.id.textView1);
        context = getApplicationContext();


        btnStartGPs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               /* //Enable GPS
                Intent intent = new Intent("android.location.GPS_ENABLED_CHANGE");
                intent.putExtra("enabled", true);
                sendBroadcast(intent);*/

                if (ActivityCompat.checkSelfPermission(NetErrorActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(NetErrorActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }


                intent1 = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent1);

            }
        });

        // check gps status
        GPSStatus();

        if(GpsStatus == true)
        {
            textview.setText("Location Services Is Enabled");
            Intent intent = new Intent(NetErrorActivity.this,MainActivity.class);
            startActivity(intent);
        }else
        {

            textview.setText("Location Services Is Disabled");
        }


    }
    public void GPSStatus(){
        locationManager = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
        GpsStatus = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }
}
