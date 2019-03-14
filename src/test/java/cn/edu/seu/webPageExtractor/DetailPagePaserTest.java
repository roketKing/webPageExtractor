package cn.edu.seu.webPageExtractor;

import cn.edu.seu.webPageExtractor.core.page.DetailPage;
import cn.edu.seu.webPageExtractor.core.page.feature.Block;
import cn.edu.seu.webPageExtractor.service.DetailPageFeatureService;
import cn.edu.seu.webPageExtractor.service.PageCrawlService;
import cn.edu.seu.webPageExtractor.service.PageDivideService;
import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@RunWith(SpringRunner.class)
@SpringBootTest
public class DetailPagePaserTest {
    @Autowired
    PageCrawlService pageCrawlService;

    @Autowired
    private PageDivideService pageDivideService;

    @Autowired
    private DetailPageFeatureService detailPageFeatureService;

    @Test
    public void pageDivide(){
        WebDriver driver = pageCrawlService.getDriver();
        //baidu
        //String url ="https://www.baidu.com/s?ie=utf-8&f=3&rsv_bp=0&rsv_idx=1&tn=baidu&wd=%E6%9C%AB%E6%97%A5%E5%AD%A4%E8%88%B0%E7%AC%AC%E4%BA%94%E5%AD%A3&rsv_pq=89727747000299f4&rsv_t=5a3agj9pGdeVKF5ncfVW%2FGM1FHyYZNjHXjJ08j4c0vK%2F5o9H0yXw3jGEz%2BI&rqlang=cn&rsv_enter=1&rsv_sug3=1&rsv_sug1=1&rsv_sug7=001&rsv_sug2=1&rsp=0&rsv_sug9=es_1_1&rsv_sug4=1562&rsv_sug=9";
        //jd
        //String url="https://item.jd.com/100002293114.html";
        //suning
        //String url="https://product.suning.com/0000000000/10638773027.html?safp=d488778a.13701.productWrap.28&safc=prd.3.ssdsn_pic02-1_jz";
        //amazon
        //String url="https://www.amazon.cn/dp/B07BR9GZ6X/ref=sr_1_6?ie=UTF8&qid=1552485312&sr=8-6&keywords=%E5%8D%8E%E4%B8%BA%E6%89%8B%E6%9C%BA&th=1";
        //tmall
        String url="https://detail.tmall.com/item.htm?spm=a230r.1.14.11.86cbcec2fsZzXh&id=579718698456&cm_id=140105335569ed55e27b&abbucket=12&sku_properties=10004:385316259;5919063:6536025";
        DetailPage detailPage = pageCrawlService.getDetailPage(url,11,11,driver);
        Block block = detailPage.getBlock();
        driver.manage().timeouts().implicitlyWait(1, TimeUnit.SECONDS);
//        pageDivideService.detailPageParser(block);
        pageDivideService.detailPageDivide(detailPage);
        List<Block> blocks = pageDivideService.getNotDividedBlock();

        for (int i =0;i<blocks.size();i++){
            long start = System.currentTimeMillis();
            detailPageFeatureService.getFeatureFromBlock(blocks.get(i),"手机");
            System.out.println("已经进行到"+i+"花费了"+(System.currentTimeMillis()-start));

        }
        printNode();

        System.out.println("good");

    }

//    List<Block> temp = ((PageDivideServiceImpl) pageDivideService).notDividedBlock;
//for (int i = 0; i < temp.size(); i++) {
//        System.out.println("第"+i+"个\n");
//
//        System.out.println(temp.get(i).getNode().getElement().getText());
//    }
     private void printNode(){
             List<Block> temp = pageDivideService.getNotDividedBlock();
         List<String> result = new ArrayList<>();
         for (int i = 0; i < temp.size(); i++) {
                 Block tempBlock = temp.get(i);
                 StringBuilder builder = new StringBuilder();
                 builder.append(tempBlock.getDomainScore());
                 builder.append(",");
                 builder.append(tempBlock.getLinkNumber());
                 builder.append(",");
                 builder.append(tempBlock.getContextDensity());
                 builder.append(",");
                 builder.append(tempBlock.getContext()==null?0:tempBlock.getContext().size());
                 builder.append(",");
                 builder.append(tempBlock.getContextStrLength());
             result.add(builder.toString());
             }
         try {
             FileUtils.writeLines(new File("/Users/jinweihao/workspace/webPageExtractor/src/main/resources/templates/detailTest4.csv"),result);
         } catch (IOException e) {
             e.printStackTrace();
         }

     }
}
