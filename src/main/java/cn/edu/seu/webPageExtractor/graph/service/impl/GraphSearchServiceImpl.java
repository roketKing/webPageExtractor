package cn.edu.seu.webPageExtractor.graph.service.impl;

import cn.edu.seu.webPageExtractor.graph.core.Property;
import cn.edu.seu.webPageExtractor.graph.core.Resource;
import cn.edu.seu.webPageExtractor.graph.service.GraphSearchService;
import com.google.gson.Gson;
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
    public String searchByWord(Map<String, Object> scriptParams) {
        SearchTemplateRequest request = new SearchTemplateRequest();
        request.setRequest(new SearchRequest("test"));
        String highlight = "  \"highlight\":{\n" +
                "  \t\"pre_tags\":[\"\"],\n" +
                "  \t\"post_tags\":[\"\"],\n" +
                "  \t\"fields\":{\n" +
                "  \t\t\"" + scriptParams.get("field1")+
                "\":{},\n" +
                "  \t\t\"" + scriptParams.get("field2")+
                "\":{}\n" +
                "  \t}\n" +
                "  \t\n" +
                "  }";
        request.setScriptType(ScriptType.INLINE);
        request.setScript("{\n" +
                "\"query\": \n" +
                "  {\n" +
                "    \"bool\": {\n" +
                "      \"should\": [\n" +
                "        {\n" +
                "          \"match\": {\n" +
                "          \"{{field1}}\": {\n" +
                "          \t\t\"query\":\"{{value1}}\",\n" +
                "          \t\t\"analyzer\":\"query_ansj\"\n" +
                "          \t}\n" +
                "          }\n" +
                "        },\n" +
                "        {\n" +
                "          \"match\": {\n" +
                "            \"{{field2}}\": {\n" +
                "          \t\t\"query\":\"{{value2}}\",\n" +
                "          \t\t\"analyzer\":\"query_ansj\"\n" +
                "          \t}\n" +
                "          }\n" +
                "        }\n" +
                "      ]\n" +
                "    }\n" +
                "    \n" +
                "  },\n" + highlight +
                "  \t\n" +
                "}");

        request.setScriptParams(scriptParams);

        return sendRequest(request, scriptParams);
    }

    private String sendRequest(SearchTemplateRequest request, Map<String, Object> params) {
        String result = null;
        try {
            SearchTemplateResponse response = esClient.searchTemplate(request, RequestOptions.DEFAULT);
            if (response.getResponse() != null && response.getResponse().getHits() != null) {
                Gson gson = new Gson();
                for (SearchHit hit : response.getResponse().getHits()) {
                    String source = hit.getSourceAsString();
                    Resource resource = gson.fromJson(source, Resource.class);
                    if (hit.getHighlightFields() != null) {
                        String categoryName = (String) params.get("field1");
                        List<String> categorySet = parseHighlightFields(hit.getHighlightFields().get(categoryName));
                        //领域检查...
                        if (categorySet.contains(categoryName)) {
                            result = getResultFromResponse(hit, params, resource);
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    private String getResultFromResponse(SearchHit hit, Map<String, Object> params, Resource resource) {
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
                    result = property.getPropertyValue();
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
