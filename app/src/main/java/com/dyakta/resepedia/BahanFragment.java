package com.dyakta.resepedia;


import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class BahanFragment extends Fragment {
    private FirebaseAuth mAuth;
    private RecyclerView bahan_list_view;
    private List<Bahan> bahan_list;
    private BahanRecyclerCRUDAdapter bahanRecyclerCRUDAdapter;
    private FirebaseFirestore firebaseFirestore;

    private TextView btn_tambahBahan;

    public BahanFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_bahan, container, false);
        bahan_list = new ArrayList<>();

        bahan_list_view = view.findViewById(R.id.rv_Bahan);

        bahanRecyclerCRUDAdapter = new BahanRecyclerCRUDAdapter(bahan_list);
        bahan_list_view.setLayoutManager(new LinearLayoutManager(container.getContext()));
        bahan_list_view.setAdapter(bahanRecyclerCRUDAdapter);

//        panduanRecyclerAdapter.setOnItemClickCallback(new PanduanRecyclerAdapter.OnItemClickCallback() {
//            @Override
//            public void klik(Tips data) {
//                showData(data);
//            }
//        });

        firebaseFirestore = FirebaseFirestore.getInstance();

        firebaseFirestore.collection("Bahan").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e == null) {
                    for (DocumentChange documentChange : queryDocumentSnapshots.getDocumentChanges()) {
                        if (documentChange.getType() == DocumentChange.Type.ADDED) {
                            firebaseFirestore.collection("Bahan").document().get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    Bahan bahan = documentChange.getDocument().toObject(Bahan.class);
                                    if (task.isSuccessful()) {
                                        bahan_list.add(bahan);
                                    } else {
                                        bahan_list.add(0, bahan);
                                    }
                                    bahanRecyclerCRUDAdapter.notifyDataSetChanged();
                                }

                            });

                        }
                    }
                }
            }
        });

        btn_tambahBahan = view.findViewById(R.id.btn_txt_tambah_bahan);
        btn_tambahBahan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), TambahBahanActivity.class);
                intent.putExtra("called", "tambah");
                startActivity(intent);
            }
        });

        return view;
    }

}
