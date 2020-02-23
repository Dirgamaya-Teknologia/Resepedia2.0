package com.dyakta.resepedia;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TambahBahanActivity extends AppCompatActivity {

    private Toolbar newPostToolbar;
    private Button btn_bahan;
    private EditText mNamaBahan;
    private FirebaseFirestore firebaseFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tambah_bahan);

        btn_bahan = findViewById(R.id.btn_tambah_bahan);
        mNamaBahan = findViewById(R.id.et_nama_bahan);

        newPostToolbar = findViewById(R.id.toolbar4);
        newPostToolbar.setTitle("Tambah Bahan");
        setSupportActionBar(newPostToolbar);

        newPostToolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        newPostToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        final Spinner satuan_dropdown = (Spinner) findViewById(R.id.satuan_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.satuan_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        satuan_dropdown.setAdapter(adapter);

        final Spinner tipe_dropdown = (Spinner) findViewById(R.id.tipe_spinner);
        final ArrayAdapter<CharSequence> tipe_adapter = ArrayAdapter.createFromResource(this,
                R.array.tipe_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        tipe_dropdown.setAdapter(tipe_adapter);

        firebaseFirestore = FirebaseFirestore.getInstance();
        btn_bahan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nama = mNamaBahan.getText().toString().trim();
                String satuan = satuan_dropdown.getSelectedItem().toString().trim();
                String tipe = tipe_dropdown.getSelectedItem().toString().trim();

                uploadData(nama, satuan, tipe);
            }
        });

    }

    private void uploadData(String nama, String satuan, String tipe) {
        String id = UUID.randomUUID().toString();
        Map<String, Object> doc = new HashMap<>();
        doc.put("id", id);
        doc.put("nama", nama);
        doc.put("satuan", satuan);
        doc.put("tipe", tipe);

        firebaseFirestore.collection("Bahan").document(id).set(doc)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(TambahBahanActivity.this,"Berhasil Menambahkan", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(TambahBahanActivity.this, MainActivity.class));
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(TambahBahanActivity.this,e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
