DROP TABLE IF EXISTS member CASCADE;

-- member 테이블 생성
CREATE TABLE member (
                        MEMBER_ID BIGINT AUTO_INCREMENT PRIMARY KEY,
                        LOGIN_ID VARCHAR(255) NOT NULL,
                        NICKNAME VARCHAR(255) NOT NULL,
                        PASSWORD VARCHAR(255) NOT NULL
);

DROP TABLE IF EXISTS book CASCADE;

-- book 테이블 생성
CREATE TABLE book (
                      BOOK_ID BIGINT AUTO_INCREMENT PRIMARY KEY,
                      title VARCHAR(50) NOT NULL,
                      author VARCHAR(255) NOT NULL,
                      publisher VARCHAR(255) NOT NULL,
                      details TEXT NOT NULL
);