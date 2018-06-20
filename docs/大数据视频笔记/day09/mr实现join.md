```
hadoop fs -mkdir -p /rjoin/input
hadoop fs -put order.txt pd.txt /rjoin/input
```
### Edit Configurations...
#### Program arguments: /rjoin/input /rjoin/output
#### VM options: -DHADOOP_USER_NAME=hadoop

### 在本地运行mapreduce任务，需修改YARNRunner的源码
#### 共三处
#### 注意使用job.setJar("jar包的绝对路径")代替setJarByClass
```
// TODO 注释原来的代码
// vargs.add(MRApps.crossPlatformifyMREnv(jobConf, Environment.JAVA_HOME) + "/bin/java");
// TODO 修改环境配置，从而在windows本地执行mapreduce任务
System.out.println(MRApps.crossPlatformifyMREnv(jobConf, Environment.JAVA_HOME) + "/bin/java");
System.out.println("$JAVA_HOME/bin/java");
vargs.add("$JAVA_HOME/bin/java");
```

```
// TODO BY ZZY
for (String key : environment.keySet()) {
    String org = environment.get(key);
    String linux = getLinux(org);
    environment.put(key, linux);
}
```

```
// TODO BY ZZY
private String getLinux(String org) {
    StringBuilder sb = new StringBuilder();
    int c = 0;
    for (int i = 0; i < org.length(); i++) {
        if (org.charAt(i) == '%') {
            c++;
            if (c % 2 == 1) {
                sb.append("$");
            }
        } else {
            switch (org.charAt(i)) {
                case ';':
                    sb.append(":");
                    break;

                case '\\':
                    sb.append("/");
                    break;
                default:
                    sb.append(org.charAt(i));
                    break;
            }
        }
    }
    return (sb.toString());
}
```