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
import com.halaqat.attendance.models.User;
import java.util.List;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.ViewHolder> {
    
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
        User user = userList.get(position);
        
        holder.tvName.setText(user.getFullName());
        holder.tvUsername.setText(user.getUsername());
        holder.tvRole.setText(getRoleInArabic(user.getRole()));
        
        holder.btnEdit.setOnClickListener(v -> listener.onEditUser(user));
        holder.btnDelete.setOnClickListener(v -> listener.onDeleteUser(user));
    }
    
    @Override
    public int getItemCount() {
        return userList.size();
    }
    
    public void updateData(List<User> newList) {
        this.userList = newList;
        notifyDataSetChanged();
    }
    
    private String getRoleInArabic(String role) {
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