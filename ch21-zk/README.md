##### 参考文章"A Note on Distributed Computing"中对分布式编程的阐述

## 21.1 安装和运行ZooKeeper
```
tar -zxf zookeeper-3.4.6.tar.gz

ln -s zookeeper-3.4.6 zookeeper

vi ~/.zshrc
export ZOOKEEPER_HOME=/Users/zzy/zookeeper
export PATH=$PATH:$ZOOKEEPER_HOME/bin

mkdir zookeeper_data

source ~/.zshrc

vi ~/zookeeper/conf/zoo.cfg
tickTime=2000
dataDir=/Users/zzy/zookeeper_data
clientPort=2181

zkServer.sh start

echo ruok | nc localhost 2181
     conf
     envi
     srvr
     stat
     srst
     isro
     
     dump
     cons
     crst
     
     wchs
     wchc
     wchp
     
     mntr(除外)
```

## 21.2 示例
### 21.2.1 组成员关系
### 21.2.2 创建组
##### CreateGroup
```
mvn clean package
export CLASSPATH=target/classes/:$ZOOKEEPER_HOME/*:\
$ZOOKEEPER_HOME/lib/*:$ZOOKEEPER_HOME/conf
java CreateGroup localhost zoo
```

### 21.2.3 加入组
##### JoinGroup & ConnectionWatcher
```
mvn clean package
export CLASSPATH=target/classes/:$ZOOKEEPER_HOME/*:\
$ZOOKEEPER_HOME/lib/*:$ZOOKEEPER_HOME/conf
java JoinGroup localhost zoo duck &
```

### 21.2.4 列出组成员
##### ListGroup
```
mvn clean package
java ListGroup localhost zoo
java JoinGroup localhost zoo duck &
java JoinGroup localhost zoo cow &
java JoinGroup localhost zoo goat &
goat_pid=$!

java ListGroup localhost zoo

kill $goat_pid

java ListGroup localhost zoo
```
##### ZooKeeper命令行工具
```
zkCli.sh -server localhost

[zk: localhost:2181(CONNECTED) 0] ls /zoo
```

### 21.2.5 删除组
##### DeleteGroup
```
mvn clean package
java DeleteGroup localhost zoo
java ListGroup localhost zoo
```

## 21.3 ZooKeeper服务
### 21.3.1 数据模型
### 21.3.2 操作
### 21.3.3 实现

## 21.4 使用ZooKeeper来构建应用
### 21.4.1 配置服务
##### ConfigUpdater & ActiveKeyValueStore
```
zkServer.sh start

mvn clean package
export CLASSPATH=target/classes/:$ZOOKEEPER_HOME/*:\
$ZOOKEEPER_HOME/lib/*:$ZOOKEEPER_HOME/conf
java ConfigUpdater localhost
```
##### ConfigWatcher
```
export CLASSPATH=target/classes/:$ZOOKEEPER_HOME/*:\
$ZOOKEEPER_HOME/lib/*:$ZOOKEEPER_HOME/conf
java ConfigWatcher localhost
```

### 21.4.2 可复原的ZooKeeper应用
##### ResilientConfigUpdater & ResilientActiveKeyValueStore
```
zkServer.sh start

mvn clean package
export CLASSPATH=target/classes/:$ZOOKEEPER_HOME/*:\
$ZOOKEEPER_HOME/lib/*:$ZOOKEEPER_HOME/conf
java ResilientConfigUpdater localhost
```
##### ConfigWatcher
```
export CLASSPATH=target/classes/:$ZOOKEEPER_HOME/*:\
$ZOOKEEPER_HOME/lib/*:$ZOOKEEPER_HOME/conf
java ConfigWatcher localhost
```
