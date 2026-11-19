# Student Profile and Skill Development Management System
CS336B – Object Oriented Programming Using Java | MSE-II | Spring Boot + MySQL

## 1. How to run
1. Install JDK 17+, Maven, MySQL 8.
2. (Optional) run `sql/schema.sql` in MySQL Workbench. Hibernate (`ddl-auto=update`) also creates tables automatically.
3. Edit the password in `src/main/resources/application.properties`.
4. `mvn spring-boot:run` -> server on http://localhost:8080

## 2. Project structure
| Package | Purpose | Exam question |
|---|---|---|
| model/Student | Entity, encapsulation, constructors, equals/hashCode/toString, derived average/category/stronger subject | Q1 |
| sql/schema.sql, repository | MySQL tables, constraints, JPA queries | Q2 |
| service/StudentService | Business rules + collections | Q2 |
| controller/StudentController | REST APIs | Q3 |
| exception, dto | Error handling, update request | Q2/Q3 |

## 3. Requirement -> code mapping (Q2)
| # | Requirement | Where |
|---|---|---|
| 1 | Average | `Student.getAverageScore()` |
| 2 | Category | `Student.getPerformanceCategory()` |
| 3 | Stronger subject | `Student.getStrongerSubject()` |
| 4 | Search by ID/email/course | `StudentService.search()` (LinkedHashSet merges results) |
| 5 | Merit list | `StudentService.getMeritList()` (Comparator, descending, rank) |
| 6 | Same skills | `studentsBySharedSkill()` (TreeMap) and `studentsWithIdenticalSkillSets()` (Map<Set,List>) |
| 7 | Weak programming, maths >= 75 | `needsProgrammingImprovement()` (programming < 50 = "Needs Improvement" band) |
| 8 | No duplicate IDs, scores 0-100 | `@Id` + `existsById`, unique email, `@DecimalMin/@DecimalMax`, `validateScore()`, SQL CHECK |

## 4. API reference (Q3)
| Method | URL | Purpose |
|---|---|---|
| POST | /api/students | Add student |
| GET | /api/students | All students |
| GET | /api/students/{id} | Student by ID |
| PUT | /api/students/{id} | Update scores and skills |
| DELETE | /api/students/{id} | Delete |
| GET | /api/students/merit-list | Merit list |
| GET | /api/students/search?id=&email=&course= | Search |
| GET | /api/students/skills/shared | Skill -> students |
| GET | /api/students/skills/identical | Identical skill sets |
| GET | /api/students/needs-programming-improvement | Rule 7 |

Sample POST body:
```json
{
  "studentId": 106,
  "studentName": "Isha Rao",
  "email": "isha@college.edu",
  "courseName": "B.Tech CSE",
  "programmingScore": 88,
  "mathematicsScore": 91,
  "technicalSkills": ["Java", "Spring Boot", "MySQL"]
}
```
Sample PUT body: `{"programmingScore": 70, "mathematicsScore": 80, "technicalSkills": ["Java","React"]}`

Test cases to demonstrate in the viva:
- POST same studentId twice -> 409 Conflict
- POST programmingScore 120 -> 400 Bad Request with message
- GET /api/students/9999 -> 404 Not Found
- GET /api/students/merit-list -> sorted descending with ranks

## 5. Three AI prompts with brief reflections (Moodle deliverable)
**Replace the reflections with your own experience.** Examples below are drafts.

**Prompt 1:** "Design a Student entity in Java Spring Boot with ID, name, email, course, programming and mathematics scores and a list of technical skills. Use encapsulation, constructors, getters/setters, equals/hashCode/toString, and Bean Validation so scores stay between 0 and 100."
*Reflection:* The AI produced the entity quickly. I checked that the skills list needed `@ElementCollection` (a separate table), and that derived values like average belong in methods, not columns, so data is never inconsistent.

**Prompt 2:** "Write a Spring Boot service using Java collections and streams to build a merit list sorted by average descending, find students who share skills, and filter students weak in programming but with maths >= 75."
*Reflection:* The first answer used only streams. I asked for explicit Map/Set usage (TreeMap, LinkedHashSet, Map<Set,List>) because the exam asks for collections, then tested each method with sample data to verify the results.

**Prompt 3:** "Create REST controller endpoints for add, get all, get by ID, update scores/skills, delete and merit list, with a global exception handler returning 404, 409 and 400."
*Reflection:* I verified each endpoint with Postman. I corrected the order of `/merit-list` versus `/{id}` mapping and confirmed duplicate IDs and invalid scores return proper errors. AI output needs testing and understanding before submission.

## 6. Viva preparation
**Collections used and why**
- `List<String>` – ordered skills per student, duplicates allowed in storage but cleaned by code.
- `Set` (`LinkedHashSet`, `TreeSet`) – remove duplicates in search results; TreeSet gives an order-independent key for skill comparison.
- `Map` (`TreeMap`, `LinkedHashMap`, `HashMap`) – skill -> students grouping (`computeIfAbsent`); LinkedHashMap preserves JSON key order in merit rows.
- `Comparator` + `Stream.sorted` – merit list (average descending, name as tie-breaker).

**Likely questions**
1. *Why private fields + getters/setters?* Encapsulation: validation and control over access.
2. *Why a no-arg constructor?* JPA and Jackson create objects by reflection.
3. *Why compute average in a method instead of storing it?* Avoids stale/inconsistent data; DRY.
4. *How are duplicate IDs prevented?* Primary key in DB, `existsById` check in service, 409 returned.
5. *How are scores validated?* Three layers: Bean Validation annotations, `validateScore()` in service, SQL CHECK constraint.
6. *What is Dependency Injection?* Spring supplies `StudentService` to the controller via constructor.
7. *What do @RestController, @Service, @Repository do?* Layered architecture: web, business logic, data access.
8. *HashMap vs TreeMap?* HashMap unordered, O(1); TreeMap sorted by key, O(log n).
9. *Why does `/merit-list` not clash with `/{id}`?* Spring prefers the more specific literal path.
10. *Why `equals/hashCode` on studentId?* Sets treat two objects with the same ID as the same student.
