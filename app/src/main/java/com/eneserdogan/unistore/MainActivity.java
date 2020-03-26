package com.eneserdogan.unistore;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
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
    private FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;
    Boolean torf=false;

    ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        email=findViewById(R.id.etEmail);
        password=findViewById(R.id.etPassword);

        firebaseAuth=FirebaseAuth.getInstance();
        firebaseFirestore=FirebaseFirestore.getInstance();

        loadProgressDialog();
    }
    public void addUser(View view){
        Intent ıntent=new Intent(MainActivity.this,verifyMailActivity.class);
        startActivity(ıntent);
    }

    public void login(View view){
        final String str_email = email.getText().toString();
        final String str_password = password.getText().toString();

        if(TextUtils.isEmpty(str_email)){
            Toast.makeText(this, "Lütfen mailinizi giriniz!",Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(str_password)){
            Toast.makeText(this, "Lütfen şifrenizi giriniz!",Toast.LENGTH_SHORT).show();
            return;
        }

        showProgressDialog();

        Thread mThread = new Thread() {
            @Override
            public void run() {
                CollectionReference collectionReference=firebaseFirestore.collection("users");
                collectionReference.whereEqualTo("email",str_email).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
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

                firebaseAuth.signInWithEmailAndPassword(str_email,str_password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful()){
                                    if(firebaseAuth.getCurrentUser().isEmailVerified()){
                                        if (str_email.matches(emailPattern)) {
                                            if (torf == false){
                                                startActivity(new Intent(MainActivity.this,HomeActivity.class));
                                                System.out.println("false girdi");
                                                finish();

                                            }else {
                                                startActivity(new Intent(MainActivity.this, NewUser.class));
                                                System.out.println("else girdi");
                                                finish();

                                            }
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

                dismissProgressDialog();
            }
        };
        mThread.start();
    }

    public void resetPassword(View view) {
        Intent intent = new Intent(getApplicationContext(), ForgotPasswordActivity.class);
        startActivity(intent);
    }

    private void loadProgressDialog(){
        progressDialog = new ProgressDialog(this, R.style.CustomProgressDialogStyle);
        progressDialog.setMessage("Giriş yapılıyor...");
    }

    private void showProgressDialog(){
        progressDialog.show();
    }

    private void dismissProgressDialog(){
        progressDialog.dismiss();
    }
}
