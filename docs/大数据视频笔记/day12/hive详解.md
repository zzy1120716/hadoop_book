#### 创建分桶表
```
0: jdbc:hive2://localhost:10000> create table t_buck(id string,name string)
0: jdbc:hive2://localhost:10000> clustered by (id)
0: jdbc:hive2://localhost:10000> sorted by (id)
0: jdbc:hive2://localhost:10000> into 4 buckets
0: jdbc:hive2://localhost:10000> row format delimited fields terminated by ',';

0: jdbc:hive2://localhost:10000> desc extended t_buck;

0: jdbc:hive2://localhost:10000> load data local inpath '/home/hadoop/sz.dat'
0: jdbc:hive2://localhost:10000> into table t_buck;
```

发现还是一个文件

需要通过sql查询，将结果插入到桶里

```
truncate table t_buck;
```

```
create table t_p(id string,name string)
row format delimited fields terminated by ',';

load data local inpath '/home/hadoop/sz.dat'
into table t_p;

insert into table t_buck
select id,name from t_p cluster by (id);
```

#### 设置变量,设置分桶为true, 设置reduce数量是分桶的数量个数
```
set hive.enforce.bucketing = true;
set mapreduce.job.reduces=4;
```

#### 查询结果插入另一张表
```
create table t_tmp
as
select * from t_p;
```

#### UDF
测试各种内置函数的快捷方法：
创建一个dual表
create table dual(id string);
load一个文件（一行，一个空格）到dual表
select substr('angelababy',2,3) from dual;
```
create table dual(id string);
load data local inpath '/Users/zzy/dual.dat' into table dual;
select concat('a', 'b') from dual;
```

##### 自定义UDF
```
add jar /Users/zzy/udf.jar;
create temporary function tolow as 'cn.itcast.bigdata.udf.ToLowerCase';
insert into t_p values(13, "ANGELA");
select tolow(name) from t_p;

create table t_flow(phonenbr int,flow int)
row format delimited fields terminated by ',';
load data local inpath '/Users/zzy/flow.dat' into table t_flow;
add jar /Users/zzy/udf.jar;
create temporary function getprovince as 'cn.itcast.bigdata.udf.ToLowerCase';
select phonenbr,getprovince(phonenbr),flow from t_flow;
```

#### 实战，电影打分，建表
1. 处理json字符串
```
create table t_json(line string)
row format delimited;
load data local inpath '/Users/zzy/Movies/大数据/02_课程配套课件及资料/day12/rating.json' into table t_json;
select * from t_json limit 10;

beeline -u jdbc:hive2://localhost:10000 -n zzy
add jar /Users/zzy/udf.jar;
create temporary function parsejson as 'cn.itcast.bigdata.udf.JsonParser';
select parsejson(line) from t_json limit 10;
```
2. 截取字符串，结果插入新表
```
create table t_rating
as
select split(parsejson(line),'\t')[0] as movieid,
split(parsejson(line),'\t')[1] as rate,
split(parsejson(line),'\t')[2] as timestring,
split(parsejson(line),'\t')[3] as uid
from t_json;
```
也可以使用内置json函数
```
insert overwrite table t_rating
select get_json_object(line,'$.movie') as movie,
get_json_object(line,'$.rate') as rate
from t_json;
```

#### Transform
1. 先编辑一个python脚本文件
2. 然后，将文件加入hive的classpath
```
add FILE /Users/zzy/weekday_mapper.py;
```
3.建表
```
CREATE TABLE t_rating_weekday as
SELECT
  TRANSFORM (movieid , rate, timestring,uid)
  USING 'python weekday_mapper.py'
  AS (movieid, rating, weekday,userid)
FROM t_rating;

select * from t_rating_weekday limit 10;
```