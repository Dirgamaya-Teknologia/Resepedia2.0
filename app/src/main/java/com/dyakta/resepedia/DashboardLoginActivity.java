package com.dyakta.resepedia;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import java.util.Arrays;

public class DashboardLoginActivity extends AppCompatActivity {


    private SignInButton signInButton;
    private Button btn_masuk,btn_daftar;
    private EditText passsword,konfirmasi,email;
    //Firebase Auth
    private FirebaseAuth mFirebaseAuth;
    private String TAG = "LOGIN";





    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mFirebaseAuth.getCurrentUser();
        if (currentUser != null){
            setMain();
        }
//        updateUI(currentUser);
    }

    private void setMain() {
        startActivity(new Intent(DashboardLoginActivity.this, MainActivity.class));
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard_login);

        Toolbar dashboardToolbar = findViewById(R.id.dashboardToolbar);
        dashboardToolbar.setTitle("Kembali ke Beranda");
        setSupportActionBar(dashboardToolbar);

        dashboardToolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        dashboardToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btn_daftar = findViewById(R.id.btn_daftarDenganEmail);
        btn_masuk = findViewById(R.id.btn_masukLewatEmail);

        //daftar
        passsword = findViewById(R.id.editxt_password);
        konfirmasi = findViewById(R.id.editxt_konfirmasiPassword);
        email = findViewById(R.id.editxt_email);


        mFirebaseAuth = FirebaseAuth.getInstance();





        btn_masuk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginIntent();
            }
        });

        btn_daftar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                daftarEmail();
            }
        });
    }

    //daftar dengan email
    private void daftarEmail() {
        String pass = passsword.getText().toString();
        String konf = konfirmasi.getText().toString();
        String emaill = email.getText().toString();

        if (!TextUtils.isEmpty(emaill)&&!TextUtils.isEmpty(pass)&&!TextUtils.isEmpty(konf)){
            if (pass.equals(konf)){
                mFirebaseAuth.createUserWithEmailAndPassword(emaill,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            Intent mainIntent = new Intent(DashboardLoginActivity.this, SetupProfilActivity.class);
                            startActivity(mainIntent);
                            finish();
                        }else {
                            String errorMessage = task.getException().getMessage();
                            Toast.makeText(DashboardLoginActivity.this,"Error : "+errorMessage,Toast.LENGTH_LONG).show();
                        }

                    }
                });
            }else {
                Toast.makeText(DashboardLoginActivity.this,"Confirm Password and Passwor Field doesn't match ",Toast.LENGTH_LONG).show();
            }
        }
    }

    private void loginIntent() {
        startActivity(new Intent(DashboardLoginActivity.this, LoginActivity.class));
    }
}
