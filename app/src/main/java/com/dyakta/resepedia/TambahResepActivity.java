package com.dyakta.resepedia;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TambahResepActivity extends AppCompatActivity {

    private EditText edtJudul, edtDeskripsi, edtPorsi, edtJenisResep, edtQuantitas, edtLangkah;
    private Spinner spBahan;
    private LinearLayout layoutBahan;
    private Button btnAddBahan, btnAddResep;
    private FirebaseFirestore firebaseFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tambah_resep);

        initComponent();
        firebaseFirestore = FirebaseFirestore.getInstance();

        btnAddBahan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO 1: methode add bahan
            }
        });

        btnAddResep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO 2: methode add resep and upload to firebase
                String judul = edtJudul.getText().toString();
                String deskripsi = edtDeskripsi.getText().toString();
                String porsi = edtPorsi.getText().toString();
                String jenisResep = edtJenisResep.getText().toString();
                String bahan = spBahan.getSelectedItem().toString().trim();
                String quantitas = edtQuantitas.toString();
                String langkah = edtLangkah.toString();

                String id = UUID.randomUUID().toString();
                Map<String, Object> doc = new HashMap<>();

                doc.put("judul", judul);
                doc.put("deskripsi", deskripsi);
                doc.put("porsi", porsi);
                doc.put("jenis_resep", jenisResep);
                doc.put("bahan", bahan);
                doc.put("quantitas", quantitas);
                doc.put("langkah", langkah);

                firebaseFirestore.collection("Resep").document(id).set(doc)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(TambahResepActivity.this,"Berhasil Menambahkan", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(TambahResepActivity.this, ResepFragment.class);
                                startActivity(intent);
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(TambahResepActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
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
        edtQuantitas = findViewById(R.id.edt_quantitas);
        edtLangkah = findViewById(R.id.edt_langkah_langkah);
        btnAddResep = findViewById(R.id.btn_add);
    }
}
