package com.halaqat.attendance.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import com.halaqat.attendance.R;
import com.halaqat.attendance.activities.AttendanceReportActivity;
import com.halaqat.attendance.models.Student;
import java.util.List;

public class StudentAttendanceAdapter extends RecyclerView.Adapter<StudentAttendanceAdapter.ViewHolder> {
    
    private Context context;
    private List<Student> studentList;
    
    public StudentAttendanceAdapter(Context context, List<Student> studentList) {
        this.context = context;
        this.studentList = studentList;
    }
    
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_student_attendance, parent, false);
        return new ViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Student student = studentList.get(position);
        
        holder.tvStudentName.setText(student.getFullName());
        
        holder.cardView.setOnClickListener(v -> {
            Intent intent = new Intent(context, AttendanceReportActivity.class);
            intent.putExtra("student_id", student.getId());
            intent.putExtra("student_name", student.getFullName());
            context.startActivity(intent);
        });
    }
    
    @Override
    public int getItemCount() {
        return studentList.size();
    }
    
    public void updateData(List<Student> newList) {
        this.studentList = newList;
        notifyDataSetChanged();
    }
    
    static class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView tvStudentName;
        
        ViewHolder(View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.card_view);
            tvStudentName = itemView.findViewById(R.id.tv_student_name);
        }
    }
}