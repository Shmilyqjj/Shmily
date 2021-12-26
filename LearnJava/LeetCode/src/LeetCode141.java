/**
 * Description: LeetCode141.环形链表  判断一个链表是否有环
 * Date: 2020/4/30 23:40
 * Author: 佳境Shmily
 * Site: shmily-qjj.top
 *
 * 考点：双指针法  快慢指针
 *
 */



//Definition for singly-linked list.
class ListNode {
    int val;
    ListNode next;
    ListNode(int x) {
        val = x;
        next = null;
    }
}


public class LeetCode141 {
    public boolean hasCycle(ListNode head) {
        ListNode fast = head;
        ListNode slow = head;
        //条件是fast和fast.next不能为空
        while(fast != null && fast.next != null){
            fast = fast.next.next;
            slow = slow.next;
            if(fast == slow){
                return true;
            }
        }
        return false;
    }

}
