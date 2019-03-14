package cn.edu.seu.webPageExtractor;



import cn.edu.seu.webPageExtractor.graph.core.Resource;
import cn.edu.seu.webPageExtractor.graph.service.GraphScoreService;
import com.google.gson.Gson;
import org.apache.commons.io.FileUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.entity.ContentType;
import org.apache.http.nio.entity.NStringEntity;
import org.apache.http.util.EntityUtils;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.client.*;
import org.elasticsearch.script.ScriptType;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.io.IOException;
import java.util.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ElasticClientTest {

//    @Test
//    public void esClientTest(){
//        Map<String, String> params = Collections.emptyMap();
//        RestClient restClient = RestClient.builder(
//                new HttpHost("localhost", 9200,"http")).build();
//        Request request = new Request("GET","/test/_search");
//        String jsonString = "{\n" +
//                "\t\"query\":{\n" +
//                "\t\t\"bool\":{\n" +
//                "\t\t\t\"should\":[\n" +
//                "\t\t\t\t {\"match\" : {\n" +
//                "        \t\"categories.name\":\"手机\"\n" +
//                "          \n" +
//                "            \n" +
//                "        }},{\n" +
//                "        \t\"match\":{\n" +
//                "        \t\t\"properties.name\" : \"浙江\"\n" +
//                "        \t}\n" +
//                "        }\n" +
//                "\t\t\t\t]\n" +
//                "\t\t}\n" +
//                "\t}\n" +
//                "       \n" +
//                "}";
//        HttpEntity entity = new NStringEntity(jsonString,ContentType.APPLICATION_JSON);
//        try {
//            request.setEntity(entity);
//            Response response  = restClient.performRequest(request);
//
//            String responseBody = EntityUtils.toString(response.getEntity());
//            System.out.println("good");
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

//    @Test
//    public void highLevelQueryTest(){
//        RestHighLevelClient client = new RestHighLevelClient(
//                RestClient.builder(
//                        new HttpHost("localhost", 9200, "http")));
//
//        SearchTemplateRequest request = new SearchTemplateRequest();
//        request.setRequest(new SearchRequest("test"));
//
//        request.setScriptType(ScriptType.INLINE);
//        request.setScript("{\n" +
//                        "  \"query\": {\n" +
//                        "    \"bool\": {\n" +
//                        "      \"should\": [\n" +
//                        "        {\n" +
//                        "          \"match\": {\n" +
//                        "            \"categories.name\": \"手机\"\n" +
//                        "          }\n" +
//                        "        },\n" +
//                        "        {\n" +
//                        "          \"match\": {\n" +
//                        "            \"properties.name\": \"CPU型号\"\n" +
//                        "          }\n" +
//                        "        }\n" +
//                        "      ]\n" +
//                        "    }\n" +
//                        "  },\n" +
//                        "  \"highlight\":{\n" +
//                        "    \"pre_tags\":[\"\"],\n" +
//                        "    \"post_tags\":[\"\"],\n" +
//                        "    \"fields\":{\n" +
//                        "      \"categories.name\":{},\n" +
//                        "      \"properties.name\":{}\n" +
//                        "    }\n" +
//                        "  }\n" +
//                        "}"
//                );
//
//        Map<String, Object> scriptParams = new HashMap<>();
//        scriptParams.put("field1", "categories.name");
//        scriptParams.put("value1", "手机");
//        scriptParams.put("field2", "properties.name");
//        scriptParams.put("value2", "CPU型号");
//        request.setScriptParams(scriptParams);
//
//
//        try {
//            Gson gson =new Gson();
//            List<Resource> resourceList = new ArrayList<>();
//            SearchTemplateResponse response = client.searchTemplate(request, RequestOptions.DEFAULT);
//            if (response.getResponse()!=null && response.getResponse().getHits()!=null){
//                for (SearchHit hit :response.getResponse().getHits()){
//                    if (hit.getHighlightFields()!=null){
//                        HighlightField categorys = hit.getHighlightFields().get("categories.name");
//                        HighlightField propertys = hit.getHighlightFields().get("properties.name");
//
//
//                    }
//
//                    Resource resource = gson.fromJson(hit.getSourceAsString(),Resource.class);
//                    resourceList.add(resource);
//                }
//            }
//            System.out.println("good");
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//
//    }

    @Autowired
    private GraphScoreService graphScoreService;
    @Test
    public void contextCalulate() throws Exception{
        List<String> context = FileUtils.readLines(new File("/Users/jinweihao/workspace/webPageExtractor/src/main/resources/templates/contextTest.txt"),"utf-8");

        Float score = graphScoreService.contextDomainScoreCalculate(contextDeal(context),"手机");
        System.out.println("good");
    }
    private List<String> contextDeal(List<String> contexts){
        Set<String> result = new HashSet<>();
        for (String context:contexts){
            if (context.trim().length()!=1){
                result.add(context);
            }
        }
        return new ArrayList<>(result);
    }

    //JSONObject res = new JSONObject(responseBody);
    //JSONObject hit = (JSONObject) res.get("hits");
    //JSONArray hits = (JSONArray)hit.get("hits");
    //JSONObject resObject = (JSONObject) hits.get(0);
    //String resString = resObject.get("_source").toString();
    //Gson gson = new Gson();
    //gson.fromJson(resString,Resource.class);
}
