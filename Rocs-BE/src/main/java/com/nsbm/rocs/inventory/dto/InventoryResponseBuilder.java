package com.nsbm.rocs.inventory.dto;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class InventoryResponseBuilder {

    public static Map<String, Object> build(Object data, String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", message);
        response.put("data", data);
        response.put("timestamp", LocalDateTime.now());
        return response;
    }

    public static Map<String, Object> error(String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("message", message);
        response.put("data", null);
        response.put("timestamp", LocalDateTime.now());
        return response;
    }
}

