package com.drunkce.dmbr.lib.mysql;

import com.drunkce.dmbr.lib.config.Config;
import com.drunkce.dmbr.lib.shortcut.DT;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Restore extends Tool {
    private static String toolName = "mysql",
        binlogTool = "mysqlbinlog",
        versionName = null,
        versionPath = null,
        binlogFrom = null,
        binlogTo = null;

    private static String[] binlogPaths = null;

    private Restore(String db) {
        this.db = db;
        DT.print("正在恢复" + db + ": ", "");
        DT.loading(true);
        execute();
        binlog();
        DT.loading(false);
    }

    /**
     * 还原
     */
    private void execute () {
        String backupPath = versionPath + "/" + db + ".sql";
        File backup = new File(backupPath);
        if (!backup.isFile()) DT.exit("数据库" + db + "备份文件不存在");
        String[] command = {toolName + " -u" + username + " -p" + password + " -h" + host + " -P " + port + " -D " + db + "<" + backupPath};
//        DT.print(command.toString());
        cmd(command);
    }

    /**
     * 从binlog恢复数据
     */
    private void binlog () {
        if (binlogFrom == null) return; // 如果未设置, 则不从binlog恢复
        String[] command;
        for (String binlogPath : binlogPaths) { // 遍历binlog, 顺序遍历恢复
            command = new String[]{binlogTool + " --start-datetime " + binlogFrom + " --stop-datetime " + binlogTo + " -d " + db + " " + binlogPath
                    + " | " + toolName + " -u" + username + " -p" + password + " -h" + host + " -P " + port + " -v " + db};
            String result = cmd(command);
//            System.out.println(result);
        }
    }

    /**
     * 取版本名组
     * @return String
     */
    public static String[] getVersionNames () {
        ArrayList<File> versions = getVersions();
        int size = versions.size();
        String[] versionNames = new String[size];
        for (int i=0; i<size; i++) versionNames[i] = versions.get(i).getName();
        return versionNames;
    }

    /**
     * 取版本的对应备份数据库
     * @return String[]
     */
    public static String[] getDbs () {
        String versionName = Config.getString("version");
        File[] versions = new File(libPath + "/" + versionName).listFiles();
        int length = versions == null ? 0 : versions.length;
        String[] dbs = new String[length];
        String dbName;
        for (int i=0; i<length; i++) {
            dbName = versions[i].getName();
            dbs[i] = dbName.substring(0, dbName.indexOf("."));
        }
        return dbs;
    }

    /**
     * 设置版本
     * @param versionName
     */
    public static void setVersion (String versionName) {
        Restore.versionName = versionName;
        versionPath = libPath + "/" + versionName;
    }

    /**
     * 设置恢复区间, 及binlog文件路径集
     * @param recovery 恢复至
     * @return Boolean
     */
    public static boolean setRecovery (String recovery) {
        if (recovery.equals("")) return true;
        binlogFrom = versionName;
        if ((binlogTo = DT.toISODate(recovery)) == null) return false;
        binlogTo = binlogTo.replaceAll("[-:\\s]", "");
        String binlogIndexPath = Config.getString("binlog");
        String binlogPath = binlogIndexPath.replaceFirst("[^\\/]+$", "");
        String binlogName;
        ArrayList<String> files = new ArrayList<>();
        try {
            BufferedReader fis = new BufferedReader(new FileReader(new File(binlogIndexPath)));
            while ((binlogName = fis.readLine()) != null) {
                binlogName = binlogName.substring(binlogName.lastIndexOf(File.separator) + 1);
                files.add(binlogPath + "/" + binlogName);
            }
            binlogPaths = files.toArray(new String[files.size()]);
            return true;
        } catch (FileNotFoundException err) {
            err.printStackTrace();
        } catch (IOException err) {
            err.printStackTrace();
        }
        return false;
    }

    /**
     * 启动/停止mysql
     * @param isStart 是否启动
     */
    private static void startMysql (Boolean isStart) {
        String result = cmd(isStart ? startCmd : stopCmd);
        DT.print(result);
    }

    public static void run() {
//        startMysql(false);
        String[] selects = (String[]) Config.get("database");
        for (String db : selects)  new Restore(db);
//        startMysql(true);
        DT.print("恢复完毕", "\n", "\n");
    }
}
