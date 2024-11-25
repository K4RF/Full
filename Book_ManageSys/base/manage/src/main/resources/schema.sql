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
-- category 테이블 삭제 및 생성
DROP TABLE IF EXISTS category CASCADE;

CREATE TABLE category (
                          id BIGINT AUTO_INCREMENT PRIMARY KEY,
                          cate VARCHAR(255) NOT NULL,
                          book_id BIGINT NOT NULL,
                          cate_order INTEGER NOT NULL DEFAULT 0, -- cate_order 필드 추가
                          UNIQUE(cate, book_id) -- cate와 book_id의 조합에 대해 유니크 제약 조건 추가
);

DROP TABLE IF EXISTS book_category CASCADE;

-- book_category 조인 테이블 생성
CREATE TABLE book_category(
                              book_id BIGINT NOT NULL,
                              category_id BIGINT NOT NULL,
                              cate_order INTEGER NOT NULL DEFAULT 0,
                              PRIMARY KEY (book_id, category_id),  -- Primary key는 book_id와 category_id 조합으로 설정
                              FOREIGN KEY (book_id) REFERENCES book(book_id) ON DELETE CASCADE,
                              FOREIGN KEY (category_id) REFERENCES category(id) ON DELETE CASCADE
);

