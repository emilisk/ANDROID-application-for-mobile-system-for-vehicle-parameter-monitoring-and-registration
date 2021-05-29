package mylocation.example.logandreg;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Formatter;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import java.lang.Math;

import static mylocation.example.logandreg.MainActivity.ats;
import static mylocation.example.logandreg.MainActivity.kelione;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMapLongClickListener,
        GoogleMap.OnMarkerDragListener, LocationListener, SensorEventListener, NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "MapsActivity";
    private GoogleMap mMap;
    private Geocoder geocoder;
    private int ACCESS_LOCATION_REQUEST_CODE = 10001;
    FusedLocationProviderClient fusedLocationProviderClient;
    LocationRequest locationRequest;


    static String totaldistance;
    // duobes trys asys

    double latitude1;
    double longitude1;
    double ax1, ay1, az1;
    private double mAccel;
    private double mAccelCurrent;
    private double mAccelLast;
    Location loc;
    Location location;
    protected LocationManager locationManager;
    boolean isGPSEnabled = false;
    boolean canGetLocation = false;
    Criteria criteria;
    boolean isNetworkEnabled;
    String bestProvider;

    //


    //vidutinis greitis
    float vid_greitis = 0f;
    int seconds, minutes, hours;
    float laikas;

    // Timer
    Timer timer;
    TimerTask timerTask;
    Double time2 = 0.0;

    boolean timerStarted = false;
    /// 5/4/2021


    // Distance
    Location currLocation, prevLocation;
    Boolean isInitialized = false;
    float distance = 0.0f;
    //

    Marker userLocationMarker;
    Circle userLocationAccuracyCircle;

    SwitchCompat sw_metric;
    TextView tv_speed;
    TextView tv_distance;
    TextView tv_time;


    // Mygtukas
    ToggleButton mygtukas;
    //


    //Akselerometras
    SensorManager sensorManager;
    Sensor accelerometer;

    // Navigation drawer
    private DrawerLayout drawer;
    NavigationView navigationView;
    Toolbar toolbar = null;

    double ax, ay, az;
    double azsenas;

    boolean detection_state = false;

    int tripid = 0;

    private Handler mHandler = new Handler();

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {

            case R.id.logout: {
                Intent LoginIntent = new Intent(MapsActivity.this, MainActivity.class);
                Toast.makeText(MapsActivity.this, "Logged out", Toast.LENGTH_SHORT).show();
                startActivity(LoginIntent);
                return true;
            }
            case R.id.Exit: {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

                alertDialogBuilder.setTitle("Confirm Exit..");

                alertDialogBuilder.setIcon(R.drawable.ic_exit);

                alertDialogBuilder.setMessage("Are you sure you want to exit?");

                alertDialogBuilder.setCancelable(false);

                alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        moveTaskToBack(true);
                        android.os.Process.killProcess(android.os.Process.myPid());
                        System.exit(1);
                        //finish();
                    }
                });

                alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(MapsActivity.this, "You clicked on cancel", Toast.LENGTH_SHORT).show();
                    }
                });

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
                return true;
            }
            case R.id.Share: {
                ApplicationInfo api = getApplicationContext().getApplicationInfo();
                String apkpath = api.sourceDir;
                Intent intent = new Intent(Intent.ACTION_SEND);
                String shareBody = "MyTrips";
                intent.setType("text/plain");
                intent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Try subject");
                intent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                startActivity(Intent.createChooser(intent, "ShareVia"));
            }
        }
        return super.onOptionsItemSelected(item);

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        final JSONObject req_data = new JSONObject();
        try {
            req_data.put("username", MainActivity.username);
            req_data.put("password", MainActivity.password);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestQueue requestQueue = Volley.newRequestQueue(MapsActivity.this);
        String URL = "http://78.60.2.145:8001/registracija2/";
        final String TAG ="Profile";
        JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.POST, URL, req_data, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONObject jsonObject = new JSONObject(response.toString());
                    totaldistance = jsonObject.getString("kelias");
                    Log.d(TAG, "Distance: " + totaldistance);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "Rest Response: " + error.toString());
            }
        });
        requestQueue.add(objectRequest);



        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);


        timer = new Timer();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.getMenu().getItem(0).setChecked(true);


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        geocoder = new Geocoder(this);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        locationRequest = LocationRequest.create();
        locationRequest.setInterval(500);
        locationRequest.setFastestInterval(500);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

//        sw_metric = findViewById(R.id.sw_metric);
        tv_speed = findViewById(R.id.tv_speed);
        tv_distance = findViewById(R.id.tv_distance);
        tv_time = findViewById(R.id.tv_time);


        //Akselerometras
//        xValue = (TextView) findViewById(R.id.xValue);
//        yValue = (TextView) findViewById(R.id.yValue);
//        zValue = (TextView) findViewById(R.id.zValue);


        Intent intent = getIntent();
        int ats = intent.getIntExtra(MainActivity.EXTRA_NUMBER, 0);
        int kelione = intent.getIntExtra(MainActivity.EXTRA_NUMBER_KID, 0);
        Log.d(TAG, "reiksme: " + ats);
        Log.d(TAG, "reiksme: " + kelione);


        // TIME AND DATE
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        String dateTime = simpleDateFormat.format(calendar.getTime());
        Log.d(TAG, "DATA: " + dateTime);
        //

        //bottom menu
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        //Set main Selected
        bottomNavigationView.setSelectedItemId(R.id.nav_main);
        //Perfomr itemselectedListener
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_main:
                        return true;
                    case R.id.nav_profile:
                        startActivity(new Intent(getApplicationContext(), Profile.class));
                        overridePendingTransition(0, 0);
                        return true;
//                    case R.id.nav_about:
//                        startActivity(new Intent(getApplicationContext(),About.class));
//                        overridePendingTransition(0, 0);
//                        return true;
                }
                return false;
            }
        });


        Log.d(TAG, "onCreate: Initializing Sensor Services");
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(MapsActivity.this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        Log.d(TAG, "onCreate: Registered acclerometer listener");
        //


        //
        mygtukas = findViewById(R.id.mygtukas);
        //MYGTUKAS
        mygtukas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                boolean on = ((ToggleButton) v).isChecked();

                if (on) {
                    //button.setBackgroundColor(getResources().getColor(greenTranslucent));
                    Toast.makeText(MapsActivity.this, "Started", Toast.LENGTH_LONG).show();
                    detection_state = true;
                    Log.d(TAG, "start trip");
                    MainActivity.kelione = MainActivity.kelione + 1;
                    tv_distance.setText("Distance = 0 kilometers");
                    distance = 0;
                    time2 = 0.0;
                    timerStarted = false;
                    tv_time.setText(formatTime(0, 0, 0));
                } else {
                    //button.setBackgroundColor(getResources().getColor(grayTranslucent));
                    Toast.makeText(MapsActivity.this, "Stopped", Toast.LENGTH_LONG).show();
                    detection_state = false;
                    Log.d(TAG, "stop trip");
                    timerTask.cancel();
                    tv_time.setText(formatTime(0, 0, 0));
                    tv_speed.setText("Speed : 0 km/h");
                    tv_distance.setText("Distance = 0 kilometers");


                    vid_greitis = distance;
                    laikas = ((float) seconds / 3600) + ((float) minutes / 60) + (float) hours;
                    vid_greitis = distance / laikas;
                    vid_greitis = (float) (Math.floor(vid_greitis * 100) / 100);
                    Log.d(TAG, "Vidutinis greitis: " + vid_greitis);


                    final String url = "http://78.60.2.145:8001/speed/";
                    final RequestQueue queue = Volley.newRequestQueue((Context) MapsActivity.this);
                    final JSONObject req_data = new JSONObject();
                    try {
                        req_data.put("tripid", MainActivity.kelione);
                        req_data.put("userid", MainActivity.ats);
//                    req_data.put("speed", nCurrentSpeed);
                        req_data.put("speed", vid_greitis);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(1, url, req_data, (Response.Listener) (new Response.Listener() {
                        public void onResponse(Object var1) {
                            this.onResponse((JSONObject) var1);
                        }

                        public final void onResponse(JSONObject response) {
                        }
                    }), (Response.ErrorListener) (new Response.ErrorListener() {
                        public final void onErrorResponse(VolleyError error) {
                        }
                    }));
                    queue.add((Request) jsonObjectRequest);


                    //Duomenu isvedimas
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MapsActivity.this);

                    alertDialogBuilder.setTitle("Your trip results:");

                    alertDialogBuilder.setMessage("Distance: " + (float) distance + " km\n" + "Duration: " + getTimerText()
                            + "\n" + "Average speed: " + vid_greitis + " km/h");

                    alertDialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();


                }

            }
        });
        //


        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1000);
        } else if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            doStuff();
        }

//        this.updateSpeed(null);

//        sw_metric.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                MapsActivity.this.updateSpeed(null);
//            }
//        });


    }


    private void startTimer() {
        timerTask = new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        time2++;
                        tv_time.setText(getTimerText());
                    }
                });
            }

        };
        timer.scheduleAtFixedRate(timerTask, 0, 1000);
    }


    private String getTimerText() {
        int rounded = (int) Math.round(time2);

        seconds = ((rounded % 86400) % 3600) % 60;
        minutes = ((rounded % 86400) % 3600) / 60;
        hours = ((rounded % 86400) / 3600);


        return formatTime(seconds, minutes, hours);
    }

    @SuppressLint("DefaultLocale")
    private String formatTime(int seconds, int minutes, int hours) {
        return String.format("%02d", hours) + " : " + String.format("%02d", minutes) + " : " + String.format("%02d", seconds);


    }

//    private int vidgreits (float vid_greitis){
//
//        vid_greitis = (seconds / 3600) + (minutes / 60) + hours;
//    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        mMap.setOnMapLongClickListener(this);
        mMap.setOnMarkerDragListener(this);


        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager
                .PERMISSION_GRANTED) {
            //enableUserLocation();
            //zoomToUserLocation();
        } else {
//            askLocationPermission();
        }


        // Add a marker in Sydney and move the camera
//        LatLng latLng = new LatLng(27.1751, 78.0421);
//        MarkerOptions markerOptions = new MarkerOptions()
//                .position(latLng)
//                .title("Taj Mahal")
//                .snippet("Wonder of the world");
//        mMap.addMarker(markerOptions);
//        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 16);
//        mMap.animateCamera(cameraUpdate);

//        try {
//            List<Address> addresses = geocoder.getFromLocationName("london", 1);
//            if (addresses.size() > 0){
//                Address address = addresses.get(0);
//                LatLng london = new LatLng(address.getLatitude(), address.getLongitude());
//                MarkerOptions markerOptions = new MarkerOptions()
//                        .position(london)
//                        .title(address.getLocality());
//                mMap.addMarker(markerOptions);
//                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(london, 16));
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }


    }

    LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            super.onLocationResult(locationResult);
//            Log.d(TAG, "onLocationResult: " + locationResult.getLastLocation());
            if (mMap != null) {
                setUserLocationMarker(locationResult.getLastLocation());
            }
        }
    };

    private void setUserLocationMarker(Location location) {

        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

        if (userLocationMarker == null) {
            // we create a new marker
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(latLng);
            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.car2));
            markerOptions.rotation(location.getBearing());
            markerOptions.anchor((float) 0.5, (float) 0.5);
            userLocationMarker = mMap.addMarker(markerOptions);
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17));
        } else {
            // use previously created marker
            userLocationMarker.setPosition(latLng);
            userLocationMarker.setRotation(location.getBearing());
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17));
        }
        if (userLocationAccuracyCircle == null) {
            CircleOptions circleOptions = new CircleOptions();
            circleOptions.center(latLng);
            circleOptions.strokeWidth(4);
            circleOptions.strokeColor(Color.argb(255, 255, 0, 0));
            circleOptions.fillColor(Color.argb(32, 255, 0, 0));
            circleOptions.radius(location.getAccuracy());
            userLocationAccuracyCircle = mMap.addCircle(circleOptions);
        } else {
            userLocationAccuracyCircle.setCenter(latLng);
            userLocationAccuracyCircle.setRadius(location.getAccuracy());
        }
    }

    @SuppressLint("MissingPermission")
    private void startLocationUpdates() {
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
    }

    private void stopLocationUpdates() {
        fusedLocationProviderClient.removeLocationUpdates(locationCallback);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager
                .PERMISSION_GRANTED) {
            startLocationUpdates();
        } else {
//            askLocationPermission();
            // you need to request permission
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopLocationUpdates();
    }

    @SuppressLint("MissingPermission")
    private void enableUserLocation() {
        mMap.setMyLocationEnabled(true);
    }

    private void zoomToUserLocation() {
        @SuppressLint("MissingPermission")
        Task<Location> locationTask = fusedLocationProviderClient.getLastLocation();
        locationTask.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 20));
                //mMap.addMarker(new MarkerOptions().position(latLng));
            }
        });
    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        Log.d(TAG, "onMapLongClick: " + latLng.toString());
        try {
            List<Address> addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
            if (addresses.size() > 0) {
                Address address = addresses.get(0);
                String streetAddress = address.getAddressLine(0);
                mMap.addMarker(new MarkerOptions()
                        .position(latLng)
                        .title(streetAddress)
                        .draggable(true)
                );
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onMarkerDragStart(Marker marker) {
        Log.d(TAG, "onMarkerDragStart: ");
    }

    @Override
    public void onMarkerDrag(Marker marker) {
        Log.d(TAG, "onMarkerDrag: ");
    }

    @Override
    public void onMarkerDragEnd(Marker marker) {
        Log.d(TAG, "onMarkerDragEnd: ");
        LatLng latLng = marker.getPosition();
        try {
            List<Address> addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
            if (addresses.size() > 0) {
                Address address = addresses.get(0);
                String streetAddress = address.getAddressLine(0);
                marker.setTitle(streetAddress);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == ACCESS_LOCATION_REQUEST_CODE && requestCode == 1000) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                enableUserLocation();
                zoomToUserLocation();
//                doStuff();
//                this.updateSpeed(null);
            } else {
                // we can show dialog that permission is not granted
            }
        }
    }


    // koordinatems

    @SuppressLint("DefaultLocale")
    @Override
    public void onLocationChanged(@NonNull Location location) {

        float latitude = 0;
        float longitude = 0;
        final String url = "http://78.60.2.145:8001/location/";
        final RequestQueue queue = Volley.newRequestQueue((Context) this);

        if (location != null) {
            CLocation myLocation = new CLocation(location, this.useMetricUnits());
            this.updateSpeed(myLocation);


            if (!isInitialized && detection_state == true) {

                prevLocation = location;
                currLocation = location;
                isInitialized = true;
                tv_distance.setText("Distance = 0 meters");

            } else if (detection_state == true && isInitialized == true) {
                if (timerStarted == false) {
                    timerStarted = true;
                    startTimer();
                }
                prevLocation = currLocation;
                currLocation = location;
                distance += (prevLocation.distanceTo(currLocation) / 1000);
                distance = (float) (Math.floor(distance * 100) / 100);
                tv_distance.setText("Distance = " + distance + " kilometers");

//                    Log.d(TAG, "onLocationResult: " + location.getLatitude());
//                    Log.d(TAG, "onLocationResult: " + location.getLongitude());

                latitude = (float) location.getLatitude();
                longitude = (float) location.getLongitude();

                Calendar calendar = Calendar.getInstance();
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                String dateTime = simpleDateFormat.format(calendar.getTime());

                final JSONObject req_data = new JSONObject();
                try {
                    req_data.put("tripid", kelione);
                    req_data.put("userid", ats);
                    req_data.put("latitude", latitude);
                    req_data.put("longitude", longitude);
                    req_data.put("laikas", dateTime);
                    req_data.put("distance", distance);
                    req_data.put("keliones_laikas", getTimerText());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
//                    Log.d(TAG, "Lokacijos platuma: " + latitude);
//                    Log.d(TAG, "Lokacijos ilguma: " + longitude);
//                    Log.d(TAG, "Laikas: " + dateTime);
//                    Log.d(TAG, "Distancija: " + distance);
//                    Log.d(TAG, "Keliones laikas: " + getTimerText());

                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(1, url, req_data, (Response.Listener) (new Response.Listener() {
                    public void onResponse(Object var1) {
                        this.onResponse((JSONObject) var1);
                    }

                    public final void onResponse(JSONObject response) {
                    }
                }), (Response.ErrorListener) (new Response.ErrorListener() {
                    public final void onErrorResponse(VolleyError error) {
                    }
                }));
                queue.add((Request) jsonObjectRequest);
            }

                double skirtumas;
                skirtumas = az - azsenas;
                Math.abs(skirtumas);
            if (detection_state == true && (skirtumas > 0.79)) {
                Log.d(TAG, "Skirtumas: " + (az - azsenas));
                Log.d(TAG, "Abs Skirtumas: " + skirtumas);


                final String url2 = "http://78.60.2.145:8001/duobes/";
                latitude = (float) location.getLatitude();
                longitude = (float) location.getLongitude();

                final JSONObject req_data = new JSONObject();
                try {
                    req_data.put("tripid", kelione);
                    req_data.put("userid", ats);
                    req_data.put("latitude", latitude);
                    req_data.put("longitude", longitude);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Log.d(TAG, "Akselerometras x siuncia: " + ax);
                Log.d(TAG, "Akselerometras y siuncia: " + ay);
                Log.d(TAG, "Akselerometras z siuncia: " + az);
                Log.d(TAG, "Akselerometras azsenas: " + azsenas);

                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(1, url2, req_data, (Response.Listener) (new Response.Listener() {
                    public void onResponse(Object var1) {
                        this.onResponse();
                    }

                    public final void onResponse() {
                    }
                }), (Response.ErrorListener) (new Response.ErrorListener() {
                    public final void onErrorResponse(VolleyError error) {
                    }
                }));
                queue.add((Request) jsonObjectRequest);


            }
            azsenas = az;


        }
    }


    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            mHandler.postDelayed(this, 6000);
            Toast.makeText(MapsActivity.this, "DELAY", Toast.LENGTH_SHORT);
        }
    };

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {

    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {

    }

    @SuppressLint("MissingPermission")
    public void doStuff() {
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        if (locationManager != null) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        }
        Toast.makeText(this, "Waiting for GPS connection", Toast.LENGTH_SHORT).show();
    }


    private void updateSpeed(CLocation location) {

        float nCurrentSpeed = 0;
        if (location != null) {
            location.setUserMetricUnits(this.useMetricUnits());
            nCurrentSpeed = location.getSpeed();
        }

        Formatter fmt = new Formatter(new StringBuilder());
        fmt.format(Locale.US, "%5.1f", nCurrentSpeed);
        String strCurrentSpeed = fmt.toString();
        strCurrentSpeed = strCurrentSpeed.replace(" ", "0");


        if (this.useMetricUnits() == false) {
//            tv_speed.setText("Speed: "+ strCurrentSpeed + " miles/h");
//        } else {
            tv_speed.setText("Speed: " + strCurrentSpeed + " km/h");
            if (detection_state == false) {
                tv_speed.setText("Speed : 0 km/h");
            }
        }
    }

    private boolean useMetricUnits() {
        return false;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

//    public Location getLocation() {
//        try {
//            locationManager = (LocationManager) this
//                    .getSystemService(LOCATION_SERVICE);
//
//            // getting GPS status
//            isGPSEnabled = locationManager
//                    .isProviderEnabled(LocationManager.GPS_PROVIDER);
//
//            // getting network status
//            isNetworkEnabled = locationManager
//                    .isProviderEnabled(LocationManager.NETWORK_PROVIDER);
//
//            if (!isGPSEnabled && !isNetworkEnabled) {
//                // no network provider is enabled
//                Log.d(TAG, " no network provider is enabled");
//            } else {
//                this.canGetLocation = true;
//                // First get location from Network Provider
//                if (isNetworkEnabled) {
//                    locationManager.requestLocationUpdates(
//                            LocationManager.NETWORK_PROVIDER,
//                            1000,
//                            1, this);
//                    Log.d(TAG, "Network");
//                    if (locationManager != null) {
//                        location = locationManager
//                                .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
//                        if (location != null) {
//                            latitude1 = location.getLatitude();
//                            longitude1 = location.getLongitude();
//                        }
//                    }
//                }
//                // if GPS Enabled get lat/long using GPS Services
//                if (isGPSEnabled) {
//                    if (location == null) {
//                        locationManager.requestLocationUpdates(
//                                LocationManager.GPS_PROVIDER,
//                                1000,
//                                1, this);
//                        Log.d(TAG, "GPS Enabled");
//                        if (locationManager != null) {
//                            location = locationManager
//                                    .getLastKnownLocation(LocationManager.GPS_PROVIDER);
//                            if (location != null) {
//                                latitude1 = location.getLatitude();
//                                longitude1 = location.getLongitude();
//                            }
//                        }
//                    }
//                }
//            }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        return location;
//    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
//        Log.d(TAG, "onSensorChanged: X: " +sensorEvent.values[0] + "Y: " + sensorEvent.values[1] + "Z: " + sensorEvent.values[2]);
        ax = sensorEvent.values[0];
        ay = sensorEvent.values[1];
        az = sensorEvent.values[2];


//        if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
//            ax1 = sensorEvent.values[0];
//            ay1 = sensorEvent.values[1];
//            az1 = sensorEvent.values[2];
//            mAccelLast = mAccelCurrent;
//            mAccelCurrent = Math.sqrt(ax1 * ax1 + ay1 * ay1 + az1 * az1);
//            double delta = mAccelCurrent - mAccelLast;
//            mAccel = mAccel * 0.9f + delta;
//
//
//            int temp = compare((int) ax1, (int) ay1, (int) az1);
//
//            if (temp == 0) {
//                if ((mAccelLast - mAccelCurrent) > 7) {
//                    Log.d(TAG, "pothole x1");
//                    if (loc == null) {
//                        loc = getLocation();
//                    }
//                    if (loc != null) {
//                        loc = getLocation();
//                    }
////                    double latitude1 = loc.getLatitude();
////                    double longitude1 = loc.getLongitude();
//
//                    final RequestQueue queue = Volley.newRequestQueue((Context) this);
//                    final String url2 = "http://78.60.2.145:8001/duobes/";
//                    latitude1 = (float) loc.getLatitude();
//                    longitude1 = (float) loc.getLongitude();
//                    Log.d(TAG, "location : " + latitude1 + " " + longitude1);
//
//                    final JSONObject req_data = new JSONObject();
//                    try {
//                        req_data.put("tripid", kelione);
//                        req_data.put("userid", ats);
//                        req_data.put("latitude", latitude1);
//                        req_data.put("longitude", longitude1);
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(1, url2, req_data, (Response.Listener) (new Response.Listener() {
//                        public void onResponse(Object var1) {
//                            this.onResponse();
//                        }
//
//                        public final void onResponse() {
//                        }
//                    }), (Response.ErrorListener) (new Response.ErrorListener() {
//                        public final void onErrorResponse(VolleyError error) {
//                        }
//                    }));
//                    queue.add((Request) jsonObjectRequest);
//                }
//            } else if (temp == 1) {
//                if ((mAccelLast - mAccelCurrent) > 7) {
//                    Log.d(TAG, "pothole y1");
//                    if (loc == null) {
//                        loc = getLocation();
//                    }
////                    double latitude1 = loc.getLatitude();
////                    double longitude1 = loc.getLongitude();
//                    if (loc != null) {
//                        loc = getLocation();
//                    }
//
//                    final RequestQueue queue = Volley.newRequestQueue((Context) this);
//                    final String url2 = "http://78.60.2.145:8001/duobes/";
//                    latitude1 = (float) loc.getLatitude();
//                    longitude1 = (float) loc.getLongitude();
//                    Log.d(TAG, "location : " + latitude1 + " " + longitude1);
//
//                    final JSONObject req_data = new JSONObject();
//                    try {
//                        req_data.put("tripid", kelione);
//                        req_data.put("userid", ats);
//                        req_data.put("latitude", latitude1);
//                        req_data.put("longitude", longitude1);
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(1, url2, req_data, (Response.Listener) (new Response.Listener() {
//                        public void onResponse(Object var1) {
//                            this.onResponse();
//                        }
//
//                        public final void onResponse() {
//                        }
//                    }), (Response.ErrorListener) (new Response.ErrorListener() {
//                        public final void onErrorResponse(VolleyError error) {
//                        }
//                    }));
//                    queue.add((Request) jsonObjectRequest);
//                }
//            } else if (temp == 2) {
//
//                if ((mAccelLast - mAccelCurrent) > 7) {
//                    Log.d(TAG, "pothole z1");
//                    if (loc == null) {
//                        loc = getLocation();
//                    }
//                    if (loc != null) {
//                        loc = getLocation();
//                    }
////                    double latitude1 = loc.getLatitude();
////                    double longitude1 = loc.getLongitude();
//
//                    final RequestQueue queue = Volley.newRequestQueue((Context) this);
//                    final String url2 = "http://78.60.2.145:8001/duobes/";
//                    latitude1 = (float) loc.getLatitude();
//                    longitude1 = (float) loc.getLongitude();
//                    Log.d(TAG, "location : " + latitude1 + " " + longitude1);
//
//                    final JSONObject req_data = new JSONObject();
//                    try {
//                        req_data.put("tripid", kelione);
//                        req_data.put("userid", ats);
//                        req_data.put("latitude", latitude1);
//                        req_data.put("longitude", longitude1);
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(1, url2, req_data, (Response.Listener) (new Response.Listener() {
//                        public void onResponse(Object var1) {
//                            this.onResponse();
//                        }
//
//                        public final void onResponse() {
//                        }
//                    }), (Response.ErrorListener) (new Response.ErrorListener() {
//                        public final void onErrorResponse(VolleyError error) {
//                        }
//                    }));
//                    queue.add((Request) jsonObjectRequest);
//                }
//            }
//        }
    }





    private int compare(int ax1, int ay1, int az1) {
        ax1 = Math.abs(ax1);
        ay1 = Math.abs(ay1);
        az1 = Math.abs(az1);
        if (ax1 > ay1) {
            if (ax1 > az1) return 0;
        } else if (ay1 > az1) return 1;
        else return 2;

        return -1;
    }




        private static MapsActivity lastPausedActivity = null;

        @Override
        protected void onPause() {

            super.onPause();
            lastPausedActivity = this;
        }

        @Override
        protected void onResume() {

            super.onResume();
            if(this == lastPausedActivity) {
                lastPausedActivity = null;
                Intent intent = new Intent(this, MapsActivity.class);
                intent.setFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP );
                startActivity(intent);
            }
        }

    @Override
    public void onBackPressed() {
            if (drawer.isDrawerOpen(GravityCompat.START)){
                drawer.closeDrawer(GravityCompat.START);
            } else {
                super.onBackPressed();
            }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            int id = item.getItemId();
            switch (id){

                case R.id.nav_home:
                    return true;
                case R.id.nav_profile:
                    startActivity(new Intent(getApplicationContext(),Profile.class));
                    overridePendingTransition(0, 0);
                    return true;
//                case R.id.nav_about:
//                    startActivity(new Intent(getApplicationContext(),About.class));
//                    overridePendingTransition(0, 0);
//                    return true;
                case R.id.nav_logout:
                    Intent LoginIntent = new Intent (MapsActivity.this, MainActivity.class);
                    Toast.makeText(MapsActivity.this, "Logged out", Toast.LENGTH_SHORT).show();
                    startActivity(LoginIntent);
                    return true;
                case R.id.nav_exit:{
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

                    alertDialogBuilder.setTitle("Confirm Exit..");

                    alertDialogBuilder.setIcon(R.drawable.ic_exit);

                    alertDialogBuilder.setMessage("Are you sure you want to exit?");

                    alertDialogBuilder.setCancelable(false);

                    alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            moveTaskToBack(true);
                            android.os.Process.killProcess(android.os.Process.myPid());
                            System.exit(1);
                            //finish();
                        }
                    });

                    alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Toast.makeText(MapsActivity.this, "You clicked on cancel", Toast.LENGTH_SHORT).show();
                        }
                    });

                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();
                    return true;
                }
                case R.id.nav_share:
                    Toast.makeText(this, "Share", Toast.LENGTH_SHORT).show();
                    ApplicationInfo api = getApplicationContext().getApplicationInfo();
                    String apkpath = api.sourceDir;
                    Intent intent = new Intent(Intent.ACTION_SEND);
                    String shareBody = "MyTrips";
                    intent.setType("text/plain");
                    intent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Try subject");
                    intent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                    startActivity(Intent.createChooser(intent, "ShareVia"));
                    return true;
            }
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);

        return true;
    }



//    private void askLocationPermission(){
//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
//            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
//                Log.d(TAG, "askLocationPermission: you should show alert dialogue");
//                ActivityCompat.requestPermissions(this, new String [] {Manifest.permission.ACCESS_FINE_LOCATION},
//                        ACCESS_LOCATION_REQUEST_CODE);
//            } else {
//                ActivityCompat.requestPermissions(this, new String [] {Manifest.permission.ACCESS_FINE_LOCATION},
//                        ACCESS_LOCATION_REQUEST_CODE);
//            }
//        }
//    }

    //        private void duobes(SensorEvent sensorEvent, Location location) {
//
//            Log.d(TAG, "onSensorChanged: X: " +sensorEvent.values[0] + "Y: " + sensorEvent.values[1] + "Z: " + sensorEvent.values[2]);
//            ax = sensorEvent.values[0];
//            ay = sensorEvent.values[1];
//            az = sensorEvent.values[2];
//
//            float latitude = 0;
//            float longitude = 0;
//            final String url = "http://78.60.2.145:8001/location/";
//            final RequestQueue queue = Volley.newRequestQueue((Context) this);
//
//            if (detection_state == true && (ax < -4.0 || ay < -4.0 || az < -4.0)) {
//
//                latitude = (float) location.getLatitude();
//                longitude = (float) location.getLongitude();
//
//                final JSONObject req_data = new JSONObject();
//                try {
//                    req_data.put("tripid", kelione);
//                    req_data.put("userid", ats);
//                    req_data.put("latitude", latitude);
//                    req_data.put("longitude", longitude);
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//
//                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(1, url, req_data, (Response.Listener) (new Response.Listener() {
//                    public void onResponse(Object var1) {
//                        this.onResponse((JSONObject) var1);
//                    }
//
//                    public final void onResponse(JSONObject response) {
//                    }
//                }), (Response.ErrorListener) (new Response.ErrorListener() {
//                    public final void onErrorResponse(VolleyError error) {
//                    }
//                }));
//                queue.add((Request) jsonObjectRequest);
//            }
//        }


    ///
}



