package com.halaqat.attendance.utils;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;

/**
 * Custom Deserializer to handle boolean values that come as 0/1 from backend
 */
public class BooleanDeserializer implements JsonDeserializer<Boolean> {
    
    @Override
    public Boolean deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
        
        try {
            // إذا كانت null أرجع false
            if (json.isJsonNull()) {
                return false;
            }
            
            // إذا كانت boolean بالفعل
            if (json.isJsonPrimitive() && json.getAsJsonPrimitive().isBoolean()) {
                return json.getAsBoolean();
            }
            
            // إذا كانت number (0 أو 1)
            if (json.isJsonPrimitive() && json.getAsJsonPrimitive().isNumber()) {
                int value = json.getAsInt();
                return value != 0; // أي رقم غير 0 يُعتبر true
            }
            
            // إذا كانت string
            if (json.isJsonPrimitive() && json.getAsJsonPrimitive().isString()) {
                String value = json.getAsString().toLowerCase().trim();
                // تحويل "true", "1", "yes" إلى true
                return value.equals("true") || value.equals("1") || value.equals("yes");
            }
            
            // القيمة الافتراضية
            return false;
            
        } catch (Exception e) {
            // في حالة أي خطأ، أرجع false
            return false;
        }
    }
}
