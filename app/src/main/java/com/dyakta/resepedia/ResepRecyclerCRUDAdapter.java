package com.dyakta.resepedia;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ResepRecyclerCRUDAdapter extends RecyclerView.Adapter<ResepRecyclerCRUDAdapter.ViewHolder> {

    public List<ResepPost> resep_list;
    public List<Admin> admin_list;
    public Context context;
    private List<String> arrayBahan = new ArrayList<>();

//    private OnItemClickCallback onItemClickCallback;
//
//    void setOnItemClickCallback(OnItemClickCallback onItemClickCallback) {
//        this.onItemClickCallback = onItemClickCallback;
//    }

    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;

    public ResepRecyclerCRUDAdapter(List<ResepPost> resep_list, List<Admin> admin_list) {
        this.resep_list = resep_list;
        this.admin_list = admin_list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.resep_crud,viewGroup,false);

        context = viewGroup.getContext();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        CollectionReference subjectRef = firebaseFirestore.collection("Bahan");
        subjectRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        String subject = document.getString("nama");
                        if (arrayBahan.size() < task.getResult().size()) {
                            arrayBahan.add(subject);
                        }
                    }
                }
            }
        });
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, final int position) {

        viewHolder.setIsRecyclable(false);

        final String resepPostId = resep_list.get(position).ResepPostId;
        final String currentUserId = firebaseAuth.getCurrentUser().getUid();
        final String resep_user_id = resep_list.get(position).getUser_id();

        if (resep_user_id.equals(currentUserId)) {
            String judul_data = resep_list.get(position).getJudul();
            viewHolder.setJudulText(judul_data);


            viewHolder.edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, EditResepActivity.class);
                    intent.putExtra("resep", resep_list.get(position));
                    intent.putStringArrayListExtra("bahan", (ArrayList<String>) arrayBahan);
                    context.startActivity(intent);
                }
            });

            viewHolder.delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setMessage("Apakah anda yakin menghapus resep ini ?")
                            .setPositiveButton("Hapus", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    // FIRE ZE MISSILES!
                                    firebaseFirestore.collection("Resep").document(resep_list.get(position).getId()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            resep_list.remove(position);


                                            notifyDataSetChanged();
                                        }
                                    });
                                }
                            })
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    // User cancelled the dialog
                                    dialog.cancel();
                                }
                            });


                    AlertDialog dialog = builder.create();
                    dialog.show();




                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return resep_list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private View mView;
        private TextView descView;
        private ImageButton edit,delete;
        private TextView judulView;
        private TextView blogUserName;
        private CircleImageView blogUserImage;



        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;

            edit = mView.findViewById(R.id.btn_edit);
            delete = mView.findViewById(R.id.btn_hapus);
        }

        public void setJudulText(String judulText){

            judulView = mView.findViewById(R.id.txt_judul_resep);
            judulView.setText(judulText);

        }



//
//        public void setUserData(String userName, String userImage) {
//            blogUserImage = mView.findViewById(R.id.resep_user_image);
//            blogUserName = mView.findViewById(R.id.txt_name_post);
//            blogUserName.setText(userName);
//            RequestOptions placeholderOption = new RequestOptions();
//            placeholderOption.placeholder(R.drawable.user_male);
//
//            Glide.with(context).applyDefaultRequestOptions(placeholderOption).load(userImage).into(blogUserImage);
//        }
    }

    }
//    public interface OnItemClickCallback{
//        void klik(ResepPost data);
//    }




