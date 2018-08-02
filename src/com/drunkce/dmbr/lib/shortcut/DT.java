package com.drunkce.dmbr.lib.shortcut;

import com.drunkce.dmbr.lib.view.CmdView;
import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DT {
    public static void print(String str, String suf, String pre){
        CmdView.getInst().print(str, suf, pre);
    }
    public static void print(String str, String suf){
        print(str, suf, "");
    }
    public static void print(String str){
        print(str, "\n\n", "");
    }


    public static <E> Object input(String label, E defaults, ArrayList<E> allow, String suffix) {
        return CmdView.getInst().input(label, defaults, allow, suffix);
    }
    public static <E> Object input(String label, E defaults, ArrayList<E> allow) {
        return input(label, defaults, allow, "\n");
    }
    public static <E> Object input(String label, E defaults) {
        return input(label, defaults, new ArrayList<>(), "\n");
    }
    public static Object input(String label) {
        return input(label, "", new ArrayList<>(), "\n");
    }

    @NotNull
    public static String type(Object obj) {
        return obj.getClass().toString().replace("class ", "");
    }

    @NotNull
    public static Boolean isNumeric(String str) {
        return str.matches("^[+-]?\\d+(\\.\\d+)?$");
    }

    public static Object strto(String str) {
        if (isNumeric(str)) {
            if (str.contains(".")) {
                return Double.parseDouble(str);
            } else {
                return Integer.parseInt(str);
            }
        }
        return str;
    }

    @Nullable
    public static String toISODate(String date) {
        String regex = "^(\\d{4})-?(\\d{2})-?(\\d{2})\\s?(\\d{2}):?(\\d{2})(?::?(\\d{2}))?$";
        Matcher match = Pattern.compile(regex).matcher(date);
        if (!match.find()) return null;
        date = date.replaceFirst(regex, "$1-$2-$3 $4:$5:$6");
        if (match.group(6) == null) date += "00";
        return date;
    }

    private static Thread threadLoading = null;
    private static Boolean isLoading = false;
    public static void loading(Boolean isStart) {
        isLoading = isStart;
        if (!isStart) {
            try { threadLoading.join(); } catch (InterruptedException err) { err.printStackTrace(); }
            System.out.println();
            return;
        }
        threadLoading = new Thread(() -> {
            while (isLoading) {
                System.out.print(".");
                try { Thread.sleep(1000); } catch (InterruptedException err) { err.printStackTrace(); }
            }
        });
        threadLoading.start();
    }

    public static boolean isWindows() {
        return System.getProperties().getProperty("os.name").toLowerCase().contains("windows");
    }

    public static void exit(String str) {
        print(str, "\n", "\n");
        System.exit(0);
    }
}
