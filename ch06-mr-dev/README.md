#### 创建Eclipse配置文件以便将项目导入Eclipse
$ mvn eclipse:eclipse -DdownloadSources=true -DdownloadJavadocs=true

#### hadoop命令行-conf的使用：
$ hadoop fs -conf conf/hadoop-localhost.xml -ls .

#### 应用ConfigurationPrinter查看conf/hadoop-localhost.xml的属性：
$ mvn compile

$ export HADOOP_CLASSPATH=target/classes

$ hadoop ConfigurationPrinter -conf conf/hadoop-localhost.xml \
| grep yarn.resourcemanager.address=

##### 命令行打包jar方式
$ mkdir -p classes/ConfigurationPrinter

$ javac -d classes/ConfigurationPrinter src/main/java/ConfigurationPrinter.java

$ mkdir jars

$ jar -cvf jars/ConfigurationPrinter.jar -C classes/ConfigurationPrinter .

$ hadoop jar jars/ConfigurationPrinter.jar ConfigurationPrinter -conf conf/hadoop-localhost.xml | grep yarn.resourcemanager.address=

##### 进行个别属性的设置
$ hadoop ConfigurationPrinter -D color=yellow | grep color

$ HADOOP_OPTS='-Dcolor=yellow' \
hadoop ConfigurationPrinter | grep color（不会输出任何值）

#### 测试MaxTemperatureDriver
$ mvn compile

$ export HADOOP_CLASSPATH=target/classes/

本地目录进行测试：

$ hadoop v2.MaxTemperatureDriver -conf conf/hadoop-local.xml \
  input/ncdc/micro output

以下命令效果相同：

$ hadoop v2.MaxTemperatureDriver -fs file:/// -jt local \
  input/ncdc/micro output
  
检查输出：

$ cat output/part-r-00000

#### 运行MaxTemperatureDriverTest
设置测试资源文件夹，其中新建一个expected.txt作为测试的预期输出

Project Structure —> Modules —> Mark as: Test Resources

#### 运行MaxTemperatureDriverMiniTest
##### 从命令行运行一个mini集群
hadoop jar $HADOOP_HOME/share/hadoop/mapreduce/hadoop-mapreduce-*-tests.jar minicluster

#### 在集群上运行
##### 打包作业
$ mvn package -DskipTests

.jar文件位于项目根路径下

##### 启动作业
使用-conf选项指定集群

$ unset HADOOP_CLASSPATH

$ hadoop jar hadoop-examples.jar v2.MaxTemperatureDriver -conf conf/hadoop-cluster.xml input/ncdc/all max-temp

#### 通过命令行查看MapReduce作业历史
hadoop ConfigurationPrinter -conf conf/hadoop-localhost.xml\
| grep mapreduce.jobhistory.done-dir

    mapreduce.jobhistory.done-dir=${yarn.app.mapreduce.am.staging-dir}/history/done

hadoop ConfigurationPrinter -conf conf/hadoop-localhost.xml \
| grep yarn.app.mapreduce.am.staging-dir

    yarn.app.mapreduce.am.staging-dir=/tmp/hadoop-yarn/staging

    /tmp/hadoop-yarn/staging/history/done_intermediate
    
mapred job -history /tmp/hadoop-yarn/staging/history/done_intermediate

#### 获取结果
-getmerge选项将源模式指定目录下所有文件合并为本地系统的一个文件

hadoop fs -getmerge max-temp max-temp-local

sort max-temp-local | tail

hadoop fs -conf conf/hadoop-local.xml -getmerge output output-local

hadoop fs -conf conf/hadoop-local.xml -cat output/*

#### 作业调试
$ mapred job -counter job_xxx_0001 \
'v3.MaxTemperatureMapper$Temperature' OVER_100

    3

#### 设置日志级别
hadoop jar hadoop-examples.jar LoggingDriver -conf conf/hadoop-local.xml \
-D mapreduce.map.log.level=DEBUG input/ncdc/micro/sample.txt logging-out

#### 调试运行Hadoop命令的JVM
$ HADOOP_ROOT_LOGGER=DEBUG,console hadoop fs -text /foo/bar

#### 启用分析
$ hadoop jar hadoop-examples.jar v4.MaxTemperatureDriver \
-conf conf/hadoop-cluster.xml \
-D mapreduce.task.profile=true \
input/ncdc/all max-temp

#### 打包和配置Oozie工作流应用
##### 安装配置Oozie
mkdistro.sh -DskipTests -Phadoop-2 -Dhadoop.auth.version=2.8.2 -Ddistcp.version=2.8.2
