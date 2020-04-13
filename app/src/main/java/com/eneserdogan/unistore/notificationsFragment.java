package com.eneserdogan.unistore;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.eneserdogan.unistore.Utils.RandomName;
import com.google.android.gms.tasks.Continuation;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;




public class notificationsFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;

    View view;
    EditText UserName;
    EditText UserMail;
    AutoCompleteTextView UserUniversite;
    Button btnDüzenle;
    Button btnKaydet;
    CircleImageView imgProfilePicture;

    FirebaseUser firebaseUser;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;
    StorageReference storageReference;

    String userID;
    String urlProfilePicture = "";

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
        storageReference = FirebaseStorage.getInstance().getReference();

        loadWidgets();

        getData();

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getContext(),
                android.R.layout.select_dialog_item, getResources().getStringArray(R.array.universite));
        UserUniversite.setThreshold(1);
        UserUniversite.setAdapter(arrayAdapter);

        imgProfilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });

        btnDüzenle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                duzenlemeyiAc(true);
            }
        });


        btnKaydet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadData();
                duzenlemeyiAc(false);
            }
        });

        return view;
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, 5);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 5 && resultCode == Activity.RESULT_OK) {
            final Uri filePath = data.getData();
            Log.i("TAG", "resim: " + filePath);
            final String randName = RandomName.randImageName();
            Log.i("TAG", "random: " + randName);

            final StorageReference refStorage = storageReference.child("profilePictures").child(randName + ".jpg");
            UploadTask uploadTask = refStorage.putFile(filePath);
            Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()){
                        throw task.getException();
                    }

                    return refStorage.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()){
                        Uri downloadUri = task.getResult();
                        urlProfilePicture = downloadUri.toString();
                        Log.i("TAG", "url: " + urlProfilePicture);
                        Toast.makeText(getContext(), "Resim yükleme başarılı!", Toast.LENGTH_SHORT).show();
                        Picasso.get().load(urlProfilePicture).resize(500,500).into(imgProfilePicture);
                        uploadData();
                    }

                    else {
                        Toast.makeText(getContext(), "Resim yüklenemedi!", Toast.LENGTH_SHORT).show();
                        imgProfilePicture.setImageResource(R.drawable.profile);
                    }
                }
            });
        }
    }

    void loadWidgets(){
        UserName=view.findViewById(R.id.etProfilAdsoyad);
        UserMail=view.findViewById(R.id.etProfilMail);
        UserUniversite=view.findViewById(R.id.autoUniversity);
        btnDüzenle=view.findViewById(R.id.btnProfilDüzenle);
        btnKaydet=view.findViewById(R.id.btnProfilKaydet);
        imgProfilePicture = view.findViewById(R.id.imgPP);

        UserMail.setEnabled(false);

        btnKaydet.setVisibility(View.GONE);
        UserName.setEnabled(false);
        UserUniversite.setEnabled(false);
        imgProfilePicture.setClickable(false);
    }

    private void duzenlemeyiAc(boolean durum) {
        if (durum){
            UserName.setEnabled(true);
            UserUniversite.setEnabled(true);
            btnKaydet.setVisibility(View.VISIBLE);
            btnDüzenle.setVisibility(View.GONE);
            imgProfilePicture.setClickable(true);
        }
        else{
            UserName.setEnabled(false);
            UserUniversite.setEnabled(false);
            btnKaydet.setVisibility(View.GONE);
            btnDüzenle.setVisibility(View.VISIBLE);
            imgProfilePicture.setClickable(false);
        }
    }

    public void getData(){
        final String userMail = firebaseUser.getEmail();
        final String[] userName = new String[1];
        final String[] userUni = new String[1];

        firebaseFirestore.collection("users")
                .whereEqualTo("email", userMail)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            Log.d("users length: ", String.valueOf(task.getResult().size()));

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                userName[0] = document.getString("adSoyad");
                                userUni[0] = document.getString("universite");
                                urlProfilePicture = document.getString("resim");
                                userID = document.getId();

                            }
                            UserName.setText(userName[0]);
                            UserMail.setText(userMail);
                            UserUniversite.setText(userUni[0]);

                            if (urlProfilePicture.equals("")){
                                imgProfilePicture.setImageResource(R.drawable.profile);
                            }else{
                                Picasso.get().load(urlProfilePicture).resize(500,500).into(imgProfilePicture);
                            }
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
        String gidenÜniversite= UserUniversite.getText().toString();

        Map<String, Object> user = new HashMap<>();
        user.put("adSoyad",gidenAd);
        user.put("email",gidenMail);
        user.put("universite",gidenÜniversite);
        user.put("resim", urlProfilePicture);


        DocumentReference washingtonRef = db.collection("users").document(userID);

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

        getData();
    }
}

