#### TODO 测试MaxTemperatureWithMapOutputCompression时有错误：
not a gzip file

#### TODO 测试SequenceFileSeekAndSyncTest中的syncAfterLastSyncPoint()方法：
书中的测试结果存在疑问，next没有到末尾，都会返回true，为何书上是false？

#### 测试StreamCompressor
mkdir -p classes/StreamCompressor

javac -d classes/StreamCompressor src/main/java/StreamCompressor.java

mkdir -p jars

jar -cvf jars/StreamCompressor.jar -C classes/StreamCompressor .

echo "Text" | hadoop jar jars/StreamCompressor.jar StreamCompressor org.apache.hadoop.io.compress.GzipCodec | gunzip

#### 测试FileDecompressor
mkdir -p classes/FileDecompressor

javac -d classes/FileDecompressor src/main/java/FileDecompressor.java

jar -cvf jars/FileDecompressor.jar -C classes/FileDecompressor .

hadoop jar jars/FileDecompressor.jar FileDecompressor file:///Users/zzy/Docs/hadoop_book/ch05-io/file.gz

#### 测试PooledStreamCompressor
mkdir -p classes/PooledStreamCompressor

javac -d classes/PooledStreamCompressor src/main/java/PooledStreamCompressor.java

jar -cvf jars/PooledStreamCompressor.jar -C classes/PooledStreamCompressor .

echo "Text" | hadoop jar jars/PooledStreamCompressor.jar PooledStreamCompressor org.apache.hadoop.io.compress.GzipCodec | gunzip

#### 测试MaxTemperatureWithCompression
Run --> Edit Configurations... --> Defaults --> Application
Program arguments: input/ncdc/sample.txt.gz output

gunzip -c output/part-r-00000.gz

#### 测试TextIterator
Run

#### 测试FileDecompressorTest
在test下创建resource文件夹

双击src --> main --> resource文件夹

将test下的resource文件夹设置为"Test Resource"

新建文本file，内容为Text\n，命令行下运行"gzip file"

#### 注意：Override is not allowed when implementing interface method错误解决
菜单栏 File --> Project Structure --> Modules --> Language Level设置为6

#### 注意：Error:java: Compilation failed: internal java compiler error错误解决
菜单栏 File --> Project Structure --> Project --> Project Language Level修改为SDK default

注释掉报错的Override注解

#### 测试MaxTemperatureWithMapOutputCompression
Run --> Edit Configurations... --> Defaults --> Application
Program arguments: input/ncdc/sample.txt.gz output

gunzip -c output/part-r-00000.gz

#### 通过命令行接口显示顺序文件SequenceFile
hadoop fs -put output/numbers.seq

hadoop fs -text numbers.seq | head

#### 测试SequenceFileWriteDemo & 测试SequenceFileReadDemo
Program arguments: output/numbers.seq

#### SequenceFile的排序和合并
以Hadoop自带的例子为例：

hadoop jar \
$HADOOP_HOME/share/hadoop/mapreduce/hadoop-mapreduce-examples-*.jar \
sort -r 1 \
-inFormat org.apache.hadoop.mapreduce.lib.input.SequenceFileInputFormat \
-outFormat org.apache.hadoop.mapreduce.lib.output.SequenceFileOutputFormat \
-outKey org.apache.hadoop.io.IntWritable \
-outValue org.apache.hadoop.io.Text \
numbers.seq sorted

hadoop fs -text sorted/part-r-00000 | head

#### 测试MapFileWriteDemo
Program arguments: output/mapfile_output

hadoop fs -put output/mapfile_output/data

hadoop fs -text data | head

hadoop fs -rm data

#### 测试MapFileFixer
Program arguments: output/mapfile_output