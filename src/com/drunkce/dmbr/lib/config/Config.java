package com.drunkce.dmbr.lib.config;

import java.io.*;
import java.util.*;

public enum Config {
    inst;

    Properties property;
    HashMap<String, Object> config = new HashMap<>();
    String path = System.getProperty("user.dir") + "/config/config.properties";

    Config () {
        property = new Properties();
        FileInputStream fs = null;
        try {
            // 写入属性文件
            fs = new FileInputStream(path);
            property.load(fs);
            config = new HashMap<>((Map) property);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                fs.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static Object get (String key) {
        return has(key) ? inst.config.get(key) : null;
    }

    public static String getString (String key) {
        return has(key) ? inst.config.get(key).toString() : "";
    }

    public static void set (String key, Object value) {
        inst.config.put(key, value);
    }

    public static Boolean has (String key) {
        return inst.config.containsKey(key);
    }

    public static void store () {
        FileOutputStream bw = null;
        try {
            // 写入属性文件
            bw = new FileOutputStream(inst.path);
            inst.property.store(bw, "DMBR Properties");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                bw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
