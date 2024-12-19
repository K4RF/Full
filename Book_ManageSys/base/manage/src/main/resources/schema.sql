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
                      BOOK_ID BIGINT AUTO_INCREMENT PRIMARY KEY,
                      TITLE VARCHAR(50) NOT NULL,
                      AUTHOR VARCHAR(255) NOT NULL,
                      PUBLISHER VARCHAR(255) NOT NULL,
                      DETAILS TEXT NOT NULL,
                      RENTAL_ABLE_BOOK BOOLEAN DEFAULT TRUE, -- 대출 가능 여부 필드
                      IMAGE_PATH VARCHAR(255),              -- 이미지 경로 필드
                      VIEW_COUNT INT DEFAULT 0,             -- 조회수 필드
                      PUBLISH_DATE DATE NOT NULL,            -- 발행일 필드
                      TOTAL_COMMENT INT DEFAULT 0           -- 댓글 갯수 필드
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
                        RETURN_DATE DATE, -- NULL 허용
                        FOREIGN KEY (BOOK_ID) REFERENCES book(BOOK_ID),
                        FOREIGN KEY (MEMBER_ID) REFERENCES member(MEMBER_ID)
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