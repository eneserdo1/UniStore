package com.eneserdogan.unistore;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class verifyMailActivity extends AppCompatActivity {

    EditText email;
    EditText password;
    String emailPattern = "[A-Za-z0-9+_.-]+@[a-zA-Z0-9.-]+\\.+edu+\\.+tr+";
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_mail);

        email=findViewById(R.id.etVerifyMail);
        password=findViewById(R.id.etVerifyPassword);
        firebaseAuth=FirebaseAuth.getInstance();


    }

    public void sendMail(View view){
        if(email.getText().toString().isEmpty()) {
            Toast.makeText(getApplicationContext(),"Üniversite Mail Adresi Girin",Toast.LENGTH_SHORT).show();
        }else {
            if (email.getText().toString().trim().matches(emailPattern)) {
                firebaseAuth.createUserWithEmailAndPassword(email.getText().toString(),
                        password.getText().toString())
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    firebaseAuth.getCurrentUser().sendEmailVerification()
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if(task.isSuccessful()){
                                                        Toast.makeText(getApplicationContext(), "\n" + "Lütfen doğrulama için e-postanızı kontrol edin ",
                                                                Toast.LENGTH_LONG).show();
                                                       // email.setText("");
                                                        //password.setText("");
                                                        Intent ıntent=new Intent(verifyMailActivity.this,NewUser.class);
                                                        startActivity(ıntent);

                                                    }else{
                                                        Toast.makeText(verifyMailActivity.this,  task.getException().getMessage(),
                                                                Toast.LENGTH_LONG).show();
                                                    }
                                                }
                                            });
                                }else {
                                    Toast.makeText(verifyMailActivity.this, task.getException().getMessage(),
                                            Toast.LENGTH_LONG).show();
                                }
                            }
                        });

            } else {
                Toast.makeText(getApplicationContext()," Lütfen @[Üniversite Adı].edu.tr Uzantılı Email Girin ", Toast.LENGTH_SHORT).show();
            }
        }

    }
}
