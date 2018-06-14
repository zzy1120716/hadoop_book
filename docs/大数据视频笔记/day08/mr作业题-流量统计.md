### 1. 统计每个用户的总流量、上行流量、下行流量
#### map
读一行，切分字段
抽取手机号，上行流量，下行流量
context.write(手机号,bean)

#### reduce

#### 测试
```
hadoop fs -mkdir -p /flowsum/input
hadoop fs -put flow.data /flowsum/input
hadoop fs -put flow.data /flowsum/input/flow.data.2
hadoop fs -put flow.data /flowsum/input/flow.data.3
hadoop jar flowsum.jar cn.itcast.bigdata.mr.flowsum.FlowCount /flowsum/input /flowsum/output
hadoop fs -cat /flowsum/output/part-r-00000
```

### 2. 得出上题结果的基础之上再加一个需求：将统计结果按照总流量倒序排序
#### 测试
```
hadoop jar flowsum.jar cn.itcast.bigdata.mr.flowsum.FlowCountSort /flowsum/output /flowsum/sortout
hadoop fs -ls /flowsum/sortout
hadoop fs -cat /flowsum/sortout/part-r-00000
```


### 3. 将统计结果按照手机归属地不同省份输出到不同文件中
#### map
读一行，切分字段
抽取手机号，上行流量，下行流量
context.write(手机号,bean)

map输出的数据要分成6个区
重写partitioner，让相同归属地的号码返回相同的分区号int

#### reduce
6个省，跑6个reduce task
拿到一个号码所有数据
遍历，累加
输出

#### 测试
```
hadoop jar flowsum.jar cn.itcast.bigdata.mr.provinceflow.FlowCount /flowsum/input /flowsum/provinceout
hadoop fs -ls /flowsum/provinceout
hadoop fs -cat /flowsum/provinceout/part-r-00000
hadoop fs -cat /flowsum/provinceout/part-r-00001
hadoop fs -cat /flowsum/provinceout/part-r-00002
hadoop fs -cat /flowsum/provinceout/part-r-00003
hadoop fs -cat /flowsum/provinceout/part-r-00004
```
