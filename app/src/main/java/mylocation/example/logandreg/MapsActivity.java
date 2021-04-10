package mylocation.example.logandreg;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Formatter;
import java.util.List;
import java.util.Locale;

import static mylocation.example.logandreg.MainActivity.ats;
import static mylocation.example.logandreg.MainActivity.kelione;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMapLongClickListener,
        GoogleMap.OnMarkerDragListener, LocationListener, SensorEventListener {

    private static final String TAG ="MapsActivity";
    private GoogleMap mMap;
    private Geocoder geocoder;
    private int ACCESS_LOCATION_REQUEST_CODE = 10001;
    FusedLocationProviderClient fusedLocationProviderClient;
    LocationRequest locationRequest;


    Marker userLocationMarker;
    Circle userLocationAccuracyCircle;

    SwitchCompat sw_metric;
    TextView tv_speed;


    // Mygtukas
    ToggleButton mygtukas;
    //

    //Akselerometras
    SensorManager sensorManager;
    Sensor accelerometer;

    TextView xValue, yValue, zValue;

    boolean detection_state = false;

    int tripid=0;

    private Handler mHandler = new Handler();

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){

            case R.id.logout:{
                Intent LoginIntent = new Intent (MapsActivity.this, MainActivity.class);
                Toast.makeText(MapsActivity.this, "Logged out", Toast.LENGTH_SHORT).show();
                startActivity(LoginIntent);
                return true;
            }
            case R.id.Exit:{
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
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
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

        sw_metric = findViewById(R.id.sw_metric);
        tv_speed = findViewById(R.id.tv_speed);

        //Akselerometras
//        xValue = (TextView) findViewById(R.id.xValue);
//        yValue = (TextView) findViewById(R.id.yValue);
//        zValue = (TextView) findViewById(R.id.zValue);



        Intent intent = getIntent();
        int ats =  intent.getIntExtra(MainActivity.EXTRA_NUMBER,0);
        int kelione =  intent.getIntExtra(MainActivity.EXTRA_NUMBER_KID,0);
        Log.d(TAG, "GAUTAS!!! " + ats);
        Log.d(TAG, "GAUTAS!!! " + kelione);

        // logout

        //

        //bottom menu
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        //Set main Selected
        bottomNavigationView.setSelectedItemId(R.id.nav_main);
        //Perfomr itemselectedListener
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.nav_main:
                        return true;
                    case R.id.nav_profile:
                        startActivity(new Intent(getApplicationContext(),Profile.class));
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.nav_about:
                        startActivity(new Intent(getApplicationContext(),About.class));
                        overridePendingTransition(0, 0);
                        return true;
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
//                    tripid = tripid + 1;
//                    MapsActivity.this.onLocationChanged(null);
//                    while(detection_state){
//                    }

                } else {
                    //button.setBackgroundColor(getResources().getColor(grayTranslucent));
                    Toast.makeText(MapsActivity.this, "Stopped", Toast.LENGTH_LONG).show();
                    detection_state = false;
                    Log.d(TAG, "stop trip");
                }

            }
        });
        //



        if(Build.VERSION.SDK_INT == Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, 1000);
        } else {
            doStuff();
        }
        this.updateSpeed(null);

        sw_metric.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                MapsActivity.this.updateSpeed(null);
            }
        });




    }


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


//        final String url = "http://78.60.2.145:8001/speed/";
//        final RequestQueue queue = Volley.newRequestQueue((Context)this);


        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager
        .PERMISSION_GRANTED) {
            //enableUserLocation();
            //zoomToUserLocation();
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)){
                // WE CAN SHOW USER DIALOG
                ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION},
                        ACCESS_LOCATION_REQUEST_CODE);
            } else {
                ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION},
                        ACCESS_LOCATION_REQUEST_CODE);
            }
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

        try {
            List<Address> addresses = geocoder.getFromLocationName("london", 1);
            if (addresses.size() > 0){
                Address address = addresses.get(0);
                LatLng london = new LatLng(address.getLatitude(), address.getLongitude());
                MarkerOptions markerOptions = new MarkerOptions()
                        .position(london)
                        .title(address.getLocality());
                mMap.addMarker(markerOptions);
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(london, 16));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    LocationCallback locationCallback = new LocationCallback(){
        @Override
        public void onLocationResult(LocationResult locationResult) {
            super.onLocationResult(locationResult);
            Log.d(TAG, "onLocationResult: " + locationResult.getLastLocation());
            if (mMap != null){
                setUserLocationMarker(locationResult.getLastLocation());

//                final String url = "http://78.60.2.145:8001/location/";
//                final RequestQueue queue = Volley.newRequestQueue((Context)MapsActivity.this);
//
//                final JSONObject req_data = new JSONObject();
//                try {
//                    req_data.put("id", "1");
//                    req_data.put("latitude", locationResult.getLastLocation());
//                    req_data.put("longitude", locationResult.getLastLocation());
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//
//                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(1, url, req_data, (Response.Listener)(new Response.Listener() {
//                    // $FF: synthetic method
//                    // $FF: bridge method
//                    public void onResponse(Object var1) {
//                        this.onResponse((JSONObject)var1);
//                    }
//
//                    public final void onResponse(JSONObject response) {
//                        //TextView var10000 = txt;
//                        //Intrinsics.checkNotNullExpressionValue(var10000, "txt");
//                        //String var2 = "Response: %s";
//                        //Object[] var3 = new Object[]{response.toString()};
//                        //boolean var4 = false;
//                        //String var10001 = String.format(var2, Arrays.copyOf(var3, var3.length));
//                        //Intrinsics.checkNotNullExpressionValue(var10001, "java.lang.String.format(this, *args)");
//                        //var10000.setText((CharSequence)var10001);
//                    }
//                }), (Response.ErrorListener)(new Response.ErrorListener() {
//                    public final void onErrorResponse(VolleyError error) {
//                        //TextView var10000 = txt;
//                        //Intrinsics.checkNotNullExpressionValue(var10000, "txt");
//                        //var10000.setText((CharSequence)error.toString());
//                    }
//                }));
//                queue.add((Request)jsonObjectRequest);
//
//
//
            }
        }
    };

    private void setUserLocationMarker(Location location){

        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

        if (userLocationMarker == null){
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
        if(userLocationAccuracyCircle == null){
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
    private void startLocationUpdates(){
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
    }

    private void stopLocationUpdates(){
        fusedLocationProviderClient.removeLocationUpdates(locationCallback);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager
                .PERMISSION_GRANTED){
            startLocationUpdates();
        } else {
            // you need to request permission
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopLocationUpdates();
    }

    @SuppressLint("MissingPermission")
    private void enableUserLocation(){
        mMap.setMyLocationEnabled(true);
    }

    private void zoomToUserLocation(){
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
        Log.d(TAG, "onMapLongClick: "+ latLng.toString());
        try {
            List<Address> addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
            if (addresses.size() > 0){
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
            if (addresses.size() > 0){
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
        if(requestCode == ACCESS_LOCATION_REQUEST_CODE){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                enableUserLocation();
                zoomToUserLocation();
                doStuff();
            } else {
                // we can show dialog that permission is not granted
            }
        }
    }





    // koordinatems

        @Override
        public void onLocationChanged(@NonNull Location location) {

            float latitude = 0;
            float longitude = 0;
            final String url = "http://78.60.2.145:8001/location/";
            final RequestQueue queue = Volley.newRequestQueue((Context)this);

            if(location != null) {
                CLocation myLocation = new CLocation(location, this.useMetricUnits());
                this.updateSpeed(myLocation);

                if (detection_state == true) {
                    Log.d(TAG, "onLocationResult: " + location.getLatitude());
                    Log.d(TAG, "onLocationResult: " + location.getLongitude());


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
                   Log.d(TAG, "NAUDOJAMAS@@@@ " + ats);

                    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(1, url, req_data, (Response.Listener) (new Response.Listener() {
                        // $FF: synthetic method
                        // $FF: bridge method
                        public void onResponse(Object var1) {
                            this.onResponse((JSONObject) var1);
                        }

                        public final void onResponse(JSONObject response) {
                            //TextView var10000 = txt;
                            //Intrinsics.checkNotNullExpressionValue(var10000, "txt");
                            //String var2 = "Response: %s";
                            //Object[] var3 = new Object[]{response.toString()};
                            //boolean var4 = false;
                            //String var10001 = String.format(var2, Arrays.copyOf(var3, var3.length));
                            //Intrinsics.checkNotNullExpressionValue(var10001, "java.lang.String.format(this, *args)");
                            //var10000.setText((CharSequence)var10001);
                        }
                    }), (Response.ErrorListener) (new Response.ErrorListener() {
                        public final void onErrorResponse(VolleyError error) {
                            //TextView var10000 = txt;
                            //Intrinsics.checkNotNullExpressionValue(var10000, "txt");
                            //var10000.setText((CharSequence)error.toString());
                        }
                    }));
                    queue.add((Request) jsonObjectRequest);
                }
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
    public void doStuff(){
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        if(locationManager != null){
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0 ,0, this);
        }
        Toast.makeText(this, "Waiting for GPS connection", Toast.LENGTH_SHORT).show();
    }

    private void updateSpeed(CLocation location){
        float nCurrentSpeed = 0;
        ///
        final String url = "http://78.60.2.145:8001/speed/";
        final RequestQueue queue = Volley.newRequestQueue((Context)this);

        if(location != null) {
            location.setUseMetricUnits(this.useMetricUnits());
            nCurrentSpeed = location.getSpeed();
            //Log.d(TAG, "updateSpeed: " + location.getSpeed());


            /// GREITIS ///
            if (detection_state == true) {
                final JSONObject req_data = new JSONObject();
                try {
                    req_data.put("tripid", kelione);
                    req_data.put("userid", ats);
                    req_data.put("speed", nCurrentSpeed);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(1, url, req_data, (Response.Listener) (new Response.Listener() {
                    // $FF: synthetic method
                    // $FF: bridge method
                    public void onResponse(Object var1) {
                        this.onResponse((JSONObject) var1);
                    }

                    public final void onResponse(JSONObject response) {
                        //TextView var10000 = txt;
                        //Intrinsics.checkNotNullExpressionValue(var10000, "txt");
                        //String var2 = "Response: %s";
                        //Object[] var3 = new Object[]{response.toString()};
                        //boolean var4 = false;
                        //String var10001 = String.format(var2, Arrays.copyOf(var3, var3.length));
                        //Intrinsics.checkNotNullExpressionValue(var10001, "java.lang.String.format(this, *args)");
                        //var10000.setText((CharSequence)var10001);
                    }
                }), (Response.ErrorListener) (new Response.ErrorListener() {
                    public final void onErrorResponse(VolleyError error) {
                        //TextView var10000 = txt;
                        //Intrinsics.checkNotNullExpressionValue(var10000, "txt");
                        //var10000.setText((CharSequence)error.toString());
                    }
                }));
                queue.add((Request) jsonObjectRequest);


            }
        }

        Formatter fmt = new Formatter(new StringBuilder());
        fmt.format(Locale.US, "%5.1f", nCurrentSpeed);
        String strCurrentSpeed = fmt.toString();
        strCurrentSpeed = strCurrentSpeed.replace(" ", "0");


        if(this.useMetricUnits()){
            tv_speed.setText("Speed: "+ strCurrentSpeed + " km/h");
        } else {
            tv_speed.setText("Speed: "+ strCurrentSpeed + " miles/h");
        }
    }

    private boolean useMetricUnits(){
        return sw_metric.isChecked();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        Log.d(TAG, "onSensorChanged: X: " +sensorEvent.values[0] + "Y: " + sensorEvent.values[1] + "Z: " + sensorEvent.values[2]);

//        xValue.setText("xValue:" + sensorEvent.values[0]);
//        yValue.setText("yValue:" + sensorEvent.values[1]);
//        zValue.setText("zValue:" + sensorEvent.values[2]);
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



    ///
}



