# MPSG
A phoneGuard that can search phone and delete some important files

1 查找手机功能
  用户可以通过查找手机功能定位到丢失的手机位置。实现的方式是从服务器上读取保存的GPS信息，进行逆解析。

2 上传手机地理位置
  通过使用高德地图SDK实现 每隔两分钟会自动将手机的GPS信息上传当服务器。

3 手机上锁
  用悬浮窗的方式实现手机上锁。且用户必须在规定时间内输入解锁密码。同时会收集用户的手指按压力度，上传到服务器，服务器根据 密码和按压力度进行验证用户信息

4 收集用户按压力度信息
  用户每次在本应用内进行按压屏幕生成的按压压力数据都会上传到服务器，用户用户身份验证的数据来源。

5 销毁文件、声音提示、震动提示等辅助功能
  尚未开发，这个就是一个辅助的功能，主要就是需要客户端和服务器保持一个长连接。。

6 用户注册、登录
  用户第一次使用此APP必须要进行注册，之后的再次登录需要输入用户账号密码。且注册必须要邮箱注册，但是没有实现邮箱的验证注册，不支持找回密码的操作。

