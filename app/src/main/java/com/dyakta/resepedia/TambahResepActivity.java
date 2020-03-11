package com.dyakta.resepedia;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import id.zelory.compressor.Compressor;


public class TambahResepActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private EditText edtJudul, edtDeskripsi, edtPorsi, edtJenisResep, edtLangkah;
    private ImageView newPostImage;
    private Spinner spBahan;
    private ProgressBar pg_tambahResep;
    private TextView txt_satuan;
    private LinearLayout layoutBahan;
    private Button btnAddBahan, btnAddResep;
    private FirebaseFirestore firebaseFirestore;
    private List<Bahan> listBahan;
    private List<String> listNamaBahan;
    private List<String> listSatuan;
    private Toolbar newPostToolbar;
    private Double v1, v2, hasilPorsi, hasilKuantitas;

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
//        newPostImage.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                CropImage.activity()
//                        .setGuidelines(CropImageView.Guidelines.ON)
//                        .setMinCropResultSize(512, 512)
//                        .setAspectRatio(1, 1)
//                        .start(TambahResepActivity.this);
//
//            }
//        });

        firebaseFirestore = FirebaseFirestore.getInstance();

        newPostToolbar.setTitle("Tambah Resep");
        btnAddResep.setText("Tambah Resep");

        setSupportActionBar(newPostToolbar);

        btnAddBahan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addBahan(v);
            }
        });

        btnAddResep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pg_tambahResep.setVisibility(View.VISIBLE);
                String judul = edtJudul.getText().toString();
                String deskripsi = edtDeskripsi.getText().toString();
                prosesPorsi();
//                String jenisResep = edtJenisResep.getText().toString();

                String bahan = spBahan.getSelectedItem().toString().trim();
                String satuann = txt_satuan.getText().toString();
                ArrayList arrayBahan = new ArrayList();
                ArrayList arrayQuantitas = new ArrayList();
                ArrayList arraySatuan = new ArrayList();

                for (int i = 0; i < layoutBahan.getChildCount(); i++) {
                    LinearLayout layout = (LinearLayout) layoutBahan.getChildAt(i);
                    Spinner spinner = (Spinner) layout.getChildAt(0);
                    TextView textView = (TextView) layout.getChildAt(1);
                    EditText editText = (EditText) layout.getChildAt(2);

                    prosesKuantitas(editText);

                    arrayBahan.add(spinner.getSelectedItem());
                    arraySatuan.add(textView.getText());
                    arrayQuantitas.add(hasilKuantitas);
                }

                String langkah = edtLangkah.getText().toString();
                if (!TextUtils.isEmpty(judul) && !TextUtils.isEmpty(deskripsi) && !TextUtils.isEmpty(bahan)  && !TextUtils.isEmpty(langkah)) {
                    String id = UUID.randomUUID().toString();

                    final String randomName = UUID.randomUUID().toString();
                    Map<String, Object> postMap = new HashMap<>();
                    postMap.put("id", randomName);
//                    postMap.put("image_url", downloadtextUri);
//                    postMap.put("thumb", downloadThumbUri);
                    postMap.put("judul", judul);
                    postMap.put("desc", deskripsi);
                    postMap.put("porsi", hasilPorsi);
//                                        postMap.put("jenis_resep", jenisResep);
                    postMap.put("bahan", arrayBahan);
                    postMap.put("satuan", arraySatuan);
                    postMap.put("quantitas", arrayQuantitas);
                    postMap.put("langkah", langkah);
                    postMap.put("user_id", current_user_id);

                    firebaseFirestore.collection("Resep").document(randomName).set(postMap)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {

                                        Toast.makeText(TambahResepActivity.this, "Resep telah ditambahkan", Toast.LENGTH_LONG).show();
                                        pg_tambahResep.setVisibility(View.GONE);
                                        Intent mainIntent = new Intent(TambahResepActivity.this, MainActivity.class);
                                        startActivity(mainIntent);
                                        finish();

                                    }
                                }
                            });

//                    final StorageReference filePath = storageReference.child("post_images").child(randomName);
//                    filePath.putFile(postImageUri).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
//                        @Override
//                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
//                            if (!task.isSuccessful()) {
//                                throw task.getException();
//                            }
//                            return filePath.getDownloadUrl();
//                        }
//                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
//                        @Override
//                        public void onComplete(@NonNull final Task<Uri> task) {
//                            if (task.isSuccessful()) {
//
//                                File newImageFile = new File(postImageUri.getPath());
//
//                                try {
//                                    compressedImageFile = new Compressor(TambahResepActivity.this).
//                                            setMaxHeight(100)
//                                            .setMaxWidth(100)
//                                            .setQuality(2)
//                                            .compressToBitmap(newImageFile);
//
//                                } catch (IOException e) {
//                                    e.printStackTrace();
//                                }
//
//                                ByteArrayOutputStream baos = new ByteArrayOutputStream();
//                                compressedImageFile.compress(Bitmap.CompressFormat.JPEG, 100, baos);
//                                byte[] thumbData = baos.toByteArray();
//
//                                UploadTask uploadTask = storageReference.child("post_images/thumbs").child(randomName + ".jpg").
//                                        putBytes(thumbData);
//                                final Uri downloadUri = task.getResult();
//                                final String downloadtextUri = downloadUri.toString();
//                                uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//                                    @Override
//                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                                        Task<Uri> uriTask = taskSnapshot.getMetadata().getReference().getDownloadUrl();
//                                        String downloadThumbUri = uriTask.toString();
//
//
//
//                                    }
//                                }).addOnFailureListener(new OnFailureListener() {
//                                    @Override
//                                    public void onFailure(@NonNull Exception e) {
//                                        //Error handling
//                                    }
//                                });
//
//
//                            } else {
////                                progressBar.setVisibility(View.INVISIBLE);
//                            }
//                        }
//                    });
                }
            }
        });
    }

    private void addBahan(View v) {
        LinearLayout layout = new LinearLayout(this);
        setLayoutBahan(layout);

        Spinner spinner = new Spinner(this);
        setSpinnerBahan(spinner);
        layout.addView(spinner);

        TextView textView = new TextView(this);
        setTextViewSatuan(textView);
        layout.addView(textView);

        EditText editText = new EditText(this);
        setEditTextBahan(editText);
        layout.addView(editText);

        layoutBahan.addView(layout, layoutBahan.getChildCount());
    }

    //This function to convert DPs to pixels
    private int convertDpToPixel(float dp) {
        DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
        float px = dp * (metrics.densityDpi / 160f);
        return Math.round(px);
    }

    private void setLayoutBahan(LinearLayout layout){
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        layout.setOrientation(LinearLayout.HORIZONTAL);
        layout.setLayoutParams(params);
    }

    private void setSpinnerBahan(Spinner spinner){
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                2);
        params.setMargins(convertDpToPixel(16), 0, convertDpToPixel(8), 0);
        params.gravity = Gravity.CENTER_VERTICAL;
        spinner.setLayoutParams(params);

        CollectionReference subjectRef = firebaseFirestore.collection("Bahan");
        listNamaBahan = new ArrayList<>();
        listSatuan = new ArrayList<>();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, listNamaBahan);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        subjectRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        String namaBahan = document.getString("nama");
                        String satuan = document.getString("satuan");
                        listNamaBahan.add(namaBahan);
                        listSatuan.add(satuan);
                    }
                    adapter.notifyDataSetChanged();
                }
            }
        });
        spinner.setOnItemSelectedListener(this);
    }

    private void setTextViewSatuan(TextView textView){
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                4);
        textView.setLayoutParams(params);
        textView.setText("satuan");
    }

    private void setEditTextBahan(EditText editText){
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                4);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            params.setMarginStart(16);
            params.setMarginEnd(16);
        }
        editText.setLayoutParams(params);
        editText.setInputType(InputType.TYPE_CLASS_NUMBER);
    }

    private void initComponent() {
        edtJudul = findViewById(R.id.edt_judul);
        edtDeskripsi = findViewById(R.id.edt_deskripsi);
        edtPorsi = findViewById(R.id.edt_porsi);
        layoutBahan = findViewById(R.id.layout_bahan);
        btnAddBahan = findViewById(R.id.btn_add_bahan);
        pg_tambahResep = findViewById(R.id.pg_tambahResep);
        //spBahan = findViewById(R.id.sp_bahan);
//        newPostImage = findViewById(R.id.imageButton);
        //edtQuantitas = findViewById(R.id.edt_quantitas);
        edtLangkah = findViewById(R.id.edt_langkah_langkah);
        btnAddResep = findViewById(R.id.btn_add);
        listBahan = new ArrayList<>();
    }

    public void konver(EditText edtQuantitas) {
        //konversi inputan ke double
        v1 = Double.parseDouble(edtPorsi.getText().toString());
        v2 = Double.parseDouble(edtQuantitas.getText().toString());
    }

    public void prosesPorsi() {
        v1 = Double.parseDouble(edtPorsi.getText().toString());
        hasilPorsi = v1 / v1;  //perhitungan
        String porsi = Double.toString(hasilPorsi);
    }

    public void prosesKuantitas(EditText edtQuantitas) {
        konver(edtQuantitas);
        hasilKuantitas = v2 / v1;  //perhitungan
        String kuantitas = Double.toString(hasilKuantitas);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        for (int i = 0; i < layoutBahan.getChildCount(); i++) {
            LinearLayout layout = (LinearLayout) layoutBahan.getChildAt(i);
            TextView textView = (TextView) layout.getChildAt(1);
            textView.setText(listSatuan.get(position));
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
//            CropImage.ActivityResult result = CropImage.getActivityResult(data);
//            if (resultCode == RESULT_OK) {
//
//                postImageUri = result.getUri();
//                newPostImage.setImageURI(postImageUri);
//
//            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
//                Exception error = result.getError();
//            }
//        }
//    }
}
