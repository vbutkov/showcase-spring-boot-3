create table t_task
(
    id        uuid primary key,
    details   text check ( length(trim(details)) > 0 ),
    completed boolean not null default false
);

