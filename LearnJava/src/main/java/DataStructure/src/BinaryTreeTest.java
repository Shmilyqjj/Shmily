package DataStructure.src;

/**
 * 数据结构 之 二叉树的实现
 * 二叉树的每个节点最多有两个子树的有序树。通常子树被称作“左子树” 和“右子树”
 * 二叉树算法的排序规则：
 * 1.选择第一个元素作为根节点
 * 2.之后如果元素大于根节点放在右子树，如果元素小于根节点，则放在左子树
 * 3.最后按照中序遍历的方式进行输出，则可以得到排序的结果（左根右）
 * date:2019.3.5
 */
public class BinaryTreeTest {
    public static void main(String[] args) {
        BinaryTree bt = new BinaryTree();
        // 8、3、10、1、6、14、4、7、13
        bt.add(8);
        bt.add(3);
        bt.add(10);
        bt.add(1);
        bt.add(6);
        bt.add(14);
        bt.add(4);
        bt.add(7);
        bt.add(13);
        bt.print();
    }
}

class BinaryTree{
    private Node root;

    public void add(int data){
        if(root == null){
            root = new Node(data);
        }else{
            //往根节点添加孩子
            root.addNode(data);
        }
    }

    public void print(){
        root.print();
    }

    private class Node{
        private int data;//数据
        private Node left;//左孩子
        private Node right;//右孩子
        public Node(int data){
            this.data = data;
        }

        public void addNode(int data){
            //如果是添加左节点
            if(this.data >= data){ //如果根节点比后进来的大，则进来的值是左孩子
                if(this.left == null){
                    this.left = new Node(data);
                }else{
                    this.left.addNode(data);
                }

            }else{  //如果小于 - 往右面加
                if(this.right == null){
                    this.right = new Node(data);
                }else{
                    this.right.addNode(data);
                }
            }
        }

        public void print(){
            if(this.left != null){ //如果左节点非null
                this.left.print(); //输出
            }
                System.out.println(this.data);//打它自己 - root
            if(this.right != null){ //如果右节点非空，输出
                this.right.print();//递归调用
            }
        }

        @Override
        public String toString() {
            return "Node{" +
                    "data=" + data +
                    '}';
        }
    }



}

