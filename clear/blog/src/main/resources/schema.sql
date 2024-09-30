-- 기존 시퀀스 생성 (필요한 경우)
CREATE SEQUENCE IF NOT EXISTS MEMBER_SEQ START WITH 1 INCREMENT BY 1;

-- 기존 테이블 삭제
DROP TABLE IF EXISTS log CASCADE;
DROP TABLE IF EXISTS member CASCADE;
DROP TABLE IF EXISTS post CASCADE;
DROP TABLE IF EXISTS tag CASCADE;
DROP TABLE IF EXISTS post_tag CASCADE;
DROP TABLE IF EXISTS comment CASCADE;

-- member 테이블 생성
CREATE TABLE member (
                        ID BIGINT AUTO_INCREMENT PRIMARY KEY,
                        LOGIN_ID VARCHAR(255) NOT NULL,
                        NAME VARCHAR(255) NOT NULL,
                        PASSWORD VARCHAR(255) NOT NULL
);

-- post 테이블 생성
CREATE TABLE post (
                      id BIGINT AUTO_INCREMENT PRIMARY KEY,
                      login_id VARCHAR(10) NOT NULL,
                      author_name VARCHAR(255) NOT NULL,
                      title VARCHAR(255) NOT NULL,
                      content TEXT NOT NULL,
                      view_count INT DEFAULT 0,
                      created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- tag 테이블 생성 (post_id 추가)
CREATE TABLE tag (
                     id BIGINT AUTO_INCREMENT PRIMARY KEY,
                     name VARCHAR(255) NOT NULL,
                     post_id BIGINT NOT NULL,
                     UNIQUE(name, post_id)  -- name과 post_id의 조합에 대해 유니크 제약 조건 추가
);

-- post_tag 조인 테이블 생성
CREATE TABLE post_tag (
                          post_id BIGINT NOT NULL,
                          tag_id BIGINT NOT NULL,
                          tag_order INTEGER NOT NULL DEFAULT 0,
                          PRIMARY KEY (post_id, tag_id),
                          FOREIGN KEY (post_id) REFERENCES post(id) ON DELETE CASCADE,
                          FOREIGN KEY (tag_id) REFERENCES tag(id) ON DELETE CASCADE
);

-- comment 테이블 생성
CREATE TABLE comment (
                         ID BIGINT AUTO_INCREMENT PRIMARY KEY,
                         POST_ID BIGINT NOT NULL,
                         AUTHOR VARCHAR(255) NOT NULL,
                         COMET TEXT NOT NULL
);
