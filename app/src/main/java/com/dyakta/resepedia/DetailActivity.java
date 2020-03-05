package com.dyakta.resepedia;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class DetailActivity extends AppCompatActivity {

    public static final String EXTRA_GAMBAR = "extra_gambar";
    public static final String EXTRA_THUMB = "extra_thumb";
    public static final String EXTRA_JUDUL = "extra_judul";
    public static final String EXTRA_DESC = "extra_desc";
    public static final String EXTRA_BAHAN = "extra_bahan";
    public static final String EXTRA_LANGKAH = "extra_langkah";
    public static final String EXTRA_QUANTITY = "extra_quantity";
    private EditText et_porsi;
    private Button btn_hitung;
    Double v1;
    List<Double> hasil = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Toolbar detailToolbar = findViewById(R.id.detailToolbar);
//        setupToolbar.setTitle("Edit Akun");
        setSupportActionBar(detailToolbar);

        detailToolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        detailToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        ImageView gambar = findViewById(R.id.imageView9);
        TextView judul = findViewById(R.id.judul_detail_resep);
        TextView desc = findViewById(R.id.txt_deskripsi_detail_resep);
        TextView kuantitas = findViewById(R.id.txt_kuantitas);
        TextView bahan = findViewById(R.id.txt_bahan_detail_resep);
        TextView langkah = findViewById(R.id.txt_langkah_detail_resep);
        btn_hitung = findViewById(R.id.btn_hitung);
        et_porsi = findViewById(R.id.et_porsi);

        String gambarResep = getIntent().getStringExtra(EXTRA_GAMBAR);
        String thumbResep = getIntent().getStringExtra(EXTRA_THUMB);
        String judulResep = getIntent().getStringExtra(EXTRA_JUDUL);
        String deskripsiResep = getIntent().getStringExtra(EXTRA_DESC);
        double[] kuantitasResep = getIntent().getDoubleArrayExtra(EXTRA_QUANTITY);
        //String[] bahanMakan = getIntent().getStringArrayExtra(EXTRA_BAHAN);
        String langkahMasak = getIntent().getStringExtra(EXTRA_LANGKAH);

        ResepPost resepPost = getIntent().getParcelableExtra("resep");

        String gbr = resepPost.getImage_url();
        String thumb = resepPost.getThumb();
        String judul1 =  resepPost.getJudul();
        String desc1 =  resepPost.getDesc();
        List<Double> quantitas = resepPost.getQuantitas();
        List<String> bahanMakan = resepPost.getBahan();
        //double textKuantitas =  kuantitasResep;
        //String text4 =  bahanMakan;
        String langkah1 =  resepPost.getLangkah();


        RequestOptions requestOptions = new RequestOptions();

        requestOptions.placeholder(R.drawable.user_male);
        Glide.with(DetailActivity.this).applyDefaultRequestOptions(requestOptions).load(gbr).thumbnail(
                Glide.with(DetailActivity.this).load(thumb)
        ).into(gambar);



        setBlogImage(gbr,thumb);
        judul.setText(judul1);
        desc.setText(desc1);
        bahan.setText(bahanMakan.toString());
        langkah.setText(langkah1);
        kuantitas.setText(quantitas.toString());

        btn_hitung.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hasil.clear();
                v1 = Double.parseDouble(et_porsi.getText().toString());
                for (int i = 0; i < quantitas.size(); i++) {
                    hasil.add(v1 * quantitas.get(i));
                }
                kuantitas.setText(hasil.toString());
                et_porsi.getText().clear();

            }
        });


    }

    public void setBlogImage(String downloadUri,String thumb){
        ImageView gambar = findViewById(R.id.imageView9);

        RequestOptions requestOptions = new RequestOptions();

        requestOptions.placeholder(R.drawable.user_male);
        Glide.with(this).applyDefaultRequestOptions(requestOptions).load(downloadUri).thumbnail(
                Glide.with(this).load(thumb)
        ).into(gambar);

    }

//    public void setUserImage(String userImageUri){
//        CircleImageView userimage = findViewById(R.id.iv_userImage_detail);
//
//        RequestOptions requestOptions = new RequestOptions();
//
//        requestOptions.placeholder(R.drawable.user_male);
//        Glide.with(this).applyDefaultRequestOptions(requestOptions).load(userImageUri).into(userimage);
//
//    }
}
