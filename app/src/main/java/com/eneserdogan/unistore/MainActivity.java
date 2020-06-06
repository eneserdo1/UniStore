package com.eneserdogan.unistore;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    String emailPattern = "[A-Za-z0-9+_.-]+@[a-zA-Z0-9.-]+\\.+edu+\\.+tr+";
    EditText email;
    EditText password;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;
    ProgressBar progressBar;

    boolean durum = true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        email=findViewById(R.id.etEmail);
        password=findViewById(R.id.etPassword);
        progressBar=findViewById(R.id.progressBar);

        progressBar.setVisibility(View.INVISIBLE);

        firebaseAuth=FirebaseAuth.getInstance();
        firebaseFirestore=FirebaseFirestore.getInstance();
    }
    public void addUser(View view){
        Intent ıntent=new Intent(MainActivity.this,verifyMailActivity.class);
        startActivity(ıntent);
    }

    public void login(View view){
        final String str_email = email.getText().toString();
        final String str_password = password.getText().toString();
        progressBar.setVisibility(View.VISIBLE);

        if(TextUtils.isEmpty(str_email)){
            Toast.makeText(this, "Lütfen mailinizi giriniz!",Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(str_password)){
            Toast.makeText(this, "Lütfen şifrenizi giriniz!",Toast.LENGTH_SHORT).show();
            return;
        }

        if (email.getText().toString().trim().matches(emailPattern))
        {
            CollectionReference collectionReference=firebaseFirestore.collection("users");
            collectionReference.whereEqualTo("email",email.getText().toString()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()){
                        QuerySnapshot document = task.getResult();
                        if(document != null){
                            System.out.println("döküment sayısı1: " + document.size());
                            System.out.println("döküment sayısı2: " + document.getDocuments().size());
                            List custom = document.getDocuments();
                            if (custom.size() == 0){
                                durum = false;
                                System.out.println("getdata girdi");
                            }

                            login(durum);
                        }
                    }
                }
            });
        }
        else{
            Toast.makeText(getApplicationContext()," Lütfen @[Üniversite Adı].edu.tr Uzantılı Email Girin ", Toast.LENGTH_SHORT).show();
        }
    }

    public void login(final Boolean durum){

        firebaseAuth.signInWithEmailAndPassword(email.getText().toString(),password.getText().toString())
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressBar.setVisibility(View.GONE);
                        if(task.isSuccessful()){
                            if(firebaseAuth.getCurrentUser().isEmailVerified()){
                                if (durum){
                                    startActivity(new Intent(MainActivity.this,HomeActivity.class));
                                    System.out.println("false girdi");
                                    finish();
                                }else {
                                    startActivity(new Intent(MainActivity.this, NewUser.class));
                                    System.out.println("else girdi");
                                    finish();
                                }
                            }else{
                                Toast.makeText(MainActivity.this, "Lütfen mailinizi doğrulayınız."
                                        , Toast.LENGTH_LONG).show();
                            }
                        }else{
                            Toast.makeText(MainActivity.this, task.getException().getMessage()
                                    , Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    public void resetPassword(View view) {
        Intent intent = new Intent(getApplicationContext(), ForgotPasswordActivity.class);
        startActivity(intent);
    }
}
