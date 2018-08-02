package com.drunkce.dmbr.lib.view;

import com.drunkce.dmbr.lib.shortcut.DT;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class CmdView<E> implements View<E>{

    public void print(String str, String suf, String pre) {
        System.out.print(pre + str + suf);
    }

    public E input(String label, E defaults, ArrayList<E> allow, String suffix) {
        String value = "";
        print(label, "", "");
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        try {
            value = br.readLine().trim();
            print(suffix, "", "");
            if (value.equals("")) value = defaults.toString();
            // 如果有限制范围, 且输入值未在范围内, 则让重新输入
            if (!allow.isEmpty() && !allow.contains(DT.type(defaults).equals("java.lang.String") ? value : DT.strto(value))) return input(label, defaults, allow, suffix);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return (E) value;
    }



    private static CmdView inst = new CmdView();

    public static CmdView getInst() {
        return inst;
    }
}
