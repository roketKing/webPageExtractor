package cn.edu.seu.webPageExtractor.core.page;


import cn.edu.seu.webPageExtractor.core.ListPageInfo;
import cn.edu.seu.webPageExtractor.core.page.feature.Block;
import cn.edu.seu.webPageExtractor.core.page.feature.ListPageFeature;

import java.util.List;

public class ListPage extends ListPageInfo {
    /**
     * 列表页的节点内容
     */
    Node node;
    /**
     *列表页的特征
     */
    List<ListPageFeature> listPageFeatures;

    Block block;

    List<Block> childBlocks;

    public Node getNode() {
        return node;
    }

    public void setNode(Node node) {
        this.node = node;
    }

    public List<ListPageFeature> getListPageFeatures() {
        return listPageFeatures;
    }

    public void setListPageFeatures(List<ListPageFeature> listPageFeatures) {
        this.listPageFeatures = listPageFeatures;
    }

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
