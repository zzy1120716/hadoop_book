```
cd
mkdir azkaban
tar -zxvf azkaban-web-server-2.5.0.tar.gz -C azkaban
tar -zxvf azkaban-executor-server-2.5.0.tar.gz -C azkaban
tar -zxvf azkaban-sql-script-2.5.0.tar.gz -C azkaban
mv azkaban-web-2.5.0 server
mv azkaban-executor-2.5.0 executor
```

```
mysql -uroot -proot

mysql> create database azkaban;
mysql> use azkaban;
mysql> source /home/hadoop/azkaban/azkaban-2.5.0/create-all-sql-2.5.0.sql;
```

创建SSL配置：
```
keytool -keystore keystore -alias jetty -genkey -keyalg RSA
密钥：123456
cp keystore server/
```

配置时区（在所有节点）：
```
su
tzselect

sudo cp /usr/share/zoneinfo/Asia/Shanghai /etc/localtime
```

统一时间：
```
sudo date -s '2018-06-25 17:50:13'
hwclock -w
```

修改server配置文件：
vi azkaban.properties
```
#Azkaban Personalization Settings
azkaban.name=Test                           #服务器UI名称,用于服务器上方显示的名字
azkaban.label=My Local Azkaban                               #描述
azkaban.color=#FF3601                                                 #UI颜色
azkaban.default.servlet.path=/index                         #
web.resource.dir=web/                                                 #默认根web目录
default.timezone.id=Asia/Shanghai                           #默认时区,已改为亚洲/上海 默认为美国
 
#Azkaban UserManager class
user.manager.class=azkaban.user.XmlUserManager   #用户权限管理默认类
user.manager.xml.file=conf/azkaban-users.xml              #用户配置,具体配置参加下文
 
#Loader for projects
executor.global.properties=conf/global.properties    # global配置文件所在位置
azkaban.project.dir=projects                                                #
 
database.type=mysql                                                              #数据库类型
mysql.port=3306                                                                       #端口号
mysql.host=localhost                                                      #数据库连接IP
mysql.database=azkaban                                                       #数据库实例名
mysql.user=root                                                                 #数据库用户名
mysql.password=root                                                          #数据库密码
mysql.numconnections=100                                                  #最大连接数
 
# Velocity dev mode
velocity.dev.mode=false
# Jetty服务器属性.
jetty.maxThreads=25                                                               #最大线程数
jetty.ssl.port=8443                                                                   #Jetty SSL端口
jetty.port=8081                                                                         #Jetty端口
jetty.keystore=keystore                                                          #SSL文件名
jetty.password=123456                                                             #SSL文件密码
jetty.keypassword=123456                                                      #Jetty主密码 与 keystore文件相同
jetty.truststore=keystore                                                                #SSL文件名
jetty.trustpassword=123456                                                   # SSL文件密码
 
# 执行服务器属性
executor.port=12321                                                               #执行服务器端口
 
# 邮件设置
mail.sender=xxxxxxxx@163.com                                       #发送邮箱
mail.host=smtp.163.com                                                       #发送邮箱smtp地址
mail.user=xxxxxxxx                                       #发送邮件时显示的名称
mail.password=**********                                                 #邮箱密码
job.failure.email=xxxxxxxx@163.com                              #任务失败时发送邮件的地址
job.success.email=xxxxxxxx@163.com                            #任务成功时发送邮件的地址
lockdown.create.projects=false                                           #
cache.directory=cache                                                            #缓存目录
```
vi azkaban-users.xml 
```
<azkaban-users>
        <user username="azkaban" password="azkaban" roles="admin" groups="azkaban" />
        <user username="metrics" password="metrics" roles="metrics"/>
        <user username="admin" password="admin" roles="admin,metrics" />
        <role name="admin" permissions="ADMIN" />
        <role name="metrics" permissions="METRICS"/>
</azkaban-users>
```

修改executor配置文件：
vi azkaban.properties
```
#Azkaban
default.timezone.id=Asia/Shanghai                                              #时区
 
# Azkaban JobTypes 插件配置
azkaban.jobtype.plugin.dir=plugins/jobtypes                   #jobtype 插件所在位置
 
#Loader for projects
executor.global.properties=conf/global.properties
azkaban.project.dir=projects
 
#数据库设置
database.type=mysql                                                                       #数据库类型(目前只支持mysql)
mysql.port=3306                                                                                #数据库端口号
mysql.host=192.168.20.200                                                           #数据库IP地址
mysql.database=azkaban                                                                #数据库实例名
mysql.user=root                                                                       #数据库用户名
mysql.password=root                                  #数据库密码
mysql.numconnections=100                                                           #最大连接数
 
# 执行服务器配置
executor.maxThreads=50                                                                #最大线程数
executor.port=12321                                                               #端口号(如修改,请与web服务中一致)
executor.flow.threads=30                                                                #线程数
```

启动：
```
bin/azkaban-web-start.sh
bin/azkaban-executor-start.sh
```
启动（后台运行）：
```
nohup bin/azkaban-web-start.sh 1>/tmp/azstd.out 2>/tmp/azerr.out &
```

实战：
```
vi command.job
#command.job
type=command
command=echo xxoo
```