package com.example.omd.friendlychat.Activities;

import android.content.Intent;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.omd.friendlychat.Fragments.LoginFragment;
import com.example.omd.friendlychat.Fragments.RegisterFragment;
import com.example.omd.friendlychat.R;
import com.google.firebase.auth.FirebaseAuth;

public class Login_Register extends AppCompatActivity {

    private Button loginBtn;
    private Button registerBtn;
    private TextView text_logo;
    FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login__register);
        getSupportFragmentManager().beginTransaction().add(R.id.login_register_fragment_container,new LoginFragment()).commit();

        mAuth = FirebaseAuth.getInstance();
        loginBtn = (Button) findViewById(R.id.loginBtn);
        registerBtn = (Button) findViewById(R.id.registerBtn);
        text_logo = (TextView) findViewById(R.id.txtlogo);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginBtn.setBackgroundResource(R.drawable.loginbutton_normal_background);
                loginBtn.setTextColor(Color.WHITE);
                registerBtn.setBackgroundResource(R.drawable.registerbutton_normal_background);
                registerBtn.setTextColor(ContextCompat.getColor(Login_Register.this,R.color.colorPrimary));
                text_logo.setText("Login");
                getSupportFragmentManager().beginTransaction().replace(R.id.login_register_fragment_container,new LoginFragment()).commit();


            }
        });
        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerBtn.setBackgroundResource(R.drawable.registerbutton_selected_background);
                registerBtn.setTextColor(Color.WHITE);
                loginBtn.setBackgroundResource(R.drawable.loginbutton_unselected_background);
                loginBtn.setTextColor(ContextCompat.getColor(Login_Register.this,R.color.colorPrimary));
                text_logo.setText("Registration");
                getSupportFragmentManager().beginTransaction().replace(R.id.login_register_fragment_container,new RegisterFragment()).commit();


            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mAuth.getCurrentUser()!=null){
            startActivity(new Intent(Login_Register.this,MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
        }

    }
}
