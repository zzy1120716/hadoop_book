#!/bin/bash

BASE_SERVER=mini1
yum install -y wget
wget $BASE_SERVER/soft/jdk-8u172-linux-x64.tar.gz
tar -zxvf jdk-8u172-linux-x64.tar.gz -C /usr/local
cat >> /etc/profile << EOF
export JAVA_HOME=/usr/local/jdk1.8.0_172 
export PATH=\$PATH:\$JAVA_HOME/bin 
EOF