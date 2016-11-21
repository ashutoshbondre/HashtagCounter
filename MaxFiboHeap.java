import java.util.HashMap;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.Queue;

public class MaxFiboHeap {

    private Node  max = null;
    private HashMap<String, Node> hashtagMap = new HashMap<>(); //Creating the Hashmap

    //Creating the class & definition of a Max Fibonacci Heap Node
    class Node{
        Node left, right, parent, child;
        String tag;
        boolean isChildCut;  //This will be used for cascading cut
        int degree, data;

        Node(String str)
        {
            left = right = parent = child = null;
            isChildCut = false;
            data = 0;
            tag = str;
            degree = 0;
        }
    }

   
    //Method to insert a new hashtag & corresponding value into the Fibonacci Heap
    public void InsertKey(String hashtag, int value){

        if (max == null){
            Node root = new Node(hashtag);
            max = root;
            max.left = max;
            max.right = max;
        }

        Node node = search(hashtag);

        if (node != null)
            increaseKey(node, value);

        else{
            Node x = new Node(hashtag);
            x.data += value;
            addToRoot(x);
        }
    }

   
    //Method to add a Node back into the root-list
   // When we cut a node we need to insert it back into the root node list.
    private void addToRoot(Node addThis){

        if (max == null){

            max = addThis;
            max.right = max;
            max.left = max;
        }
        else {

            max.left.right = addThis;
            addThis.left = max.left;
            addThis.right = max;
            max.left = addThis;

            if (max.data < addThis.data) // If new node is larger than old Max,set new node as new Max.
                max = addThis;
        }
    }

   // Method to Search for a hashtag from the heap
    public Node search(String tag){

        if (max.tag == tag)
            return max;

        Node head = max;
        Queue<Node> q = new LinkedList<>();
        q.add(max);

        while (max != head.right){   //add the elements into the queue until you find the hashtag
            head = head.right;

            if (head.tag == tag)
                return head;

            q.add(head);
        }

        while (!q.isEmpty()){ //Empty the queue to enter the nodes back into the heap

            head = q.remove().child;

            if (head == null)
                continue;
            else if (head.tag == tag)
                return head;

            Node current = head.right;
            q.add(head);

            while (head != current) {

                if (current.tag == tag)
                    return current;

                else {
                    current = current.right;
                    q.add(current);
                }
            }
        }

        return null;
    }

    //Method to increase the frequency of a hashtag
    private void increaseKey(Node name, int newKey){

        try{
            if (newKey < 0) //Check if the node exists or not
                throw new Exception("Invalid increase Key"); 
        }
        catch (Exception e){
            e.printStackTrace();
        }

        name.data += newKey;
        Node y = name.parent;

        if (y != null && name.data > y.data){
        	// If increased key is bigger than its parent then cut and cascading cut the parent
            cut(name, y);
            cascadingCut(y);
        }

        
        if (max.data < name.data) //Change the Maximum Node if the increased key is bigger than max.
            max = name;
    }

    
    // Method to extract max Node from the heap and rearrange the heap.
    public String removeMax(){

        
        String maxTagDetails;

        if (max == null) //Handling edge cases
            throw new NoSuchElementException("Heap is empty");

        hashtagMap.remove(max.tag);
        maxTagDetails = max.tag + ":" + max.data;

        // Add children to root list
        Node currentChild = max.child;
        Node nextChild;

        if (currentChild != null){

            while (currentChild.right != max.child ){

                nextChild = currentChild.right;

                currentChild.right = currentChild.left = null;
                currentChild.parent = null;
                addToRoot(currentChild);

                currentChild = nextChild;
            }

            currentChild.right = currentChild.left = null;
            currentChild.parent = null;
            addToRoot(currentChild);
        }


        HashMap<Integer, Node> degreeCompare = new HashMap<>(); //Hashmap to store degree of each node, for pairwise combine        
        
        //Loop to pairwise combine the elements
        if (max.right != max){

            Node neighbor = max.right;
            Node nextNeighbor, sameDegreeNode;
            while (neighbor != max){

                nextNeighbor = neighbor.right;
                neighbor.right = neighbor.left = null;
                while (degreeCompare.containsKey(neighbor.degree)) {

                    sameDegreeNode = degreeCompare.get(neighbor.degree);
                    degreeCompare.remove(neighbor.degree);

                    // Combine nodes
                    neighbor = combine(neighbor, sameDegreeNode);
                }

                degreeCompare.put(neighbor.degree, neighbor);
                neighbor = nextNeighbor;
            }
        }

        Node head, previousNeighbor = null;
        max = null;
        int newMaxData = Integer.MIN_VALUE;

        for (int key : degreeCompare.keySet()) {
        	// Check from the hashmap, degrees of the nodes

            head = degreeCompare.get(key);

            if (previousNeighbor == null)
                head.left = head.right = head;
            else {
                head.left = previousNeighbor;
                head.right = previousNeighbor.right;
                previousNeighbor.right.left = head;
                previousNeighbor.right = head;
            }

            if (head.data > newMaxData) {
                newMaxData = head.data;
                max = head;
            }

            previousNeighbor = head;
        }

        return maxTagDetails;
    }

    // Method to combine two heaps of the same degree
   // Larger of the two is set as Maximum.
    private Node combine(Node n1, Node n2){

        Node small = n1, large = n2;

        if (small.data > large.data){
            small= n2;
            large= n1;
        }

        small.parent = large;
        large.degree += 1;

        if (large.child == null) {
            large.child = small;
            small.left = small.right = small;
        }
        else{

            Node neighbor = large.child;
            neighbor.left.right = small;
            small.left = neighbor.left;

            neighbor.left = small;
            small.right = neighbor;
        }

        return large; // Larger of the two max nodes, set as the parent now.
    }

    // Method to insert Node and Frequency pair into hashmap
    public void insert(String tag, int num){

        if (hashtagMap.containsKey(tag)){

            increaseKey(hashtagMap.get(tag), num);
        }
        else{

            Node node = new Node(tag);
            node.data = num;
            addToRoot(node);
            hashtagMap.put(tag, node);
        }
    }

    //Helper method to print the heap.
    public void printHeap(){

        if (max == null) {
            System.out.print("Heap is empty");
            return;
        }

        Node head = max;
        Queue<Node> q = new LinkedList<>();
        q.add(max);

        System.out.print(max.tag + ": " + max.data + ", ");

        while (max != head.right){
            head = head.right;

            System.out.print(head.tag + ": " + head.data + ", ");
            q.add(head);
        }

        while (!q.isEmpty()){

            head = q.remove().child;

            if (head == null)
                continue;

            System.out.print(head.tag + ": " + head.data + ", ");
            Node current = head.right;
            q.add(head);

            while (head != current) {

                System.out.print(current.tag + ": " + current.data + ", ");
                q.add(current);
                current = current.right;
            }
        }

    }
    
    // Method to cut the Node from its parent
    //The node is reinserted back from the root list
    private void cut(Node current, Node parent){

        current.parent = null;
        current.isChildCut = false;

        if (parent.child == current){

            if (current != current.right)
                parent.child = current.right;
            else
                parent.child = null;
        }

        if (current != current.right){
            current.right.left = current.left;
            current.left.right = current.right;
        }

        parent.degree -= 1;
        addToRoot(current);

    }

    
  //Cascade Cut the parent node ,if its ChildCut was already true.
    
    private void cascadingCut(Node current){

        Node parent = current.parent;
        if (parent != null){

            if (current.isChildCut == false) //If child cut is false, set it to true
                current.isChildCut = true;
            else{
                cut(current, parent);
                cascadingCut(parent);
            }
        }

    }

}

