create table Books
(
    ISBN        bigint      not null
        primary key,
    Title       varchar(50) null,
    Author      varchar(50) null,
    ReleaseDate varchar(25) null,
    Pagecount   int         null
);

create table Relationship
(
    ID     int    not null,
    ISBN   bigint not null,
    Number int    not null
);

create table User
(
    ID       int auto_increment
        primary key,
    Name     varchar(50)  null,
    Forename varchar(50)  null,
    EMail    varchar(50)  null,
    Password varchar(256) null,
    Salt     varchar(256) null
);