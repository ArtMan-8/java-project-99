package hexlet.code.util;

import hexlet.code.model.Task;
import hexlet.code.model.TaskStatus;
import hexlet.code.model.User;
import hexlet.code.repository.TaskRepository;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements ApplicationRunner {
    private final UserRepository userRepository;
    private final TaskStatusRepository taskStatusRepository;
    private final TaskRepository taskRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        initDefaultUsers();
        initDefaultTaskStatuses();
        initDefaultTasks();
    }

    private void initDefaultUsers() {
        if (userRepository.findByEmail("hexlet@example.com").isEmpty()) {
            User admin = new User();
            admin.setEmail("hexlet@example.com");
            admin.setFirstName("Admin");
            admin.setLastName("Admin");
            admin.setPassword(passwordEncoder.encode("qwerty"));
            userRepository.save(admin);
        }
    }

    private void initDefaultTaskStatuses() {
        String[][] statuses = {
            {"draft", "Черновик"},
            {"to_review", "На рассмотрении"},
            {"to_be_fixed", "Требует доработки"},
            {"to_publish", "Готов к публикации"},
            {"published", "Опубликовано"}
        };

        for (String[] status : statuses) {
            String slug = status[0];
            String name = status[1];

            if (taskStatusRepository.findBySlug(slug).isEmpty()) {
                TaskStatus taskStatus = new TaskStatus();
                taskStatus.setSlug(slug);
                taskStatus.setName(name);
                taskStatusRepository.save(taskStatus);
            }
        }
    }

    private void initDefaultTasks() {
        if (taskRepository.count() == 0) {
            User user = userRepository.findByEmail("hexlet@example.com").orElse(null);
            TaskStatus status = taskStatusRepository.findBySlug("draft").orElse(null);

            if (user != null && status != null) {
                Task task = new Task();
                task.setTitle("Test Task");
                task.setContent("Test content");
                task.setIndex(1);
                task.setAssignee(user);
                task.setTaskStatus(status);
                taskRepository.save(task);
            }
        }
    }
}
