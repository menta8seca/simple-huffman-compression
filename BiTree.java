public class BiTree {
    public Node root;
    private int checkk=0;
    public void add(Node x) {
        root = add(root, x);
        if(checkk==1){
            fix(this.root,0);
            checkk=0;
            add(x);
        }
    }
    private Node add(Node root, Node x) {
        if (root == null) {
            return x;
        } 
        if (root.left==null||root.left.lflag==0) {
            root.left = add(root.left, x);
        } else if (root.right==null||root.right.lflag==0){
            if(root.right==null&&x.lflag==1){
                root.lflag=1;
            }
            root.right = add(root.right, x);
        }else{
            checkk=1;
        }
        return root;
    }
    private int fix(Node root,int check) {
        if (root != null) {
            if (root.left!=null&&root.left.lflag==1) {
                if (root.right!=null&&root.right.lflag==1){
                    root.lflag=1;
                    return 1;
                }else if(root.right!=null&&root.right.lflag==0){
                    check=fix(root.right,check);
                    if(check==1){
                        root.lflag=1;
                        return 1;
                    }
                }
            } 
            else if (root.left!=null&&root.left.lflag==0){
                fix(root.left,check);
            }
        }
        return check;
    }
    public int find(StringBuilder x) {
        return find(root, x);
    }
    private int find(Node root, StringBuilder x) {
        if (root == null) {
            return -1;
        } else if (root.left!=null&&x.length()>=1&&x.charAt(0)=='0') {
            x.deleteCharAt(0);
            return (find(root.left, x));
        } else if (root.right!=null&&x.length()>=1&&x.charAt(0)=='1') {
            x.deleteCharAt(0);
            return (find(root.right, x));
        } else {
            if(root.left==null&&root.right==null){
                return root.chara;
            }
            else{
                return -1;
            }
        }
    }
    public void printtree(){
        printtree(root);
    }
    private void printtree(Node root){
        if(root!=null){
            System.out.print(root.chara+" ");
            printtree(root.left);
            printtree(root.right);
        }
    }
}