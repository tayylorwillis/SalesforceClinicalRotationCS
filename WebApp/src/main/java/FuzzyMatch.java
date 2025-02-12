package WebApp.src.main.java;

public class FuzzyMatch {

    // Uses Levenshtein Distance
    public static int levenshteinDistance(String text, String pattern){
        int[][] doubleArray = new int[text.length() + 1][pattern.length()+1];

        for(int i =0; i<=(text.length()); i++){
            for( int j = 0; j<= pattern.length(); j++){
                if (i == 0){
                    doubleArray[i][j] = j;
                }
                else if(j ==0){
                    doubleArray[i][j] = i;
                }
                else{
                    doubleArray[i][j] = Math.min(Math.min(doubleArray[i-1][j-1] + (text.charAt(i-1) == pattern.charAt(j-1)?0:1), doubleArray[i-1][j] + 1), doubleArray[i][j-1]+1);
                }

            }
        }
        return doubleArray[text.length()][pattern.length()];
    }

    // Calculates the ratio of similarities between the two strings
    public static double similarities(String text, String pattern){
        int maximum = Math.max(text.length(), pattern.length());
        if (maximum == 0){
            return 1.0;
        }
        return 1.0 - (double) levenshteinDistance(text, pattern) / maximum;
    }

    public static void main(String[] args){
        String str1 = "puppy";
        String str2 = "puppies";
        String str3 = "kitten";
        String str4 = "cat";

        System.out.println(levenshteinDistance(str1, str2));
        System.out.println(similarities(str1, str2));
        System.out.println(levenshteinDistance(str1, str3));
        System.out.println(similarities(str1, str3));
        System.out.println(levenshteinDistance(str3, str4));
        System.out.println(similarities(str3, str4));
    }
}
