package com.example.AuthTask.dao.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data

public class TaskDto {
    private Long id;

    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Description is required")
    private String description;

    @Pattern(regexp = "open|in_progress|done", message = "Status must be one of: open, in_progress, done")
    private String status;
}
