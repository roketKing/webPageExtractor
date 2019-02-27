package cn.edu.seu.webPageExtractor.graph.service.impl;

import cn.edu.seu.webPageExtractor.graph.Triple;
import cn.edu.seu.webPageExtractor.graph.core.Resource;
import cn.edu.seu.webPageExtractor.graph.service.GraphQueryService;
import org.apache.jena.atlas.json.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.TypedAggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class GraphQueryByMongoDbImpl implements GraphQueryService {
    @Autowired
    private MongoTemplate mongoTemplate;

    //db.getCollection("resource").find({"categories.name":"手机"}).count()
    @Override
    public Integer countAllInstanceNum(String categoryName) {
        Query query = new Query(Criteria.where("categories.name").is(categoryName));
        Long insNum = mongoTemplate.count(query, Resource.class);
        return Integer.parseInt(insNum.toString());
    }

    @Override
    public List<Triple> queryAllPPVofCategory(String categoryName) {
        List<String> properties = new ArrayList<>();

        TypedAggregation<Resource> aggregation = Aggregation.newAggregation(Resource.class,
                Aggregation.match(Criteria.where("categories.name").is(categoryName)),
                Aggregation.project().and("properties.name").as("name"),
                Aggregation.unwind("name"),
                Aggregation.group("name"));
        AggregationResults<String> results = mongoTemplate.aggregate(aggregation,"resource",String.class);
        for(String res:results.getMappedResults()){
            properties.add(String.valueOf(JSON.parse(res).get("_id")).replace("\"",""));
        }

        return null;
    }

    @Override
    public List<Triple> queryAllInstanceAndCategoryOfProperty(String proName) {
        return null;
    }

    @Override
    public List<Triple> queryAllInstanceAndCategoryOfPV(String proVName) {
        return null;
    }

    //db.getCollection("resource").find({$and:[{"categories.name":"手机"},{"categories.name":"科技产品"}]}).count()
    @Override
    public Integer querySameInstanceBetweenCategory(String categoryName, String anotherCategoryName) {
        Query query = new Query(Criteria.where("categories.name").is(categoryName).and("categories.name").is(anotherCategoryName));
        Long sameInsNum = mongoTemplate.count(query, Resource.class);
        return Integer.parseInt(sameInsNum.toString());
    }
}
