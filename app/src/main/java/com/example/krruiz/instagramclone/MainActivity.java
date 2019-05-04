package com.example.krruiz.instagramclone;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.krruiz.instagramclone.Model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {


    private EditText inputUsername;
    private EditText inputPassword;

    private Button signIn;
    private Button logIn;

    private ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        inputUsername = findViewById(R.id.input_username);
        inputPassword = findViewById(R.id.input_password);

        signIn = findViewById(R.id.buttonSignup);
        logIn = findViewById(R.id.buttonLogin);

        loadingBar = new ProgressDialog(this);

        logIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser();
            }
        });

        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createAccount();
            }
        });

    }

    private void loginUser() {

        final String username = inputUsername.getText().toString();
        final String password = inputPassword.getText().toString();

        InputMethodManager mtdo = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        mtdo.hideSoftInputFromWindow(signIn.getWindowToken(),0);

        if (username.equals("")){
            Toast.makeText(this, "Username is required", Toast.LENGTH_SHORT).show();
        } else if (password.equals("")){
            Toast.makeText(this, "Please write your password", Toast.LENGTH_SHORT).show();

        } else {

            loadingBar.setTitle("Login Account");
            loadingBar.setMessage("Please wait");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();
            AllowAccessToAccount(username, password);

        }
    }

    private void AllowAccessToAccount(final String username, String password) {

        final DatabaseReference rootRef;
        rootRef = FirebaseDatabase.getInstance().getReference();

        final String numberTT = username;
        final String passwordTT = password;

        rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.child("Users").child(numberTT).exists()) {

                    User userData = dataSnapshot.child("Users").child(numberTT).getValue(User.class);
                    String passwordAux = userData.getPassword();

                    if (passwordAux.equals(passwordTT)) {

                        Prevalent.currentUser = userData;

                        Toast.makeText(getApplicationContext(), "Login sucessfully", Toast.LENGTH_LONG).show();
                        loadingBar.dismiss();
                        Intent intent = new Intent(getApplicationContext(), ImagesShowActivity.class);
                        startActivity(intent);

                    } else {
                        Toast.makeText(getApplicationContext(), "Password Incorrect", Toast.LENGTH_LONG).show();
                        loadingBar.dismiss();
                    }

                } else {

                    Toast.makeText(getApplicationContext(), "Incorrect ID", Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void createAccount() {

        final String username = inputUsername.getText().toString();
        final String password = inputPassword.getText().toString();

        InputMethodManager mtdo = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        mtdo.hideSoftInputFromWindow(signIn.getWindowToken(),0);

        if (username.equals("")){
            Toast.makeText(getApplicationContext(), "Please input a username", Toast.LENGTH_LONG).show();

        }else if (password.equals("")){
            Toast.makeText(getApplicationContext(), "Please input a password", Toast.LENGTH_LONG).show();

        } else {

            loadingBar.setTitle("Creating Account");
            loadingBar.setMessage("Please wait");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();

            final DatabaseReference rootRef;
            rootRef = FirebaseDatabase.getInstance().getReference();

            rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    if (!dataSnapshot.child("Users").child(username).exists()){
                        HashMap<String, Object> userdataMap = new HashMap<>();
                        userdataMap.put("username", username);
                        userdataMap.put("password", password);

                        rootRef.child("Users").child(username).updateChildren(userdataMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                if (task.isSuccessful()){

                                    Toast.makeText(MainActivity.this, "Your account was created sucessfully", Toast.LENGTH_LONG).show();
                                    loadingBar.dismiss();

                                } else {
                                    loadingBar.dismiss();
                                    Toast.makeText(MainActivity.this, " Network Error: Please try again", Toast.LENGTH_LONG).show();
                                }
                            }
                        });

                    }else{
                        Toast.makeText(getApplicationContext(), "This username already exists", Toast.LENGTH_LONG).show();
                        loadingBar.dismiss();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });


        }



    }
}
