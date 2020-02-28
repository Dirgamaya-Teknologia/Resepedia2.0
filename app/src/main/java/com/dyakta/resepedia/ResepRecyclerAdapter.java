package com.dyakta.resepedia;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import de.hdodenhof.circleimageview.CircleImageView;

public class ResepRecyclerAdapter extends RecyclerView.Adapter<ResepRecyclerAdapter.ViewHolder> {

    public List<ResepPost> resep_list;
    public List<Admin> admin_list;
    public Context context;

    private OnItemClickCallback onItemClickCallback;

    void setOnItemClickCallback(OnItemClickCallback onItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback;
    }

    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;

    public ResepRecyclerAdapter(List<ResepPost> resep_list, List<Admin> admin_list) {
        this.resep_list = resep_list;
        this.admin_list = admin_list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.resep_list_item,viewGroup,false);

        context = viewGroup.getContext();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, final int position) {

        viewHolder.setIsRecyclable(false);

        final String resepPostId = resep_list.get(position).ResepPostId;
        final String currentUserId = firebaseAuth.getCurrentUser().getUid();


        String judul_data = resep_list.get(position).getJudul();
        viewHolder.setJudulText(judul_data);
        String desc_data = resep_list.get(position).getDesc();
        viewHolder.setDescText(desc_data);
        String image_uri = resep_list.get(position).getImage_url();
        String thumbUri = resep_list.get(position).getThumb();
        viewHolder.setBlogImage(image_uri,thumbUri);
        String userName = admin_list.get(position).getName();
        String userImage = admin_list.get(position).getImage();
        viewHolder.setUserData(userName,userImage);

//
//        try {
//            long milliseconds = resep_list.get(position).getTimestamp().getTime();
//            String dateString = DateFormat.format("dd/MM/yyyy",new Date(milliseconds)).toString();
//            viewHolder.setTime(dateString);
//
//        }catch (Exception e ){
//            Toast.makeText(context,"Error "+ e.getMessage(),Toast.LENGTH_SHORT).show();
//        }

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ResepPost reseplist = resep_list.get(viewHolder.getAdapterPosition());
                onItemClickCallback.klik(reseplist);
            }
        });


//

    }

    @Override
    public int getItemCount() {
        return resep_list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private View mView;
        private TextView descView;
        private TextView judulView;
        private ImageView blogImageView;
        private TextView blogUserName;
        private CircleImageView blogUserImage;



        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;


        }

        public void setJudulText(String judulText){

            judulView = mView.findViewById(R.id.txt_judul_resep);
            judulView.setText(judulText);

        }

        public void setDescText(String descText){

            descView = mView.findViewById(R.id.txt_deskripsi);
            descView.setText(descText);

        }

        public void setBlogImage(String downloadUri,String thumb){
            blogImageView = mView.findViewById(R.id.iv_post_resep);

            RequestOptions requestOptions = new RequestOptions();

            requestOptions.placeholder(R.drawable.user_male);
            Glide.with(context).applyDefaultRequestOptions(requestOptions).load(downloadUri).thumbnail(
              Glide.with(context).load(thumb)
            ).into(blogImageView);

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
    interface OnItemClickCallback{
        void klik(ResepPost data);
    }




