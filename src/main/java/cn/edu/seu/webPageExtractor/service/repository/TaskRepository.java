package cn.edu.seu.webPageExtractor.service.repository;

import cn.edu.seu.webPageExtractor.core.TaskInfo;
import org.springframework.data.repository.CrudRepository;

public interface TaskRepository extends CrudRepository<TaskInfo,Integer> {
}
