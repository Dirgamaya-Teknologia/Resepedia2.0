package com.dyakta.resepedia;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class BahanRecyclerCRUDAdapter extends RecyclerView.Adapter<BahanRecyclerCRUDAdapter.ViewHolder> {

    public List<Bahan> bahan_list;
    public Context context;

//    private OnItemClickCallback onItemClickCallback;
//
//    void setOnItemClickCallback(OnItemClickCallback onItemClickCallback) {
//        this.onItemClickCallback = onItemClickCallback;
//    }

    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;

    public BahanRecyclerCRUDAdapter(List<Bahan> bahan_list) {
        this.bahan_list = bahan_list;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.bahan_crud,viewGroup,false);

        context = viewGroup.getContext();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, final int position) {

        viewHolder.setIsRecyclable(false);


//        final String currentUserId = firebaseAuth.getCurrentUser().getUid();
//        final String b = bahan_list.get(position).getUser_id();

            String judul_data = bahan_list.get(position).getNama();
            viewHolder.setJudulText(judul_data);


            viewHolder.edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });

            viewHolder.delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    firebaseFirestore.collection("Bahan").document().delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            bahan_list.remove(position);


                            notifyDataSetChanged();
                        }
                    });
                }
            });


    }

    @Override
    public int getItemCount() {
        return bahan_list.size();
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

            judulView = mView.findViewById(R.id.txt_judul_bahan);
            judulView.setText(judulText);

        }




        public void setUserData(String userName, String userImage) {
            blogUserImage = mView.findViewById(R.id.resep_user_image);
            blogUserName = mView.findViewById(R.id.txt_name_post);
            blogUserName.setText(userName);
            RequestOptions placeholderOption = new RequestOptions();
            placeholderOption.placeholder(R.drawable.user_male);

            Glide.with(context).applyDefaultRequestOptions(placeholderOption).load(userImage).into(blogUserImage);
        }
    }

    }
//    public interface OnItemClickCallback{
//        void klik(ResepPost data);
//    }




