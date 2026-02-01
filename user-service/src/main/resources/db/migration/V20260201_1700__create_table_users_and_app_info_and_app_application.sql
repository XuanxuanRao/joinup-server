create table app_info
(
    id                   bigserial
        primary key,
    app_key              varchar(63)                         not null
        unique,
    app_secret           varchar(127)                        not null,
    token_expire_minutes bigint                              not null,
    deleted              boolean   default false             not null,
    enabled              boolean   default true              not null,
    create_time          timestamp default CURRENT_TIMESTAMP not null,
    update_time          timestamp default CURRENT_TIMESTAMP not null
);



create table users
(
    id           bigserial
        primary key,
    username     varchar(63)                                           not null,
    phone        char(15),
    email        varchar(255)
        constraint email
            unique,
    verified     boolean      default false                            not null,
    password     varchar(63),
    sso_password varchar(255),
    avatar       varchar(255) default 'https://joinup.oss-cn-beijing.aliyuncs.com/images/avatar/6.jpeg'::character varying,
    student_id   varchar(15),
    create_time  timestamp    default CURRENT_TIMESTAMP                not null,
    update_time  timestamp    default CURRENT_TIMESTAMP                not null,
    openid       varchar(31)
        constraint users_pk
            unique,
    role         varchar(7)   default 'USER'::bpchar                   not null,
    gender       integer      default 0                                not null,
    real_name    char(15),
    app_key      varchar(63),
    app_uuid     varchar(255),
    user_type    integer
);

comment on table users is '用户表';

comment on column users.gender is '性别，0表示未知，1表示男，2表示女';

comment on column users.real_name is '真实姓名';

comment on column users.user_type is '用户类型，0表示内部用户，1表示外部用户';

create unique index idx_users_app_key_app_uuid
    on users (app_key, app_uuid);

create index idx_users_username
    on users (username);



create table app_application
(
    id                bigserial
        primary key,
    app_key           varchar(63)                         not null,
    contact_email     varchar(255),
    applicant_user_id bigint                              not null
        references users,
    status            varchar(63)                         not null,
    create_time       timestamp default CURRENT_TIMESTAMP not null,
    update_time       timestamp default CURRENT_TIMESTAMP not null
);

create index idx_app_application_app_key
    on app_application (app_key);

create index idx_app_application_user_id
    on app_application (applicant_user_id);

