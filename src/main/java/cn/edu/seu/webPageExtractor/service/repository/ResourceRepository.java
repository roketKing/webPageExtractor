package cn.edu.seu.webPageExtractor.service.repository;

import cn.edu.seu.webPageExtractor.graph.core.Resource;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ResourceRepository extends MongoRepository<Resource,String> {
    List<Resource> findResourceById(String id);
}
