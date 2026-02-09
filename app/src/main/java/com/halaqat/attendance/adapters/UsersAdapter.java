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
import com.halaqat.attendance.models.User;
import java.util.List;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.ViewHolder> {
    
    private static final String TAG = "UsersAdapter";
    
    private Context context;
    private List<User> userList;
    private OnUserActionListener listener;
    
    public interface OnUserActionListener {
        void onEditUser(User user);
        void onDeleteUser(User user);
    }
    
    public UsersAdapter(Context context, List<User> userList, OnUserActionListener listener) {
        this.context = context;
        this.userList = userList;
        this.listener = listener;
    }
    
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_user, parent, false);
        return new ViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        try {
            User user = userList.get(position);
            
            if (user != null) {
                holder.tvName.setText(user.getFullName() != null ? user.getFullName() : "بدون اسم");
                holder.tvUsername.setText(user.getUsername() != null ? user.getUsername() : "");
                holder.tvRole.setText(getRoleInArabic(user.getRole()));
                
                holder.btnEdit.setOnClickListener(v -> {
                    if (listener != null) {
                        listener.onEditUser(user);
                    }
                });
                
                holder.btnDelete.setOnClickListener(v -> {
                    if (listener != null) {
                        listener.onDeleteUser(user);
                    }
                });
            }
        } catch (Exception e) {
            Log.e(TAG, "Error binding user at position " + position, e);
        }
    }
    
    @Override
    public int getItemCount() {
        return userList != null ? userList.size() : 0;
    }
    
    public void updateData(List<User> newList) {
        if (newList != null) {
            this.userList = newList;
            notifyDataSetChanged();
            Log.d(TAG, "Data updated with " + newList.size() + " items");
        } else {
            Log.w(TAG, "Attempted to update with null list");
        }
    }
    
    private String getRoleInArabic(String role) {
        if (role == null) return "";
        
        switch (role.toLowerCase()) {
            case "admin": return "مدير";
            case "teacher": return "معلم";
            case "parent": return "ولي أمر";
            default: return role;
        }
    }
    
    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvUsername, tvRole;
        ImageButton btnEdit, btnDelete;
        
        ViewHolder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_name);
            tvUsername = itemView.findViewById(R.id.tv_username);
            tvRole = itemView.findViewById(R.id.tv_role);
            btnEdit = itemView.findViewById(R.id.btn_edit);
            btnDelete = itemView.findViewById(R.id.btn_delete);
        }
    }
}
