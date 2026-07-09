package com.example.idemojakodrugizadatakodaizapripremiu;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;
import retrofit2.http.GET;

public class MainActivity extends AppCompatActivity {


    interface DrzaveRetrofil {
        @GET("countries")
        Call<ArrayList<Drzava>> getDrzave();
    }
    private static final int REQUEST_PERMISSIONS = 100;
    private TextView textView1;
    private static final int CAMERA_REQUEST = 1888;
    private ImageButton imageButton;
    private ImageView imageView;
    private Switch aSwitch;

    private Integer i = 0;
    private AppDatabaseHelper databaseHelper;
    private SharedPreferences sharedPreferences;

    // Declare variables
    private FusedLocationProviderClient locationClient;
    private static final int LOCATION_PERMISSION_REQUEST = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        textView1 = findViewById(R.id.textView1);

        imageButton = findViewById(R.id.imageButton);
        requestNeededPermissions();
        aSwitch = findViewById(R.id.mainSwitch);
        imageView = findViewById(R.id.mainImageView);

        databaseHelper = new AppDatabaseHelper(this);

        textView1.setText("Ucitavanje trenutne lokacije");


        imageButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
            }
        });

        aSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    handleSwitchOn();
                } else {
//                    handleSwitchOff();
                }

        });
        locationClient = LocationServices.getFusedLocationProviderClient(this);
        getCurrentLocation();

    }

    private void requestNeededPermissions() {
        ArrayList<String> permissions = new ArrayList<>();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.CAMERA);
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        }
//
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)
//                != PackageManager.PERMISSION_GRANTED) {
//            permissions.add(Manifest.permission.READ_CONTACTS);
//        }
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
//            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
//                    != PackageManager.PERMISSION_GRANTED) {
//                permissions.add(Manifest.permission.POST_NOTIFICATIONS);
//            }
//        }

        if (!permissions.isEmpty()) {
            ActivityCompat.requestPermissions(
                    this,
                    permissions.toArray(new String[0]),
                    REQUEST_PERMISSIONS
            );
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions,
                                           int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_PERMISSIONS) {
            Toast.makeText(this, "Odgovor na permisije primljen", Toast.LENGTH_SHORT).show();
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            imageView.setImageBitmap(photo);
            Toast.makeText(this, "ludiloo", Toast.LENGTH_LONG).show();
        }
    }

    private void handleSwitchOn() {
        if (i < 1) {
            final String[] ludilo = {"2"};
            Gson gson = new GsonBuilder()
                    .setLenient()
                    .create();
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("https://dummy-json.mock.beeceptor.com/")
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();

                DrzaveRetrofil retrofil = retrofit.create(DrzaveRetrofil.class);
//            retrofil.getDrzave().enqueue(new Callback<ArrayList<Drzava>>() {
//                @Override
//                public void onResponse(Call<ArrayList<Drzava>> call, Response<ArrayList<Drzava>> response) {
//                    ArrayList<Drzava> drzave = response.body();
//                    Log.d("ludilo",drzave.toString());
//                    for (Drzava d: drzave) {
//                        databaseHelper.insertDrzava(d);
//                        Toast.makeText(MainActivity.this, "uneta drzava " + d.getName(), Toast.LENGTH_SHORT).show();
//
//                    }
//                    Toast.makeText(MainActivity.this, ludilo[0], Toast.LENGTH_SHORT).show();
//                    i = 1;
//                }
//
//                @Override
//                public void onFailure(Call<ArrayList<Drzava>> call, Throwable t) {
//                    ludilo[0] = t.toString();
//                    Toast.makeText(MainActivity.this, ludilo[0], Toast.LENGTH_SHORT).show();
//                    Log.e("ludilo", String.valueOf(t));
//
//                }
            retrofil.getDrzave().enqueue(new Callback<ArrayList<Drzava>>() {

                @Override public void onResponse(Call<ArrayList<Drzava>> call, Response<ArrayList<Drzava>> response) { ArrayList<Drzava> drzave = response.body(); Log.d("ludilo",drzave.toString()); for (Drzava d: drzave) { databaseHelper.insertDrzava(d); Toast.makeText(MainActivity.this, "uneta drzava " + d.getName(), Toast.LENGTH_SHORT).show(); } Toast.makeText(MainActivity.this, ludilo[0], Toast.LENGTH_SHORT).show(); i = 1; }

                @Override
                public void onFailure(Call<ArrayList<Drzava>> call, Throwable t) {

                    Log.e("JSON_ERROR", t.toString());

                }
            });

        } else {
            ArrayList<Drzava> drzave = databaseHelper.getAllDrzave();
            Toast.makeText(MainActivity.this, drzave.get(0).getName(), Toast.LENGTH_SHORT).show();
        }


    }
    private void getCurrentLocation() {
        // Check if location permission is granted
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Request permission if not granted
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST);
            return;
        }

        // Fetch the last known location
        locationClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    // Get latitude and longitude
                    double lat = location.getLatitude();
                    double lon = location.getLongitude();

                    // Display location in TextView
                    textView1.setText("Latitude: " + lat + "\nLongitude: " + lon);
                } else {
                    // Display error message if location is null
                    textView1.setText("Unable to get location");
                }
            }
        });
    }

}
    implementation 'com.squareup.retrofit2:converter-gson:3.0.0'
    implementation 'com.squareup.retrofit2:retrofit:3.0.0'
    implementation 'com.squareup.retrofit2:converter-scalars:2.11.0'



