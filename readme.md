# Drunk MySQL Backup Restore Tool

DMBR是一款Java编写的MySQL备份恢复工具，工具利用mysqldump、mysql、mysqlbinlog完成数据库的备份恢复工作，工具兼容Windows与Linux。通过简单的配置及设置任务计划，即可自动备份Mysql及区间恢复。

PHP、Python等更适合写此类工具，用Java编写主要为了练手。在编写过程中，遇到的最大的坑是折腾了3天的命令行空格坑，尝试各种方式，都不能很好的解决空格路径问题，最终放弃，以配置环境变量解决。Mysql类工具需配置到环境变量中，备份路径、binlog路径中也不能含有空格。本工具会持续维护一个季度左右，在所运维的项目中无典型问题之后将停止更新。

## 配置

配置文件路径：./config/config.properties

```bash
host=192.168.1.222 // 备份恢复的主机地址
port=3306 // 备份恢复数据库端口
ssl=false // 非ssl连接
keeps=30 // 保存备份的版本数
user=root // 数据库用户名
password=drunk // 数据库密码
backup=E:/Cache/backup/ // 备份文件目录(勿带空格)
database=dmbr_test1,dmbr_test2 // 备份恢复的数据库
binlog=E:/Database/MySQL/logs/bin/.index // 需恢复的binlog索引存放位置(勿带空格)
```

每次备份会新建一个版本，当版本超过设置的“备份版本数”时超出的旧版本会删除，同时会将当前新版本存到备份存档目录一份（\_VersionKeeps_），此目录的存档不会自动删除。

若要自动备份，则需配置任务计划。附上命令参数（-a,-d）、或者在配置文件中配好命令参数(action=,database=)即可静默执行任务。

## 流程

备份
* 刷新binlog日志
* 建立版本
* 遍历备份
* 存档
* 清理过期备份

恢复
* SQL恢复
* binlog恢复

预览图

![DMBR](https://www.xinwen1.com/res/temp/dmbr-screen.png)