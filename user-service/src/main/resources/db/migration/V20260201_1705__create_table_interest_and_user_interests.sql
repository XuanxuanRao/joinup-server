create table if not exists interests
(
    id          bigserial
        primary key,
    parent_id   bigint
        constraint interests_ibfk_1
            references interests
            on delete set null,
    name        varchar(100)                        not null,
    create_time timestamp default CURRENT_TIMESTAMP not null,
    constraint uk_interest_parent_name
        unique (parent_id, name)
);

comment on table interests is '兴趣和技能标签库';

comment on column interests.parent_id is '父级ID，NULL表示顶级分类 (e.g., 技术的parent_id为NULL, 开发的parent_id指向技术)';

comment on column interests.name is '兴趣/技能/分类名称 (e.g., 技术, 开发, 前端, 篮球)';

create index idx_interests_name
    on interests (name);

create index idx_interests_parent_id
    on interests (parent_id);


create table if not exists user_interests
(
    user_id     bigint not null,
    interest_id bigint not null
        constraint fk_user_interests_interest
            references interests
            on delete cascade,
    create_time timestamp default CURRENT_TIMESTAMP,
    primary key (user_id, interest_id)
);


