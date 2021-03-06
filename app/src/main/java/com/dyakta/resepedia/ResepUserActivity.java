package com.dyakta.resepedia;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;

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

public class ResepUserActivity extends AppCompatActivity {
    private RecyclerView resep_list_view;
    private List<ResepPost> resep_list;
    private List<Admin> adminList;


    private FirebaseAuth firebaseAuth;

    private FirebaseFirestore firebaseFirestore;
    private ResepRecyclerAdapter resepRecyclerAdapter;

    private DocumentSnapshot lastVisible;
    private Boolean isFirstPageFirstLoad = true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resep_user);
        resep_list = new ArrayList<>();
        adminList = new ArrayList<>();
        resep_list_view = findViewById(R.id.resep_list_user);

        resepRecyclerAdapter = new ResepRecyclerAdapter(resep_list,adminList);
        resep_list_view.setLayoutManager(new LinearLayoutManager(this));
        resep_list_view.setAdapter(resepRecyclerAdapter);

        firebaseAuth = FirebaseAuth.getInstance();
        resepRecyclerAdapter.setOnItemClickCallback(new OnItemClickCallback() {
            @Override
            public void klik(ResepPost data) {
                showData(data);
            }
        });

        if (firebaseAuth.getCurrentUser() == null){

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

                                                    adminList.add(admin);
                                                    resep_list.add(resepPost);
                                                } else {
                                                    adminList.add(0, admin);
                                                    resep_list.add(0, resepPost);
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

    }

    private void loadPost() {
        if (firebaseAuth.getCurrentUser() == null){
            Query nextQuery = firebaseFirestore.collection("Resep").
                    orderBy("judul", Query.Direction.ASCENDING)
                    .startAfter(lastVisible).limit(30);

            nextQuery.addSnapshotListener(this, new EventListener<QuerySnapshot>() {
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
                                            Admin admin = task.getResult().toObject(Admin.class);

                                            adminList.add(admin);
                                            resep_list.add(resepPost);


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

    private void showData(ResepPost data) {
        Intent intent = new Intent(this, DetailActivity.class);
        intent.putExtra(DetailActivity.EXTRA_GAMBAR,data.getImage_url());
        intent.putExtra(DetailActivity.EXTRA_THUMB,data.getThumb());
        intent.putExtra(DetailActivity.EXTRA_JUDUL, data.getJudul());
        intent.putExtra(DetailActivity.EXTRA_DESC, data.getDesc());
//        intent.putExtra(String.valueOf(DetailActivity.EXTRA_QUANTITY), (Serializable) data.getQuantitas());
        intent.putExtra(DetailActivity.EXTRA_BAHAN, (Parcelable) data.getBahan());
        intent.putExtra(DetailActivity.EXTRA_LANGKAH, data.getLangkah());
        startActivity(intent);
    }
}
