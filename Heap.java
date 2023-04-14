public class Heap {
    public Node[] arr;
    public int heapsize;
    public int size = 0;
    public int tsize;
    public String[] result = new String[256];
    public StringBuilder trashie = new StringBuilder();
    public StringBuilder charecters = new StringBuilder();

    // ===============================================================================
    public Heap(int[] data) {
        int counter = 0;
        Node[] data1 = new Node[data.length];
        for (int j = 0; j < data.length; j++) {
            if (data[j] != 0) {
                data1[counter] = new Node(j);
                data1[counter].freq = data[j];
                counter++;
            }
        }
        buildHeap(data1, counter);
    }
    public void buildHeap(Node[] data, int counter) {
        arr = new Node[((counter + 1))];
        for (int i = 0; i < counter; i++) {
            insert(data[i]);
        }
        tsize = size;
        prioritize(size);
    }
    private void prioritize(int index) {
        for (int u = 0; u < index - 1; u++) {
            Node root1 = remove();
            Node root2 = remove();
            Node temp = new Node(0);
            temp.freq = root1.freq + root2.freq;
            temp.left = root1;
            temp.right = root2;
            insert(temp);
        }
        if (index == 1) {
            generateCode(arr[1], Character.toString(arr[1].chara));
        } else {
            generateCode(arr[1], "");
        }
    }
    private void generateCode(Node root, String codie) {
        if (root.left != null || root.right != null) {
            trashie.append("0");
            if (root.left != null) {
                generateCode(root.left, codie + "0");
            }
            if (root.right != null) {
                generateCode(root.right, codie + "1");
            }
        } else {
            result[root.chara] = codie;
            charecters.append((char) root.chara);
            trashie.append("1");
        }
    }
    public String[] getResult() {
        return result;
    }
    public boolean isEmpty() {
        if (arr[1] == null) {
            return true;
        } else {
            return false;
        }
    }
    public boolean isFull() {
        if (size == arr.length)
            return true;
        else
            return false;
    }
    public void insert(Node data) {
        if (false == isFull()) {
            arr[size + 1] = data;
            size++;
            HeapifyUp(size);
        }
    }
    public Node remove() {
        if (false == isEmpty()) {
            Node root = arr[1];
            arr[1] = arr[size];
            size--;
            HeapifyDown(1);
            return root;
        }
        return null;
    }
    public void HeapifyUp(int index) {
        while (index / 2 != 0) {
            if (arr[index].freq < arr[index / 2].freq) {
                Node temp = arr[index];
                arr[index] = arr[index / 2];
                arr[index / 2] = temp;
                index = index / 2;
            } else {
                break;
            }
        }
    }
    public void HeapifyDown(int index) {
        while (index * 2 <= size) {
            if ((arr[index].freq > arr[index * 2].freq) && (arr[index * 2].freq < arr[index * 2 + 1].freq)) {
                Node temp = arr[index];
                arr[index] = arr[index * 2];
                arr[index * 2] = temp;
                index = index * 2;
            } else if ((arr[index].freq > arr[index * 2 + 1].freq) && (arr[index * 2].freq > arr[index * 2 + 1].freq)) {
                Node temp = arr[index];
                arr[index] = arr[index * 2 + 1];
                arr[index * 2 + 1] = temp;
                index = index * 2 + 1;
            } else {
                break;
            }
        }
    }
    public String getchara() {
        return charecters.toString();
    }
    public String getTrash() {
        int size8 = trashie.length() % 8;
        if (size8 != 0) {
            size8 = 8 - size8;
            for (int k = 0; k < size8; k++) {
                trashie.append("0");
            }
        }
        return trashie.toString();
    }
    public void showstucture() {
        for (int i = 1; i < arr.length; i++) {
            System.out.print(arr[i].freq + "/" + arr[i].chara + " ");
        }
    }
    public void printtree(){
        printtree(arr[1]);
    }
    private void printtree(Node root){
        if(root!=null){
            System.out.print(root.chara+"/"+root.freq+" ");
            printtree(root.left);
            printtree(root.right);
        }
    }
}