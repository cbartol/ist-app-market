# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table app (
  id                        bigint not null,
  name                      varchar(255),
  description               varchar(255),
  file_logo                 varchar(255),
  creation_date             timestamp,
  constraint pk_app primary key (id))
;

create table comment (
  id                        bigint not null,
  text                      varchar(255),
  creation_date             timestamp,
  likes                     bigint,
  author_username           varchar(255),
  app_id                    bigint,
  constraint pk_comment primary key (id))
;

create table fenix_user (
  username                  varchar(255) not null,
  name                      varchar(255),
  email                     varchar(255),
  constraint pk_fenix_user primary key (username))
;


create table app_fenix_user (
  app_id                         bigint not null,
  fenix_user_username            varchar(255) not null,
  constraint pk_app_fenix_user primary key (app_id, fenix_user_username))
;

create table comment_fenix_user (
  comment_id                     bigint not null,
  fenix_user_username            varchar(255) not null,
  constraint pk_comment_fenix_user primary key (comment_id, fenix_user_username))
;

create table fenix_user_app (
  fenix_user_username            varchar(255) not null,
  app_id                         bigint not null,
  constraint pk_fenix_user_app primary key (fenix_user_username, app_id))
;

create table fenix_user_comment (
  fenix_user_username            varchar(255) not null,
  comment_id                     bigint not null,
  constraint pk_fenix_user_comment primary key (fenix_user_username, comment_id))
;
create sequence app_seq;

create sequence comment_seq;

create sequence fenix_user_seq;

alter table comment add constraint fk_comment_author_1 foreign key (author_username) references fenix_user (username);
create index ix_comment_author_1 on comment (author_username);
alter table comment add constraint fk_comment_app_2 foreign key (app_id) references app (id);
create index ix_comment_app_2 on comment (app_id);



alter table app_fenix_user add constraint fk_app_fenix_user_app_01 foreign key (app_id) references app (id);

alter table app_fenix_user add constraint fk_app_fenix_user_fenix_user_02 foreign key (fenix_user_username) references fenix_user (username);

alter table comment_fenix_user add constraint fk_comment_fenix_user_comment_01 foreign key (comment_id) references comment (id);

alter table comment_fenix_user add constraint fk_comment_fenix_user_fenix_u_02 foreign key (fenix_user_username) references fenix_user (username);

alter table fenix_user_app add constraint fk_fenix_user_app_fenix_user_01 foreign key (fenix_user_username) references fenix_user (username);

alter table fenix_user_app add constraint fk_fenix_user_app_app_02 foreign key (app_id) references app (id);

alter table fenix_user_comment add constraint fk_fenix_user_comment_fenix_u_01 foreign key (fenix_user_username) references fenix_user (username);

alter table fenix_user_comment add constraint fk_fenix_user_comment_comment_02 foreign key (comment_id) references comment (id);

# --- !Downs

drop table if exists app cascade;

drop table if exists app_fenix_user cascade;

drop table if exists comment cascade;

drop table if exists comment_fenix_user cascade;

drop table if exists fenix_user cascade;

drop table if exists fenix_user_app cascade;

drop table if exists fenix_user_comment cascade;

drop sequence if exists app_seq;

drop sequence if exists comment_seq;

drop sequence if exists fenix_user_seq;

