package com.example.demoq.controller;

import com.example.demoq.model.Category;
import com.example.demoq.model.Task;
import com.example.demoq.model.User;
import com.example.demoq.repository.CategoryRepository;
import com.example.demoq.repository.TaskRepository;
import com.example.demoq.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/tasks")
public class ApiTaskController {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;

    public ApiTaskController(TaskRepository taskRepository, UserRepository userRepository, CategoryRepository categoryRepository) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
    }


    @GetMapping
    public ResponseEntity<List<Task>> getAllTasks() {
        List<Task> tasks = taskRepository.findAll();
        return ResponseEntity.ok(tasks);
    }


    @GetMapping("/{id}")
    public ResponseEntity<Task> getTaskById(@PathVariable Long id) {
        Optional<Task> task = taskRepository.findById(id);
        return task.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }


    @PostMapping
    public ResponseEntity<Task> createTask(@RequestBody Task task) {
        Optional<User> userOptional = userRepository.findById(task.getUser().getId());
        Optional<Category> categoryOptional = categoryRepository.findById(task.getCategory().getId());

        if (userOptional.isEmpty() || categoryOptional.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        task.setUser(userOptional.get());
        task.setCategory(categoryOptional.get());

        Task savedTask = taskRepository.save(task);
        return ResponseEntity.ok(savedTask);
    }


    @PutMapping("/{id}")
    public ResponseEntity<Task> updateTask(@PathVariable Long id, @RequestBody Task updatedTask) {
        Optional<Task> taskOptional = taskRepository.findById(id);

        if (taskOptional.isPresent()) {
            Task task = taskOptional.get();


            task.setTitle(updatedTask.getTitle());
            task.setDescription(updatedTask.getDescription());
            task.setDueDate(updatedTask.getDueDate());
            task.setPriority(updatedTask.getPriority());
            task.setStatus(updatedTask.getStatus());

            Optional<User> userOptional = userRepository.findById(updatedTask.getUser().getId());
            Optional<Category> categoryOptional = categoryRepository.findById(updatedTask.getCategory().getId());

            if (userOptional.isEmpty() || categoryOptional.isEmpty()) {
                return ResponseEntity.badRequest().build();
            }

            task.setUser(userOptional.get());
            task.setCategory(categoryOptional.get());

            Task savedTask = taskRepository.save(task);
            return ResponseEntity.ok(savedTask);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        if (taskRepository.existsById(id)) {
            taskRepository.deleteById(id);
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
