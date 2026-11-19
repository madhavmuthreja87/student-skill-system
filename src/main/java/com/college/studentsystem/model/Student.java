package com.college.studentsystem.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Q1: Student entity.
 * - Encapsulation: private fields + public getters/setters
 * - Constructors: no-arg (needed by JPA/Jackson) and parameterised
 * - Object methods: toString, equals, hashCode, plus business helper methods
 */
@Entity
@Table(name = "students")
public class Student {

    @Id
    @Column(name = "student_id")
    @NotNull(message = "Student ID is required")
    private Long studentId;                       // primary key => no duplicate IDs

    @NotBlank(message = "Student name is required")
    @Column(name = "student_name", nullable = false)
    private String studentName;

    @NotBlank(message = "Email is required")
    @Email(message = "Email format is invalid")
    @Column(nullable = false, unique = true)
    private String email;

    @NotBlank(message = "Course name is required")
    @Column(name = "course_name", nullable = false)
    private String courseName;

    @NotNull(message = "Programming score is required")
    @DecimalMin(value = "0.0", message = "Programming score must be >= 0")
    @DecimalMax(value = "100.0", message = "Programming score must be <= 100")
    @Column(name = "programming_score", nullable = false)
    private Double programmingScore;

    @NotNull(message = "Mathematics score is required")
    @DecimalMin(value = "0.0", message = "Mathematics score must be >= 0")
    @DecimalMax(value = "100.0", message = "Mathematics score must be <= 100")
    @Column(name = "mathematics_score", nullable = false)
    private Double mathematicsScore;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "student_skills", joinColumns = @JoinColumn(name = "student_id"))
    @Column(name = "skill")
    private List<String> technicalSkills = new ArrayList<>();

    public Student() { }

    public Student(Long studentId, String studentName, String email, String courseName,
                   Double programmingScore, Double mathematicsScore, List<String> technicalSkills) {
        this.studentId = studentId;
        this.studentName = studentName;
        this.email = email;
        this.courseName = courseName;
        this.programmingScore = programmingScore;
        this.mathematicsScore = mathematicsScore;
        this.technicalSkills = technicalSkills == null ? new ArrayList<>() : new ArrayList<>(technicalSkills);
    }

    // ---------- Business helper methods (derived values, not stored) ----------

    /** Rule 1: average of the two scores, rounded to 2 decimals. */
    public double getAverageScore() {
        if (programmingScore == null || mathematicsScore == null) return 0.0;
        double avg = (programmingScore + mathematicsScore) / 2.0;
        return Math.round(avg * 100.0) / 100.0;
    }

    /** Rule 2: performance category from the average. */
    public String getPerformanceCategory() {
        double avg = getAverageScore();
        if (avg >= 85) return "Excellent";
        else if (avg >= 70) return "Good";
        else if (avg >= 50) return "Satisfactory";
        else return "Needs Improvement";
    }

    /** Rule 3: stronger subject. */
    public String getStrongerSubject() {
        if (programmingScore == null || mathematicsScore == null) return "N/A";
        if (programmingScore > mathematicsScore) return "Programming";
        if (mathematicsScore > programmingScore) return "Mathematics";
        return "Equal";
    }

    // ---------- Getters and setters ----------
    public Long getStudentId() { return studentId; }
    public void setStudentId(Long studentId) { this.studentId = studentId; }

    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getCourseName() { return courseName; }
    public void setCourseName(String courseName) { this.courseName = courseName; }

    public Double getProgrammingScore() { return programmingScore; }
    public void setProgrammingScore(Double programmingScore) { this.programmingScore = programmingScore; }

    public Double getMathematicsScore() { return mathematicsScore; }
    public void setMathematicsScore(Double mathematicsScore) { this.mathematicsScore = mathematicsScore; }

    public List<String> getTechnicalSkills() { return technicalSkills; }
    public void setTechnicalSkills(List<String> technicalSkills) {
        this.technicalSkills = technicalSkills == null ? new ArrayList<>() : new ArrayList<>(technicalSkills);
    }

    // ---------- Object methods ----------
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Student)) return false;
        return Objects.equals(studentId, ((Student) o).studentId);
    }

    @Override
    public int hashCode() { return Objects.hash(studentId); }

    @Override
    public String toString() {
        return "Student{id=" + studentId + ", name='" + studentName + "', email='" + email
                + "', course='" + courseName + "', programming=" + programmingScore
                + ", mathematics=" + mathematicsScore + ", skills=" + technicalSkills + "}";
    }
}
