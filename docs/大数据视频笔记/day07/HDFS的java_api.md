#### Hadoop打印集群状态
```
hdfs dfsadmin -report
```

```
hadoop-daemon.sh start datanode
```

##### 注意：修改namenode路径后，需要删除原来的hadoop.tmp.dir指向的路径，
##### 再运行start-dfs.sh