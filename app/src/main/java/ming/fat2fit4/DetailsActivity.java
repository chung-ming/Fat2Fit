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
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Ernie&Ming on 17-Jan-17.
 */

public class DetailsActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private FirebaseAuth auth;
    private DatabaseReference root;
    private DatabaseReference exercises;
    private DatabaseReference sportJSON;
    private DatabaseReference users;
    private DatabaseReference name;
    private DatabaseReference userExercises;
    private DatabaseReference userTimestamp;
    private DatabaseReference totalCaloriesBurnedDatabase;
    private DatabaseReference totalDurationOfExerciseDatabase;
    private DatabaseReference ageDatabase;
    private DatabaseReference genderDatabase;
    private DatabaseReference heightDatabase;
    private DatabaseReference weightDatabase;
    private DatabaseReference goalDatabase;
    private SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
    private String timestamp = s.format(new Date());
    private String emailEncode = "";
    private String username = "";
    private Button buttonSendToDatabase, buttonGoal;
    private EditText editTextAge, editTextHeight, editTextWeight, editTextGoal;
    private RadioGroup radioGender;
    private RadioButton radioButtonGender;
    private RadioButton radioButtonMale;
    private RadioButton radioButtonFemale;
    private TextView textViewBMI;
    private TextView textViewCalorie;
    private String gender = "", bmiStatment = "";
    private int age, height;
    private double goal, weight, bmiNum, calorie = 0;
    private FirebaseAnalytics mFirebaseAnalytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        //=========================================================================
        buttonSendToDatabase = (Button) findViewById(R.id.buttonSendToDatabase);
        buttonGoal = (Button) findViewById(R.id.buttonGoal);
        editTextAge = (EditText) findViewById(R.id.editTextAge);
        editTextHeight = (EditText) findViewById(R.id.editTextHeight);
        editTextWeight = (EditText) findViewById(R.id.editTextWeight);
        editTextGoal = (EditText) findViewById(R.id.editTextGoal);
        radioGender = (RadioGroup) findViewById(R.id.radioGender);
        radioButtonMale = (RadioButton) findViewById(R.id.radioButtonMale);
        radioButtonFemale = (RadioButton) findViewById(R.id.radioButtonFemale);
        textViewBMI = (TextView) findViewById(R.id.textViewBMI);
        textViewCalorie = (TextView) findViewById(R.id.textViewCalorie);

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

        // Shows root, i.e. users and exercises.
        root = FirebaseDatabase.getInstance().getReference().getRoot();
        // Show user, i.e. Chungming, Yuxuan, etc.
        users = root.child("users");
        // Show Age, Weight, Height, etc.
        name = users.child(emailEncode);
        // Show timestamps.
        userExercises = name.child("userExercises");

        ageDatabase = name.child("Age");
        ageDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Read value of Age from the database.
                age = Integer.parseInt(dataSnapshot.getValue().toString());
                // Display age value onto editTextAge.
                editTextAge.setText(Integer.toString(age));
            }
            @Override
            public void onCancelled(DatabaseError error) {

            }
        });

        genderDatabase = name.child("Gender");
        genderDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Read value of Gender from the database.
                gender = dataSnapshot.getValue().toString();
                // Display gender selection in radio button.
                if(gender.equals("Male")){
                    radioButtonMale.setChecked(true);
                    radioButtonFemale.setChecked(false);
                } else {
                    radioButtonMale.setChecked(false);
                    radioButtonFemale.setChecked(true);
                }
            }
            @Override
            public void onCancelled(DatabaseError error) {

            }
        });

        heightDatabase = name.child("Height");
        heightDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Read value of Age from the database.
                height = Integer.parseInt(dataSnapshot.getValue().toString());
                // Display age value onto editTextAge.
                editTextHeight.setText(Integer.toString(height));
            }
            @Override
            public void onCancelled(DatabaseError error) {

            }
        });

        weightDatabase = name.child("Weight");
        weightDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Read value of Age from the database.
                weight = Double.parseDouble(dataSnapshot.getValue().toString());
                // Display age value onto editTextAge.
                editTextWeight.setText(Double.toString(weight));
            }
            @Override
            public void onCancelled(DatabaseError error) {

            }
        });

        goalDatabase = name.child("Goal");
        goalDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Read value of Age from the database.
                goal = Double.parseDouble(dataSnapshot.getValue().toString());
                // Display age value onto editTextAge.
                editTextGoal.setText(Double.toString(goal));
            }
            @Override
            public void onCancelled(DatabaseError error) {

            }
        });

        // Obtain the FirebaseAnalytics instance.
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        sendToDatabase();

        sendGoal();
    }

    private void sendToDatabase() {
        buttonSendToDatabase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name.child("Age").setValue(editTextAge.getText().toString());
                name.child("Height").setValue(editTextHeight.getText().toString());
                name.child("Weight").setValue(editTextWeight.getText().toString());

                int selectedId = radioGender.getCheckedRadioButtonId();
                radioButtonGender = (RadioButton) findViewById(selectedId);
                name.child("Gender").setValue(radioButtonGender.getText());

//                String summary = "Email is "+emailEncode+".\nUsername is "+username+".";
//                Toast.makeText(getApplicationContext(), summary, Toast.LENGTH_LONG).show();

                textViewBMI.setText(calculateBMI(weight, height));
                textViewCalorie.setText("Your recommended daily calorie intake is " +
                        calculateCalorie(gender, weight, height, age) + " calories.");
            }
        });
    }

    private void sendGoal() {
        buttonGoal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name.child("Goal").setValue(editTextGoal.getText().toString());
                name.child("Calories Needed to Reach Goal").setValue(editTextGoal.getText().toString());
            }
        });
    }

    private String calculateBMI(double weight, int height) {
        bmiNum = weight / Math.pow(height / 100.0, 2);

        if (bmiNum < 18.5) {
            bmiStatment = "Your BMI is classified as Underweight.";
        } else if (bmiNum >= 18.5 || bmiNum <= 24.9) {
            bmiStatment = "Your BMI is classified as Normal weight.";
        } else if (bmiNum >= 25 || bmiNum <= 29.9) {
            bmiStatment = "Your BMI is classified as Overweight.";
        } else if (bmiNum >= 30 || bmiNum <= 39.9) {
            bmiStatment = "Your BMI is classified as Obese.";
        } else if (bmiNum >= 40) {
            bmiStatment = "Your BMI is classified as Morbidly Obese.";
        }
        return bmiStatment;
    }

    private double calculateCalorie(String gender, double weight, int height, int age) {
        if (gender == "Male") {
            calorie = 10 * weight + 6.25 * height - 5 * age + 5;
        } else {
            calorie = 10 * weight + 6.25 * height - 5 * age - 161;
        }
        return calorie;
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
        AlertDialog.Builder builder = new AlertDialog.Builder(DetailsActivity.this);
        builder.setTitle("Logout");
        builder.setMessage("Are you sure?");

        builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // Int Firebase
                auth = FirebaseAuth.getInstance();
                auth.signOut();
                if(auth.getCurrentUser() == null) {
                    startActivity(new Intent(DetailsActivity.this, LoginActivity.class));
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