package cn.edu.seu.webPageExtractor.core.page;


import cn.edu.seu.webPageExtractor.core.ListPageInfo;
import cn.edu.seu.webPageExtractor.core.page.feature.Block;

import java.util.List;

public class ListPage extends ListPageInfo {
    /**
     * 列表页当前的节点
     */
    Block block;

    /**
     * 列表页的孩子节点
     */
    List<Block> childBlocks;

    public Block getBlock() {
        return block;
    }

    public void setBlock(Block block) {
        this.block = block;
    }

    public List<Block> getChildBlocks() {
        return childBlocks;
    }

    public void setChildBlocks(List<Block> childBlocks) {
        this.childBlocks = childBlocks;
    }
}
