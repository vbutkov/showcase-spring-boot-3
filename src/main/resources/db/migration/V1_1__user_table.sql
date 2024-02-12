create table t_user
(
    id       uuid primary key,
    username varchar(255) not null,
    password text         not null
);

create unique index t_user_username_idx on t_user (username);

alter table t_task
    add column user_id uuid not null references t_user (id);

create index t_task_user_id_idx on t_task (user_id);