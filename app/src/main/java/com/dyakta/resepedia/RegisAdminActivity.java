package com.dyakta.resepedia;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class RegisAdminActivity extends AppCompatActivity {

    private Button btn_daftar;
    private EditText passsword,konfirmasi,email;
    //Firebase Auth
    private FirebaseAuth mFirebaseAuth;

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
        startActivity(new Intent(RegisAdminActivity.this, MainActivity.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regis_admin);

        btn_daftar = findViewById(R.id.btn_daftarDenganEmail);
        mFirebaseAuth = FirebaseAuth.getInstance();

        //daftar
        passsword = findViewById(R.id.editxt_password);
        konfirmasi = findViewById(R.id.editxt_konfirmasiPassword);
        email = findViewById(R.id.editxt_email);

        btn_daftar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                daftarEmail();
            }
        });
    }

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
                            Intent mainIntent = new Intent(RegisAdminActivity.this, SetupProfilActivity.class);
                            startActivity(mainIntent);
                            finish();
                        }else {
                            String errorMessage = task.getException().getMessage();
                            Toast.makeText(RegisAdminActivity.this,"Error : "+errorMessage,Toast.LENGTH_LONG).show();
                        }

                    }
                });
            }else {
                Toast.makeText(RegisAdminActivity.this,"Confirm Password and Passwor Field doesn't match ",Toast.LENGTH_LONG).show();
            }
        }
    }
}
