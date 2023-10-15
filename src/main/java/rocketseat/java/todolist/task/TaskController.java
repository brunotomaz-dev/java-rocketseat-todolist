package rocketseat.java.todolist.task;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import rocketseat.java.todolist.utils.Utils;

@RestController
@RequestMapping("/tasks")
public class TaskController {

    @Autowired
    private ITaskRepository taskRepository;

    @PostMapping("/")
    public ResponseEntity create(@RequestBody TaskModel taskModel, HttpServletRequest request) {
        var idUser = (UUID) request.getAttribute("userId");
        taskModel.setUserId(idUser);

        var startAt = taskModel.getStartAt();
        var endAt = taskModel.getEndAt();
        var currentDate = LocalDateTime.now();

        if (currentDate.isAfter(startAt) || currentDate.isAfter(endAt)) {
            return ResponseEntity.badRequest().body("A data de início/término não pode ser menor que a data atual");
        }

        if (startAt.isAfter(endAt)) {
            return ResponseEntity.badRequest().body("A data de início não pode ser maior que a data de término");
        }

        var task = this.taskRepository.save(taskModel);
        return ResponseEntity.ok(task);
    }

    @GetMapping("/")
    public List<TaskModel> list(HttpServletRequest request) {
        var idUser = (UUID) request.getAttribute("userId");
        var tasks = this.taskRepository.findByUserId(idUser);
        return tasks;
    }

    // No Put Precisa passar todos os dados da tarefa, então foi usado o utils para
    // copiar os dados e não precisar passar todos dados
    @PutMapping("/{id}")
    public ResponseEntity update(@RequestBody TaskModel taskModel, @PathVariable UUID id, HttpServletRequest request) {

        var task = this.taskRepository.findById(id).orElse(null);
        if (task == null) {
            return ResponseEntity.internalServerError().body("Task não encontrada");
        }

        var idUser = request.getAttribute("userId");
        if (!task.getUserId().equals(idUser)) {
            return ResponseEntity.badRequest().body("Usuário sem autorização");
        }

        Utils.copyNonNullProperties(taskModel, task);

        var updatedTask = this.taskRepository.save(task);

        return ResponseEntity.ok(updatedTask);

    }
}
