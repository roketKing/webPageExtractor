package cn.edu.seu.webPageExtractor.service.impl;


import cn.edu.seu.webPageExtractor.constants.FileLocationEnum;
import cn.edu.seu.webPageExtractor.core.DetailPageInfo;
import cn.edu.seu.webPageExtractor.core.ListPageInfo;
import cn.edu.seu.webPageExtractor.core.page.DetailPage;
import cn.edu.seu.webPageExtractor.core.page.ListPage;
import cn.edu.seu.webPageExtractor.core.page.Node;
import cn.edu.seu.webPageExtractor.service.PageCrawlService;
import cn.edu.seu.webPageExtractor.service.manage.DetailPageInfoManager;
import cn.edu.seu.webPageExtractor.service.manage.ListPageInfoManager;
import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.seg.common.Term;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
public class PageCrawlServiceImpl implements PageCrawlService {
    Logger logger = LoggerFactory.getLogger(PageCrawlServiceImpl.class);

    @Autowired
    private ListPageInfoManager listPageInfoManager;
    @Autowired
    private DetailPageInfoManager detailPageInfoManager;

    public WebDriver getDriver() {
        System.setProperty("webdriver.chrome.driver", "/usr/local/bin/chromedriver");
        WebDriver driver = new ChromeDriver();
        driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
        return driver;
    }

    @Override
    public Node getWebPageByLink(String link, WebDriver driver) {
        try{
            driver.get(link);
            Node node = new Node();
            node.setElement(driver.findElement(By.xpath("/html/body")));
            node.setLink(link);
            return node;
        }catch (Exception e){
            logger.error(e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<ListPage> getListPage(String searchLink, Integer taskId, WebDriver driver, Integer pageNum) {
        List<ListPage> results = new ArrayList<>();
        Node next = null;
        Node body = getWebPageByLink(searchLink, driver);
        Integer pageCount = 0;
        while (next != null || results.size() == 0) {
            //大于设定的页数则停止
            if (pageCount > pageNum) {
                break;
            }
            ListPage listPage = new ListPage();
            listPage.setNode(body);
            listPage.setTaskId(taskId);
            listPage.setLink(body.getLink());

            //存入本地
            try {
                String fileLocation = FileLocationEnum.LISTPAGELOCATION.getLocation() + taskId + pageNum + ".html";
                String innerText = body.getElement().getAttribute("innerHTML");
                FileUtils.write(new File(fileLocation), innerText, "utf-8");
                listPage.setLocation(fileLocation);
            } catch (Exception e) {
                logger.error(e.getMessage());
            }

            //存入数据库并返回id
            ListPageInfo listPageInfo = listPageInfoManager.saveListPageInfo(listPage);
            listPage.setId(listPageInfo.getId());
            results.add(listPage);
            next = getNextPage(body.getElement());
            if (next ==null){
                continue;
            }
            pageCount++;

        }
        return results;
    }

    @Override
    public List<String> getListPageALink(String searchWord, ListPage listPage) {
        List<String> result = new ArrayList<>();
        List<Term> termList = HanLP.segment(searchWord);
        Node node = listPage.getNode();
        Set<WebElement> elementSet = new HashSet<>();
        for (Term term : termList){
            String segWord = term.word;
            List<WebElement> elements = node.getElement().findElements(By.partialLinkText(segWord));
            if (elementSet.size()==0){
                elementSet.addAll(elements);
            }else {
                elementSet.retainAll(elements);
            }

        }
        for (WebElement element : elementSet) {
            String link = element.getAttribute("href");
            result.add(link);
        }
        return result;


    }

    @Override
    public DetailPage getDetailPage(String link, Integer taskId, Integer listPageId, WebDriver driver) {
        Node body = getWebPageByLink(link, driver);
        DetailPage detailPage = new DetailPage();
        detailPage.setNode(body);
        detailPage.setLink(link);
        detailPage.setTaskId(taskId);
        detailPage.setTime(new Date());
        detailPage.setListId(listPageId);
        //存入本地
        try {
            String fileLocation = FileLocationEnum.DETAILPAGELOCATION.getLocation()
                    + taskId + link.substring(15) + ".html";
            String innerText = body.getElement().getAttribute("innerHTML");
            FileUtils.write(new File(fileLocation), innerText, "utf-8");
            detailPage.setLocation(fileLocation);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        //存入数据库并返回id
        DetailPageInfo detailPageInfo = detailPageInfoManager.saveDetailPageInfo(detailPage);
        detailPage.setId(detailPageInfo.getId());
        return detailPage;
    }


    /**
     * 获取下一页的网页
     *
     * @param element
     * @return
     */
    private Node getNextPage(WebElement element) {
        try{
        WebElement next = element.findElement(By.partialLinkText("下一页"));
        if (next != null) {
            next.click();
            Node nextElement = new Node();
            nextElement.setElement(next);
            return nextElement;
        }}catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

}