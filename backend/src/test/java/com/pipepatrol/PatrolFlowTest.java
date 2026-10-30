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
@SpringBootTest(properties={"pipe.data=target/test-state.json"}) @AutoConfigureMockMvc
class PatrolFlowTest {
 @Autowired MockMvc mvc; @Autowired ObjectMapper json;
 @BeforeAll static void clean() throws Exception {Files.deleteIfExists(Path.of("target/test-state.json.database.mv.db"));}
 String login(String name,String password)throws Exception{return "Bearer "+json.readTree(mvc.perform(post("/api/auth/login").contentType("application/json").content("{\"username\":\""+name+"\",\"password\":\""+password+"\"}")).andExpect(status().isOk()).andReturn().getResponse().getContentAsString()).get("token").asText();}
 void act(String token,String id,String action,String note,String weather,int expected)throws Exception{mvc.perform(post("/api/tasks/"+id+"/action").header("Authorization",token).contentType("application/json").content(json.writeValueAsString(new PatrolController.Action(action,note,weather)))).andExpect(status().is(expected));}
 @Test void fullTaskLifecycleAndPermissions() throws Exception {
  String admin=login("admin","PipeDemo2026!"),worker=login("worker","WorkerDemo2026!");
  mvc.perform(get("/api/state")).andExpect(status().isUnauthorized());
  mvc.perform(post("/api/routes/preview").header("Authorization",worker).contentType("application/json").content("{\"start\":\"J01\",\"end\":\"J06\"}")).andExpect(status().isForbidden());
  mvc.perform(post("/api/routes/preview").header("Authorization",admin).contentType("application/json").content("{\"start\":\"J03\",\"end\":\"J07\"}")).andExpect(status().isOk()).andExpect(jsonPath("$.length").value(75));
  var task=json.readTree(mvc.perform(post("/api/tasks").header("Authorization",admin).contentType("application/json").content("{\"name\":\"Integration run\",\"start\":\"J01\",\"end\":\"J06\",\"worker\":\"worker\",\"device\":\"PP-001\"}")).andExpect(status().isOk()).andReturn().getResponse().getContentAsString());String id=task.get("id").asText();
  act(worker,id,"approve","","dry",403);act(admin,id,"dispatch","","dry",400);act(admin,id,"approve","","dry",200);act(admin,id,"dispatch","","dry",200);
  act(worker,id,"start","","rain",400);act(worker,id,"start","","dry",200);
  mvc.perform(post("/api/device/telemetry").header("X-Device-Key","wrong").contentType("application/json").content(json.writeValueAsString(new PatrolController.Telemetry("PP-001",id,80,20,27,"")))).andExpect(status().isUnauthorized());
  mvc.perform(post("/api/device/telemetry").header("X-Device-Key","pipepatrol-local-device").contentType("application/json").content(json.writeValueAsString(new PatrolController.Telemetry("PP-001",id,80,20,27,"simulated obstruction")))).andExpect(status().isOk()).andExpect(jsonPath("$.taskStatus").value("paused"));
  mvc.perform(post("/api/tasks/"+id+"/messages").header("Authorization",admin).contentType("application/json").content("{\"text\":\"Check the pipe joint\"}")).andExpect(status().isOk());
  mvc.perform(get("/api/state").header("Authorization",worker)).andExpect(status().isOk()).andExpect(jsonPath("$.messages[0].text").value("Check the pipe joint"));
  act(worker,id,"start","checked","dry",200);act(worker,id,"finish","","dry",400);act(worker,id,"finish","Inspection complete","dry",200);act(admin,id,"review","reviewed","dry",200);
  mvc.perform(post("/api/device/telemetry").header("X-Device-Key","pipepatrol-local-device").contentType("application/json").content(json.writeValueAsString(new PatrolController.Telemetry("PP-001",id,80,20,27,"")))).andExpect(status().isBadRequest());
  Assertions.assertTrue(Files.exists(Path.of("target/test-state.json.database.mv.db")));
 }
}
