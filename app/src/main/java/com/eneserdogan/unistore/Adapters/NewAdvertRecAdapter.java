package com.eneserdogan.unistore.Adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.eneserdogan.unistore.R;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class NewAdvertRecAdapter extends RecyclerView.Adapter<NewAdvertRecAdapter.ViewHolder> {
    private final static String TAG = NewAdvertRecAdapter.class.getSimpleName();

    public interface NewAdvertRecListener{
        void selectImage(int position);
        void doingSomeOnImage(int position);
    }

    NewAdvertRecListener mListener;

    Context mContext;
    private ArrayList<String> listImages = new ArrayList<>(Arrays.asList("ek","","","","","","","","",""));
    private HashMap<String, byte[]> compressedImages = new HashMap<String, byte[]>();

    public NewAdvertRecAdapter(Context mContext, NewAdvertRecListener listener) {
        this.mContext = mContext;
        mListener = listener;
    }

    public HashMap<String, byte[]> getImages(){
        return compressedImages;
    }

    private byte[] compress(Uri uri) {
        Bitmap bitmap = null;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(mContext.getContentResolver(), uri);
        } catch (IOException e) {
            e.printStackTrace();
        }
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        Log.d(TAG, "before compress: " + bitmap.getByteCount()/1024 + "KB");
        bitmap.compress(Bitmap.CompressFormat.JPEG, 25, stream);
        Log.d(TAG, "after compress: " + stream.toByteArray().length/1024 + "KB");
        return stream.toByteArray();
    }

    public void addImage(Uri imageData, int pos) throws IOException {
        listImages.set(pos, imageData.toString());
        compressedImages.put(imageData.toString(), compress(imageData));
        if (pos < listImages.size() - 1){
            listImages.set(pos + 1, "ek");
        }
    }



    public void deleteImage(int pos){
        compressedImages.remove(listImages.get(pos));
        if (!listImages.contains("ek")){
            listImages.remove(pos);
            listImages.add("ek");
        } else{
            listImages.remove(pos);
            listImages.add("");
        }
    }

    public void clearList(){
        listImages = new ArrayList<>(Arrays.asList("ek","","","","","","","","",""));
        compressedImages.clear();
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        ImageView imageView;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
        }
    }

    @Override
    public int getItemCount() {
        return listImages.size();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_images_card, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {

        if (!(listImages.get(position).equals("") || listImages.get(position).equals("ek"))){
            Glide.with(mContext).load(listImages.get(position)).into(holder.imageView);

            holder.imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "position in rec:" + position);
                    mListener.doingSomeOnImage(position);
                }
            });
        }
        else if (listImages.get(position).equals("ek")){
            holder.imageView.setImageResource(R.drawable.addphoto);

            holder.imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "position in rec:" + position);
                    mListener.selectImage(position);
                }
            });
        }
        else {
            holder.imageView.setImageDrawable(null);
            holder.imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "position in rec:" + position);
                }
            });
        }
    }

}
