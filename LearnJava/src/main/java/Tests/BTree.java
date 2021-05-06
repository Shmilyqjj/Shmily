package Tests;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class BTree<E extends Comparable<E>> {
    private BTreeNode root = null;
    private int t;
    private final int fullNum;

    public BTree(int t) {
        this.t = t;
        fullNum = 2 * t - 1;
    }

    private final BTreeNode NullBTreeNode = new BTreeNode();

    private class BTreeNode {
        private int number = 0;
        private List<E> values = new ArrayList<E>();
        private List<BTreeNode> children = new ArrayList<BTreeNode>();
        private boolean isLeaf = false;

        E getKey(int i) {
            return values.get(i);
        }

        BTreeNode getChildren(int i) {
            return children.get(i);
        }

        void AddKey(int i, E elementent) {
            values.add(i, elementent);
        }

        void removeKey(int i) {
            values.remove(i);
        }

        void AddChildren(int i, BTreeNode c) {
            children.add(i, c);
        }

        void removeChildren(int i) {
            children.remove(i);
        }

        boolean isFull() {
            if (number == fullNum)
                return true;
            return false;
        }

        int getSize() {
            return values.size();
        }

        boolean isNull() {
            return (this == NullBTreeNode);
        }

        public String toString() {
            if (number == 0)
                return "NullNode";

            StringBuilder sb = new StringBuilder();
            sb.append("[N: " + number + "] [values: ");
            for (E e : values) {
                sb.append(e + ", ");
            }
            sb.append(" ] [ children: ");
            for (BTreeNode bNode : children) {
                if (bNode == NullBTreeNode)
                    sb.append(bNode + ", ");
                else
                    sb.append("NotNullNode" + ", ");
            }
            sb.append("] [childrenSize: " + children.size());
            sb.append("] [ isLeaf: " + isLeaf);
            sb.append("]");
            return sb.toString();
        }
    }

    private void constructRoot(E element) {
        root = new BTreeNode();
        root.number = 1;
        root.AddKey(0, element);
        root.isLeaf = false;
    }

    private void addelementToNode(BTreeNode node, E elementent, int i) {
        node.AddKey(i, elementent);
        node.number++;
        node.AddChildren(i, NullBTreeNode);
    }

    public void insertelement(E element) {
        if (root == null) {
            // The first node
            constructRoot(element);
            root.isLeaf = true;
            root.AddChildren(0, NullBTreeNode);
            root.AddChildren(1, NullBTreeNode);
            return;
        }

        BTreeNode curNode = root;

        if (root.isFull()) {
            // Extend the root
            constructRoot(curNode.getKey(t - 1));

            // Get new node
            BTreeNode newNode = getExtendedNode(curNode);

            // Process old full node
            processFullNode(curNode);

            // Process root
            root.AddChildren(0, curNode);
            root.AddChildren(1, newNode);
            return;
        }

        int i = 0;
        BTreeNode childNode = null;
        // Find the node to insert
        while (true) {
            while ((i < curNode.getSize())
                    && (element.compareTo(curNode.getKey(i)) > 0)) {
                i++;
            }
            childNode = curNode.getChildren(i);
            if (childNode.isFull()) {
                curNode.number++;
                curNode.AddKey(i, childNode.getKey(t - 1));
                BTreeNode newNode = getExtendedNode(childNode);
                processFullNode(childNode);
                curNode.AddChildren(i + 1, newNode);
                if (element.compareTo(curNode.getKey(i)) < 0) {
                    curNode = childNode;
                } else {
                    curNode = newNode;
                }
                i = 0;
                continue;
            }
            if (!childNode.isNull()) {
                curNode = childNode;
                i = 0;
                continue;
            }
            addelementToNode(curNode, element, i);
            return;
        }

    }

    private BTreeNode getExtendedNode(BTreeNode fullNode) {
        BTreeNode newNode = new BTreeNode();
        newNode.number = t - 1;
        newNode.isLeaf = fullNode.isLeaf;
        for (int i = 0; i < t; i++) {
            if (i != t - 1) {
                newNode.AddKey(i, fullNode.getKey(t + i));
            }
            newNode.AddChildren(i, fullNode.getChildren(t + i));
        }
        return newNode;
    }

    private void processFullNode(BTreeNode fullNode) {
        fullNode.number = t - 1;
        for (int i = t - 1; i >= 0; i--) {
            fullNode.removeKey(t + i - 1);
            fullNode.removeChildren(t + i);
        }
    }

    public String toString() {
        if (root == null)
            return "NULL";

        StringBuilder sb = new StringBuilder();

        LinkedList<BTreeNode> queue = new LinkedList<BTreeNode>();
        queue.push(root);

        BTreeNode tem = null;
        while ((tem = queue.poll()) != null) {
            for (BTreeNode node : tem.children) {
                if (!node.isNull())
                    queue.offer(node);
            }
            sb.append(tem.toString() + "\n");
        }
        return sb.toString();
    }

    public static void main(String[] args) {
        BTree<Character> tree = new BTree<Character>(3);
        System.out.println(tree);
        Character[] cL = {'D', 'E', 'F', 'A', 'C', 'B', 'Z', 'H', 'I', 'J'};
        for (int i = 0; i < cL.length; i++) {
            tree.insertelement(cL[i]);
            System.out.println("After insert the: " + cL[i]);
            System.out.println(tree);
        }
    }
}