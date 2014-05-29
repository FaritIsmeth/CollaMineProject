# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table file (
  filename                  varchar(255) not null,
  content                   longblob,
  uploaded_by               varchar(255),
  datetime                  varchar(255),
  constraint pk_file primary key (filename))
;

create table user (
  email                     varchar(255) not null,
  name                      varchar(255),
  password                  varchar(255),
  constraint pk_user primary key (email))
;




# --- !Downs

SET FOREIGN_KEY_CHECKS=0;

drop table file;

drop table user;

SET FOREIGN_KEY_CHECKS=1;

