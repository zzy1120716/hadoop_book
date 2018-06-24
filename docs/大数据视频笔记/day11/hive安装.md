```
tar -zxvf apache-hive-1.2.1-bin.tar.gz
mv apache-hive-1.2.1-bin hive
cd hive
vi hive-site.xml
```

```
<configuration>
<property>
<name>javax.jdo.option.ConnectionURL</name>
<value>jdbc:mysql://localhost:3306/hive?createDatabaseIfNotExist=true&amp;useSSL=false</value>
<description>JDBC connect string for a JDBC metastore</description>
</property>

<property>
<name>javax.jdo.option.ConnectionDriverName</name>
<value>com.mysql.jdbc.Driver</value>
<description>Driver class name for a JDBC metastore</description>
</property>

<property>
<name>javax.jdo.option.ConnectionUserName</name>
<value>root</value>
<description>username to use against metastore database</description>
</property>

<property>
<name>javax.jdo.option.ConnectionPassword</name>
<value>root</value>
<description>password to use against metastore database</description>
</property>
</configuration>
```

### mysql驱动包
http://search.maven.org

把驱动包mysql-connector-java-5.1.46.jar放到
/home/hadoop/apps/hive/lib路径下

### 启动
```
cd apps/hive
bin/hive
```

### hive初步
清空、删除表
```
truncate table t_sz01;
drop table t_sz01;
```

创建表
```
create table t_sz01(id int, name string)
row format delimited
fields terminated by ',';
```

上传数据
```
hadoop fs -put sz.dat /user/hive/warehouse/shizhan03.db/t_sz01
```

#### hive thrift方式
```
bin/hiveserver2
```
在另一台安装了hive的机器上：
```
bin/beeline
beeline> !connect jdbc:hive2://localhost:10000
username: hadoop (默认为启动hive的用户名)
password: 直接回车
```

```
beeline -u jdbc:hive2://localhost:10000 -n zzy
```

#### 创建外部表
```
0: jdbc:hive2://localhost:10000> create external table t_sz_ext(id int,name string)
0: jdbc:hive2://localhost:10000> row format delimited fields terminated by '\t'
0: jdbc:hive2://localhost:10000> stored as textfile
0: jdbc:hive2://localhost:10000> location '/class03';

desc extended t_sz_ext;
desc formatted t_sz_ext;
```
思考：drop掉内部表、外部表，数据文件是否会一起被删除？

#### 创建分区表
```
0: jdbc:hive2://localhost:10000> create table t_sz_part(id int,name string)
0: jdbc:hive2://localhost:10000> partitioned by (country string)
0: jdbc:hive2://localhost:10000> row format delimited
0: jdbc:hive2://localhost:10000> fields terminated by ',';
```
按分区导入数据：
```
0: jdbc:hive2://localhost:10000> load data local inpath '/home/hadoop/sz.dat' into table t_sz_part
0: jdbc:hive2://localhost:10000> partition(country='China');

0: jdbc:hive2://localhost:10000> load data local inpath '/home/hadoop/sz.dat.japan' into table t_sz_part
0: jdbc:hive2://localhost:10000> partition(country='Japan');
```
指定分区查询：
```
select count(1) from t_sz_part where country='China' group by (name='furong');
```
增加/删除分区：
```
alter table t_sz_part add partition (country='American');
alter table t_sz_part drop partition (country='American');
```