#!/usr/bin/env python

import re
import sys

try:
    for line in sys.stdin:
        (year, temp, q) = line.strip().split()
        if (temp != "9999" and re.match("[01459]", q)):
            print "%s\t%s" % (year, temp)

except:
    print sys.exc_info()