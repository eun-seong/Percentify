package com.eun.percentify;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Arrays;

public class RecyclerMainAdapter extends RecyclerView.Adapter<RecyclerMainAdapter.WidgetViewHolder> {
    private final static String TAG = "@@@@RecyclerAdapter";
    private static ArrayList<WidgetItem> listWidget;
    private static ArrayList<Integer> listResId = new ArrayList<>(Arrays.asList(
            R.drawable._widgettheme_0,
            R.drawable._widgettheme_1,
            R.drawable._widgettheme_2,
            R.drawable._widgettheme_3,
            R.drawable._widgettheme_4,
            R.drawable._widgettheme_5,
            R.drawable._widgettheme_6,
            R.drawable._widgettheme_7,
            R.drawable._widgettheme_8
    ));
    private Context mContext;

    private static OnListItemSelectedInterface mListener;

    public RecyclerMainAdapter(ArrayList<WidgetItem> listWidget
            ,Context context
            , OnListItemSelectedInterface listener) {
        this.listWidget = new ArrayList<>(listWidget);
        this.mContext = context;
        this.mListener = listener;
    }

    public static class WidgetViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;
        private TextView textView;

        public WidgetViewHolder(@NonNull View v) {
            super(v);
            imageView = v.findViewById(R.id.theme);
            textView = v.findViewById(R.id.title);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "appWidgetId = "+ listWidget.get(getAdapterPosition()).getId());
                    mListener.onItemSelected(v, listWidget.get(getAdapterPosition()).getId());
                }
            });
        }

        void onBind(int ResId){
            imageView.setImageResource(listResId.get(ResId));
        }
    }

    @NonNull
    @Override
    public RecyclerMainAdapter.WidgetViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_main, parent, false);

        WidgetViewHolder vh = new WidgetViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(WidgetViewHolder holder, int position) {
        holder.textView.setText(listWidget.get(position).getType());

        holder.onBind(listWidget.get(position).getTheme());
        holder.imageView.setClipToOutline(true);
    }

    @Override
    public int getItemCount() {
        return listWidget.size();
    }

    public interface OnListItemSelectedInterface {
        void onItemSelected(View v, int position);
    }

}
