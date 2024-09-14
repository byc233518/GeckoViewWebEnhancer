package com.gecko.webview.tv.pop;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gecko.webview.tv.OnInputConfirmListener;
import com.gecko.webview.tv.R;
import com.gecko.webview.tv.utils.SPUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.MyViewHolder> {

    private Context context;
    private List<String> dataList;
    private String currentAddress;
    private OnInputConfirmListener listener;

    public HistoryAdapter(Context context,List<String> dataList, String currentAddress, OnInputConfirmListener listener) {
        this.context = context;
        this.dataList = dataList;
        this.currentAddress = currentAddress;
        this.listener = listener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_history, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        String data = dataList.get(position);
        holder.tvAddress.setText(data);
        if (Objects.equals(data, currentAddress)) {
            holder.imgStatus.setVisibility(View.VISIBLE);
        } else {
            holder.imgStatus.setVisibility(View.INVISIBLE);
        }
        holder.layoutAddress.setTag(position);
        holder.layoutAddress.setOnClickListener(v -> {
            int _position = (int) v.getTag();
            listener.onConfirm(dataList.get(_position));
        });
        holder.imgDelete.setTag(position);
        holder.imgDelete.setOnClickListener(v -> {
            int _position = (int) v.getTag();
            SPUtil.removeHistoryString(context,dataList.get(_position));
            dataList.remove(_position);
            notifyDataSetChanged();
        });
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tvAddress;
        ImageView imgStatus;
        ImageButton imgDelete;
        LinearLayout layoutAddress;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tvAddress = itemView.findViewById(R.id.tv_address);
            imgStatus = itemView.findViewById(R.id.img_status);
            imgDelete = itemView.findViewById(R.id.img_delete);
            layoutAddress = itemView.findViewById(R.id.ll_address);
        }
    }

}
