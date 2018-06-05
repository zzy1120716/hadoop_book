#### 搭好集群后报错：WARN hdfs.DFSClient: DataStreamer Exception    
org.apache.hadoop.ipc.RemoteException(java.io.IOException): File   
/home/input/file1.txt._COPYING_ could only be replicated to 0   
nodes instead of minReplication (=1).  There are 2 datanode(s)   
running and no node(s) are excluded in this operation.  
##### 需关闭防火墙
##### Centos7下
关闭防火墙：
```
sudo systemctl stop firewalld
```
禁用防火墙（开机不启动）：
```
sudo systemctl disable firewalld
```
##### Centos6下
关闭防火墙：
```
service iptables stop
```
禁用防火墙（开机不启动）：
```
chkconfig iptables off
```