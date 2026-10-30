"""Checks the worker app's HTTP contract against the running backend. No real handset/hardware implied."""
import json,urllib.request,urllib.error
BASE='http://127.0.0.1:8080/api'
def call(path,body=None,token='',key=''):
 headers={'Content-Type':'application/json'}
 if token:headers['Authorization']='Bearer '+token
 if key:headers['X-Device-Key']=key
 req=urllib.request.Request(BASE+path,data=None if body is None else json.dumps(body).encode(),headers=headers)
 with urllib.request.urlopen(req,timeout=10) as res:return json.load(res)
admin=call('/auth/login',{'username':'admin','password':'PipeDemo2026!'})['token']
worker=call('/auth/login',{'username':'worker','password':'WorkerDemo2026!'})['token']
t=call('/tasks',{'name':'接口联调验证（模拟设备）','start':'J01','end':'J06','worker':'worker','device':'PP-001'},admin);tid=t['id']
for action in ['approve','dispatch']:call('/tasks/'+tid+'/action',{'action':action,'note':'','weather':'dry'},admin)
call('/tasks/'+tid+'/action',{'action':'start','note':'现场检查确认（模拟）','weather':'dry'},worker)
call('/tasks/'+tid+'/messages',{'text':'总台联调：检查接口后提交记录'},admin)
call('/device/telemetry',{'device':'PP-001','task':tid,'battery':84,'distance':18,'temperature':26.5,'alarm':'模拟卡滞告警（接口测试）'},key='pipepatrol-local-device')
s=call('/state',token=worker);assert any(m['task']==tid for m in s['messages']);assert next(x for x in s['tasks'] if x['id']==tid)['status']=='paused'
alert=next(x for x in s['alerts'] if x['task']==tid);call('/alerts/'+alert['id']+'/ack',{},worker)
call('/tasks/'+tid+'/action',{'action':'start','note':'复核确认恢复（模拟）','weather':'dry'},worker)
call('/tasks/'+tid+'/action',{'action':'finish','note':'接口测试结束；非真实巡检数据','weather':'dry'},worker)
call('/tasks/'+tid+'/action',{'action':'review','note':'联调归档','weather':'dry'},admin)
assert next(x for x in call('/state',token=admin)['tasks'] if x['id']==tid)['status']=='reviewed'
print('PASS: management -> worker API -> device telemetry -> alert -> message -> review; '+tid)
