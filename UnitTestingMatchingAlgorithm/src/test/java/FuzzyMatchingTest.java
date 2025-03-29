package UnitTestingMatchingAlgorithm.src.test.java;

import UnitTestingMatchingAlgorithm.src.main.java.FuzzyMatchingAlgorithm;
import UnitTestingMatchingAlgorithm.src.main.java.Preceptor;
import UnitTestingMatchingAlgorithm.src.main.java.Student;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class FuzzyMatchingTest {

    private Student testStudent;
    private List<Preceptor> testPreceptors;
    private static List<Student> updatedStudents = new ArrayList<>();

    static {
        FuzzyMatchingAlgorithm.updateStudentsImplementation = students -> {
            updatedStudents.clear();
            updatedStudents.addAll(students);
            System.out.println("Updated " + students.size() + " students in test");
        };
    }

    @BeforeEach
    public void setUp() {
        updatedStudents.clear();

        testStudent = new Student("ST001", "John Doe");
        testStudent.setFieldValue("specialtyEntering", "Nursing");
        testStudent.setFieldValue("specialtyInterest", "Pediatrics");
        testStudent.setFieldValue("startDate", LocalDate.of(2025, 6, 1));
        testStudent.setFieldValue("endDate", LocalDate.of(2025, 8, 30));
        testStudent.setFieldValue("availability", "Full-time");
        testStudent.setFieldValue("interestPopulations", "Children");
        testStudent.setFieldValue("specialSkills", "CPR Certified");
        testStudent.setFieldValue("academicYear", "Third Year");
        testStudent.setFieldValue("educationLevel", "Graduate");
        testStudent.setFieldValue("educationalProgram", "Nursing");
        testStudent.setFieldValue("providerRestrictions", "No");

        testPreceptors = new ArrayList<>();

        Preceptor perfectMatch = new Preceptor("PR001", "Dr. Perfect Match");
        perfectMatch.setFieldValue("specialtyEntering", "Nursing");
        perfectMatch.setFieldValue("specialtyInterest", "Pediatrics");
        perfectMatch.setFieldValue("startDate", LocalDate.of(2025, 6, 1));
        perfectMatch.setFieldValue("endDate", LocalDate.of(2025, 8, 30));
        perfectMatch.setFieldValue("availability", "Full-time");
        perfectMatch.setFieldValue("interestPopulations", "Children");
        perfectMatch.setFieldValue("specialSkills", "CPR Certified");
        perfectMatch.setFieldValue("academicYear", "Third Year");
        perfectMatch.setFieldValue("educationLevel", "Graduate");
        perfectMatch.setFieldValue("educationalProgram", "Nursing");
        perfectMatch.setFieldValue("providerRestrictions", "No");
        testPreceptors.add(perfectMatch);

        Preceptor goodMatch = new Preceptor("PR002", "Dr. Good Match");
        goodMatch.setFieldValue("specialtyEntering", "Nursing");
        goodMatch.setFieldValue("specialtyInterest", "Pediatrics");
        goodMatch.setFieldValue("startDate", LocalDate.of(2025, 6, 15)); // 2 weeks difference
        goodMatch.setFieldValue("endDate", LocalDate.of(2025, 9, 15)); // 2 weeks difference
        goodMatch.setFieldValue("availability", "Full-time");
        goodMatch.setFieldValue("interestPopulations", "Children");
        goodMatch.setFieldValue("specialSkills", "First Aid"); // different skill
        goodMatch.setFieldValue("academicYear", "Third Year");
        goodMatch.setFieldValue("educationLevel", "Graduate");
        goodMatch.setFieldValue("educationalProgram", "Nursing");
        goodMatch.setFieldValue("providerRestrictions", "No");
        testPreceptors.add(goodMatch);

        Preceptor poorMatch = new Preceptor("PR003", "Dr. Poor Match");
        poorMatch.setFieldValue("specialtyEntering", "Medicine"); // different specialty
        poorMatch.setFieldValue("specialtyInterest", "Surgery"); // different interest
        poorMatch.setFieldValue("startDate", LocalDate.of(2025, 5, 1)); // 1 month difference
        poorMatch.setFieldValue("endDate", LocalDate.of(2025, 7, 30)); // 1 month difference
        poorMatch.setFieldValue("availability", "Part-time"); // different availability
        poorMatch.setFieldValue("interestPopulations", "Adults"); // different population
        poorMatch.setFieldValue("specialSkills", "Surgical Assistance"); // different skill
        poorMatch.setFieldValue("academicYear", "Fourth Year"); // different year
        poorMatch.setFieldValue("educationLevel", "Graduate");
        poorMatch.setFieldValue("educationalProgram", "Medicine"); // different program
        poorMatch.setFieldValue("providerRestrictions", "No");
        testPreceptors.add(poorMatch);
    }

    /**
     * Tests that the matching algorithm correctly identifies and sorts matches based on compatibility scores.
     * This test verifies:
     * 1. That at least one match is found when using a 70% minimum threshold
     * 2. That the perfect match (PR001) is ranked first with a score > 95%
     * 3. That all matches are sorted in descending order by score
     *
     * @see FuzzyMatchingAlgorithm#calculateMatches(List)
     */
    @Test
    public void testMatchingAlgorithmScoreCalculation() {
        FuzzyMatchingAlgorithm.TESTING_MODE = true;

        FuzzyMatchingAlgorithm.MatchRequest request = new FuzzyMatchingAlgorithm.MatchRequest(testStudent, testPreceptors);
        request.setMinimumScore(70.0);
        List<FuzzyMatchingAlgorithm.MatchRequest> requests = new ArrayList<>();
        requests.add(request);

        List<FuzzyMatchingAlgorithm.MatchResult> results = FuzzyMatchingAlgorithm.calculateMatches(requests);

        assertEquals(1, results.size(), "Should return one result for one student");
        List<FuzzyMatchingAlgorithm.PreceptorMatch> matches = results.get(0).getMatches();

        // verify perfect match is first with high score
        assertTrue(matches.size() >= 1, "Should have at least one match");
        assertEquals("PR001", matches.get(0).getPreceptorId(), "Perfect match should be first");
        assertTrue(matches.get(0).getScore() > 95.0, "Perfect match should have score above 95%");

        // verify matches are in descending order by score
        for (int i = 0; i < matches.size() - 1; i++) {
            assertTrue(matches.get(i).getScore() >= matches.get(i+1).getScore(),
                    "Matches should be sorted by score in descending order");
        }
    }

    /**
     * Tests the text similarity calculation functionality with various input scenarios.
     * This test accesses the private method calculateTextSimilarity through reflection.
     * This test verifies the following cases:
     * 1. Identical strings should have 100% similarity
     * 2. Strings with one character different have expected similarity
     * 3. Completely different strings have near 0% similarity
     * 4. Empty strings have 100% similarity
     * 5. Case differences are ignored (case insensitive comparison)
     */
    @Test
    public void testTextSimilarityCalculation() {
        System.out.println("\n--- Starting Text Similarity Test ---");

        class TestHelper extends FuzzyMatchingAlgorithm {
            public double testTextSimilarity(String s1, String s2) {
                try {
                    java.lang.reflect.Method method = FuzzyMatchingAlgorithm.class.getDeclaredMethod(
                            "calculateTextSimilarity", String.class, String.class);
                    method.setAccessible(true);
                    return (double) method.invoke(null, s1, s2);
                } catch (Exception e) {
                    System.out.println("Error in test helper: " + e);
                    return -1.0;
                }
            }
        }

        TestHelper helper = new TestHelper();

        try {
            double identicalScore = helper.testTextSimilarity("test", "test");
            System.out.println("Identical strings 'test' vs 'test': " + identicalScore + "%");
            assertEquals(100.0, identicalScore, 0.1, "Identical strings should have 100% similarity");

            double oneCharDiffScore = helper.testTextSimilarity("test", "text");
            System.out.println("Similar strings 'test' vs 'text': " + oneCharDiffScore + "%");
            assertEquals(75.0, oneCharDiffScore, 0.1, "One character different in 4-char string should be 75%");

            double differentScore = helper.testTextSimilarity("test", "abcd");
            System.out.println("Different strings 'test' vs 'abcd': " + differentScore + "%");
            assertEquals(0.0, differentScore, 0.1, "Completely different strings should have low similarity");

            double emptyScore = helper.testTextSimilarity("", "");
            System.out.println("Empty strings '' vs '': " + emptyScore + "%");
            assertEquals(100.0, emptyScore, 0.1, "Empty strings should have 100% similarity");

            // case insensitivity
            double caseScore = helper.testTextSimilarity("Test", "test");
            System.out.println("Case different 'Test' vs 'test': " + caseScore + "%");
            assertEquals(100.0, caseScore, 0.1, "Should be case insensitive");
        } catch (Exception e) {
            System.out.println("Exception in test main body: " + e.getMessage());
            e.printStackTrace();
            fail("Exception occurred while testing calculateTextSimilarity: " + e.getMessage());
        }

        System.out.println("--- Finished Text Similarity Test ---\n");
    }

    /**
     * Tests that the threshold filtering mechanism correctly includes or excludes matches
     * based on the minimum score threshold. This test verifies:
     * 1. High threshold (90%) should only include high-scoring matches
     * 2. Low threshold (20%) should include all preceptors
     * 3. Poor matches are correctly filtered out by high thresholds
     *
     * @see FuzzyMatchingAlgorithm#calculateMatches(List)
     */
    @Test
    public void testThresholdFiltering() {
        System.out.println("\n--- Starting Threshold Filtering Test ---");

        FuzzyMatchingAlgorithm.TESTING_MODE = true;
        System.out.println("Testing mode enabled to force scoring");

        // high threshold test
        FuzzyMatchingAlgorithm.MatchRequest highRequest =
                new FuzzyMatchingAlgorithm.MatchRequest(testStudent, testPreceptors);
        highRequest.setMinimumScore(90.0); // High threshold
        System.out.println("Created high threshold request with minimum score: 90.0");

        // low threshold test
        FuzzyMatchingAlgorithm.MatchRequest lowRequest =
                new FuzzyMatchingAlgorithm.MatchRequest(testStudent, testPreceptors);
        lowRequest.setMinimumScore(20.0); // Very low threshold to include poor match
        System.out.println("Created low threshold request with minimum score: 20.0");

        List<FuzzyMatchingAlgorithm.MatchRequest> highRequests = new ArrayList<>();
        highRequests.add(highRequest);
        System.out.println("Calculating high threshold matches...");
        List<FuzzyMatchingAlgorithm.MatchResult> highResults =
                FuzzyMatchingAlgorithm.calculateMatches(highRequests);

        List<FuzzyMatchingAlgorithm.MatchRequest> lowRequests = new ArrayList<>();
        lowRequests.add(lowRequest);
        System.out.println("Calculating low threshold matches...");
        List<FuzzyMatchingAlgorithm.MatchResult> lowResults =
                FuzzyMatchingAlgorithm.calculateMatches(lowRequests);

        List<FuzzyMatchingAlgorithm.PreceptorMatch> highMatches = highResults.get(0).getMatches();
        List<FuzzyMatchingAlgorithm.PreceptorMatch> lowMatches = lowResults.get(0).getMatches();

        System.out.println("High threshold matches: " + highMatches.size());
        for (FuzzyMatchingAlgorithm.PreceptorMatch match : highMatches) {
            System.out.println("  " + match.getPreceptorId() + " (" + match.getPreceptorName() + "): " + match.getScore() + "%");
        }

        System.out.println("Low threshold matches: " + lowMatches.size());
        for (FuzzyMatchingAlgorithm.PreceptorMatch match : lowMatches) {
            System.out.println("  " + match.getPreceptorId() + " (" + match.getPreceptorName() + "): " + match.getScore() + "%");
        }

        System.out.println("\nChecking which preceptors are in each list:");

        boolean pr001InHigh = false, pr002InHigh = false, pr003InHigh = false;
        for (FuzzyMatchingAlgorithm.PreceptorMatch match : highMatches) {
            if (match.getPreceptorId().equals("PR001")) pr001InHigh = true;
            if (match.getPreceptorId().equals("PR002")) pr002InHigh = true;
            if (match.getPreceptorId().equals("PR003")) pr003InHigh = true;
        }
        System.out.println("High threshold contains PR001: " + pr001InHigh);
        System.out.println("High threshold contains PR002: " + pr002InHigh);
        System.out.println("High threshold contains PR003: " + pr003InHigh);

        boolean pr001InLow = false, pr002InLow = false, pr003InLow = false;
        for (FuzzyMatchingAlgorithm.PreceptorMatch match : lowMatches) {
            if (match.getPreceptorId().equals("PR001")) pr001InLow = true;
            if (match.getPreceptorId().equals("PR002")) pr002InLow = true;
            if (match.getPreceptorId().equals("PR003")) pr003InLow = true;
        }
        System.out.println("Low threshold contains PR001: " + pr001InLow);
        System.out.println("Low threshold contains PR002: " + pr002InLow);
        System.out.println("Low threshold contains PR003: " + pr003InLow);

        // all high threshold matches should have scores >= 90
        for (FuzzyMatchingAlgorithm.PreceptorMatch match : highMatches) {
            assertTrue(match.getScore() >= 90.0,
                    "All matches with high threshold should have scores above threshold");
        }

        // verify the poor match is included in the low threshold results
        boolean poorMatchInLow = false;

        for (FuzzyMatchingAlgorithm.PreceptorMatch match : lowMatches) {
            if (match.getPreceptorId().equals("PR003")) {
                poorMatchInLow = true;
                break;
            }
        }

        System.out.println("\nFinal assertions:");
        System.out.println("Checking if poor match (PR003) is in low threshold results: " + poorMatchInLow);
        assertTrue(poorMatchInLow, "Poor match should be included with low threshold");

        System.out.println("Checking if low threshold includes all preceptors. Found: " + lowMatches.size() + ", Expected: at least 3");
        assertTrue(lowMatches.size() >= 3, "Low threshold should include all preceptors");

        System.out.println("--- Finished Threshold Filtering Test ---\n");
    }

    /**
     * Tests that the match approval process correctly updates student records.
     * This test verifies:
     * 1. The correct number of students are updated (exactly one)
     * 2. The updated student has the correct ID, preceptor ID, match score and status
     *
     * @see FuzzyMatchingAlgorithm#approveMatch(List)
     */
    @Test
    public void testMatchApproval() {
        System.out.println("\n--- Starting Match Approval Test ---");

        updatedStudents.clear();
        System.out.println("Cleared updatedStudents list. Size: " + updatedStudents.size());

        String studentId = "ST001";
        String preceptorId = "PR001";
        double matchScore = 98.5;
        System.out.println("Creating approval request for student: " + studentId + ", preceptor: " + preceptorId);

        FuzzyMatchingAlgorithm.ApproveMatchRequest request =
                new FuzzyMatchingAlgorithm.ApproveMatchRequest(studentId, preceptorId, matchScore);
        List<FuzzyMatchingAlgorithm.ApproveMatchRequest> requests = new ArrayList<>();
        requests.add(request);

        // directly call approveMatch
        System.out.println("Calling approveMatch...");
        FuzzyMatchingAlgorithm.approveMatch(requests);

        System.out.println("Updated students size after approveMatch: " + updatedStudents.size());
        if (!updatedStudents.isEmpty()) {
            Student s = updatedStudents.get(0);
            System.out.println("First updated student: ID=" + s.getId() +
                    ", matchedPreceptor=" + s.getMatchedPreceptorId() +
                    ", score=" + s.getMatchScore() +
                    ", status=" + s.getMatchStatus());
        } else {
            System.out.println("WARNING: No students were updated!");
            System.out.println("Checking if updateStudentsImplementation is set: " +
                    (FuzzyMatchingAlgorithm.updateStudentsImplementation != null));
        }

        assertEquals(1, updatedStudents.size(), "One student should be updated");

        if (!updatedStudents.isEmpty()) {
            Student updatedStudent = updatedStudents.get(0);

            assertEquals(studentId, updatedStudent.getId(),
                    "Updated student should have correct ID");
            assertEquals(preceptorId, updatedStudent.getMatchedPreceptorId(),
                    "Updated student should have matched preceptor ID");
            assertEquals(matchScore, updatedStudent.getMatchScore(), 0.1,
                    "Updated student should have correct match score");
            assertEquals("Approved", updatedStudent.getMatchStatus(),
                    "Updated student should have status set to Approved");
        }

        System.out.println("--- Finished Match Approval Test ---\n");
    }
}