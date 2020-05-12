package com.eneserdogan.unistore;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.eneserdogan.unistore.Adapters.GetAdvertisementAdapter;
import com.eneserdogan.unistore.Models.Advertisement;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.lang.reflect.Constructor;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import static androidx.constraintlayout.widget.Constraints.TAG;
interface arayüz{
    void deneme(Advertisement advertisement);
}

public class homeFragment extends Fragment implements arayüz {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static ArrayList<Advertisement> advertisements = new ArrayList<>();
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;
    private List<String> documents;
    GetAdvertisementAdapter getAdvertisementAdapter;
    RecyclerView recyclerView;


    View view;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public homeFragment() {
        // Required empty public constructor
    }


    public static homeFragment newInstance(String param1, String param2) {
        homeFragment fragment = new homeFragment();
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
        view=inflater.inflate(R.layout.fragment_home, container, false);
        documents=new ArrayList<>();
        recyclerView=view.findViewById(R.id.recyclerViewHome);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        firebaseFirestore=FirebaseFirestore.getInstance();
        firebaseAuth=FirebaseAuth.getInstance();
        getDocumentId();

        System.out.println("oncreatede");





        //getSubDocument();
        return view;
    }

    public void getDocumentId(){
        firebaseFirestore.collection("advertisement")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (final QueryDocumentSnapshot document : task.getResult()) {
                                String baslik = document.getString("title");
                                String fiyat = document.getString("price");
                                String açıklama = document.getString("description");
                                String kategori=document.getString("category");
                                String mail=document.getString("mail");
                                String id=document.getId();
                                System.out.println("1.getdocument"+id);

                                Advertisement advertisement=new Advertisement(id,baslik,açıklama,kategori,fiyat,mail);
                                getSubDocument(advertisement);
                                //System.out.println("çıktı "+advertisement.getUrlname()+advertisement.getId());

                            }
                        } else {
                            Log.w("Error getting documents", task.getException());
                        }
                    }
                });


    }
    public void getSubDocument(final Advertisement adver){

        firebaseFirestore.collection("advertisement").document(adver.getId()).collection("pictures").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String url = document.getString("urlPicture");
                                System.out.println("1.url" + url);
                                adver.add(url);
                            }
                            getAdvertisementAdapter=new GetAdvertisementAdapter(getContext(),advertisements);

                            recyclerView.setAdapter(getAdvertisementAdapter);
                            System.out.println("1.getsubdocument");

                            deneme(adver);
                        } else {
                            Log.w("Error getting documents", task.getException());
                        }
                    }
                });

    }

    @Override
    public void deneme(Advertisement advertisement) {
        advertisements.add(advertisement);

        System.out.println("1.deneme");

        //System.out.println("SELAMIN AS" + advertisement.getUrlname() + "ID Si de bu = "  + advertisement.getId());
        //System.out.println("arraydaki url "+advertisement.getUrlname());
        //Burada table reload

    }
/*
    public void downloadImage(){
        FirebaseStorage storage=FirebaseStorage.getInstance();
        StorageReference imageRef=storage.getReference()
                .child("userEmail")
                .child("advertisement")
                .child("belge adı")
                .child("resim adı");

        imageRef.getBytes(1024*1024)
                .addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        Bitmap bitmap= BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                        ımageview.setImageBitmap(bitmap);
                    }
                });

    }*/



}


