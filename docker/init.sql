CREATE DATABASE IF NOT EXISTS starwars;
CREATE USER IF NOT EXISTS 'myuser'@'%' IDENTIFIED BY 'mypassword';
GRANT ALL PRIVILEGES ON starwars.* TO 'myuser'@'%';
FLUSH PRIVILEGES;
