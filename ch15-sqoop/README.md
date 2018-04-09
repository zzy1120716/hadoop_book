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
```

#### 添加MySQL Java Connector
```
cp ~/Downloads/mysql-connector-java-5.1.46/mysql-connector-java-5.1.46.jar $SQOOP_HOME/lib
```

#### MySQL导入示例
1.创建一个新的MySQL数据库模式
```
mysql -u root -p
Enter password:password
CREATE DATABASE hadoopguide;
GRANT ALL PRIVILEGES ON hadoopguide.* TO '%'@'localhost';
GRANT ALL PRIVILEGES ON hadoopguide.* TO 'zzy'@'localhost' IDENTIFIED BY 'password';
quit;
```

2.创建一个被导入HDFS的表
```
mysql hadoopguide -uzzy -p
Enter password:password
```

```
CREATE TABLE widgets(id INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
widget_name VARCHAR(64) NOT NULL,
price DECIMAL(10,2),
design_date DATE,
version INT,
design_comment VARCHAR(100));

INSERT INTO widgets VALUES (NULL, 'sprocket', 0.25, '2010-02-10',
1, 'Connects two gizmos');

INSERT INTO widgets VALUES (NULL, 'gizmo', 4.00, '2009-11-30',
4, NULL);

INSERT INTO widgets VALUES (NULL, 'gadget', 99.99, '1983-08-13',
13, 'Our flagship product');

quit;
```

3.将表导入HDFS
```
start-dfs.sh && start-yarn.sh
sqoop import --connect jdbc:mysql://localhost/hadoopguide --username zzy \
--password password --table widgets -m 1
```

```
sqoop-list-databases --connect jdbc:mysql://localhost/ --username root --password password
sqoop-list-tables --connect jdbc:mysql://localhost/hadoopguide --username zzy \
--password password
```

4.查看文件内容
```
hadoop fs -cat widgets/part-m-00000

1,sprocket,0.25,2010-02-10,1,Connects two gizmos
2,gizmo,4.00,2009-11-30,4,null
3,gadget,99.99,1983-08-13,13,Our flagship product
```

5.查看生成的widgets.java代码
```
cat widgets.java
```

6.指定生成一个名为Widget的类
```
cd src/main/java 
```

```
sqoop codegen --connect jdbc:mysql://localhost/hadoopguide \
--username zzy --password password --table widgets --class-name Widget
```

#### MaxWidgetId (找出具有最大Id的部件)
```
HADOOP_CLASSPATH=$SQOOP_HOME/sqoop-1.4.7.jar hadoop jar \
sqoop-examples.jar MaxWidgetId -libjars $SQOOP_HOME/sqoop-1.4.7.jar
```

```
hadoop fs -cat maxwidget/part-r-00000
```

#### MaxWidgetIdGenericAvro
```
hadoop fs -put widgets/part-m-00000.avro widgets

HADOOP_CLASSPATH=/Users/zzy/avro/avro-mapred-1.8.2-hadoop2.jar:$SQOOP_HOME/sqoop-1.4.7.jar hadoop jar \
sqoop-examples.jar MaxWidgetIdGenericAvro -libjars $SQOOP_HOME/sqoop-1.4.7.jar
```

#### 导入的数据与hive
1.安装和配置hive(https://blog.csdn.net/wk51920/article/details/51731133)
```
tar -zxf apache-hive-2.3.2-bin.tar.gz
ln -s apache-hive-2.3.2-bin hive
vi ~/.zshrc

export HIVE_HOME=/Users/zzy/hive

export PATH=$PATH:$HIVE_HOME/bin

source ~/.zshrc

vi /Users/zzy/hive/conf/hive-site.xml
```
添加：
```
<?xml version="1.0" encoding="UTF-8"?>
<?xml-stylesheet type="text/xsl" href="configuration.xsl"?>

<configuration>
    <property>
        <name>javax.jdo.option.ConnectionURL</name>
        <value>jdbc:mysql://localhost:3306/hive?createDatabaseIfNotExist=true</value>
    </property>

    <property>
        <name>javax.jdo.option.ConnectionDriverName</name>
        <value>com.mysql.jdbc.Driver</value>
    </property>

    <property>
        <name>javax.jdo.option.ConnectionUserName</name>
        <value>hive</value>
    </property>

    <property>
        <name>javax.jdo.option.ConnectionPassword</name>
        <value>hive</value>
    </property>
</configuration>
```

```
cd /Users/zzy/hive/conf
cp hive-env.sh.template hive-env.sh
```
修改：
```
HADOOP_HOME=/Users/zzy/hadoop
export HIVE_CONF_DIR=/Users/zzy/hive/conf
```

```
mysql -u root
GRANT ALL PRIVILEGES ON *.* TO 'root'@'%' WITH GRANT OPTION;
GRANT ALL PRIVILEGES ON *.* TO 'zzy'@'%' WITH GRANT OPTION;
GRANT ALL PRIVILEGES ON *.* TO 'hive'@'%' WITH GRANT OPTION;
CREATE USER 'hive'@'%' IDENTIFIED BY 'hive';
CREATE USER 'hive'@'localhost' IDENTIFIED BY 'hive';
GRANT ALL ON *.* TO 'hive'@'%' IDENTIFIED BY 'hive';
GRANT ALL ON *.* TO 'hive'@'localhost' IDENTIFIED BY 'hive';
FLUSH PRIVILEGES;
```

```
  hadoop fs -mkdir       /tmp
  hadoop fs -mkdir       /user/hive/warehouse
  hadoop fs -chmod g+w   /tmp
  hadoop fs -chmod g+w   /user/hive/warehouse

cp /Users/zzy/Downloads/mysql-connector-java-5.1.46/mysql-connector-java-5.1.46-bin.jar /Users/zzy/hive/lib
schematool -dbType mysql -initSchema
hive
```

2.载入销售数据
```
CREATE TABLE sales(widget_id INT, qty INT, 
street STRING, city STRING, state STRING,
zip INT, sale_date STRING)
ROW FORMAT DELIMITED FIELDS TERMINATED BY ',';

LOAD DATA LOCAL INPATH "sales.log" INTO TABLE sales;
```

3.加载保存在HDFS中的数据
```
sqoop create-hive-table --connect jdbc:mysql://localhost/hadoopguide \
--username root --password password --verbose --table widgets \
--fields-terminated-by '\t'

hive

LOAD DATA INPATH "widgets" INTO TABLE widgets;
```

注意：Caused by: java.lang.ClassNotFoundException: org.apache.hadoop.hive.conf.HiveConf
错误解决
```
vi ~/.zshrc
export HADOOP_CLASSPATH=$HADOOP_CLASSPATH:$HIVE_HOME/lib/*
source ~/.zshrc
```
java.lang.NoClassDefFoundError: Could not initialize class org.apache.derby.jdbc.AutoloadedDriver40
错误解决
>将hive的版本从2.3.2换成1.2.2

4.直接创建hive表
```
sqoop import --connect jdbc:mysql://localhost/hadoopguide \
--username root --password password --table widgets \
-m 1 --hive-import --fields-terminated-by ,
```

```
sqoop import --connect jdbc:mysql://localhost/hadoopguide \
--username root --password password --table widgets --fields-terminated-by "\t" --lines-terminated-by "\n" \
--hive-import --hive-overwrite \
--hive-table widgets -m 1
```

此处遇到错误，sqoop导入表不能在hive中显示，故手动创建：
```
CREATE TABLE IF NOT EXISTS `widgets` ( `id` INT, `widget_name` STRING, `price` DOUBLE, `design_date` STRING, `version` INT, `design_comment` STRING) 
COMMENT 'Imported by sqoop on 2018/04/03 19:49:08' 
ROW FORMAT DELIMITED FIELDS TERMINATED BY '\t' LINES TERMINATED BY '\n'
STORED AS TEXTFILE;
```

5.计算最赚钱的邮政编码地区
```
hive

CREATE TABLE zip_profits
AS
SELECT SUM(w.price * s.qty) AS sales_vol, s.zip FROM SALES s
JOIN widgets w ON (s.widget_id = w.id) GROUP BY s.zip;

SELECT * FROM zip_profits ORDER BY sales_vol DESC;
```

#### MySQL导出示例
1.创建相同列顺序与合适SQL类型的MySQL目标表
```
mysql -uroot -p

use hadoopguide;

CREATE TABLE sales_by_zip (volume DECIMAL(8,2), zip INTEGER);
```

2.运行导出命令
```
sqoop export --connect jdbc:mysql://localhost/hadoopguide \
--username root --password password -m 1 \
--table sales_by_zip --export-dir /user/hive/warehouse/zip_profits \
--input-fields-terminated-by '\0001'
```

3.检查mysql
```
mysql hadoopguide -e 'SELECT * FROM sales_by_zip' -uroot -p
```

#### 导入widgets表到SequenceFile中，再将其导出到另外一个数据库表
1.导入SequenceFile
```
sqoop import --connect jdbc:mysql://localhost/hadoopguide \
--username root --password password \
--table widgets -m 1 --class-name WidgetHolder \
--as-sequencefile \
--target-dir widget_sequence_files --bindir .
```

2.创建mysql表
```
mysql hadoopguide -uroot -p

CREATE TABLE widgets2(id INT, widet_name VARCHAR(100),
price DOUBLE, designed DATE, version INT, notes VARCHAR(200));
```

3.导出到widget2
```
sqoop export --connect jdbc:mysql://localhost/hadoopguide \
--username root --password password \
--table widgets2 -m 1 --class-name WidgetHolder \
--jar-file WidgetHolder.jar --export-dir widget_sequence_files
```

4.检查mysql
```
mysql hadoopguide -e 'SELECT * FROM widgets2' -uroot -p
```