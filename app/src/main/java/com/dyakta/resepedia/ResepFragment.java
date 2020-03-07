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
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;


/**
 * A simple {@link Fragment} subclass.
 */
public class ResepFragment extends Fragment {
    private FirebaseAuth mAuth;
    private RecyclerView resep_list_view;
    private List<ResepPost> resep_list;
    private List<Admin> adminList;
    private ResepRecyclerCRUDAdapter resepRecyclerCRUDAdapter;
    private FirebaseFirestore firebaseFirestore;

    private TextView btnTambahResep;

    public ResepFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_resep, container, false);
        resep_list = new ArrayList<>();
        adminList = new ArrayList<>();
        resep_list_view = view.findViewById(R.id.rv_crudResep);

        resepRecyclerCRUDAdapter = new ResepRecyclerCRUDAdapter(resep_list,adminList);
        resep_list_view.setLayoutManager(new LinearLayoutManager(container.getContext()));
        resep_list_view.setAdapter(resepRecyclerCRUDAdapter);

//        panduanRecyclerAdapter.setOnItemClickCallback(new PanduanRecyclerAdapter.OnItemClickCallback() {
//            @Override
//            public void klik(Tips data) {
//                showData(data);
//            }
//        });

        firebaseFirestore = FirebaseFirestore.getInstance();

        firebaseFirestore.collection("Resep").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e == null) {
                    for (DocumentChange documentChange : queryDocumentSnapshots.getDocumentChanges()) {
                        if (documentChange.getType() == DocumentChange.Type.ADDED) {
                            firebaseFirestore.collection("Resep").document().get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    ResepPost resepPost = documentChange.getDocument().toObject(ResepPost.class);

                                    if (task.isSuccessful()) {
                                            resep_list.add(resepPost);
                                    } else {
                                            resep_list.add(0, resepPost);
                                    }
                                    resepRecyclerCRUDAdapter.notifyDataSetChanged();
                                }

                            });

                        }
                    }
                }
            }
        });
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        btnTambahResep = view.findViewById(R.id.btn_txt_tambah_Resep);
        btnTambahResep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), TambahResepActivity.class);
                startActivity(intent);
            }
        });
    }
}
