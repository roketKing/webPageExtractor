package cn.edu.seu.webPageExtractor.service.impl;

import cn.edu.seu.webPageExtractor.core.page.DetailPage;
import cn.edu.seu.webPageExtractor.core.page.ListPage;
import cn.edu.seu.webPageExtractor.core.page.feature.Block;
import cn.edu.seu.webPageExtractor.core.page.feature.Context;
import cn.edu.seu.webPageExtractor.service.PageDeNoiseService;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class PageDeNoiseServiceImpl implements PageDeNoiseService {
    @Override
    public List<Block> listPageDeNoise(ListPage listPage, List<Context> negativeContext) {
        List<Block> childBlocks = listPage.getChildBlocks();
        //删除DomainScore为0的block
        childBlocks.removeIf(tempBlock -> tempBlock.getBlockDeNoiseFeature() == null || tempBlock.getBlockDeNoiseFeature().getDomainScore() == null);
        //排序，获得无噪声的block，然后获取关键词
        Map<Context, Integer> keyWordMap = new HashMap<>();
        childBlocks.sort(new Comparator<Block>() {
            @Override
            public int compare(Block o1, Block o2) {
                Float res = o1.getBlockDeNoiseFeature().getDomainScore() - o2.getBlockDeNoiseFeature().getDomainScore();
                if (res > 0) {
                    return -1;
                } else if (res < 0) {
                    return 1;
                } else {
                    return -1;
                }
            }
        });
        Integer endNum = childBlocks.size() - 1;
        if (childBlocks.size() > 10) {
            endNum = 9;
        }
        List<Block> correctBlock = new ArrayList<>();
        for (Block block : childBlocks.subList(0, endNum)) {
            List<Context> contexts = block.getBlockDeNoiseFeature().getContext();
            for (Context con : contexts) {
                //在负例中没有的文本再进行词频统计
                if (!negativeContext.contains(con)) {
                    if (keyWordMap.containsKey(con)) {
                        Integer count = keyWordMap.get(con);
                        keyWordMap.put(con, count + 1);
                    } else {
                        keyWordMap.put(con, 1);
                    }
                }
            }
            correctBlock.add(block);
        }


        //获取前10个关键字
        List<Map.Entry<Context, Integer>> keyWordMapList = new ArrayList<>(keyWordMap.entrySet());
        keyWordMapList.sort(new Comparator<Map.Entry<Context, Integer>>() {
            @Override
            public int compare(Map.Entry<Context, Integer> o1, Map.Entry<Context, Integer> o2) {
                return o2.getValue() - o1.getValue();
            }
        });

        List<Context> keyWordList = new ArrayList<>();
        for (Map.Entry<Context, Integer> entry : keyWordMapList.subList(0, 9)) {
            keyWordList.add(entry.getKey());
        }

        for (Block block : childBlocks.subList(endNum, childBlocks.size() - 1)) {
            List<Context> contexts = block.getBlockDeNoiseFeature().getContext();
            Integer initContextSize = contexts.size();
            contexts.removeAll(keyWordList);
            if (initContextSize - contexts.size() > 5) {
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
            if (block.getBlockDeNoiseFeature().getDomainScore() != 0) {
                if (!block.getBlockDeNoiseFeature().getContextDensity().isInfinite()) {
                    wordDensityList.add(block.getBlockDeNoiseFeature().getContextDensity());
                    notZeroBlocks.add(block);
                }
            }
        }

        Float maxWordDensity = Collections.max(wordDensityList);
        Float minWordDensity = Collections.min(wordDensityList);
        Float gradient = 1 / (maxWordDensity - minWordDensity);

        Iterator<Block> blockIterator = notZeroBlocks.iterator();
        while (blockIterator.hasNext()) {
            Block tempBlock = blockIterator.next();
            if (!tempBlock.getBlockDeNoiseFeature().getContextDensity().isInfinite()) {
                Float density = gradient * (tempBlock.getBlockDeNoiseFeature().getContextDensity() - minWordDensity);
                Float res = tempBlock.getBlockDeNoiseFeature().getDomainScore() * (1 / density + 1 / (tempBlock.getBlockDeNoiseFeature().getLinkNumber() + 1));
                tempBlock.getBlockDeNoiseFeature().setScoreResult(res);
                if (res.isInfinite()) {
                    blockIterator.remove();
                }
            } else {
                blockIterator.remove();
            }
        }

        //block排序
        notZeroBlocks.sort(new Comparator<Block>() {
            @Override
            public int compare(Block o1, Block o2) {
                float res = o1.getBlockDeNoiseFeature().getScoreResult() - o2.getBlockDeNoiseFeature().getScoreResult();
                if (res > 0) {
                    return 1;
                } else if (res < 0) {
                    return -1;
                } else {
                    return 0;
                }

            }
        });

        return notZeroBlocks.get(notZeroBlocks.size() - 1);
    }
}
