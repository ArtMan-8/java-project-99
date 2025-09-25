package hexlet.code.specification;

import hexlet.code.model.Task;
import org.springframework.data.jpa.domain.Specification;
import hexlet.code.dto.Task.TaskFilterDTO;

public class TaskSpecification {
    public static Specification<Task> buildSpecification(TaskFilterDTO filter) {
        return byTitleCont(filter.getTitleCont())
            .and(byAssigneeId(filter.getAssigneeId()))
            .and(byStatus(filter.getStatus()))
            .and(byLabelId(filter.getLabelId()));
    }

    public static Specification<Task> byTitleCont(String titleCont) {
        return (root, query, criteriaBuilder) -> {
            if (titleCont == null || titleCont.isEmpty()) {
                return criteriaBuilder.conjunction();
            }

            return criteriaBuilder.like(
                criteriaBuilder.lower(root.get("title")),
                "%" + titleCont.toLowerCase() + "%"
            );
        };
    }

    public static Specification<Task> byAssigneeId(Long assigneeId) {
        return (root, query, criteriaBuilder) -> {
            if (assigneeId == null) {
                return criteriaBuilder.conjunction();
            }

            return criteriaBuilder.equal(root.get("assignee").get("id"), assigneeId);
        };
    }

    public static Specification<Task> byStatus(String status) {
        return (root, query, criteriaBuilder) -> {
            if (status == null || status.isEmpty()) {
                return criteriaBuilder.conjunction();
            }

            return criteriaBuilder.equal(root.get("taskStatus").get("slug"), status);
        };
    }

    public static Specification<Task> byLabelId(Long labelId) {
        return (root, query, criteriaBuilder) -> {
            if (labelId == null) {
                return criteriaBuilder.conjunction();
            }

            return criteriaBuilder.equal(root.join("labels").get("id"), labelId);
        };
    }
}
