package com.halaqat.attendance.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.halaqat.attendance.R;
import com.halaqat.attendance.models.Halaqa;
import java.util.List;

public class HalaqatAdapter extends RecyclerView.Adapter<HalaqatAdapter.ViewHolder> {
    
    private static final String TAG = "HalaqatAdapter";
    
    private Context context;
    private List<Halaqa> halaqatList;
    private OnHalaqaActionListener listener;
    
    public interface OnHalaqaActionListener {
        void onEditHalaqa(Halaqa halaqa);
        void onDeleteHalaqa(Halaqa halaqa);
    }
    
    public HalaqatAdapter(Context context, List<Halaqa> halaqatList, OnHalaqaActionListener listener) {
        this.context = context;
        this.halaqatList = halaqatList;
        this.listener = listener;
    }
    
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_halaqa, parent, false);
        return new ViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        try {
            Halaqa halaqa = halaqatList.get(position);
            
            if (halaqa != null) {
                holder.tvName.setText(halaqa.getName() != null ? halaqa.getName() : "بدون اسم");
                holder.tvDescription.setText(halaqa.getDescription() != null ? halaqa.getDescription() : "");
                
                holder.btnEdit.setOnClickListener(v -> {
                    if (listener != null) {
                        listener.onEditHalaqa(halaqa);
                    }
                });
                
                holder.btnDelete.setOnClickListener(v -> {
                    if (listener != null) {
                        listener.onDeleteHalaqa(halaqa);
                    }
                });
            }
        } catch (Exception e) {
            Log.e(TAG, "Error binding halaqa at position " + position, e);
        }
    }
    
    @Override
    public int getItemCount() {
        return halaqatList != null ? halaqatList.size() : 0;
    }
    
    public void updateData(List<Halaqa> newList) {
        if (newList != null) {
            this.halaqatList = newList;
            notifyDataSetChanged();
            Log.d(TAG, "Data updated with " + newList.size() + " items");
        } else {
            Log.w(TAG, "Attempted to update with null list");
        }
    }
    
    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvDescription;
        ImageButton btnEdit, btnDelete;
        
        ViewHolder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_name);
            tvDescription = itemView.findViewById(R.id.tv_description);
            btnEdit = itemView.findViewById(R.id.btn_edit);
            btnDelete = itemView.findViewById(R.id.btn_delete);
        }
    }
}
