create table Books
(
    ISBN        bigint(13)  not null,
    Title       varchar(50) null,
    Author      varchar(50) null,
    ReleaseDate varchar(25) null,
    Pagecount   int         null,
    constraint Books_ISBN_uindex
        unique (ISBN)
);

alter table Books
    add primary key (ISBN);

create table Relationship
(
    ID     int        not null,
    ISBN   bigint(13) not null,
    Number int        null,
    primary key (ID, ISBN),
    constraint Relationship_Books_ISBN_fk
        foreign key (ISBN) references Books (isbn),
    constraint Relationship_ibfk_1
        foreign key (ID) references User (id)
            on update cascade on delete cascade
);

create index ID
    on Relationship (ID);

create index ISBN
    on Relationship (ISBN);

create table User
(
    ID       int auto_increment,
    Name     varchar(50)  null,
    Forename varchar(50)  null,
    EMail    varchar(50)  null,
    Password varchar(256) null,
    Salt     varchar(256) null,
    constraint User_ID_uindex
        unique (ID)
);

alter table User
    add primary key (ID);