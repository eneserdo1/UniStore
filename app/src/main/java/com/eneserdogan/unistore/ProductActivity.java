package com.eneserdogan.unistore;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import com.eneserdogan.unistore.Adapters.ImageSliderAdapter;
import com.eneserdogan.unistore.Models.Advertisement;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class ProductActivity extends AppCompatActivity {

    private static final String TAG = "ProductActivity";

    FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    FirebaseFirestore firestore = FirebaseFirestore.getInstance();

    ViewPager imgSlider;
    Spinner spKategori;
    EditText editBaslik;
    EditText editFiyat;
    EditText editAciklama;
    Button btnConversation;

    Advertisement advertisement;
    String productOwnerEmail;

    ImageSliderAdapter imgAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);
        advertisement = (Advertisement) getIntent().getSerializableExtra("DocID");

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Ürün Detayı");

        // add back arrow to toolbar
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        loadWidgets();
        loadAdversitementInfos();
    }

    void loadWidgets(){
        imgSlider = findViewById(R.id.imgSlider);
        spKategori = findViewById(R.id.spinKategori);
        spKategori.setEnabled(false);
        editBaslik = findViewById(R.id.etBaslik);
        editBaslik.setEnabled(false);
        editFiyat = findViewById(R.id.etFiyat);
        editFiyat.setEnabled(false);
        editAciklama = findViewById(R.id.etAciklama);
        editAciklama.setEnabled(false);
        btnConversation = findViewById(R.id.btnStartConversation);

        ArrayAdapter adapter = ArrayAdapter.createFromResource(this, R.array.kategori, android.R.layout.simple_spinner_dropdown_item);
        spKategori.setAdapter(adapter);

        imgAdapter = new ImageSliderAdapter(this);
        imgSlider.setAdapter(imgAdapter);
        loadPhotosToSlider(advertisement.getId());
    }

    private void loadPhotosToSlider(String id) {
        CollectionReference pictures = firestore.collection("advertisement").document(id).collection("pictures");

        final ArrayList<String> imgUrls = new ArrayList<>();

        pictures.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    for (QueryDocumentSnapshot docs : task.getResult()){
                        Log.d(TAG, "onComplete: resim adı: " + docs.get("namePicture"));
                        Log.d(TAG, "onComplete: resim URL: " + docs.get("urlPicture"));
                        String url = (String) docs.get("urlPicture");
                        imgAdapter.addURL(url);
                    }
                }
                else {
                    Log.d(TAG, "onComplete: Resimleri çekmede hata!");
                }
            }
        });
    }

    private void loadAdversitementInfos() {
        DocumentReference docAdver = firestore.collection("advertisement").document(advertisement.getId());
        docAdver.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Log.d(TAG, "onSuccess: Kayıt bilgileri getirildi.");
                editBaslik.setText(documentSnapshot.getString("title"));
                editAciklama.setText(documentSnapshot.getString("description"));
                editFiyat.setText(documentSnapshot.getString("price"));
                int pos = getPosition(documentSnapshot.getString("category"));
                productOwnerEmail = documentSnapshot.getString("mail");
                spKategori.setSelection(pos);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "onFailure: Kayıt bilgilerini getirmede hata!");
            }
        });
    }

    private int getPosition(String category) {
        String[] categories = getResources().getStringArray(R.array.kategori);
        int pos = 0;
        for (int i = 0; i < categories.length; i++){
            if (categories[i].equals(category)){
                pos = i;
                break;
            }
        }

        return pos;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }
        return super.onOptionsItemSelected(item);
    }


}
