drop table member if exists cascade;
create table member (
                        member_id varchar(10),
                        login_id varchar(10) not null,
                        name varchar(10) not null,
                        password varchar(10) not null,
                        primary key (member_id)
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