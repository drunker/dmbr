# 初始化数据

drop database if exists dmbr_test1;
drop database if exists dmbr_test2;

create database dmbr_test1 charset utf8;
create database dmbr_test2 charset utf8;

use dmbr_test1;

create table test1(
  id int unsigned not null auto_increment primary key,
  name varchar(63) not null,
  memo varchar(1023) not null default ''
);

insert into test1 (name, memo) values ("测试1", "备注1"), ("测试2", "备注2");

use dmbr_test2;

create table test2(
  id int unsigned not null auto_increment primary key,
  name varchar(63) not null,
  memo varchar(1023) not null default ''
);

insert into test2 (name, memo) values ("测试1", "备注1");




# 插入数据
use dmbr_test1;
insert into test1 (name, memo) values ("测试3", "备注3"), ("测试4", "备注4");

use dmbr_test2;
insert into test2 (name, memo) values ("测试2", "备注2");




# 删除数据
use dmbr_test1;
delete from test1 where id = 3;

use dmbr_test2;
delete from test2 where id = 2;




# 再次删除数据
use dmbr_test1;
delete from test1 where id = 4;