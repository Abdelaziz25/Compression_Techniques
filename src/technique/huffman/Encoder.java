package technique.huffman;

import utils.Bytes;

import java.util.*;

public class Encoder {
    Map<Bytes,Node> map;
    HashMap<Bytes, String> table;

    public Encoder() {
        this.map = new HashMap<>();
        this.table = new HashMap<>();
    }

    void calculateFrequencies(byte[] buffer, int bufferLength, int windowSize){
        for(int i = 0; i < bufferLength/windowSize; i++) {
            Bytes byteArray = new Bytes(Arrays.copyOfRange(buffer, i*windowSize, (i+1)*windowSize));
//            System.out.println(byteArray);
            if (map.containsKey(byteArray))
                map.get(byteArray).incrementFreq();
            else
                map.put(byteArray, new Node(1L, byteArray));
        }
    }

    Node constructTree(){
        PriorityQueue<Node> queue = new PriorityQueue<>();
        for (Map.Entry<Bytes, Node> entry : map.entrySet())
            queue.add(entry.getValue());
        while (queue.size() > 1){
            Node left = queue.poll();
            Node right = queue.poll();
            Node node =
                    new Node(left.freq+ right.freq, left, right);
            queue.add(node);
        }
        return queue.poll();
    }

    void getCodes(Node root, StringBuilder builder){
        if (root == null)
            return;
        if(root.isLeaf()){
            if(builder.isEmpty())
                builder.append("0");
            String code = builder.toString();
            table.put(root.b, code);
//          System.out.println("symbol " + root.b + " has code " + builder);
            return;
        }
        builder.append(0);
        getCodes(root.left, builder);
        builder.setLength(builder.length()-1);

        builder.append(1);
        getCodes(root.right, builder);
        builder.setLength(builder.length()-1);
    }

    HashMap<Bytes, String> getCodes(Node root){
        getCodes(root, new StringBuilder());
        return table;
    }
}