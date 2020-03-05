package com.dyakta.resepedia;


import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Parcelable;
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
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {
    private RecyclerView resep_list_view;
    private List<ResepPost> resep_list;
    private List<Admin> adminList;


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
        adminList = new ArrayList<>();
        resep_list_view = view.findViewById(R.id.resep_list_item);

        resepRecyclerAdapter = new ResepRecyclerAdapter(resep_list,adminList);
        resep_list_view.setLayoutManager(new LinearLayoutManager(container.getContext()));
        resep_list_view.setAdapter(resepRecyclerAdapter);

        firebaseAuth = FirebaseAuth.getInstance();
        resepRecyclerAdapter.setOnItemClickCallback(new OnItemClickCallback() {
            @Override
            public void klik(ResepPost data) {
                showData(data);
            }
        });
//        resepRecyclerAdapter.setOnItemClickCallback(new ResepRecyclerAdapter().OnItemClickCallback() {
//            @Override
//            public void klik(ResepPost data) {
//                showData(data);
//            }
//        });

        if (firebaseAuth.getCurrentUser() != null){

            firebaseFirestore = FirebaseFirestore.getInstance();

            resep_list_view.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);

                    boolean reacheBottom = !recyclerView.canScrollVertically(1);
                    if (reacheBottom){
                        loadPost();
                    }
                }
            });

            Query firstQuery = firebaseFirestore.collection("Resep").orderBy("judul", Query.Direction.ASCENDING).limit(30);
            firstQuery.addSnapshotListener((queryDocumentSnapshots, e) -> {
                if (e == null) {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        if (isFirstPageFirstLoad) {
                            lastVisible = queryDocumentSnapshots.getDocuments().get(queryDocumentSnapshots.size() - 1);
                            resep_list.clear();
                            adminList.clear();
                        }

                        for (DocumentChange documentChange : queryDocumentSnapshots.getDocumentChanges()) {
                            if (documentChange.getType() == DocumentChange.Type.ADDED) {
                                String resepPostId = documentChange.getDocument().getId();
                                final ResepPost resepPost = documentChange.getDocument().toObject(ResepPost.class).withId(resepPostId);

                                String resepUserId = documentChange.getDocument().getString("user_id");
                                if (resepUserId != null) {
                                    firebaseFirestore.collection("Admin").document(resepUserId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                            if (task.isSuccessful()) {
                                                Admin admin = task.getResult().toObject(Admin.class);
                                                if (isFirstPageFirstLoad) {
                                                    try {
                                                        adminList.add(admin);
                                                        resep_list.add(resepPost);
                                                    }catch (Exception e){
                                                        e.printStackTrace();
                                                    }


                                                } else {
                                                    try {
                                                        adminList.add(0, admin);
                                                        resep_list.add(0, resepPost);
                                                    }catch (Exception ee){
                                                        ee.printStackTrace();
                                                    }

                                                }

                                                resepRecyclerAdapter.notifyDataSetChanged();
                                            }
                                        }
                                    });
                                }
                            }
                            isFirstPageFirstLoad = false;
                        }
                    }
                }
            });
        }

        return view;
    }

    private void showData(ResepPost data) {
        Intent intent = new Intent(getActivity(), DetailActivity.class);
//        intent.putExtra(DetailActivity.EXTRA_GAMBAR,data.getImage_url());
//        intent.putExtra(DetailActivity.EXTRA_THUMB,data.getThumb());
//        intent.putExtra(DetailActivity.EXTRA_JUDUL, data.getJudul());
//        intent.putExtra(DetailActivity.EXTRA_DESC, data.getDesc());
//        intent.pu(DetailActivity.EXTRA_QUANTITY,  data.getQuantitas());
//        intent.putStringArrayListExtra(DetailActivity.EXTRA_BAHAN, (ArrayList<String>) data.getBahan());
//        intent.putExtra(DetailActivity.EXTRA_LANGKAH, data.getLangkah());
        intent.putExtra("resep", data);
        startActivity(intent);
    }

    private void loadPost() {
        if (firebaseAuth.getCurrentUser() != null){
            Query nextQuery = firebaseFirestore.collection("Resep").
                    orderBy("judul", Query.Direction.ASCENDING)
                    .startAfter(lastVisible).limit(30);

            nextQuery.addSnapshotListener(getActivity(), new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                    if (!queryDocumentSnapshots.isEmpty()){
                        lastVisible = queryDocumentSnapshots.getDocuments().get(queryDocumentSnapshots.size() - 1);
                        for (DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()){
                            if (doc.getType() == DocumentChange.Type.ADDED){
                                String resepPostId = doc.getDocument().getId();
                                final ResepPost resepPost = doc.getDocument().toObject(ResepPost.class).withId(resepPostId);
                                String resepUserId = doc.getDocument().getString("user_id");

                                firebaseFirestore.collection("Admin").document(resepUserId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if (task.isSuccessful()){
                                            try {
                                            Admin admin = task.getResult().toObject(Admin.class);


                                                adminList.add(admin);
                                                resep_list.add(resepPost);
                                            }catch (Exception e){
                                                e.printStackTrace();
                                            }


                                            resepRecyclerAdapter.notifyDataSetChanged();
                                        }
                                    }
                                });
                            }
                        }
                    }
                }
            });
        }
    }

}
