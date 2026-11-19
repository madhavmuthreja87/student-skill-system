package com.college.studentsystem.service;

import com.college.studentsystem.dto.UpdateStudentRequest;
import com.college.studentsystem.exception.DuplicateStudentException;
import com.college.studentsystem.exception.StudentNotFoundException;
import com.college.studentsystem.model.Student;
import com.college.studentsystem.repository.StudentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Q2: all business rules live here.
 * Collections used: List, Set (TreeSet / LinkedHashSet), Map (TreeMap / LinkedHashMap),
 * plus Comparator and Streams for sorting/grouping.
 */
@Service
@Transactional
public class StudentService {

    private final StudentRepository repository;

    public StudentService(StudentRepository repository) {
        this.repository = repository;
    }

    // ---------- CRUD ----------

    /** Rule 8: duplicate ID/email prevention and score validation. */
    public Student addStudent(Student student) {
        validateScore(student.getProgrammingScore(), "Programming");
        validateScore(student.getMathematicsScore(), "Mathematics");

        if (repository.existsById(student.getStudentId())) {
            throw new DuplicateStudentException("Student ID " + student.getStudentId() + " already exists");
        }
        if (repository.existsByEmailIgnoreCase(student.getEmail())) {
            throw new DuplicateStudentException("Email " + student.getEmail() + " is already registered");
        }
        student.setTechnicalSkills(cleanSkills(student.getTechnicalSkills()));
        return repository.save(student);
    }

    @Transactional(readOnly = true)
    public List<Student> getAllStudents() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public Student getById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new StudentNotFoundException("Student with ID " + id + " not found"));
    }

    public Student updateScoresAndSkills(Long id, UpdateStudentRequest req) {
        Student s = getById(id);
        validateScore(req.getProgrammingScore(), "Programming");
        validateScore(req.getMathematicsScore(), "Mathematics");
        s.setProgrammingScore(req.getProgrammingScore());
        s.setMathematicsScore(req.getMathematicsScore());
        if (req.getTechnicalSkills() != null) {
            s.setTechnicalSkills(cleanSkills(req.getTechnicalSkills()));
        }
        return repository.save(s);
    }

    public void deleteStudent(Long id) {
        Student s = getById(id);       // throws 404 if absent
        repository.delete(s);
    }

    // ---------- Rule 4: search by ID, email or course ----------

    @Transactional(readOnly = true)
    public List<Student> search(Long id, String email, String course) {
        // LinkedHashSet removes duplicates while keeping insertion order
        Set<Student> result = new LinkedHashSet<>();
        if (id != null) repository.findById(id).ifPresent(result::add);
        if (email != null && !email.isBlank()) repository.findByEmailIgnoreCase(email.trim()).ifPresent(result::add);
        if (course != null && !course.isBlank()) result.addAll(repository.findByCourseNameIgnoreCase(course.trim()));
        return new ArrayList<>(result);
    }

    // ---------- Rule 5: merit list ----------

    @Transactional(readOnly = true)
    public List<Map<String, Object>> getMeritList() {
        List<Student> sorted = repository.findAll().stream()
                .sorted(Comparator.comparingDouble(Student::getAverageScore).reversed()
                        .thenComparing(Student::getStudentName))   // tie-breaker
                .collect(Collectors.toList());

        List<Map<String, Object>> merit = new ArrayList<>();
        int rank = 0;
        for (Student s : sorted) {
            Map<String, Object> row = new LinkedHashMap<>();   // keeps key order in JSON
            row.put("rank", ++rank);
            row.put("studentId", s.getStudentId());
            row.put("studentName", s.getStudentName());
            row.put("averageScore", s.getAverageScore());
            row.put("performanceCategory", s.getPerformanceCategory());
            merit.add(row);
        }
        return merit;
    }

    // ---------- Rule 6: students who have the same technical skills ----------

    /** Skill -> students who have that skill (only skills shared by 2+ students). */
    @Transactional(readOnly = true)
    public Map<String, List<String>> studentsBySharedSkill() {
        Map<String, List<String>> skillMap = new TreeMap<>();       // sorted by skill name
        for (Student s : repository.findAll()) {
            for (String skill : s.getTechnicalSkills()) {
                skillMap.computeIfAbsent(skill.toLowerCase(), k -> new ArrayList<>())
                        .add(s.getStudentId() + " - " + s.getStudentName());
            }
        }
        skillMap.values().removeIf(list -> list.size() < 2);        // keep shared skills only
        return skillMap;
    }

    /** Groups of students whose ENTIRE skill set is identical. */
    @Transactional(readOnly = true)
    public List<List<String>> studentsWithIdenticalSkillSets() {
        Map<Set<String>, List<String>> groups = new HashMap<>();
        for (Student s : repository.findAll()) {
            Set<String> key = s.getTechnicalSkills().stream()
                    .map(String::toLowerCase)
                    .collect(Collectors.toCollection(TreeSet::new));   // order-independent key
            if (key.isEmpty()) continue;
            groups.computeIfAbsent(key, k -> new ArrayList<>())
                  .add(s.getStudentId() + " - " + s.getStudentName());
        }
        return groups.values().stream().filter(g -> g.size() > 1).collect(Collectors.toList());
    }

    // ---------- Rule 7: weak in programming, strong in maths ----------

    @Transactional(readOnly = true)
    public List<Student> needsProgrammingImprovement() {
        return repository.findAll().stream()
                .filter(s -> s.getProgrammingScore() < 50 && s.getMathematicsScore() >= 75)
                .collect(Collectors.toList());
    }

    // ---------- helpers ----------

    private void validateScore(Double score, String subject) {
        if (score == null || score < 0 || score > 100) {
            throw new IllegalArgumentException(subject + " score must be between 0 and 100");
        }
    }

    /** Trim, drop blanks, remove case-insensitive duplicates, keep order. */
    private List<String> cleanSkills(List<String> skills) {
        if (skills == null) return new ArrayList<>();
        Map<String, String> unique = new LinkedHashMap<>();
        for (String sk : skills) {
            if (sk != null && !sk.isBlank()) unique.putIfAbsent(sk.trim().toLowerCase(), sk.trim());
        }
        return new ArrayList<>(unique.values());
    }
}
