package com.eneserdogan.unistore;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.eneserdogan.unistore.Adapters.MesajlarAdapter;
import com.eneserdogan.unistore.Models.OtherId;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class MesajlarActivity extends AppCompatActivity {

    FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    DatabaseReference reference;
    List<String> otherIdList;
    String userId;
    MesajlarAdapter mesajlarAdapter;
    ListView mesajlarListview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mesajlar);

        otherIdList=new ArrayList<>();
        reference= FirebaseDatabase.getInstance().getReference();
        userId=firebaseUser.getUid();
        mesajlarAdapter=new MesajlarAdapter(otherIdList,userId,getApplicationContext(),MesajlarActivity.this);
        mesajlarListview=findViewById(R.id.mesajlarListview);
        mesajlarListview.setAdapter(mesajlarAdapter);
        listele();
    }
    public void listele(){

        reference.child("messages").child(userId).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                System.out.println("mesajlarrr"+dataSnapshot.getKey());
                otherIdList.add(dataSnapshot.getKey());
                System.out.println("keyyyy ="+dataSnapshot.getKey());
                mesajlarAdapter.notifyDataSetChanged();

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


}
