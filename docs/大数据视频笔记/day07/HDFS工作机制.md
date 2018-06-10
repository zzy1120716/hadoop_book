#### 关于副本数量的问题
副本数由客户端的参数dfs.replication决定（优先级：conf.set > 自定义配置文件 > jar包中的hdfs-default.xml）

#### 思考题：
1. 如果namenode的磁盘损坏，元数据是否还能恢复？如何恢复？
将secondarynamenode的元数据目录拷贝给namenode。

2. 通过以上思考，我们在配namenode工作目录参数时，有什么注意点？
namenode的工作目录应该配在多块磁盘上。

#### 配置多个namenode路径，防止磁盘损坏，造成元数据丢失
```
vi hdfs-site.xml

<property>
  <name>dfs.name.dir</name>
  <value>/home/hadoop/name1,/home/hadoop/name2</value>
</property>

hadoop namenode -format
```

#### 补充：seen_txid
文件中记录的是edits滚动的序号，每次重启namenode时，namenode就知道要将哪些edits进行加载edits