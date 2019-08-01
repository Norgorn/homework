create database homework with owner postgres;

\c homework

create table sync
(
	id varchar not null
		constraint sync_pk
			primary key,
	country_code varchar not null,
	money bigint not null,
	raw_json jsonb not null,
	created_at timestamp with time zone,
	updated_at timestamp with time zone
);

alter table sync owner to postgres;

create unique index sync_id_uindex
	on sync (id);

create index sync_money_index
	on sync (country_code asc, money desc);

create index sync_country_code_created_at_index
	on sync (country_code, created_at);