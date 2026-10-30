import { http } from '@kit.NetworkKit';
export interface PipeNode { id:string; x:number; y:number; label:string }
export interface Pipe { id:string; from:string; to:string; length:number; diameter:number; status:string }
export interface Task { id:string; name:string; worker:string; device:string; status:string; pipes:string[]; start:string; end:string; progress:number; note:string; weather:string;videoUrl:string;workedSeconds:number;startedAt:string;finishedAt:string }
export interface AlertItem { id:string; task:string; level:string; text:string; ack:boolean; at:string }
export interface Device { id:string; name:string; online:boolean; battery:number; distance:number; temperature:number; lastSeen:string }
export interface MessageItem { id:string; task:string; text:string; at:string;sender:string;acknowledged:boolean }
export interface PatrolState { nodes:PipeNode[]; pipes:Pipe[]; tasks:Task[]; alerts:AlertItem[]; devices:Device[]; messages:MessageItem[]; demo:boolean }
export interface LoginInput { username:string; password:string }
export interface LoginResult { token:string; role:string; name:string }
export interface ActionInput { action:string; note:string; weather:string }
export interface ErrorResult { message:string }
export class Api {
 static base:string='http://10.0.2.2:8080';
 static token:string='';
 static async request(path:string, body:string='',post:boolean=false,put:boolean=false):Promise<string> {
  const req=http.createHttp();
  try {
   const response=await req.request(Api.base.replace(/\/api\/?$/,'')+'/api'+path,{method:put?http.RequestMethod.PUT:post?http.RequestMethod.POST:http.RequestMethod.GET,header:{'Content-Type':'application/json','Authorization':'Bearer '+Api.token},extraData:body,connectTimeout:8000,readTimeout:10000});
   const text=response.result as string;
   if(response.responseCode<200||response.responseCode>=300){const err=JSON.parse(text) as ErrorResult;throw new Error(err.message||'服务请求失败');}
   return text;
  } finally {req.destroy();}
 }
}
