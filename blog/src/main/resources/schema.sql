CREATE SEQUENCE IF NOT EXISTS MEMBER_SEQ START WITH 1 INCREMENT BY 1;
DROP TABLE IF EXISTS log CASCADE;
DROP TABLE IF EXISTS member CASCADE;

CREATE TABLE member (
                        ID BIGINT AUTO_INCREMENT PRIMARY KEY,
                        LOGIN_ID VARCHAR(255) NOT NULL,
                        NAME VARCHAR(255) NOT NULL,
                        PASSWORD VARCHAR(255) NOT NULL
);

-- Post 테이블 생성
DROP TABLE IF EXISTS post CASCADE;

CREATE TABLE post (
                      id BIGINT AUTO_INCREMENT PRIMARY KEY,
                      login_id VARCHAR(10) NOT NULL,
                      author_name VARCHAR(255) NOT NULL,  -- 작성자 이름 추가
                      title VARCHAR(255) NOT NULL,
                      content TEXT NOT NULL
);

-- Tag 테이블 생성
DROP TABLE IF EXISTS tag CASCADE;

CREATE TABLE tag (
                     id BIGINT AUTO_INCREMENT PRIMARY KEY,
                     name VARCHAR(255) NOT NULL UNIQUE
);

-- 기존 post_tag 조인 테이블 삭제
DROP TABLE IF EXISTS post_tag CASCADE;

-- 수정된 post_tag 조인 테이블 생성 (ManyToMany 관계 매핑)
CREATE TABLE post_tag (
                          post_id BIGINT NOT NULL,
                          tag_id BIGINT NOT NULL,
                          tag_order INTEGER NOT NULL DEFAULT 0, -- 기본값 0으로 설정
                          PRIMARY KEY (post_id, tag_id),
                          FOREIGN KEY (post_id) REFERENCES post(id) ON DELETE CASCADE,
                          FOREIGN KEY (tag_id) REFERENCES tag(id) ON DELETE CASCADE
);

-- Comment 테이블 생성
DROP TABLE IF EXISTS comment CASCADE;

CREATE TABLE comment (
                         ID BIGINT AUTO_INCREMENT PRIMARY KEY,
                         POST_ID BIGINT NOT NULL,
                         AUTHOR VARCHAR(255) NOT NULL,
                         COMET VARCHAR(255) NOT NULL
);
