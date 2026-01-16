DROP
SEQUENCE t_sys_user_seq;

CREATE
SEQUENCE t_sys_user_seq;

DROP TABLE t_sys_user;

CREATE TABLE t_sys_user
(
    id          INTEGER PRIMARY KEY,
    user_name   VARCHAR(100) DEFAULT '123456',
    password    VARCHAR(100),
    role_id     INTEGER,
    create_time TIMESTAMP    DEFAULT SYSDATE NOT NULL
);

drop table if exists sys_role_middle;

CREATE TABLE IF NOT EXISTS sys_role_middle
(
    user_id INTEGER not null,
    role_id INTEGER not null
);

INSERT INTO sys_role_middle
VALUES (1, 1),
       (1, 2),
       (2, 2),
       (2, 3);

DROP
SEQUENCE sys_role_seq;

CREATE
SEQUENCE sys_role_seq;

DROP TABLE t_sys_role;

CREATE TABLE t_sys_role
(
    id          INTEGER PRIMARY KEY,
    name        VARCHAR(100)              not null,
    create_time TIMESTAMP DEFAULT SYSDATE NOT NULL
);

DROP TABLE sys_user_score;

CREATE TABLE sys_user_score
(
    user_id INTEGER PRIMARY KEY,
    score   decimal(6, 2)
);

insert all
    into t_sys_user
values (t_sys_user_seq.NEXTVAL, 'admin', '123', 0, TO_DATE('2023-10-11 15:16:17', 'YYYY-MM-DD HH24:MI:SS'))
into t_sys_user
values (t_sys_user_seq.NEXTVAL, 'test1', '123456', 1, TO_DATE('2023-10-11 15:16:17', 'YYYY-MM-DD HH24:MI:SS'))
into t_sys_user
values (t_sys_user_seq.NEXTVAL, 'test2', null, 1, TO_DATE('2023-10-12 15:16:17', 'YYYY-MM-DD HH24:MI:SS'))
select *
from dual;



insert all
    into t_sys_role
values (sys_role_seq.NEXTVAL, '测试', TO_DATE('2022-10-10', 'YYYY-MM-DD'))
into t_sys_role
values (sys_role_seq.NEXTVAL, '运维', TO_DATE('2022-10-10', 'YYYY-MM-DD'))
select *
from dual;


insert all
    into sys_user_score
values (2, 3.2)
into sys_user_score
values (3, 2.6)
select *
from dual;

DROP
SEQUENCE id_test_seq;

CREATE
SEQUENCE id_test_seq;

DROP TABLE id_test;

CREATE TABLE id_test
(
    id NUMBER (20) PRIMARY KEY,
    create_time TIMESTAMP DEFAULT SYSDATE NOT NULL
);

DROP TABLE uuid_test;

CREATE TABLE uuid_test
(
    id          varchar(64) PRIMARY KEY,
    create_time TIMESTAMP DEFAULT SYSDATE NOT NULL
);

DROP TABLE version_test;

CREATE TABLE version_test
(
    id          varchar(32) PRIMARY KEY,
    version     INT                       NOT NULL,
    name        VARCHAR(100)              not null,
    create_time TIMESTAMP DEFAULT SYSDATE NOT NULL
);

DROP TABLE tenant_test;

CREATE TABLE tenant_test
(
    id          varchar(32) PRIMARY KEY,
    tenant_id   INT                       NOT NULL,
    name        VARCHAR(100)              not null,
    create_time TIMESTAMP DEFAULT SYSDATE NOT NULL
);

DROP
SEQUENCE logic_delete_test_seq;

CREATE
SEQUENCE logic_delete_test_seq;

DROP TABLE logic_delete_test;

CREATE TABLE logic_delete_test
(
    id NUMBER (20) PRIMARY KEY,
    name        VARCHAR(100) not null,
    deleted NUMBER (3) default 0 not NULL,
    delete_time TIMESTAMP
);

insert all
    into logic_delete_test
values (logic_delete_test_seq.NEXTVAL, '测试', 0, null)
into logic_delete_test
values (logic_delete_test_seq.NEXTVAL, '运维', 0, null)
into logic_delete_test
values (logic_delete_test_seq.NEXTVAL, '运维2', 0, null)
select *
from dual;

DROP
SEQUENCE default_value_test_seq;

CREATE
SEQUENCE default_value_test_seq;

DROP TABLE default_value_test;

CREATE TABLE default_value_test
(
    id          INT PRIMARY KEY,
    value1      VARCHAR(100),
    value2      INT       not NULL,
    value3      VARCHAR(100),
    value4      INT,
    create_time TIMESTAMP NOT NULL
);

DROP
SEQUENCE composite_test_seq;

CREATE
SEQUENCE composite_test_seq;

DROP TABLE composite_test;

CREATE TABLE composite_test
(
    id NUMBER (20) PRIMARY KEY,
    name        varchar(100),
    version     int not null,
    tenant_id   int not null,
    deleted NUMBER (3) default 0 not NULL,
    delete_time TIMESTAMP
);

DROP TABLE nested_first;

CREATE TABLE nested_first
(
    id      INT PRIMARY KEY,
    th_name VARCHAR(100) not null
);

DROP TABLE nested_second;

CREATE TABLE nested_second
(
    id            INT PRIMARY KEY,
    nested_one_id INT          NOT NULL,
    th_name       VARCHAR(100) not null
);

DROP TABLE nested_third;

CREATE TABLE nested_third
(
    id               INT PRIMARY KEY,
    nested_second_id INT          NOT NULL,
    th_name          VARCHAR(100) not null
);

insert all
    into nested_first(id, th_name)
values (1, '嵌套A')
into nested_first(id, th_name)
values (2, '嵌套B')
select *
from dual;

insert all
    into nested_second(id, nested_one_id, th_name)
values (1, 1, '嵌套AA')
into nested_second(id, nested_one_id, th_name)
values (2, 2, '嵌套BA')
select *
from dual;

insert all
    into nested_third(id, nested_second_id, th_name)
values (1, 1, '嵌套AAA')
into nested_third(id, nested_second_id, th_name)
values (2, 2, '嵌套BAA')
select *
from dual;

DROP TABLE nested_muti_first;

CREATE TABLE nested_muti_first
(
    id      INT PRIMARY KEY,
    th_name VARCHAR(100) not null
);

DROP TABLE nested_muti_second;

CREATE TABLE nested_muti_second
(
    id            INT PRIMARY KEY,
    nested_one_id INT          NOT NULL,
    th_name       VARCHAR(100) not null
);

DROP TABLE nested_muti_third;

CREATE TABLE nested_muti_third
(
    id               INT PRIMARY KEY,
    nested_second_id INT          NOT NULL,
    th_name          VARCHAR(100) not null
);

insert all
    into nested_muti_first(id, th_name)
values (1, '嵌套A')
into nested_muti_first(id, th_name)
values (2, '嵌套B')
select *
from dual;

insert all
    into nested_muti_second(id, nested_one_id, th_name)
values (1, 1, '嵌套AA')
into nested_muti_second(id, nested_one_id, th_name)
values (2, 1, '嵌套AB')
into nested_muti_second(id, nested_one_id, th_name)
values (3, 2, '嵌套BA')
select *
from dual;

insert all
    into nested_muti_third(id, nested_second_id, th_name)
values (1, 2, '嵌套BAA')
into nested_muti_third(id, nested_second_id, th_name)
values (2, 2, '嵌套BAB')
select *
from dual;

DROP TABLE multi_pk;

CREATE TABLE multi_pk
(
    id1  INT          NOT NULL,
    id2  INT          NOT NULL,
    name VARCHAR(100) not null,
    PRIMARY KEY (id1, id2)
);