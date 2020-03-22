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
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    String emailPattern = "[A-Za-z0-9+_.-]+@[a-zA-Z0-9.-]+\\.+edu+\\.+tr+";
    EditText Email;
    EditText Password;
    private FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;
    String mailKontrol;
    ArrayList<String> kontrol;
    String sayac="0";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Email=findViewById(R.id.etEmail);
        Password=findViewById(R.id.etPassword);

        firebaseAuth=FirebaseAuth.getInstance();
        kontrol=new ArrayList<>();

    }
    public void addUser(View view){
        Intent ıntent=new Intent(MainActivity.this,verifyMailActivity.class);
        startActivity(ıntent);
    }

    public void login(View view){
        firebaseAuth.signInWithEmailAndPassword(Email.getText().toString(),
                Password.getText().toString())
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            if(firebaseAuth.getCurrentUser().isEmailVerified()){
                                if (Email.getText().toString().trim().matches(emailPattern)) {
                                    startActivity(new Intent(MainActivity.this, NewUser.class));
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
    /*public void getdata(){
        firebaseFirestore.collection("users")
                .whereEqualTo("email",Email.getText().toString())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @SuppressLint("LongLogTag")
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                if (document != null) {
                                    mailKontrol=document.getString("email");
                                    kontrol.add(mailKontrol);
                                }else {
                                    System.out.println("Gelen veri yok");
                                }


                            }
                        } else {
                            Log.w("Error getting documents.", task.getException());
                        }
                    }
                });

    }*/
}
