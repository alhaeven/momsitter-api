create table moms.account
(
    user_idx int auto_increment
        primary key,
    user_id  varchar(255)                not null,
    password varchar(255)                not null,
    role     varchar(100) default 'USER' not null,
    name     varchar(255)                not null,
    birth    date                        not null,
    gender   varchar(20)                 not null
        check (`gender` in ('m', 'f')),
    email    varchar(255)                not null,
    req_info text                        null comment 'user 추가 정보 역정규화',
    constraint account_pk2
        unique (user_id)
);

create table moms.child
(
    child_idx int auto_increment
        primary key,
    user_idx  int         not null,
    gender    varchar(20) not null,
    birth     date        not null,
    constraint child_account_user_idx_fk
        foreign key (user_idx) references moms.account (user_idx),
    constraint check_child_gender
        check (`gender` in ('f', 'm'))
);

create table moms.sitter
(
    user_idx     int             not null
        primary key,
    coverage_min int default 0   not null,
    coverage_max int default 200 not null,
    intro        text            null,
    constraint sitter_account_user_idx_fk
        foreign key (user_idx) references moms.account (user_idx)
);

create table moms.test
(
    idx int not null
        primary key,
    num int not null
);

