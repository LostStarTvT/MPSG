#!/usr/bin/env python 
# -*- coding:utf-8 -*-
# author: Seven time: 2018/11/26 
# Describe :  tonardo 服务器入口文件，开启服务器需要运行这个文件

import tornado.web
import tornado.ioloop
import Server.ServerHandler as Sh  # 自定义包 所有的handler都在这里面


# 表示注册路由，  MainHandler 表示相应的执行者。是一个类。
def make_app():
    return tornado.web.Application([
        (r"/", Sh.MainHandler),
        (r"/ajax", Sh.AjaxHandler),
        (r"/login", Sh.UserLogin),

        (r"/users/login", Sh.UserLogin),
        (r"/users/register", Sh.UserRegister),
        (r"/users/user_location_store", Sh.UserStoreLocation),
        (r"/users/user_lookup_lasted_location", Sh.UserLookUpLastedLocation),
        (r"/users/user_pressure_store", Sh.UserStorePressure),
        (r"/users/user_alter_pwd", Sh.UserAlterPwd),
        (r"/users/user_init_locked_pwd", Sh.UserInitLockPwd),
        (r"/users/user_unlock_phone", Sh.UserUnlockedPhone),

    ])


# 主函数，函数的入口
if __name__ == "__main__":
    app = make_app()
    app.listen(80)
    tornado.ioloop.IOLoop.current().start()
