package com.drunkce.dmbr.lib.view;

import com.drunkce.dmbr.lib.config.Config;
import com.drunkce.dmbr.lib.mysql.Backup;
import com.drunkce.dmbr.lib.mysql.Tool;
import com.drunkce.dmbr.lib.mysql.Restore;
import com.drunkce.dmbr.lib.shortcut.DT;
import org.apache.commons.cli.*;
import java.util.*;

public class Interface {
    private Interface (String[] args) {
        parseCmd(args);
        init();
    }

    private void parseCmd (String [] args) {
        CommandLineParser parser = new DefaultParser( );
        Options options = new Options( );
        options.addOption("a", "action", true, "指定备份/恢复操作");
        options.addOption("d", "database", true, "选定的数据库,多个以逗号隔开,all表示所有");
        options.addOption("v", "version", true, "指定需恢复的备份版本" );
        options.addOption("r", "recovery", true, "指定要恢复至" );
        try {
            CommandLine cmdArgs = parser.parse(options, args);
            if (cmdArgs.hasOption("a") && !Config.has("action")) Config.set("action", cmdArgs.getOptionValue("a"));
            if (cmdArgs.hasOption("d") && !Config.has("database")) Config.set("database", cmdArgs.getOptionValue("d"));
            if (cmdArgs.hasOption("v") && !Config.has("version")) Config.set("version", cmdArgs.getOptionValue("v"));
            if (cmdArgs.hasOption("r") && !Config.has("recovery")) Config.set("recovery", cmdArgs.getOptionValue("r"));
        } catch (ParseException ex) {
            ex.printStackTrace();
        }
    }

    private void init () {
        action();
        version();
        database();
        recovery();
    }

    private void action () {
        if (Config.has("version")) Config.set("action", 2); // 如果有指定版本, 则为恢复
        if (!Config.has("action")) Config.set("action", DT.input("备份或恢复(1/2)[1]:", "1", new ArrayList<>(Arrays.asList("1", "2"))));
    }

    private void version () {
        if (!Config.getString("action").equals("2")) return;
        if (!Config.has("version")) { // 如果未指定, 则让选择需恢复的版本
            String[] versionNames = Restore.getVersionNames();
            listDbs(versionNames, "可选的备份版本");
            Config.set("version", DT.input("要恢复的备份版本：", "", new ArrayList<>(Arrays.asList(versionNames))));
        }
        Restore.setVersion(Config.getString("version"));
    }

    private void database () {
        String[] dbs;
        String selects = Config.getString("database"); // 从配置读取

        if (Config.getString("action").equals("2")) { // 从恢复中选择数据库
            dbs = Restore.getDbs();
            if (selects == "") {
                listDbs(dbs, "可选数据库");
                selects = DT.input("要恢复的数据库(db1,db2|all)：").toString();
            }
        } else { // 取数据库列表
            dbs = Backup.getDbs();
            if (selects == "") {
                listDbs(dbs, "可选数据库");
                selects = DT.input("要备份的数据库(db1,db2|all)：").toString();
            }
        }
        String[] sels = selects.split(",");
        if (selects.equals("all")) sels = dbs;
        for (String db : sels) if (-1 == Arrays.binarySearch(dbs, db))  DT.exit("数据库" + db + "不存在");
        Config.set("database", sels);
    }

    private void recovery () {
        // 如果非恢复, 或未配置binlog, 则无需指定恢复截至时间点
        if (!Config.getString("action").equals("2") || Config.getString("binlog").equals("") || Config.has("recovery")) return;
        String recovery = DT.input("恢复Binlog数据至(留空不恢复)：").toString();
        Boolean isSetted = Restore.setRecovery(recovery);
        if (!isSetted) recovery(); // 如果有设置, 且设置的值无效, 则让其重新设置
        Config.set("recovery", recovery);
    }

    private void listDbs (String[] dbs, String label) {
        DT.print(label, "\n");
        for (String db : dbs) DT.print(db, "     ");
        DT.print("");
    }

    public static void run (String[] args) {
        new Interface(args);
    }
}
