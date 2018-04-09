#### 下载&配置parquet-tools
```
vi ~/.zshrc
export PARQUET_HOME=/Users/zzy/parquet
source ~/.zshrc
java -jar $PARQUET_HOME/parquet-tools-1.8.1.jar --help
```
##### 用法
```
usage: parquet-tools cat [option...] <input>
where option is one of:
       --debug     Enable debug output
    -h,--help      Show this help string
    -j,--json      Show records in JSON format.
       --no-color  Disable color output even if supported
where <input> is the parquet file to print to stdout

usage: parquet-tools head [option...] <input>
where option is one of:
       --debug          Enable debug output
    -h,--help           Show this help string
    -n,--records <arg>  The number of records to show (default: 5)
       --no-color       Disable color output even if supported
where <input> is the parquet file to print to stdout

usage: parquet-tools meta [option...] <input>
where option is one of:
       --debug     Enable debug output
    -h,--help      Show this help string
       --no-color  Disable color output even if supported
where <input> is the parquet file to print to stdout

usage: parquet-tools dump [option...] <input>
where option is one of:
    -c,--column <arg>  Dump only the given column, can be specified more than
                       once
    -d,--disable-data  Do not dump column data
       --debug         Enable debug output
    -h,--help          Show this help string
    -m,--disable-meta  Do not dump row group and page metadata
       --no-color      Disable color output even if supported
where <input> is the parquet file to print to stdout
```

#### TextToParquetWithExample
1. 打包
```
mvn clean && mvn package -DskipTests
```
2. 运行
```
hadoop jar parquet-examples.jar TextToParquetWithExample input/docs/quangle.txt output
```
3. 查看结果
```
hadoop jar $PARQUET_HOME/parquet-tools-1.8.1.jar cat output/part-m-00000.parquet
hadoop jar $PARQUET_HOME/parquet-tools-1.8.1.jar cat -j output/part-m-00000.parquet
hadoop jar $PARQUET_HOME/parquet-tools-1.8.1.jar schema output/part-m-00000.parquet
hadoop jar $PARQUET_HOME/parquet-tools-1.8.1.jar meta output/part-m-00000.parquet
```

#### ParquetToTextWithExample
```
hadoop jar parquet-examples.jar ParquetToTextWithExample \
  output/part-m-00000.parquet output-text
```

```
hadoop fs -cat output-text/part-m-00000 
```

#### TextToParquetWithAvro
```
hadoop jar parquet-examples.jar TextToParquetWithAvro \
  input/docs/quangle.txt output
```

```
hadoop jar $PARQUET_HOME/parquet-tools-1.8.1.jar cat output/part-m-00000.parquet
hadoop jar $PARQUET_HOME/parquet-tools-1.8.1.jar cat -j output/part-m-00000.parquet
hadoop jar $PARQUET_HOME/parquet-tools-1.8.1.jar schema output/part-m-00000.parquet
hadoop jar $PARQUET_HOME/parquet-tools-1.8.1.jar meta output/part-m-00000.parquet
hadoop jar $PARQUET_HOME/parquet-tools-1.8.1.jar head output/part-m-00000.parquet
hadoop jar $PARQUET_HOME/parquet-tools-1.8.1.jar dump output/part-m-00000.parquet
```

#### ParquetToTextWithAvro
```
hadoop jar parquet-examples.jar ParquetToTextWithAvro \
  output/part-m-00000.parquet output-text
```

```
hadoop fs -cat output-text/part-m-00000 
```