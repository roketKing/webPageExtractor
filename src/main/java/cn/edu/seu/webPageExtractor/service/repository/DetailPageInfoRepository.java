package cn.edu.seu.webPageExtractor.service.repository;

import cn.edu.seu.webPageExtractor.core.DetailPageInfo;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface DetailPageInfoRepository extends CrudRepository<DetailPageInfo,Integer> {
    /**
     * 通过任务id找详情页
     * @param taskId
     * @return
     */
    List<DetailPageInfo> findDetailPageInfosByTaskId(Integer taskId);

    /**
     * 通过列表页id找详情页
     * @param listId
     * @return
     */
    List<DetailPageInfo> findDetailPageInfosByListId(Integer listId);
}
