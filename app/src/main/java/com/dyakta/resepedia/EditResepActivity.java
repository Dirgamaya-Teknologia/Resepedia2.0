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
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
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

public class EditResepActivity extends AppCompatActivity {

    private EditText edtJudul, edtDeskripsi, edtPorsi, edtJenisResep, edtQuantitas, edtLangkah;
    private ImageView newPostImage;
    private Spinner spBahan;
    private ProgressBar progressBar;
    private LinearLayout layoutBahan;
    private Button btnAddBahan, btnAddResep;
    private FirebaseFirestore firebaseFirestore;
    private List<String> bahan;
    private List<String> listNamaBahan;
    private Toolbar newPostToolbar;
    Double v1, v2, hasilPorsi, hasilKuantitas;

    private Uri postImageUri = null;
//    private ProgressBar newPostProgress;

    private StorageReference storageReference;

    private FirebaseAuth firebaseAuth;
    private Bitmap compressedImageFile;
    private String current_user_id;
    private ArrayList<String> arrayBahan;
    private ArrayList arrayQuantitas;
    List<String> subjects = new ArrayList<>();
    List<String> subjects2 = new ArrayList<>();
    public String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_resep);
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
                        .setMinCropResultSize(512, 512)
                        .setAspectRatio(1, 1)
                        .start(EditResepActivity.this);

            }
        });

        firebaseFirestore = FirebaseFirestore.getInstance();

        getDataFromIntent();

        setSupportActionBar(newPostToolbar);

        btnAddResep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                ResepPost resepPost = getIntent().getParcelableExtra("resep");
                id = resepPost.getId();
                // TODO methode add resep and upload to firebase
                String judul = edtJudul.getText().toString();
                String deskripsi = edtDeskripsi.getText().toString();
                prosesPorsi();
                String jenisResep = edtJenisResep.getText().toString();
                //String bahan = spBahan.getSelectedItem().toString().trim();
                ArrayList arrayBahan = new ArrayList();
                ArrayList arrayQuantitas = new ArrayList();
                for (int i = 0; i < layoutBahan.getChildCount(); i++) {
                    LinearLayout layout = (LinearLayout) layoutBahan.getChildAt(i);
                    Spinner spinner = (Spinner) layout.getChildAt(0);
                    EditText editText = (EditText) layout.getChildAt(2);

                    prosesKuantitas(editText);

                    arrayBahan.add(spinner.getSelectedItem());
                    arrayQuantitas.add(hasilKuantitas);
                }

                String langkah = edtLangkah.getText().toString();
                if (!TextUtils.isEmpty(judul) && !TextUtils.isEmpty(deskripsi) && !TextUtils.isEmpty(jenisResep) && !TextUtils.isEmpty(langkah) && postImageUri != null) {
                    //String id = UUID.randomUUID().toString();

                    final String randomName = UUID.randomUUID().toString();
                    final StorageReference filePath = storageReference.child("post_images").child(randomName);
                    filePath.putFile(postImageUri).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                        @Override
                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                            if (!task.isSuccessful()) {
                                throw task.getException();
                            }
                            return filePath.getDownloadUrl();
                        }
                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull final Task<Uri> task) {
                            if (task.isSuccessful()) {

                                File newImageFile = new File(postImageUri.getPath());

                                try {
                                    compressedImageFile = new Compressor(EditResepActivity.this).
                                            setMaxHeight(100)
                                            .setMaxWidth(100)
                                            .setQuality(2)
                                            .compressToBitmap(newImageFile);

                                } catch (IOException e) {
                                    e.printStackTrace();
                                }

                                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                compressedImageFile.compress(Bitmap.CompressFormat.JPEG, 100, baos);
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

                                        Map<String, Object> postMap = new HashMap<>();
                                        postMap.put("id", randomName);
                                        postMap.put("image_url", downloadtextUri);
                                        postMap.put("thumb", downloadThumbUri);
                                        postMap.put("judul", judul);
                                        postMap.put("desc", deskripsi);
                                        postMap.put("porsi", hasilPorsi);
                                        postMap.put("jenis_resep", jenisResep);
                                        postMap.put("bahan", arrayBahan);
                                        postMap.put("quantitas", arrayQuantitas);
                                        postMap.put("langkah", langkah);
                                        postMap.put("user_id", current_user_id);

                                        firebaseFirestore.collection("Resep").document(id).update(postMap)
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            progressBar.setVisibility(View.GONE);
                                                            Toast.makeText(EditResepActivity.this, "Resep telah diubahkan", Toast.LENGTH_LONG).show();
                                                            Intent mainIntent = new Intent(EditResepActivity.this, MainActivity.class);
                                                            startActivity(mainIntent);
                                                            finish();
                                                        }
                                                    }
                                                }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {

                                                Toast.makeText(EditResepActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        });


                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(EditResepActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });


                            } else {
//                                progressBar.setVisibility(View.INVISIBLE);
                            }
                        }
                    });

                }
            }
        });
    }

    private void getDataFromIntent() {
        ResepPost resepPost = getIntent().getParcelableExtra("resep");
        id = resepPost.getId();
        newPostToolbar.setTitle("Ubah Resep");
        btnAddResep.setText("Ubah Resep");

        String judul = resepPost.getJudul();
        String gbr = resepPost.getImage_url();
        String thumb = resepPost.getThumb();
        String desc = resepPost.getDesc();
        double porsi = resepPost.getPorsi();
        String jenisResep = resepPost.getJenis_resep();
        bahan = resepPost.getBahan();
        List<Double> quantitas = resepPost.getQuantitas();
        String langkah = resepPost.getLangkah();

        setBlogImage(gbr,thumb);
        edtJudul.setText(judul);
        edtDeskripsi.setText(desc);
        edtPorsi.setText(String.valueOf(porsi));
        edtJenisResep.setText(jenisResep);

        for (int i = 0; i < bahan.size(); i++) {
            btnAddBahan.performClick();

            LinearLayout layout = (LinearLayout) layoutBahan.getChildAt(i);
            Spinner spinner = (Spinner) layout.getChildAt(0);
            EditText editText = (EditText) layout.getChildAt(2);

            Toast.makeText(this, arrayBahan.toString(), Toast.LENGTH_SHORT).show();
            String namaBahan = bahan.get(i);
            for (int j = 0; j < arrayBahan.size(); j++) {
                if (namaBahan.equals(arrayBahan.get(j))) {
                    spinner.setSelection(j);
                }
            }
            editText.setText(quantitas.get(i).toString());
        }
        edtLangkah.setText(langkah);
    }

    public void setBlogImage(String downloadUri,String thumb){
        ImageView gambar = findViewById(R.id.imageButton);

        RequestOptions requestOptions = new RequestOptions();

        requestOptions.placeholder(R.drawable.user_male);
        Glide.with(this).applyDefaultRequestOptions(requestOptions).load(downloadUri).thumbnail(
                Glide.with(this).load(thumb)
        ).into(gambar);

    }

    public void AddBahan(View view) {
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.field_bahan, null);
        layoutBahan.addView(view, layoutBahan.getChildCount());

        arrayBahan = getIntent().getStringArrayListExtra("bahan");

        spBahan = view.findViewById(R.id.sp_bahan);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, arrayBahan);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spBahan.setAdapter(adapter);
    }

    private void initComponent() {
        edtJudul = findViewById(R.id.edt_judul);
        edtDeskripsi = findViewById(R.id.edt_deskripsi);
        edtPorsi = findViewById(R.id.edt_porsi);
        edtJenisResep = findViewById(R.id.edt_jenis_resep);
        layoutBahan = findViewById(R.id.layout_bahan);
        btnAddBahan = findViewById(R.id.btn_add_bahan);
        progressBar = findViewById(R.id.progressBar2);
        //spBahan = findViewById(R.id.sp_bahan);
        newPostImage = findViewById(R.id.imageButton);
        edtQuantitas = findViewById(R.id.edt_quantitas);
        edtLangkah = findViewById(R.id.edt_langkah_langkah);
        btnAddResep = findViewById(R.id.btn_add);
        //listBahan = new ArrayList<>();
        arrayBahan = new ArrayList<>();
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
