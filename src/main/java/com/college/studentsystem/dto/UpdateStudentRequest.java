package com.college.studentsystem.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.util.List;

/** Body for PUT /api/students/{id} : update scores and skills only. */
public class UpdateStudentRequest {

    @NotNull(message = "Programming score is required")
    @DecimalMin(value = "0.0", message = "Programming score must be >= 0")
    @DecimalMax(value = "100.0", message = "Programming score must be <= 100")
    private Double programmingScore;

    @NotNull(message = "Mathematics score is required")
    @DecimalMin(value = "0.0", message = "Mathematics score must be >= 0")
    @DecimalMax(value = "100.0", message = "Mathematics score must be <= 100")
    private Double mathematicsScore;

    private List<String> technicalSkills;

    public Double getProgrammingScore() { return programmingScore; }
    public void setProgrammingScore(Double programmingScore) { this.programmingScore = programmingScore; }
    public Double getMathematicsScore() { return mathematicsScore; }
    public void setMathematicsScore(Double mathematicsScore) { this.mathematicsScore = mathematicsScore; }
    public List<String> getTechnicalSkills() { return technicalSkills; }
    public void setTechnicalSkills(List<String> technicalSkills) { this.technicalSkills = technicalSkills; }
}
