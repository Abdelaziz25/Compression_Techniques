package technique.huffman;

import utils.Bytes;

public class Node implements Comparable<Node>{
    long freq;
    Bytes b;
    Node left, right;

    public void incrementFreq(){
        this.freq++;
    }

    public Node(long freq, Bytes b, Node left, Node right) {
        this.freq = freq;
        this.left = left;
        this.right = right;
        this.b = b;
    }

    public Node(long freq , Node left, Node right) {
        this.freq = freq;
        this.left = left;
        this.right = right;
    }

    public Node(long freq, Bytes b) {
        this.freq = freq;
        this.b = b;
        this.left = null;
        this.right = null;
    }

    public boolean isLeaf(){
        return this.left == null && this.right == null;
    }

    @Override
    public int compareTo(Node o) {
        return Long.compare(this.freq, o.freq);
    }

    @Override
    public String toString() {
        return b + ":" + freq;
    }
}
