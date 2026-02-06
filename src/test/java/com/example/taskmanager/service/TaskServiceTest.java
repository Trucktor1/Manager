package com.example.taskmanager.service;

import com.example.taskmanager.dto.TaskRequest;
import com.example.taskmanager.exception.TaskNotFoundException;
import com.example.taskmanager.model.Task;
import com.example.taskmanager.model.TaskStatus;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class TaskServiceTest {

    private final TaskService taskService = new TaskService();

    @Test
    void shouldCreateTaskSuccessfully() {
        TaskRequest request = new TaskRequest(
                "Новая задача",
                "Описание задачи",
                TaskStatus.TODO,
                LocalDateTime.now().plusDays(1)
        );

        Task created = taskService.create(request);

        assertNotNull(created.id());
        assertEquals("Новая задача", created.title());
        assertEquals(TaskStatus.TODO, created.status());
        assertNotNull(created.createdAt());
        assertEquals(request.dueDate(), created.dueDate());
        assertTrue(taskService.findById(created.id()).isPresent());
    }

    @Test
    void shouldUpdateTaskSuccessfullyAndKeepCreatedAt() {
        Task created = taskService.create(new TaskRequest(
                "Task 1",
                "desc",
                TaskStatus.TODO,
                LocalDateTime.now().plusDays(2)
        ));

        Task updated = taskService.update(created.id(), new TaskRequest(
                "Task 1 updated",
                "desc updated",
                TaskStatus.IN_PROGRESS,
                LocalDateTime.now().plusDays(5)
        ));

        assertEquals(created.id(), updated.id());
        assertEquals("Task 1 updated", updated.title());
        assertEquals(TaskStatus.IN_PROGRESS, updated.status());
        assertEquals(created.createdAt(), updated.createdAt(), "createdAt must not change");
    }

    @Test
    void shouldDeleteTaskSuccessfully() {
        Task created = taskService.create(new TaskRequest(
                "Task to delete",
                null,
                TaskStatus.DONE,
                null
        ));

        taskService.delete(created.id());

        assertTrue(taskService.findById(created.id()).isEmpty());
    }

    @Test
    void shouldThrowWhenDeletingNonExistingTask() {
        UUID id = UUID.randomUUID();
        assertThrows(TaskNotFoundException.class, () -> taskService.delete(id));
    }
}