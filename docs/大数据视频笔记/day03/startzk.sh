# export A=1  定义的变量，会对自己所在的shell进程及其子进程生效
# B=1 定义的变量，只对自己所在的shell进程生效
# 在script.sh中定义的变量，在当前登录的shell进程中 source script.sh时，
# 脚本中定义的变量也会进入当前登录的进程

#!/bin/sh
echo "start zkServer..."
for i in 1 2 3
do
    ssh mini$i "source /etc/profile;/root/apps/zookeeper-3.4.9/bin/zkServer.sh start"
done