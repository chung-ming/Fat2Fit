package ming.fat2fit4;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static ming.fat2fit4.Modules.EncoderDecoder.EncodeString;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private FirebaseAuth auth;
    private DatabaseReference root;
    private DatabaseReference exercises;
    private DatabaseReference sportJSON;
    private DatabaseReference users;
    private DatabaseReference name;
    private DatabaseReference userExercises;
    private DatabaseReference userTimestamp;
    private DatabaseReference totalCaloriesBurnedDatabase;
    private DatabaseReference totalDistanceRanDatabase;
    private DatabaseReference totalDurationOfExerciseDatabase;
    private DatabaseReference caloriesNeededToGoalDatabase;
    private DatabaseReference usernameDatabase;
    private SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
    private String timestamp = s.format(new Date());
    private String username = "";
    private String emailEncode = "";
    private AdView mAdView;
    private TextView textViewCalories;
    private TextView textViewDistance;
    private TextView textViewDuration;
    private TextView textViewGoal;
    private TextView homeUsername;
    private ImageView androidHi;
    private double totalCaloriesBurned = 0.0, totalDistanceRan = 0.0, totalDurationOfExercise = 0.0, caloriesNeededToGoal = 0.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textViewCalories = (TextView) findViewById(R.id.textViewCalories);
        textViewDistance = (TextView) findViewById(R.id.textViewDistance);
        textViewDuration = (TextView) findViewById(R.id.textViewDuration);
        textViewGoal = (TextView) findViewById(R.id.textViewGoal);
        homeUsername = (TextView) findViewById(R.id.homeUsername);
        androidHi = (ImageView) findViewById(R.id.android_hi);

        // Int Firebase
        auth = FirebaseAuth.getInstance();
        // Get user's email address. Note: Firebase does not allow '.', '#', '$', '[', or ']'
        if(auth.getCurrentUser() != null){
            emailEncode = EncodeString(auth.getCurrentUser().getEmail());
        } else {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }

        // Load Navigation Drawer
        final NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
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

        // Shows root, i.e. users and exercises.
        root = FirebaseDatabase.getInstance().getReference().getRoot();
        // Show user, i.e. Chungming, Yuxuan, etc.
        users = root.child("users");
        // Show Age, Weight, Height, etc.
        name = users.child(emailEncode);
        // Show timestamps.
        userExercises = name.child("userExercises");
        // Display Hi + username
        displayUsername();
        // Display Total Calories Burned
        displayTotalCaloriesBurned();
        // Display Total Distance Ran
        displayTotalDistanceRan();
        // Display Total Duration of Exercise
        displayTotalDurationOfExercise();
        // Display Calories Needed to Reach Goal
        displayCaloriesNeededToReachGoal();

        // generate Dates
        Calendar calendar = Calendar.getInstance();
        Date d1 = calendar.getTime();
        calendar.add(Calendar.DATE, 1);
        Date d2 = calendar.getTime();
        calendar.add(Calendar.DATE, 1);
        Date d3 = calendar.getTime();

        // Load banner ads
        loadAdMob();
    }

    private void displayUsername() {
        usernameDatabase = name.child("Username");
        usernameDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Reading value of Total Calories Burned from database.
                username = dataSnapshot.getValue().toString();
                String textUsername = "Hi "+username+",";
                homeUsername.setText(textUsername);
                androidHi.setVisibility(View.VISIBLE);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void displayTotalCaloriesBurned() {
        totalCaloriesBurnedDatabase = name.child("Total Calories Burned");
        totalCaloriesBurnedDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Reading value of Total Calories Burned from database.
                totalCaloriesBurned = Double.parseDouble(dataSnapshot.getValue().toString());
                String message = "Total calories burned so far is "+(int)totalCaloriesBurned+" cal.";
                textViewCalories.setText(message);
                textViewCalories.setVisibility(View.VISIBLE);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void displayTotalDistanceRan() {
        totalDistanceRanDatabase = name.child("Total Distance Ran");
        totalDistanceRanDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Reading value of Total Calories Burned from database.
                totalDistanceRan = Double.parseDouble(dataSnapshot.getValue().toString());
                String message = "Total distance you ran so far is "+(int)totalDistanceRan+" m.";
                textViewDistance.setText(message);
                textViewDistance.setVisibility(View.VISIBLE);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void displayTotalDurationOfExercise() {
        totalDurationOfExerciseDatabase = name.child("Total Duration of Exercise");
        totalDurationOfExerciseDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Reading value of Total Calories Burned from database.
                totalDurationOfExercise = Double.parseDouble(dataSnapshot.getValue().toString());
                String message = "Total duration of exercise so far is "+(int)totalDurationOfExercise+" m.";
                textViewDuration.setText(message);
                textViewDuration.setVisibility(View.VISIBLE);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void displayCaloriesNeededToReachGoal() {
        caloriesNeededToGoalDatabase = name.child("Calories Needed to Reach Goal");
        caloriesNeededToGoalDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String message = "";
                // Reading value of Total Calories Burned from database.
                caloriesNeededToGoal = Double.parseDouble(dataSnapshot.getValue().toString());

                if(caloriesNeededToGoal == 0.0) {
                    message = "Go to Personal Details to set a new goal now!";
                    achievedGoalDialog();
                }
                else {
                    message = "Burn "+(int)caloriesNeededToGoal+" cal more to reach your goal.";
                }

                textViewGoal.setText(message);
                textViewGoal.setVisibility(View.VISIBLE);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void achievedGoalDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Hey you..!");
        builder.setMessage("Success isn't given, it's earned. So keep working hard to achieve it by setting a new goal now!");

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent(MainActivity.this, DetailsActivity.class);
                intent.putExtra("email",emailEncode);
                intent.putExtra("username",username);
                startActivity(intent);
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

        if(id == R.id.nav_home) {
            Intent i = new Intent(this, MainActivity.class);
            startActivity(i);
        } else if(id == R.id.nav_details) {
            Intent i = new Intent(this, DetailsActivity.class);
            i.putExtra("email",emailEncode);
            i.putExtra("username",username);
            startActivity(i);
        } else if(id == R.id.nav_exercise) {
            Intent i = new Intent(this, ExerciseActivity.class);
            i.putExtra("email",emailEncode);
            i.putExtra("username",username);
            startActivity(i);
        } else if(id == R.id.nav_run) {
            Intent i = new Intent(this, RunActivity.class);
            i.putExtra("email",emailEncode);
            i.putExtra("username",username);
            startActivity(i);
        } else if(id == R.id.nav_chart) {
            Intent i = new Intent(this, ChartActivity.class);
            i.putExtra("email",emailEncode);
            i.putExtra("username",username);
            startActivity(i);
        } else if(id == R.id.nav_chat) {
            Intent i = new Intent(this, ChatHouseActivity.class);
            i.putExtra("email",emailEncode);
            i.putExtra("username",username);
            startActivity(i);
        } else if(id == R.id.nav_camera) {
            openCamera();
        } else if(id == R.id.nav_gallery) {
            openGallery();
        } else if(id == R.id.nav_change_password) {
            Intent i = new Intent(this, DashBoard.class);
            i.putExtra("email",emailEncode);
            i.putExtra("username",username);
            startActivity(i);
        } else if(id == R.id.nav_logout) {
            logoutDialog();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void logoutDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Logout");
        builder.setMessage("Are you sure?");

        builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // Int Firebase
                auth = FirebaseAuth.getInstance();
                auth.signOut();
                if(auth.getCurrentUser() == null) {
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
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
            moveTaskToBack(true);
            //super.onBackPressed();
        }
    }

    private void openCamera() {
        startActivity(new Intent("android.media.action.IMAGE_CAPTURE"));
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, 0);
    }

    private void loadAdMob() {
        mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }

    protected void onStart() {
        super.onStart();
    }

    protected void onResume() {
        super.onResume();
    }

    protected void onPause() {
        super.onPause();
    }

    protected void onStop() {
        super.onStop();
    }

}