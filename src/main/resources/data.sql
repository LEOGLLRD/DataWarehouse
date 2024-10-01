create table if not exists users
(
    id serial
    primary key,
    email varchar(255),
    password varchar(255),
    role varchar(255)
    );

create table if not exists stockers
(
    private_key varchar(255) not null
    primary key,
    allocated_disk bigint,
    id_user integer
    constraint stockers_users_id_fk
    references users
    );

create table if not exists file
(
    id integer not null
    primary key,
    id_user integer
    constraint file_users_id_fk
    references users,
    name varchar(255),
    size bigint
    );

create table if not exists file_piece
(
    id serial
    primary key,
    id_file integer
    constraint file_piece_file_id_fk
    references file,
    name varchar(255),
    size bigint
    );

create table if not exists filepiecestock
(
    idpiece integer not null
    constraint filepiecestock_file_piece_id_fk
    references file_piece,
    privatekey varchar not null
    constraint filepiecestock_stockers_private_key_fk
    references stockers,
    constraint filepiecestock_pk
    primary key (idpiece, privatekey)
    );