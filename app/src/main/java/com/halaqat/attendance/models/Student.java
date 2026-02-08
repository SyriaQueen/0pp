package com.halaqat.attendance.models;

import com.google.gson.annotations.SerializedName;

public class Student {
    @SerializedName("id")
    private int id;
    
    @SerializedName("full_name")
    private String fullName;
    
    @SerializedName("parent_id")
    private int parentId;
    
    @SerializedName("parent_name")
    private String parentName;
    
    @SerializedName("date_of_birth")
    private String dateOfBirth;
    
    @SerializedName("gender")
    private String gender;
    
    @SerializedName("notes")
    private String notes;
    
    @SerializedName("created_at")
    private String createdAt;
    
    private boolean isSelected = false;
    
    // Constructors
    public Student() {}
    
    public Student(int id, String fullName, int parentId) {
        this.id = id;
        this.fullName = fullName;
        this.parentId = parentId;
    }
    
    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    
    public int getParentId() { return parentId; }
    public void setParentId(int parentId) { this.parentId = parentId; }
    
    public String getParentName() { return parentName; }
    public void setParentName(String parentName) { this.parentName = parentName; }
    
    public String getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(String dateOfBirth) { this.dateOfBirth = dateOfBirth; }
    
    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }
    
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    
    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
    
    public boolean isSelected() { return isSelected; }
    public void setSelected(boolean selected) { isSelected = selected; }
}