package hexlet.code.utils;

import hexlet.code.model.Label;
import hexlet.code.model.Task;
import hexlet.code.model.TaskStatus;
import hexlet.code.model.User;
import hexlet.code.repository.LabelRepository;
import hexlet.code.repository.TaskRepository;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.repository.UserRepository;
import net.datafaker.Faker;
import net.datafaker.providers.base.Text;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
public class TestDataFactory {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TaskStatusRepository statusRepository;

    @Autowired
    private LabelRepository labelRepository;

    @Autowired
    private TaskRepository taskRepository;

    private final Faker faker = new Faker();


    public void cleanAll() {
        taskRepository.deleteAll();
        userRepository.deleteAll();
        statusRepository.deleteAll();
        labelRepository.deleteAll();
    }

    public User createUser() {
        User user = new User();
        user.setFirstName(faker.name().firstName());
        user.setLastName(faker.name().lastName());
        user.setEmail(faker.internet().emailAddress());
        user.setPassword(faker.text().text(Text.TextSymbolsBuilder.builder()
                .len(8)
                .with("ABCDEFGHIJKLMNOPQRSTUVWXYZ", 2)
                .with("0123456789", 3)
                .build()));
        return userRepository.save(user);
    }

    public TaskStatus createTaskStatus() {
        TaskStatus status = new TaskStatus();
        status.setName(faker.lorem().word());
        status.setSlug(faker.lorem().word().toLowerCase().replace(" ", "-") + "-" + faker.number().randomNumber());
        return statusRepository.save(status);
    }

    public Label createLabel() {
        Label label = new Label();
        label.setName(faker.lorem().word());
        return labelRepository.save(label);
    }

    public Task createTask(User assignee, TaskStatus status, Set<Label> labels) {
        Task task = new Task();
        task.setTitle("Test Task " + faker.number().randomNumber());
        task.setContent("Test content " + faker.lorem().sentence(2));
        task.setIndex(faker.number().numberBetween(1, 100));
        task.setAssignee(assignee);
        task.setTaskStatus(status);
        task.setLabels(new HashSet<>(labels));
        return taskRepository.save(task);
    }

    public Task createTaskWithDependencies() {
        User user = createUser();
        TaskStatus status = createTaskStatus();
        Label label = createLabel();
        return createTask(user, status, Set.of(label));
    }
}
