package com.eneserdogan.unistore;


import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.eneserdogan.unistore.Models.Picture;
import com.eneserdogan.unistore.Utils.RandomName;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
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

    private final static String TAG = notificationsFragment.class.getSimpleName();

    private final static String path_of_PP = "profilePicture";

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

    String namePP = "";
    String urlPP = "";

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

            final StorageReference refStorage = storageReference.child(firebaseUser.getEmail()).child("profilePicture").child(randName + ".jpg");
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

                        Log.i(TAG, "url: " + downloadUri);
                        Toast.makeText(getContext(), "Resim yükleme başarılı!", Toast.LENGTH_SHORT).show();

                        Picasso.get().load(downloadUri).resize(500,500).into(imgProfilePicture);

                        if (!namePP.equals("")){
                            deleteOldPhoto(namePP);
                            System.out.println("1resim ismi: " + namePP);
                        }

                        namePP = randName + ".jpg";
                        System.out.println("2resim ismi: " + namePP);
                        urlPP = String.valueOf(downloadUri);

                        uploadData();
                        duzenlemeyiAc(false);
                    }

                    else {
                        Toast.makeText(getContext(), "Resim yüklenemedi!", Toast.LENGTH_SHORT).show();
                        imgProfilePicture.setImageResource(R.drawable.profile);
                    }
                }
            });
        }

    }

    public void getData(){

        firebaseFirestore.
                collection("users").
                document(firebaseUser.getUid()).
                get().
                addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()){

                        UserName.setText(document.getString("name"));
                        UserMail.setText(document.getString("email"));
                        UserUniversite.setText(document.getString("university"));

                        HashMap map = (HashMap) document.get("picture");
                        if (map.get("urlPicture").equals("")){
                            imgProfilePicture.setImageResource(R.drawable.profile);
                        }else{
                            namePP = (String) map.get("namePicture");
                            urlPP = (String) map.get("urlPicture");

                            Picasso.get().load(urlPP).resize(500,500).into(imgProfilePicture);
                        }
                    } else {
                        Log.d(TAG, "Veri bulunamadı.");
                    }
                } else {
                    Log.d(TAG, "İşlem başarısız.");
                }
            }
        });

    }

    public void uploadData(){

        String name=UserName.getText().toString();
        String university= UserUniversite.getText().toString();

        if (name.length() == 0 || university.length() == 0){
            Toast.makeText(getActivity(), "Lütfen alanları boş bırakmayınız.", Toast.LENGTH_SHORT).show();
        } else {
            if (controlUniversity(university)){
                Map<String, Object> updatedData = new HashMap<>();
                updatedData.put("name", name);
                updatedData.put("university", university);
                updatedData.put("picture", new Picture(namePP, urlPP));

                firebaseFirestore.collection("users").
                        document(firebaseUser.getUid()).update(updatedData).
                        addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(getActivity(), "Başarıyla güncellendi.", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getActivity(), "Güncelleme başarısız.", Toast.LENGTH_SHORT).show();

                        getData();
                    }
                });
            } else {
                Toast.makeText(getActivity(), "Lütfen geçerli bir üniversite adı giriniz.", Toast.LENGTH_SHORT).show();
                getData();
            }
        }
    }

    public void deleteOldPhoto(String namePP){

        StorageReference refOfDeleted = storageReference.child(firebaseUser.getEmail()).child(path_of_PP).child(namePP);

        refOfDeleted.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "Silme başarılı");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "Silme başarısız");
            }
        });

    }

    private boolean controlUniversity(String university){
        boolean durum = false;

        for (String str : getResources().getStringArray(R.array.universite)){
            if (str.equals(university)){
                durum = true;
                break;
            }
        }

        return durum;
    }

}

