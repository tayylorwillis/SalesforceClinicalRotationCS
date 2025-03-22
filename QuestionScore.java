/*
 * calculate a score with rank and matching questions (just to show an output, may not be real data) 
 */

package match;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

class FuzzyQuestionnaire {
    private List<String> questions;
    private List<Double> weights;
    private List<String> responses;
    private Scanner scanner;

    public FuzzyQuestionnaire() {
        this.questions = new ArrayList<>();
        this.weights = new ArrayList<>();
        this.responses = new ArrayList<>();
        this.scanner = new Scanner(System.in);
    }

    public void addQuestion(String question, double weight) {
        questions.add(question);
        weights.add(weight);
    }

    public void getResponses() {
        responses.clear();
        for (String question : questions) {
            System.out.print(question + " : ");
            responses.add(scanner.nextLine().trim());
        }
    }

    public double computeConfidence() {
        if (responses.size() < 2) return 0.0;
        
        String firstResponse = responses.get(0);
        String secondResponse = responses.get(1);
        
        return firstResponse.equalsIgnoreCase(secondResponse) ? 1.0 : 0.0;
    }

    public String evaluate() {
        double confidence = computeConfidence();
        if (confidence == 1.0) {
            return "Responses match";
        } else {
            return "Responses do not match";
        }
    }

    public static void main(String[] args) {
        FuzzyQuestionnaire fuzzySystem = new FuzzyQuestionnaire();
        fuzzySystem.addQuestion("Organization Name*", 2.0);
        fuzzySystem.addQuestion("What was your specialty of choice upon entering your professional training?", 2.0);

        fuzzySystem.getResponses();
        System.out.println("Confidence Level: " + fuzzySystem.evaluate());
    }
}
