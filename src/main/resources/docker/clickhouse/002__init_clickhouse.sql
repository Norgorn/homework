CREATE DATABASE IF NOT EXISTS home_db
;

CREATE TABLE IF NOT EXISTS home_db.activity (
    date UInt64 default toUInt64(now()),
    user_id UUID,
    activity UInt64
) ENGINE = MergeTree()
PARTITION BY user_id
ORDER BY date
;