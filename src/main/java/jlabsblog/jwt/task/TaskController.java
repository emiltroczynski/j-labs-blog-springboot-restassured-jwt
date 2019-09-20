package jlabsblog.jwt.task;

import java.util.List;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/tasks")
public class TaskController {
  private TaskRepository taskRepository;

  public TaskController(TaskRepository taskRepository) {
    this.taskRepository = taskRepository;
  }

  @GetMapping
  public List<Task> getTask() {
    return taskRepository.findAll();
  }

  @PostMapping
  public void addTask(@RequestBody Task task) {
    taskRepository.save(task);
  }

  @PutMapping("/{id}")
  public void editTask(@PathVariable long id, @RequestBody Task task) {
    Task existingTask = taskRepository.findById(id).get();
    existingTask.setDescription(task.getDescription());
    taskRepository.save(existingTask);
  }

  @DeleteMapping("/{id}")
  public void deleteTask(@PathVariable long id) {
    Task existingTask = taskRepository.findById(id).get();
    taskRepository.delete(existingTask);
  }
}

