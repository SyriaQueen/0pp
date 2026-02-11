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
    
    // ✅ الآن هذا سيعمل مع 0/1 و true/false
    @SerializedName("is_active")
    private boolean isActive;
    
    @SerializedName("created_at")
    private String createdAt;
    
    @SerializedName("updated_at")
    private String updatedAt;
    
    // Constructors
    public Fawj() {
        this.isActive = true;
    }
    
    // Getters and Setters
    public int getId() { 
        return id; 
    }
    
    public void setId(int id) { 
        this.id = id; 
    }
    
    public String getName() { 
        return name != null ? name : ""; 
    }
    
    public void setName(String name) { 
        this.name = name; 
    }
    
    public int getHalaqaId() { 
        return halaqaId; 
    }
    
    public void setHalaqaId(int halaqaId) { 
        this.halaqaId = halaqaId; 
    }
    
    public String getHalaqaName() { 
        return halaqaName != null ? halaqaName : ""; 
    }
    
    public void setHalaqaName(String halaqaName) { 
        this.halaqaName = halaqaName; 
    }
    
    public boolean isActive() { 
        return isActive; 
    }
    
    public void setActive(boolean active) { 
        isActive = active; 
    }
    
    public String getCreatedAt() { 
        return createdAt != null ? createdAt : ""; 
    }
    
    public void setCreatedAt(String createdAt) { 
        this.createdAt = createdAt; 
    }
    
    public String getUpdatedAt() { 
        return updatedAt != null ? updatedAt : ""; 
    }
    
    public void setUpdatedAt(String updatedAt) { 
        this.updatedAt = updatedAt; 
    }
}
