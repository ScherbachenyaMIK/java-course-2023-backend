create schema if not exists url_tracker_db;

create table if not exists Chat
(
    id              bigint generated always as identity,
    username        text                        not null,

    primary key (id),
)

create table if not exists Link
(
    id              bigint generated always as identity,
    link            text                        not null,
    last_update     timestamp                   not null,

    primary key (id),
    UNIQUE (link)
)

create table if not exists Chat_Link
(
    chat_id         bigint,
    link_id         bigint,

    foreign key (chat_id) references Chat(id)
    foreign key (link_id) references Link(id)
    primary key (chat_id, link_id)
)
