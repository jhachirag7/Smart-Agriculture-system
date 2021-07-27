package com.agri.smartagriculture;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Login_Activity extends AppCompatActivity {

    public EditText emailId,password;
    public Button btnSignIn;
    public TextView tvSignUp,forgot_pass;
    CheckBox passvisible;
    FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    DatabaseReference reff;
    //Member member;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_);

        mFirebaseAuth = FirebaseAuth.getInstance();
        forgot_pass=findViewById(R.id.reset);
        emailId = findViewById(R.id.editTextTextEmailAddress);
        password = findViewById(R.id.editTextTextPassword);
        btnSignIn = findViewById(R.id.button);
        passvisible = findViewById(R.id.checkBox2);
        tvSignUp = findViewById(R.id.textView);
        reff = FirebaseDatabase.getInstance().getReference();

        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser mFirebaseUser = mFirebaseAuth.getCurrentUser();
                if(mFirebaseUser !=null && mFirebaseUser.isEmailVerified()){
                    Toast.makeText(Login_Activity.this,"You are Logged in",Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(Login_Activity.this,"Please Login",Toast.LENGTH_SHORT).show();

                }
            }
        };
//***********************************************SignIn*****************************************************//
        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailId.getText().toString();
                String pwd = password.getText().toString();
                if(email.isEmpty()){
                    emailId.setError("Please enter Email id");
                    emailId.requestFocus();
                }
                else if(pwd.isEmpty()){
                    password.setError("Please enter your password");
                    password.requestFocus();
                }
                else if(email.isEmpty() && pwd.isEmpty()){
                    Toast.makeText(Login_Activity.this,"Fields Are Empty!",Toast.LENGTH_SHORT).show();
                }
                else if(!email.isEmpty() && !pwd.isEmpty()) {
                    mFirebaseAuth.signInWithEmailAndPassword(email, pwd).addOnCompleteListener(Login_Activity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (!task.isSuccessful()) {
                                Toast.makeText(Login_Activity.this, "Internet is not good or Password is wrong", Toast.LENGTH_SHORT).show();
                            } else {
                               if(mFirebaseAuth.getCurrentUser().isEmailVerified()){
                                   Intent inToHome = new Intent(Login_Activity.this, HomeActivity.class);
                                   startActivity(inToHome);
                                   finish();
                               }
                               else{
                                   mFirebaseAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                       @Override
                                       public void onComplete(@NonNull Task<Void> task) {
                                           if(task.isSuccessful())
                                           Toast.makeText(Login_Activity.this,"Verification mail has been sent to your account",Toast.LENGTH_SHORT).show();

                                           else
                                               Toast.makeText(Login_Activity.this,task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                                       }
                                   });
                               }

                            }
                        }
                    });
                }
                else{
                    Toast.makeText(Login_Activity.this,"Error Occurred!",Toast.LENGTH_SHORT).show();
                }
            }
        });
        tvSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intSignUp = new Intent(Login_Activity.this,MainActivity.class);
                startActivity(intSignUp);
                finish();
            }
        });
// ************************************************Forgot Password**********************************************************//
        forgot_pass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailId.getText().toString();
                if(email.isEmpty()){
                    emailId.setError("Please enter Email id");
                    emailId.requestFocus();
                }
                else{
                    mFirebaseAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(Login_Activity.this,"Check your mail",Toast.LENGTH_SHORT).show();
                            }
                            else{
                                Toast.makeText(Login_Activity.this,"mail does not exist or check your internet connection",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
//************************************************************pass visibility***************************************************//

        passvisible.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
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
    @Override
    protected void onStart() {
        super.onStart();
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
    }

}