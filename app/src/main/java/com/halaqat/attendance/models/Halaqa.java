package com.halaqat.attendance.models;

import com.google.gson.annotations.SerializedName;

public class Halaqa {
    @SerializedName("id")
    private int id;
    
    @SerializedName("name")
    private String name;
    
    @SerializedName("description")
    private String description;
    
    @SerializedName("is_active")
    private Boolean isActive;
    
    @SerializedName("created_at")
    private String createdAt;
    
    @SerializedName("updated_at")
    private String updatedAt;
    
    // Constructors
    public Halaqa() {
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
    
    public String getDescription() { 
        return description != null ? description : ""; 
    }
    
    public void setDescription(String description) { 
        this.description = description; 
    }
    
    public boolean isActive() { 
        return isActive != null ? isActive : true; 
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
