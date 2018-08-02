package com.drunkce.dmbr.lib.mysql;

import com.drunkce.dmbr.lib.config.Config;
import com.drunkce.dmbr.lib.shortcut.DT;

import java.io.*;
import java.nio.charset.Charset;
import java.util.*;

public abstract class Tool {
    String db;

    private static Runtime runtime = Runtime.getRuntime();

    private static String cmd, c;

    static String binPath,
            username,
            host,
            port,
            binlog,
            password,
            libPath,
            startCmd,
            stopCmd,
            keepPath;
    static int keeps;
    private static File keep;

    static {
        init();
    }

    private static void init () {
        if (DT.isWindows()) {
            cmd = "cmd";    c = "/c";
        } else {
            cmd = "sh";     c = "-c";
        }
        binPath = (Config.has("mysql") ? Config.getString("mysql") + "/" : "");
        username = Config.getString("user");
        password = Config.getString("password");
        host = Config.getString("host");
        port = Config.getString("port");
        keeps = Integer.parseInt(Config.getString("keeps"));
        binlog = Config.getString("binlog");
        libPath = Config.getString("backup");
        startCmd = Config.getString("start");
        stopCmd = Config.getString("stop");
        if (!new File(libPath).isDirectory()) libPath = System.getProperty("user.dir") + "/" + libPath;
        if (!new File(libPath).isDirectory()) DT.exit("备份保存目录不存在");
        keepPath = libPath + "/_VersionKeeps_";
        keep = new File(keepPath);
        if (!keep.isDirectory() && !keep.mkdir()) DT.exit("备份存档目录新建失败");
    }

    static ArrayList<File> getVersions () {
        File[] paths = new File(libPath).listFiles();
        ArrayList<File> versions = new ArrayList<>();
        for (File path : paths) {
            if (path.equals(keep)) continue;
            versions.add(path);
        }
        return versions;
    }

    static ArrayList<File> getKeepVersions () {
        File[] paths = keep.listFiles();
        ArrayList<File> versions = new ArrayList<>();
        Collections.addAll(versions, paths);
        return versions;
    }

    static String cmd (String command) {
        command = cmd + " " + c + " " + command;
        try {
            return exec(runtime.exec(command));
        } catch (Exception err) {
            err.printStackTrace();
            return null;
        }
    }

    static String cmd (ArrayList<String> command) {
        command.add(0, cmd);
        command.add(1, c);
//        DT.print(command.toString());
        String[] cmd = command.toArray(new String[command.size()]);
        try {
            return exec(runtime.exec(cmd));
        } catch (Exception err) {
            err.printStackTrace();
            return null;
        }
    }

    public static String cmd(String[] command) {
        return cmd(new ArrayList<>(Arrays.asList(command)));
    }

    private static String exec(Process process) throws Exception {
        final StringBuffer stringBuffer = new StringBuffer();
//            System.out.print(Arrays.toString(cmd));
        new Thread(() -> {
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream(), Charset.forName("GBK")));
                String line = null;
                while ((line = reader.readLine()) != null) {
                    stringBuffer.append(line);
                }
//                DT.print(stringBuffer.toString());
                reader.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
        new Thread(() -> {
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getErrorStream(), Charset.forName("GBK")));
                String line = null;
                while ((line = reader.readLine()) != null) {
                    stringBuffer.append(line);
                }
//                DT.print(stringBuffer.toString());
                reader.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
        process.getOutputStream().close();
        process.waitFor();
        return stringBuffer.toString();
    }
}
