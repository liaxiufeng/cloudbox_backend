-- 请先运行photos.sql
use cloudbox;

create table user(
	uid int auto_increment,
	username varchar(50) not null UNIQUE,
	email varchar(100) not null UNIQUE,
	`password` varchar(50) not null,
	describe_word VARCHAR(200),
	home_file int,
	birthday date,
	account_birthday date,
	last_chat int,
	photo mediumtext,
	sex int,
	PRIMARY KEY(uid)
);


-- 使用的空间
insert into user values
(1,'root','root@163.com','123456abc','管理员账号',null,'2000-01-15','2020-2-4',null,null,1),
(2,'liujun','liujun@163.com','123456abc','我虽无意逐鹿，叶问张天痔',null,'2002-6-2','2019-2-4',null,null,1),
(3,'娜可露露','nakelulu@163.com','123456abc','马尔哈哈，鸭梨噶多',null,'2001-11-11','2021-2-4',null,null,2),
(4,'李白','libai@163.com','123456abc','我的心可不冷！',null,'2003-6-29','2018-7-4',null,null,1),
(5,'梦琪','mengqi@163.com','123456abc','来重庆吃火锅！',null,'2005-10-12','2019-10-2',null,null,1);

UPDATE user set photo = (select base64_code from photos where user.username = photos.name);

drop table photos;

create table mail_code(
	email VARCHAR(100) primary key,
	code varchar(20) not null,
	date date not null
);

create table file_item(
	fid int auto_increment,
	parent int,
	uid int null,
	`name` varchar(50) not null,
	is_folder int(2) not null DEFAULT 1,
	is_heart int(2) DEFAULT 0,
	PRIMARY KEY(fid),
	FOREIGN key(parent) REFERENCES file_item(fid) ON DELETE CASCADE ON UPDATE CASCADE,
	FOREIGN key(uid) REFERENCES user(uid) ON DELETE CASCADE ON UPDATE CASCADE
);


insert into file_item VALUES
(1,NULL,1,'root',1,0),
(2,null,2,'liujun',1,0),
(3,2,2,'css',1,0),
(4,2,2,'empty',1,0),
(5,2,2,'images',1,0),
(6,2,2,'js',1,0),
(7,2,2,'html.html',0,0),
(8,3,2,'css.css',0,0),
(9,5,2,'png.png',0,0),
(10,5,2,'jpg.jpg',0,0),
(11,6,2,'js.js',0,0),
(12,null,3,'nakelulu',1,0),
(13,null,4,'libai',1,0),
(14,null,5,'mengqi',1,0);


update user set home_file = 1 where uid = 1;
update user set home_file = 2 where uid = 2;
update user set home_file = 12 where uid = 3;
update user set home_file = 13 where uid = 4;
update user set home_file = 14 where uid = 5;


CREATE TABLE recent_open_file(
	rid int auto_increment,
	fid int NULL,
	uid int NULL,
	is_folder int,
	time datetime not null,
	PRIMARY KEY(rid),
	FOREIGN key(uid) REFERENCES user(uid) ON DELETE CASCADE ON UPDATE CASCADE,
	FOREIGN key(fid) REFERENCES file_item(fid) ON DELETE CASCADE ON UPDATE CASCADE
);
 

CREATE table user_relation(
  rid int auto_increment,
	proposer int not null,
	verifier int not null,
	time datetime not null,
	PRIMARY KEY(rid),
	FOREIGN key(proposer) REFERENCES user(uid) ON DELETE CASCADE ON UPDATE CASCADE,
	FOREIGN key(verifier) REFERENCES user(uid) ON DELETE CASCADE ON UPDATE CASCADE
);

insert into user_relation values
(1,1,2,'2020-2-2 01:02:21'),
(2,3,2,'2021-3-11 12:12:12'),
(3,4,2,'2021-6-6 11:23:00'),
(4,5,2,'2022-12-12 21:07:52');


create table user_message(
	mid int auto_increment,
	type int not null,
	proposer int not null,
	verifier int not null,
	propose_time datetime not null,
	is_read int not null,
	message varchar(200),
	PRIMARY KEY(mid),
	FOREIGN key(proposer) REFERENCES user(uid) ON DELETE CASCADE ON UPDATE CASCADE,
	FOREIGN key(verifier) REFERENCES user(uid) ON DELETE CASCADE ON UPDATE CASCADE
);

insert into user_message values
(1,1,1,2,'2020-2-2 01:02:21',0,'我是管理员'),
(2,2,1,2,'2020-2-2 01:12:21',0,'1'),
(3,2,3,2,'2020-2-2 01:02:21',0,'2'),
(4,1,3,2,'2020-2-2 01:12:21',0,'我是娜可露露，我会马尔哈哈'),
(5,1,4,2,'2020-2-2 01:02:21',0,'我是李白，我特别能刮痧'),
(6,2,4,2,'2020-2-2 01:02:21',0,'3'),
(7,2,5,2,'2020-2-2 01:02:21',0,'4'),
(8,1,5,2,'2020-2-2 01:02:21',0,
'我是梦琪，欢迎来重庆吃火锅
我是梦琪，欢迎来重庆吃火锅
我是梦琪，欢迎来重庆吃火锅
哈哈哈'),
(9,1,2,5,'2020-2-2 01:12:21',0,'好的');

-- insert into

-- delete from user_message where  (proposer = 1 and verifier = 2) or (verifier = 2 and proposer = 1)
				
-- select friend_uid_table.proposer as proposer,-- 			(select count(*) from user_message m1 where m1.proposer = friend_uid_table.proposer and  m1.verifier = 2 and is_read = 0) as not_read_number,
-- 			(select type from user_message m2 where m2.proposer = friend_uid_table.proposer and  m2.verifier = 2 order by m2.mid desc limit 0,1) as type,
-- 			(select message from user_message m3 where m3.proposer = friend_uid_table.proposer and  m3.verifier = 2 order by m3.mid desc limit 0,1) as last_message
-- from (select proposer from user_relation where verifier = 2 union select verifier from user_relation where proposer = 2) as friend_uid_table;


-- select friend_uid_table.proposer as proposer,
-- 			(select count(*) from user_message m1 where m1.proposer = friend_uid_table.proposer and  m1.verifier = 2 and is_read = 0) as not_read_number,
-- 			(select type from user_message m2 where m2.proposer = friend_uid_table.proposer and  m2.verifier = 2 order by m2.mid desc limit 0,1) as type,
-- 			(select message from user_message m3 where m3.proposer = friend_uid_table.proposer and  m3.verifier = 2 order by m3.mid desc limit 0,1) as last_message
-- from (select proposer from user_relation where verifier = 2 union select verifier from user_relation where proposer = 2) as friend_uid_table;
-- 				