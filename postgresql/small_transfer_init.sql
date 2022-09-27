create database small_transfer;
create user small_transfer with encrypted password 'pwd123';
grant all privileges on database small_transfer to small_transfer;