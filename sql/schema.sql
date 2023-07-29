drop table if exists tree;

create table tree
(
    id        serial8,
    name      varchar not null,
    left_key  int4    not null,
    right_key int4    not null,
    level     int2    not null,
    primary key (id)
);

insert into tree (name, left_key, right_key, level)
values ('Комплектующие', 1, 10, 0),
       ('Процессоры', 2, 7, 1),
       ('Intel', 3, 4, 2),
       ('AMD', 5, 6, 2),
       ('ОЗУ', 8, 9, 1),
       ('Аудиотехника', 11, 20, 0),
       ('Наушники', 12, 17, 1),
       ('С микрофоном', 13, 14, 2),
       ('Без микрофона', 15, 16, 2),
       ('Колонки', 18, 19, 1);


