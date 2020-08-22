--
-- Database Table Creation
--
-- This file will create the tables for use with the book 
-- Database Management Systems by Raghu Ramakrishnan and Johannes Gehrke.
-- It is run automatically by the installation script.
--
-- by: Wanxing (Sarah) Xu
--
-- First drop any existing tables. Any errors are ignored.
--
DROP TABLE student CASCADE;
DROP TABLE faculty CASCADE;
DROP TABLE class CASCADE;
DROP TABLE enrolled CASCADE;
DROP TABLE emp CASCADE;
DROP TABLE works CASCADE;
DROP TABLE dept CASCADE;
DROP TABLE flights CASCADE;
DROP TABLE aircraft CASCADE;
DROP TABLE certified CASCADE;
DROP TABLE employees CASCADE;
DROP TABLE suppliers CASCADE;
DROP TABLE parts CASCADE;
DROP TABLE catalog CASCADE;
DROP TABLE sailors CASCADE;
--
-- Now, add each table.
--
CREATE TABLE student(
	snum NUMERIC(9,0) PRIMARY KEY,
	sname CHAR(30),
	major CHAR(25),
	standing CHAR(2),
	age NUMERIC(3,0)
);
CREATE TABLE faculty(
	fid NUMERIC(9,0) PRIMARY KEY,
	fname CHAR(30),
	deptid NUMERIC(2,0)
);
CREATE TABLE class(
	name CHAR(40) PRIMARY KEY,
	meets_at CHAR(20),
	room CHAR(10),
	fid NUMERIC(9,0),
	FOREIGN KEY(fid) REFERENCES faculty
);   
CREATE TABLE enrolled(
	snum NUMERIC(9,0), 
	cname CHAR(40),
	PRIMARY KEY(snum,cname),
	FOREIGN KEY(snum) REFERENCES student,
	FOREIGN KEY(cname) REFERENCES class(name)
);
CREATE TABLE emp(
	eid NUMERIC(9,0) PRIMARY KEY,
	ename CHAR(30),
	age NUMERIC(3,0),
	salary NUMERIC(10,2)
);
CREATE TABLE dept(
	did NUMERIC(2,0) PRIMARY KEY,
	dname CHAR(20),
	budget NUMERIC(10,2),
	managerid NUMERIC(9,0),
	FOREIGN KEY(managerid) REFERENCES emp(eid)
);
CREATE TABLE works(
	eid NUMERIC(9,0),
	did NUMERIC(2,0),
	pct_time NUMERIC(3,0),
	PRIMARY KEY(eid,did),
	FOREIGN KEY(eid) REFERENCES emp,
	FOREIGN KEY(did) REFERENCES dept
);
CREATE TABLE flights(
	flno NUMERIC(4,0) PRIMARY KEY,
	origin CHAR(20),
	destination CHAR(20),
	distance NUMERIC(6,0),
	departs date,
	arrives date,
	price NUMERIC(7,2)
);
CREATE TABLE aircraft(
	aid NUMERIC(9,0) PRIMARY KEY,
	aname CHAR(30),
	crusingrange NUMERIC(6,0)
);
CREATE TABLE employees(
	eid NUMERIC(9,0) PRIMARY KEY,
	ename CHAR(30),
	salary NUMERIC(10,2)
);
CREATE TABLE certified(
	eid NUMERIC(9,0),
	aid NUMERIC(9,0),
	PRIMARY KEY(eid,aid),
	FOREIGN KEY(eid) REFERENCES employees,
	FOREIGN KEY(aid) REFERENCES aircraft
);
CREATE TABLE suppliers(
	sid NUMERIC(9,0) PRIMARY KEY,
	sname CHAR(30),
	address CHAR(40)
);
CREATE TABLE parts(
	pid NUMERIC(9,0) PRIMARY KEY,
	pname CHAR(40),
	color CHAR(15)
);
CREATE TABLE catalog(
	sid NUMERIC(9,0),
	pid NUMERIC(9,0),
	cost NUMERIC(10,2),
	PRIMARY KEY(sid,pid),
	FOREIGN KEY(sid) REFERENCES suppliers,
	FOREIGN KEY(pid) REFERENCES parts
);
CREATE TABLE sailors(
	sid NUMERIC(9,0) PRIMARY KEY,
	sname CHAR(30),
	rating NUMERIC(2,0),
	age NUMERIC(4,1)
);

COPY suppliers (
	sid,
	sname,
	address)
FROM 'suppliers.txt'
WITH DELIMITER ';';

COPY parts(
	pid,
	pname,
	color)
FROM 'parts.txt'
WITH DELIMITER ',';

COPY catalog (
	sid,
	pid,
	cost)
FROM 'catalog.txt'
WITH DELIMITER ',';
