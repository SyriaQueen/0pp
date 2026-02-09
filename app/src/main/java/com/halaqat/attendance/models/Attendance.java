package com.halaqat.attendance.models;

import com.google.gson.annotations.SerializedName;

public class Attendance {
    @SerializedName("id")
    private int id;
    
    @SerializedName("student_id")
    private int studentId;
    
    @SerializedName("student_name")
    private String studentName;
    
    @SerializedName("fawj_id")
    private int fawjId;
    
    @SerializedName("fawj_name")
    private String fawjName;
    
    @SerializedName("halaqa_name")
    private String halaqaName;
    
    @SerializedName("date")
    private String date;
    
    @SerializedName("status")
    private String status;
    
    @SerializedName("notes")
    private String notes;
    
    @SerializedName("marked_by")
    private int markedBy;
    
    @SerializedName("marked_by_name")
    private String markedByName;
    
    @SerializedName("created_at")
    private String createdAt;
    
    @SerializedName("updated_at")
    private String updatedAt;
    
    // Constructors
    public Attendance() {}
    
    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public int getStudentId() { return studentId; }
    public void setStudentId(int studentId) { this.studentId = studentId; }
    
    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }
    
    public int getFawjId() { return fawjId; }
    public void setFawjId(int fawjId) { this.fawjId = fawjId; }
    
    public String getFawjName() { return fawjName; }
    public void setFawjName(String fawjName) { this.fawjName = fawjName; }
    
    public String getHalaqaName() { return halaqaName; }
    public void setHalaqaName(String halaqaName) { this.halaqaName = halaqaName; }
    
    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    
    public int getMarkedBy() { return markedBy; }
    public void setMarkedBy(int markedBy) { this.markedBy = markedBy; }
    
    public String getMarkedByName() { return markedByName; }
    public void setMarkedByName(String markedByName) { this.markedByName = markedByName; }
    
    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
    
    public String getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }
}
