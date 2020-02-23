package com.dyakta.resepedia;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
public class HomeFragment extends Fragment {
    private RecyclerView resep_list_view;
    private List<ResepPost> resep_list;


    private FirebaseAuth firebaseAuth;

    private FirebaseFirestore firebaseFirestore;
    private ResepRecyclerAdapter resepRecyclerAdapter;

    private DocumentSnapshot lastVisible;
    private Boolean isFirstPageFirstLoad = true;


    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_home, container, false);

        resep_list = new ArrayList<>();
        resep_list_view = view.findViewById(R.id.resep_list_item);

        resepRecyclerAdapter = new ResepRecyclerAdapter(resep_list);
        resep_list_view.setLayoutManager(new LinearLayoutManager(container.getContext()));
        resep_list_view.setAdapter(resepRecyclerAdapter);

//        resepRecyclerAdapter.setOnItemClickCallback(new PanduanRecyclerAdapter.OnItemClickCallback() {
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
                                    resepRecyclerAdapter.notifyDataSetChanged();
                                }

                            });

                        }
                    }
                }
            }
        });
        return view;
    }

}
