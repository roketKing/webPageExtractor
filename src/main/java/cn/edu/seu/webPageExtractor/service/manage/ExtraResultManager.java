package cn.edu.seu.webPageExtractor.service.manage;

import cn.edu.seu.webPageExtractor.core.ExtraResultInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ExtraResultManager {
    @Autowired
    MongoTemplate mongoTemplate;

    public List<ExtraResultInfo> queryResultByTaskId(String taskId){
        Query query = new Query(Criteria.where("taskId").is(taskId));
        List<ExtraResultInfo> resultInfoList = mongoTemplate.find(query,ExtraResultInfo.class,"webResult");
        return resultInfoList;
    }

    public boolean saveResult(List<ExtraResultInfo> infos){
        mongoTemplate.insert(infos,"webResult");
        return true;
    }
}
