# 网上教程

将 Hadoop 的 classhpath 信息添加到 CLASSPATH 变量中，在 ~/.bashrc 中增加如下几行：

export HADOOP_HOME=/usr/local/hadoop
export CLASSPATH=$($HADOOP_HOME/bin/hadoop classpath):$CLASSPATH

//编译wordcount
mkdir WordCount
javac -d WordCount WordCount.java


//打包程序
jar -cvf wordcount.jar -C WordCount .


//运行程序
hadoop jar wordcount.jar WordCount /input /output

# 测试URLCat

//设置classpath
export CLASSPATH=$($HADOOP_HOME/bin/hadoop classpath):$CLASSPATH

//编译URLCatTest
mkdir URLCatTest
javac -d URLCatTest URLCatTest.java


//打包程序
jar -cvf URLCatTest.jar -C URLCatTest .


//运行程序
hadoop jar URLCatTest.jar URLCatTest hdfs://localhost:9000/user/zzy/server.log

hadoop jar FileCopyWithProgress.jar FileCopyWithProgress server1.log  hdfs://localhost:9000/user/zzy/server1.log

# 注意：
import static作用：导入这个类里的静态方法，只要把 .* 换成静态方法名就行了，然后在这个类中，就可以直接用方法名调用静态方法，而不必用ClassName.方法名 的方式来调用。 
这种方法建议在有很多重复调用的时候使用，如果仅有一到两次调用，不如直接写来的方便。

# 测试ListStatus
<del>hadoop ListStatus hdfs://localhost:9000/ hdfs://localhost:9000/user/zzy</del>

mkdir classes/ListStatus

javac -d classes/ListStatus src/main/java/ListStatus.java

jar -cvf jars/ListStatus.jar -C classes/ListStatus .

hadoop jar jars/ListStatus.jar ListStatus hdfs://localhost:9000/ hdfs://localhost:9000/user/zzy