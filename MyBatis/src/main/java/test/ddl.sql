DROP DATABASE IF EXISTS test;
CREATE DATABASE test DEFAULT CHARACTER SET utf8;

use test;
CREATE TABLE student(
  id int(11) NOT NULL AUTO_INCREMENT,
  studentID int(11) NOT NULL UNIQUE,
  name varchar(255) NOT NULL,
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

INSERT INTO student VALUES(1,1,'QJJ');
INSERT INTO student VALUES(2,2,'ABC');
INSERT INTO student VALUES(3,3,'MM');

select * from student;