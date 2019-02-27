package cn.edu.seu.webPageExtractor.service.repository;

import cn.edu.seu.webPageExtractor.core.ListPageInfo;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ListPageInfoRepository extends CrudRepository<ListPageInfo,Integer> {
    List<ListPageInfo> findListPageInfosByTaskId(Integer taskId);
}
