package com.example.template;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.template.model.Post;
import com.google.android.gms.location.LocationServices;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.template.db.AppDatabaseHelper;
import com.example.template.helpers.NotificationHelper;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private static final int REQUEST_PERMISSIONS = 100;

    interface GetPost{
        @GET("/posts")
        Call<ArrayList<Post>> getPosts();
    }
    private TextView mainTextView;
    private ImageButton imageButton;
    private Button mainButton;
    private Switch mainSwitch;
    private ImageView mainImageView;
    private SensorManager sensorManager;
    private View view;
    private int i = 0;

    private long lastUpdate;

    private AppDatabaseHelper databaseHelper;
    private SharedPreferences sharedPreferences;

    private String xyz = "pocetnka";
    private static final int CAMERA_REQUEST = 1888;

    // Declare variables
    private FusedLocationProviderClient locationClient;
    private static final int LOCATION_PERMISSION_REQUEST = 1001;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 1. Povezivanje XML elemenata
        mainTextView = findViewById(R.id.mainTextView);
        imageButton = findViewById(R.id.imageButton);
        mainButton = findViewById(R.id.mainButton);
        mainSwitch = findViewById(R.id.mainSwitch);
        mainImageView = findViewById(R.id.mainImageView);

        // 2. Baza
        databaseHelper = new AppDatabaseHelper(this);

        // 3. SharedPreferences
        sharedPreferences = getSharedPreferences("app_prefs", MODE_PRIVATE);

        // 4. Notifikacioni kanal
        NotificationHelper.createNotificationChannel(this);

        // 5. Dozvole
        requestNeededPermissions();

        mainButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isNotEmpty = databaseHelper.deleteFirstPost();
                if (!isNotEmpty)
                    Toast.makeText(MainActivity.this, "Nema više postova!", Toast.LENGTH_LONG).show();


            }
        });
        // 6. Button logika
        imageButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
            }
        });

        // 7. Switch logika
        mainSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                handleSwitchOn();
            } else {
                handleSwitchOff();
            }
        });

        locationClient = LocationServices.getFusedLocationProviderClient(this);
        getCurrentLocation();

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);


    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            mainImageView.setImageBitmap(photo);
            Toast.makeText(this, xyz, Toast.LENGTH_LONG).show();
        }
    }
    // Function to get the current location
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
                    mainTextView.setText("Latitude: " + lat + "\nLongitude: " + lon);
                } else {
                    // Display error message if location is null
                    mainTextView.setText("Unable to get location");
                }
            }
        });
    }

    private void handleButtonClick() {
        Toast.makeText(this, "Button klik", Toast.LENGTH_SHORT).show();
    }

    private void handleSwitchOn() {
        if (i < 1) {
            final String[] ludilo = {"2"};
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("https://dummy-json.mock.beeceptor.com/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            GetPost getPost = retrofit.create(GetPost.class);
            getPost.getPosts().enqueue(new Callback<ArrayList<Post>>() {
                @Override
                public void onResponse(Call<ArrayList<Post>> call, Response<ArrayList<Post>> response) {
                    ArrayList<Post> posts = response.body();
                    for (Post post: posts) {
                        databaseHelper.insertPost(post);
                        Toast.makeText(MainActivity.this, "unet post" + post.getTitle(), Toast.LENGTH_SHORT).show();

                    }
                    Toast.makeText(MainActivity.this, ludilo[0], Toast.LENGTH_SHORT).show();
                    i = 1;

                }

                @Override
                public void onFailure(Call<ArrayList<Post>> call, Throwable t) {
                    ludilo[0] = t.toString();
                    Toast.makeText(MainActivity.this, ludilo[0], Toast.LENGTH_SHORT).show();

                }
            });
        } else {
            ArrayList<Post> posts = databaseHelper.getAllPosts();
            Toast.makeText(MainActivity.this, posts.get(0).getTitle(), Toast.LENGTH_SHORT).show();
        }


    }

    private void handleSwitchOff() {
        sharedPreferences.edit()
                .putString("tekst", mainTextView.getText().toString())
                .apply();

        String firstContactName = getFirstContactName();

        mainTextView.setText(firstContactName);

        Toast.makeText(
                this,
                "Tekst sačuvan, prikazan prvi kontakt",
                Toast.LENGTH_SHORT
        ).show();
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

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.READ_CONTACTS);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.POST_NOTIFICATIONS);
            }
        }

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

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];

            xyz = "X: " + x + "\nY: " + y + "\nZ: " + z;

            mainButton.setText(xyz);
        }
    }

    private void getAccelerometer(SensorEvent event) {
        float[] values = event.values;
        // Movement
        float x = values[0];
        float y = values[1];
        float z = values[2];

        float accelationSquareRoot = (x * x + y * y + z * z)
                / (SensorManager.GRAVITY_EARTH * SensorManager.GRAVITY_EARTH);
        long actualTime = System.currentTimeMillis();
        if (accelationSquareRoot >= 2) //
        {
            if (actualTime - lastUpdate < 200) {
                return;
            }
            lastUpdate = actualTime;
            xyz = x + " " + y + " " + z;
        }
    }
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) { }

    @Override
    protected void onResume() {
        super.onResume();

        if (sensorManager == null) {
            sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        }

        Sensor accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        if (accelerometer != null) {
            sensorManager.registerListener(
                    this,
                    accelerometer,
                    SensorManager.SENSOR_DELAY_NORMAL
            );
        }
    }
    @Override
    protected void onPause() {
        super.onPause();

        if (sensorManager != null) {
            sensorManager.unregisterListener(this);
        }
    }
    private String getFirstContactName() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {
            return "Nema dozvole za kontakte";
        }

        Cursor cursor = getContentResolver().query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                null,
                null,
                null,
                null
        );

        String contactName = "Nema kontakata";

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                contactName = cursor.getString(
                        cursor.getColumnIndexOrThrow(
                                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME
                        )
                );
            }

            cursor.close();
        }

        return contactName;
    }
}
