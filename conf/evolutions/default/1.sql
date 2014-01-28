# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table app (
  id                        bigint auto_increment not null,
  name                      varchar(255),
  description               varchar(255),
  constraint pk_app primary key (id))
;

create table user (
  username                  varchar(255) not null,
  name                      varchar(255),
  email                     varchar(255),
  constraint pk_user primary key (username))
;


create table app_user (
  app_id                         bigint not null,
  user_username                  varchar(255) not null,
  constraint pk_app_user primary key (app_id, user_username))
;

create table user_app (
  user_username                  varchar(255) not null,
  app_id                         bigint not null,
  constraint pk_user_app primary key (user_username, app_id))
;



alter table app_user add constraint fk_app_user_app_01 foreign key (app_id) references app (id) on delete restrict on update restrict;

alter table app_user add constraint fk_app_user_user_02 foreign key (user_username) references user (username) on delete restrict on update restrict;

alter table user_app add constraint fk_user_app_user_01 foreign key (user_username) references user (username) on delete restrict on update restrict;

alter table user_app add constraint fk_user_app_app_02 foreign key (app_id) references app (id) on delete restrict on update restrict;

# --- !Downs

SET FOREIGN_KEY_CHECKS=0;

drop table app;

drop table app_user;

drop table user;

drop table user_app;

SET FOREIGN_KEY_CHECKS=1;

