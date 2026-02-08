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
    
    @SerializedName("created_at")
    private String createdAt;
    
    // Constructors
    public Fawj() {}
    
    public Fawj(int id, String name, int halaqaId) {
        this.id = id;
        this.name = name;
        this.halaqaId = halaqaId;
    }
    
    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public int getHalaqaId() { return halaqaId; }
    public void setHalaqaId(int halaqaId) { this.halaqaId = halaqaId; }
    
    public String getHalaqaName() { return halaqaName; }
    public void setHalaqaName(String halaqaName) { this.halaqaName = halaqaName; }
    
    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
}