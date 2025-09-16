package com.example.AuthTask.controller;


import com.example.AuthTask.dao.dto.TaskDto;
import com.example.AuthTask.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/tasks")
@RequiredArgsConstructor
public class TaskController {
    private final TaskService taskService;

    private Long getUserId(Authentication auth){
        return (Long) auth.getPrincipal();
    }

    @PostMapping
    public ResponseEntity<TaskDto> createTask(@RequestBody TaskDto dto, Authentication auth) {
        Long userId = getUserId(auth);
        TaskDto created = taskService.createTask(userId, dto);
        return ResponseEntity.status(201).body(created);
    }

    @GetMapping
    public ResponseEntity<List<TaskDto>> getTasks(Authentication auth) {
        Long userId = getUserId(auth);
        return ResponseEntity.ok(taskService.getTasks(userId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TaskDto> updateTask(@PathVariable Long id, @RequestBody TaskDto dto, Authentication auth) {
        Long userId = getUserId(auth);
        TaskDto updated = taskService.updateStatus(userId, id, dto.getStatus());
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTask(@PathVariable Long id, Authentication auth) {
        Long userId = getUserId(auth);
        taskService.deleteTask(userId, id);
        return ResponseEntity.noContent().build();
    }
}

