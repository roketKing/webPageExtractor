package cn.edu.seu.webPageExtractor.graph.service.impl;

import cn.edu.seu.webPageExtractor.graph.service.GraphQueryService;
import cn.edu.seu.webPageExtractor.graph.service.GraphScoreService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;


@RunWith(SpringRunner.class)
@SpringBootTest
public class GraphQueryByMongoDbImplTest {

    @Autowired
    private GraphQueryService graphQueryService;

    @Autowired
    private GraphScoreService graphScoreService;

    @Test
    public void countAllInstanceNum() {
    }

    @Test
    public void queryAllPPVofCategory() {
    }

    @Test
    public void queryAllInstanceAndCategoryOfProperty() {
    }

    @Test
    public void queryAllInstanceAndCategoryOfPV() {
    }

    @Test
    public void querySameInstanceBetweenCategory() {
    }

    @Test
    public void scoreTest(){
        graphScoreService.scoreCalculate("手机");
    }
}