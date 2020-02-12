package com.dyakta.resepedia;


import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 */
public class BahanFragment extends Fragment {

    private TextView btn_tambahBahan;

    public BahanFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_bahan, container, false);

        btn_tambahBahan = view.findViewById(R.id.btn_txt_tambah_bahan);
        btn_tambahBahan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), TambahBahanActivity.class);
                startActivity(intent);
            }
        });

        return view;
    }

}
