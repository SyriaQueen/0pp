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
import com.halaqat.attendance.models.Fawj;
import java.util.List;

public class FawjAdapter extends RecyclerView.Adapter<FawjAdapter.ViewHolder> {
    
    private static final String TAG = "FawjAdapter";
    
    private Context context;
    private List<Fawj> fawjList;
    private OnFawjActionListener listener;
    
    public interface OnFawjActionListener {
        void onEditFawj(Fawj fawj);
        void onDeleteFawj(Fawj fawj);
    }
    
    public FawjAdapter(Context context, List<Fawj> fawjList, OnFawjActionListener listener) {
        this.context = context;
        this.fawjList = fawjList;
        this.listener = listener;
    }
    
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_fawj, parent, false);
        return new ViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        try {
            if (fawjList == null || position >= fawjList.size()) {
                Log.e(TAG, "Invalid position or null list");
                return;
            }
            
            Fawj fawj = fawjList.get(position);
            
            if (fawj == null) {
                Log.e(TAG, "Fawj at position " + position + " is null");
                return;
            }
            
            // عرض البيانات مع فحص null
            String name = fawj.getName();
            String halaqaName = fawj.getHalaqaName();
            
            holder.tvName.setText(name != null && !name.isEmpty() ? name : "بدون اسم");
            holder.tvHalaqaName.setText(halaqaName != null && !halaqaName.isEmpty() ? halaqaName : "");
            
            // الأزرار
            holder.btnEdit.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onEditFawj(fawj);
                }
            });
            
            holder.btnDelete.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onDeleteFawj(fawj);
                }
            });
            
        } catch (Exception e) {
            Log.e(TAG, "Error binding fawj at position " + position, e);
        }
    }
    
    @Override
    public int getItemCount() {
        return fawjList != null ? fawjList.size() : 0;
    }
    
    public void updateData(List<Fawj> newList) {
        if (newList != null) {
            this.fawjList = newList;
            notifyDataSetChanged();
            Log.d(TAG, "✅ Data updated with " + newList.size() + " items");
        } else {
            Log.w(TAG, "⚠️ Attempted to update with null list");
        }
    }
    
    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvHalaqaName;
        ImageButton btnEdit, btnDelete;
        
        ViewHolder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_name);
            tvHalaqaName = itemView.findViewById(R.id.tv_halaqa_name);
            btnEdit = itemView.findViewById(R.id.btn_edit);
            btnDelete = itemView.findViewById(R.id.btn_delete);
        }
    }
}
