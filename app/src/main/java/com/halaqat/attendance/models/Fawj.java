package com.halaqat.attendance.models;

import com.google.gson.annotations.SerializedName;

public class Fawj {
    @SerializedName("id")
    private int id;
    
    @SerializedName("name")
    private String name;
    
    @SerializedName("halaqa_id")
    private int halaqaId;
    
    @SerializedName("halaqa_name")
    private String halaqaName;
    
    @SerializedName("is_active")
    private boolean isActive;
    
    @SerializedName("created_at")
    private String createdAt;
    
    @SerializedName("updated_at")
    private String updatedAt;
    
    // Constructors
    public Fawj() {}
    
    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public int getHalaqaId() { return halaqaId; }
    public void setHalaqaId(int halaqaId) { this.halaqaId = halaqaId; }
    
    public String getHalaqaName() { return halaqaName; }
    public void setHalaqaName(String halaqaName) { this.halaqaName = halaqaName; }
    
    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }
    
    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
    
    public String getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }
}
