package UnitTestingMatchingAlgorithm.src.main.java;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Preceptor {
    private String id;
    private String name;

    private Map<String, Object> fields = new HashMap<>();

    public Preceptor() {
    }

    public Preceptor(String id, String name) {
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

    public static List<Preceptor> createSamplePreceptors() {
        List<Preceptor> preceptors = new ArrayList<>();

        Preceptor preceptor1 = new Preceptor("PR001", "Dr. Jane Smith");
        preceptor1.setFieldValue("specialtyEntering", "Nursing");
        preceptor1.setFieldValue("specialtyInterest", "Pediatrics");
        preceptor1.setFieldValue("startDate", LocalDate.of(2025, 6, 1));
        preceptor1.setFieldValue("endDate", LocalDate.of(2025, 8, 30));
        preceptor1.setFieldValue("availability", "Full-time");
        preceptor1.setFieldValue("interestPopulations", "Children");
        preceptor1.setFieldValue("specialSkills", "CPR Certified");
        preceptor1.setFieldValue("academicYear", "Third Year");
        preceptor1.setFieldValue("educationLevel", "Graduate");
        preceptor1.setFieldValue("educationalProgram", "Nursing");
        preceptor1.setFieldValue("providerRestrictions", "No");
        preceptors.add(preceptor1);

        Preceptor preceptor2 = new Preceptor("PR002", "Dr. Robert Johnson");
        preceptor2.setFieldValue("specialtyEntering", "Nursing");
        preceptor2.setFieldValue("specialtyInterest", "General");
        preceptor2.setFieldValue("startDate", LocalDate.of(2025, 6, 15));
        preceptor2.setFieldValue("endDate", LocalDate.of(2025, 9, 15));
        preceptor2.setFieldValue("availability", "Part-time");
        preceptor2.setFieldValue("interestPopulations", "Adults");
        preceptor2.setFieldValue("specialSkills", "First Aid");
        preceptor2.setFieldValue("academicYear", "Third Year");
        preceptor2.setFieldValue("educationLevel", "Graduate");
        preceptor2.setFieldValue("educationalProgram", "Nursing");
        preceptor2.setFieldValue("providerRestrictions", "No");
        preceptors.add(preceptor2);

        Preceptor preceptor3 = new Preceptor("PR003", "Dr. Maria Garcia");
        preceptor3.setFieldValue("specialtyEntering", "Medicine");
        preceptor3.setFieldValue("specialtyInterest", "Surgery");
        preceptor3.setFieldValue("startDate", LocalDate.of(2025, 5, 1));
        preceptor3.setFieldValue("endDate", LocalDate.of(2025, 7, 30));
        preceptor3.setFieldValue("availability", "Part-time");
        preceptor3.setFieldValue("interestPopulations", "Elderly");
        preceptor3.setFieldValue("specialSkills", "Surgical Assistance");
        preceptor3.setFieldValue("academicYear", "Fourth Year");
        preceptor3.setFieldValue("educationLevel", "Graduate");
        preceptor3.setFieldValue("educationalProgram", "Medicine");
        preceptor3.setFieldValue("providerRestrictions", "Yes");
        preceptor3.setFieldValue("otherProvider", "Requires supervision");
        preceptors.add(preceptor3);

        return preceptors;
    }
}