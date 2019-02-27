package cn.edu.seu.webPageExtractor;

import cn.edu.seu.webPageExtractor.graph.service.GraphScoreService;
import cn.edu.seu.webPageExtractor.service.manage.DbTransferManager;
import cn.edu.seu.webPageExtractor.service.manage.TripleManager;
import cn.edu.seu.webPageExtractor.service.repository.TripleRepository;
import cn.edu.seu.webPageExtractor.util.StringUtil;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class Graph2DbTest {
    @Autowired
    private TripleManager tripleManager;
    @Autowired
    private GraphScoreService graphScoreService;

    @Test
    public void testDb() {
        tripleManager.transTriple2Db("/Users/jinweihao/学习/毕业论文/data/zhishime/baidubaike/baidubaike_categories.nt");
    }

    @Test
    public void spliteFile() throws IOException {
        LineIterator iterator = FileUtils.lineIterator(new File("/Users/jinweihao/学习/毕业论文/data/zhishime/baidubaike/baidubaike_infobox.nt"), "utf-8");
        List<String> codeline = new ArrayList<>();
        Integer count = 1;
        while (iterator.hasNext()) {
            String line = iterator.nextLine();
            //转码
            line = StringUtil.decodeString(line);
            //分割
            String[] words = line.split("> <");
            if (words.length >= 3) {
                String sub = words[0].replace("<", "").replace(">", "");
                String predict = words[1].replace("<", "").replace(">", "");
                String obj = words[2].replace(" .\t", "").replace(">", "");
                if (sub.length() < 255 && predict.length() < 255 && obj.length() < 255) {
                    codeline.add(sub + "\t" + predict + "\t" + obj);
                }
            }

            if (codeline.size() == 100000) {
                FileUtils.writeLines(new File("/Users/jinweihao/学习/毕业论文/data/zhishime/baidubaike/infobox/infobox" + count + ".txt"), "utf-8", codeline);
                codeline.clear();
                System.out.println("已经转存了" + count * 100000);
                count++;
            }
        }
        FileUtils.writeLines(new File("/Users/jinweihao/学习/毕业论文/data/zhishime/baidubaike/infobox/infobox" + count + ".txt"), "utf-8", codeline);
    }

    @Test
    public void graphCalculate() {
        graphScoreService.scoreCalculate("手机");
    }

    @Test
    public void htmlParse() throws Exception {
        List<String> context = new ArrayList<>();
        String fileString = FileUtils.readFileToString(new File("/Users/jinweihao/学习/毕业论文/data/detailPage/12082664.com/26687383858.html.html"), "utf-8");
        Integer codenum = fileString.length() - fileString.replace("\n", "").length();
        Document document = Jsoup.parse(fileString);
        Element detailBody = document.body();
        Integer linkNum = 0;
//        Elements specialElements = detailBody.getElementsByTag("table");
//        specialElements.addAll(detailBody.getElementsByTag("ul"));
//        specialElements.addAll(detailBody.getElementsByTag("ol"));
//        specialElements.addAll(detailBody.getElementsByTag("dl"));
//
//
//        for (Element specialEle : specialElements) {
//            loopElementGetSpecialContext(specialEle, context,linkNum,codeNum);
//        }
        loopElementGetSpecialContext(detailBody, context, linkNum);
        System.out.println("good");
    }

    private Integer loopElementGetSpecialContext(Element specialEle, List<String> contexts, Integer linkNum) {
        List<Node> nodes = specialEle.childNodes();
        if (nodes.size() == 0) {
            return linkNum;
        }
        for (Node node : nodes) {
            if (node.getClass().getName().contains("TextNode")) {
                String tempText = node.toString();
                tempText = tempText.replaceAll(" ", "");
                if (!tempText.isEmpty()) {
                    contexts.add(specialEle.text());
                }
                continue;
            }
            if (node.getClass().getName().contains("Comment") || node.getClass().getName().contains("DataNode")) {
                continue;
            }
            if (node.nodeName().equals("a")) {
                linkNum++;
            }
            linkNum += loopElementGetSpecialContext((Element) node, contexts, linkNum);
        }
        return linkNum;
    }

    @Autowired
    private TripleRepository tripleRepository;

    @Test
    public void dbqueryTest() {
        Long start = System.currentTimeMillis();
        Object num = tripleRepository.isParentCategoryNum("http://zhishi.me/baidubaike/category/学校", "http://zhishi.me/baidubaike/category/教育");
        System.out.println("查询花费了" + (System.currentTimeMillis() - start) + "结果为" + num);
    }

    @Autowired
    private DbTransferManager dbTransferManager;

    @Test
    public void mysql2Mongo() {
        for (int i = 0; i < 5000; i = i + 500) {
            try{
            if (i + 500 < 5000) {
                dbTransferManager.transferData(i, i + 500);
            }} catch (Exception e){
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }
}
