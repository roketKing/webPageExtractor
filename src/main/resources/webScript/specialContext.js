var textReuslt = [];
function printTableList(node) {
    //孩子节点为0的时候
    if (node.childElementCount === 0 ){
        return;
    }
    var nodeChildrens = node.children;
    for (var i=0;i<node.childElementCount;i++){
        //打印节点属性
        if (nodeChildrens[i].tagName === "TABLE" || nodeChildrens[i].tagName === "UL" || nodeChildrens[i].tagName === "OL" || nodeChildrens[i].tagName === "DL"){
            var tempText = nodeChildrens[i].innerText.replace(/[\n 󰄫◆	�]/g,"");
            if (tempText.length>0){
                textReuslt.push(tempText);
            }
            continue;
        }
        printTableList(nodeChildrens[i]);
    }
}
