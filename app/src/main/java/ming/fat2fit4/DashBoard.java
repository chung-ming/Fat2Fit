package ming.fat2fit4;

/**
 * Created by Ernie&Ming on 28-Jan-17.
 */

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class DashBoard extends AppCompatActivity implements View.OnClickListener{

    private TextView txtWelcome;
    private EditText input_new_password;
    private Button btnChangePass;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        //View
        txtWelcome = (TextView) findViewById(R.id.dashboard_welcome);
        input_new_password = (EditText) findViewById(R.id.dashboard_new_password);
        btnChangePass = (Button) findViewById(R.id.dashboard_btn_change_pass);

        btnChangePass.setOnClickListener(this);

        // Int Firebase
        auth = FirebaseAuth.getInstance();

        // Session check
        if(auth.getCurrentUser() != null) {
            txtWelcome.setText("Welcome, "+auth.getCurrentUser().getEmail());
        }
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.dashboard_btn_change_pass) {
            changePassword(input_new_password.getText().toString());
        }
    }

    private void changePassword(String newPassword) {
        FirebaseUser user = auth.getCurrentUser();
        user.updatePassword(newPassword).addOnCompleteListener(this, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()) {
                    Toast.makeText(DashBoard.this, "Password changed successful.", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(DashBoard.this, "Error, please try again.", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}



//public class DashBoard extends AppCompatActivity implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener {
//
//    private TextView txtWelcome;
//    private EditText input_new_password;
//    private Button btnChangePass;
//    private Button toastButton;
//    RelativeLayout activity_dashboard;
//    private FirebaseAuth auth;
//    private String emailEncode = "";
//    private String username = "";
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_dashboard);
//
//        //View
//        txtWelcome = (TextView) findViewById(R.id.dashboard_welcome);
//        input_new_password = (EditText) findViewById(R.id.dashboard_new_password);
//        btnChangePass = (Button) findViewById(R.id.dashboard_btn_change_pass);
//        activity_dashboard = (RelativeLayout) findViewById(R.id.activity_dashboard);
//        toastButton = (Button) findViewById(R.id.toastButton);
//
//        // Load Navigation Drawer
////        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
////        navigationView.setNavigationItemSelectedListener(this);
//
//        // Load Toolbar
//        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
//        setSupportActionBar(myToolbar);
//
//        btnChangePass.setOnClickListener(this);
//        toastButton.setOnClickListener(this);
//
//        // Int Firebase
//        auth = FirebaseAuth.getInstance();
//        auth.getCurrentUser().getEmail();
//
//        username = getIntent().getExtras().get("username").toString();
//
//        // Session check
//        if(auth.getCurrentUser() != null) {
//            txtWelcome.setText("Welcome, "+username);
//        }
//    }
//
//    @Override
//    public void onClick(View view) {
//        if(view.getId() == R.id.dashboard_btn_change_pass) {
//            changePassword(input_new_password.getText().toString());
//        }
//        else if(view.getId() == R.id.toastButton) {
//            String message = "Password is "+input_new_password.getText().toString();
//            Toast.makeText(this, message, Toast.LENGTH_LONG).show();
//        }
//    }
//
//    private void logoutUser() {
//        auth.signOut();
//        if(auth.getCurrentUser() == null) {
//            startActivity(new Intent(DashBoard.this, LoginActivity.class));
//            finish();
//        }
//    }
//
//    private void changePassword(String newPassword) {
//        FirebaseUser user = auth.getCurrentUser();
//        user.updatePassword(newPassword).addOnCompleteListener(this, new OnCompleteListener<Void>() {
//            @Override
//            public void onComplete(@NonNull Task<Void> task) {
//                if(task.isSuccessful()) {
//                    Toast.makeText(DashBoard.this, "Password changed", Toast.LENGTH_LONG).show();
////                    Snackbar.make(activity_dashboard, "Password changed", Snackbar.LENGTH_SHORT).show();
//                } else {
//                    Toast.makeText(DashBoard.this, "Error, please try again.", Toast.LENGTH_LONG).show();
//                }
//            }
//        });
//    }
//
//    @SuppressWarnings("StatementWithEmptyBody")
//    @Override
//    public boolean onNavigationItemSelected(MenuItem item) {
//        // Handle navigation view item clicks here.
//        int id = item.getItemId();
//
//        if(id == R.id.nav_home) {
//            Intent i = new Intent(this, MainActivity.class);
//            startActivity(i);
//        } else if(id == R.id.nav_details) {
//            Intent i = new Intent(this, DetailsActivity.class);
//            i.putExtra("email",emailEncode);
//            i.putExtra("username",username);
//            startActivity(i);
//        } else if(id == R.id.nav_exercise) {
//            Intent i = new Intent(this, ExerciseActivity.class);
//            i.putExtra("email",emailEncode);
//            i.putExtra("username",username);
//            startActivity(i);
//        } else if(id == R.id.nav_run) {
//            Intent i = new Intent(this, RunActivity.class);
//            i.putExtra("email",emailEncode);
//            i.putExtra("username",username);
//            startActivity(i);
//        } else if(id == R.id.nav_chat) {
//            Intent i = new Intent(this, ChatHouseActivity.class);
//            i.putExtra("email",emailEncode);
//            i.putExtra("username",username);
//            startActivity(i);
//        } else if(id == R.id.nav_camera) {
//            openCamera();
//        } else if(id == R.id.nav_gallery) {
//            openGallery();
//        } else if(id == R.id.nav_change_password) {
//            Intent i = new Intent(this, DashBoard.class);
//            i.putExtra("email",emailEncode);
//            i.putExtra("username",username);
//            startActivity(i);
//        } else if(id == R.id.nav_logout) {
//            logoutDialog();
//        }
//
//        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
//        drawer.closeDrawer(GravityCompat.START);
//        return true;
//    }
//
//    private void logoutDialog() {
//        AlertDialog.Builder builder = new AlertDialog.Builder(DashBoard.this);
//        builder.setTitle("Logout");
//        builder.setMessage("Are you sure?");
//
//        builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialogInterface, int i) {
//                // Int Firebase
//                auth = FirebaseAuth.getInstance();
//                auth.signOut();
//                if(auth.getCurrentUser() == null) {
//                    startActivity(new Intent(DashBoard.this, LoginActivity.class));
//                    finish();
//                }
//            }
//        });
//
//        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialogInterface, int i) {
//                dialogInterface.cancel();
//            }
//        });
//
//        builder.show();
//    }
//
//    @Override
//    public void onBackPressed() {
//        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
//        if (drawer.isDrawerOpen(GravityCompat.START)) {
//            drawer.closeDrawer(GravityCompat.START);
//        } else {
//            super.onBackPressed();
//        }
//    }
//
//    private void openCamera() {
//        startActivity(new Intent("android.media.action.IMAGE_CAPTURE"));
//    }
//
//    private void openGallery() {
//        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//        startActivityForResult(intent, 0);
//    }
//}
