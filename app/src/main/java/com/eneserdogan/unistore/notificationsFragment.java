package com.eneserdogan.unistore;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;


import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;


public class notificationsFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;

    View view;
    FirebaseUser firebaseUser;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;

    String kullanıcıMail;
    EditText UserName;
    EditText UserMail;
    EditText UserÜniversite;
    Button btnDüzenle;
    Button btnKaydet;
    String ıd;

    String gelenAd;
    String gelenUniversite;


    public notificationsFragment() {
    }


    public static notificationsFragment newInstance(String param1, String param2) {
        notificationsFragment fragment = new notificationsFragment();
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

        view= inflater.inflate(R.layout.fragment_notifications, container, false);

        firebaseFirestore=FirebaseFirestore.getInstance();
        firebaseAuth=FirebaseAuth.getInstance();
        firebaseUser=firebaseAuth.getCurrentUser();

        kullanıcıMail=firebaseUser.getEmail();

        UserName=view.findViewById(R.id.etProfilAdsoyad);
        UserMail=view.findViewById(R.id.etProfilMail);
        UserÜniversite=view.findViewById(R.id.etProfilÜniversite);
        btnDüzenle=view.findViewById(R.id.btnProfilDüzenle);
        btnKaydet=view.findViewById(R.id.btnProfilKaydet);

        btnKaydet.setVisibility(View.INVISIBLE);
        getData();

        btnDüzenle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnKaydet.setVisibility(View.VISIBLE);

            }
        });

        btnKaydet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadData();
                btnKaydet.setVisibility(View.INVISIBLE);
            }
        });
        return view;
    }

    public void getData(){
        firebaseFirestore.collection("users")
                .whereEqualTo("email",kullanıcıMail)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                gelenAd = document.getString("adSoyad");
                                gelenUniversite = document.getString("Üniversite");
                                ıd=document.getId();
                            }
                            UserName.setText(gelenAd);
                            UserMail.setText(kullanıcıMail);
                            UserÜniversite.setText(gelenUniversite);
                        } else {
                            Log.w("Error getting documents", task.getException());
                        }
                    }
                });

    }


    public void uploadData(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String gidenAd=UserName.getText().toString();
        String gidenMail=UserMail.getText().toString();
        String gidenÜniversite=UserÜniversite.getText().toString();

        Map<String, Object> user = new HashMap<>();
        user.put("adSoyad",gidenAd);
        user.put("email",gidenMail);
        user.put("Üniversite",gidenÜniversite);


        DocumentReference washingtonRef = db.collection("users").document(ıd);

        // Set the "isCapital" field of the city 'DC'
        washingtonRef
                .update(user)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getActivity(),"Başarıyla Güncellendi",Toast.LENGTH_LONG).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getActivity(),"Hata Oluştu Güncellenemedi",Toast.LENGTH_LONG).show();
                    }
                });

    }
}
