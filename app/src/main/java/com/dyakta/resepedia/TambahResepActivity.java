package com.dyakta.resepedia;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import id.zelory.compressor.Compressor;


public class TambahResepActivity extends AppCompatActivity {

    private EditText edtJudul, edtDeskripsi, edtPorsi, edtJenisResep, edtQuantitas, edtLangkah;
    private ImageView newPostImage;
    private Spinner spBahan;
    private LinearLayout layoutBahan;
    private Button btnAddBahan, btnAddResep;
    private FirebaseFirestore firebaseFirestore;
    private List<Bahan> listBahan;
    private List<String> listNamaBahan;
    private Toolbar newPostToolbar;
    Double v1,v2,hasilPorsi,hasilKuantitas;

    private Uri postImageUri = null;
//    private ProgressBar newPostProgress;

    private StorageReference storageReference;

    private FirebaseAuth firebaseAuth;
    private Bitmap compressedImageFile;
    private String current_user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tambah_resep);
        storageReference = FirebaseStorage.getInstance().getReference();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        current_user_id = firebaseAuth.getCurrentUser().getUid();

        TextView tvJudul = findViewById(R.id.tv_judul);
        newPostToolbar = findViewById(R.id.toolbarResep);

        newPostToolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        newPostToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        initComponent();
        newPostImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setMinCropResultSize(512,512)
                        .setAspectRatio(1,1)
                        .start(TambahResepActivity.this);

            }
        });

        firebaseFirestore = FirebaseFirestore.getInstance();

        CollectionReference subjectRef = firebaseFirestore.collection("Bahan");
        spBahan = findViewById(R.id.sp_bahan);
        List<String> subjects = new ArrayList<>();
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, subjects);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spBahan.setAdapter(adapter);
        subjectRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        String subject = document.getString("nama");
                        subjects.add(subject);
                    }
                    adapter.notifyDataSetChanged();
                }
            }
        });

        String called = getIntent().getStringExtra("called");
        ResepPost resepPost = getIntent().getParcelableExtra("resep");
        String id = resepPost.getId();
        if (called.equalsIgnoreCase("edit")){
            newPostToolbar.setTitle("Ubah Resep");
            btnAddResep.setText("Ubah Resep");

            String judul = resepPost.getJudul();
            String desc = resepPost.getDesc();
            double porsi = resepPost.getPorsi();
            String bahan = resepPost.getBahan();
            double quantitas = resepPost.getQuantitas();
            String langkah = resepPost.getLangkah();

            edtJudul.setText(judul);
            edtDeskripsi.setText(desc);
            edtPorsi.setText(String.valueOf(porsi));

            for (int i = 0; i < subjects.size(); i++) {
                if (bahan.equals(subjects.get(i))){
                    spBahan.setSelection(i);
                }
            }

            edtQuantitas.setText(String.valueOf(quantitas));
            edtLangkah.setText(langkah);

        } else {
            newPostToolbar.setTitle("Tambah Resep");
            btnAddResep.setText("Tambah Resep");
        }

        setSupportActionBar(newPostToolbar);

        btnAddBahan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                final View rowView = inflater.inflate(R.layout.field_bahan, null);
                layoutBahan.addView(rowView, layoutBahan.getChildCount() - 1);
            }
        });

        btnAddResep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO methode add resep and upload to firebase
                String judul = edtJudul.getText().toString();
                String deskripsi = edtDeskripsi.getText().toString();
                prosesPorsi();
                String jenisResep = edtJenisResep.getText().toString();
                String bahan = spBahan.getSelectedItem().toString().trim();
                prosesKuantitas();
                String langkah = edtLangkah.getText().toString();
                if (!TextUtils.isEmpty(judul)&& !TextUtils.isEmpty(deskripsi)&& !TextUtils.isEmpty(bahan) && !TextUtils.isEmpty(jenisResep)&& !TextUtils.isEmpty(langkah) && postImageUri != null){
                    String id = UUID.randomUUID().toString();

                    final String randomName = UUID.randomUUID().toString();

                    final StorageReference filePath = storageReference.child("post_images").child(randomName);
                    filePath.putFile(postImageUri).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                        @Override
                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                            if (!task.isSuccessful()){
                                throw task.getException();
                            }
                            return filePath.getDownloadUrl();
                        }
                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull final Task<Uri> task) {
                            if (task.isSuccessful()){

                                File newImageFile = new File(postImageUri.getPath());

                                try {
                                    compressedImageFile = new Compressor(TambahResepActivity.this).
                                            setMaxHeight(100)
                                            .setMaxWidth(100)
                                            .setQuality(2)
                                            .compressToBitmap(newImageFile);

                                } catch (IOException e) {
                                    e.printStackTrace();
                                }

                                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                compressedImageFile.compress(Bitmap.CompressFormat.JPEG,100,baos);
                                byte[] thumbData = baos.toByteArray();

                                UploadTask uploadTask = storageReference.child("post_images/thumbs").child(randomName + ".jpg").
                                        putBytes(thumbData);
                                final Uri downloadUri = task.getResult();
                                final String downloadtextUri = downloadUri.toString();
                                uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                        Task<Uri> uriTask = taskSnapshot.getMetadata().getReference().getDownloadUrl();
                                        String downloadThumbUri = uriTask.toString();

                                        Map<String,Object> postMap = new HashMap<>();
                                        postMap.put("id",randomName);
                                        postMap.put("image_url",downloadtextUri);
                                        postMap.put("thumb", downloadThumbUri);
                                        postMap.put("judul",judul);
                                        postMap.put("desc",deskripsi);
                                        postMap.put("porsi",hasilPorsi);
                                        postMap.put("jenis_resep",jenisResep);
                                        postMap.put("bahan",bahan);
                                        postMap.put("quantitas",hasilKuantitas);
                                        postMap.put("langkah",langkah);
                                        postMap.put("user_id",current_user_id);

                                        if (called.equalsIgnoreCase("edit")){
                                            firebaseFirestore.collection("resep").document(randomName).update(postMap)
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful()) {

                                                                Toast.makeText(TambahResepActivity.this, "Resep telah diubahkan", Toast.LENGTH_LONG).show();
                                                                Intent mainIntent = new Intent(TambahResepActivity.this, MainActivity.class);
                                                                startActivity(mainIntent);
                                                                finish();

                                                            }
                                                        }
                                                    });
                                        } else {
                                            firebaseFirestore.collection("Resep").document(randomName).set(postMap)
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful()) {

                                                                Toast.makeText(TambahResepActivity.this, "Resep telah ditambahkan", Toast.LENGTH_LONG).show();
                                                                Intent mainIntent = new Intent(TambahResepActivity.this, MainActivity.class);
                                                                startActivity(mainIntent);
                                                                finish();

                                                            }
                                                        }
                                                    });
                                        }
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        //Error handling
                                    }
                                });



                            }else {
//                                progressBar.setVisibility(View.INVISIBLE);
                            }
                        }
                    });
                }
            }
        });
    }

    private void initComponent() {
        edtJudul = findViewById(R.id.edt_judul);
        edtDeskripsi = findViewById(R.id.edt_deskripsi);
        edtPorsi = findViewById(R.id.edt_porsi);
        edtJenisResep = findViewById(R.id.edt_jenis_resep);
        layoutBahan = findViewById(R.id.layout_bahan);
        btnAddBahan = findViewById(R.id.btn_add_bahan);
        spBahan = findViewById(R.id.sp_bahan);
        newPostImage = findViewById(R.id.imageButton);
        edtQuantitas = findViewById(R.id.edt_quantitas);
        edtLangkah = findViewById(R.id.edt_langkah_langkah);
        btnAddResep = findViewById(R.id.btn_add);
        listBahan = new ArrayList<>();
    }
    public void konver(){
        //konversi inputan ke double
        v1 = Double.parseDouble(edtPorsi.getText().toString());
        v2 = Double.parseDouble(edtQuantitas.getText().toString());
    }

    public void prosesPorsi() {
        konver();
        hasilPorsi = v1/v1;  //perhitungan
        String porsi = Double.toString(hasilPorsi);
    }

    public void prosesKuantitas() {
        konver();
        hasilKuantitas = v2/v1;  //perhitungan
        String kuantitas = Double.toString(hasilKuantitas);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                postImageUri = result.getUri();
                newPostImage.setImageURI(postImageUri);

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }
}
