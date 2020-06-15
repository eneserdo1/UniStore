package com.eneserdogan.unistore;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import android.os.Environment;
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

import com.bumptech.glide.Glide;
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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
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

    private final int RESULT_LOAD_IMAGE = 48;
    private final int REQUEST_IMAGE_CAPTURE = 38;

    View view;
    EditText UserName;
    EditText UserMail;
    AutoCompleteTextView UserUniversite;
    Button btnDüzenle;
    Button btnKaydet;
    CircleImageView imgProfilePicture;

    FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    StorageReference storageReference = FirebaseStorage.getInstance().getReference();

    String namePP = "";
    String urlPP = "";
    boolean isExistsPhoto = false;
    byte[] compressed = null;

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
    public void onResume() {
        super.onResume();

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
                Log.d(TAG, "onClick: tıkla " + imgProfilePicture.isClickable());
            }
        });

        btnKaydet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(UserUniversite.getText().toString().trim().length() == 0 || UserName.getText().toString().trim().length() == 0){
                    Toast.makeText(getContext(),"LÜtfen Gerekli Alanları Doldurunuz",Toast.LENGTH_LONG).show();
                }else {
                    String university = UserUniversite.getText().toString();

                    if (controlUniversity(university)){
                        uploadPhoto();
                    } else {
                        Toast.makeText(getActivity(), "Lütfen geçerli bir üniversite adı giriniz.", Toast.LENGTH_SHORT).show();
                    }
                }
                duzenlemeyiAc(false);
                Log.d(TAG, "onClick: tıkla" + imgProfilePicture.isClickable());
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view= inflater.inflate(R.layout.fragment_notifications, container, false);

        loadWidgets();

        getData();
        Log.d(TAG, "onClick: tıkla" + imgProfilePicture.isClickable());

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getContext(),
                android.R.layout.select_dialog_item, getResources().getStringArray(R.array.universite));
        UserUniversite.setThreshold(1);
        UserUniversite.setAdapter(arrayAdapter);

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
        if (isExistsPhoto){
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            final CharSequence[] items = {"Yeni Fotoğraf", "Fotoğrafı Sil",
                    "İptal"};
            builder.setTitle("Fotoğrafı Düzenle");
            builder.setItems(items, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int item) {
                    if (items[item].equals("Yeni Fotoğraf")) {
                        newPhotoAlert();
                        dialog.dismiss();
                    } else if (items[item].equals("Fotoğrafı Sil")) {
                        deletePhotoOnView();
                    } else if (items[item].equals("İptal")) {
                        dialog.dismiss();
                    }
                }
            });
            builder.show();
        }
        else{
            newPhotoAlert();
        }
    }

    private void newPhotoAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        final CharSequence[] items = {"Fotoğraf Çek", "Galeriden Seç",
                "İptal"};
        builder.setTitle("Fotoğraf Ekle");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("Fotoğraf Çek")) {
                    activeTakePhoto();
                } else if (items[item].equals("Galeriden Seç")) {
                    activeGallery();
                } else if (items[item].equals("İptal")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    private void activeGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, RESULT_LOAD_IMAGE);
    }

    private void activeTakePhoto() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getContext().getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(getContext(),
                        getContext().getPackageName() + ".fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    String currentPhotoPath;

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case RESULT_LOAD_IMAGE:
                if (requestCode == RESULT_LOAD_IMAGE && resultCode == Activity.RESULT_OK & null != data) {

                    keepImage(data.getData());
                    Glide.with(this).load(data.getData()).into(imgProfilePicture);

                }

                break;

            case REQUEST_IMAGE_CAPTURE:
                if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {

                    Uri mData = FileProvider.getUriForFile(getContext(), getContext().getPackageName() + ".fileprovider", new File(currentPhotoPath));

                    Glide.with(this).load(mData).into(imgProfilePicture);
                    keepImage(mData);

                }
        }

    }

    private void keepImage(Uri data) {
        compressed = compress(data);
    }

    private byte[] compress(Uri uri) {
        Bitmap bitmap = null;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), uri);
        } catch (IOException e) {
            e.printStackTrace();
        }
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        Log.d(TAG, "before compress: " + bitmap.getByteCount()/1024 + "KB");
        bitmap.compress(Bitmap.CompressFormat.JPEG, 25, stream);
        Log.d(TAG, "after compress: " + stream.toByteArray().length/1024 + "KB");
        return stream.toByteArray();
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
                            isExistsPhoto = false;
                            imgProfilePicture.setImageResource(R.drawable.profile);
                        }else{
                            isExistsPhoto = true;
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

    private void uploadPhoto() {
        if (compressed != null){
            final String randName = RandomName.randImageName() + ".jpg";
            final StorageReference refStorage = storageReference
                    .child(firebaseUser.getEmail())
                    .child(path_of_PP)
                    .child(randName);
            UploadTask uploadTask = refStorage.putBytes(compressed);
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
                        uploadProfile(randName, downloadUri.toString());
                    }
                    else {
                        Toast.makeText(getContext(), "Resim yüklenemedi, güncelleme başarısız.", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "Resim upload edilemedi.");
                    }
                }
            });
        }
        else{
            Log.d(TAG, "uploadPhoto: namePp:" + namePP);
            uploadProfile(namePP,  urlPP);
        }
        deletePhotoCompletely();
    }

    private void uploadProfile(String imgname, String uri) {
        String name = UserName.getText().toString();
        String university = UserUniversite.getText().toString();

        Map<String, Object> updatedData = new HashMap<>();
        updatedData.put("name", name);
        updatedData.put("university", university);
        updatedData.put("picture", new Picture(imgname, uri));

        firebaseFirestore.collection("users").
                document(firebaseUser.getUid()).update(updatedData).
                addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getActivity(), "Başarıyla güncellendi.", Toast.LENGTH_SHORT).show();

                        getData();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getActivity(), "Güncelleme başarısız.", Toast.LENGTH_SHORT).show();

                getData();
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

    private void deletePhotoOnView() {
        Log.d(TAG, "deletePhotoOnView: Fotoğraf arayüzden silindi.");
        clearCompressed();
        imgProfilePicture.setImageResource(R.drawable.profile);
        namePP = "";
        urlPP = "";
    }
    
    private void deletePhotoCompletely(){
        Log.d(TAG, "deletePhotoCompletely: Fotoğraf tamamen silindi");
        deletePhotoOnView();
        deletePhotoFromStorage(namePP);
    }

    private void clearCompressed(){
        compressed = null;
    }

    private void deletePhotoFromStorage(String namePP){
        if (!namePP.equals("")){
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
        else {
            Log.d(TAG, "deletePhotoFromStorage: Resim zaten yok.");
        }
    }

}

