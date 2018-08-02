package com.drunkce.dmbr;

import com.drunkce.dmbr.lib.view.Interface;
import com.drunkce.dmbr.lib.config.Config;
import com.drunkce.dmbr.lib.mysql.Backup;
import com.drunkce.dmbr.lib.mysql.Restore;

public class DMBR {
    public static void main(String[] args) {
        Interface.run(args);
        if (Config.getString("action").equals("1")) { // 备份
            Backup.run();
        } else { // 恢复
            Restore.run();
        }
    }
}
