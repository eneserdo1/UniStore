package com.eneserdogan.unistore;

import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;


public class dashboardFragment extends Fragment implements AdapterView.OnItemSelectedListener{

    String kategoriSecim;
    Spinner SpKategori;
    EditText ilanAçıklama;
    EditText ilanBaşlık;
    EditText ilanFiyat;
    String id;
    String mail;
    Button btnilanYükle;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseUser firebaseUser;

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
        id= UUID.randomUUID().toString();

        firebaseFirestore=FirebaseFirestore.getInstance();
        firebaseAuth=FirebaseAuth.getInstance();
        firebaseUser=firebaseAuth.getCurrentUser();

        mail=firebaseUser.getEmail();

        SpKategori=view.findViewById(R.id.SpİlanKategori);
        ilanAçıklama=view.findViewById(R.id.etİlanAcıklama);
        ilanBaşlık=view.findViewById(R.id.etİlanBaşlık);
        ilanFiyat=view.findViewById(R.id.etİlanFiyat);
        btnilanYükle=view.findViewById(R.id.btnİlanYükle);

        ArrayAdapter<CharSequence> adapter=ArrayAdapter.createFromResource(getActivity(),R.array.kategori,android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        SpKategori.setAdapter(adapter);
        SpKategori.setOnItemSelectedListener(this);

        btnilanYükle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String başlık=ilanBaşlık.getText().toString();
                String açıklama=ilanAçıklama.getText().toString();
                String fiyat=ilanFiyat.getText().toString();
                String kategori=kategoriSecim;
                String İlanİd=id;
                String İlanMail=mail;
                uploadData(başlık,açıklama,fiyat,İlanİd,İlanMail,kategori);


            }
        });




        return view;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        kategoriSecim=SpKategori.getSelectedItem().toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }


    public void uploadData(String başlık,String açıklama,String fiyat,String İlanİd,String İlanMail,String kategori){
        FirebaseFirestore db=FirebaseFirestore.getInstance();

        Map<String,Object> ilan=new HashMap<>();
        ilan.put("kategori",kategori);
        ilan.put("başlık",başlık);
        ilan.put("açıklama",açıklama);
        ilan.put("fiyat",fiyat);
        ilan.put("ilanİd",İlanİd);
        ilan.put("userMail",İlanMail);

        db.collection("advertisement")
                .add(ilan)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toast.makeText(getActivity(),"İlan Başarıyla Yüklendi",Toast.LENGTH_LONG).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getActivity(),e.getLocalizedMessage().toString(),Toast.LENGTH_LONG).show();

                    }
                });

    }
}
