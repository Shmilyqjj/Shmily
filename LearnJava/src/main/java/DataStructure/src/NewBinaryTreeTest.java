package DataStructure.src;/*
*  ①、路径：顺着节点的边从一个节点走到另一个节点，所经过的节点的顺序排列就称为“路径”。
　　②、根：树顶端的节点称为根。一棵树只有一个根，如果要把一个节点和边的集合称为树，那么从根到其他任何一个节点都必须有且只有一条路径。A是根节点。
　　③、父节点：若一个节点含有子节点，则这个节点称为其子节点的父节点；B是D的父节点。
　　④、子节点：一个节点含有的子树的根节点称为该节点的子节点；D是B的子节点。
　　⑤、兄弟节点：具有相同父节点的节点互称为兄弟节点；比如上图的D和E就互称为兄弟节点。
　　⑥、叶节点：没有子节点的节点称为叶节点，也叫叶子节点，比如上图的H、E、F、G都是叶子节点。
　　⑦、子树：每个节点都可以作为子树的根，它和它所有的子节点、子节点的子节点等都包含在子树中。
　　⑧、节点的层次：从根开始定义，根为第一层，根的子节点为第二层，以此类推。
　　⑨、深度：对于任意节点n,n的深度为从根到n的唯一路径长，根的深度为0；
　　⑩、高度：对于任意节点n,n的高度为从n到一片树叶的最长路径长，所有树叶的高度为0；
*
*
* 二叉搜索树（二叉排序树）要求：若它的左子树不空，则左子树上所有结点的值均小于它的根结点的值； 若它的右子树不空，则右子树上所有结点的值均大于它的根结点的值； 它的左、右子树也分别为二叉排序树。
* */

/**
 * date：2019.7.24
 */
public class NewBinaryTreeTest {
    public static void main(String[] args) {
        NewBinaryTree nbt = new NewBinaryTree();
        nbt.add(3);
        nbt.add(1);
        nbt.add(8);
        nbt.add(5);
        nbt.add(4);
        nbt.add(6);
        nbt.add(9);
        nbt.add(7);
        nbt.add(2);

        nbt.find(8);

        nbt.insert(10);
        nbt.find(10);

        nbt.midOrder(); //中序遍历，输出排序结果 左中右
        System.out.println();

        nbt.preOrder(); //先序遍历    中左右
        System.out.println();

        nbt.aftOrder(); //后序遍历    左右中
        System.out.println();

        nbt.findMax().printSingleNode();//最大
        nbt.findMin().printSingleNode();//最小







    }
}

class NewBinaryTree{
    private Node root;

    public void add(int i){ //添加元素
        if(root == null){
            root = new Node(i);
        }else{
            root.addNode(i);
        }
    }

    class Node{
        private int data;  //节点数据
        public Node(int data){this.data = data;}  //初始化-构造函数
        public Node leftChild; //左子节点的引用
        public Node rightChild; //右子节点的引用
        public void addNode(int i){
            if(this.data >= i){
                if(this.leftChild == null){
                    this.leftChild = new Node(i);
                }else{
                    this.leftChild.addNode(i); //递归
                }
            }else{
                if(this.rightChild == null){
                    this.rightChild = new Node(i);
                }else{
                    this.rightChild.addNode(i);
                }
            }
        }

        //中序遍历 LDR左中右 二叉排序树出来的中序遍历结果有序
        public void midPrintNode() {
            if(this.leftChild != null){
                this.leftChild.midPrintNode();
            }
            System.out.print(this.data+" ");
            if(this.rightChild != null){
                this.rightChild.midPrintNode();
            }
        }

        //先序遍历  中左右
        public void prePrintNode(){
            System.out.print(this.data+" ");
            if(this.leftChild != null){
                this.leftChild.prePrintNode();
            }
            if(this.rightChild != null){
                this.rightChild.prePrintNode();
            }
        }

        //后序遍历 左右中
        public void aftPrintNode(){

            if(this.leftChild != null){
                this.leftChild.prePrintNode();
            }
            if(this.rightChild != null){
                this.rightChild.prePrintNode();
            }
            System.out.print(this.data+" ");
        }

        public void printSingleNode(){
            System.out.println(this.data);
        }

    }



    public void find(int key) {
        //1.查找值比当前节点值大，则搜索右子树；
        //2.查找值等于当前节点值，停止搜索（终止条件）；
        //3.查找值小于当前节点值，则搜索左子树；
        Node current = root;
        while (current != null && key != current.data) {
            if (key < current.data) {
                current = current.leftChild;
            } else {
                current = current.rightChild;
            }
        }
        System.out.println(current == null ? key+"不存在" : key+"存在");
    }

    public boolean insert(int key) {
        //1.先从根插入  根为空则插入根 插入成功
        //2.根不为空，小于根节点与左子树比较 大于根节点与右子树比较
        //3.比较时左子节点或右子节点为空 直接插入
        //4.插入失败 返回false
        Node newNode = new Node(key);
        if(root == null){
            root = newNode;
        }else{
            Node current = root;
            Node parentNode = null;
            while(current != null){
                parentNode = current;
                if(current.data >= key){
                    current = current.leftChild;
                    if(current == null){
                        parentNode.leftChild = newNode;
                        return  true;
                    }
                }else{
                    current = current.rightChild;
                    if(current == null){
                        parentNode.rightChild = newNode;
                        return true;
                    }
                }
            }
        }
        return false;
    }


    public void delete(int key) {
        //删除操作很难，分三种情况：1.删除没有子节点的节点  2.删除有一个子节点的节点  3.删除有两个子节点的节点
        //删除很复杂，所以我们在Node内部类里面加isDelete标识位，删除就是把这个变True 其他操作比如find在做之前先判断isDelete标识位
    }

    public void midOrder(){
        root.midPrintNode();
    }

    public void preOrder() {
        root.prePrintNode();
    }



    public void aftOrder() {
        root.aftPrintNode();
    }

    public Node findMax(){
        Node current = root;
        Node MaxNode = null;
        while(current!=null){
            MaxNode = current;
            current = current.rightChild;
        }
        return MaxNode;
    }
    public Node findMin(){
        Node current = root;
        Node minNode = null;
        while(current != null){
            minNode = current;
            current = current.leftChild;
        }
        return minNode;
    }

}