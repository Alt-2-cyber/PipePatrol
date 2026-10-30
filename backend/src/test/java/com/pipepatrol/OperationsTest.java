package com.pipepatrol;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import java.nio.file.*;
import java.util.*;
@SpringBootTest(properties={"pipe.data=target/operations-test.json"}) @AutoConfigureMockMvc
class OperationsTest {
 @Autowired MockMvc mvc;@Autowired ObjectMapper json;@Autowired PatrolController controller;
 @BeforeAll static void clean()throws Exception{Files.deleteIfExists(Path.of("target/operations-test.json.database.mv.db"));}
 String login(String name,String password)throws Exception{return "Bearer "+json.readTree(mvc.perform(post("/api/auth/login").contentType("application/json").content(json.writeValueAsString(Map.of("username",name,"password",password)))).andExpect(status().isOk()).andReturn().getResponse().getContentAsString()).get("token").asText();}
 String data(Object o)throws Exception{return json.writeValueAsString(o);}
 @Test void databaseWorkersRoutingAndFeedback()throws Exception{
  String admin=login("admin","PipeDemo2026!"),w1=login("worker","WorkerDemo2026!"),w2=login("worker02","WorkerDemo2026!");
  mvc.perform(get("/api/workers").header("Authorization",w1)).andExpect(status().isForbidden());
  mvc.perform(post("/api/workers").header("Authorization",admin).contentType("application/json").content(data(Map.of("username","inspector04","displayName","Inspector Four","password","SecurePass123","area","West","phone","")))).andExpect(status().isOk());
  String w4=login("inspector04","SecurePass123");
  mvc.perform(post("/api/routes/preview").header("Authorization",admin).contentType("application/json").content(data(Map.of("start","J01","end","J06","via",List.of("J02"))))).andExpect(status().isOk()).andExpect(jsonPath("$.length").value(127)).andExpect(jsonPath("$.legs.length()").value(2));
  mvc.perform(post("/api/routes/preview").header("Authorization",admin).contentType("application/json").content(data(Map.of("start","J01","end","J06","avoid",List.of("P03"))))).andExpect(status().isOk()).andExpect(jsonPath("$.length").value(127));
  mvc.perform(post("/api/routes/preview").header("Authorization",admin).contentType("application/json").content(data(Map.of("start","J01","end","J06","minDiameter",300)))).andExpect(status().isBadRequest());
  var pipes=new ArrayList<>(controller.state.pipes);var old=pipes.get(2);pipes.set(2,new PatrolController.Pipe(old.id(),old.from(),old.to(),100,old.diameter(),old.status()));
  mvc.perform(put("/api/network").header("Authorization",admin).contentType("application/json").content(data(new OperationsController.Network(controller.state.nodes,pipes,"Integration test network",true)))).andExpect(status().isOk());
  mvc.perform(post("/api/routes/preview").header("Authorization",admin).contentType("application/json").content(data(Map.of("start","J01","end","J06")))).andExpect(status().isOk()).andExpect(jsonPath("$.length").value(127));
  String id=json.readTree(mvc.perform(post("/api/tasks").header("Authorization",admin).contentType("application/json").content(data(Map.of("name","Worker Four Run","start","J01","end","J06","worker","inspector04","device","PP-001")))).andExpect(status().isOk()).andReturn().getResponse().getContentAsString()).get("id").asText();
  for(String action:List.of("approve","dispatch"))mvc.perform(post("/api/tasks/"+id+"/action").header("Authorization",admin).contentType("application/json").content(data(Map.of("action",action)))).andExpect(status().isOk());
  mvc.perform(get("/api/state").header("Authorization",w2)).andExpect(status().isOk()).andExpect(jsonPath("$.tasks.length()").value(0));
  mvc.perform(post("/api/tasks/"+id+"/messages").header("Authorization",w1).contentType("application/json").content(data(Map.of("text","unauthorized")))).andExpect(status().isForbidden());
  var msg=json.readTree(mvc.perform(post("/api/tasks/"+id+"/messages").header("Authorization",w4).contentType("application/json").content(data(Map.of("text","Site conditions checked")))).andExpect(status().isOk()).andReturn().getResponse().getContentAsString());
  mvc.perform(post("/api/messages/"+msg.get("id").asText()+"/ack").header("Authorization",admin)).andExpect(status().isOk());
  mvc.perform(post("/api/tasks/"+id+"/action").header("Authorization",w4).contentType("application/json").content(data(Map.of("action","start","weather","dry")))).andExpect(status().isOk());
  mvc.perform(post("/api/device/telemetry").header("X-Device-Key","pipepatrol-local-device").contentType("application/json").content(data(new PatrolController.Telemetry("PP-001",id,80,10,27,"")))).andExpect(status().isOk());
  mvc.perform(get("/api/tasks/"+id+"/telemetry").header("Authorization",w4)).andExpect(status().isOk()).andExpect(jsonPath("$[0].distance").value(10));
  mvc.perform(get("/api/tasks/"+id+"/telemetry").header("Authorization",w2)).andExpect(status().isForbidden());
  mvc.perform(put("/api/workers/inspector04").header("Authorization",admin).contentType("application/json").content(data(Map.of("username","inspector04","displayName","Inspector Four","enabled",false)))).andExpect(status().isBadRequest());
  mvc.perform(post("/api/tasks/"+id+"/action").header("Authorization",w4).contentType("application/json").content(data(Map.of("action","pause","note","checked")))).andExpect(status().isOk());
  mvc.perform(put("/api/workers/inspector04").header("Authorization",admin).contentType("application/json").content(data(Map.of("username","inspector04","displayName","Inspector Four","enabled",false)))).andExpect(status().isOk());
  mvc.perform(get("/api/state").header("Authorization",w4)).andExpect(status().isUnauthorized());
  var recovered=controller.db.load();Assertions.assertTrue(recovered.tasks.stream().anyMatch(t->t.id.equals(id)));Assertions.assertTrue(recovered.messages.stream().anyMatch(m->m.acknowledged()));Assertions.assertFalse(recovered.events.isEmpty());
  try(var c=controller.db.connect();var st=c.createStatement();var rs=st.executeQuery("SELECT password_hash FROM accounts WHERE username='inspector04'")){Assertions.assertTrue(rs.next());Assertions.assertNotEquals("SecurePass123",rs.getString(1));}
 }
}
