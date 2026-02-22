#!/usr/bin/env python3
import sys
path = sys.argv[1] if len(sys.argv) > 1 else '/root/mattfuncional/mattfuncional'
with open(path, 'rb') as f:
    data = f.read()
data = data.replace(b'\r\n', b'\n').replace(b'\r', b'\n')
with open(path, 'wb') as f:
    f.write(data)
print('Done')
