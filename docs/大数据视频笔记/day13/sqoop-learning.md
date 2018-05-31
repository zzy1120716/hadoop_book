## Sqoop作用：数据迁移，Hadoop和关系数据库服务器之间传送数据
### 代替自己写MapReduce程序(Map任务读取数据库)
#### 安装配置sqoop
```
tar xzf sqoop-1.4.7.bin__hadoop-2.6.0.tar.gz
ln -s sqoop-1.4.7.bin__hadoop-2.6.0 sqoop
sudo vi ~/.zshrc
export SQOOP_HOME=/Users/zzy/sqoop
export PATH=$PATH:$SQOOP_HOME/bin
source ~/.zshrc
sqoop-help
```

```
cd /User/zzy/sqoop/conf
cp sqoop-env-template.sh sqoop-env.sh
vi sqoop-env.sh

export HADOOP_COMMON_HOME=/Users/zzy/hadoop
export HADOOP_MAPRED_HOME=/Users/zzy/hadoop
export HIVE_HOME=/Users/zzy/hive
```

#### 添加MySQL Java Connector
```
cp ~/Downloads/mysql-connector-java-5.1.46/mysql-connector-java-5.1.46.jar $SQOOP_HOME/lib
```

#### 测试安装是否成功
```
sqoop help
```

#### MySQL中导入测试数据
```
mysql -uroot -p
>source /home/zzy/mysql-traffic.sql
>use traffic;
>show tables;
```

#### 导入关系表到HDFS
```
sqoop import \
--connect jdbc:mysql://localhost:3306/traffic \
--username root \
--password password \
--table dim_shengfen \
--m 1
```
验证：
```
hadoop fs -cat  /user/zzy/dim_shengfen/part-m-00000
```

#### 导入关系表到Hive
```
hadoop fs -rm -r /user/zzy/dim_shengfen
```
```
sqoop import \
--connect jdbc:mysql://localhost:3306/traffic \
--username root \
--password password \
--table dim_shengfen \
--hive-import \
--m 1
```

##### 报错：Could not load org.apache.hadoop.hive.conf.HiveConf
```
vi ~/.bash_profile
export HADOOP_CLASSPATH=$HADOOP_CLASSPATH:/opt/hive/lib/*
```

##### 报错：hive-site.xml not found on CLASSPATH
##### ERROR tool.ImportTool: Import failed: java.io.IOException: Hive CliDriver exited with status=40000
```
sudo vi ~/.bash_profile

export HADOOP_CLASSPATH=$HADOOP_CLASSPATH:$HIVE_HOME/lib/*:$HIVE_HOME/conf

source ~/.bash_profile
```

#### 导入到HDFS指定目录
```
sqoop import \
--connect jdbc:mysql://localhost:3306/traffic \
--username root \
--password password \
--target-dir /dim \
--table dim_shengfen \
--m 1
```