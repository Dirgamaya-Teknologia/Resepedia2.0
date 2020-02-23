package com.dyakta.resepedia;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class TambahResepActivity extends AppCompatActivity {

    private EditText edtJudul, edtDeskripsi, edtPorsi, edtJenisResep, edtQuantitas, edtLangkah;
    private Spinner spBahan;
    private LinearLayout layoutBahan;
    private Button btnAddBahan, btnAddResep;
    private FirebaseFirestore firebaseFirestore;
    private List<Bahan> listBahan;
    private List<String> listNamaBahan;
    private Toolbar newPostToolbar;
    Double v1,v2,hasilPorsi,hasilKuantitas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tambah_resep);

        TextView tvJudul = findViewById(R.id.tv_judul);
        newPostToolbar = findViewById(R.id.toolbarResep);
        newPostToolbar.setTitle("Tambah Resep");
        setSupportActionBar(newPostToolbar);

        newPostToolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        newPostToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        initComponent();
        firebaseFirestore = FirebaseFirestore.getInstance();

//        firebaseFirestore.collection("Bahan").document().get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
//            @Override
//            public void onSuccess(DocumentSnapshot documentSnapshot) {
//                String id = documentSnapshot.getId();
//                String nama = documentSnapshot.getString("nama");
//                String satuan = documentSnapshot.getString("satuan");
//                String tipe = documentSnapshot.getString("tipe");
//                Bahan bahan = new Bahan(id, nama, satuan, tipe);
//                listBahan.add(bahan);
//                tvJudul.setText(nama);
//            }
//        });
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


        for (int i = 0; i < listBahan.size(); i++) {
            listNamaBahan.add(listBahan.get(i).getNama());
        }

//        ArrayAdapter<Bahan> arrayAdapter = new ArrayAdapter<>(
//                this, android.R.layout.simple_spinner_item, listBahan
//        );
//        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        spBahan.setAdapter(arrayAdapter);
//        spBahan.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//
//            }
//        });

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
//                String porsi = edtPorsi.getText().toString();
                prosesPorsi();
                String jenisResep = edtJenisResep.getText().toString();
                String bahan = spBahan.getSelectedItem().toString().trim();
//                String quantitas = edtQuantitas.getText().toString();
                prosesKuantitas();
                String langkah = edtLangkah.getText().toString();
                String id = UUID.randomUUID().toString();
                Map<String, Object> doc = new HashMap<>();

                doc.put("judul", judul);
                doc.put("deskripsi", deskripsi);
                doc.put("porsi", hasilPorsi);
                doc.put("jenis_resep", jenisResep);
                doc.put("bahan", bahan);
                doc.put("quantitas", hasilKuantitas);
                doc.put("langkah", langkah);

                firebaseFirestore.collection("Resep").document(id).set(doc)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(TambahResepActivity.this,"Berhasil Menambahkan", Toast.LENGTH_SHORT).show();
                                // TODO intent Activity to Fragment
                                startActivity(new Intent(TambahResepActivity.this, MainActivity.class));
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

}
