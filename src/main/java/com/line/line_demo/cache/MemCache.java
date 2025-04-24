package com.line.line_demo.cache;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@Setter
@Getter
public class MemCache {

    public static Map<String, Object> mapData = new HashMap<>();

    public static void addData(String key, Object value) {
        mapData.put(key, value);
    }

    public static String getAsString(String key) {
        return (String) mapData.get(key);
    }

    public static Object getByKey(String key) {
        return mapData.get(key);
    }

}
