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

public class EditBahanActivity extends AppCompatActivity {

    private Toolbar newPostToolbar;
    private Button btn_bahan;
    private EditText mNamaBahan;
    private ProgressBar pg_edit_bahan;
    private FirebaseFirestore firebaseFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tambah_bahan);

        btn_bahan = findViewById(R.id.btn_tambah_bahan);
        mNamaBahan = findViewById(R.id.et_nama_bahan);
        pg_edit_bahan = findViewById(R.id.progressBar);

        newPostToolbar = findViewById(R.id.toolbar4);

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

        Bahan bahan = getIntent().getParcelableExtra("bahan");
        String id = bahan.getId();
        newPostToolbar.setTitle("Ubah Bahan");
        btn_bahan.setText("Ubah Bahan");

        String nama = bahan.getNama();
        String satuan = bahan.getSatuan();
        String tipe = bahan.getTipe();

        mNamaBahan.setText(nama);

        String[] satuans = getResources().getStringArray(R.array.satuan_array);
        int sizeSatuan = satuans.length;
        for (int i = 0; i < sizeSatuan; i++) {
            if (satuan.equals(satuans[i])) {
                satuan_dropdown.setSelection(i);
            }
        }

        String[] tipes = getResources().getStringArray(R.array.tipe_array);
        int sizeTipe = tipes.length;
        for (int i = 0; i < sizeTipe; i++) {
            if (tipe.equals(tipes[i])) {
                tipe_dropdown.setSelection(i);
            }
        }

        setSupportActionBar(newPostToolbar);

        firebaseFirestore = FirebaseFirestore.getInstance();
        btn_bahan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pg_edit_bahan.setVisibility(View.VISIBLE);
                String nama = mNamaBahan.getText().toString().trim();
                String satuan = satuan_dropdown.getSelectedItem().toString().trim();
                String tipe = tipe_dropdown.getSelectedItem().toString().trim();

                UpdateData(id, nama, satuan, tipe);
            }
        });
    }

    private void UpdateData(String id, String nama, String satuan, String tipe) {
        Map<String, Object> doc = new HashMap<>();
        doc.put("id", id);
        doc.put("nama", nama);
        doc.put("satuan", satuan);
        doc.put("tipe", tipe);

        firebaseFirestore.collection("Bahan").document(id).update(doc)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(EditBahanActivity.this, "Berhasil Mengubah", Toast.LENGTH_SHORT).show();
                        pg_edit_bahan.setVisibility(View.GONE);
                        startActivity(new Intent(EditBahanActivity.this, MainActivity.class));
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(EditBahanActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
