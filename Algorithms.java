/**
 * How different data types of matching work on java. create a 
 * class that has some method and that can test those sample-matching responses.
 */
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Algorithms {
	
	// This is a cosine similarity between arrays
    public static double cosineSimilarity(double[] vectorA, double[] vectorB) {
	    double dotProduct = 0.0;
	    double normA = 0.0;
	    double normB = 0.0;
	    for (int i = 0; i < vectorA.length; i++) {
	      dotProduct += vectorA[i] * vectorB[i];
	      normA += Math.pow(vectorA[i], 2);
	      normB += Math.pow(vectorB[i], 2);
	    }
	    return dotProduct / (Math.sqrt(normA) * Math.sqrt(normB));
	  }
    
    // This is a cosine similarity between strings. 
    public static double cosineTextSimilarity(String[] left, String[] right) {
        Map<String, Integer> leftWordCountMap = new HashMap<String, Integer>();
        Map<String, Integer> rightWordCountMap = new HashMap<String, Integer>();
        Set<String> uniqueSet = new HashSet<String>();
        Integer temp = null;
        for (String leftWord : left) {
            temp = leftWordCountMap.get(leftWord);
            if (temp == null) {
                leftWordCountMap.put(leftWord, 1);
                uniqueSet.add(leftWord);
            } else {
                leftWordCountMap.put(leftWord, temp + 1);
            }
        }
        for (String rightWord : right) {
            temp = rightWordCountMap.get(rightWord);
            if (temp == null) {
                rightWordCountMap.put(rightWord, 1);
                uniqueSet.add(rightWord);
            } else {
                rightWordCountMap.put(rightWord, temp + 1);
            }
        }
        int[] leftVector = new int[uniqueSet.size()];
        int[] rightVector = new int[uniqueSet.size()];
        int index = 0;
        Integer tempCount = 0;
        for (String uniqueWord : uniqueSet) {
            tempCount = leftWordCountMap.get(uniqueWord);
            leftVector[index] = tempCount == null ? 0 : tempCount;
            tempCount = rightWordCountMap.get(uniqueWord);
            rightVector[index] = tempCount == null ? 0 : tempCount;
            index++;
        }
        return cosineVectorSimilarity(leftVector, rightVector);
    }
    
    // Calculates the string similarity. 
    private static double cosineVectorSimilarity(int[] leftVector,
            int[] rightVector) {
        if (leftVector.length != rightVector.length)
            return 1;
        double dotProduct = 0;
        double leftNorm = 0;
        double rightNorm = 0;
        for (int i = 0; i < leftVector.length; i++) {
            dotProduct += leftVector[i] * rightVector[i];
            leftNorm += leftVector[i] * leftVector[i];
            rightNorm += rightVector[i] * rightVector[i];
        }

        double result = dotProduct
                / (Math.sqrt(leftNorm) * Math.sqrt(rightNorm));
        return result;
    }
	  
    
    public static void main(String[] args){
        // Cosine Similarity Values
        double[] vectorA = {1, 2, 3,4};
	    double[] vectorB = {3, 2, 1, 4};

        
        // Cosine Similarity Vector Outputs
        double cosineSimilarity = cosineSimilarity(vectorA, vectorB);
	    System.out.println("Cosine similarity: " + cosineSimilarity);
	    System.out.println(); 
	    
	    // Cosine Similarity Text Output
	    String str5[] = {"This", "is", "a", "string", "test"};
	    String str6[] = {"These", "are", "string", "tet"};
	    System.out.println("Cosine String Similarity: " + cosineTextSimilarity(str5, str6));
    
}
}


	