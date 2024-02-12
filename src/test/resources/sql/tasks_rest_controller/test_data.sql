insert into t_user(id, username, password)
values ('3468b72c-c682-11ee-bb8c-38d57ae4482a', 'user1', '{noop}password1'),
       ('358a4d64-c682-11ee-bf3e-38d57ae4482b', 'user2', '{noop}password2');

insert into t_task(id, details, completed, user_id)
values ('3468b72c-c682-11ee-bb8c-38d57ae4482d', 'Первая задача', false, '3468b72c-c682-11ee-bb8c-38d57ae4482a'),
       ('358a4d64-c682-11ee-bf3e-38d57ae4482d', 'Вторая задача', true, '3468b72c-c682-11ee-bb8c-38d57ae4482a'),
       ('358a4d64-c682-11ee-bf3e-38d57ae4482e', 'Третья задача', true, '358a4d64-c682-11ee-bf3e-38d57ae4482b');


