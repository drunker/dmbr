package com.drunkce.dmbr.lib.view;

import java.util.ArrayList;

public interface View<E> {
    void print(String str, String pre, String suf);

    E input(String label, E defaults, ArrayList<E> allow, String pre);
}
