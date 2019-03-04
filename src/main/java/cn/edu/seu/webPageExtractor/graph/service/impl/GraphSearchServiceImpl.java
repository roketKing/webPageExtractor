package cn.edu.seu.webPageExtractor.graph.service.impl;

import cn.edu.seu.webPageExtractor.graph.service.GraphSearchService;
import org.apache.http.HttpHost;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.script.ScriptType;
import org.elasticsearch.script.mustache.SearchTemplateRequest;
import org.elasticsearch.script.mustache.SearchTemplateResponse;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class GraphSearchServiceImpl implements GraphSearchService {
    private RestHighLevelClient esClient;

    @PostConstruct
    public void initClient() {
        esClient = new RestHighLevelClient(
                RestClient.builder(
                        new HttpHost("localhost", 9200, "http")));
    }

    @Override
    public String searchByWord(String categoryName, String word) {
        SearchTemplateRequest request = new SearchTemplateRequest();
        request.setRequest(new SearchRequest("test"));

        request.setScriptType(ScriptType.INLINE);
        request.setScript("{\n" +
                "  \"query\": {\n" +
                "    \"bool\": {\n" +
                "      \"should\": [\n" +
                "        {\n" +
                "          \"match\": {\n" +
                "            \"{{field1}}\": \"{{value1}}\"\n" +
                "          }\n" +
                "        },\n" +
                "        {\n" +
                "          \"match\": {\n" +
                "            \"{{field2}}\": \"{{value2}}\"\n" +
                "          }\n" +
                "        }\n" +
                "      ]\n" +
                "    }\n" +
                "  },\n" +
                "  \"highlight\":{\n" +
                "    \"pre_tags\":[\"\"],\n" +
                "    \"post_tags\":[\"\"],\n" +
                "    \"fields\":{\n" +
                "      \"categories.name\":{},\n" +
                "      \"properties.name\":{}\n" +
                "    }\n" +
                "  }\n" +
                "}"
        );

        Map<String, Object> scriptParams = new HashMap<>();
        scriptParams.put("field1", "categories.name");
        scriptParams.put("value1", categoryName);
        scriptParams.put("field2", "properties.name");
        scriptParams.put("value2", word);
        request.setScriptParams(scriptParams);

        return sendRequest(request, categoryName);
    }

    private String sendRequest(SearchTemplateRequest request, String categoryName) {
        String result = null;
        try {
            SearchTemplateResponse response = esClient.searchTemplate(request, RequestOptions.DEFAULT);
            if (response.getResponse() != null && response.getResponse().getHits() != null) {
                for (SearchHit hit : response.getResponse().getHits()) {
                    if (hit.getHighlightFields() != null) {
                        List<String> categorySet = parseHighlightFields(hit.getHighlightFields().get("categories.name"));
                        //领域检查...
                        if (categorySet.contains(categoryName)) {
                            //只返回在匹配领域得分最高的
                            List<String> propertySet = parseHighlightFields(hit.getHighlightFields().get("properties.name"));
                            if (propertySet.size()!=0){
                                result = propertySet.get(0);
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

    /**
     * 对高亮的词语进行解析
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
