package com.example.lalthanpuiachhangte.garbagebackend;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import android.Manifest;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;

public class MainActivity extends AppCompatActivity {

    public DatabaseReference databaseReference;
    public LocationManager locationManager;
    public static LocationListener locationListener;
    public static String userSelect = "";

    EditText userNameET;
    TextView statusTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        userNameET = findViewById(R.id.userName);
        statusTV = findViewById(R.id.statusTextView);

        //ASKED / CHECK FOR LOCATION PERMISSION
        checkLocationPermission();

    }

    /*
        THIS LINE UP TO THE END ASKED FOR THE USER PERMISSION FOR TURNING ON THE GPS
    */
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(this)
                        .setTitle("location Permission")
                        .setMessage("Location Permission")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(MainActivity.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION);
                            }
                        })
                        .create()
                        .show();


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        //Request location updates:
                       // locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 400, 1, (LocationListener) this);
                    }
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
        }
    }

    //LISTENNING FOR THE DRIVER LOCATION
    public void listeningDriverLocation (){

        Toast.makeText(this,"Location Active",Toast.LENGTH_SHORT).show();

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        //Location lastKnowLocation = locationManager.getLastKnownLocation(locationManager.GPS_PROVIDER);

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                databaseReference = FirebaseDatabase.getInstance().getReference();
                String newKey = databaseReference.child(userSelect+"/location/").push().getKey();

                DecimalFormat decimalFormat = new DecimalFormat("#.######");
                String stringLat = decimalFormat.format(location.getLatitude());
                String stringLng = decimalFormat.format(location.getLongitude());

                /*databaseReference.child("truck-2/location/"+ newKey).child("latitude").setValue(location.getLatitude());
                databaseReference.child("truck-2/location/"+ newKey).child("longitude").setValue(location.getLongitude());
*/              databaseReference.child(userSelect+"/location/"+ newKey).child("latitude").setValue(stringLat);
                databaseReference.child(userSelect+"/location/"+ newKey).child("longitude").setValue(stringLng);

            }
            @Override public void onStatusChanged(String provider, int status, Bundle extras) { }
            @Override public void onProviderEnabled(String provider) { }
            @Override public void onProviderDisabled(String provider) { }
        };

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,800,5,locationListener);

    }

    //CHECKING THE TRUCK DRIVER ID
    public void submitClick(View view) {
        String userId= userNameET.getText().toString();
        boolean userStatus = false;

        if(userId != ""){
            //CHECK THE UNIQUE KEY OF THE USER

            switch (userId){
                case "truck1": userSelect="truck-1"; userStatus=true; break;
                case "truck2": userSelect="truck-2"; userStatus=true; break;
                case "truck3": userSelect="truck-3"; userStatus=true; break;
                case "truck4": userSelect="truck-4"; userStatus=true; break;
                default:userSelect="invalid";
            }
            statusTV.setText(userSelect);

            if (userStatus)
                listeningDriverLocation();
        }

    }

    public void inActiveClick(View view) {
        locationListener = null;
        Toast.makeText(this, "Location Inactive", Toast.LENGTH_SHORT).show();
    }
}
