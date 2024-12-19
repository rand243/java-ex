package com.example.demoq.controller;

import com.example.demoq.model.Category;
import com.example.demoq.model.Task;
import com.example.demoq.model.User;
import com.example.demoq.service.CategoryService;
import com.example.demoq.service.TaskService;
import com.example.demoq.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/tasks")
public class TaskController {

    private final TaskService taskService;
    private final UserService userService;
    private final CategoryService categoryService;

    @Autowired
    public TaskController(TaskService taskService, UserService userService, CategoryService categoryService) {
        this.taskService = taskService;
        this.userService = userService;
        this.categoryService = categoryService;
    }

    @GetMapping
    public String listTasks(Model model) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userService.findByUsername(username);
        if (currentUser == null) {
            throw new RuntimeException("User not found");
        }
        List<Task> tasks = taskService.findByUserId(currentUser.getId());
        model.addAttribute("tasks", tasks);
        model.addAttribute("currentUser", currentUser);
        return "task-list";
    }

    @GetMapping("/create")
    public String showCreateTaskForm(Model model) {
        model.addAttribute("task", new Task());
        model.addAttribute("categories", categoryService.getAllCategories());
        LocalDate today = LocalDate.now();
        model.addAttribute("today", today);
        return "task-create";
    }

    @PostMapping("/create")
    public String createTask(@Valid @ModelAttribute Task task, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors() || (task.getDueDate() != null && task.getDueDate().isBefore(LocalDate.now()))) {
            if (task.getDueDate() != null && task.getDueDate().isBefore(LocalDate.now())) {
                model.addAttribute("dateError", "Due date cannot be in the past.");
            }
            model.addAttribute("categories", categoryService.getAllCategories());
            return "task-create";
        }

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userService.findByUsername(username);
        if (currentUser == null) {
            throw new RuntimeException("User not found");
        }
        task.setUser(currentUser);

        taskService.saveTask(task);
        return "redirect:/tasks";
    }

    @GetMapping("/edit/{id}")
    public String showEditTaskForm(@PathVariable("id") Long id, Model model) {
        Task task = taskService.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found"));
        model.addAttribute("task", task);
        model.addAttribute("categories", categoryService.findAll());
        return "task-edit";
    }

    @PostMapping("/edit/{id}")
    public String editTask(@PathVariable("id") Long id, @Valid @ModelAttribute Task task, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors() || (task.getDueDate() != null && task.getDueDate().isBefore(LocalDate.now()))) {
            if (task.getDueDate() != null && task.getDueDate().isBefore(LocalDate.now())) {
                model.addAttribute("dateError", "Due date cannot be in the past.");
            }
            model.addAttribute("categories", categoryService.findAll());
            return "task-edit";
        }

        Task existingTask = taskService.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found"));
        task.setUser(existingTask.getUser());
        task.setId(id);
        taskService.saveTask(task);
        return "redirect:/tasks";
    }

    @GetMapping("/delete/{id}")
    public String deleteTask(@PathVariable("id") Long id) {
        taskService.deleteTask(id);
        return "redirect:/tasks";
    }
}
