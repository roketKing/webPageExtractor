package cn.edu.seu.webPageExtractor.service.manage;

import cn.edu.seu.webPageExtractor.graph.core.Category;
import cn.edu.seu.webPageExtractor.graph.core.Property;
import cn.edu.seu.webPageExtractor.graph.core.Resource;
import cn.edu.seu.webPageExtractor.service.repository.ResourceRepository;
import cn.edu.seu.webPageExtractor.service.repository.TripleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class DbTransferManager {
    @Autowired
    private ResourceRepository resourceRepository;
    @Autowired
    private TripleRepository tripleRepository;
    @Autowired
            private MongoTemplate mongoTemplate;

    Logger logger = LoggerFactory.getLogger(DbTransferManager.class);

    @Async
    public void transferData(Integer pageNum, Integer pageSize) {
        long startTime = System.currentTimeMillis();
        logger.info("线程" + Thread.currentThread().getName() + "起始位置" + (pageNum/5000-1) );
        //从mysql中查询数据
        List<String> resUris = tripleRepository.findAllResourceUri(pageNum, pageSize);
        List<Resource> resources = new ArrayList<>();
        for (String uri : resUris) {
            Resource resource = new Resource();
            String[] rNameSplit = uri.split("/");
            resource.setUri(uri);
            resource.setName(rNameSplit[rNameSplit.length - 1]);
            resource.setId(uri);

            List<Object> obList = tripleRepository.findResourceProperty(uri);
            List<String> categoryList = tripleRepository.findAllCategoryResource(uri);

            List<Property> properties = new ArrayList<>();
            for (Object ob : obList) {
                Object[] obArray = (Object[]) ob;
                Property property = parseObject2Property(obArray);
                if (property != null) {
                    properties.add(property);
                }
            }
            resource.setProperties(properties);

            List<Category> categories = new ArrayList<>();
            for (String cat : categoryList) {
                String[] catSplit = cat.split("/");
                Category category = new Category(catSplit[catSplit.length - 1], cat);
                categories.add(category);
            }
            resource.setCategories(categories);
            resources.add(resource);
        }
        //将数据重新组织插入mongodb
        try {

            mongoTemplate.insertAll(resources);
        }catch (Exception e){
            logger.info(e.getMessage()+"开始位置：" + pageNum );
        }

        logger.info("线程" + Thread.currentThread().getName() + "结束" + "使用时间" +
                (System.currentTimeMillis() - startTime)+"插入的条数"+resources.size());
    }


    private Property parseObject2Property(Object[] pro) {
        if (pro.length != 2) {
            return null;
        }
        Property property = new Property();

        String pName = pro[0].toString();
        property.setPropertyUri(pName);

        String pValue = pro[1].toString();
        String[] pNameSplit = pName.split("property/");
        pName = pNameSplit[pNameSplit.length - 1].replace(" ", "");
        if (pValue.contains("resource/")) {
            String[] pValueSplit = pValue.split("resource/");
            pValue = pValueSplit[pValueSplit.length - 1];
        }

        property.setName(pName);
        property.setPropertyValue(pValue);

        return property;

    }

}
