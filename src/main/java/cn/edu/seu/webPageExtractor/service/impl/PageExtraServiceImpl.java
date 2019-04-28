package cn.edu.seu.webPageExtractor.service.impl;

import cn.edu.seu.webPageExtractor.constants.ContextType;
import cn.edu.seu.webPageExtractor.controller.dto.TaskInfoDto;
import cn.edu.seu.webPageExtractor.core.ExtraResultInfo;
import cn.edu.seu.webPageExtractor.core.page.feature.Block;
import cn.edu.seu.webPageExtractor.core.page.feature.Context;
import cn.edu.seu.webPageExtractor.service.PageExtraService;
import cn.edu.seu.webPageExtractor.service.manage.ExtraResultManager;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class PageExtraServiceImpl implements PageExtraService {
    @Autowired
    private ExtraResultManager extraResultManager;

    @Override
    public void listPageExtra(List<Block> correctBlock, TaskInfoDto taskInfoDto) {
        //抽取内联元素的文本
        List<ExtraResultInfo> infos = new ArrayList<>();

        for (Block block : correctBlock) {
            //对block抽取
            if (block.getBlockDeNoiseFeature().getBlockInfos() != null) {
                List<String> blockInfos = block.getBlockDeNoiseFeature().getBlockInfos();
                Gson gson = new Gson();
                String temp = gson.toJson(blockInfos);

                ExtraResultInfo extraResultInfo = new ExtraResultInfo();
                extraResultInfo.setTaskId(taskInfoDto.getId().toString());
                extraResultInfo.setContext(temp);
                extraResultInfo.setTaskDomain(taskInfoDto.getDomain());
                infos.add(extraResultInfo);
            }
        }

        extraResultManager.saveResult(infos);
    }


    @Override
    public void detailPageExtra(Block block, TaskInfoDto taskInfoDto) {
        //判断属性名，属性值HTML标签，抽取属性-属性值对
        List<ExtraResultInfo> infos = new ArrayList<>();
        Map<String, String> result = new HashMap();
        List<Context> contexts = block.getBlockDeNoiseFeature().getContext();

        //统计标签
        Map<String, Integer> pMap = new HashMap<>();
        Map<String, Integer> pvMap = new HashMap<>();
        try {
            for (Context con : contexts) {
                String htmlTag = con.getHtmlTag();
                if (con.getType().equals(ContextType.PROPERTY_CONTEXT)) {
                    if (!pMap.containsKey(htmlTag)) {
                        pMap.put(htmlTag, 0);
                    } else {
                        pMap.put(htmlTag, pMap.get(htmlTag) + 1);
                    }
                } else if (con.getType().equals(ContextType.PROPERTYVALUE_CONTEXT)) {
                    if (!pvMap.containsKey(htmlTag)) {
                        pvMap.put(htmlTag, 0);
                    } else {
                        pvMap.put(htmlTag, pvMap.get(htmlTag) + 1);
                    }
                }

            }
            String pTag = sortMap(pMap);
            String pvTag = sortMap(pvMap);
            String pStr = null;

            for (int i = 0; i < contexts.size(); i++) {
                String tempHtmlTag = contexts.get(i).getHtmlTag();
                if (tempHtmlTag.equals(pTag)) {
                    if (pStr == null) {
                        pStr = contexts.get(i).getStr();
                    }

                } else if (tempHtmlTag.equals(pvTag)) {
                    if (pStr != null) {
                        result.put(pStr, contexts.get(i).getStr());
                        pStr = null;
                    }
                }
            }


            Gson gson = new Gson();
            String contextStr = gson.toJson(result);
            ExtraResultInfo extraResultInfo = new ExtraResultInfo();
            extraResultInfo.setTaskId(taskInfoDto.getId().toString());
            extraResultInfo.setContext(contextStr);
            extraResultInfo.setTaskDomain(taskInfoDto.getDomain());
            infos.add(extraResultInfo);

            extraResultManager.saveResult(infos);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private String sortMap(Map<String, Integer> inMap) {
        List<Map.Entry<String, Integer>> tempListMap = new ArrayList<>(inMap.entrySet());
        tempListMap.sort(new Comparator<Map.Entry<String, Integer>>() {
            @Override
            public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
                return o2.getValue() - o1.getValue();
            }
        });
        return tempListMap.get(0).getKey();
    }
}
