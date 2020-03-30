package com.eneserdogan.unistore;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;


public class dashboardFragment extends Fragment {
    String[] kategori={"Elektronik","Beyaz Eşya","Mobilya"};


    String kategoriSecim;
    ListView LvKategori;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;

    View view;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public dashboardFragment() {
    }


    public static dashboardFragment newInstance(String param1, String param2) {
        dashboardFragment fragment = new dashboardFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        view= inflater.inflate(R.layout.fragment_dashboard, container, false);

        firebaseFirestore=FirebaseFirestore.getInstance();
        firebaseAuth=FirebaseAuth.getInstance();
        LvKategori=view.findViewById(R.id.LvİlanKategori);

        final ArrayAdapter<String> adapter;
        adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1,kategori);
        LvKategori.setAdapter(adapter);

        LvKategori.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                kategoriSecim=(String) kategori[position];
                System.out.println("Sonuc ="+kategoriSecim);
            }
        });
        return view;
    }
}
