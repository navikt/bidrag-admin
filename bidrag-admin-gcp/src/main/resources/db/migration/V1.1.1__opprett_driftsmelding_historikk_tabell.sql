CREATE TABLE driftsmelding_historikk
(
    id                  bigint PRIMARY KEY GENERATED BY DEFAULT AS IDENTITY (INCREMENT 1 START 1 MINVALUE 1),
    opprettet_tidspunkt TIMESTAMP DEFAULT now() NOT NULL,
    driftsmelding_id     bigint REFERENCES driftsmelding (id) not null,
    aktiv_fra_tidspunkt TIMESTAMP,
    aktiv_til_tidspunkt TIMESTAMP,
    opprettet_av_navn   text                    NOT NULL,
    opprettet_av        text                    NOT NULL,
    innhold             text                    NOT NULL,
    status              text                    NOT NULL
);

alter table driftsmelding drop column if exists innhold;
alter table lest_av_bruker drop column if exists driftsmelding_id;
alter table lest_av_bruker
    add column if not exists driftsmelding_historikk_id bigint REFERENCES driftsmelding_historikk (id)