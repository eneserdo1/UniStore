package com.eneserdogan.unistore;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.app.Activity;
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
import com.squareup.picasso.Picasso;
import de.hdodenhof.circleimageview.CircleImageView;

public class NewUser extends AppCompatActivity {
    private final static String TAG = "NewUser";

    EditText etAdSoyad;
    AutoCompleteTextView autoUniversity;
    CircleImageView imgProfilePicture;

    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    FirebaseFirestore db;
    StorageReference storageReference;

    String namePP = "";
    String urlPP = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_user);

        etAdSoyad=findViewById(R.id.etName);
        autoUniversity = findViewById(R.id.autoUniversity);
        imgProfilePicture = findViewById(R.id.imgPP);

        firebaseAuth=FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();
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
            if (data != null) {

                Uri filePath = data.getData();
                final String randName = RandomName.randImageName();

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
                            Log.d(TAG, "Image Name: " + randName + ".jpg");
                            Log.i(TAG, "URL: " + downloadUri);
                            Picasso.get().load(downloadUri).resize(500,500).into(imgProfilePicture);

                            namePP = randName + ".jpg";
                            urlPP = downloadUri.toString();

                            Toast.makeText(NewUser.this, "Resim yükleme başarılı!", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            Toast.makeText(NewUser.this, "Resim yüklenemedi!", Toast.LENGTH_SHORT).show();
                            imgProfilePicture.setImageResource(R.drawable.profile);
                        }
                    }
                });
            }
        }
    }

    public void btnKaydet(View view){
        if(autoUniversity.getText().toString().trim().length() == 0 || etAdSoyad.getText().toString().trim().length() ==0){
            Toast.makeText(getApplicationContext(),"LÜtfen Gerekli Alanları Doldurunuz",Toast.LENGTH_LONG).show();
        }else {
            String name = etAdSoyad.getText().toString();
            String university = autoUniversity.getText().toString();

            if (controlUniversity(university)){
                uploadData(name, university);
            } else {
                Toast.makeText(this, "Lütfen geçerli bir üniversite adı giriniz.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void uploadData(String adSoyad, String universite) {
        db.collection("users").
                document(firebaseUser.getUid()).
                set(new User(firebaseUser.getUid(), firebaseUser.getEmail(), adSoyad, universite, new Picture(namePP, urlPP))).
                addOnSuccessListener(new OnSuccessListener<Void>() {
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
