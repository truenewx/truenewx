CREATE TABLE t_customer (
 id INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
 mobile_phone VARCHAR(11) NOT NULL,
 password CHAR(64),
 nickname VARCHAR(20) NOT NULL,
 disabled BIT(1) DEFAULT 0 NOT NULL,
 gender VARCHAR(6) NOT NULL,
 create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
 
 UNIQUE KEY uk_mobile_phone (mobile_phone)
);
