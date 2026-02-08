package com.halaqat.attendance.adapters;

import android.content.Context;
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
        Halaqa halaqa = halaqatList.get(position);
        
        holder.tvName.setText(halaqa.getName());
        holder.tvDescription.setText(halaqa.getDescription());
        
        holder.btnEdit.setOnClickListener(v -> listener.onEditHalaqa(halaqa));
        holder.btnDelete.setOnClickListener(v -> listener.onDeleteHalaqa(halaqa));
    }
    
    @Override
    public int getItemCount() {
        return halaqatList.size();
    }
    
    public void updateData(List<Halaqa> newList) {
        this.halaqatList = newList;
        notifyDataSetChanged();
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