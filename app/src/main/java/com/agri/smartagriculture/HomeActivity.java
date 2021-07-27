package com.agri.smartagriculture;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class HomeActivity extends AppCompatActivity {
    public Button btnLogout;
    TextView temperature,email,moisture,humidity;
    String uid;
    FirebaseAuth mFirebaseAuth;
    DatabaseReference reff;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    FloatingActionButton fmain,flogout;
    Float translationY=100f;
    Boolean menuOpen= false;
    OvershootInterpolator interpolator = new OvershootInterpolator();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        setContentView(R.layout.activity_home);
        //btnLogout = findViewById(R.id.logout);
        email =findViewById(R.id.textView3);
        temperature = findViewById(R.id.textView6);
        moisture = findViewById(R.id.textView9);
        humidity = findViewById(R.id.textView7);
        /*btnLiveData = findViewById(R.id.Live);*/


        mFirebaseAuth = FirebaseAuth.getInstance();
        uid = mFirebaseAuth.getCurrentUser().getUid();
        reff = FirebaseDatabase.getInstance().getReference().child("Members").child(uid);

        ShowMenu();
//***********************************************************Snapshot Firebase***********************************************//
        reff.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String Email = snapshot.child("email").getValue().toString();
                email.setText(Email);
                String temp = snapshot.child("temperature").getValue().toString();
                check(temp,temperature);
                String moist = snapshot.child("moisture").getValue().toString();
                check(moist,moisture);
                String hum = snapshot.child("humidity").getValue().toString();
                check(hum,humidity);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    void check(String snap, TextView set){
        if(snap.charAt(0)=='-'){
            set.setText("NA");
        }
        else{
            set.setText(snap);
        }
    }
//*************************************************************Exitdialog*******************************************************//


    @Override
    public void onBackPressed() {
        AlertDialog.Builder build = new AlertDialog.Builder(this);

        build.setMessage("Are you sure you want to Exit?").setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        FirebaseAuth.getInstance().signOut();
                        Intent intToLogin = new Intent(HomeActivity.this, Login_Activity.class);
                        startActivity(intToLogin);
                        finish();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        AlertDialog alertDialog = build.create();
        alertDialog.show();
    }

    private void ShowMenu() {
        fmain = findViewById(R.id.floatingActionButton);
        flogout = findViewById(R.id.floatingActionButton2);
        flogout.setEnabled(false);
        flogout.setAlpha(0f);

        flogout.setTranslationY(translationY);

        fmain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(menuOpen){
                    flogout.setEnabled(false);
                    Closemenu();
                }
                else{
                    flogout.setEnabled(true);
                    Openmenu();
                }
            }
        });
//**************************************************************Logout**************************************************//
        flogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intToMain = new Intent(HomeActivity.this, Login_Activity.class);
                startActivity(intToMain);
                finish();
                Closemenu();
            }
        });
    }

    private void Openmenu() {

        menuOpen=!menuOpen;
        fmain.setImageResource(R.drawable.ic_baseline_expand_more_24);
        flogout.animate().translationY(0f).alpha(1f).setInterpolator(interpolator).setDuration(300).start();
    }

    private void Closemenu() {

        menuOpen=!menuOpen;
        fmain.setImageResource(R.drawable.ic_baseline_expand_less_24);
        flogout.animate().translationY(translationY).alpha(0f).setInterpolator(interpolator).setDuration(300).start();

    }
}