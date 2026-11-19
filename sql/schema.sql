-- Q2: MySQL database and tables
CREATE DATABASE IF NOT EXISTS student_skill_db;
USE student_skill_db;

CREATE TABLE IF NOT EXISTS students (
    student_id        BIGINT        NOT NULL,
    student_name      VARCHAR(100)  NOT NULL,
    email             VARCHAR(150)  NOT NULL,
    course_name       VARCHAR(100)  NOT NULL,
    programming_score DOUBLE        NOT NULL,
    mathematics_score DOUBLE        NOT NULL,
    PRIMARY KEY (student_id),                    -- prevents duplicate student IDs
    CONSTRAINT uq_student_email UNIQUE (email),
    CONSTRAINT chk_prog_score CHECK (programming_score BETWEEN 0 AND 100),
    CONSTRAINT chk_math_score CHECK (mathematics_score BETWEEN 0 AND 100)
);

CREATE TABLE IF NOT EXISTS student_skills (
    student_id BIGINT       NOT NULL,
    skill      VARCHAR(80)  NOT NULL,
    PRIMARY KEY (student_id, skill),
    CONSTRAINT fk_skill_student FOREIGN KEY (student_id)
        REFERENCES students(student_id) ON DELETE CASCADE
);

-- Sample data
INSERT INTO students VALUES
 (101,'Aarav Sharma','aarav@college.edu','B.Tech CSE',92,88),
 (102,'Diya Verma','diya@college.edu','B.Tech CSE',45,82),
 (103,'Kabir Singh','kabir@college.edu','B.Tech IT',68,55),
 (104,'Meera Nair','meera@college.edu','B.Tech CSE AI',76,79),
 (105,'Rohan Gupta','rohan@college.edu','B.Tech IT',40,30);
INSERT INTO student_skills VALUES
 (101,'Java'),(101,'Spring Boot'),(102,'Java'),(102,'Spring Boot'),
 (103,'Python'),(104,'Python'),(104,'Machine Learning'),(105,'HTML');

-- SQL equivalents of the business rules (the Java code implements the same logic)
-- 1. Average
SELECT student_id, student_name, (programming_score + mathematics_score)/2 AS average FROM students;
-- 2. Category
SELECT student_id, student_name,
  CASE WHEN (programming_score+mathematics_score)/2 >= 85 THEN 'Excellent'
       WHEN (programming_score+mathematics_score)/2 >= 70 THEN 'Good'
       WHEN (programming_score+mathematics_score)/2 >= 50 THEN 'Satisfactory'
       ELSE 'Needs Improvement' END AS category
FROM students;
-- 3. Stronger subject
SELECT student_id, student_name,
  CASE WHEN programming_score > mathematics_score THEN 'Programming'
       WHEN programming_score < mathematics_score THEN 'Mathematics'
       ELSE 'Equal' END AS stronger_subject
FROM students;
-- 4. Search
SELECT * FROM students WHERE student_id = 101 OR email = 'diya@college.edu' OR course_name = 'B.Tech IT';
-- 5. Merit list
SELECT student_id, student_name, (programming_score+mathematics_score)/2 AS average
FROM students ORDER BY average DESC;
-- 6. Students sharing the same skill
SELECT skill, GROUP_CONCAT(student_id) AS students, COUNT(*) AS cnt
FROM student_skills GROUP BY skill HAVING cnt > 1;
-- 7. Weak in programming (<50) but maths >= 75
SELECT * FROM students WHERE programming_score < 50 AND mathematics_score >= 75;
