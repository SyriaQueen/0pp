package com.halaqat.attendance.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.halaqat.attendance.R;
import com.halaqat.attendance.models.Student;
import java.util.*;

public class AttendanceMarkAdapter extends RecyclerView.Adapter<AttendanceMarkAdapter.ViewHolder> {
    
    private Context context;
    private List<Student> studentList;
    private Map<Integer, String> attendanceStatus = new HashMap<>();
    
    public AttendanceMarkAdapter(Context context, List<Student> studentList) {
        this.context = context;
        this.studentList = studentList;
    }
    
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_attendance_mark, parent, false);
        return new ViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Student student = studentList.get(position);
        
        holder.tvStudentName.setText(student.getFullName());
        
        String status = attendanceStatus.get(student.getId());
        if (status != null) {
            switch (status) {
                case "present":
                    holder.rbPresent.setChecked(true);
                    break;
                case "absent":
                    holder.rbAbsent.setChecked(true);
                    break;
                case "excused":
                    holder.rbExcused.setChecked(true);
                    break;
            }
        }
        
        holder.rgStatus.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rb_present) {
                attendanceStatus.put(student.getId(), "present");
            } else if (checkedId == R.id.rb_absent) {
                attendanceStatus.put(student.getId(), "absent");
            } else if (checkedId == R.id.rb_excused) {
                attendanceStatus.put(student.getId(), "excused");
            }
        });
    }
    
    @Override
    public int getItemCount() {
        return studentList.size();
    }
    
    public void updateData(List<Student> newList) {
        this.studentList = newList;
        this.attendanceStatus.clear();
        notifyDataSetChanged();
    }
    
    public void markAllPresent() {
        for (Student student : studentList) {
            attendanceStatus.put(student.getId(), "present");
        }
        notifyDataSetChanged();
    }
    
    public List<Map<String, Object>> getAttendanceData(int fawjId, String date) {
        List<Map<String, Object>> attendanceList = new ArrayList<>();
        
        for (Student student : studentList) {
            String status = attendanceStatus.get(student.getId());
            if (status != null) {
                Map<String, Object> attendance = new HashMap<>();
                attendance.put("student_id", student.getId());
                attendance.put("fawj_id", fawjId);
                attendance.put("date", date);
                attendance.put("status", status);
                attendanceList.add(attendance);
            }
        }
        
        return attendanceList;
    }
    
    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvStudentName;
        RadioGroup rgStatus;
        RadioButton rbPresent, rbAbsent, rbExcused;
        
        ViewHolder(View itemView) {
            super(itemView);
            tvStudentName = itemView.findViewById(R.id.tv_student_name);
            rgStatus = itemView.findViewById(R.id.rg_status);
            rbPresent = itemView.findViewById(R.id.rb_present);
            rbAbsent = itemView.findViewById(R.id.rb_absent);
            rbExcused = itemView.findViewById(R.id.rb_excused);
        }
    }
}
