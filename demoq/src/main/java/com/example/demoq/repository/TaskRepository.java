package com.example.demoq.repository;

import com.example.demoq.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByUserId(Long userId);

    List<Task> findByUserIdAndStatus(Long userId, String status);

    List<Task> findByCategoryId(Long categoryId);
}

