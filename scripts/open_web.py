from pathlib import Path
import os,shutil,subprocess,time,urllib.request,webbrowser
ROOT=Path(__file__).resolve().parent.parent
def healthy(url):
 try:
  with urllib.request.urlopen(url,timeout=2) as r:return r.status==200
 except Exception:return False
try:
 logs=ROOT/'logs';logs.mkdir(exist_ok=True)
 if not healthy('http://127.0.0.1:8080/api/health'):
  java=str(Path(os.environ['JAVA_HOME'])/'bin/java.exe') if os.environ.get('JAVA_HOME') else shutil.which('java')
  if not java:raise RuntimeError('Java 17 is required. Set JAVA_HOME or add java to PATH.')
  if not (ROOT/'backend/target/pipepatrol-backend-0.1.0.jar').exists():raise RuntimeError('Build the backend first: cd backend && mvn package')
  with (logs/'backend.out.log').open('ab') as out:
   subprocess.Popen([java,'-jar',str(ROOT/'backend/target/pipepatrol-backend-0.1.0.jar')],cwd=ROOT/'backend',stdout=out,stderr=out,creationflags=subprocess.CREATE_NO_WINDOW)
 if not healthy('http://127.0.0.1:5173/'):
  node=shutil.which('node')
  if not node:raise RuntimeError('Node.js is required. Add node to PATH.')
  if not (ROOT/'web/node_modules/vite/bin/vite.js').exists():raise RuntimeError('Install web dependencies first: cd web && npm ci')
  with (logs/'web.out.log').open('ab') as out:
   subprocess.Popen([node,str(ROOT/'web/node_modules/vite/bin/vite.js'),'--host','127.0.0.1','--port','5173','--strictPort'],cwd=ROOT/'web',stdout=out,stderr=out,creationflags=subprocess.CREATE_NO_WINDOW)
 for i in range(30):
  if healthy('http://127.0.0.1:8080/api/health') and healthy('http://127.0.0.1:5173/'):
   print('Ready: http://127.0.0.1:5173/  Login: admin / PipeDemo2026!')
   webbrowser.open('http://127.0.0.1:5173/');break
  time.sleep(.5)
 else:raise RuntimeError('Services not ready. Check logs folder or manual start scripts.')
except Exception as e:
 print(str(e));raise SystemExit(1)
