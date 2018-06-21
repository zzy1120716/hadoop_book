#### 数据倾斜 map端join
maptask的输出，由于大部分都有相同的key，导致数据集中输入到一个reducetask

1. 建一张产品表全表，在map端进行拼接  
map side join  
map端join  
避免数据倾斜  

2. DistributedCache

#### 倒排索引
