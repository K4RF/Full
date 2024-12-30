-- 기존 member 테이블 삭제
DROP TABLE IF EXISTS member CASCADE;

-- member 테이블 생성 (Role 추가)
CREATE TABLE member (
                        MEMBER_ID BIGINT AUTO_INCREMENT PRIMARY KEY,
                        LOGIN_ID VARCHAR(255) NOT NULL,
                        NICKNAME VARCHAR(255) NOT NULL,
                        PASSWORD VARCHAR(255) NOT NULL,
                        ROLE VARCHAR(20) NOT NULL DEFAULT 'USER' -- 역할 필드 추가 (기본값: USER)
);


-- 기존 book 테이블 삭제
DROP TABLE IF EXISTS book CASCADE;

-- book 테이블 생성
CREATE TABLE book (
                      book_id BIGINT AUTO_INCREMENT PRIMARY KEY,  -- 고유 ID
                      title VARCHAR(50) NOT NULL,                 -- 도서 제목
                      author VARCHAR(255) NOT NULL,               -- 저자
                      publisher VARCHAR(255) NOT NULL,            -- 출판사
                      details TEXT NOT NULL,                      -- 도서 상세 설명
                      rental_able_book BOOLEAN DEFAULT TRUE,      -- 대출 가능 여부
                      image_path VARCHAR(255),                    -- 이미지 경로
                      view_count INT DEFAULT 0,                   -- 조회수
                      publish_date DATE NOT NULL,                 -- 발행일
                      total_comment INT DEFAULT 0,                -- 댓글 갯수
                      price int DEFAULT 0              -- 도서 가격 (소수점 2자리까지)
);


-- 기존 rental 테이블 삭제
DROP TABLE IF EXISTS rental CASCADE;

-- rental 테이블 생성
CREATE TABLE rental (
                        RENTAL_ID BIGINT AUTO_INCREMENT PRIMARY KEY,
                        BOOK_ID BIGINT NOT NULL,
                        MEMBER_ID BIGINT NOT NULL,
                        RENTAL_STATUS VARCHAR(20) NOT NULL,
                        RENTAL_DATE DATE NOT NULL,
                        DUE_DATE DATE NOT NULL,                    -- 반납 마감일 필드 추가
                        RETURN_DATE DATE,                          -- 반납일
                        EXTENSION_COUNT INT DEFAULT 0 NOT NULL,    -- 연장 횟수
                        FOREIGN KEY (BOOK_ID) REFERENCES book(BOOK_ID) ON DELETE CASCADE,
                        FOREIGN KEY (MEMBER_ID) REFERENCES member(MEMBER_ID) ON DELETE CASCADE
);

-- 기존 category 테이블 삭제
DROP TABLE IF EXISTS category CASCADE;

-- category 테이블 생성
CREATE TABLE category (
                          ID BIGINT AUTO_INCREMENT PRIMARY KEY,
                          CATE VARCHAR(255) NOT NULL,
                          BOOK_ID BIGINT NOT NULL,
                          CATE_ORDER INTEGER NOT NULL DEFAULT 0, -- cate_order 필드 추가
                          UNIQUE(CATE, BOOK_ID) -- cate와 book_id의 조합에 대해 유니크 제약 조건 추가
);

-- 기존 book_category 테이블 삭제
DROP TABLE IF EXISTS book_category CASCADE;

-- book_category 조인 테이블 생성
CREATE TABLE book_category (
                               BOOK_ID BIGINT NOT NULL,
                               CATEGORY_ID BIGINT NOT NULL,
                               CATE_ORDER INTEGER NOT NULL DEFAULT 0,
                               PRIMARY KEY (BOOK_ID, CATEGORY_ID),  -- Primary key는 book_id와 category_id 조합으로 설정
                               FOREIGN KEY (BOOK_ID) REFERENCES book(BOOK_ID) ON DELETE CASCADE,
                               FOREIGN KEY (CATEGORY_ID) REFERENCES category(ID) ON DELETE CASCADE
);

-- 기존 comment 테이블 삭제
DROP TABLE IF EXISTS comment CASCADE;

-- comment 테이블 생성 (별점 수정 적용)
CREATE TABLE comment (
                         COMMENT_ID BIGINT AUTO_INCREMENT PRIMARY KEY,
                         BOOK_ID BIGINT NOT NULL,
                         WRITER VARCHAR(255) NOT NULL,
                         REVIEW TEXT NOT NULL,
                         RATING DECIMAL(2, 1) CHECK (RATING BETWEEN 0.0 AND 5.0), -- 별점 0.0~5.0 사이, 소수점 한 자리
                         FOREIGN KEY (BOOK_ID) REFERENCES book(BOOK_ID) ON DELETE CASCADE, -- book과의 관계 설정
                         REVIEW_DATE DATE NOT NULL           -- 발행일 필드

);

-- orders 테이블 삭제 (기존 테이블이 존재하면 삭제)
DROP TABLE IF EXISTS orders CASCADE;

-- orders 테이블 생성
CREATE TABLE orders (
                        order_id BIGINT AUTO_INCREMENT PRIMARY KEY, -- 고유 주문 ID
                        member_id BIGINT NOT NULL,                  -- 회원 ID
                        book_id BIGINT NOT NULL,                    -- 도서 ID
                        book_price int NOT NULL,         -- 도서 가격 (소수점 2자리까지)
                        order_date DATE NOT NULL,                   -- 주문 날짜
                        FOREIGN KEY (member_id) REFERENCES member(member_id) ON DELETE CASCADE, -- 회원 외래키
                        FOREIGN KEY (book_id) REFERENCES book(book_id) ON DELETE CASCADE        -- 도서 외래키
);
