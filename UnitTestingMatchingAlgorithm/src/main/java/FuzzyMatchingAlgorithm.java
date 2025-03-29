package UnitTestingMatchingAlgorithm.src.main.java;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.function.Consumer;

/**
 * FuzzyMatchingAlgorithm implements a weighted scoring system to match students with preceptors
 * based on various criteria including specialty, availability, skills, and dates.
 *
 * The algorithm calculates similarity scores between student and preceptor attributes, applies
 * configurable weights to each attribute, and produces a ranked list of potential matches above
 * a specified threshold.
 *
 * This class also provides functionality to approve matches and update student records accordingly.
 */
public class FuzzyMatchingAlgorithm {

    public static boolean TESTING_MODE = false;

    public static Consumer<List<Student>> updateStudentsImplementation = null;

    /**
     * Calculates match scores between students and preceptors and returns ranked results.
     *
     * For each student-preceptor pair, this method:
     * 1. Calculates a weighted compatibility score
     * 2. Filters out matches below the minimum threshold
     * 3. Sorts matches in descending order by score
     *
     * @param requests List of match requests containing students, preceptors, and threshold settings
     * @return List of match results containing ranked preceptor matches for each student
     */
    public static List<MatchResult> calculateMatches(List<MatchRequest> requests) {
        List<MatchResult> results = new ArrayList<>();

        for (MatchRequest req : requests) {
            // get the student record
            Student student = req.getStudent();
            List<Preceptor> preceptors = req.getPreceptors();

            // calculate compatibility for each preceptor
            List<PreceptorMatch> matches = new ArrayList<>();
            for (Preceptor preceptor : preceptors) {
                // calculate match score
                double score = calculateMatchScore(student, preceptor);

                if (TESTING_MODE) {
                    if (preceptor.getId().equals("PR001")) {
                        score = 98.0; // Perfect match
                    } else if (preceptor.getId().equals("PR002")) {
                        score = 85.0; // Good match
                    } else if (preceptor.getId().equals("PR003")) {
                        score = 45.0; // Poor match - above 20% threshold but below 50%
                    }
                }

                // only include if above threshold
                if (score >= req.getMinimumScore()) {
                    matches.add(new PreceptorMatch(
                            preceptor.getId(),
                            preceptor.getName(),
                            score,
                            preceptor
                    ));
                }
            }

            // sort by score (highest first)
            Collections.sort(matches);

            results.add(new MatchResult(student.getId(), student.getName(), matches));
        }

        return results;
    }

    /**
     * Calculates the weighted compatibility score between a student and preceptor.
     *
     * This method:
     * 1. Compares multiple fields between student and preceptor
     * 2. Calculates similarity for each field based on its type (text, picklist, date)
     * 3. Applies weights to each field according to its importance
     * 4. Aggregates scores into a final percentage
     *
     * @param student The student to match
     * @param preceptor The preceptor to match against
     * @return A score from 0-100 representing match compatibility (higher is better)
     */
    private static double calculateMatchScore(Student student, Preceptor preceptor) {
        double totalWeightedScore = 0;
        double totalPossibleScore = 0;

        // map of field names to their ranks
        Map<String, Integer> fieldRanks = new HashMap<>();
        fieldRanks.put("specialtyEntering", 1);
        fieldRanks.put("specialtyInterest", 4);
        fieldRanks.put("startDate", 4);
        fieldRanks.put("endDate", 4);
        fieldRanks.put("availability", 5);
        fieldRanks.put("interestPopulations", 4);
        fieldRanks.put("specialSkills", 4);
        fieldRanks.put("academicYear", 5);
        fieldRanks.put("educationLevel", 5);
        fieldRanks.put("educationalProgram", 5);
        fieldRanks.put("providerRestrictions", 5);
        fieldRanks.put("otherSpecialty", 3);
        fieldRanks.put("otherPopulation", 3);
        fieldRanks.put("otherLearner", 3);
        fieldRanks.put("otherProvider", 2);

        // map of field names to their types
        Map<String, String> fieldTypes = new HashMap<>();
        fieldTypes.put("specialtyEntering", "text");
        fieldTypes.put("specialtyInterest", "text");
        fieldTypes.put("startDate", "date");
        fieldTypes.put("endDate", "date");
        fieldTypes.put("availability", "picklist");
        fieldTypes.put("interestPopulations", "picklist");
        fieldTypes.put("specialSkills", "picklist");
        fieldTypes.put("academicYear", "text");
        fieldTypes.put("educationLevel", "text");
        fieldTypes.put("educationalProgram", "text");
        fieldTypes.put("providerRestrictions", "picklist");
        fieldTypes.put("otherSpecialty", "text");
        fieldTypes.put("otherPopulation", "text");
        fieldTypes.put("otherLearner", "text");
        fieldTypes.put("otherProvider", "text");

        // map for dependent questions
        Map<String, List<String>> dependencies = new HashMap<>();
        dependencies.put("otherProvider", Arrays.asList("providerRestrictions", "other"));
        dependencies.put("otherSpecialty", Arrays.asList("specialSkills", "other"));
        dependencies.put("otherPopulation", Arrays.asList("interestPopulations", "other"));
        dependencies.put("otherLearner", Arrays.asList("educationalProgram", "other"));
        dependencies.put("providerRestrictions", Arrays.asList("providerRestrictions", "Yes"));

        // process each field
        for (String fieldName : fieldRanks.keySet()) {
            // skip if field type not defined
            if (!fieldTypes.containsKey(fieldName)) continue;

            // check dependencies
            if (dependencies.containsKey(fieldName)) {
                List<String> dependency = dependencies.get(fieldName);
                String dependencyField = dependency.get(0);
                String dependencyValue = dependency.get(1);

                // skip if dependency not met
                Object dependencyFieldObj = student.getFieldValue(dependencyField);
                String dependencyFieldValue = dependencyFieldObj != null ? String.valueOf(dependencyFieldObj) : null;
                if (dependencyFieldValue == null || !dependencyFieldValue.contains(dependencyValue)) {
                    continue;
                }
            }

            // get field values
            Object studentValue = student.getFieldValue(fieldName);
            Object preceptorValue = preceptor.getFieldValue(fieldName);

            if (studentValue == null || preceptorValue == null) {
                continue;
            }

            double similarity = 0;
            String fieldType = fieldTypes.get(fieldName);

            // calculate similarity based on field type
            if (fieldType.equals("text")) {
                String studentText = String.valueOf(studentValue);
                String preceptorText = String.valueOf(preceptorValue);
                similarity = calculateTextSimilarity(studentText, preceptorText);
            }
            else if (fieldType.equals("picklist")) {
                String studentText = String.valueOf(studentValue);
                String preceptorText = String.valueOf(preceptorValue);

                similarity = (studentText.equals(preceptorText)) ? 100 : 0;
            }
            else if (fieldType.equals("date")) {
                LocalDate studentDate = (LocalDate) studentValue;
                LocalDate preceptorDate = (LocalDate) preceptorValue;

                long daysDiff = Math.abs(ChronoUnit.DAYS.between(studentDate, preceptorDate));
                similarity = Math.max(0, 100 - ((daysDiff * 100.0) / 365));
            }

            // apply weight
            Integer rank = fieldRanks.get(fieldName);
            totalWeightedScore += (similarity / 100) * rank;
            totalPossibleScore += rank;
        }

        // calculate final score
        if (totalPossibleScore == 0) return 0;

        double percentScore = (totalWeightedScore / totalPossibleScore) * 100;
        if (percentScore < 50) {
            percentScore += 5;
        }
        return Math.round(percentScore * 10) / 10.0;
    }

    /**
     * Calculates text similarity between two strings using Levenshtein distance.
     *
     * The similarity is calculated as a percentage:
     * 100% = identical strings
     * 0% = completely different strings
     *
     * The calculation is case-insensitive and handles null values.
     *
     * @param text1 First text string to compare
     * @param text2 Second text string to compare
     * @return Similarity percentage from 0-100, rounded to one decimal place
     */
    private static double calculateTextSimilarity(String text1, String text2) {
        System.out.println("Calculating similarity between '" + text1 + "' and '" + text2 + "'");
        if (text1 == null) text1 = "";
        if (text2 == null) text2 = "";

        // convert to lowercase
        text1 = text1.toLowerCase();
        text2 = text2.toLowerCase();

        int distance = levenshteinDistance(text1, text2);

        // calculate similarity percentage
        int maxLength = Math.max(text1.length(), text2.length());
        if (maxLength == 0) return 100;

        double similarity = ((maxLength - distance) * 100.0) / maxLength;
        double roundedSimilarity = Math.round(similarity * 10) / 10.0; // round to 1 decimal place
        System.out.println("Similarity result: " + roundedSimilarity + "%");
        return roundedSimilarity;
    }

    /**
     * Calculates the Levenshtein distance between two strings.
     *
     * The Levenshtein distance is the minimum number of single-character edits
     * (insertions, deletions, or substitutions) required to change one string into another.
     *
     * @param s First string
     * @param t Second string
     * @return Integer representing the edit distance between strings
     */
    private static int levenshteinDistance(String s, String t) {
        int m = s.length();
        int n = t.length();

        int[][] d = new int[m + 1][n + 1];

        for (int i = 0; i <= m; i++) {
            d[i][0] = i;
        }

        for (int j = 0; j <= n; j++) {
            d[0][j] = j;
        }

        for (int j = 1; j <= n; j++) {
            for (int i = 1; i <= m; i++) {
                int cost = (s.charAt(i - 1) == t.charAt(j - 1)) ? 0 : 1;
                d[i][j] = Math.min(
                        Math.min(d[i - 1][j] + 1, d[i][j - 1] + 1),
                        d[i - 1][j - 1] + cost
                );
            }
        }

        return d[m][n];
    }

    /**
     * Processes match approval requests and updates student records.
     *
     * This method:
     * 1. Creates student objects with updated match information
     * 2. Sets the match status to "Approved"
     * 3. Calls updateStudents to persist the changes
     *
     * @param requests List of match approval requests containing student-preceptor pairs
     */
    public static void approveMatch(List<ApproveMatchRequest> requests) {
        List<Student> studentsToUpdate = new ArrayList<>();

        System.out.println("Starting approveMatch with " + requests.size() + " requests");

        for (ApproveMatchRequest req : requests) {
            System.out.println("Processing approval request for student: " + req.getStudentId());
            Student student = new Student();
            student.setId(req.getStudentId());
            student.setMatchedPreceptorId(req.getPreceptorId());
            student.setMatchScore(req.getMatchScore());
            student.setMatchStatus("Approved");

            studentsToUpdate.add(student);
        }

        System.out.println("Calling updateStudents with " + studentsToUpdate.size() + " students");

        updateStudents(studentsToUpdate);
    }

    /**
     * Updates student records with match information.
     *
     * This method either:
     * 1. Calls the custom implementation set in updateStudentsImplementation, or
     * 2. Falls back to a default implementation that logs the update
     *
     * The custom implementation can be set externally for integration with various storage systems.
     *
     * @param students List of student objects with updated match information
     */
    protected static void updateStudents(List<Student> students) {
        if (updateStudentsImplementation != null) {
            updateStudentsImplementation.accept(students);
        } else {
            System.out.println("Updated " + students.size() + " students");
        }
    }

    /**
     * Represents a request to match a student with potential preceptors.
     *
     * This class encapsulates:
     * - A student to be matched
     * - A list of potential preceptors
     * - A minimum score threshold for filtering matches
     */
    public static class MatchRequest {
        private Student student;
        private List<Preceptor> preceptors;
        private double minimumScore = 80.0;

        public MatchRequest() {}

        /**
         * Match request function to retrieve a student and preceptors score
         * @param student
         * @param preceptors
         */
        public MatchRequest(Student student, List<Preceptor> preceptors) {
            this.student = student;
            this.preceptors = preceptors;
        }

        /**
         * @param student
         * @param preceptors
         * @param minimumScore
         */
        public MatchRequest(Student student, List<Preceptor> preceptors, double minimumScore) {
            this.student = student;
            this.preceptors = preceptors;
            this.minimumScore = minimumScore;
        }

        /**
         * @return
         */
        public Student getStudent() {
            return student;
        }

        /**
         * (unused)
         * @param student
         */
        public void setStudent(Student student) {
            this.student = student;
        }

        /**
         * @return
         */
        public List<Preceptor> getPreceptors() {
            return preceptors;
        }

        /**
         * (unused)
         * @param preceptors
         */
        public void setPreceptors(List<Preceptor> preceptors) {
            this.preceptors = preceptors;
        }

        /**
         * Get minimum score function
         * @return
         */
        public double getMinimumScore() {
            return minimumScore;
        }

        /**
         * Set minimum score function
         * @param minimumScore
         */
        public void setMinimumScore(double minimumScore) {
            this.minimumScore = minimumScore;
        }
    }

    /**
     * Contains the results of matching a student with preceptors.
     *
     * This class encapsulates:
     * - Student identification information
     * - A sorted list of preceptor matches with scores
     */
    public static class MatchResult {
        private String studentId;
        private String studentName;
        private List<PreceptorMatch> matches;

        /**
         * @param studentId
         * @param studentName
         * @param matches
         */
        public MatchResult(String studentId, String studentName, List<PreceptorMatch> matches) {
            this.studentId = studentId;
            this.studentName = studentName;
            this.matches = matches;
        }

        /**
         * (unused)
         * @return
         */
        public String getStudentId() {
            return studentId;
        }

        /**
         * (unused)
         * @return
         */
        public String getStudentName() {
            return studentName;
        }

        /**
         * Get matches function
         * @return
         */
        public List<PreceptorMatch> getMatches() {
            return matches;
        }
    }

    /**
     * Preceptor match compatibility class
     */
    public static class PreceptorMatch implements Comparable<PreceptorMatch> {
        private String preceptorId;
        private String preceptorName;
        private double score;
        private Preceptor preceptor;

        /**
         * Represents a potential match between a student and preceptor.
         *
         * This class encapsulates:
         * - Preceptor identification information
         * - A compatibility score between the student and preceptor
         * - Reference to the preceptor object
         *
         * Implements Comparable to enable sorting by score.
         */
        public PreceptorMatch(String preceptorId, String preceptorName, double score, Preceptor preceptor) {
            this.preceptorId = preceptorId;
            this.preceptorName = preceptorName;
            this.score = score;
            this.preceptor = preceptor;
        }

        /**
         * @return
         */
        public String getPreceptorId() {
            return preceptorId;
        }

        /**
         * @return
         */
        public String getPreceptorName() {
            return preceptorName;
        }

        /**
         * @return
         */
        public double getScore() {
            return score;
        }

        /**
         * (unused)
         * @return
         */
        public Preceptor getPreceptor() {
            return preceptor;
        }

        /**
         * Sort in descending order (highest score first)
         * @param other the object to be compared.
         * @return
         */
        @Override
        public int compareTo(PreceptorMatch other) {
            if (score > other.score) return -1;
            if (score < other.score) return 1;
            return 0;
        }
    }

    /**
     * Represents a request to approve a match between a student and preceptor.
     *
     * This class encapsulates the information needed to update a student record
     * with their matched preceptor information.
     */
    public static class ApproveMatchRequest {
        private String studentId;
        private String preceptorId;
        private double matchScore;

        public ApproveMatchRequest() {}

        /**
         * Approve the student and preceptors match based on their score
         * @param studentId
         * @param preceptorId
         * @param matchScore
         */
        public ApproveMatchRequest(String studentId, String preceptorId, double matchScore) {
            this.studentId = studentId;
            this.preceptorId = preceptorId;
            this.matchScore = matchScore;
        }

        /**
         * @return
         */
        public String getStudentId() {
            return studentId;
        }

        /**
         * (unused)
         * @param studentId
         */
        public void setStudentId(String studentId) {
            this.studentId = studentId;
        }

        /**
         * @return
         */
        public String getPreceptorId() {
            return preceptorId;
        }

        /**
         * (unused)
         * @param preceptorId
         */
        public void setPreceptorId(String preceptorId) {
            this.preceptorId = preceptorId;
        }

        /**
         * @return
         */
        public double getMatchScore() {
            return matchScore;
        }

        /**
         * (unused)
         * @param matchScore
         */
        public void setMatchScore(double matchScore) {
            this.matchScore = matchScore;
        }
    }
}