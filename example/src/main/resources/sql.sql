create table user_table(
id int  not null primary key AUTO_INCREMENT,
type varchar(50) default '',
username varchar(50) default '',
password varchar(50) default '',
token varchar(220) default '',
salt_key varchar(100)  default '',
email varchar(50) default ''
)ENGINE=InnoDB COMMENT='';