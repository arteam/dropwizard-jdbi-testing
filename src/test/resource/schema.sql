create table players(
    id  serial primary key,
    first_name varchar(128) not null,
    last_name varchar(128) not null,
    birth_date date not null,
    weight int,
    height int
);

create table divisions(
  name varchar(32) primary key
);

create table teams(
   id  serial primary key,
   name varchar(128) not null,
   division varchar(32) not null,
   foreign key (division) references divisions(name)
);

create table roster(
  team_id bigint not null ,
  player_id bigint not null ,
  foreign key (team_id) references teams(id),
  foreign key (player_id) references players(id)
);