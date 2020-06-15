package com.eneserdogan.unistore.Adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.eneserdogan.unistore.Models.Advertisement;
import com.eneserdogan.unistore.ProductActivity;
import com.eneserdogan.unistore.R;
import com.squareup.picasso.Picasso;

import java.io.Serializable;
import java.util.ArrayList;

public class GetAdvertisementAdapter extends RecyclerView.Adapter<GetAdvertisementAdapter.MyViewHolder> {
    Context context;
    ArrayList<Advertisement> products;
    ArrayList<String> photoUrls;

    public GetAdvertisementAdapter(Context context){
        this.context = context;
        this.products = new ArrayList<>();
        this.photoUrls = new ArrayList<>();
    }

    public void addElements(Advertisement product, String photoUrl){
        products.add(product);
        photoUrls.add(photoUrl);
    }

    @NonNull
    @Override
    public GetAdvertisementAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.row_layout,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull GetAdvertisementAdapter.MyViewHolder holder, final int position) {

        holder.başlık.setText(products.get(position).getTitle());
        holder.fiyat.setText(products.get(position).getPrice() + " TL");
        Picasso.get().load(String.valueOf(photoUrls.get(position))).into(holder.productPic);

        holder.layProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context.getApplicationContext(), ProductActivity.class);
                intent.putExtra("DocID", products.get(position));
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView başlık,fiyat;
        ImageView productPic;
        LinearLayout layProduct;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            başlık=itemView.findViewById(R.id.ilan_baslik);
            fiyat=itemView.findViewById(R.id.ilan_fiyat);
            productPic=itemView.findViewById(R.id.ilan_ımage);
            layProduct = itemView.findViewById(R.id.layoutProduct);
        }
    }
}
