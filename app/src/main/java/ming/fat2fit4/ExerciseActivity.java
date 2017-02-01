package ming.fat2fit4;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * Created by Ernie&Ming on 17-Jan-17.
 */

public class ExerciseActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private FirebaseAuth auth;
    private DatabaseReference root = FirebaseDatabase.getInstance().getReference().getRoot();
    private DatabaseReference exercises = root.child("exercises");
    private DatabaseReference sportJSON;
    private DatabaseReference users = root.child("users");
    private DatabaseReference name;
    private DatabaseReference userExercises;
    private DatabaseReference userTimestamp;
    private DatabaseReference totalCaloriesBurnedDatabase;
    private DatabaseReference totalDurationOfExerciseDatabase;
    private DatabaseReference caloriesNeededToGoalDatabase;
    private SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
    private String timestamp = s.format(new Date());
    private AdView mAdView;
    private ListView listView;
    private ArrayAdapter<String> arrayAdapter;
    private ArrayList<String> list_of_exercises = new ArrayList<>();
    private String duration = "";
    private String calorie = "";
    private String time = "";
    private String sport = "";
    private String emailEncode = "";
    private String username = "";
    private Double totalDurationOfExercise = 0.0;
    private Double totalCaloriesBurned = 0.0;
    private Double caloriesBurned = 0.0;
    private Double caloriesNeededToGoal = 0.0;
//    private Button toastButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise);

        // Initialize
        listView = (ListView) findViewById(R.id.listView);
        arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, list_of_exercises);
        listView.setAdapter(arrayAdapter);
//        toastButton = (Button) findViewById(R.id.toastButton);

        emailEncode = getIntent().getExtras().get("email").toString();
        username = getIntent().getExtras().get("username").toString();

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

        name = users.child(emailEncode);
        totalCaloriesBurnedDatabase = name.child("Total Calories Burned");
        totalDurationOfExerciseDatabase = name.child("Total Duration of Exercise");
        caloriesNeededToGoalDatabase = name.child("Calories Needed to Reach Goal");
        userExercises = name.child("userExercises");
        userTimestamp = userExercises.child(timestamp);
        // Load exercise list
        loadListViewExercises();
        // When a list item is selected.
        selectItemOnListView();
        // Load banner ads
        loadAdMob();

        getTotalCaloriesBurned();
        getCaloriesNeededToGoal();
        getTotalDurationOfExercise();

//        toastButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String message = "Calories burned is "+caloriesBurned+
//                        ".\nTotal calories burned is "+totalCaloriesBurned+
//                        ".\nTotal duration of exercise is "+totalDurationOfExercise+
//                        ".\nCalories needed to reach goal is "+caloriesNeededToGoal+
//                        ".";
//                Toast.makeText(ExerciseActivity.this, message, Toast.LENGTH_LONG).show();
//            }
//        });
    }

    private void loadListViewExercises() {
        exercises.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Set<String> set = new HashSet<String>();
                Iterator i = dataSnapshot.getChildren().iterator();

                while (i.hasNext()){
                    set.add(((DataSnapshot)i.next()).getKey());
                }

                list_of_exercises.clear();
                list_of_exercises.addAll(set);
                // Gets notified when the data in list view has been changed.
                arrayAdapter.notifyDataSetChanged();
                // Sorts data in list view in ascending order.
                arrayAdapter.sort(new Comparator<String>() {
                    @Override
                    public int compare(String lhs, String rhs) {
                        return lhs.compareTo(rhs);
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void selectItemOnListView() {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                // Get string of selected list item
                sport = (String)(listView.getItemAtPosition(i));

                // Init sportJSON as child of activity, i.e. Calorie and Time
                sportJSON = exercises.child(sport);

                // Get calorie and time values
                sportJSON.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Iterator i = dataSnapshot.getChildren().iterator();

                        while (i.hasNext()){
                            calorie = (((DataSnapshot)i.next()).getValue().toString());
                            time = (((DataSnapshot)i.next()).getValue().toString());
                        }

                        // Set the exercise duration
                        setExerciseDuration(calorie, time, sport);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
            }
        });
    }

    private void setExerciseDuration(final String calorie, final String time, final String sport) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Set Duration of "+sport);
        builder.setMessage("Note: "+calorie+" kcal burned per "+time+" mins.");

        final EditText input_field = new EditText(this);
        input_field.setHint("in minutes");
        input_field.setPadding(78, 30, 78, 30);
        builder.setView(input_field);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                duration = input_field.getText().toString();
                setUserExercises(timestamp, duration, sport, caloriesBurned);
                caloriesBurned = calculateCaloriesBurned(duration, time, calorie);

                setTotalCaloriesBurned(caloriesBurned, totalCaloriesBurned);
                setCaloriesNeededToGoal(caloriesBurned, caloriesNeededToGoal);
                setTotalDurationOfExercise(duration, totalDurationOfExercise);
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

    @NonNull
    private Double calculateCaloriesBurned(String duration, String time, String calorie) {
        // Calculate calorie burned.
        Double caloriesBurned = Double.parseDouble(duration) / Double.parseDouble(time) * Double.parseDouble(calorie);
        return caloriesBurned;
    }

    private void setUserExercises(String timestamp, String duration, String sport, Double caloriesBurned) {
        userExercises = name.child("userExercises");
        // Update timestamps.
        Map<String,Object> map = new HashMap<String, Object>();
        userExercises.updateChildren(map);

        // Show user picked exercise, its duration and corresponding calories
        userTimestamp = userExercises.child(timestamp);
        Map<String,Object> map2 = new HashMap<String, Object>();
        map2.put("Calorie", String.valueOf(caloriesBurned));
        map2.put("Duration", duration);
        map2.put("Sport", sport);
        userTimestamp.updateChildren(map2);
    }

    private Double getTotalCaloriesBurned() {
        totalCaloriesBurnedDatabase = name.child("Total Calories Burned");
        totalCaloriesBurnedDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Reading value of Total Calories Burned from database.
                totalCaloriesBurned = Double.parseDouble(dataSnapshot.getValue().toString());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        return totalCaloriesBurned;
    }

    private void setTotalCaloriesBurned(Double caloriesBurned, Double totalCaloriesBurned) {
        totalCaloriesBurned += caloriesBurned;
        name.child("Total Calories Burned").setValue(String.valueOf(totalCaloriesBurned));
    }

    private Double getCaloriesNeededToGoal() {
        caloriesNeededToGoalDatabase = name.child("Calories Needed to Reach Goal");
        caloriesNeededToGoalDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                caloriesNeededToGoal = Double.parseDouble(dataSnapshot.getValue().toString());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        return caloriesNeededToGoal;
    }

    private void setCaloriesNeededToGoal(Double caloriesBurned, Double caloriesNeededToGoal) {
        caloriesNeededToGoal -= caloriesBurned;
        if(caloriesNeededToGoal <= 0) {
            caloriesNeededToGoal = 0.0;
            achievedGoalDialog();
            name.child("Goal").setValue(String.valueOf(caloriesNeededToGoal));
        }
        name.child("Calories Needed to Reach Goal").setValue(String.valueOf(caloriesNeededToGoal));
    }

    private Double getTotalDurationOfExercise() {
        totalDurationOfExerciseDatabase = name.child("Total Duration of Exercise");
        totalDurationOfExerciseDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                totalDurationOfExercise = Double.parseDouble(dataSnapshot.getValue().toString());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        return totalDurationOfExercise;
    }

    private void setTotalDurationOfExercise(String duration, Double totalDurationOfExercise) {
        totalDurationOfExercise += Double.parseDouble(duration);
        name.child("Total Duration of Exercise").setValue(String.valueOf(totalDurationOfExercise));
    }

    private void achievedGoalDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ExerciseActivity.this);
        builder.setTitle("Congratulations!");
        builder.setMessage("You have reached your goal. Success isn't given, it's earned. So keep working hard to achieve it by setting a new goal now!");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                startActivity(new Intent(ExerciseActivity.this, DetailsActivity.class));
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

    private void loadAdMob() {
        mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
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
        AlertDialog.Builder builder = new AlertDialog.Builder(ExerciseActivity.this);
        builder.setTitle("Logout");
        builder.setMessage("Are you sure?");

        builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // Int Firebase
                auth = FirebaseAuth.getInstance();
                auth.signOut();
                if(auth.getCurrentUser() == null) {
                    startActivity(new Intent(ExerciseActivity.this, LoginActivity.class));
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

}
