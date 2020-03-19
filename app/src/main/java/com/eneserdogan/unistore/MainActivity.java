package com.eneserdogan.unistore;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
    public void addUser(View view){
        Intent ıntent=new Intent(MainActivity.this,NewUser.class);
        startActivity(ıntent);
    }

    public void login(View view){

    }
}
