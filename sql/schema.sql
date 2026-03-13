-- ============================================================
--  Talabalar Boshqaruvi Tizimi - Ma'lumotlar Bazasi Sxemasi
--  Student Management System - Database Schema
-- ============================================================
--  MySQL uchun versiya
--  Muallif: Student Management System Loyihasi
--  Versiya: 1.0
-- ============================================================

-- Ma'lumotlar bazasini yaratish
CREATE DATABASE IF NOT EXISTS student_management
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE student_management;

-- ─── JADVAL: students ─────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS students (
                                        id          INT AUTO_INCREMENT PRIMARY KEY COMMENT 'Avtomatik raqam',
                                        student_id  VARCHAR(20)  NOT NULL UNIQUE   COMMENT 'Talaba raqami (masalan: ST-2024-001)',
    full_name   VARCHAR(150) NOT NULL           COMMENT 'To'liq ism-sharif',
    course      TINYINT      NOT NULL           COMMENT 'Kurs (1-6)',
    subjects    TEXT                            COMMENT 'Fanlar (vergul bilan ajratilgan)',
    grades      TEXT                            COMMENT 'Baholar (vergul bilan ajratilgan)',
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT 'Qo'shilgan vaqt',
    updated_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP
    ON UPDATE CURRENT_TIMESTAMP     COMMENT 'Yangilangan vaqt',

    CONSTRAINT chk_course CHECK (course BETWEEN 1 AND 6)
    ) ENGINE=InnoDB
    DEFAULT CHARSET=utf8mb4
    COLLATE=utf8mb4_unicode_ci
    COMMENT='Talabalar asosiy jadvali';

-- ─── INDEKSLAR ────────────────────────────────────────────────────────────
CREATE INDEX idx_student_id  ON students (student_id);
CREATE INDEX idx_full_name   ON students (full_name);
CREATE INDEX idx_course      ON students (course);

-- ─── NAMUNAVIY MA'LUMOTLAR ────────────────────────────────────────────────
INSERT INTO students (student_id, full_name, course, subjects, grades) VALUES
                                                                           ('ST-2024-001', 'Aliyev Jasur Bahodir o'g'li',     1, 'Matematika, Fizika, Informatika',         '85, 78, 92'),
                                                                           ('ST-2024-002', 'Karimova Nilufar Hamid qizi',      1, 'Matematika, Kimyo, Biologiya',            '90, 88, 76'),
                                                                           ('ST-2023-001', 'Toshmatov Bobur Erkin o'g'li',     2, 'Algoritmlar, Ma\'lumotlar bazasi, Tarmoq','95, 89, 84'),
('ST-2023-002', 'Yusupova Malika Shuhrat qizi',     2, 'Dasturlash, Ingliz tili, Matematika',    '72, 95, 68'),
('ST-2022-001', 'Raximov Sardor Nurbek o'g'li',     3, 'Web dasturlash, UI/UX, Java',            '88, 82, 91'),
('ST-2022-002', 'Hamidova Dildora Zafar qizi',      3, 'Python, Django, PostgreSQL',             '94, 87, 90'),
('ST-2021-001', 'Nazarov Ulugbek Tolib o'g'li',     4, 'Tizim tahlili, Loyiha boshqaruvi',       '78, 83'),
('ST-2021-002', 'Sotvoldiyeva Maftuna Anvar qizi',  4, 'Spring Boot, Microservices, Docker',     '86, 79, 88'),
('ST-2020-001', 'Ergashev Murod Ravshan o'g'li',    5, 'Machine Learning, Deep Learning',         '92, 95'),
('ST-2020-002', 'Qodirov Sirojiddin Behruz o'g'li', 6, 'Bitiruv ishi, Staj',                    '88, 96');

-- ─── FOYDALI VIEWS ────────────────────────────────────────────────────────

-- Talabalar statistikasi ko'rinishi
CREATE OR REPLACE VIEW v_students_summary AS
SELECT
    id,
                                                                            student_id,
                                                                            full_name,
                                                                            course,
                                                                            subjects,
                                                                            grades,
                                                                            created_at
                                                                                FROM students
ORDER BY course, full_name;

-- ─── STORED PROCEDURES ────────────────────────────────────────────────────

DELIMITER //

-- Barcha talabalarni olish
CREATE PROCEDURE sp_get_all_students()
BEGIN
SELECT * FROM students ORDER BY course, full_name;
END //

-- ID bo'yicha talabani topish
CREATE PROCEDURE sp_get_student_by_id(IN p_id INT)
BEGIN
SELECT * FROM students WHERE id = p_id;
END //

-- Qidirish
CREATE PROCEDURE sp_search_students(IN p_keyword VARCHAR(100))
BEGIN
SELECT * FROM students
WHERE full_name LIKE CONCAT('%', p_keyword, '%')
   OR student_id LIKE CONCAT('%', p_keyword, '%')
ORDER BY course, full_name;
END //

-- Yangi talaba qo'shish
CREATE PROCEDURE sp_add_student(
    IN p_student_id VARCHAR(20),
    IN p_full_name  VARCHAR(150),
    IN p_course     TINYINT,
    IN p_subjects   TEXT,
    IN p_grades     TEXT
)
BEGIN
INSERT INTO students (student_id, full_name, course, subjects, grades)
VALUES (p_student_id, p_full_name, p_course, p_subjects, p_grades);
SELECT LAST_INSERT_ID() AS new_id;
END //

-- Talabani yangilash
CREATE PROCEDURE sp_update_student(
    IN p_id         INT,
    IN p_student_id VARCHAR(20),
    IN p_full_name  VARCHAR(150),
    IN p_course     TINYINT,
    IN p_subjects   TEXT,
    IN p_grades     TEXT
)
BEGIN
UPDATE students
SET student_id = p_student_id,
    full_name  = p_full_name,
    course     = p_course,
    subjects   = p_subjects,
    grades     = p_grades
WHERE id = p_id;
SELECT ROW_COUNT() AS affected_rows;
END //

-- Talabani o'chirish
CREATE PROCEDURE sp_delete_student(IN p_id INT)
BEGIN
DELETE FROM students WHERE id = p_id;
SELECT ROW_COUNT() AS affected_rows;
END //

DELIMITER ;

-- ─── TEKSHIRISH SO'ROVLARI ─────────────────────────────────────────────────
-- SELECT * FROM students;
-- SELECT COUNT(*) AS jami_talabalar FROM students;
-- SELECT course, COUNT(*) AS talabalar_soni FROM students GROUP BY course ORDER BY course;
-- CALL sp_search_students('Aliyev');

-- ============================================================
--  PostgreSQL uchun ekvivalent (MySQL o'rnida foydalanish)
-- ============================================================
/*
CREATE DATABASE student_management;

\c student_management;

CREATE TABLE IF NOT EXISTS students (
    id          SERIAL PRIMARY KEY,
    student_id  VARCHAR(20)  NOT NULL UNIQUE,
    full_name   VARCHAR(150) NOT NULL,
    course      SMALLINT     NOT NULL CHECK (course BETWEEN 1 AND 6),
    subjects    TEXT,
    grades      TEXT,
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_student_id ON students (student_id);
CREATE INDEX idx_full_name  ON students (full_name);
CREATE INDEX idx_course     ON students (course);
*/