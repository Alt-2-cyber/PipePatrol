"""Python标准库模拟器：先在作业App启动任务，再执行。不是Hi3863固件。"""
import argparse,json,time,urllib.request,urllib.error
p=argparse.ArgumentParser();p.add_argument('--url',default='http://127.0.0.1:8080');p.add_argument('--task',default='T-DEMO01');p.add_argument('--key',default='pipepatrol-local-device');p.add_argument('--alarm',default='');p.add_argument('--count',type=int,default=20);a=p.parse_args()
for i in range(a.count):
 body=json.dumps({'device':'PP-001','task':a.task,'battery':round(86-i*.1,1),'distance':round(i*1.8,1),'temperature':26.5,'alarm':a.alarm if i==a.count-1 else ''}).encode()
 r=urllib.request.Request(a.url+'/api/device/telemetry',data=body,headers={'Content-Type':'application/json','X-Device-Key':a.key})
 try:
  with urllib.request.urlopen(r,timeout=10) as f:print(f.status,f.read().decode())
 except urllib.error.HTTPError as e: print(e.code,e.read().decode());break
 time.sleep(2)
