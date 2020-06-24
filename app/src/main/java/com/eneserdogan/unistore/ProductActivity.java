package com.eneserdogan.unistore;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.eneserdogan.unistore.Adapters.ImageSliderAdapter;
import com.eneserdogan.unistore.Models.Advertisement;
import com.eneserdogan.unistore.Models.OtherId;
import com.eneserdogan.unistore.Models.Picture;
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
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ProductActivity extends AppCompatActivity {

    private static final String TAG = "ProductActivity";

    FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    FirebaseFirestore firestore = FirebaseFirestore.getInstance();

    ViewPager imgSlider;
    EditText tvKategori,editBaslik,editFiyat,editAciklama;
    Button btnConversation,btnDeleteProduct,btnGüncelle,btnKaydet;
    Advertisement advertisement;
    String productOwnerEmail,otherId;
    String usermail,othermail;
    ImageSliderAdapter imgAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);
        advertisement = (Advertisement) getIntent().getSerializableExtra("DocID");


        /*Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Ürün Detayı");

        // add back arrow to toolbar
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }*/
        usermail=firebaseUser.getEmail();
        othermail=advertisement.getMail();
        loadWidgets();


        loadAdversitementInfos();


    }

    @Override
    protected void onResume() {
        super.onResume();
        if (usermail.equals(othermail)) {
            System.out.println("if girdiii");
            btnGüncelle.setVisibility(View.VISIBLE);
            btnDeleteProduct.setVisibility(View.VISIBLE);
            btnConversation.setVisibility(View.GONE);
        }else{

            System.out.println("else girdiii");
            btnGüncelle.setVisibility(View.GONE);
            btnDeleteProduct.setVisibility(View.GONE);
            btnConversation.setVisibility(View.VISIBLE);
        }
    }

    void loadWidgets(){
        imgSlider = findViewById(R.id.imgSlider);
        tvKategori = findViewById(R.id.tvKategori);
        editBaslik = findViewById(R.id.etBaslik);
        editFiyat = findViewById(R.id.etFiyat);
        editAciklama = findViewById(R.id.etAciklama);
        btnConversation = findViewById(R.id.btnStartConversation);
        btnGüncelle=findViewById(R.id.btnGüncelle);
        btnDeleteProduct=findViewById(R.id.btnDeleteProduct);
        btnKaydet=findViewById(R.id.btnGüncellemeKayıt);
        btnKaydet.setVisibility(View.GONE);
        editBaslik.setEnabled(false);
        tvKategori.setEnabled(false);
        editFiyat.setEnabled(false);
        editAciklama.setEnabled(false);



        /*ArrayAdapter adapter = ArrayAdapter.createFromResource(this, R.array.kategori, android.R.layout.simple_spinner_dropdown_item);
        spKategori.setAdapter(adapter);*/

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
                editFiyat.setText(documentSnapshot.getString("price")+"₺");
                tvKategori.setText(documentSnapshot.getString("category"));
                productOwnerEmail = documentSnapshot.getString("mail");

                //spKategori.setSelection(pos);
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
    public void btnMesaj(View view){
        startActivity(new Intent(ProductActivity.this,ChatActivity.class));
        otherId=advertisement.getUuid();
        OtherId.setOtherId(otherId);

    }
    public void deleteProuct(View view){
        AlertDialog.Builder builder = new AlertDialog.Builder(ProductActivity.this);
        builder.setTitle("UniStore");
        builder.setMessage("Silmek istiyor musunuz ?");
        builder.setIcon(R.drawable.icon_delete);
        builder.setNegativeButton("Hayır", null);
        builder.setPositiveButton("Evet", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                firestore.collection("advertisement").document(advertisement.getId())
                        .delete()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d(TAG, "Başarıyla Silindi!");
                                Intent intent=new Intent(ProductActivity.this,HomeActivity.class);
                                startActivity(intent);
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w(TAG, "Silme Başarısız", e);
                            }
                        });
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();



    }
    public void güncelle(View view){
        if (usermail.equals(othermail)){
            btnKaydet.setVisibility(View.VISIBLE);
            btnGüncelle.setVisibility(View.GONE);
            editBaslik.setEnabled(true);
            editFiyat.setEnabled(true);
            tvKategori.setEnabled(true);
            editAciklama.setEnabled(true);
        }
    }
    public void kaydet(View view){
        String title = editBaslik.getText().toString();
        String kategori = tvKategori.getText().toString();
        String fiyat = editFiyat.getText().toString();
        String açıklama = editAciklama.getText().toString();

        Map<String, Object> updatedData = new HashMap<>();
        updatedData.put("category", kategori);
        updatedData.put("description", açıklama);
        updatedData.put("title",title);
        updatedData.put("price",fiyat);


        firestore.collection("advertisement").
                document(advertisement.getId()).update(updatedData).
                addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(ProductActivity.this, "Başarıyla güncellendi.", Toast.LENGTH_SHORT).show();
                        getData();
                        btnKaydet.setVisibility(View.GONE);
                        btnGüncelle.setVisibility(View.VISIBLE);
                        editBaslik.setEnabled(false);
                        tvKategori.setEnabled(false);
                        editFiyat.setEnabled(false);
                        editAciklama.setEnabled(false);
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ProductActivity.this, "Güncelleme başarısız.", Toast.LENGTH_SHORT).show();

                getData();
            }
        });

    }
    public void getData(){
        firestore.
                collection("advertisement").
                document(advertisement.getId()).
                get().
                addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()){
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()){

                                editAciklama.setText(document.getString("description"));
                                //UserMail.setText(document.getString("email"));
                                editBaslik.setText(document.getString("title"));
                                editFiyat.setText(document.getString("price"));
                                tvKategori.setText(document.getString("category"));

                            } else {
                                Log.d(TAG, "Veri bulunamadı.");
                            }
                        } else {
                            Log.d(TAG, "İşlem başarısız.");
                        }
                    }
                });

    }


}
