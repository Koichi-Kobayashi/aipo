--
-- Aipo is a groupware program developed by TOWN, Inc.
-- Copyright (C) 2004-2015 TOWN, Inc.
-- http://www.aipo.com
--
-- This program is free software: you can redistribute it and/or modify
-- it under the terms of the GNU Affero General Public License as
-- published by the Free Software Foundation, either version 3 of the
-- License, or (at your option) any later version.
--
-- This program is distributed in the hope that it will be useful,
-- but WITHOUT ANY WARRANTY; without even the implied warranty of
-- MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
-- GNU Affero General Public License for more details.
--
-- You should have received a copy of the GNU Affero General Public License
-- along with this program.  If not, see <http://www.gnu.org/licenses/>.
--

-- 20160328
ALTER TABLE TURBINE_USER ADD CODE VARCHAR(255) DEFAULT NULL;

ALTER TABLE EIP_T_MESSAGE_ROOM_MEMBER ADD DESKTOP_NOTIFICATION VARCHAR(1) DEFAULT 'A';
ALTER TABLE EIP_T_MESSAGE_ROOM_MEMBER ADD MOBILE_NOTIFICATION VARCHAR(1) DEFAULT 'A';

ALTER TABLE EIP_T_MESSAGE_ROOM_MEMBER ADD HISTORY_LAST_MESSAGE_ID INTEGER NOT NULL DEFAULT 0;

ALTER TABLE EIP_T_EXT_TIMECARD_SYSTEM ADD OVERTIME_TYPE VARCHAR(8) DEFAULT 'O';
UPDATE EIP_T_EXT_TIMECARD_SYSTEM SET OVERTIME_TYPE = 'O';

ALTER TABLE EIP_T_EXT_TIMECARD_SYSTEM ADD HOLIDAY_OF_WEEK VARCHAR(32) DEFAULT 'A';
-- 20160328

-- 20160815
UPDATE EIP_T_ACL_PORTLET_FEATURE SET ACL_TYPE = 27 WHERE FEATURE_NAME = 'report_other' AND FEATURE_ALIAS_NAME = '報告書（他ユーザーの報告書）操作';
UPDATE EIP_T_ACL_ROLE SET ACL_TYPE = 27 WHERE FEATURE_ID IN (SELECT FEATURE_ID FROM EIP_T_ACL_PORTLET_FEATURE WHERE FEATURE_NAME = 'report_other') AND ROLE_NAME = '報告書（他ユーザーの報告書）管理者';
-- 20160815

-- 20170105
-- timeline
INSERT INTO EIP_T_ACL_PORTLET_FEATURE VALUES(NEXTVAL('pk_eip_t_acl_portlet_feature'),'timeline_post','タイムライン（自分の投稿）操作',21);
INSERT INTO EIP_T_ACL_PORTLET_FEATURE VALUES(NEXTVAL('pk_eip_t_acl_portlet_feature'),'timeline_post_other','タイムライン（他ユーザーの投稿）操作',17);
INSERT INTO EIP_T_ACL_PORTLET_FEATURE VALUES(NEXTVAL('pk_eip_t_acl_portlet_feature'),'timeline_comment','タイムライン（コメント）操作',20);
INSERT INTO EIP_T_ACL_ROLE VALUES(NEXTVAL('pk_eip_t_acl_role'), 'タイムライン（自分の投稿）管理者',(SELECT FEATURE_ID from EIP_T_ACL_PORTLET_FEATURE WHERE FEATURE_NAME = 'timeline_post' LIMIT 1),21,'＊追加、削除は一覧表示の権限を持っていないと使用できません');
INSERT INTO EIP_T_ACL_ROLE VALUES(NEXTVAL('pk_eip_t_acl_role'),'タイムライン（他ユーザーの投稿）管理者',(SELECT FEATURE_ID from EIP_T_ACL_PORTLET_FEATURE WHERE FEATURE_NAME = 'timeline_post_other' LIMIT 1),1,NULL);
INSERT INTO EIP_T_ACL_ROLE VALUES(NEXTVAL('pk_eip_t_acl_role'),'タイムライン（コメント）管理者',(SELECT FEATURE_ID from EIP_T_ACL_PORTLET_FEATURE WHERE FEATURE_NAME = 'timeline_comment' LIMIT 1),20,NULL);
-- migration
INSERT INTO EIP_T_ACL_USER_ROLE_MAP(id,user_id,role_id) SELECT NEXTVAL('pk_eip_t_acl_user_role_map'),user_id,(SELECT role_id FROM EIP_T_ACL_ROLE WHERE ROLE_NAME = 'タイムライン（自分の投稿）管理者' limit 1) FROM TURBINE_USER WHERE disabled!='T' and not (login_name='admin' or login_name='anon' or login_name='template');
INSERT INTO EIP_T_ACL_USER_ROLE_MAP(id,user_id,role_id) SELECT NEXTVAL('pk_eip_t_acl_user_role_map'),user_id,(SELECT role_id FROM EIP_T_ACL_ROLE WHERE ROLE_NAME = 'タイムライン（他ユーザーの投稿）管理者' limit 1) FROM TURBINE_USER WHERE disabled!='T' and not (login_name='admin' or login_name='anon' or login_name='template');
INSERT INTO EIP_T_ACL_USER_ROLE_MAP(id,user_id,role_id) SELECT NEXTVAL('pk_eip_t_acl_user_role_map'),user_id,(SELECT role_id FROM EIP_T_ACL_ROLE WHERE ROLE_NAME = 'タイムライン（コメント）管理者' limit 1) FROM TURBINE_USER WHERE disabled!='T' and not (login_name='admin' or login_name='anon' or login_name='template');
SELECT setval('pk_eip_t_acl_portlet_feature', (SELECT MAX(FEATURE_ID) FROM EIP_T_ACL_PORTLET_FEATURE));
SELECT setval('pk_eip_t_acl_role', (SELECT MAX(ROLE_ID) FROM EIP_T_ACL_ROLE));
SELECT setval('pk_eip_t_acl_user_role_map', (SELECT MAX(ID) FROM EIP_T_ACL_USER_ROLE_MAP));
-- 20170105

-- 20170118
INSERT INTO EIP_T_ACL_PORTLET_FEATURE VALUES(NEXTVAL('pk_eip_t_acl_portlet_feature'),'attachment','添付ファイル操作',52);
INSERT INTO EIP_T_ACL_ROLE VALUES(NEXTVAL('pk_eip_t_acl_role'), '添付ファイル操作管理者',(SELECT FEATURE_ID from EIP_T_ACL_PORTLET_FEATURE WHERE FEATURE_NAME = 'attachment' LIMIT 1),52,NULL,NULL,NULL);
INSERT INTO EIP_T_ACL_USER_ROLE_MAP(id,user_id,role_id) SELECT nextval('pk_eip_t_acl_user_role_map'),user_id,(SELECT role_id from EIP_T_ACL_ROLE WHERE ROLE_NAME = '添付ファイル操作管理者' limit 1) FROM TURBINE_USER WHERE disabled!='T' and not (login_name='admin' or login_name='anon');
SELECT setval('pk_eip_t_acl_portlet_feature', (SELECT MAX(FEATURE_ID) FROM EIP_T_ACL_PORTLET_FEATURE));
SELECT setval('pk_eip_t_acl_role', (SELECT MAX(ROLE_ID) FROM EIP_T_ACL_ROLE));
SELECT setval('pk_eip_t_acl_user_role_map', (SELECT MAX(ID) FROM EIP_T_ACL_USER_ROLE_MAP));
-- 20170118

-- 20170123
-- timeline
ALTER TABLE EIP_T_TIMELINE ADD PINNED VARCHAR(1) DEFAULT 'F';
INSERT INTO EIP_T_ACL_PORTLET_FEATURE VALUES(NEXTVAL('pk_eip_t_acl_portlet_feature'),'timeline_pin','タイムライン（固定化）操作',8);
INSERT INTO EIP_T_ACL_ROLE VALUES(NEXTVAL('pk_eip_t_acl_role'), 'タイムライン（固定化）管理者',(SELECT FEATURE_ID from EIP_T_ACL_PORTLET_FEATURE WHERE FEATURE_NAME = 'timeline_pin' LIMIT 1),8,NULL);
-- migration
INSERT INTO EIP_T_ACL_USER_ROLE_MAP(id,user_id,role_id) SELECT NEXTVAL('pk_eip_t_acl_user_role_map'),user_id,(SELECT role_id from EIP_T_ACL_ROLE WHERE ROLE_NAME = 'タイムライン（固定化）管理者' limit 1) FROM TURBINE_USER WHERE disabled!='T' and not (login_name='admin' or login_name='anon' or login_name='template');
UPDATE EIP_T_TIMELINE SET PINNED ='F';
SELECT setval('pk_eip_t_acl_portlet_feature', (SELECT MAX(FEATURE_ID) FROM EIP_T_ACL_PORTLET_FEATURE));
SELECT setval('pk_eip_t_acl_role', (SELECT MAX(ROLE_ID) FROM EIP_T_ACL_ROLE));
SELECT setval('pk_eip_t_acl_user_role_map', (SELECT MAX(ID) FROM EIP_T_ACL_USER_ROLE_MAP));
-- 20170123

-- 20170425
UPDATE EIP_T_ACL_PORTLET_FEATURE SET ACL_TYPE = 63 WHERE FEATURE_NAME = 'schedule_self' AND FEATURE_ALIAS_NAME = 'スケジュール（自分の予定）操作';
UPDATE EIP_T_ACL_PORTLET_FEATURE SET ACL_TYPE = 63 WHERE FEATURE_NAME = 'schedule_other' AND FEATURE_ALIAS_NAME = 'スケジュール（他ユーザーの予定）操作';
UPDATE EIP_T_ACL_PORTLET_FEATURE SET ACL_TYPE = 44 WHERE FEATURE_NAME = 'schedule_facility' AND FEATURE_ALIAS_NAME = 'スケジュール（設備の予約）操作';
UPDATE EIP_T_ACL_ROLE SET ACL_TYPE = 63, NOTE = '＊追加、編集、削除、外部入力は一覧表示の権限を持っていないと使用できません' WHERE FEATURE_ID IN (SELECT FEATURE_ID FROM EIP_T_ACL_PORTLET_FEATURE WHERE FEATURE_NAME = 'schedule_self') AND ROLE_NAME = 'スケジュール（自分の予定）管理者';
UPDATE EIP_T_ACL_ROLE SET ROLE_NAME = 'スケジュール（他ユーザーの予定）管理者', NOTE = '＊追加、編集、削除、外部入力は一覧表示の権限を持っていないと使用できません' WHERE FEATURE_ID IN (SELECT FEATURE_ID FROM EIP_T_ACL_PORTLET_FEATURE WHERE FEATURE_NAME = 'schedule_other') AND ROLE_NAME = 'スケジュール（他ユーザーの予定）';
UPDATE EIP_T_ACL_ROLE SET ACL_TYPE = 44 WHERE FEATURE_ID IN (SELECT FEATURE_ID FROM EIP_T_ACL_PORTLET_FEATURE WHERE FEATURE_NAME = 'schedule_facility') AND ROLE_NAME = 'スケジュール（設備の予約）管理者';
-- 20170425

-- 20170706
CREATE INDEX eip_t_message_read_index3 ON EIP_T_MESSAGE_READ (ROOM_ID, MESSAGE_ID, USER_ID, IS_READ);
-- 20170706

-- 20170825
ALTER TABLE EIP_T_EXT_TIMECARD_SYSTEM ADD RESTTIME_START_HOUR INTEGER DEFAULT 12;
ALTER TABLE EIP_T_EXT_TIMECARD_SYSTEM ADD RESTTIME_START_MINUTE INTEGER DEFAULT 0;
ALTER TABLE EIP_T_EXT_TIMECARD_SYSTEM ADD RESTTIME_END_HOUR INTEGER DEFAULT 13;
ALTER TABLE EIP_T_EXT_TIMECARD_SYSTEM ADD RESTTIME_END_MINUTE INTEGER DEFAULT 0;
ALTER TABLE EIP_T_EXT_TIMECARD_SYSTEM ADD RESTTIME_TYPE VARCHAR (1) DEFAULT 'I';
ALTER TABLE EIP_T_EXT_TIMECARD_SYSTEM ADD CONSIDERED_OVERTIME_FLAG VARCHAR(1) DEFAULT 'F';
ALTER TABLE EIP_T_EXT_TIMECARD_SYSTEM ADD CONSIDERED_OVERTIME INTEGER DEFAULT 0;
UPDATE EIP_T_EXT_TIMECARD_SYSTEM SET RESTTIME_START_HOUR =12;
UPDATE EIP_T_EXT_TIMECARD_SYSTEM SET RESTTIME_START_MINUTE =0;
UPDATE EIP_T_EXT_TIMECARD_SYSTEM SET RESTTIME_END_HOUR =13;
UPDATE EIP_T_EXT_TIMECARD_SYSTEM SET RESTTIME_END_MINUTE =0;
UPDATE EIP_T_EXT_TIMECARD_SYSTEM SET CONSIDERED_OVERTIME = 0;
-- 20170825

-- 20171006
ALTER TABLE EIP_T_EXT_TIMECARD_SYSTEM ADD MORNING_OFF INTEGER DEFAULT 240;
ALTER TABLE EIP_T_EXT_TIMECARD_SYSTEM ADD AFTERNOON_OFF INTEGER DEFAULT 240;
-- 20171006