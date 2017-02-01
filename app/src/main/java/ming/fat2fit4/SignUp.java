package ming.fat2fit4;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

import static ming.fat2fit4.Modules.EncoderDecoder.EncodeString;

/**
 * Created by Ernie&Ming on 28-Jan-17.
 */

public class SignUp extends AppCompatActivity implements View.OnClickListener {

    private DatabaseReference root;
    private DatabaseReference exercises;
    private DatabaseReference sportJSON;
    private DatabaseReference users;
    private DatabaseReference name;
    private DatabaseReference userExercises;
    private DatabaseReference userTimestamp;
    private DatabaseReference totalCaloriesBurnedDatabase;
    private DatabaseReference totalDurationOfExerciseDatabase;
    private DatabaseReference caloriesNeededToGoalDatabase;
    private DatabaseReference ageDatabase;
    private DatabaseReference genderDatabase;
    private DatabaseReference heightDatabase;
    private DatabaseReference weightDatabase;
    private Button btnSignup;
    private TextView btnLogin, btnForgotPass;
    private EditText input_email, input_pass, input_username;
    RelativeLayout activity_sign_up;

    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        // View
        btnSignup = (Button) findViewById(R.id.signup_btn_register);
        btnLogin = (TextView) findViewById(R.id.signup_btn_login);
        btnForgotPass = (TextView) findViewById(R.id.signup_btn_forgot_pass);
        input_email = (EditText) findViewById(R.id.signup_email);
        input_pass = (EditText) findViewById(R.id.signup_password);
        input_username = (EditText) findViewById(R.id.signup_username);
        activity_sign_up = (RelativeLayout) findViewById(R.id.activity_sign_up);

        btnSignup.setOnClickListener(this);
        btnLogin.setOnClickListener(this);
        btnForgotPass.setOnClickListener(this);

        // Init Firebase
        auth = FirebaseAuth.getInstance();
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.signup_btn_login) {
            startActivity(new Intent(SignUp.this, LoginActivity.class));
        } else if (view.getId() == R.id.signup_btn_forgot_pass) {
            startActivity(new Intent(SignUp.this, ForgotPassword.class));
        } else if (view.getId() == R.id.signup_btn_register){
            SignUpUser(input_email.getText().toString(), input_pass.getText().toString());
        }
    }

    private void SignUpUser(String email, String password) {
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
            if(!task.isSuccessful()) {
                Toast.makeText(SignUp.this, "Error: "+task.getException(), Toast.LENGTH_LONG).show();
//                    Snackbar.make(activity_sign_up, "Error: "+task.getException(), Snackbar.LENGTH_SHORT).show();
            } else {
                Toast.makeText(SignUp.this, "Register success. ", Toast.LENGTH_LONG).show();
//                    Snackbar.make(activity_sign_up, "Register success. ", Snackbar.LENGTH_SHORT).show();
                createUserInDatabase();
                Intent i = new Intent(SignUp.this, MainActivity.class);
                i.putExtra("username",input_username.getText().toString());
                i.putExtra("email",input_email.getText().toString());
                startActivity(i);
            }
            }
        });
    }

    private void createUserInDatabase() {
        // Shows root, i.e. users and exercises.
        root = FirebaseDatabase.getInstance().getReference().getRoot();
        // Show user, i.e. Chungming, Yuxuan, etc.
        users = root.child("users");
        name = users.child(EncodeString(input_email.getText().toString()));
        Map<String,Object> map = new HashMap<String, Object>();
        map.put("Age", String.valueOf(0));
        map.put("Calories Needed to Reach Goal", String.valueOf(0.0));
        map.put("Gender", "Male");
        map.put("Goal", String.valueOf(0.0));
        map.put("Height", String.valueOf(0));
        map.put("Total Calories Burned", String.valueOf(0.0));
        map.put("Total Distance Ran", String.valueOf(0.0));
        map.put("Total Duration of Exercise", String.valueOf(0.0));
        map.put("Username", input_username.getText().toString());
        map.put("Weight", String.valueOf(0.0));
        name.updateChildren(map);
    }

}
