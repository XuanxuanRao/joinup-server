alter table public.users
    add column github_id varchar(255) unique;

comment on column public.users.github_id is '用户的GitHub账号ID，用于GitHub登录';
