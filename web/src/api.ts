export interface Node { id:string; x:number; y:number; label:string; lng?:number|null; lat?:number|null }
export interface Pipe { id:string; from:string; to:string; length:number; diameter:number; status:string; directed?:boolean; material?:string }
export interface Task { id:string; name:string; worker:string; device:string; status:string; pipes:string[]; start:string; end:string; progress:number; note:string; weather:string;createdAt:string;startedAt:string;finishedAt:string;workedSeconds:number;resumedAt:string;videoUrl:string }
export interface Alert { id:string; task:string; level:string; text:string; ack:boolean; at:string }
export interface Device { id:string; name:string; online:boolean; battery:number; distance:number; temperature:number; lastSeen:string }
export interface Message { id:string; task:string; text:string; at:string;sender:string;acknowledged:boolean }
export interface TaskEvent {id:string;task:string;actor:string;action:string;text:string;at:string}
export interface Worker {username:string;displayName:string;enabled:boolean;phone:string;area:string;tasks:number;running:number;completed:number;workedSeconds:number;openAlerts:number}
export interface State { nodes:Node[]; pipes:Pipe[]; tasks:Task[]; alerts:Alert[]; devices:Device[]; messages:Message[];events:TaskEvent[];networkSource:string;demo:boolean }
export async function api<T>(path:string,body?:unknown,method?:string):Promise<T>{
 const res=await fetch('/api'+path,{method:method||(body?'POST':'GET'),headers:{'Content-Type':'application/json',Authorization:'Bearer '+(sessionStorage.getItem('token')||'')},body:body?JSON.stringify(body):undefined});
 let data;try{data=await res.json();}catch{throw Error('服务暂时不可用，请稍后重试');}if(!res.ok){if(res.status===401){sessionStorage.removeItem('token');window.dispatchEvent(new Event('session-expired'));}throw Error(data.message||'请求失败');}return data as T;
}
export function download(name:string,data:unknown){const url=URL.createObjectURL(new Blob([JSON.stringify(data,null,2)],{type:'application/json'}));const a=document.createElement('a');a.href=url;a.download=name;a.click();URL.revokeObjectURL(url);}
export function duration(seconds:number){const n=Math.max(0,Math.floor(seconds||0));return Math.floor(n/3600)+'时'+Math.floor(n%3600/60)+'分'+n%60+'秒';}
