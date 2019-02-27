package cn.edu.seu.webPageExtractor.graph.service.impl;

import cn.edu.seu.webPageExtractor.graph.Triple;
import cn.edu.seu.webPageExtractor.graph.service.GraphQueryService;
import cn.edu.seu.webPageExtractor.graph.util.QueryGenerateUtil;
import cn.edu.seu.webPageExtractor.util.StringUtil;
import com.franz.agraph.repository.*;
import org.eclipse.rdf4j.query.BindingSet;
import org.eclipse.rdf4j.query.QueryLanguage;
import org.eclipse.rdf4j.query.TupleQueryResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Service
public class GraphQueryServiceImpl implements GraphQueryService {
    private Logger logger = LoggerFactory.getLogger(GraphQueryServiceImpl.class);
    @Value("${graoh.server.url}")
    private String SERVER_URL;
    @Value("${graph.server.catalog.id}")
    private String CATALOG_ID;
    @Value("${graph.server.repository.id}")
    private String REPOSITORY_ID;
    @Value("${graph.server.username}")
    private String USERNAME;
    @Value("${graph.server.password}")
    private String PASSWORD;

    private AGRepositoryConnection graphConnect;
//    @PostConstruct
    private void connectAg() {
        AGServer server = new AGServer(SERVER_URL, USERNAME, PASSWORD);
        AGCatalog catalog = server.getCatalog(CATALOG_ID);
        AGRepository myRepository = catalog.createRepository(REPOSITORY_ID);
        myRepository.initialize();
        graphConnect = myRepository.getConnection();
        logger.info("已经连接图数据库");
    }

    @Override
    public Integer countAllInstanceNum(String categoryName) {
        StringBuilder queryString = new StringBuilder();
        queryString.append("select (COUNT (?resource) AS ?s) where {?resource ");
        queryString.append("<http://zhishi.me/ontology/category> ");
        queryString.append(QueryGenerateUtil.categoryUriGenerator(categoryName,"baidubaike"));
        queryString.append("}");

        List<Triple> triples = queryAG(queryString.toString());
        return Integer.parseInt(triples.get(0).getSubject());
    }

    @Override
    public List<Triple> queryAllPPVofCategory(String categoryName) {
       //?p为属性 ?o为属性值
        StringBuilder queryString = new StringBuilder();
        queryString.append("select ?p ?o where {?resource <http://zhishi.me/ontology/category> ");
        queryString.append(QueryGenerateUtil.categoryUriGenerator(categoryName,"baidubaike"));
        queryString.append(". ");
        queryString.append("?resource ?p ?o}");

        List<Triple> result = queryAG(queryString.toString());
        result.removeIf(triple -> !triple.getPredict().contains("property"));

        return result;
    }

    @Override
    public List<Triple> queryAllInstanceAndCategoryOfProperty(String proName) {
        //?o 为category ?s 为resource
        StringBuilder queryString = new StringBuilder();
        queryString.append("select ?s ?o where {?s ");
        queryString.append(QueryGenerateUtil.propertyUriGenerator(proName,"baidubaike"));
        queryString.append(" ?pv. ?s <http://zhishi.me/ontology/category> ?o.}");

        Triple triple = new Triple();
        return queryAG(queryString.toString());
    }

    @Override
    public List<Triple> queryAllInstanceAndCategoryOfPV(String proVName) {
        //?o 为category ?s 为resource
        StringBuilder queryString = new StringBuilder();
        queryString.append("select ?s ?o where {?s ?p ");
        queryString.append(proVName);
        queryString.append(". ?s <http://zhishi.me/ontology/category> ?o.}");

        Triple triple = new Triple();
        return queryAG(queryString.toString());
    }

    public Integer querySameInstanceBetweenCategory(String categoryName,String anotherCategoryName){
        StringBuilder queryString = new StringBuilder();
        queryString.append("select (COUNT (?resource) AS ?s) where {?resource  <http://zhishi.me/ontology/category> ");
        queryString.append(QueryGenerateUtil.categoryUriGenerator(categoryName,"baidubaike"));
        queryString.append(". ?resource  <http://zhishi.me/ontology/category> ");
        queryString.append(QueryGenerateUtil.categoryUriGenerator(anotherCategoryName,"baidubaike"));
        queryString.append(".}");

        Triple triple = new Triple();
        List<Triple> triples = queryAG(queryString.toString());
        return Integer.parseInt(triples.get(0).getSubject());
    }




    private List<Triple> queryAG( String queryString) {
        List<Triple> resultTriple = new ArrayList<>();
        AGTupleQuery tupleQuery = graphConnect.prepareTupleQuery(QueryLanguage.SPARQL, queryString);
        TupleQueryResult result = tupleQuery.evaluate();
        try {
            while (result.hasNext()) {

                Triple temp = new Triple();
                BindingSet bindingSet = result.next();
                org.eclipse.rdf4j.model.Value s = bindingSet.getValue("s");
                org.eclipse.rdf4j.model.Value p = bindingSet.getValue("p");
                org.eclipse.rdf4j.model.Value o = bindingSet.getValue("o");
                if (s != null) {
                    temp.setSubject(StringUtil.decodeString(s.stringValue()));
                }
                if (o != null) {
                    temp.setObject(StringUtil.decodeString(o.stringValue()));
                }
                if (p != null) {
                    temp.setPredict(StringUtil.decodeString(p.stringValue()));
                }
                resultTriple.add(temp);
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        }

        result.close();
        return resultTriple;
    }
}
