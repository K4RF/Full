drop table if exists post CASCADE;
create table post
(
    id bigint generated by default as identity,
    title varchar(10),
    content varchar(256),
    login_id varchar(10),
    primary key (id)
);