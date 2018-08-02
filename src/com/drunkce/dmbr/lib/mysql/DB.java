package com.drunkce.dmbr.lib.mysql;

import com.drunkce.dmbr.lib.config.Config;

import java.sql.*;
import java.util.*;

public enum  DB {
    inst;

    static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
    private Connection conn = null;
    public Statement stmt = null;

    DB() {
        try {
            Class.forName(JDBC_DRIVER);
            conn = DriverManager.getConnection("jdbc:mysql://" +Config.getString("host") + ":"+Config.getString("port") +"/?serverTimezone=PRC&useSSL="+Config.getString("ssl"),
                    Config.getString("user"),Config.getString("password"));
            stmt = conn.createStatement();
        } catch(SQLException se) {
            se.printStackTrace();
        } catch(Exception e) { // 处理 Class.forName 错误
            e.printStackTrace();
        }
    }

    public void close () throws java.sql.SQLException{
        stmt.close();
        conn.close();
    }
}
