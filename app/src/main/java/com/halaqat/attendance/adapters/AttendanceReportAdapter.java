package com.halaqat.attendance.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import com.halaqat.attendance.R;
import com.halaqat.attendance.models.Attendance;
import java.util.List;

public class AttendanceReportAdapter extends RecyclerView.Adapter<AttendanceReportAdapter.ViewHolder> {
    
    private Context context;
    private List<Attendance> attendanceList;
    
    public AttendanceReportAdapter(Context context, List<Attendance> attendanceList) {
        this.context = context;
        this.attendanceList = attendanceList;
    }
    
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_attendance_report, parent, false);
        return new ViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Attendance attendance = attendanceList.get(position);
        
        holder.tvStudentName.setText(attendance.getStudentName());
        holder.tvFawjName.setText(attendance.getFawjName());
        holder.tvDate.setText(attendance.getDate());
        
        String statusText = "";
        int statusColor = 0;
        
        switch (attendance.getStatus()) {
            case "present":
                statusText = "حاضر";
                statusColor = context.getResources().getColor(R.color.gradient_4_start);
                break;
            case "absent":
                statusText = "غائب";
                statusColor = context.getResources().getColor(R.color.gradient_2_end);
                break;
            case "excused":
                statusText = "إجازة";
                statusColor = context.getResources().getColor(R.color.gradient_5_start);
                break;
        }
        
        holder.tvStatus.setText(statusText);
        holder.tvStatus.setTextColor(statusColor);
    }
    
    @Override
    public int getItemCount() {
        return attendanceList.size();
    }
    
    public void updateData(List<Attendance> newList) {
        this.attendanceList = newList;
        notifyDataSetChanged();
    }
    
    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvStudentName, tvFawjName, tvDate, tvStatus;
        
        ViewHolder(View itemView) {
            super(itemView);
            tvStudentName = itemView.findViewById(R.id.tv_student_name);
            tvFawjName = itemView.findViewById(R.id.tv_fawj_name);
            tvDate = itemView.findViewById(R.id.tv_date);
            tvStatus = itemView.findViewById(R.id.tv_status);
        }
    }
}