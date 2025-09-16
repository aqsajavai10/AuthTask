package com.example.AuthTask.service;


import com.example.AuthTask.dao.dto.TaskDto;
import com.example.AuthTask.dao.entity.Task;
import com.example.AuthTask.dao.entity.User;
import com.example.AuthTask.dao.repository.TaskRepository;
import com.example.AuthTask.dao.repository.UserRepository;
import com.example.AuthTask.exception.ForbiddenException;
import com.example.AuthTask.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TaskService {
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    public TaskDto createTask(Long userId, TaskDto dto) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found"));
        Task t = Task.builder()
                .title(dto.getTitle())
                .description(dto.getDescription())
                .status(dto.getStatus() == null ? "open" : dto.getStatus())
                .user(user)
                .build();
        Task saved = taskRepository.save(t);
        dto.setId(saved.getId());
        return dto;
    }

    public List<TaskDto> getTasks(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found"));
        return taskRepository.findByUser(user).stream().map(this::toDto).collect(Collectors.toList());
    }

    public TaskDto updateStatus(Long userId, Long taskId, String newStatus) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found"));
        Task task = taskRepository.findById(taskId).orElseThrow(() -> new NotFoundException("Task not found"));
        if (!task.getUser().getId().equals(user.getId())) throw new ForbiddenException("Not allowed");
        task.setStatus(newStatus);
        Task updated = taskRepository.save(task);
        return toDto(updated);
    }

    public void deleteTask(Long userId, Long taskId) {
        Task task = taskRepository.findById(taskId).orElseThrow(() -> new NotFoundException("Task not found"));
        if (!task.getUser().getId().equals(userId)) throw new ForbiddenException("Not allowed");
        taskRepository.delete(task);
    }

    private TaskDto toDto(Task t){
        TaskDto d = new TaskDto();
        d.setId(t.getId());
        d.setTitle(t.getTitle());
        d.setDescription(t.getDescription());
        d.setStatus(t.getStatus());
        return d;
    }
}
