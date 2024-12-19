package com.example.demoq.service;

import com.example.demoq.model.Task;
import com.example.demoq.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TaskService {

    private final TaskRepository taskRepository;

    @Autowired
    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public Task saveTask(Task task) {
        return taskRepository.save(task);
    }

    public List<Task> findByUserId(Long userId) {
        return taskRepository.findByUserId(userId);
    }

    public List<Task> findByUserIdAndStatus(Long userId, String status) {
        return taskRepository.findByUserIdAndStatus(userId, status);
    }

    public List<Task> findByCategoryId(Long categoryId) {
        return taskRepository.findByCategoryId(categoryId);
    }

    public Optional<Task> findById(Long id) {
        return taskRepository.findById(id);
    }

    public void deleteTask(Long id) {
        taskRepository.deleteById(id);
    }
}

