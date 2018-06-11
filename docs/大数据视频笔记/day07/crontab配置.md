```
 */1 * * * * sh /export/servers/shell/uploadFile2Hdfs.v2.sh
 */1 * * * * sh source /etc/profile;sh /export/servers/shell/uploadFile2Hdfs.v1.sh
```

# 编辑命令是crontab -e
# 查看命令是crontab -l