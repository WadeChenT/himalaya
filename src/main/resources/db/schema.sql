CREATE SCHEMA IF NOT EXISTS OTC_TW;

CREATE TABLE IF NOT EXISTS OTC_TW.ACTION_LOG (
    id              serial PRIMARY KEY,
    http_method     character(20) NOT NULL,
    rest_url        character(500) NOT NULL,
    req_header      text NULL,
    req_body        text NULL,
    res_header      text NULL,
    res_body        text NULL,
    exception_trace text NULL,
    user_id         character(255),
    user_name       character(255),
    ip              character(30) NOT NULL,
    created_at     timestamp with time zone DEFAULT CURRENT_TIMESTAMP NOT NULL
);
CREATE SEQUENCE ACTION_LOG_SEQ;

CREATE TABLE IF NOT EXISTS OTC_TW.USER (
	id              character(255) PRIMARY KEY,
	user_name       character(255) NOT NULL,
	password        character(255) NOT NULL,
	user_role       smallint NOT NULL,
	email           character(255) UNIQUE NOT NULL,
	trade_amount    integer,
	trade_count     integer,
	liked_count     integer,
	auth_email      boolean DEFAULT FALSE NOT NULL,
	auth_discord    boolean DEFAULT FALSE NOT NULL,
	tmp_token       character(255),
	updated_at      timestamp with time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
	created_at      timestamp with time zone DEFAULT CURRENT_TIMESTAMP NOT NULL
);

CREATE TABLE IF NOT EXISTS OTC_TW.TRADE (
	id              character(255) PRIMARY KEY,
	trade_type      smallint NOT NULL,
	title           character(1000) NOT NULL,
	b_user_id       character(255),
	b_currency_sign character(255),
	b_currency      integer,
	s_user_id       character(255),
	s_currency_sign character(255),
	s_currency      integer,
	remark          text,
	status          smallint DEFAULT 0 NOT NULL,
	modifier        character(255) NOT NULL,
	start_at        timestamp,
	end_at          timestamp,
	updated_at      timestamp with time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
	created_at      timestamp with time zone DEFAULT CURRENT_TIMESTAMP NOT NULL
);

CREATE TABLE IF NOT EXISTS OTC_TW.TRADE_HIS (
	id              character(255) PRIMARY KEY,
	trade_id        character(255) NOT NULL,
    trade_type      smallint NOT NULL,
    title           character(1000) NOT NULL,
    b_user_id       character(255),
    b_currency_sign character(255),
    b_currency      integer,
    s_user_id       character(255),
    s_currency_sign character(255),
    s_currency      integer,
    remark          text,
    status          smallint DEFAULT 0 NOT NULL,
    modifier        character(255) NOT NULL,
    start_at        timestamp,
    end_at          timestamp,
    updated_at      timestamp with time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    created_at      timestamp with time zone DEFAULT CURRENT_TIMESTAMP NOT NULL
);

CREATE INDEX TRADE_HIS_IDX ON OTC_TW.TRADE_HIS(trade_id);

CREATE TABLE IF NOT EXISTS OTC_TW.TRADE_MESS (
	id              character(255) PRIMARY KEY,
	trade_id        character(255) NOT NULL,
	user_id         character(255) NOT NULL,
	mess            text,
    pic             bytea,
    file_extension  character(10),
	updated_at      timestamp with time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
	created_at      timestamp with time zone DEFAULT CURRENT_TIMESTAMP NOT NULL
);

CREATE INDEX TRADE_MESS_IDX ON OTC_TW.TRADE_MESS(trade_id);


INSERT INTO OTC_TW.USER (id, user_name, password, user_role, email) VALUES (1, 'admin', '$2a$10$7YVYlA5MyYKPyw1spG55C.dVvCBamyoTj41Qrk4rjNS3g46s4J4o6', 0, 'test+admin@gmail.com');
INSERT INTO OTC_TW.USER (id, user_name, password, user_role, email) VALUES (2, 'tester', '$2a$10$3XY/8AJR9icw3asT8zjo2OcHF6YTCSnS4uOyqbPLXgayWSsc8XlC.', 1, 'test+tester@gmail.com');