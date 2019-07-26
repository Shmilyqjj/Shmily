/**
 * 输入一个链表，按链表值从尾到头的顺序返回一个ArrayList。
 */
import java.util.ArrayList;
import java.util.Iterator;


public class Problem3 {
    public static void main(String[] args) {

    //链表初始化-插入数据
        ListNode ln = new ListNode();
        ln.addNode(1);
        ln.addNode(2);
        ln.addNode(3);
        ln.addNode(4);
        ln.addNode(5);


    Solution3 s3 = new Solution3();
    ArrayList<Integer> arr = s3.printListFromTailToHead(ln);
        for (int i = 0; i < arr.size(); i++) {
            System.out.println(arr.get(i));
        }
    Iterator<Integer> it = arr.iterator();
    while (it.hasNext()) {
        int i = it.next();
        System.out.println(i);
    }
    }

}

class ListNode {

    ListNode(){}
    ListNode(int val) {
        this.val = val;
    }
    int val;
    ListNode next = null;
    public void addNode(int data) {

        ListNode node = new ListNode(data);

        ListNode root = new ListNode();
        ListNode temp = root;

        while (temp.next != null) {

            temp = temp.next;

        }

        temp.next = node;

    }

}
class Solution3 {
    public ArrayList<Integer> printListFromTailToHead(ListNode listNode) {
        ArrayList<Integer> arr = new ArrayList<Integer>();
        prtListFromEndToHead(listNode,arr);
        return arr;
    }

    private void prtListFromEndToHead(ListNode listnode,ArrayList arr){
        if(listnode == null){
            return;
        }
        prtListFromEndToHead(listnode.next,arr);
        arr.add(listnode.val);
    }
}


