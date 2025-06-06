CREATE TABLE endringslogg
(
    id                   bigint PRIMARY KEY GENERATED BY DEFAULT AS IDENTITY (INCREMENT 1 START 1 MINVALUE 1),
    opprettet_tidspunkt  TIMESTAMP DEFAULT now() NOT NULL,
    aktiv_fra_tidspunkt  TIMESTAMP,
    aktiv_til_tidspunkt  TIMESTAMP,
    tilhører_skjermbilde text                    NOT NULL,
    tittel               text                    NOT NULL,
    sammendrag           text                    NOT NULL,
    opprettet_av_navn   text                                NOT NULL,
    opprettet_av        text                                NOT NULL,
    er_påkrevd           BOOLEAN   default false NOT NULL
);

CREATE TABLE endringslogg_endring
(
    id                  bigint PRIMARY KEY GENERATED BY DEFAULT AS IDENTITY (INCREMENT 1 START 1 MINVALUE 1),
    endringslogg_id     bigint REFERENCES endringslogg (id) not null,
    rekkefølge_indeks   INTEGER                             NOT NULL,
    innhold             TEXT                                NOT NULL,
    tittel              TEXT                                NOT NULL,
    opprettet_tidspunkt TIMESTAMP DEFAULT now()             NOT NULL
);

CREATE TABLE person
(
    id        bigint PRIMARY KEY GENERATED BY DEFAULT AS IDENTITY (INCREMENT 1 START 1 MINVALUE 1),
    nav_ident TEXT NOT NULL,
    navn      TEXT NOT NULL
);

CREATE TABLE lest_av_bruker
(
    id              bigint PRIMARY KEY GENERATED BY DEFAULT AS IDENTITY (INCREMENT 1 START 1 MINVALUE 1),
    endringslogg_id bigint REFERENCES endringslogg (id),
    person_id       bigint REFERENCES person (id),
    lest_tidspunkt  TIMESTAMP DEFAULT now() NOT NULL
);

