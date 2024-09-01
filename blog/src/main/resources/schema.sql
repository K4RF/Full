CREATE SEQUENCE IF NOT EXISTS MEMBER_SEQ START WITH 1 INCREMENT BY 1;

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

-- post_tag 조인 테이블 생성 (ManyToMany 관계 매핑)
drop table if exists post_tag CASCADE;
CREATE TABLE post_tag (
                          post_id BIGINT NOT NULL,
                          tag_id BIGINT NOT NULL,
                          PRIMARY KEY (post_id, tag_id),
                          FOREIGN KEY (post_id) REFERENCES post(id) ON DELETE CASCADE,
                          FOREIGN KEY (tag_id) REFERENCES tag(id) ON DELETE CASCADE
);