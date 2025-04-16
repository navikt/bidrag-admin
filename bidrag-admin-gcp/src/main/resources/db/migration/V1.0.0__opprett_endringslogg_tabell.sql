CREATE TABLE endringslogg
(
    id                   bigint PRIMARY KEY GENERATED BY DEFAULT AS IDENTITY (INCREMENT 1 START 1 MINVALUE 1),
    opprettet_tidspunkt  TIMESTAMP DEFAULT now() NOT NULL,
    tilhører_skjermbilde text                    NOT NULL,
    tittel               text                    NOT NULL,
    sammendrag           text                    NOT NULL,
    er_påkrevd           BOOLEAN   default false NOT NULL
);

CREATE TABLE endringslogg_endring
(
    id                  bigint PRIMARY KEY GENERATED BY DEFAULT AS IDENTITY (INCREMENT 1 START 1 MINVALUE 1),
    endringslogg_id     bigint REFERENCES endringslogg (id) not null,
    rekkefølge_indeks   INTEGER                             NOT NULL,
    innhold             TEXT                                NOT NULL,
    tittel              TEXT                                NOT NULL,
    opprettet_tidspunkt TIMESTAMP DEFAULT now()             NOT NULL,
    UNIQUE (endringslogg_id, rekkefølge_indeks)
);

CREATE TABLE endringslogg_bruker_lesing
(
    id              bigint PRIMARY KEY GENERATED BY DEFAULT AS IDENTITY (INCREMENT 1 START 1 MINVALUE 1),
    endringslogg_id bigint REFERENCES endringslogg (id),
    ident           TEXT                    NOT NULL,
    navn            TEXT,
    lest_tidspunkt  TIMESTAMP DEFAULT now() NOT NULL
);