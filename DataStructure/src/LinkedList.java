/**
*功能：链表学习    用内部类实现
*/
public class LinkedList {
    public static void main(String[] args) {
        NodeManager nm = new NodeManager();
        nm.add(5);//添加数据
        nm.add(4);
        nm.add(3);
        nm.add(2);
        nm.add(1);

        nm.printAll();
        System.out.println("___________________________");
        System.out.println(nm.find(3)); //查找节点   find会返回Node，Node有toString方法
        System.out.println("___________________________");

        nm.del(4); //删除节点
        nm.printAll();
        System.out.println("___________________________");

        nm.insert(2,233); //插入节点
        nm.printAll();
    }
}

class NodeManager {  //节点管理
    private Node root;  //头节点 根节点
    private Node next;  //下一个节点---创建新节点
    private int count = 0;//计数第几个节点  记录索引，记录找到第几个节点了  ，默认0 是根节点

//方法调用内部类
    public void add(int data) {   //添加节点/值
        if (root == null) {  //如果根节点为空，创建新节点
            this.root = new Node(data); //谁调用这个add方法，这个this就是谁
        } else {
            this.root.addNode(data);//在root这个节点后面添加
        }
    }

    public void insert(int index, int data) {  //插入值到两个节点中间
        count++;//寻找一次 则节点计数+1
        if(index == 0){//如果是根节点
            Node nNode = new Node(data);//建一个新的节点
            nNode.next = this.root; //新节点的下一个节点指向原来的根节点
            this.root = nNode; //将新节点赋给根节点
        }else{ //不是根节点
            this.root.insertNode(index,data);
        }
    }

    public void update(int oldVal, int newVal) {
        //修改
    }

    public void del(int data) {
        //删除
        if(this.root != null){
            if(this.root.data==data){
                this.root = this.root.next;//删掉那节，那节的地址赋给前一个
            }else{
                this.root.delNode(data);
            }
        }

    }

    public Node find(int data) { //查找节点
        if (this.root.data == data) {
            return this.root;
        } else {
            return this.root.findNode(data);
        }

    }


    public void printAll() {  //打印链表
        if (this.root != null) {
            System.out.print(this.root.data + "->");  //先打印root
            this.root.printAll();//递归---连续打印整个链表   一开始是root，递归
            System.out.println();
        }
    }











    private class Node { //内部类 Node 节点   private封装所以里面的方法想调用后就要放在内部类外再定义一次
        private int data;  //数据
        private Node next; //地址-----Node类型

        public Node(int data) {
            this.data = data;
        }

        public void addNode(int data) {   //添加节点/值
            if (this.next == null) {  //递归，一直向后找，直到下一个节点为空，创建新节点
                this.next = new Node(data);//添加该节点的值
            } else {
                this.next.addNode(data);//找到了不为空  接着递归调用  接着找
            }
        }


        public void insertNode(int index, int data) {  //插入值到两个节点中间
            count++;//寻找一次 则节点计数+1
            if (this.next != null) { //如果下一个节点不是空节点  - 遍历整个链表 链表最后一个节点的下一个节点指向为空
                if (index == count) {
                    Node n = new Node(data);
                    n.next = this.next;//把当前的节点传给下一个
                    this.next = n;//把新节点变为当前节点
                } else {
                    this.next.insertNode(index, data);//递归 继续找下一个节点
                }
            } else {//如果下个节点是空节点
                this.next = new Node(data);
            }
        }

        public void updateNode(int oldVal, int newVal) {
            //修改
        }

        public void delNode(int data) {
            //删除
            if(this.next != null){
                if(this.next.data == data){ //如果找到当前节点的下一个为data
                    this.next = this.next.next; //连接链表
                }else{
                    this.next.delNode(data);//递归，继续找我的下一个
                }
            }

        }

        public Node findNode(int data) { //查找节点
            if (this.next != null) {  //直到下一个等于空
                if (this.next.data == data) { //找下一个   data正好和下一个节点相等
                    return this.next;
                } else {
                    return this.next.findNode(data); //递归，继续找               }
                }
            }
            return null;
        }

        public void printAll() {  //打印链表
            if (this.next != null) { // 一开始this表示root  我们要root的下一个节点，不为空就打印 直到为空 结束
                System.out.print(this.next.data + "->");//打一个
                this.next.printAll();// 递归 打印它的下一个
            }
        }

        public String toString() {
            return "data" + data;
        }

        public int getData() {
            return data;
        }

        public void setData(int data) {
            this.data = data;
        }

        public Node getNext() {
            return next;
        }

        public void setNext(Node next) {
            this.next = next;
        }
    }

}



