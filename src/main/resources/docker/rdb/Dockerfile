FROM postgres:11.4

COPY 001__init.sql /docker-entrypoint-initdb.d/001__init.sql

ENV PGDATA=/pgdata