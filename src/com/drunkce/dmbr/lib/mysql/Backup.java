package com.drunkce.dmbr.lib.mysql;

import com.drunkce.dmbr.lib.config.Config;
import com.drunkce.dmbr.lib.shortcut.DT;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class Backup extends Tool {
    private static String versionPath = null,
        versionName = null,
        toolName = "mysqldump";
    private static Boolean isNeedKeep = null;

    private String backupPath;
    private File backup;

    private Backup(String db) {
        this.db = db;
        DT.print("正在备份" + db + ": ", "");
        DT.loading(true);
        version();
        execute();
        keep();
        clear();
        DT.loading(false);
    }

    /**
     * 新建版本
     */
    private void version () {
        if (versionPath == null) {
            versionName = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
            versionPath = libPath + "/" + versionName;
//            DT.print(versionPath);
            File version = new File(versionPath);
            if (!version.isDirectory() && !version.mkdir()) DT.exit("备份目录创建失败!");
        }
        backupPath = versionPath + "/" + db + ".sql";
    }

    /**
     * 备份
     */
    private void execute () {
//        ArrayList<String> command = new ArrayList<>(Arrays.asList(toolName, "-u" + username, "-p" + password, "-h", host, "-P", port, "--databases", db, ">", backupPath));
        String[] command = {toolName + " -u" + username + " -p" + password + " -h" + host + " -P " + port + " -B " + db + " -r " + backupPath};
//        DT.print(command.toString());
        String result = cmd(command);
//        DT.print(result);
        backup = new File(backupPath);
        if (!backup.isFile()) DT.exit(db + "备份失败");
    }

    /**
     * 备份存档
     */
    private void keep () {
        ArrayList<File> versions = getVersions();
        ArrayList<File> versionKeeps = getKeepVersions();
        if (isNeedKeep == null) { // 第一次才判断是否需存档
            for (File versionKeep : versionKeeps) for (File version : versions) if (version.getName().equals(versionKeep.getName())) {
                isNeedKeep = false; // 如果某备份版本已存档, 则本次不存档
                return;
            }
            isNeedKeep = true;
        } else if (!isNeedKeep) {
            return;
        }
        String versionKeepPath = keepPath + "/" + versionName;
        File versionKeep = new File(versionKeepPath);
        if (!versionKeep.isDirectory() && !versionKeep.mkdir()) DT.exit("创建备份版本存档目录失败");
        File backupKeep = new File(versionKeepPath + "/" + db + ".sql");
        try {
            Files.copy(backup.toPath(), backupKeep.toPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 清理备份
     */
    private void clear () {
        ArrayList<File> versions = getVersions();
        int expired = versions.size() - keeps;
        if (expired < 1) return;
        Collections.sort(versions, Comparator.comparing(employee -> employee.getName()));
        int counter = 1;
        for (File file: versions) {
            try {
                FileUtils.deleteDirectory(file);
            } catch (IOException err) {
                err.printStackTrace();
            }
            if (++counter > expired) break;
        }
    }

    /**
     * 取数据库集
     * @return String[]
     */
    public static String[] getDbs () {
        List<String> dbsFilter = Arrays.asList("mysql", "performance_schema", "information_schema", "sys");
        ArrayList<String> databases = new ArrayList<>();
        try {
            ResultSet rs = DB.inst.stmt.executeQuery("show databases;");
            while (rs.next()) {
                String database = rs.getString("Database");
                if (dbsFilter.contains(database)) continue; // 排除掉mysql自带表
                databases.add(database);
            }
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return databases.toArray(new String[databases.size()]);
    }

    /**
     * 刷新二进制日志
     */
    private static void binlog () {
        if (binlog == "") return;
        try {
            DB.inst.stmt.execute("flush logs;");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void run() {
        binlog();
        String[] selects = (String[]) Config.get("database");
        for (String db : selects)  new Backup(db);
        DT.print("备份完毕", "\n", "\n");
    }
}
