package com.eneserdogan.unistore;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Toast;

import com.eneserdogan.unistore.Utils.RandomName;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

public class NewUser extends AppCompatActivity {
    EditText etAdSoyad;
    AutoCompleteTextView autoUniversity;


    CircleImageView imgProfilePicture;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    StorageReference storageReference;

    String urlProfilePicture = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_user);

        etAdSoyad=findViewById(R.id.etName);
        autoUniversity = findViewById(R.id.autoUniversity);
        imgProfilePicture = findViewById(R.id.imgPP);

        firebaseAuth=FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        storageReference = FirebaseStorage.getInstance().getReference();

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this,
                android.R.layout.select_dialog_item, getResources().getStringArray(R.array.universite));
        autoUniversity.setThreshold(1);
        autoUniversity.setAdapter(arrayAdapter);

        if (firebaseUser == null){
            Toast.makeText(this, "Bir meydana geldi. Giriş sayfasına yönlendiriliyorsunuz.", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    public void loadProfilePicture(View view){
        openGallery();
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, 5);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
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
                        Toast.makeText(NewUser.this, "Resim yükleme başarılı!", Toast.LENGTH_SHORT).show();
                        Picasso.get().load(urlProfilePicture).resize(500,500).into(imgProfilePicture);
                    }

                    else {
                        Toast.makeText(NewUser.this, "Resim yüklenemedi!", Toast.LENGTH_SHORT).show();
                        imgProfilePicture.setImageResource(R.drawable.profile);
                    }
                }
            });
        }

    }

    public void btnKaydet(View view){
        if(autoUniversity.getText().toString().trim().length() == 0 || etAdSoyad.getText().toString().trim().length() ==0){
            Toast.makeText(getApplicationContext(),"LÜtfen Gerekli Alanları Doldurunuz",Toast.LENGTH_LONG).show();
        }else {
            String adSoyad=etAdSoyad.getText().toString().trim();
            String universite = autoUniversity.getText().toString();
            uploadData(adSoyad, universite);
        }
    }
    public void uploadData(String adSoyad,String universite){
        String id= UUID.randomUUID().toString();//random ıd
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Kullanıcı oluşturuluyor
        Map<String, Object> user = new HashMap<>();
        user.put("id",id);
        user.put("adSoyad",adSoyad);
        user.put("email",firebaseUser.getEmail());
        user.put("universite", universite);
        user.put("resim", urlProfilePicture);

        // Yeni belge ekleniyor
        db.collection("users")
                .document(id).set(user);

        Toast.makeText(NewUser.this, "Kayıt Başarılı",
                Toast.LENGTH_LONG).show();
        Intent intent2=new Intent(NewUser.this,HomeActivity.class);
        startActivity(intent2);
        finish();
    }
}
