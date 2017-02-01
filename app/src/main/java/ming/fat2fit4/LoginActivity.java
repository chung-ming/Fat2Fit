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

/**
 * Created by Ernie&Ming on 28-Jan-17.
 */

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btnLogin;
    private EditText input_email, input_password;
    private TextView btnSignup, btnForgotPass;
    RelativeLayout activity_main;

    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // View
        btnLogin = (Button) findViewById(R.id.login_btn_login);
        input_email = (EditText) findViewById(R.id.login_email);
        input_password = (EditText) findViewById(R.id.login_password);
        btnSignup = (TextView) findViewById(R.id.login_btn_signup);
        btnForgotPass = (TextView) findViewById(R.id.login_btn_forgot_password);
        activity_main = (RelativeLayout) findViewById(R.id.activity_main);

        btnSignup.setOnClickListener(this);
        btnForgotPass.setOnClickListener(this);
        btnLogin.setOnClickListener(this);

        // Init Firebase Auth
        auth = FirebaseAuth.getInstance();

        // Check already session, if ok -> MainActivity
        if(auth.getCurrentUser() != null) {
            Intent i = new Intent(LoginActivity.this, MainActivity.class);
            i.putExtra("email",auth.getCurrentUser().getEmail());
            startActivity(i);
        }
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.login_btn_forgot_password){
            startActivity(new Intent(LoginActivity.this, ForgotPassword.class));
            finish();
        } else if(view.getId() == R.id.login_btn_signup){
            startActivity(new Intent(LoginActivity.this, SignUp.class));
            finish();
        } else if(view.getId() == R.id.login_btn_login){
            loginUser(input_email.getText().toString(), input_password.getText().toString());
        }
    }

    private void loginUser(String email, final String password) {
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(!task.isSuccessful()) {
                    if(password.length() < 6) {
                        Toast.makeText(LoginActivity.this, "Password length must be over 6", Toast.LENGTH_LONG).show();
                    }
                } else {
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                }
            }
        });
    }
}
