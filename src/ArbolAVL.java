public class ArbolAVL {
    /**
     * Constructor de la clase node
     */
    private class Node {
        Platillos value;
        Node left;
        Node right;
        int height;

        Node(Platillos value) {
            this.value = value;
            height = 1;
        }
    }

    private Node root;

    /**
     * Función para insertar un platillo
     * @param value
     */
    public void insert(Platillos value) {
        root = insert(root, value);
    }

    private Node insert(Node node, Platillos value) {
        if (node == null) {
            return new Node(value);
        }

        if (value.getId() < node.value.getId()) {
            node.left = insert(node.left, value);
        } else if (value.getId() > node.value.getId()) {
            node.right = insert(node.right, value);
        } else {
            return node; // no se permiten duplicados
        }

        node.height = 1 + Math.max(height(node.left), height(node.right));

        int balance = getBalance(node);

        if (balance > 1 && value.getId() < node.left.value.getId()) {
            return rightRotate(node);
        }

        if (balance < -1 && value.getId() > node.right.value.getId()) {
            return leftRotate(node);
        }

        if (balance > 1 && value.getId() > node.left.value.getId()) {
            node.left = leftRotate(node.left);
            return rightRotate(node);
        }

        if (balance < -1 && value.getId() < node.right.value.getId()) {
            node.right = rightRotate(node.right);
            return leftRotate(node);
        }

        return node;
    }

    /**
     * Función para obtener la altura del árbol
     * @param node
     * @return
     */
    private int height(Node node) {
        if (node == null) {
            return 0;
        }
        return node.height;
    }

    /**
     * Función para obtener el balance del árbol
     * @param node
     * @return
     */
    private int getBalance(Node node) {
        if (node == null) {
            return 0;
        }
        return height(node.left) - height(node.right);
    }

    /**
     * Función para la rotación a la derecha de los subárboles
     * @param y
     * @return
     */
    private Node rightRotate(Node y) {
        Node x = y.left;
        Node t2 = x.right;

        x.right = y;
        y.left = t2;

        y.height = 1 + Math.max(height(y.left), height(y.right));
        x.height = 1 + Math.max(height(x.left), height(x.right));

        return x;
    }

    /**
     * Función para la rotación a la izquierda de los subárboles
     * @param x
     * @return
     */
    private Node leftRotate(Node x) {
        Node y = x.right;
        Node t2 = y.left;

        y.left = x;
        x.right = t2;

        x.height = 1 + Math.max(height(x.left), height(x.right));
        y.height = 1 + Math.max(height(y.left), height(y.right));

        return y;
    }

    public void inOrderTraversal() {
        inOrderTraversal(root);
    }

    private void inOrderTraversal(Node node) {
        if (node == null) {
            return;
        }
        inOrderTraversal(node.left);
        System.out.println(node.value.toString());
        inOrderTraversal(node.right);
    }
}
