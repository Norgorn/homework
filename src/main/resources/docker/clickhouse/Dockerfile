FROM yandex/clickhouse-server

COPY config.xml /etc/clickhouse-server/config.xml

COPY 002__init_clickhouse.sql /docker-entrypoint-initdb.d/002__init_clickhouse.sql

EXPOSE 8123
EXPOSE 9000
EXPOSE 9009
