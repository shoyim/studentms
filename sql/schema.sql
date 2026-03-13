CREATE DATABASE IF NOT EXISTS student_management CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE student_management;

CREATE TABLE IF NOT EXISTS students (
                                        id         INT AUTO_INCREMENT PRIMARY KEY,
                                        student_id VARCHAR(20)  NOT NULL UNIQUE,
    full_name  VARCHAR(150) NOT NULL,
    course     TINYINT      NOT NULL CHECK (course BETWEEN 1 AND 6)
    );

CREATE TABLE IF NOT EXISTS subjects (
                                        id   INT AUTO_INCREMENT PRIMARY KEY,
                                        name VARCHAR(100) NOT NULL UNIQUE
    );

CREATE TABLE IF NOT EXISTS student_subjects (
                                                id         INT AUTO_INCREMENT PRIMARY KEY,
                                                student_id INT NOT NULL,
                                                subject_id INT NOT NULL,
                                                grade      DECIMAL(5,2) CHECK (grade BETWEEN 0 AND 100),
    FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE CASCADE,
    FOREIGN KEY (subject_id) REFERENCES subjects(id) ON DELETE CASCADE,
    UNIQUE KEY uq_student_subject (student_id, subject_id)
    );

INSERT INTO subjects (name) VALUES
                                ('Matematika'),
                                ('Fizika'),
                                ('Informatika'),
                                ('Kimyo'),
                                ('Biologiya'),
                                ('Ingliz tili'),
                                ('Dasturlash'),
                                ('Algoritmlar'),
                                ('Malumotlar bazasi'),
                                ('Web dasturlash'),
                                ('Java'),
                                ('Python');

INSERT INTO students (student_id, full_name, course) VALUES
                                                         ('ST-2024-001', 'Aliyev Jasur Bahodir', 1),
                                                         ('ST-2024-002', 'Karimova Nilufar Hamid', 1),
                                                         ('ST-2023-001', 'Toshmatov Bobur Erkin', 2),
                                                         ('ST-2023-002', 'Yusupova Malika Shuhrat', 2),
                                                         ('ST-2022-001', 'Raximov Sardor Nurbek', 3);

INSERT INTO student_subjects (student_id, subject_id, grade) VALUES
                                                                 (1, 1, 85), (1, 2, 78), (1, 3, 92),
                                                                 (2, 1, 90), (2, 4, 88), (2, 5, 76),
                                                                 (3, 8, 95), (3, 9, 89), (3, 7, 84),
                                                                 (4, 7, 72), (4, 6, 95), (4, 1, 68),
                                                                 (5, 10, 88), (5, 11, 91), (5, 3, 82);