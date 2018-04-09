## 下载NCDC气象数据 
### ncdc_download.py
http://blog.csdn.net/lihuinihao/article/details/38315231
ftp://ftp3.ncdc.noaa.gov/pub/data/noaa/isd-lite/
python3 ncdc_download.py 1901 2014 weatherdata

ncdc_download.py       //上述代码文件
1901   //起始下载年份
2014  //截止下载年份
weatherdata                  //指定的下载目录（要手动自己创建）

### ncdc.sh
chmod 777 ncdc.sh
ftp://ftp.ncdc.noaa.gov/pub/data/noaa

Usage: ./ncdc.sh 1901 1930 # download wheather datasets for period from 1901 to 1930.