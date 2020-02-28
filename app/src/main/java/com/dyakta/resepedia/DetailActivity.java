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

import de.hdodenhof.circleimageview.CircleImageView;

public class DetailActivity extends AppCompatActivity {

    public static final String EXTRA_GAMBAR = "extra_gambar";
    public static final String EXTRA_THUMB = "extra_thumb";
    public static final String EXTRA_JUDUL = "extra_judul";
    public static final String EXTRA_DESC = "extra_desc";
    public static final String EXTRA_BAHAN = "extra_bahan";
    public static final String EXTRA_LANGKAH = "extra_langkah";
    public static final Double EXTRA_QUANTITY = 0.0;
    private EditText et_porsi;
    private Button btn_hitung;
    Double hasil,v1;

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
        double kuantitasResep = getIntent().getDoubleExtra(String.valueOf(EXTRA_QUANTITY),0.0);
        String bahanMakan = getIntent().getStringExtra(EXTRA_BAHAN);
        String langkahMasak = getIntent().getStringExtra(EXTRA_LANGKAH);


        String gbr = gambarResep;
        String thumb = thumbResep;
        String text =  judulResep;
        String text2 =  deskripsiResep;
        double textKuantitas =  kuantitasResep;
        String text4 =  bahanMakan;
        String text5 =  langkahMasak;


        RequestOptions requestOptions = new RequestOptions();

        requestOptions.placeholder(R.drawable.user_male);
        Glide.with(DetailActivity.this).applyDefaultRequestOptions(requestOptions).load(gbr).thumbnail(
                Glide.with(DetailActivity.this).load(thumb)
        ).into(gambar);



        setBlogImage(gbr,thumb);
        judul.setText(text);
        desc.setText(text2);
        bahan.setText(text4);
        langkah.setText(text5);
        kuantitas.setText(String.valueOf(textKuantitas));

        btn_hitung.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v1 = Double.parseDouble(et_porsi.getText().toString());
                hasil = v1 * textKuantitas;
                String porsi = Double.toString(hasil);
                kuantitas.setText(porsi);
                finish();

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
