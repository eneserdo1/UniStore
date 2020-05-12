package com.eneserdogan.unistore.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.eneserdogan.unistore.Models.Advertisement;
import com.eneserdogan.unistore.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class GetAdvertisementAdapter extends RecyclerView.Adapter<GetAdvertisementAdapter.MyViewHolder> {
    Context context;
    ArrayList<Advertisement> products;
    ArrayList<String> resimler;

    public GetAdvertisementAdapter(Context c,ArrayList<Advertisement> p){
        context=c;
        products=p;
    }

    @NonNull
    @Override
    public GetAdvertisementAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.row_layout,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull GetAdvertisementAdapter.MyViewHolder holder, int position) {


        System.out.println("döngü"+products.get(position).getUrlname());
        holder.başlık.setText(products.get(position).getTitle());
        holder.fiyat.setText(products.get(position).getPrice());
        Picasso.get().load(String.valueOf(products.get(position).getUrlname())).into(holder.productPic);

    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView başlık,fiyat;
        ImageView productPic;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            başlık=itemView.findViewById(R.id.ilan_baslik);
            fiyat=itemView.findViewById(R.id.ilan_fiyat);
            productPic=itemView.findViewById(R.id.ilan_ımage);
        }
    }
}
