package cn.edu.seu.webPageExtractor.service.impl;

import cn.edu.seu.webPageExtractor.core.page.DetailPage;
import cn.edu.seu.webPageExtractor.core.page.ListPage;
import cn.edu.seu.webPageExtractor.service.PageDivideService;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebElement;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class PageDivideServiceImpl implements PageDivideService {
    @Override
    public void listPageDivide(ListPage listPage) {
        WebElement body = listPage.getNode().getElement();
        List<WebElement> childs = body.findElements(By.xpath("./*"));

        Map<Integer,Integer> eleSizeMap = new HashMap<>();
        Set<Integer> sizeSet = new HashSet<>();
        for(int i=0;i<childs.size();i++){
            Dimension size =  childs.get(i).getSize();
            Integer sizeResult = size.getHeight()*size.getWidth();

            eleSizeMap.put(i,sizeResult);
            sizeSet.add(sizeResult);
        }
        //只有一个大小则返回，有多个大小的取最大区间继续分割
        if (sizeSet.size()==1){
            return;
        }

        List<Map.Entry<Integer,Integer>> eleSizeMapList = new ArrayList<>(eleSizeMap.entrySet());
        eleSizeMapList.sort(new Comparator<Map.Entry<Integer, Integer>>() {
            @Override
            public int compare(Map.Entry<Integer, Integer> o1, Map.Entry<Integer, Integer> o2) {
                return o2.getValue() - o1.getValue();
            }
        });

        WebElement bigElement = childs.get(eleSizeMapList.get(0).getKey());
        listPage.getNode().setElement(bigElement);

        listPageDivide(listPage);

    }


    public void detailPageDivide(DetailPage detailPage){

    }
}
