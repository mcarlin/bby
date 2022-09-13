CREATE TABLE IF NOT EXISTS Url (
    url_id bigint NOT NULL PRIMARY KEY,
    url text NOT NULL,
    ttl timestamp
);