package cn.edu.seu.webPageExtractor.core.page.feature;

import cn.edu.seu.webPageExtractor.core.page.Node;

import java.util.List;

public class Block {
    /**
     * 分块中的节点内容
     */
    private Node node;
    /**
     * 子分块
     */
    private List<Block> blocks;

    /**
     * 父分块
     */
    private Block parentBlock;

    /**
     * 块的特征
     */
    private BlockDeNoiseFeature blockDeNoiseFeature;
    /**
     * 块的标签
     */
    private BlockDivideFeature blockDivideFeature;

    public Block (){
        this.blockDeNoiseFeature = new BlockDeNoiseFeature();
        this.blockDivideFeature = new BlockDivideFeature();
    }

    public Node getNode() {
        return node;
    }

    public void setNode(Node node) {
        this.node = node;
    }

    public List<Block> getBlocks() {
        return blocks;
    }

    public void setBlocks(List<Block> blocks) {
        this.blocks = blocks;
    }

    public Block getParentBlock() {
        return parentBlock;
    }

    public void setParentBlock(Block parentBlock) {
        this.parentBlock = parentBlock;
    }

    public BlockDeNoiseFeature getBlockDeNoiseFeature() {
        return blockDeNoiseFeature;
    }

    public void setBlockDeNoiseFeature(BlockDeNoiseFeature blockDeNoiseFeature) {
        this.blockDeNoiseFeature = blockDeNoiseFeature;
    }

    public BlockDivideFeature getBlockDivideFeature() {
        return blockDivideFeature;
    }

    public void setBlockDivideFeature(BlockDivideFeature blockDivideFeature) {
        this.blockDivideFeature = blockDivideFeature;
    }
}
