#!/usr/bin/env python 
# -*- coding:utf-8 -*-
# author: Seven time: 2018/11/27 
# Describe :  服务器具体得执行类。 这里面的代码相当于和主文件合并成一个文件，所以说
# 下面的代码也会直接的执行，相当于js。

import tornado.web
import pymysql as mysql
import Server.SqlMap as SqlMap
import json

# 打开数据库连接
# 腾讯云服务器mysql
# db = mysql.connect("119.29.185.194", "root", "maps11223344", "mpsg_server")
# 搬瓦工上的mysql服务器
db = mysql.connect("127.0.0.1", "root", "A123456", "mpsg_server")

sql_test = SqlMap.sql_test

# 使用 cursor() 方法获取操作游标
cursor = db.cursor()


# 智能键盘实现获取键盘数据。
class StoreKeyboardPressure(tornado.web.RequestHandler):
    def data_received(self, chunk):
        pass

    def post(self):
        # 获取客户机发送的请求数据。并且转换成 py对象。

        phone_id = self.get_argument("phoneId")
        user_name = self.get_arguments("userName")
        items = json.loads(self.get_argument("data"))
        # print(phone_id)
        # print(user_name)
        print(len(items))
        print(items)
        try:
            # 进行存储用户的数据。
            for itemPr in items:

                base_obj_id = itemPr.get('baseObjId', '')
                time_stamp = itemPr.get('timeStamp', '')
                rTime = itemPr.get('rTime', '')  # 释放时间
                pTime = itemPr.get('pTime', '')  # 按下时间
                letter = itemPr.get('letter', '')
                ppDiffer = itemPr.get('ppDiffer', '')
                cursor.execute(SqlMap.insertKeyBoardUserPressure, (phone_id, user_name, base_obj_id, time_stamp, rTime, pTime, letter, ppDiffer))

                db.commit()  # 插入提交。
            self.write("OK")
        except ConnectionError:
            print("数据库连接失败")
            self.write("连接失败")


# handler类，重定向到index上 可以发送ajax·
class MainHandler(tornado.web.RequestHandler):
    def data_received(self, chunk):
        pass

    def get(self):
        self.render("index.html")


# ajax 执行者
class AjaxHandler(tornado.web.RequestHandler):
    def data_received(self, chunk):
        pass

    def post(self):
        cursor.execute(sql_test)
        results = cursor.fetchall()

        # 现在已经可以获得查询到的数据，之后就是进行相应的操作，
        self.write(self.get_argument("keyword"))


# 用户登录 loginState = 100 表示密码验证失败  200 表示登录成功。 300 表示登录出错。
class UserLogin(tornado.web.RequestHandler):
    def data_received(self, chunk):
        pass

    def post(self):
        # 获取客户机发送的请求数据。并且转换成 py对象。
        params = json.loads(self.get_argument("params"))
        login_id = params.get('loginID', '0000')
        login_pwd = params.get('password', '')
        print(params)
        try:
            cursor.execute(SqlMap.select_user_by_user_login_id, login_id)
            result = cursor.fetchall()

            # 100 表示密码不正确 200 表示密码正确 300 表示账号出错
            login_state = 100
            result_all = {}
            # 如果有结果，就是查阅有此人
            if result:
                # 组合形成字典 类似于 py中只有字典才可以自动的转成json。之后进行数据的传输。
                col_names = [desc[0] for desc in cursor.description]
                col_values = [result_value for result_value in result[0]]
                result_all = dict(zip(col_names, col_values))
                # 删除这个注册时间。是一个日期时间对象,不是简单的字符类型，只能删掉。datetime.datetime(2018, 8, 8, 16, 42, 13)
                result_all.pop('user_register_time')
                # 获取到密码，
                server_pwd = result[0][5]
                print(result)
                # 验证密码是否正确
                if login_pwd == server_pwd:
                    login_state = 200
                # 验证手机ID是否有变,如果有变则进行更新存储。
                phone_id = params.get('userPhoneId', '')
                if phone_id and phone_id != result[0][4]:
                    cursor.execute(SqlMap.updatePhoneId, (phone_id, login_id))
                    db.commit()
            else:
                login_state = 300  # 查无此人

            # 写回登录状态， 只能写会dict。
            self.write({"LoginState": login_state, "data": result_all})
        except ConnectionError:
            print("数据库连接失败")
            # 返回连接失败信息
            self.write({"LoginState": 300})


# 用户注册
class UserRegister(tornado.web.RequestHandler):
    def data_received(self, chunk):
        pass

    def post(self):
        # 获取客户机发送的请求数据。并且转换成 py对象。
        params = json.loads(self.get_argument("params"))

        register_id = params['loginID']
        register_pwd = params['password']
        whether_have_register = False
        # 注册账号标志，300账号已经注册，200 表示注册成功。
        register_state = 300
        try:
            # 查找此用户有没有已经注册
            cursor.execute(SqlMap.select_user_by_user_login_id, register_id)
            select_result = cursor.fetchall()

            if select_result == ():
                cursor.execute(SqlMap.user_register, (register_id, register_pwd))
                db.commit()  # 插入提交。
                insert_result = cursor.fetchall()
                print(insert_result)
                register_state = 200
            self.write({'RegisterState': register_state})
        except ConnectionError:
            print("数据库连接失败")
            self.write("连接失败")


# 存储用户的地址信息
class UserStoreLocation(tornado.web.RequestHandler):
    def data_received(self, chunk):
        pass

    def post(self):
        # 获取客户机发送的请求数据。并且转换成 py对象。
        params = json.loads(self.get_argument("params"))
        user_id = params.get('loginId', '0000')
        latitude_str = params.get('latitudeStr', '')
        longitude_str = params.get('longitudeStr', '')
        time_str = params.get('timeStr', '')

        try:
            # 进行存储位置
            cursor.execute(SqlMap.update_location, (user_id, latitude_str, longitude_str, time_str))
            db.commit()  # 插入提交。
            self.write("OK")
        except ConnectionError:
            print("数据库连接失败")
            self.write("连接失败")


# 用户查找最新的地理位置信息
class UserLookUpLastedLocation(tornado.web.RequestHandler):
    def data_received(self, chunk):
        pass

    def post(self):
        # 获取客户机发送的请求数据。并且转换成 py对象。
        params = json.loads(self.get_argument("params"))
        user_id = params.get('loginId', '0000')
        # print(user_id)
        try:
            # 进行存储位置
            cursor.execute(SqlMap.user_lookup_lasted_location, user_id)
            location_results = cursor.fetchall()
            col_names = [desc[0] for desc in cursor.description]
            col_values = [result_value for result_value in location_results[0]]
            result_all = dict(zip(col_names, col_values))
            print(result_all)
            self.write(result_all)
        except ConnectionError:
            print("数据库连接失败")
            self.write("连接失败")


# 存储用户的压力信息
class UserStorePressure(tornado.web.RequestHandler):
    def data_received(self, chunk):
        pass

    def post(self):
        # 获取客户机发送的请求数据。并且转换成 py对象。
        params = json.loads(self.get_argument("params"))
        user_id = params.get('loginId', '0000')
        pressure = params.get('pressure', '')
        finger_id = params.get('fingerID', '')

        try:
            # 进行存储用户压力
            cursor.execute(SqlMap.insertUserPressure, (user_id, pressure, finger_id))
            db.commit()  # 插入提交。
            self.write("OK")
        except ConnectionError:
            print("数据库连接失败")
            self.write("连接失败")


# 用户更改密码
class UserAlterPwd(tornado.web.RequestHandler):
    def data_received(self, chunk):
        pass

    def post(self):
        # 获取客户机发送的请求数据。并且转换成 py对象。
        params = json.loads(self.get_argument("params"))
        user_id = params.get('loginId', '0000')
        new_pwd = params.get('newPwd', '')
        old_pwd = params.get('oldPwd', '')
        whether_pwd_equal = False
        # print(new_pwd + "  " + old_pwd)
        try:
            # 进行存储用户压力
            cursor.execute(SqlMap.user_look_up_pwd, user_id)
            lookup_result = cursor.fetchall()
            # 查询的如果有结果
            if lookup_result:
                # 获取到查询到的密码
                lookup_pwd = lookup_result[0][0]
                if lookup_result == old_pwd:
                    whether_pwd_equal = True
                    cursor.execute(SqlMap.user_alter_pwd, (new_pwd, user_id))
                    db.commit()  # 插入提交。
            self.write({"AlterState": whether_pwd_equal})
        except ConnectionError:
            print("数据库连接失败")
            self.write("连接失败")


# 用户进行解锁时候的操作
# 1 进行初始化用户解锁密码。设置新的手机锁密码
class UserInitLockPwd(tornado.web.RequestHandler):
    def data_received(self, chunk):
        pass

    def post(self):
        # 获取客户机发送的请求数据。并且转换成 py对象。
        params = json.loads(self.get_argument("params"))
        user_id = params.get('loginId', '0000')
        pwd_data = params.get('pwdData', '')

        try:
            #  更新用户的密码解锁。
            cursor.execute(SqlMap.updateUserLockedPwdData, (pwd_data, user_id))
            db.commit()  # 插入提交。
            self.write("OK")
        except ConnectionError:
            print("数据库连接失败")
            self.write("连接失败")


# 2 用户解锁操作 最重要。  首先查找用户的保存的解锁密码,
class UserUnlockedPhone(tornado.web.RequestHandler):
    def data_received(self, chunk):
        pass

    def post(self):
        # 获取客户机发送的请求数据。并且转换成 py对象。
        params = json.loads(self.get_argument("params"))
        user_id = params.get('loginId', '0000')
        pwd_data = params.get('pwdData', '')
        pressure_data = params.get('pressureData', '')
        authentication_flag = 404
        try:
            # 查询用户解锁密码。
            cursor.execute(SqlMap.user_unlock_phone, user_id)
            result = cursor.fetchall()
            # 获取用户存储的解锁密码
            select_pwd = result[0][0]
            if select_pwd == pwd_data:
                authentication_flag = 200
            print(select_pwd)
            self.write({"authentication_flag": authentication_flag})
        except ConnectionError:
            print("数据库连接失败")
            self.write("连接失败")
