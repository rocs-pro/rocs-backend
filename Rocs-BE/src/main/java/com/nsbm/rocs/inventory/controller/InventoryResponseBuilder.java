package com.nsbm.rocs.inventory.controller;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

final class InventoryResponseBuilder {

    private InventoryResponseBuilder() {
    }

    static Map<String, Object> build(Object data, int count, String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", data);
        response.put("count", count);
        response.put("message", message);
        return response;
    }

    static Map<String, Object> build(Object data, String message) {
        return build(data, determineCount(data), message);
    }

    static Map<String, Object> buildMessage(String message) {
        return build(null, 0, message);
    }

    private static int determineCount(Object data) {
        if (data == null) {
            return 0;
        }
        if (data instanceof Collection<?> collection) {
            return collection.size();
        }
        if (data.getClass().isArray()) {
            return Array.getLength(data);
        }
        return 1;
    }
}

