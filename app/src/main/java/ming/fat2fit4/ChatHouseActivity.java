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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Created by Ernie&Ming on 27-Jan-17.
 */

public class ChatHouseActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

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
    private DatabaseReference ageDatabase;
    private DatabaseReference genderDatabase;
    private DatabaseReference heightDatabase;
    private DatabaseReference weightDatabase;
    private Button add_room;
    private EditText room_name;
    private ListView listView;
    private ArrayAdapter<String> arrayAdapter;
    private ArrayList<String> list_of_rooms = new ArrayList<>();
    private String emailEncode = "";
    private String username = "";
    private AdView mAdView;

    /** TODO: Consider calling
     * Capture persons name at login so that dont need to input name in Alert Dialog
     * Capture persons name to be updated to database
     * Modify database for chat
     * Add icon at nav drawer
     * Implement authentication
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_house);

        add_room = (Button) findViewById(R.id.btn_add_room);
        room_name = (EditText) findViewById(R.id.room_name_edittext);

        listView = (ListView) findViewById(R.id.listView);
        arrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,list_of_rooms);
        listView.setAdapter(arrayAdapter);

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
        users = root.child("community forum");
        // Show Age, Weight, Height, etc.
        name = users.child(emailEncode);
        // Show timestamps.
        userExercises = name.child("userExercises");

//        requestUserName();

        add_room.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Map<String,Object> map = new HashMap<String, Object>();
                map.put(room_name.getText().toString(),"");
                users.updateChildren(map);

                room_name.setText("");
//                String message = "Email is "+emailEncode+".\nUsername is "+username+".";
//                Toast.makeText(ChatHouseActivity.this, message, Toast.LENGTH_LONG).show();
            }
        });

        users.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Set<String> set = new HashSet<String>();
                Iterator i = dataSnapshot.getChildren().iterator();

                while (i.hasNext()){
                    set.add(((DataSnapshot)i.next()).getKey());
                }

                list_of_rooms.clear();
                list_of_rooms.addAll(set);

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

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getApplicationContext(),ChatRoomActivity.class);
                intent.putExtra("roomName",((TextView)view).getText().toString() );
                intent.putExtra("username", username);
                startActivity(intent);
            }
        });

        // Load AdMob
        loadAdMob();
    }

    private void requestUserName() {
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setTitle("Enter name:");
//
//        final EditText input_field = new EditText(this);
//        builder.setView(input_field);
//
//        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialogInterface, int i) {
//            name = input_field.getText().toString();
//            }
//        });
//
//        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialogInterface, int i) {
//            dialogInterface.cancel();
//            request_user_name();
//            }
//        });
//
//        builder.show();
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
        AlertDialog.Builder builder = new AlertDialog.Builder(ChatHouseActivity.this);
        builder.setTitle("Logout");
        builder.setMessage("Are you sure?");

        builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // Int Firebase
                auth = FirebaseAuth.getInstance();
                auth.signOut();
                if(auth.getCurrentUser() == null) {
                    startActivity(new Intent(ChatHouseActivity.this, LoginActivity.class));
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

    private void loadAdMob() {
        mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }
}
