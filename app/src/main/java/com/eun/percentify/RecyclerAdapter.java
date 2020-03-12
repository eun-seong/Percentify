package com.eun.percentify;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ItemViewHolder> {
    private final static String TAG = "@@@@RecyclerAdapter";
    private ArrayList<Integer> listData;
    private Context mContext;

    private static OnListItemSelectedInterface mListener;

    public RecyclerAdapter(ArrayList<Integer> listData
            ,Context context
            , OnListItemSelectedInterface listener) {
        this.listData = new ArrayList<>(listData);
        this.mContext = context;
        this.mListener = listener;
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;

        public ItemViewHolder(@NonNull View v) {
            super(v);
            imageView = v.findViewById(R.id.theme);


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "position = "+ getAdapterPosition());
                    mListener.onItemSelected(v, getAdapterPosition());
                }
            });
        }

        void onBind(int ResId){
            imageView.setImageResource(ResId);
        }
    }

    @NonNull
    @Override
    public RecyclerAdapter.ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_theme, parent, false);

        ItemViewHolder vh = new ItemViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ItemViewHolder holder, int position) {
        holder.onBind(listData.get(position));
        holder.imageView.setClipToOutline(true);
    }

    @Override
    public int getItemCount() {
        return listData.size();
    }

    public interface OnListItemSelectedInterface {
        void onItemSelected(View v, int position);
    }

}
