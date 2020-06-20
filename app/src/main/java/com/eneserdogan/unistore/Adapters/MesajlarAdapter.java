package com.eneserdogan.unistore.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.telecom.Call;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.eneserdogan.unistore.ChatActivity;
import com.eneserdogan.unistore.MesajlarActivity;
import com.eneserdogan.unistore.Models.Advertisement;
import com.eneserdogan.unistore.Models.OtherId;
import com.eneserdogan.unistore.Models.User;
import com.eneserdogan.unistore.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class MesajlarAdapter extends BaseAdapter {
    List<String> otherIdList;
    String userId;
    Context context;
    Activity activity;
    FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();


    public MesajlarAdapter(List<String> otherIdList, String userId, Context context,Activity activity) {
        this.otherIdList = otherIdList;
        this.userId = userId;
        this.context = context;
        this.activity=activity;
    }


    @Override
    public int getCount() {
        return otherIdList.size();
    }

    @Override
    public Object getItem(int position) {
        return otherIdList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        convertView= LayoutInflater.from(context).inflate(R.layout.other,parent,false);
        TextView textView;
        textView=(TextView) convertView.findViewById(R.id.otherText);
        istekAt(otherIdList.get(position),textView);
        LinearLayout linearLayout=convertView.findViewById(R.id.linearMesaj);
        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(activity, ChatActivity.class);
                OtherId.setOtherId(otherIdList.get(position).toString());
                activity.startActivity(intent);
            }
        });


        System.out.println("otherId Adapter ="+otherIdList.get(position).toString());

        return convertView;
    }
    public void istekAt(String uye_id,final TextView textView){
        //textView.setText(firebaseUser.getEmail());
        DocumentReference docAdver = firebaseFirestore.collection("users").document(uye_id);
        docAdver.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Log.d(TAG, "onSuccess: Kayıt bilgileri getirildi.");
                System.out.println("adapter maaill ="+documentSnapshot.getString("email"));
                textView.setText(documentSnapshot.getString("email"));
                //spKategori.setSelection(pos);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "onFailure: Kayıt bilgilerini getirmede hata!");
            }
        });

    }
}
