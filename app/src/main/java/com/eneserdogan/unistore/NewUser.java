package com.eneserdogan.unistore;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelStoreOwner;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class NewUser extends AppCompatActivity {
    EditText etMail;
    EditText etAdSoyad;
    EditText etPassword;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_user);

        etMail=findViewById(R.id.etEmail);
        etAdSoyad=findViewById(R.id.etName);
        etPassword=findViewById(R.id.etPassword);

        firebaseAuth=FirebaseAuth.getInstance();
    }

    public void kayıtOl(View view){
        if(etMail.getText().toString().trim().length() == 0 || etAdSoyad.getText().toString().trim().length() ==0){
            Toast.makeText(getApplicationContext(),"LÜtfen Gerekli Alanları Doldurunuz",Toast.LENGTH_LONG).show();
        }else {
            String email=etMail.getText().toString().trim();
            String adSoyad=etAdSoyad.getText().toString().trim();
            String password=etPassword.getText().toString().trim();
            uploadData(email,adSoyad,password);

        }

    }
    public void uploadData(String email,String adSoyad,String password){
        String id= UUID.randomUUID().toString();//random ıd
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Kullanıcı oluşturuluyor
        Map<String, Object> user = new HashMap<>();
        user.put("id",id);
        user.put("adSoyad",adSoyad);
        user.put("email",email);
        user.put("şifre",password);


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
