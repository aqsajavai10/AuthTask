package com.example.AuthTask.controller;

import com.example.AuthTask.dao.dto.TaskDto;
import com.example.AuthTask.service.TaskService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TaskController.class)
@AutoConfigureMockMvc(addFilters = true) // ✅ Enable security filters
class TaskControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @MockitoBean
    private TaskService taskService;


    @Test
    @WithMockUser(username = "1", roles = "USER")
    void testCreateTask() throws Exception {
        TaskDto req = new TaskDto();
        req.setTitle("New Task");
        req.setDescription("Some description");

        TaskDto saved = new TaskDto();
        saved.setId(1L);
        saved.setTitle("New Task");
        saved.setDescription("Some description");

        Mockito.when(taskService.createTask(anyLong(), any(TaskDto.class)))
                .thenReturn(saved);

        mockMvc.perform(post("/tasks")
                        .with(csrf()) // ✅ add CSRF
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("New Task"));
    }


    @Test
    @WithMockUser(username = "1", roles = "USER")
    void testGetTasks() throws Exception {
        TaskDto dto = new TaskDto();
        dto.setId(1L);
        dto.setTitle("Sample Task");
        dto.setStatus("open");

        Mockito.when(taskService.getTasks(anyLong()))
                .thenReturn(Collections.singletonList(dto));

        mockMvc.perform(get("/tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Sample Task"));
    }

    @Test
    @WithMockUser(username = "1", roles = "USER")
    void testUpdateTaskStatus() throws Exception {
        TaskDto updated = new TaskDto();
        updated.setId(1L);
        updated.setTitle("Sample Task");
        updated.setStatus("done");

        Mockito.when(taskService.updateStatus(anyLong(), eq(1L), eq("done")))
                .thenReturn(updated);

        mockMvc.perform(put("/tasks/1")
                        .with(csrf()) // ✅ add CSRF
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"status\":\"done\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("done"));
    }

    @Test
    @WithMockUser(username = "1", roles = "USER")
    void testDeleteTask() throws Exception {
        Mockito.doNothing().when(taskService).deleteTask(anyLong(), eq(1L));

        mockMvc.perform(delete("/tasks/1")
                        .with(csrf())) // ✅ add CSRF
                .andExpect(status().isNoContent());
    }



    // ❌ Unauthorized Tests (no @WithMockUser)

    @Test
    void testCreateTask_Unauthorized() throws Exception {
        TaskDto req = new TaskDto();
        req.setTitle("Unauthorized Task");
        req.setDescription("Should fail");

        mockMvc.perform(post("/tasks")
                        .with(csrf()) // ✅ include CSRF to bypass CSRF filter
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isUnauthorized()); // ✅ expect 401 now
    }


    @Test
    void testGetTasks_Unauthorized() throws Exception {
        mockMvc.perform(get("/tasks"))
                .andExpect(status().isUnauthorized());
    }
    @Test
    void testUpdateTaskStatus_Unauthorized() throws Exception {
        mockMvc.perform(put("/tasks/1")
                        .with(csrf()) // ✅ add CSRF so we hit auth filter, not CSRF filter
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"status\":\"done\"}"))
                .andExpect(status().isUnauthorized()); // ✅ expect 401 now
    }

    @Test
    void testDeleteTask_Unauthorized() throws Exception {
        mockMvc.perform(delete("/tasks/1")
                        .with(csrf())) // ✅ add CSRF
                .andExpect(status().isUnauthorized()); // ✅ expect 401 now
    }
}
