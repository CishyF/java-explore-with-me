CREATE SCHEMA IF NOT EXISTS public;

CREATE TABLE IF NOT EXISTS public."user" (
    id    BIGINT       GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name  VARCHAR(250) NOT NULL,
    email VARCHAR(254) NOT NULL,
    UNIQUE (email)
);

CREATE TABLE IF NOT EXISTS public.category (
    id   BIGINT      GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    UNIQUE (name)
);

CREATE TABLE IF NOT EXISTS public.location (
    id  BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    lat FLOAT  NOT NULL,
    lon FLOAT  NOT NULL,
    CONSTRAINT lat_bounds CHECK ( lat >= -90 AND lat <= 90 ),
    CONSTRAINT lon_bounds CHECK ( lon >= -180 AND lon <= 180 )
);

CREATE TABLE IF NOT EXISTS public.event (
    id                 BIGINT                      GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    title              VARCHAR(120)                NOT NULL,
    annotation         TEXT                        NOT NULL,
    category_id        BIGINT                      NOT NULL REFERENCES category (id),
    initiator_id       BIGINT                      NOT NULL REFERENCES "user" (id),
    description        TEXT                        NOT NULL,
    created_on         TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    published_on       TIMESTAMP WITHOUT TIME ZONE,
    event_date         TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    location_id        BIGINT                      NOT NULL REFERENCES location (id),
    paid               BOOLEAN                     NOT NULL,
    participant_limit  INTEGER                     DEFAULT 0,
    request_moderation BOOLEAN                     DEFAULT FALSE,
    state              INTEGER                     NOT NULL,
    CONSTRAINT event_date_after_created CHECK (event_date > created_on)
);

CREATE TABLE IF NOT EXISTS public.participation_request (
    id           BIGINT                      GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    event_id     BIGINT                      NOT NULL REFERENCES event (id),
    requester_id BIGINT                      NOT NULL REFERENCES "user" (id),
    created      TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    status       INTEGER                     NOT NULL,
    UNIQUE (event_id, requester_id)
);

CREATE TABLE IF NOT EXISTS public.compilation (
    id     BIGINT      GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    title  VARCHAR(50) NOT NULL,
    pinned BOOLEAN     DEFAULT FALSE
);

CREATE TABLE IF NOT EXISTS public.compilation_event (
    compilation_id BIGINT NOT NULL REFERENCES compilation (id),
    event_id       BIGINT NOT NULL REFERENCES event (id),
    UNIQUE (compilation_id, event_id)
);