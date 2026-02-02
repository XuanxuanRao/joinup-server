create table announcements
(
    id               bigserial
        primary key,
    title            varchar(255)                        not null,
    content          text                                not null,
    poster_user_id   bigint                              not null,
    create_time      timestamp default CURRENT_TIMESTAMP not null,
    cover            varchar(255),
    deleted          boolean   default false             not null,
    update_time      timestamp default CURRENT_TIMESTAMP not null,
    target_user_type varchar(31),
    target_app_keys  varchar(63)[]
);


create table conversations
(
    id          varchar(36) not null
        primary key,
    type        varchar(20) not null,
    team_id     bigint,
    create_time timestamp   not null
);

comment on column conversations.id is '对话唯一ID，使用UUID';

comment on column conversations.type is '对话类型: private (私聊), group (群聊)';

comment on column conversations.team_id is '如果type是group，关联的团队/群组ID，可为空';

comment on column conversations.create_time is '对话创建时间';


create index idx_conversations_team_id
    on conversations (team_id);

create index idx_conversations_type
    on conversations (type);

create table chat_messages
(
    id              bigserial
        primary key,
    conversation_id varchar(36) not null
        constraint chat_messages_ibfk_1
            references conversations,
    type            integer     not null,
    content         jsonb,
    sender_id       bigint      not null,
    create_time     timestamp   not null
);

comment on column chat_messages.id is '消息主键ID，自增长';

comment on column chat_messages.conversation_id is '所属会话ID，关联 conversations 表';

comment on column chat_messages.type is '消息类型';

comment on column chat_messages.content is '消息内容，使用JSON格式存储灵活数据';

comment on column chat_messages.sender_id is '发送消息的用户ID';

comment on column chat_messages.create_time is '消息发送时间';


create index idx_chat_messages_conv_time
    on chat_messages (conversation_id asc, create_time desc);

create index idx_chat_messages_sender_id
    on chat_messages (sender_id);

create table conversation_participants
(
    conversation_id varchar(36) not null
        constraint conversation_participants_ibfk_1
            references conversations,
    user_id         bigint      not null,
    create_time     timestamp   not null,
    constraint uk_conversation_user
        unique (conversation_id, user_id)
);

comment on column conversation_participants.conversation_id is '关联的对话ID';

comment on column conversation_participants.user_id is '参与对话的用户ID';

comment on column conversation_participants.create_time is '参与记录创建时间';


create table email_log
(
    id          bigserial
        primary key,
    sender      varchar(255) not null,
    receiver    varchar(255) not null,
    subject     varchar(255),
    create_time timestamp default CURRENT_TIMESTAMP
);


create table feedbacks
(
    id          bigserial
        primary key,
    user_id     bigint                              not null,
    subject     varchar(255)                        not null,
    content     text,
    contact     varchar(255),
    create_time timestamp default CURRENT_TIMESTAMP not null,
    handled     boolean   default false             not null
);

comment on column feedbacks.handled is '反馈是否已经完成处理';


create index idx_feedbacks_user_id
    on feedbacks (user_id);

create table log_entries
(
    id          bigserial
        primary key,
    path        varchar(255)                        not null,
    method      varchar(10)                         not null,
    user_id     bigint,
    ip          varchar(45)                         not null,
    status      integer                             not null,
    duration    bigint                              not null,
    create_time timestamp default CURRENT_TIMESTAMP not null
);

comment on table log_entries is '请求日志记录表';


create index idx_log_entries_path
    on log_entries (path);

create index idx_log_entries_user_id
    on log_entries (user_id);

create table message_template
(
    id            bigserial
        primary key,
    code          varchar(255)                        not null,
    template      text,
    encoding      varchar(32)                         not null,
    create_time   timestamp default CURRENT_TIMESTAMP not null,
    resource_path varchar(63),
    title         varchar(255)
);


create table site_messages
(
    id               bigserial
        primary key,
    title            varchar(255),
    content          text,
    receiver_user_id bigint  not null,
    sender_user_id   bigint,
    notify_type      integer not null,
    read             boolean   default false,
    deleted          boolean   default false,
    create_time      timestamp default CURRENT_TIMESTAMP,
    read_time        timestamp
);

comment on table site_messages is '站内消息表';

comment on column site_messages.receiver_user_id is '接收用户id（必须）';

comment on column site_messages.sender_user_id is '发送用户id（可选）';

comment on column site_messages.notify_type is '通知类型，0-组队通知，1-课程通知，2-博雅通知';

comment on column site_messages.read is '是否已读（0-未读，1-已读）';

comment on column site_messages.deleted is '逻辑删除标记（0-未删除，1-已删除）';

comment on column site_messages.create_time is '接收时间';

comment on column site_messages.read_time is '阅读时间';


create table verify_logs
(
    id          bigserial
        primary key,
    account     varchar(255)                        not null,
    channel     integer                             not null,
    create_time timestamp default CURRENT_TIMESTAMP not null
);

comment on table verify_logs is '验证码日志表';


create table splash_resource
(
    id           bigserial
        primary key,
    title        varchar(255)                        not null,
    resource_url varchar(1024)                       not null,
    click_action integer,
    click_url    varchar(1024),
    duration     bigint    default 3000              not null,
    enabled      boolean   default true,
    deleted      boolean   default false,
    create_time  timestamp default CURRENT_TIMESTAMP not null,
    update_time  timestamp default CURRENT_TIMESTAMP not null
);

comment on table splash_resource is '开屏页资源';

comment on column splash_resource.click_action is '点击行为（对应 ClickAction 枚举）';

comment on column splash_resource.duration is '展示时长，单位毫秒';


create table splash_strategy
(
    id               bigserial
        primary key,
    resource_id      bigint not null
        constraint fk_splash_strategy_resource
            references splash_resource,
    start_time       timestamp,
    end_time         timestamp,
    priority         integer,
    target_platforms text[],
    enabled          boolean   default true,
    deleted          boolean   default false,
    create_time      timestamp default CURRENT_TIMESTAMP,
    update_time      timestamp default CURRENT_TIMESTAMP
);

comment on table splash_strategy is '投放策略';

comment on column splash_strategy.priority is '优先级，数值越大优先级越高';

comment on column splash_strategy.target_platforms is '目标平台列表';


create table qrtz_job_details
(
    sched_name        varchar(120) not null,
    job_name          varchar(200) not null,
    job_group         varchar(200) not null,
    description       varchar(250),
    job_class_name    varchar(250) not null,
    is_durable        boolean      not null,
    is_nonconcurrent  boolean      not null,
    is_update_data    boolean      not null,
    requests_recovery boolean      not null,
    job_data          bytea,
    primary key (sched_name, job_name, job_group)
);


create index idx_qrtz_j_req_recovery
    on qrtz_job_details (sched_name, requests_recovery);

create index idx_qrtz_j_grp
    on qrtz_job_details (sched_name, job_group);

create table qrtz_triggers
(
    sched_name     varchar(120) not null,
    trigger_name   varchar(200) not null,
    trigger_group  varchar(200) not null,
    job_name       varchar(200) not null,
    job_group      varchar(200) not null,
    description    varchar(250),
    next_fire_time bigint,
    prev_fire_time bigint,
    priority       integer,
    trigger_state  varchar(16)  not null,
    trigger_type   varchar(8)   not null,
    start_time     bigint       not null,
    end_time       bigint,
    calendar_name  varchar(200),
    misfire_instr  smallint,
    job_data       bytea,
    primary key (sched_name, trigger_name, trigger_group),
    foreign key (sched_name, job_name, job_group) references qrtz_job_details
);


create index idx_qrtz_t_j
    on qrtz_triggers (sched_name, job_name, job_group);

create index idx_qrtz_t_jg
    on qrtz_triggers (sched_name, job_group);

create index idx_qrtz_t_c
    on qrtz_triggers (sched_name, calendar_name);

create index idx_qrtz_t_g
    on qrtz_triggers (sched_name, trigger_group);

create index idx_qrtz_t_state
    on qrtz_triggers (sched_name, trigger_state);

create index idx_qrtz_t_n_state
    on qrtz_triggers (sched_name, trigger_name, trigger_group, trigger_state);

create index idx_qrtz_t_n_g_state
    on qrtz_triggers (sched_name, trigger_group, trigger_state);

create index idx_qrtz_t_next_fire_time
    on qrtz_triggers (sched_name, next_fire_time);

create index idx_qrtz_t_nft_st
    on qrtz_triggers (sched_name, trigger_state, next_fire_time);

create index idx_qrtz_t_nft_misfire
    on qrtz_triggers (sched_name, misfire_instr, next_fire_time);

create index idx_qrtz_t_nft_st_misfire
    on qrtz_triggers (sched_name, misfire_instr, next_fire_time, trigger_state);

create index idx_qrtz_t_nft_st_misfire_grp
    on qrtz_triggers (sched_name, misfire_instr, next_fire_time, trigger_group, trigger_state);

create table qrtz_simple_triggers
(
    sched_name      varchar(120) not null,
    trigger_name    varchar(200) not null,
    trigger_group   varchar(200) not null,
    repeat_count    bigint       not null,
    repeat_interval bigint       not null,
    times_triggered bigint       not null,
    primary key (sched_name, trigger_name, trigger_group),
    foreign key (sched_name, trigger_name, trigger_group) references qrtz_triggers
);


create table qrtz_cron_triggers
(
    sched_name      varchar(120) not null,
    trigger_name    varchar(200) not null,
    trigger_group   varchar(200) not null,
    cron_expression varchar(120) not null,
    time_zone_id    varchar(80),
    primary key (sched_name, trigger_name, trigger_group),
    foreign key (sched_name, trigger_name, trigger_group) references qrtz_triggers
);


create table qrtz_simprop_triggers
(
    sched_name    varchar(120) not null,
    trigger_name  varchar(200) not null,
    trigger_group varchar(200) not null,
    str_prop_1    varchar(512),
    str_prop_2    varchar(512),
    str_prop_3    varchar(512),
    int_prop_1    integer,
    int_prop_2    integer,
    long_prop_1   bigint,
    long_prop_2   bigint,
    dec_prop_1    numeric(13, 4),
    dec_prop_2    numeric(13, 4),
    bool_prop_1   boolean,
    bool_prop_2   boolean,
    primary key (sched_name, trigger_name, trigger_group),
    constraint qrtz_simprop_triggers_sched_name_trigger_name_trigger_grou_fkey
        foreign key (sched_name, trigger_name, trigger_group) references qrtz_triggers
);


create table qrtz_blob_triggers
(
    sched_name    varchar(120) not null,
    trigger_name  varchar(200) not null,
    trigger_group varchar(200) not null,
    blob_data     bytea,
    primary key (sched_name, trigger_name, trigger_group),
    foreign key (sched_name, trigger_name, trigger_group) references qrtz_triggers
);


create table qrtz_calendars
(
    sched_name    varchar(120) not null,
    calendar_name varchar(200) not null,
    calendar      bytea        not null,
    primary key (sched_name, calendar_name)
);


create table qrtz_paused_trigger_grps
(
    sched_name    varchar(120) not null,
    trigger_group varchar(200) not null,
    primary key (sched_name, trigger_group)
);


create table qrtz_fired_triggers
(
    sched_name        varchar(120) not null,
    entry_id          varchar(95)  not null,
    trigger_name      varchar(200) not null,
    trigger_group     varchar(200) not null,
    instance_name     varchar(200) not null,
    fired_time        bigint       not null,
    sched_time        bigint       not null,
    priority          integer      not null,
    state             varchar(16)  not null,
    job_name          varchar(200),
    job_group         varchar(200),
    is_nonconcurrent  boolean,
    requests_recovery boolean,
    primary key (sched_name, entry_id)
);


create index idx_qrtz_ft_trig_inst_name
    on qrtz_fired_triggers (sched_name, instance_name);

create index idx_qrtz_ft_inst_job_req_rcvry
    on qrtz_fired_triggers (sched_name, instance_name, requests_recovery);

create index idx_qrtz_ft_j_g
    on qrtz_fired_triggers (sched_name, job_name, job_group);

create index idx_qrtz_ft_jg
    on qrtz_fired_triggers (sched_name, job_group);

create index idx_qrtz_ft_t_g
    on qrtz_fired_triggers (sched_name, trigger_name, trigger_group);

create index idx_qrtz_ft_tg
    on qrtz_fired_triggers (sched_name, trigger_group);

create table qrtz_scheduler_state
(
    sched_name        varchar(120) not null,
    instance_name     varchar(200) not null,
    last_checkin_time bigint       not null,
    checkin_interval  bigint       not null,
    primary key (sched_name, instance_name)
);


create table qrtz_locks
(
    sched_name varchar(120) not null,
    lock_name  varchar(40)  not null,
    primary key (sched_name, lock_name)
);


create table exchange_rate_monitor_rule
(
    id             bigserial
        primary key,
    user_id        bigint                 not null,
    email          varchar(255),
    base_currency  varchar(10)            not null,
    quote_currency varchar(10)            not null,
    thresholds     varchar                not null,
    active         boolean   default true not null,
    create_time    timestamp default now(),
    update_time    timestamp default now()
);


create index idx_exchange_rate_rule_user
    on exchange_rate_monitor_rule (user_id);

create unique index uk_exchange_rate_rule_user_currency
    on exchange_rate_monitor_rule (user_id, base_currency, quote_currency);

