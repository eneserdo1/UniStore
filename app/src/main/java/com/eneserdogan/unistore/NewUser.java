package com.eneserdogan.unistore;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.eneserdogan.unistore.Models.Picture;
import com.eneserdogan.unistore.Models.User;
import com.eneserdogan.unistore.Utils.RandomName;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import de.hdodenhof.circleimageview.CircleImageView;

public class NewUser extends AppCompatActivity {
    private final static String TAG = "NewUser";

    private final int RESULT_LOAD_IMAGE = 48;
    private final int REQUEST_IMAGE_CAPTURE = 38;

    EditText etAdSoyad;
    AutoCompleteTextView autoUniversity;
    CircleImageView imgProfilePicture;

    FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    StorageReference storageReference = FirebaseStorage.getInstance().getReference();

    byte[] compressed = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_user);

        loadWidgets();

        if (firebaseUser == null){
            Toast.makeText(this, "Bir hata meydana geldi. Giriş sayfasına yönlendiriliyorsunuz.", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    private void loadWidgets(){
        etAdSoyad=findViewById(R.id.etName);
        autoUniversity = findViewById(R.id.autoUniversity);
        imgProfilePicture = findViewById(R.id.imgPP);

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this,
                android.R.layout.select_dialog_item, getResources().getStringArray(R.array.universite));
        autoUniversity.setThreshold(1);
        autoUniversity.setAdapter(arrayAdapter);
    }

    public void loadProfilePicture(View view){
        openGallery();
    }

    private void openGallery() {
        if (compressed == null){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
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
        else {
            final CharSequence[] items = {"Fotoğrafı Sil",
                    "İptal"};
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Fotoğrafı silmek istiyor musunuz?");
            builder.setItems(items, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int item) {
                    if (items[item].equals("Fotoğrafı Sil")) {

                        clearImage();

                    } else if (items[item].equals("İptal")) {

                        dialog.dismiss();

                    }
                }
            });
            builder.show();
        }
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
        if (takePictureIntent.resolveActivity(this.getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        this.getApplicationContext().getPackageName() + ".fileprovider",
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
        File storageDir = this.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
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
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

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

                    Uri mData = FileProvider.getUriForFile(this, this.getApplicationContext().getPackageName() + ".fileprovider", new File(currentPhotoPath));

                    Glide.with(this).load(mData).into(imgProfilePicture);
                    keepImage(mData);

                }
        }
    }

    private void keepImage(Uri data) {
        compressed = compress(data);
    }

    private void clearImage(){
        imgProfilePicture.setImageResource(R.drawable.profile);
        compressed = null;
    }

    private byte[] compress(Uri uri) {
        Bitmap bitmap = null;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
        } catch (IOException e) {
            e.printStackTrace();
        }
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        Log.d(TAG, "before compress: " + bitmap.getByteCount()/1024 + "KB");
        bitmap.compress(Bitmap.CompressFormat.JPEG, 25, stream);
        Log.d(TAG, "after compress: " + stream.toByteArray().length/1024 + "KB");
        return stream.toByteArray();
    }

    public void btnKaydet(View view){
        if(autoUniversity.getText().toString().trim().length() == 0 || etAdSoyad.getText().toString().trim().length() ==0){
            Toast.makeText(getApplicationContext(),"LÜtfen Gerekli Alanları Doldurunuz",Toast.LENGTH_LONG).show();
        }else {
            String university = autoUniversity.getText().toString();

            if (controlUniversity(university)){
                uploadPhoto();
                //uploadData(name, university);
            } else {
                Toast.makeText(this, "Lütfen geçerli bir üniversite adı giriniz.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void uploadPhoto() {
        if (compressed != null){
            final String randName = RandomName.randImageName() + ".jpg";
            final StorageReference refStorage = storageReference
                    .child(firebaseUser.getEmail())
                    .child("profilePicture")
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
                        saveProfile(randName, downloadUri.toString());
                    }
                    else {
                        Log.d(TAG, "Resim upload edilemedi.");
                    }
                }
            });
        }
        else{
            saveProfile("", "");
        }

    }

    private void saveProfile(String imgname, String uri) {
        String adSoyad = etAdSoyad.getText().toString();
        String universite = autoUniversity.getText().toString();

        db.collection("users").
                document(firebaseUser.getUid())
                .set(new User(firebaseUser.getUid(), firebaseUser.getEmail(), adSoyad, universite, new Picture(imgname, uri.toString())))
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(NewUser.this, "Kayıt Başarılı",
                                Toast.LENGTH_LONG).show();

                        Intent intent2=new Intent(NewUser.this,HomeActivity.class);
                        startActivity(intent2);
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(NewUser.this, "Kayıt Eklemede bir hata oluştu.",
                        Toast.LENGTH_LONG).show();

                Log.e(TAG, String.valueOf(e));
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
