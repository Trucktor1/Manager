package com.example.taskmanager.service;

import com.example.taskmanager.dto.TaskRequest;
import com.example.taskmanager.exception.TaskNotFoundException;
import com.example.taskmanager.model.Task;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class TaskService {

    private final ConcurrentHashMap<UUID, Task> tasks = new ConcurrentHashMap<>();

    public Task create(TaskRequest request) {
        Task task = new Task(
                UUID.randomUUID(),
                request.title(),
                request.description(),
                request.status(),
                LocalDateTime.now(),
                request.dueDate()
        );
        tasks.put(task.id(), task);
        return task;
    }

    public Optional<Task> findById(UUID id) {
        return Optional.ofNullable(tasks.get(id));
    }

    public List<Task> findAll() {
        return new ArrayList<>(tasks.values());
    }

    public Task update(UUID id, TaskRequest request) {
        Task existing = tasks.get(id);
        if (existing == null) {
            throw new TaskNotFoundException(id);
        }

        Task updated = new Task(
                id,
                request.title(),
                request.description(),
                request.status(),
                existing.createdAt(),
                request.dueDate()
        );

        tasks.put(id, updated);
        return updated;
    }

    public void delete(UUID id) {
        if (!tasks.containsKey(id)) {
            throw new TaskNotFoundException(id);
        }
        tasks.remove(id);
    }
}
