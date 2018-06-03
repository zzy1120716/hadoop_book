#### ZooKeeper是一个分布式协调服务
只要有半数以上节点存活，zk就能正常服务

##### 功能：
1. 管理（存储、读取）用户程序提交的数据
2. 为用户程序提供数据节点监听服务

#### zookeeper角色分配原理

角色：leader和follower (observer)

##### zookeeper适合装在奇数台机器上！

#### zookeeper集群搭建
安装：
```
yum install -y lrzsz
mkdir apps
tar -zxvf zookeeper-x.x.x.tar.gz
cd zookeeper-x.x.x
rm -rf *.txt src dist-maven
cd conf
cp zoo_sample.cfg zoo.cfg
vi zoo.cfg
```

修改配置：
```
dataDir=/root/zkdata
server.1=mini1:2888:3888
server.2=mini2:2888:3888
server.3=mini3:2888:3888
```

配置myid：
```
mkdir /root/zkdata
cd /root/zkdata
echo 1 > myid
```

分配到各个主机：
```
scp -r apps/ mini2:/root
mkdir zkdata
echo 2 > zkdata/myid

scp -r apps/ mini3:/root
mkdir zkdata
echo 3 > zkdata/myid
```

关闭所有主机的防火墙：
```
SecureCRT软件：View -> Chat Window -> 右键 -> Send Chat to All Sessions

service iptables stop
systemctl stop firewalld.service
```

启动：
```
cd /root/apps/zookeeper-x.x.x
bin/zkServer.sh start
```

查看角色：
```
bin/zkServer.sh status
```

#### zookeeper命令行客户端
```
bin/zkServer.sh start
bin/zkCli.sh

help
connect mini2:2181
ls /
create /app1 this_is_app1_servers_parent
ls /
create /app1/server01 192.168.32.5,100
ls /app1
get /app1
get /app1/server01
```

#### zookeeper数据结构
树状结构
两种znode类型
ephemeral(断开连接自己删除)
persistent(断开连接不删除)

##### 创建短暂节点
```
create -e /app-ephemeral 888888
ls /
quit

bin/zkCli.sh
ls /
```
退出命令行，再进入，短暂节点已不存在

##### 带序号节点
```
create /test 88
create -s /test/aa 999
create -s /test/bb 999
create -s /test/aa 999
create -s /app1/aa 888
create -e -s /app1/bb 888
```
##### 修改数据(实时刷新，数据一致性)
```
set /app1 uuuuuuuu
get /app1
set /app1 yyyyyyyy
在其他节点get到修改后的结果
```

##### 监听(只生效一次)
```
get /app1 watch
set /app1 hhhhhhhh

create /app1/aaaaa iiii
ls /app1 watch -- 监听子节点变化
```

#### zk集群自动启动脚本
```
ssh root@mini2 /root/apps/zookeeper-x.x.x/bin/zkServer.sh start
```
会启动失败，因为mini2主机无法获取JAVA_HOME变量
```
ssh root@mini2 "source /etc/profile && /root/apps/zookeeper-x.x.x/bin/zkServer.sh start"
```
##### 执行脚本启动集群：
```
chmod +x startzk.sh
mkdir /root/bin
mv startzk.sh /root/bin
```
PATH中默认含有/root/bin，即可直接通过`startzk.sh`启动zk集群

#### zookeeper的Java客户端API
##### hadoop_book/ch21-zk/src/main/cn/itcast/bigdata/zk
##### 注意：
```
bin/zkCli.sh
create -e /bbb 999
create /bbb/ccc 888 -- 无法给临时节点创建子节点
```
#### zookeeper应用实战：分布式系统动态感知
```
create /servers iii
```
业务逻辑：
1、客户端main方法一启动，创建一个客户端连接，获取服务器列表，业务线程开始待命，一旦有事件发生，就会收到监听器的通知，process方法被调用，重新获取服务器列表，得到更新的服务器状态。

##### 测试效果
https://blog.csdn.net/xuemengrui12/article/details/74984731
Export -> Runable jar
导出两次，依次为server.jar, client.jar
```
java -jar server.jar mini1
java -jar server.jar mini2
java -jar client.jar
增加一个server，看client能否检测到
java -jar server.jar mini3
```





















