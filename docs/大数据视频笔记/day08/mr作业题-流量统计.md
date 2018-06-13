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