/**
 *输入某二叉树的前序遍历和中序遍历的结果，请重建出该二叉树。假设输入的前序遍历和中序遍历的结果中都不含重复的数字。
 * 例如输入前序遍历序列{1,2,4,7,3,5,6,8}和中序遍历序列{4,7,2,1,5,3,8,6}，则重建二叉树并返回。
 */


public class Problem4 {
    public static void main(String[] args) {
        int[] preOrder = {1,2,4,7,3,5,6,8};
        int[]  inOrder = {4,7,2,1,5,3,8,6};

    }
}

class TreeNode{
    int val;
    TreeNode left;
    TreeNode right;
    TreeNode(int val){
        this.val = val;
    }
}
class Solution4 {
    public TreeNode reConstructBinaryTree(int [] pre,int [] in) {

        return null;
    }
}