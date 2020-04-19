CREATE TABLE "user" (
    id  SERIAL NOT NULL,
    login text NOT NULL,
    PRIMARY KEY (id)
);
create unique index user_login_idx on "user" using btree(login);
grant select,insert on "user" to jboss_chell_central;
grant usage on "user_id_seq" to jboss_chell_central;

CREATE TABLE public.agent (
	id serial NOT NULL,
	"token" text NULL,
	"name" text NOT NULL,
	description text NULL,
	initoptions text NULL,
	CONSTRAINT agent_pkey PRIMARY KEY (id)
);
grant select,insert on "agent" to jboss_chell_central;
grant usage on "agent_id_seq" to jboss_chell_central;

CREATE TABLE "match" (
    id  SERIAL NOT NULL,
    idengineconfig int NOT null REFERENCES agent (id),
    idengineconfig int NOT null REFERENCES agent (id),
    state text  NOT NULL,
    result text,
    gamecount int,
    PRIMARY KEY (id)
);
grant select,insert,update on "match" to jboss_chell_central;
grant usage on "match_id_seq" to jboss_chell_central;

CREATE TABLE public.game (
	id serial NOT NULL,
	idmatch int4 NOT NULL,
	boardstate text NOT NULL,
	"result" text NULL,
	gamenumber int4 NULL,
	whiteplayedbyfirstagent bool NULL,
	CONSTRAINT game_pkey PRIMARY KEY (id),
	CONSTRAINT game_idmatch_fkey FOREIGN KEY (idmatch) REFERENCES match(id)
);

grant select,insert,update on "game" to jboss_chell_central;
grant usage on "game_id_seq" to jboss_chell_central;

CREATE TABLE public.engineconfig (
	id serial NOT NULL,
    idagent int NOT null REFERENCES agent (id),
	description text NULL,
	initoptions text NULL,
    elo int NOT NULL,
	CONSTRAINT engineconfig_pkey PRIMARY KEY (id)
);
grant select,insert on "engineconfig" to jboss_chell_central;
grant usage on "engineconfig_id_seq" to jboss_chell_central;



CREATE TABLE public.subengines (
	id serial NOT NULL,
	engine_id int4 NOT NULL,
	subengines_id int4 NULL,
	CONSTRAINT subengines_pk PRIMARY KEY (id)
);

grant select,insert on "subengines" to jboss_chell_central;
grant usage on "subengines_id_seq" to jboss_chell_central;