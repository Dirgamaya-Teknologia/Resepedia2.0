package com.dyakta.resepedia;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.Navigation;

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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisAdminActivity extends AppCompatActivity {

    private Button btn_daftar;
    private EditText passsword,konfirmasi,email,nama;
    private String user_id;

    //Firebase Auth
    private FirebaseAuth mFirebaseAuth;
    private FirebaseFirestore firebaseFirestore;

//    @Override
//    public void onStart() {
//        super.onStart();
//        // Check if user is signed in (non-null) and update UI accordingly.
//        FirebaseUser currentUser = mFirebaseAuth.getCurrentUser();
//        if (currentUser != null){
//            setMain();
//        }
////        updateUI(currentUser);
//    }

    private void setMain() {
        startActivity(new Intent(RegisAdminActivity.this, MainActivity.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regis_admin);

        btn_daftar = findViewById(R.id.btn_daftarDenganEmail);
        mFirebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

//        user_id = mFirebaseAuth.getCurrentUser().getUid();


        //daftar
        passsword = findViewById(R.id.editxt_password);
        konfirmasi = findViewById(R.id.editxt_konfirmasiPassword);
        email = findViewById(R.id.editxt_email);
        nama = findViewById(R.id.et_nama_daftar);

//        firebaseFirestore.collection("Admin").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//
//                if (task.isSuccessful()){
//
//                    if (task.getResult().exists()){
//                        String name = task.getResult().getString("name");
//                        nama.setText(name);
//                        String emaill = task.getResult().getString("email");
//                        email.setText(emaill);
//                        String pass = task.getResult().getString("password");
//                        passsword.setText(pass);
//                    }
//
//
//
//                }else {
//                    String error = task.getException().getMessage();
//                    Toast.makeText(RegisAdminActivity.this,"(FIRESTORE Retrieve Error) : "+error,Toast.LENGTH_LONG).show();
//
//                }
//            }
//        });

        btn_daftar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                user_id = mFirebaseAuth.getCurrentUser().getUid();

                daftarEmail();
            }
        });
    }

    private void daftarEmail() {
        String pass = passsword.getText().toString();
        String konf = konfirmasi.getText().toString();
        String emaill = email.getText().toString();
        String namaa = nama.getText().toString();

        if (!TextUtils.isEmpty(emaill)&&!TextUtils.isEmpty(pass)&&!TextUtils.isEmpty(konf)&&!TextUtils.isEmpty(namaa)){
            if (pass.equals(konf)){
                mFirebaseAuth.createUserWithEmailAndPassword(emaill,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            Intent mainIntent = new Intent(RegisAdminActivity.this, MainActivity.class);
                            startActivity(mainIntent);
                            storeFireStore(namaa,emaill,pass);
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

    private void storeFireStore(String namaa, String emaill, String pass) {
        Map<String, String> userMap = new HashMap<>();
        userMap.put("name",namaa);
        userMap.put("email",emaill);
        userMap.put("password",pass);
//        userMap.put("image",downloadUri.toString());

        firebaseFirestore.collection("Users").document(user_id).set(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){

                    Toast.makeText(RegisAdminActivity.this,"The user Settings are updated.",Toast.LENGTH_LONG).show();
                    Intent mainIntent = new Intent(RegisAdminActivity.this, MainActivity.class);
                    startActivity(mainIntent);
                } else {
                    String error = task.getException().getMessage();
                    Toast.makeText(RegisAdminActivity.this,"(FIRESTORE Error) : "+error,Toast.LENGTH_LONG).show();
                }

            }
        });
    }
}
