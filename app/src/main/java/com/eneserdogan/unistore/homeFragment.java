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
import com.google.android.gms.tasks.OnFailureListener;
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
import java.net.URL;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class homeFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;

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

        firebaseFirestore=FirebaseFirestore.getInstance();
        firebaseAuth=FirebaseAuth.getInstance();

        recyclerView=view.findViewById(R.id.recyclerViewHome);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        getAdvertisementAdapter=new GetAdvertisementAdapter(getContext());
        recyclerView.setAdapter(getAdvertisementAdapter);

        getAdvertisements();

        System.out.println("oncreatede");
        return view;
    }

    private void getAdvertisements() {
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
                                String uuid=document.getString("uuid");

                                Advertisement adver = new Advertisement(id,baslik,açıklama,kategori,fiyat,mail,uuid);
                                getPhotoUrl(adver);
                            }

                        } else {
                            Log.w("Error getting documents", task.getException());
                        }
                    }
                });
    }

    public void getPhotoUrl(final Advertisement adver){

        firebaseFirestore.collection("advertisement").document(adver.getId())
                .collection("pictures").document(adver.getId() + "_resim" + 1)
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                String URL = documentSnapshot.getString("urlPicture");
                addtoRecAdapter(adver, URL);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "onFailure: resim çekmede hata");
            }
        });
    }

    private void addtoRecAdapter(Advertisement advertisement, String photoUrl) {
        getAdvertisementAdapter.addElements(advertisement, photoUrl);
        getAdvertisementAdapter.notifyDataSetChanged();
    }
}


