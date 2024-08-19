drop table member if exists cascade;
create table member (
                        member_id varchar(10),
                        login_Id varchar(10) not null,
                        name varchar(10) not null,
                        password varchar(10) not null,
                        primary key (member_id)
);