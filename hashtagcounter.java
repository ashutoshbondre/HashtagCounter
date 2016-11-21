import java.io.*;

public class hashtagcounter {

	public static void main(String[] args) {

        hashtagcounter htc = new hashtagcounter(); // Creating a hashtagcounter object
        StringBuilder sb = new StringBuilder();   

        // Reading the input file
        try{
            BufferedReader bufferReader = new BufferedReader(new FileReader(args[0]));

            for (String line; (line = bufferReader.readLine()) != null; )
                sb.append(line + "\n");

        }
        catch (Exception e){
            System.out.println("File cannot be read: Exception");
            e.printStackTrace();
        }

        long startTime,  endTime ;
        startTime = System.currentTimeMillis(); // built in method to measure time
        htc.countHashTags(sb.toString());
        endTime = System.currentTimeMillis();  // built in method to measure time

        System.out.println("Total time required for the program is: " + (endTime - startTime)+ " Milliseconds");

    }

	
	
	// Method to first separate tags from the input file
    private static void getTags(String[] lines, MaxFiboHeap heap) throws Exception{
    	
    	int increaseBy = Integer.parseInt(lines[1]);
    	String hashtag = lines[0].split("#")[1];  //Seperating using regular expression
        
        if (hashtag != null && increaseBy >= 0)
            heap.insert(hashtag, increaseBy);
        else
            throw new Exception("Invalid Input: Either Hashtag or Key");

    }

    
    // Method to parse the query integer and produce the output ,appending the strings on a StringBuilder.
    private static String getN(int queryCount, MaxFiboHeap heap){

        String tagDetails;
        String[] segments;  // Segments will have a tag-frequency pair.
        StringBuilder answer = new StringBuilder();
        String[] tags = new String[queryCount];
        int[] tagCount = new int[queryCount];

        for (int i = 0; i < queryCount; i++){

            tagDetails = heap.removeMax();             
            segments = tagDetails.split(":");

            if ( i != queryCount-1)
                answer.append(segments[0] + ",");
            else
                answer.append(segments[0]+ "\n");

            tags[i] = segments[0];
            tagCount[i] = Integer.parseInt(segments[1]);
        }

        for (int i = 0; i < queryCount; i++){
            heap.insert(tags[i], tagCount[i]);
        }

    
        return answer.toString(); //converting StringBuilder into String adn returning it.
    }
    
    
    
    //Method to count hashtags ,which takes input as a stream of strings 
    private void countHashTags(String StreamOfString)
    {
    	String[] segments; //segments will have the split version of the input file into tags and counts
    	String[] lines = StreamOfString.split("\\n"); // Separating the input file into individual lines
        
        MaxFiboHeap heap = new MaxFiboHeap(); //Creating the heap object

        try {

            BufferedWriter out = new BufferedWriter(new FileWriter("output_file.txt"));
            out.close(); //Close the Stream
            out = new BufferedWriter(new FileWriter("output_file.txt", true)); //Writing the answers onto the output text file.

            for (String eachline : lines) {

                segments = eachline.split("\\s"); //Regular expression for white space

                if (segments.length > 1)
                   getTags(segments, heap);

                else if (segments[0].equals("STOP")) //Condition to encounter the only STOP query in the file
                    break;

                else if (segments.length == 1){

                	for (char c : segments[0].toCharArray())
                    {
                        if (!Character.isDigit(c)) throw new Exception("Invalid Query");
                    }
                	
                        out.write(getN(Integer.parseInt(segments[0]), heap));    
                }
            }

            out.close(); //Close the stream
        }
        catch(Exception error){

            error.printStackTrace();
        }
    }
}
