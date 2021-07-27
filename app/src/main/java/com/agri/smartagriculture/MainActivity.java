package com.agri.smartagriculture;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {
    public EditText emailId,password,name;
    public Button btnSignUP;
    public TextView tvSignIn;
    CheckBox showpass;
    FirebaseAuth mFirebaseAuth;
    LoadinActivity loading;
    DatabaseReference reff;
    Member member;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mFirebaseAuth = FirebaseAuth.getInstance();
        name = findViewById(R.id.editTextTextPersonName);
        emailId = findViewById(R.id.editTextTextEmailAddress);
        password = findViewById(R.id.editTextTextPassword);
        btnSignUP = findViewById(R.id.button);
        tvSignIn = findViewById(R.id.textView);
        loading = new LoadinActivity(MainActivity.this);
        showpass=findViewById(R.id.checkBox);
        String mail = emailId.getEditableText().toString();
        member = new Member();
        reff = FirebaseDatabase.getInstance().getReference().child("Members");
//************************************************************Register***********************************************//
        btnSignUP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailId.getText().toString();
                String pwd = password.getText().toString();
                String nam = name.getText().toString();
                if(nam.isEmpty()){
                    name.setError("Please enter your name");
                    name.requestFocus();
                }
                else if(email.isEmpty()){
                    emailId.setError("Please enter Email id");
                    emailId.requestFocus();
                }
                else if(pwd.isEmpty()){
                    password.setError("Please enter your password");
                    password.requestFocus();
                }
                else if(email.isEmpty() && pwd.isEmpty() && nam.isEmpty()){
                    Toast.makeText(MainActivity.this,"Fields Are Empty!",Toast.LENGTH_SHORT).show();
                }
                else if(pwd.length()<8){
                    password.setError("Password is too short");
                    password.requestFocus();
                }
                else if(passValidator(pwd)==false){
                    password.setError("Must contain a uppercase letter,number,lowercase letter and one special symbol");
                }
                else if(!email.isEmpty() && !pwd.isEmpty() && !nam.isEmpty()){
                    mFirebaseAuth.createUserWithEmailAndPassword(email,pwd).addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(!task.isSuccessful()){
                                Toast.makeText(MainActivity.this,task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                            }
                            else{
                                mFirebaseAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task1) {
                                        if(task1.isSuccessful()) {
                                            float temp =-1,hum=-1;
                                            int moist =-1;
                                            member.setName(name.getText().toString().trim());
                                            member.setEmail(emailId.getText().toString().trim());
                                            member.setTemperature(temp);
                                            member.setHumidity(hum);
                                            member.setMoisture(moist);
                                            reff.child(mFirebaseAuth.getCurrentUser().getUid()).setValue(member);
                                            Toast.makeText(MainActivity.this, "verify your email to login", Toast.LENGTH_SHORT).show();
                                            //startLoading();
                                            startActivity(new Intent(MainActivity.this, Login_Activity.class));
                                            finish();



                                        }
                                        else{
                                            Toast.makeText(MainActivity.this,task1.getException().getMessage(),Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });

                            }
                        }
                    });
                }
                else{
                    Toast.makeText(MainActivity.this,"Error Occurred!",Toast.LENGTH_SHORT).show();
                }
            }
        });
//*************************************************************LoginActivity**********************************************//
        tvSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //startLoading();
                Intent i = new Intent(MainActivity.this,Login_Activity.class);
                startActivity(i);
                finish();
            }
        });
//************************************************************pass visibility***************************************************//

        showpass.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }
                else{
                    password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
            }
        });
    }
    boolean passValidator(String pass){
        Pattern pattern;
        Matcher matcher;
        final String pass_regex = "((?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%]).{8,25})";
        pattern = Pattern.compile(pass_regex);
        matcher =pattern.matcher(pass);
        return matcher.matches();
    }
    void startLoading(){
        loading.LoadingDialog();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                loading.dismissDialog();
            }
        },1000);
    }
}