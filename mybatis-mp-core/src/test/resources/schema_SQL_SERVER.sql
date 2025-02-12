/*
 *  Copyright (c) 2024-2025, Ai东 (abc-127@live.cn).
 *
 *  Licensed under the Apache License, Version 2.0 (the "License").
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and limitations under the License.
 *
 */



DROP TABLE IF EXISTS t_sys_user;

CREATE TABLE t_sys_user
(
    id INTEGER PRIMARY KEY IDENTITY(1,1),
    user_name VARCHAR(100) DEFAULT '123456',
    password VARCHAR(100),
    role_id INTEGER,
    create_time DATETIME NOT NULL DEFAULT  GETDATE()
);



drop table if exists sys_role;

CREATE TABLE sys_role
(
    id INTEGER PRIMARY KEY IDENTITY(1,1),
    name VARCHAR(100) not null,
    create_time DATETIME NOT NULL DEFAULT  GETDATE()
);

drop table if exists sys_user_score;

CREATE TABLE sys_user_score
(
    user_id INTEGER PRIMARY KEY,
    score decimal(6, 2)
);

insert into t_sys_user(user_name, password, role_id, create_time)
values ( 'admin', '123', 0, '2023-10-11 15:16:17'),
       ( 'test1', '123456', 1, '2023-10-11 15:16:17'),
       ( 'test2', null, 1, '2023-10-12 15:16:17');

insert into sys_role( name, create_time)
values ('测试', '2022-10-10'),
       ('运维', '2022-10-10');


insert into sys_user_score
values (2, 3.2),
       (3, 2.6);

drop table if exists id_test;

CREATE TABLE id_test
(
    id BIGINT PRIMARY KEY IDENTITY(1,1),
    create_time DATETIME NOT NULL DEFAULT  GETDATE()
);

drop table if exists uuid_test;

CREATE TABLE uuid_test
(
    id varchar(64) PRIMARY KEY,
    create_time DATETIME NOT NULL DEFAULT  GETDATE()
);

drop table if exists version_test;

CREATE TABLE version_test
(
    id varchar(32) PRIMARY KEY,
    version INT NOT NULL,
    name VARCHAR(100) not null,
    create_time DATETIME NOT NULL DEFAULT  GETDATE()
);

drop table if exists tenant_test;

CREATE TABLE tenant_test
(
    id varchar(32) PRIMARY KEY,
    tenant_id INT NOT NULL,
    name VARCHAR(100) not null,
    create_time DATETIME NOT NULL DEFAULT  GETDATE()
);

drop table if exists logic_delete_test;

CREATE TABLE logic_delete_test
(
    id BIGINT PRIMARY KEY IDENTITY(1,1),
    name VARCHAR(100) not null,
    deleted TINYINT not NULL default 0,
    delete_time DATETIME
);

insert into logic_delete_test(name, deleted, delete_time)
values ( '测试', 0, null),
       ( '运维', 0, null),
       ( '运维2', 0, null);

drop table if exists default_value_test;

CREATE TABLE default_value_test
(
    id INT PRIMARY KEY IDENTITY(1,1),
    value1 VARCHAR(100) not null,
    value2 INT not NULL ,
    value3 VARCHAR(100),
    value4 INT ,
    create_time DATETIME NOT NULL
);

drop table if exists composite_test;

CREATE TABLE composite_test
(
    id BIGINT PRIMARY KEY IDENTITY(1,1),
    version int not null,
    tenant_id int not null,
    deleted TINYINT not NULL default 0,
    delete_time DATETIME
);

drop table if exists nested_first;

CREATE TABLE nested_first
(
    id INT PRIMARY KEY,
    th_name VARCHAR(100) not null
);

drop table if exists nested_second;

CREATE TABLE nested_second
(
    id INT PRIMARY KEY,
    nested_one_id INT NOT NULL,
    th_name VARCHAR(100) not null
);

drop table if exists nested_third;

CREATE TABLE nested_third
(
    id INT PRIMARY KEY,
    nested_second_id INT NOT NULL,
    th_name VARCHAR(100) not null
);

insert into nested_first(id,th_name)
values
    (1,'嵌套A'),
    (2,'嵌套B');

insert into nested_second(id,nested_one_id,th_name)
values
    (1,1,'嵌套AA'),
    (2,2,'嵌套BA');

insert into nested_third(id,nested_second_id,th_name)
values
    (1,1,'嵌套AAA'),
    (2,2,'嵌套BAA');



drop table if exists nested_muti_first;

CREATE TABLE nested_muti_first
(
    id INT PRIMARY KEY,
    th_name VARCHAR(100) not null
);

drop table if exists nested_muti_second;

CREATE TABLE nested_muti_second
(
    id INT PRIMARY KEY,
    nested_one_id INT NOT NULL,
    th_name VARCHAR(100) not null
);

drop table if exists nested_muti_third;

CREATE TABLE nested_muti_third
(
    id INT PRIMARY KEY,
    nested_second_id INT NOT NULL,
    th_name VARCHAR(100) not null
);

insert into nested_muti_first(id,th_name)
values
    (1,'嵌套A'),
    (2,'嵌套B');

insert into nested_muti_second(id,nested_one_id,th_name)
values
    (1,1,'嵌套AA'),
    (2,1,'嵌套AB'),
    (3,2,'嵌套BA');

insert into nested_muti_third(id,nested_second_id,th_name)
values
    (1,2,'嵌套BAA'),
    (2,2,'嵌套BAB');

drop table if exists multi_pk;

CREATE TABLE multi_pk
(
    id1 INT NOT NULL,
    id2 INT NOT NULL,
    name VARCHAR(100) not null,
    PRIMARY KEY(id1,id2)
);