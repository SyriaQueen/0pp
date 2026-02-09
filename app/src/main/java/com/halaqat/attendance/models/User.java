package com.halaqat.attendance.models;

import com.google.gson.annotations.SerializedName;

public class User {
    @SerializedName("id")
    private int id;
    
    @SerializedName("username")
    private String username;
    
    @SerializedName("full_name")
    private String fullName;
    
    @SerializedName("role")
    private String role;
    
    @SerializedName("email")
    private String email;
    
    @SerializedName("phone")
    private String phone;
    
    @SerializedName("is_active")
    private boolean isActive;
    
    @SerializedName("created_at")
    private String createdAt;
    
    @SerializedName("updated_at")
    private String updatedAt;
    
    // Constructors
    public User() {}
    
    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    
    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }
    
    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
    
    public String getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }
}
