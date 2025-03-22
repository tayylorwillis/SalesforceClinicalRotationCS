package UnitTestingMatchingAlgorithm.src.main.java;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class Student {
    private String id;
    private String name;
    private String matchedPreceptorId;
    private double matchScore;
    private String matchStatus;

    private Map<String, Object> fields = new HashMap<>();

    public Student() {
    }

    public Student(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMatchedPreceptorId() {
        return matchedPreceptorId;
    }

    public void setMatchedPreceptorId(String matchedPreceptorId) {
        this.matchedPreceptorId = matchedPreceptorId;
    }

    public double getMatchScore() {
        return matchScore;
    }

    public void setMatchScore(double matchScore) {
        this.matchScore = matchScore;
    }

    public String getMatchStatus() {
        return matchStatus;
    }

    public void setMatchStatus(String matchStatus) {
        this.matchStatus = matchStatus;
    }

    public void setFieldValue(String fieldName, Object value) {
        fields.put(fieldName, value);
    }

    public Object getFieldValue(String fieldName) {
        return fields.get(fieldName);
    }

    public String getFieldValue(String fieldName, String defaultValue) {
        Object value = fields.get(fieldName);
        return value != null ? String.valueOf(value) : defaultValue;
    }

    public static Student createSampleStudent() {
        Student student = new Student("ST001", "John Doe");

        student.setFieldValue("specialtyEntering", "Nursing");
        student.setFieldValue("specialtyInterest", "Pediatrics");
        student.setFieldValue("startDate", LocalDate.of(2025, 6, 1));
        student.setFieldValue("endDate", LocalDate.of(2025, 8, 30));
        student.setFieldValue("availability", "Full-time");
        student.setFieldValue("interestPopulations", "Children");
        student.setFieldValue("specialSkills", "CPR Certified");
        student.setFieldValue("academicYear", "Third Year");
        student.setFieldValue("educationLevel", "Graduate");
        student.setFieldValue("educationalProgram", "Nursing");
        student.setFieldValue("providerRestrictions", "No");

        return student;
    }
}