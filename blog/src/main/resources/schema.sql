CREATE SEQUENCE IF NOT EXISTS MEMBER_SEQ START WITH 1 INCREMENT BY 1;
drop table log if exists cascade;
drop table member if exists cascade;
CREATE TABLE member (
                        ID BIGINT AUTO_INCREMENT PRIMARY KEY,
                        LOGIN_ID VARCHAR(255) NOT NULL,
                        NAME VARCHAR(255) NOT NULL,
                        PASSWORD VARCHAR(255) NOT NULL
);


-- Post 테이블 생성
drop table if exists post CASCADE;
CREATE TABLE post (
                      id BIGINT AUTO_INCREMENT PRIMARY KEY,
                      login_id VARCHAR(10) NOT NULL,
                      title VARCHAR(255) NOT NULL,
                      content TEXT NOT NULL
);

-- Tag 테이블 생성
drop table if exists tag CASCADE;
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
                          tag_order INTEGER NOT NULL, -- 태그 순서를 지정하는 필드 추가
                          PRIMARY KEY (post_id, tag_id),
                          FOREIGN KEY (post_id) REFERENCES post(id) ON DELETE CASCADE,
                          FOREIGN KEY (tag_id) REFERENCES tag(id) ON DELETE CASCADE
);
drop table if exists comment CASCADE;
CREATE TABLE COMMENT (
                         ID BIGINT AUTO_INCREMENT PRIMARY KEY,
                         POST_ID BIGINT NOT NULL,
                         AUTHOR VARCHAR(255) NOT NULL,
                         COMET VARCHAR(255) NOT NULL
);