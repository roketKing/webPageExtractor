package cn.edu.seu.webPageExtractor.service.impl;

import cn.edu.seu.webPageExtractor.core.page.DetailPage;
import cn.edu.seu.webPageExtractor.core.page.ListPage;
import cn.edu.seu.webPageExtractor.core.page.feature.Block;
import cn.edu.seu.webPageExtractor.service.PageDeNoiseService;
import cn.edu.seu.webPageExtractor.service.PageDivideService;

import java.util.*;

public class PageDeNoiseServiceImpl implements PageDeNoiseService {
    @Override
    public List<Block> listPageDeNoise(ListPage listPage) {
        List<Block> childBlocks = listPage.getChildBlocks();
        //排序，获得无噪声的block，然后获取关键词
        Map<String, Integer> keyWordMap = new HashMap<>();
        childBlocks.sort(new Comparator<Block>() {
            @Override
            public int compare(Block o1, Block o2) {
                Float res = o1.getDomainScore() - o2.getDomainScore();
                if (res > 0) {
                    return 1;
                } else if (res < 0) {
                    return -1;
                } else {
                    return 1;
                }
            }
        });
        Integer endNum = childBlocks.size() - 1;
        if (childBlocks.size() > 10) {
            endNum = 10;
        }
        List<Block> correctBlock = new ArrayList<>();
        for (Block block : childBlocks.subList(0, endNum)) {
            List<String> contexts = block.getContext();
            for (String con : contexts) {
                if (keyWordMap.containsKey(con)) {
                    Integer count = keyWordMap.get(con);
                    keyWordMap.put(con, count + 1);
                } else {
                    keyWordMap.put(con, 1);
                }
            }
            correctBlock.add(block);
        }

        //获取前10个关键字
        List<Map.Entry<String, Integer>> keyWordMapList = new ArrayList<>(keyWordMap.entrySet());
        keyWordMapList.sort(new Comparator<Map.Entry<String, Integer>>() {
            @Override
            public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
                return o2.getValue() - o1.getValue();
            }
        });

        List<String> keyWordList = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : keyWordMapList.subList(0, 9)) {
            keyWordList.add(entry.getKey());
        }

        for (Block block : childBlocks.subList(endNum, childBlocks.size() - 1)) {
            List<String> contexts = block.getContext();
            Integer initContextSize = contexts.size();
            contexts.removeAll(keyWordList);
            if (initContextSize - contexts.size() > 10) {
                correctBlock.add(block);
            }
        }
        return correctBlock;
    }

    @Override
    public Block detailPageDeNoise(DetailPage detailPage) {
        List<Block> blocks = detailPage.getBlocks();
        //block特征得分计算
        List<Block> notZeroBlocks = new ArrayList<>();
        List<Float> wordDensityList = new ArrayList<>();
        for (Block block : blocks) {
            if (block.getDomainScore() != 0) {
                wordDensityList.add(block.getContextDensity());
                notZeroBlocks.add(block);
            }
        }

        Float maxWordDensity = Collections.max(wordDensityList);
        Float minWordDensity = Collections.min(wordDensityList);
        Float gradient = 1 / maxWordDensity - minWordDensity;

        for (Block block : notZeroBlocks) {
            if (!block.getContextDensity().isInfinite()) {
                Float density = gradient * (block.getContextDensity() - minWordDensity);
                Float res = block.getDomainScore() * (1 / density + 1 / block.getLinkNumber());
                block.setScoreResult(res);
            }
        }

        //block排序
        notZeroBlocks.sort(new Comparator<Block>() {
            @Override
            public int compare(Block o1, Block o2) {
                float res = o1.getScoreResult() - o2.getScoreResult();
                if (res > 0) {
                    return 1;
                } else if (res < 0) {
                    return -1;
                } else {
                    return 0;
                }

            }
        });

        return blocks.get(0);
    }
}
