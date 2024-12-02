import java.util.*;
import java.util.concurrent.TimeUnit;

// TreeNode class to represent nodes in the tree
class TreeNode {
    int key;
    TreeNode left, right;

    public TreeNode(int key) {
        this.key = key;
        this.left = this.right = null;
    }
}

// BinarySearchTree class with basic functionality
class BinarySearchTree {
    protected TreeNode root;

    public BinarySearchTree() {
        this.root = null;
    }

    public void insert(int key) {
        root = insertRec(root, key);
    }

    private TreeNode insertRec(TreeNode node, int key) {
        if (node == null) {
            return new TreeNode(key);
        }
        if (key < node.key) {
            node.left = insertRec(node.left, key);
        } else if (key > node.key) {
            node.right = insertRec(node.right, key);
        }
        return node;
    }

    public void delete(int key) {
        root = deleteRec(root, key);
    }

    private TreeNode deleteRec(TreeNode node, int key) {
        if (node == null) return null;

        if (key < node.key) {
            node.left = deleteRec(node.left, key);
        } else if (key > node.key) {
            node.right = deleteRec(node.right, key);
        } else {
            // Node with one child or no child
            if (node.left == null) return node.right;
            if (node.right == null) return node.left;

            // Node with two children
            TreeNode minNode = findMin(node.right);
            node.key = minNode.key;
            node.right = deleteRec(node.right, minNode.key);
        }
        return node;
    }

    protected TreeNode findMin(TreeNode node) {
        while (node.left != null) {
            node = node.left;
        }
        return node;
    }

    public int findMax() {
        TreeNode current = root;
        while (current != null && current.right != null) {
            current = current.right;
        }
        return current != null ? current.key : Integer.MIN_VALUE;
    }

    public List<Integer> traverseInOrder() {
        List<Integer> result = new ArrayList<>();
        inOrderRec(root, result);
        return result;
    }

    private void inOrderRec(TreeNode node, List<Integer> result) {
        if (node != null) {
            inOrderRec(node.left, result);
            result.add(node.key);
            inOrderRec(node.right, result);
        }
    }
}

// AVLTree class inheriting BinarySearchTree and adding self-balancing functionality
class AVLTree extends BinarySearchTree {

    private int height(TreeNode node) {
        if (node == null) return 0;
        return 1 + Math.max(height(node.left), height(node.right));
    }

    private int balanceFactor(TreeNode node) {
        if (node == null) return 0;
        return height(node.left) - height(node.right);
    }

    private TreeNode rotateLeft(TreeNode node) {
        TreeNode rightChild = node.right;
        node.right = rightChild.left;
        rightChild.left = node;
        return rightChild;
    }

    private TreeNode rotateRight(TreeNode node) {
        TreeNode leftChild = node.left;
        node.left = leftChild.right;
        leftChild.right = node;
        return leftChild;
    }

    @Override
    public void insert(int key) {
        root = insertRec(root, key);
    }

    private TreeNode insertRec(TreeNode node, int key) {
        if (node == null) {
            return new TreeNode(key);
        }
        if (key < node.key) {
            node.left = insertRec(node.left, key);
        } else if (key > node.key) {
            node.right = insertRec(node.right, key);
        }

        // Balance the tree
        int balance = balanceFactor(node);
        if (balance > 1) {
            if (key < node.left.key) {
                return rotateRight(node);
            } else {
                node.left = rotateLeft(node.left);
                return rotateRight(node);
            }
        } else if (balance < -1) {
            if (key > node.right.key) {
                return rotateLeft(node);
            } else {
                node.right = rotateRight(node.right);
                return rotateLeft(node);
            }
        }
        return node;
    }

    @Override
    public void delete(int key) {
        root = deleteRec(root, key);
    }

    private TreeNode deleteRec(TreeNode node, int key) {
        if (node == null) return null;

        if (key < node.key) {
            node.left = deleteRec(node.left, key);
        } else if (key > node.key) {
            node.right = deleteRec(node.right, key);
        } else {
            if (node.left == null) return node.right;
            if (node.right == null) return node.left;

            TreeNode minNode = findMin(node.right);
            node.key = minNode.key;
            node.right = deleteRec(node.right, minNode.key);
        }

        // Balance the tree
        int balance = balanceFactor(node);
        if (balance > 1) {
            if (balanceFactor(node.left) >= 0) {
                return rotateRight(node);
            } else {
                node.left = rotateLeft(node.left);
                return rotateRight(node);
            }
        } else if (balance < -1) {
            if (balanceFactor(node.right) <= 0) {
                return rotateLeft(node);
            } else {
                node.right = rotateRight(node.right);
                return rotateLeft(node);
            }
        }
        return node;
    }
}

// Main class for performance testing
public class BSTAndAVLPerformance {

    public static void main(String[] args) {
        int[] sizes = {100, 1000, 10000};
        Map<String, List<Long>> bstTimes = new HashMap<>();
        Map<String, List<Long>> avlTimes = new HashMap<>();
        bstTimes.put("Insert", new ArrayList<>());
        bstTimes.put("Delete", new ArrayList<>());
        avlTimes.put("Insert", new ArrayList<>());
        avlTimes.put("Delete", new ArrayList<>());

        for (int size : sizes) {
            int[] data = new Random().ints(size, 0, size * 10).toArray();

            // Test BST
            BinarySearchTree bst = new BinarySearchTree();
            long start = System.nanoTime();
            for (int val : data) bst.insert(val);
            bstTimes.get("Insert").add(TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start));

            start = System.nanoTime();
            for (int val : data) bst.delete(val);
            bstTimes.get("Delete").add(TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start));

            // Test AVL
            AVLTree avl = new AVLTree();
            start = System.nanoTime();
            for (int val : data) avl.insert(val);
            avlTimes.get("Insert").add(TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start));

            start = System.nanoTime();
            for (int val : data) avl.delete(val);
            avlTimes.get("Delete").add(TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start));
        }

        // Print Results
        System.out.println("BST Times (ms): " + bstTimes);
        System.out.println("AVL Times (ms): " + avlTimes);
    }
}
