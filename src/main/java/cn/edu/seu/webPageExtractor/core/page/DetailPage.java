package cn.edu.seu.webPageExtractor.core.page;


import cn.edu.seu.webPageExtractor.core.DetailPageInfo;
import cn.edu.seu.webPageExtractor.core.page.feature.Block;

import java.util.List;

public class DetailPage extends DetailPageInfo {
    /**
     * 当前详情页的节点内容
     */
    Block block;
    /**
     * 详情页的分块结果
     */
    List<Block> blocks;

    /**
     * 详情页的特殊标签文本
     */
    List<String> specialContext;



    public Block getBlock() {
        return block;
    }

    public void setBlock(Block block) {
        this.block = block;
    }

    public List<Block> getBlocks() {
        return blocks;
    }

    public void setBlocks(List<Block> blocks) {
        this.blocks = blocks;
    }

    public List<String> getSpeacilContext() {
        return specialContext;
    }

    public void setSpeacilContext(List<String> specialContext) {
        this.specialContext = specialContext;
    }
}
