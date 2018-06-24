#!/bin/python
import sys
import datetime

for line in sys.stdin:
    line = line.strip()
    movieid, rating, unixtime,userid = line.split('\t')
    weekday = datetime.datetime.fromtimestamp(float(unixtime)).isoweekday()
    print '\t'.join([movieid, rating, str(weekday),userid])