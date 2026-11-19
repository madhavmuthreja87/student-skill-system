package com.college.studentsystem.controller;

import com.college.studentsystem.dto.UpdateStudentRequest;
import com.college.studentsystem.model.Student;
import com.college.studentsystem.service.StudentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/** Q3: REST APIs. Base URL: /api/students */
@RestController
@RequestMapping("/api/students")
public class StudentController {

    private final StudentService service;

    public StudentController(StudentService service) {
        this.service = service;
    }

    // 1. Add a student
    @PostMapping
    public ResponseEntity<Student> add(@Valid @RequestBody Student student) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.addStudent(student));
    }

    // 2. Retrieve all students
    @GetMapping
    public List<Student> getAll() {
        return service.getAllStudents();
    }

    // 3. Search for a student by ID
    @GetMapping("/{id}")
    public Student getById(@PathVariable Long id) {
        return service.getById(id);
    }

    // 4. Update student scores and skills
    @PutMapping("/{id}")
    public Student update(@PathVariable Long id, @Valid @RequestBody UpdateStudentRequest request) {
        return service.updateScoresAndSkills(id, request);
    }

    // 5. Delete a student record
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> delete(@PathVariable Long id) {
        service.deleteStudent(id);
        return ResponseEntity.ok(Map.of("message", "Student " + id + " deleted successfully"));
    }

    // 6. Display the merit list
    @GetMapping("/merit-list")
    public List<Map<String, Object>> meritList() {
        return service.getMeritList();
    }

    // ---- Extra endpoints for the Q2 business rules ----

    // Search by ID, email or course:  /api/students/search?email=..&course=..&id=..
    @GetMapping("/search")
    public List<Student> search(@RequestParam(required = false) Long id,
                                @RequestParam(required = false) String email,
                                @RequestParam(required = false) String course) {
        return service.search(id, email, course);
    }

    @GetMapping("/skills/shared")
    public Map<String, List<String>> sharedSkills() {
        return service.studentsBySharedSkill();
    }

    @GetMapping("/skills/identical")
    public List<List<String>> identicalSkillSets() {
        return service.studentsWithIdenticalSkillSets();
    }

    @GetMapping("/needs-programming-improvement")
    public List<Student> needsProgrammingImprovement() {
        return service.needsProgrammingImprovement();
    }

    // Handles IllegalArgumentException thrown by score validation in the service
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> badRequest(IllegalArgumentException ex) {
        return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
    }
}
