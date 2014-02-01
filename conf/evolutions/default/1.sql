# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table app (
  id                        bigint not null,
  name                      varchar(255),
  description               varchar(255),
  creation_date             timestamp,
  constraint pk_app primary key (id))
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

create table fenix_user_app (
  fenix_user_username            varchar(255) not null,
  app_id                         bigint not null,
  constraint pk_fenix_user_app primary key (fenix_user_username, app_id))
;
create sequence app_seq;

create sequence fenix_user_seq;




alter table app_fenix_user add constraint fk_app_fenix_user_app_01 foreign key (app_id) references app (id);

alter table app_fenix_user add constraint fk_app_fenix_user_fenix_user_02 foreign key (fenix_user_username) references fenix_user (username);

alter table fenix_user_app add constraint fk_fenix_user_app_fenix_user_01 foreign key (fenix_user_username) references fenix_user (username);

alter table fenix_user_app add constraint fk_fenix_user_app_app_02 foreign key (app_id) references app (id);

# --- !Downs

drop table if exists app cascade;

drop table if exists app_fenix_user cascade;

drop table if exists fenix_user cascade;

drop table if exists fenix_user_app cascade;

drop sequence if exists app_seq;

drop sequence if exists fenix_user_seq;

