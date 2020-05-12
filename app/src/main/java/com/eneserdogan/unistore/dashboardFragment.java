package com.eneserdogan.unistore;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import com.eneserdogan.unistore.Adapters.NewAdvertRecAdapter;
import com.eneserdogan.unistore.Models.Advertisement;
import com.eneserdogan.unistore.Models.Picture;
import com.eneserdogan.unistore.Utils.RandomName;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class dashboardFragment extends Fragment implements NewAdvertRecAdapter.NewAdvertRecListener {

    private final String TAG = dashboardFragment.class.getSimpleName();

    private final int RESULT_LOAD_IMAGE = 48;
    private final int REQUEST_IMAGE_CAPTURE = 38;


    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private FirebaseFirestore firebaseFirestore;
    private StorageReference firebaseStorage;

    View view;

    RecyclerView recImages;
    Spinner spKategori;
    EditText editBaslik;
    EditText editFiyat;
    EditText editAciklama;
    Button btnEkle;

    NewAdvertRecAdapter recAdapter;
    int positionRec;


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

        view = inflater.inflate(R.layout.fragment_dashboard, container, false);

        firebaseFirestore=FirebaseFirestore.getInstance();
        firebaseAuth=FirebaseAuth.getInstance();
        firebaseUser=firebaseAuth.getCurrentUser();
        firebaseStorage = FirebaseStorage.getInstance().getReference();

        initRecyclerView(view);
        loadWidgets(view);

        btnEkle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                urunKayit();
            }
        });

        return view;
    }

    private void loadWidgets(View view) {
        spKategori = view.findViewById(R.id.spinKategori);
        editBaslik = view.findViewById(R.id.etBaslik);
        editFiyat = view.findViewById(R.id.etFiyat);
        editAciklama = view.findViewById(R.id.etAciklama);
        btnEkle = view.findViewById(R.id.btnUrunEkle);

        ArrayAdapter adapter = ArrayAdapter.createFromResource(getContext(), R.array.kategori, android.R.layout.simple_spinner_dropdown_item);
        spKategori.setAdapter(adapter);
    }

    private void initRecyclerView(View view){
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), RecyclerView.HORIZONTAL, false);
        recImages = view.findViewById(R.id.recyclerImages);
        recImages.setLayoutManager(layoutManager);
        recAdapter = new NewAdvertRecAdapter(getContext(), this);
        recImages.setAdapter(recAdapter);
    }

    private void urunKayit() {
        String baslik = editBaslik.getText().toString();
        String aciklama = editAciklama.getText().toString();
        String fiyat = editFiyat.getText().toString();
        String kategori = spKategori.getSelectedItem().toString();
        String mail=firebaseUser.getEmail();

        firebaseFirestore.collection("advertisement").add(new Advertisement(baslik, aciklama, kategori, fiyat,mail))
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "Document ID: " + documentReference);
                        fotoYukle(documentReference);
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }

    private void fotoYukle(final DocumentReference documentReference) {
        HashMap<String, byte[]> images = recAdapter.getImages();

        for(Map.Entry<String, byte[]> entry : images.entrySet()) {
            String key = entry.getKey();
            byte[] value = entry.getValue();
            Log.d(TAG, "uri: " + key);
            Log.d(TAG, "resim: " + Arrays.toString(value));

            final String randName = RandomName.randImageName();
            final StorageReference refStorage = firebaseStorage
                    .child(firebaseUser.getEmail())
                    .child("advertisements")
                    .child(documentReference.getId())
                    .child(randName + ".jpg");
            UploadTask uploadTask = refStorage.putBytes(value);
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
                        kaydetFoto(documentReference, randName, downloadUri);
                    }
                    else {
                        Log.d(TAG, "Resim upload edilemedi.");
                    }
                }
            });
        }
    }

    public void kaydetFoto(DocumentReference documentReference, final String name, final Uri uri){

        CollectionReference refSubCollection = documentReference.collection("pictures");
        Log.d(TAG, "kaydetFoto: docID:" + documentReference.getId());
        refSubCollection.document(name).set(new Picture(name + ".jpg", uri.toString())).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "resim yüklendi: Uri: " + uri.toString());
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "resim yüklemede hata: " + uri);
            }
        });
    }

    @Override
    public void selectImage(int position) {
        Log.d(TAG, "selectImage: position: " + position);
        positionRec = position;
        final CharSequence[] items = {"Fotoğraf Çek", "Galeriden Seç",
                "İptal"};
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Fotoğraf Ekle");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("Fotoğraf Çek")) {
                    activeTakePhoto();
                } else if (items[item].equals("Galeriden Seç")) {
                    activeGallery(true);
                } else if (items[item].equals("İptal")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    @Override
    public void doingSomeOnImage(final int position) {
        positionRec = position;
        final CharSequence[] items = {"Fotoğrafı Sil",
                "İptal"};
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Fotoğraf Ekle");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("Fotoğrafı Sil")) {

                    deleteImage(position);

                } else if (items[item].equals("İptal")) {

                    dialog.dismiss();

                }
            }
        });
        builder.show();
    }



    private void activeGallery(boolean newOrChange) {
        Intent intent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, RESULT_LOAD_IMAGE);
    }

    private void activeTakePhoto() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
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
                        getActivity().getApplicationContext().getPackageName() + ".fileprovider",
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case RESULT_LOAD_IMAGE:
                if (requestCode == RESULT_LOAD_IMAGE && resultCode == Activity.RESULT_OK & null != data) {

                    try {
                        addImage(data.getData(), positionRec);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }

                break;

            case REQUEST_IMAGE_CAPTURE:
                if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {

                    Uri mData = FileProvider.getUriForFile(getContext(), getActivity().getApplicationContext().getPackageName() + ".fileprovider", new File(currentPhotoPath));
                    System.out.println("data: " + mData);

                    try {
                        addImage(mData, positionRec);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
        }
    }

    private void deleteImage(int position) {
        recAdapter.deleteImage(position);
        recAdapter.notifyDataSetChanged();
    }

    private void addImage(Uri data, int positionRec) throws IOException {
        recAdapter.addImage(data, positionRec);
        recAdapter.notifyDataSetChanged();
        recImages.smoothScrollToPosition(positionRec + 1);
    }
}
