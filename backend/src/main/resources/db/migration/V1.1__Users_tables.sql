CREATE TABLE users
(
    id                 BIGSERIAL PRIMARY KEY,
    created_time       TIMESTAMPTZ NOT NULL,
    updated_time       TIMESTAMPTZ,
    email              TEXT        NOT NULL UNIQUE,
    password           TEXT        NOT NULL,
    first_name         TEXT        NOT NULL,
    last_name          TEXT        NOT NULL,
    active             BOOLEAN     NOT NULL DEFAULT TRUE,
    photo              TEXT
);
CREATE INDEX ON users (email);

CREATE TABLE users_roles
(
    user_id BIGINT NOT NULL REFERENCES users (id),
    role    TEXT   NOT NULL,
    UNIQUE (user_id, role)
);
CREATE INDEX ON users_roles (user_id);
