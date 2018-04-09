hadoop fs -touchz quangle
hadoop fs -rm quangle
hadoop fs -lsr .Trash
hadoop fs -mv .Trash/Current/user/zzy/quangle .
hadoop fs -ls .