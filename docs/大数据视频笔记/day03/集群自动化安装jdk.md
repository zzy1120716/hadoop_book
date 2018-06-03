#### 安装Apache服务器
```
yum install -y httpd
systemctl stop firewalld.service
```
#### 从本机发送安装包及脚本，到服务器目录下
```
scp /Users/zzy/Downloads/jdk-8u172-linux-x64.tar.gz root@mini1:/var/www/html/soft
scp /Users/zzy/Docs/hadoop_book/docs/大数据视频笔记/day03/boot.sh /Users/zzy/Docs/hadoop_book/docs/大数据视频笔记/day03/install.sh root@mini1:/root/
```
#### 启动脚本
```
chmod +x boot.sh install.sh
./boot.sh
```
#### 验证安装是否成功
```
source /etc/profile
java -version
```