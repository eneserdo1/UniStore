package com.eneserdogan.unistore;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    String emailPattern = "[A-Za-z0-9+_.-]+@[a-zA-Z0-9.-]+\\.+edu+\\.+tr+";
    EditText Email;
    EditText Password;
    private FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;
    FirebaseUser firebaseUser;
    String mailKontrol="";
    Boolean torf=false;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Email=findViewById(R.id.etEmail);
        Password=findViewById(R.id.etPassword);

        firebaseAuth=FirebaseAuth.getInstance();
        firebaseFirestore=FirebaseFirestore.getInstance();

    }
    public void addUser(View view){
        Intent ıntent=new Intent(MainActivity.this,verifyMailActivity.class);
        startActivity(ıntent);
    }

    public void login(View view){
        mailKontrol=Email.getText().toString();
        CollectionReference collectionReference=firebaseFirestore.collection("users");
        collectionReference.whereEqualTo("email",mailKontrol).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    QuerySnapshot document = task.getResult();
                    if(document != null){
                        List custom = document.getDocuments();
                        if (custom.size() == 0){
                            torf = true;
                            System.out.println("getdata girdi");
                        }
                    }
                }
            }
        });

        firebaseAuth.signInWithEmailAndPassword(Email.getText().toString(),
                Password.getText().toString())
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            if(firebaseAuth.getCurrentUser().isEmailVerified()){
                                if (Email.getText().toString().trim().matches(emailPattern)) {
                                    if (torf == false){
                                        startActivity(new Intent(MainActivity.this,HomeActivity.class));
                                        System.out.println("false girdi");

                                    }else {
                                        startActivity(new Intent(MainActivity.this, NewUser.class));
                                        System.out.println("else girdi");

                                    }
                                }
                            }else{
                                Toast.makeText(MainActivity.this, "Please verify your email address"
                                        , Toast.LENGTH_LONG).show();
                            }
                        }else{
                            Toast.makeText(MainActivity.this, task.getException().getMessage()
                                    , Toast.LENGTH_LONG).show();
                        }
                    }
                });


    }

}
