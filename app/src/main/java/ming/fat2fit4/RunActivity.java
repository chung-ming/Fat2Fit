package ming.fat2fit4;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import ming.fat2fit4.Modules.DirectionFinder;
import ming.fat2fit4.Modules.DirectionFinderListener;
import ming.fat2fit4.Modules.Route;

import static ming.fat2fit4.R.id.map;

/**
 * Created by Ernie&Ming on 18-Jan-17.
 * Reference: https://github.com/hiepxuan2008/GoogleMapDirectionSimple
 */

public class RunActivity extends AppCompatActivity implements OnMapReadyCallback, DirectionFinderListener, NavigationView.OnNavigationItemSelectedListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private FirebaseAuth auth;
    private DatabaseReference root = FirebaseDatabase.getInstance().getReference().getRoot();
    ;
    private DatabaseReference exercises;
    private DatabaseReference sportJSON;
    private DatabaseReference users = root.child("users");
    private DatabaseReference name;
    private DatabaseReference userExercises;
    private DatabaseReference userTimestamp;
    private DatabaseReference totalCaloriesBurnedDatabase;
    private DatabaseReference totalDistanceRanDatabase;
    private DatabaseReference totalDurationOfExerciseDatabase;
    private DatabaseReference caloriesNeededToGoalDatabase;
    private DatabaseReference ageDatabase;
    private DatabaseReference genderDatabase;
    private DatabaseReference heightDatabase;
    private DatabaseReference weightDatabase;
    private GoogleMap mMap;
    private Button btnFindPath;
    private EditText etOrigin;
    private EditText etDestination;
    private List<Marker> originMarkers = new ArrayList<>();
    private List<Marker> destinationMarkers = new ArrayList<>();
    private List<Polyline> polylinePaths = new ArrayList<>();
    private ProgressDialog progressDialog;
    private Double totalDistanceRan;
    private Double totalDurationOfExercise;
    private Double distance;
    private Double duration;
    private Double caloriesBurned;
    private Double totalCaloriesBurned;
    private Double caloriesNeededToGoal;
    private SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
    private String timestamp = s.format(new Date());
    private String gender;
    private String message;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private String emailEncode = "";
    private String username = "";
    double lat = 0, lng = 0;
    private LocationManager locationManager;
    private static final long MIN_TIME = 400;
    private static final float MIN_DISTANCE = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_run);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(map);
        mapFragment.getMapAsync(this);

        // Initialize
        btnFindPath = (Button) findViewById(R.id.btnFindPath);
        etOrigin = (EditText) findViewById(R.id.etOrigin);
        etDestination = (EditText) findViewById(R.id.etDestination);

        emailEncode = getIntent().getExtras().get("email").toString();
        username = getIntent().getExtras().get("username").toString();

        // Show Age, Weight, Height, etc.
        name = users.child(emailEncode);

        // Load Navigation Drawer
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Load Toolbar
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        myToolbar.setNavigationIcon(R.drawable.ic_more_vert);
        // Drawer slides out when icon on toolbar is clicked
        myToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                drawer.openDrawer(GravityCompat.START);
            }
        });

        btnFindPath.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendRequest();
            }
        });

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            enableLocationAlertDialog();
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME, MIN_DISTANCE, this); //You can also use LocationManager.GPS_PROVIDER and LocationManager.PASSIVE_PROVIDER
    }

    private void sendRequest() {
        String origin = etOrigin.getText().toString();
        String destination = etDestination.getText().toString();
        if (origin.isEmpty()) {
            Toast.makeText(this, "Please enter origin address!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (destination.isEmpty()) {
            Toast.makeText(this, "Please enter destination address!", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            new DirectionFinder(this, origin, destination).execute();

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Oops");
            builder.setMessage("We don't know the location you are searching for. Please try again.");

            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Intent intent = new Intent(RunActivity.this, RunActivity.class);
                    intent.putExtra("email", emailEncode);
                    intent.putExtra("username", username);
                    startActivity(intent);
                }
            });

            builder.show();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
//        LatLng hcmus = new LatLng(10.762963, 106.682394);
//        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(hcmus, 18));
//        originMarkers.add(mMap.addMarker(new MarkerOptions()
//                .title("Đại học Khoa học tự nhiên")
//                .position(hcmus)));
//
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            enableLocationAlertDialog();
            return;
        }
        mMap.setMyLocationEnabled(true);
        mMap.isMyLocationEnabled();

        //add this here:
        buildGoogleApiClient();
    }

    private void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    public void onDirectionFinderStart() {
        progressDialog = ProgressDialog.show(this, "Please wait.",
                "Finding direction..!", true);

        if (originMarkers != null) {
            for (Marker marker : originMarkers) {
                marker.remove();
            }
        }

        if (destinationMarkers != null) {
            for (Marker marker : destinationMarkers) {
                marker.remove();
            }
        }

        if (polylinePaths != null) {
            for (Polyline polyline : polylinePaths) {
                polyline.remove();
            }
        }
    }

    @Override
    public void onDirectionFinderSuccess(List<Route> routes) {
        progressDialog.dismiss();
        polylinePaths = new ArrayList<>();
        originMarkers = new ArrayList<>();
        destinationMarkers = new ArrayList<>();

        for (Route route : routes) {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(route.startLocation, 16));
            ((TextView) findViewById(R.id.tvDuration)).setText(route.duration.text);
            ((TextView) findViewById(R.id.tvDistance)).setText(route.distance.text);

            distance = (double) route.distance.value;
            duration = (double) route.duration.value;

            originMarkers.add(mMap.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_place_red))
                    .title(route.startAddress)
                    .position(route.startLocation)));
            destinationMarkers.add(mMap.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_place_green))
                    .title(route.endAddress)
                    .position(route.endLocation)));

            PolylineOptions polylineOptions = new PolylineOptions().
                    geodesic(true).
                    color(Color.BLUE).
                    width(10);

            for (int i = 0; i < route.points.size(); i++)
                polylineOptions.add(route.points.get(i));

            polylinePaths.add(mMap.addPolyline(polylineOptions));
        }

        message = "Distance is " + distance + ".\n Duration is " + duration + ".";
        Toast.makeText(RunActivity.this, message, Toast.LENGTH_LONG).show();

        updateTotalDistanceRanDatabase();

        updateTotalDurationOfExerciseDatabase();

        updateTotalCaloriesBurnedDatabase();
    }

    private void updateTotalDistanceRanDatabase() {
        totalDistanceRanDatabase = name.child("Total Distance Ran");
        totalDistanceRanDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Reading value of Total Calories Burned from database.
                totalDistanceRan = Double.parseDouble(dataSnapshot.getValue().toString());
                // Incrementing the totalCaloriesBurned.
                totalDistanceRan += distance;
                // Writing value of Total Calorie Burned to database.
                name.child("Total Distance Ran").setValue(String.valueOf(totalDistanceRan));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void updateTotalDurationOfExerciseDatabase() {
        totalDurationOfExerciseDatabase = name.child("Total Duration of Exercise");
        totalDurationOfExerciseDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Reading value of Total Calories Burned from database.
                totalDurationOfExercise = Double.parseDouble(dataSnapshot.getValue().toString());
                // Incrementing the totalCaloriesBurned.
                totalDurationOfExercise += duration;
                // Writing value of Total Calorie Burned to database.
                name.child("Total Duration of Exercise").setValue(String.valueOf(totalDurationOfExercise));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void updateTotalCaloriesBurnedDatabase() {
        totalCaloriesBurnedDatabase = name.child("Total Calories Burned");
        totalCaloriesBurnedDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Reading value of Total Calories Burned from database.
                totalCaloriesBurned = Double.parseDouble(dataSnapshot.getValue().toString());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        genderDatabase = name.child("Gender");
        genderDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                gender = dataSnapshot.getValue().toString();
                if (gender.equals("Male")) {
                    caloriesBurned = distance / 1609.3 * 124;
                } else {
                    caloriesBurned = distance / 1609.3 * 105;
                }
                totalCaloriesBurned += caloriesBurned;
                name.child("Total Calories Burned").setValue(String.valueOf(totalCaloriesBurned));

                setUserExercises(timestamp, caloriesBurned, duration);

                updateCaloriesNeededToReachGoalDatabase();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void setUserExercises(String timestamp, Double caloriesBurned, Double duration) {
        // Show timestamps.
        userExercises = name.child("userExercises");
        // Update timestamps.
        Map<String, Object> map = new HashMap<String, Object>();
        userExercises.updateChildren(map);

        // Show user picked exercise, its duration and corresponding calories
        userTimestamp = userExercises.child(timestamp);
        Map<String, Object> map2 = new HashMap<String, Object>();
        map2.put("Calorie", String.valueOf(caloriesBurned));
        map2.put("Duration", String.valueOf(duration));
        map2.put("Sport", "Running");
        userTimestamp.updateChildren(map2);
    }

    private void updateCaloriesNeededToReachGoalDatabase() {
        caloriesNeededToGoalDatabase = name.child("Calories Needed to Reach Goal");
        caloriesNeededToGoalDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                caloriesNeededToGoal = Double.parseDouble(dataSnapshot.getValue().toString());
                caloriesNeededToGoal -= caloriesBurned;
                if (caloriesNeededToGoal <= 0) {
                    caloriesNeededToGoal = 0.0;
                    achievedGoalDialog();
                    name.child("Goal").setValue(String.valueOf(caloriesNeededToGoal));
                }
                name.child("Calories Needed to Reach Goal").setValue(String.valueOf(caloriesNeededToGoal));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void achievedGoalDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(RunActivity.this);
        builder.setTitle("Congratulations!");
        builder.setMessage("You have reached your goal. Success isn't given, it's earned. So keep working hard to achieve it by setting a new goal now!");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                startActivity(new Intent(RunActivity.this, DetailsActivity.class));
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });

        builder.show();
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            Intent i = new Intent(this, MainActivity.class);
            startActivity(i);
        } else if (id == R.id.nav_details) {
            Intent i = new Intent(this, DetailsActivity.class);
            i.putExtra("email", emailEncode);
            i.putExtra("username", username);
            startActivity(i);
        } else if (id == R.id.nav_exercise) {
            Intent i = new Intent(this, ExerciseActivity.class);
            i.putExtra("email", emailEncode);
            i.putExtra("username", username);
            startActivity(i);
        } else if (id == R.id.nav_run) {
            Intent i = new Intent(this, RunActivity.class);
            i.putExtra("email", emailEncode);
            i.putExtra("username", username);
            startActivity(i);
        } else if (id == R.id.nav_chart) {
            Intent i = new Intent(this, ChartActivity.class);
            i.putExtra("email", emailEncode);
            i.putExtra("username", username);
            startActivity(i);
        } else if (id == R.id.nav_chat) {
            Intent i = new Intent(this, ChatHouseActivity.class);
            i.putExtra("email", emailEncode);
            i.putExtra("username", username);
            startActivity(i);
        } else if (id == R.id.nav_camera) {
            openCamera();
        } else if (id == R.id.nav_gallery) {
            openGallery();
        } else if (id == R.id.nav_change_password) {
            Intent i = new Intent(this, DashBoard.class);
            i.putExtra("email", emailEncode);
            i.putExtra("username", username);
            startActivity(i);
        } else if (id == R.id.nav_logout) {
            logoutDialog();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void logoutDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(RunActivity.this);
        builder.setTitle("Logout");
        builder.setMessage("Are you sure?");

        builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // Int Firebase
                auth = FirebaseAuth.getInstance();
                auth.signOut();
                if (auth.getCurrentUser() == null) {
                    startActivity(new Intent(RunActivity.this, LoginActivity.class));
                    finish();
                }
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });

        builder.show();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private void openCamera() {
        startActivity(new Intent("android.media.action.IMAGE_CAPTURE"));
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, 0);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            enableLocationAlertDialog();
            return;
        }
        mMap.isMyLocationEnabled();
        mMap.setMyLocationEnabled(true);
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        if (mLastLocation != null) {
            lat = mLastLocation.getLatitude();
            lng = mLastLocation.getLongitude();

            LatLng loc = new LatLng(lat, lng);
            mMap.addMarker(new MarkerOptions().position(loc).title("New Marker"));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(loc));
        }
    }

    private void enableLocationAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Oops!");
        builder.setMessage("It seems you did not give us permission to use your location. Please enable location to use this feature.");

        builder.setPositiveButton("CLOSE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent(RunActivity.this, MainActivity.class);
                intent.putExtra("email", emailEncode);
                intent.putExtra("username", username);
                startActivity(intent);
            }
        });

        builder.show();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 16);
        mMap.animateCamera(cameraUpdate);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            enableLocationAlertDialog();
            return;
        }
        locationManager.removeUpdates(this);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
