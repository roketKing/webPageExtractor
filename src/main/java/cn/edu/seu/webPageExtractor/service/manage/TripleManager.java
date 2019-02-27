package cn.edu.seu.webPageExtractor.service.manage;

import cn.edu.seu.webPageExtractor.core.Triple;
import cn.edu.seu.webPageExtractor.service.repository.TripleRepository;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.net.URLDecoder;
import java.util.*;

@Service
public class TripleManager {
    @Autowired
    private TripleRepository tripleRepository;
    private Map<String,List<Triple>> cacheTriple = new HashMap<>();
    private Map<String,List<String>> cacheFileLine = new HashMap<>();
    private Long count = 0L;

    /**
     * 每个10万做一个缓存，处理完成之后删除
     * @param filePath
     */
    public void transTriple2Db(String filePath){
        try {
            LineIterator iterator = FileUtils.lineIterator(new File(filePath),"utf-8");
            String uuid = UUID.randomUUID().toString();
            while (iterator.hasNext()){
                if (cacheFileLine.containsKey(uuid)){
                    cacheFileLine.get(uuid).add(iterator.nextLine());
                    if (cacheFileLine.get(uuid).size()==100000){
                        dataDeal(uuid);
                    }
                }else {
                    cacheFileLine.put(uuid,new ArrayList<>());
                    cacheFileLine.get(uuid).add(iterator.nextLine());
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void dataDeal(String uuid){
//
//        List<Triple> triples;
//        if (cacheTriple.containsKey(uuid)){
//            triples = cacheTriple.get(uuid);
//            if (cacheTriple.get(uuid).size()==100000){
//                saveData2Db(uuid);
//                uuid = UUID.randomUUID().toString();
//            }
//        }else {
//            triples = new ArrayList<>();
//            cacheTriple.put(uuid,triples);
//        }
//        String line = iterator.nextLine();
//        //转码
//        line = URLDecoder.decode(line,"utf-8");
//        //分割
//        String[] words = line.split("> <");
//        Triple triple = new Triple();
//        if (words.length>=3){
//            String sub = words[0].replace("<","").replace(">","");
//            String predict = words[1].replace("<","").replace(">","");
//            String obj = words[2].replace(" .\t","").replace(">","");
//            if (sub.length()<255 && predict.length()<255 && obj.length()<255){
//                triple.setSubject(sub);
//                triple.setPredict(predict);
//                triple.setObject(obj);
//                //存储
//                triples.add(triple);
//            }
//        }
    }
    @Async
    public void saveData2Db(String uuid){
        Thread th=Thread.currentThread();
        tripleRepository.saveAll(cacheTriple.get(uuid));
        count = count+100000;
        cacheTriple.remove(uuid);
        System.out.println("已经存储了" + count+"线程名为"+th.getName());
    }
}
