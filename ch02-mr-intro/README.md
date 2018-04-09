mvn install:install-file -Dfile=./target/ch02-mr-intro-4.0.jar -DgroupId=com.zzy.hadoopbook -DartifactId=ch02-mr-intro -Dversion=4.0 -Dpackaging=jar

# 测试MaxTemperature
Run --> Edit Configurations... --> 
Program arguments: sample.txt output

cat output/part-r-00000