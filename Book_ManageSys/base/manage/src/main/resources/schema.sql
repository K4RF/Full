DROP TABLE IF EXISTS member CASCADE;

-- member 테이블 생성
CREATE TABLE member (
                        MEMBER_ID BIGINT AUTO_INCREMENT PRIMARY KEY,
                        LOGIN_ID VARCHAR(255) NOT NULL,
                        NICKNAME VARCHAR(255) NOT NULL,
                        PASSWORD VARCHAR(255) NOT NULL
);

-- 기존 테이블 삭제
DROP TABLE IF EXISTS book CASCADE;

-- book 테이블 생성
CREATE TABLE book (
                      BOOK_ID BIGINT AUTO_INCREMENT PRIMARY KEY,
                      title VARCHAR(50) NOT NULL,
                      author VARCHAR(255) NOT NULL,
                      publisher VARCHAR(255) NOT NULL,
                      details TEXT NOT NULL,
                      RENTAL_ABLE_BOOK BOOLEAN DEFAULT TRUE -- 대출 가능 여부 필드 추가
);

DROP TABLE IF EXISTS rental CASCADE;

CREATE TABLE rental (
                        rental_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                        book_id BIGINT NOT NULL,
                        member_id BIGINT NOT NULL,
                        rental_status VARCHAR(20) NOT NULL,
                        rental_date DATE NOT NULL,
                        return_date DATE, -- NULL 허용
                        FOREIGN KEY (book_id) REFERENCES book(book_id),
                        FOREIGN KEY (member_id) REFERENCES member(member_id)
);
