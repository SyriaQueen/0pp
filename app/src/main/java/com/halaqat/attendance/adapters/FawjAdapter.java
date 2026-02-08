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
import com.halaqat.attendance.models.Fawj;
import java.util.List;

public class FawjAdapter extends RecyclerView.Adapter<FawjAdapter.ViewHolder> {
    
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
        Fawj fawj = fawjList.get(position);
        
        holder.tvName.setText(fawj.getName());
        holder.tvHalaqa.setText(fawj.getHalaqaName());
        
        holder.btnEdit.setOnClickListener(v -> listener.onEditFawj(fawj));
        holder.btnDelete.setOnClickListener(v -> listener.onDeleteFawj(fawj));
    }
    
    @Override
    public int getItemCount() {
        return fawjList.size();
    }
    
    public void updateData(List<Fawj> newList) {
        this.fawjList = newList;
        notifyDataSetChanged();
    }
    
    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvHalaqa;
        ImageButton btnEdit, btnDelete;
        
        ViewHolder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_name);
            tvHalaqa = itemView.findViewById(R.id.tv_halaqa);
            btnEdit = itemView.findViewById(R.id.btn_edit);
            btnDelete = itemView.findViewById(R.id.btn_delete);
        }
    }
}