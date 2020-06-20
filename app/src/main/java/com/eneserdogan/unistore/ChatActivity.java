package com.eneserdogan.unistore;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.eneserdogan.unistore.Adapters.MesajAdapter;
import com.eneserdogan.unistore.Models.MesajModel;
import com.eneserdogan.unistore.Models.OtherId;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {

    DatabaseReference reference;
    FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    String userId,otherId,key,userTable,otherTable;
    EditText mesajEditText;
    Button sendMesajButon;
    List<MesajModel> list;
    MesajAdapter adapter;
    RecyclerView mesajListiew;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        tanimla();
        action();
        load();

    }
    public void tanimla(){
        mesajListiew=(RecyclerView)findViewById(R.id.mesajListiew);
        mesajEditText=findViewById(R.id.mesajEditText);
        sendMesajButon=findViewById(R.id.sendMesajButon);
        reference= FirebaseDatabase.getInstance().getReference();
        otherId= OtherId.getOtherId();
        userId=firebaseUser.getUid();

        list=new ArrayList<>();
        adapter=new MesajAdapter(list,getApplicationContext(),userId);
        RecyclerView.LayoutManager manager=new LinearLayoutManager(getApplicationContext());
        mesajListiew.setLayoutManager(manager);
        mesajListiew.setAdapter(adapter);


        System.out.println("emaill"+userId);

    }

    public void action(){
        sendMesajButon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage(mesajEditText.getText().toString(),userId,otherId);
            }
        });
    }
    public void sendMessage(String mesajBody,String UsrId,String othId){

        userTable="messages/"+userId+"/"+otherId;
        otherTable="messages/"+otherId+"/"+userId;
        key=reference.child("messages").child(userTable).child(otherTable).push().getKey();

        Map map=send(mesajBody,UsrId,othId);
        Map map2=new HashMap();
        map2.put(userTable+"/"+key,map);
        map2.put(otherTable+"/"+key,map);

        mesajEditText.setText("");
        reference.updateChildren(map2, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {

            }
        });

    }
    public Map send(String mesajbody,String userId,String otherId){

        Map msj=new HashMap();
        msj.put("mesaj",mesajbody);
        msj.put("from",userId);
        msj.put("to",otherId);

        return msj;

    }
    public void load(){

        reference.child("messages").child(userId).child(otherId).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                MesajModel m=dataSnapshot.getValue(MesajModel.class);
                list.add(m);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                MesajModel m=dataSnapshot.getValue(MesajModel.class);
                list.add(m);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                MesajModel m=dataSnapshot.getValue(MesajModel.class);
                list.add(m);
                adapter.notifyDataSetChanged();
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
