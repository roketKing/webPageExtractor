package cn.edu.seu.webPageExtractor.graph.service.impl;

import cn.edu.seu.webPageExtractor.graph.core.Property;
import cn.edu.seu.webPageExtractor.graph.core.Resource;
import cn.edu.seu.webPageExtractor.graph.service.GraphSearchService;
import com.google.gson.Gson;
import org.apache.http.HttpHost;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class GraphSearchServiceImpl implements GraphSearchService {
    private RestHighLevelClient esClient;

    @PostConstruct
    public void initClient() {
        RestClient lowLevelRestClient = RestClient.builder(
                new HttpHost("localhost", 9200, "http")).build();
        esClient = new RestHighLevelClient(lowLevelRestClient);
    }

    @Override
    public String searchByWord(Map<String, String> scriptParams) {
        SearchRequest searchRequest = new SearchRequest("test");

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        MatchQueryBuilder firstFieldMatchQuery = QueryBuilders.matchQuery(scriptParams.get("field1"),scriptParams.get("value1")).analyzer("query_ansj");
        MatchQueryBuilder secondFieldMatchQuery = QueryBuilders.matchQuery(scriptParams.get("field2"),scriptParams.get("value2")).analyzer("query_ansj");

        BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
        boolQueryBuilder.must(firstFieldMatchQuery);
        boolQueryBuilder.must(secondFieldMatchQuery);

        searchSourceBuilder.query(boolQueryBuilder);

        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.preTags("");
        highlightBuilder.postTags("");
        HighlightBuilder.Field firstHighlightTitle =
                new HighlightBuilder.Field(scriptParams.get("field1"));
        HighlightBuilder.Field secondHighlightTitle =
                new HighlightBuilder.Field(scriptParams.get("field2"));

        highlightBuilder.field(firstHighlightTitle);
        highlightBuilder.field(secondHighlightTitle);


        searchSourceBuilder.highlighter(highlightBuilder);

        searchRequest.source(searchSourceBuilder);

        return sendRequest(searchRequest, scriptParams);
    }

    private String sendRequest(SearchRequest request, Map<String, String> params) {
        String result = null;
        try {
            SearchResponse searchResponse = esClient.search(request);
            if (searchResponse != null && searchResponse.getHits() != null) {
                Gson gson = new Gson();
                for (SearchHit hit : searchResponse.getHits()) {
                    String source = hit.getSourceAsString();
                    Resource resource = gson.fromJson(source, Resource.class);
                    if (hit.getHighlightFields() != null) {
                        String categoryName = params.get("value1");
                        List<String> categorySet = parseHighlightFields(hit.getHighlightFields().get(params.get("field1")));
                        //领域检查...
                        if (categorySet.contains(categoryName)) {
                            result = getResultFromResponse(hit, params, resource);
                            if (result!=null){
                                break;
                            }
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    private String getResultFromResponse(SearchHit hit, Map<String, String> params, Resource resource) {
        String result = null;
        //只返回在匹配领域得分最高的
        String field2 = (String) params.get("field2");
        List<String> propertySet = parseHighlightFields(hit.getHighlightFields().get(field2));
        //是属性
        if (propertySet.size() != 0 && field2.equals("properties.name")) {
            result = propertySet.get(0);
            return result;
        } else if (propertySet.size() != 0 && field2.equals("properties.propertyValue")) {
            //是属性值
            result = propertySet.get(0);
            List<Property> properties = resource.getProperties();
            for (Property property : properties) {
                if (property.getPropertyValue().equals(result)) {
                    result = property.getName();
                    return result;
                }
            }
        }
        return result;
    }

    /**
     * 对高亮的词语进行解析
     *
     * @param field
     * @return
     */
    private List<String> parseHighlightFields(HighlightField field) {
        List<String> fields = new ArrayList<>();
        if (field != null && field.getFragments() != null) {
            for (int i = 0; i < field.getFragments().length; i++) {
                String temp = field.getFragments()[i].string();
                fields.add(temp);
            }
        }
        return fields;
    }
}
