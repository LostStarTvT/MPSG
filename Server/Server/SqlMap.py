#!/usr/bin/env python 
# -*- coding:utf-8 -*-
# author : Seven time : 2018/11/27
# Describe : 存储用户sql操作


sql_test = "SELECT * FROM user_info"
select_user_by_user_login_id = "select * from user_info where user_login_id = %s "
user_register = "insert into user_info(user_login_id,user_pwd) values (%s,%s) "
user_alter_pwd = "update user_info set user_pwd = %s  where user_login_id = %s"

updateUserLockedPwdData = "update user_info set user_Guard_pwd = %s where user_login_id = %s"
user_look_up_pwd = "select user_pwd from user_info where user_login_id = %s"
insertUserPressure = "insert into user_pressure_store(user_login_id,pressure,fingerID) values (%s,%s,%s)"
updatePhoneId = "update user_info set user_phoneId = %s  where user_login_id =  %s "
update_location = "insert into  loaction_info(user_login_id,longitude,latitude,timeStr) values(%s,%s,%s,%s) "
user_lookup_lasted_location = "select *  from loaction_info where user_login_id = %s order by timeStr desc  limit 1"
user_unlock_phone = " select user_Guard_pwd from user_info where user_login_id = %s "
