CREATE DATABASE IF NOT EXISTS test DEFAULT CHARACTER SET utf8;

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

CREATE TABLE test.user(
  id varchar(255) NOT NULL DEFAULT (UUID ()),
  name varchar(255) NOT NULL UNIQUE,
  age int(3) NOT NULL,
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

INSERT INTO test.user(name,age) VALUES('QJJ', 26);
INSERT INTO test.user(name,age) VALUES('ABC', 26);
INSERT INTO test.user(name,age) VALUES('aa', 26),('bb', 26),('cc', 26),('dd', 26),('ee', 26),('ff', 26),('gg', 26),('hh', 26),('ii', 26),('jj', 26),('kk', 26),('ll', 26),('mm', 26),('nn', 26),('oo', 26),('pp', 26),('qq', 26),('rr', 26),('ss', 26),('tt', 26),('uu', 26),('vv', 26),('ww', 26),('xx', 26),('yy', 26),('zz', 26);

select * from test.user;
