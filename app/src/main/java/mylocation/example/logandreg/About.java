package mylocation.example.logandreg;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class About extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG ="About";
    static int tripas;
    static String duration;
    static int vidgreitis;
    static String data;
    static int distance;

    static String info;

    RecyclerView recyclerView;
    RecyclerView.Adapter adapter;
    List<User> user;
    String URL_Data="https://api.github.com/users";
    RequestQueue reqQue;

    private DrawerLayout drawer;
    NavigationView navigationView;
    Toolbar toolbar = null;

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
                Intent LoginIntent = new Intent (About.this, MainActivity.class);
                Toast.makeText(About.this, "Logged out", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(About.this, "You clicked on cancel", Toast.LENGTH_SHORT).show();
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
        setContentView(R.layout.activity_about);

        recyclerView=(RecyclerView)findViewById(R.id.recyleview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        user=new ArrayList<>();


        loadingurl();
        loadurl();



        //Navigation drawer
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        drawer = findViewById(R.id.drawer_layout);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.getMenu().getItem(2).setChecked(true);



        //bottom menu
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        //Set main Selected
//        bottomNavigationView.setSelectedItemId(R.id.nav_about);
//        //Perfomr itemselectedListener
//        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
//            @Override
//            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
//                switch (item.getItemId()){
//                    case R.id.nav_main:
//                        startActivity(new Intent(getApplicationContext(),MapsActivity.class));
//                        overridePendingTransition(0, 0);
//                        return true;
//                    case R.id.nav_profile:
//                        startActivity(new Intent(getApplicationContext(),Profile.class));
//                        overridePendingTransition(0, 0);
//                        return true;
//                    case R.id.nav_about:
//                        return true;
//                }
//                return false;
//            }
//        });
    }


    public void loadingurl() {
        final String url = "http://78.60.2.145:8001/duomenys/";
        final RequestQueue queue = Volley.newRequestQueue((Context) this);
        final JSONObject req_data = new JSONObject();
        try {
            req_data.put("username", MainActivity.username);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(1, url, req_data, (Response.Listener) (new Response.Listener() {
            @Override
            public void onResponse(Object response) {
            }
            public void onResponse(String var1) {
                this.onResponse((String) var1);
                Log.d(TAG, "onResponseObject: " + var1);
            }


            public final void onResponse(JSONObject response) {
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(response.toString());
                    String pageName = jsonObject.getString("tripid");
                    Log.d(TAG, "onResponse: " + pageName);
//                    kelione = jsonObject.getInt("tripid");
//                    Log.d(TAG, "TRIP ID: " +kelione);
                    duration = jsonObject.getString("keliones_laikas");
                    Log.d(TAG, "onResponse: " + duration);
//                    vidgreitis = jsonObject.getInt("vid.greitis");
//                    data = jsonObject.getString("data");
//                    distance = jsonObject.getInt("distance");
//
//                    info = jsonObject.getString("info");

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }), (Response.ErrorListener) (new Response.ErrorListener() {
            public final void onErrorResponse(VolleyError error) {
                Log.d(TAG, "onResponseError: " + error.toString());
            }
        }));
        queue.add((Request) jsonObjectRequest);
    }


    public void loadurl() {
        JsonArrayRequest stringRequest=new JsonArrayRequest(URL_Data, new Response.Listener<JSONArray>() {

            @Override
            public void onResponse(JSONArray response) {

                getvalue(response);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        reqQue = Volley.newRequestQueue(this);

        reqQue.add(stringRequest);
    }

    public void getvalue(JSONArray array) {

        for (int i = 0; i < array.length(); i++) {

            User userlist = new User();

            JSONObject json = null;
            try {
                json = array.getJSONObject(i);


                userlist.setLogin(json.getString("login"));

                userlist.setType(json.getString("type"));

            } catch (JSONException e) {

                e.printStackTrace();
            }
            user.add(userlist);
        }

        adapter = new UserAdapter(user, this);

        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)){
            drawer.closeDrawer(GravityCompat.START);
        }else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id){

            case R.id.nav_home:
                startActivity(new Intent(getApplicationContext(), MapsActivity.class));
                overridePendingTransition(0, 0);
                return true;
            case R.id.nav_profile:
                startActivity(new Intent(getApplicationContext(), Profile.class));
                overridePendingTransition(0, 0);
                return true;
//            case R.id.nav_about:
//                return true;
            case R.id.nav_logout:
                Intent LoginIntent = new Intent (About.this, MainActivity.class);
                Toast.makeText(About.this, "Logged out", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(About.this, "You clicked on cancel", Toast.LENGTH_SHORT).show();
                    }
                });

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
                return true;
            }
            case R.id.nav_share:
                Toast.makeText(this, "Share", Toast.LENGTH_SHORT).show();
                return true;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        return true;
    }
}