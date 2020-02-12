package com.dyakta.resepedia;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

//import com.bumptech.glide.Glide;
//import com.bumptech.glide.request.RequestOptions;
//import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;


public class SetupProfilActivity extends AppCompatActivity {

//    private CircleImageView setupImage;
    private Uri mainImageUri = null;
    private String user_id;
    private boolean isChanged = false;
    private EditText setupName, setupPhone, setupLocation;
    private TextView ambilFoto;
    private Button setupBtn;
    private StorageReference storageReference;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private ProgressBar setupProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        Toolbar setupToolbar = findViewById(R.id.setupToolbar);
        setupToolbar.setTitle("Edit Akun");
        setSupportActionBar(setupToolbar);

        setupToolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        setupToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
//        getSupportActionBar().setTitle("Account Setup");

        firebaseAuth = FirebaseAuth.getInstance();
        user_id = firebaseAuth.getCurrentUser().getUid();

        firebaseFirestore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();


        setupName = findViewById(R.id.et_setup_nama);
        setupPhone = findViewById(R.id.et_phone_setup);
        setupLocation = findViewById(R.id.et_lokasi_setup);
        setupBtn = findViewById(R.id.btn_save_setup);
        setupProgress = findViewById(R.id.progressBar_setup);


        setupProgress.setVisibility(View.VISIBLE);
        setupBtn.setEnabled(false);
        firebaseFirestore.collection("Users").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if (task.isSuccessful()){

                    if (task.getResult().exists()){
                        String name = task.getResult().getString("name");
                        setupName.setText(name);
                    }



                }else {
                    String error = task.getException().getMessage();
                    Toast.makeText(SetupProfilActivity.this,"(FIRESTORE Retrieve Error) : "+error,Toast.LENGTH_LONG).show();

                }
                setupProgress.setVisibility(View.INVISIBLE);
                setupBtn.setEnabled(true);
            }
        });

        setupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String user_name = setupName.getText().toString();

                if (!TextUtils.isEmpty(user_name)) {

                    setupProgress.setVisibility(View.VISIBLE);
                    if (isChanged) {

                        user_id = firebaseAuth.getCurrentUser().getUid();

//                        final StorageReference image_path = storageReference.child("profile_images").child(user_id + ".jpg");
//                        image_path.putFile(mainImageUri).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
//                            @Override
//                            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
//                                if (!task.isSuccessful()) {
//                                    throw task.getException();
//                                }
//                                return image_path.getDownloadUrl();
//                            }
//                        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
//                            @Override
//                            public void onComplete(@NonNull Task<Uri> task) {
//                                if (task.isSuccessful()) {
//
//                                    storeFireStore(task, user_name, user_phone, user_location);
//
//                                }
//                            }
//                        });
//
                    }else {

//                        storeFireStore(null,user_name,user_phone,user_location);

                    }
                }

            }
        });





    }
    private void storeFireStore(String user_name) {

        Map<String, String> userMap = new HashMap<>();
        userMap.put("name",user_name);

        firebaseFirestore.collection("Users").document(user_id).set(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){

                    Toast.makeText(SetupProfilActivity.this,"The user Settings are updated.",Toast.LENGTH_LONG).show();
                    Intent mainIntent = new Intent(SetupProfilActivity.this,MainActivity.class);
                    startActivity(mainIntent);
                    finish();

                } else {
                    String error = task.getException().getMessage();
                    Toast.makeText(SetupProfilActivity.this,"(FIRESTORE Error) : "+error,Toast.LENGTH_LONG).show();
                }

                setupProgress.setVisibility(View.INVISIBLE);
            }
        });
    }




}