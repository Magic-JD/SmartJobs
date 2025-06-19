DROP TABLE IF EXISTS public.coupon;
DROP TABLE IF EXISTS public.criteria_analysis;
DROP TABLE IF EXISTS public.analysis;
DROP TABLE IF EXISTS public.candidate;
DROP TABLE IF EXISTS public.user_criteria;
DROP TABLE IF EXISTS public.selected_role;
DROP TABLE IF EXISTS public."role";
DROP TABLE IF EXISTS public.credit;
DROP TABLE IF EXISTS public.defined_criteria;
DROP TABLE IF EXISTS public.credential;

CREATE TABLE public.credential (
	username varchar NOT NULL,
	"password" varchar NOT NULL,
	id int8 GENERATED ALWAYS AS IDENTITY( INCREMENT BY 1 MINVALUE 1 MAXVALUE 9223372036854775807 START 1 CACHE 1 NO CYCLE) NOT NULL,
	authority varchar DEFAULT 'USER' NOT NULL,
	CONSTRAINT credential_pkey PRIMARY KEY (id)
);

CREATE TABLE public.defined_criteria (
	id int8 NOT NULL,
	category varchar NOT NULL,
	criteria varchar NOT NULL,
	"input" bool NOT NULL,
	ai_prompt text NOT NULL,
	is_boolean bool NOT NULL,
	input_example varchar NULL,
	tooltip varchar NOT NULL,
	CONSTRAINT criteria_pk PRIMARY KEY (id),
	CONSTRAINT criteria_unique UNIQUE (criteria)
);

CREATE TABLE public.credit (
	id int8 GENERATED ALWAYS AS IDENTITY( INCREMENT BY 1 MINVALUE 1 MAXVALUE 9223372036854775807 START 1 CACHE 1 NO CYCLE) NOT NULL,
	user_id int8 NOT NULL,
	balance int8 NOT NULL,
	"event" int2 NOT NULL,
	CONSTRAINT credit_pk PRIMARY KEY (id),
	CONSTRAINT credit_credential_fk FOREIGN KEY (user_id) REFERENCES public.credential(id) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE public."role" (
	user_id int8 NOT NULL,
	"position" varchar NOT NULL,
	id int8 GENERATED ALWAYS AS IDENTITY( INCREMENT BY 1 MINVALUE 1 MAXVALUE 9223372036854775807 START 1 CACHE 1 NO CYCLE) NOT NULL,
	CONSTRAINT role_pk PRIMARY KEY (id),
	CONSTRAINT role_credential_fk FOREIGN KEY (user_id) REFERENCES public.credential(id) ON DELETE CASCADE ON UPDATE CASCADE DEFERRABLE INITIALLY DEFERRED
);

CREATE TABLE public.selected_role (
	id int8 GENERATED ALWAYS AS IDENTITY( INCREMENT BY 1 MINVALUE 1 MAXVALUE 9223372036854775807 START 1 CACHE 1 NO CYCLE) NOT NULL,
	user_id int8 NOT NULL,
	role_id int8 NOT NULL,
	CONSTRAINT selected_role_pk PRIMARY KEY (id),
	CONSTRAINT selected_role_unique UNIQUE (user_id),
	CONSTRAINT selected_role_unique_1 UNIQUE (role_id),
	CONSTRAINT selected_role_credential_fk FOREIGN KEY (user_id) REFERENCES public.credential(id) ON DELETE CASCADE ON UPDATE CASCADE DEFERRABLE INITIALLY DEFERRED,
	CONSTRAINT selected_role_role_fk FOREIGN KEY (role_id) REFERENCES public."role"(id) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE public.user_criteria (
	id int8 GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
	defined_criteria_id int8 NOT NULL,
	role_id int8 NOT NULL,
	value varchar,
	score int8 NOT NULL,
	CONSTRAINT user_criteria_criteria_fk FOREIGN KEY (defined_criteria_id) REFERENCES public.defined_criteria(id) ON DELETE CASCADE ON UPDATE CASCADE,
	CONSTRAINT user_criteria_role_fk FOREIGN KEY (role_id) REFERENCES public."role"(id) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE public.candidate (
	id int8 GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
	currently_selected bool,
	"name" varchar,
	file_hash varchar,
	condensed_text text,
	user_id int8 NOT NULL,
	role_id int8 NOT NULL,
	last_accessed date,
	CONSTRAINT candidate_user_fk FOREIGN KEY (user_id) REFERENCES public.credential(id) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE INDEX candidate_id_idx ON public.candidate (id);

CREATE TABLE public.analysis (
	id int8 GENERATED ALWAYS AS IDENTITY( INCREMENT BY 1 MINVALUE 1 MAXVALUE 9223372036854775807 START 1 CACHE 1 NO CYCLE) NOT NULL,
	user_id int8 NOT NULL,
	candidate_id int8 NOT NULL,
	role_id int8 NOT NULL,
	CONSTRAINT analysis_pk PRIMARY KEY (id),
	CONSTRAINT analysis_credential_fk FOREIGN KEY (user_id) REFERENCES public.credential(id) ON DELETE CASCADE ON UPDATE CASCADE,
	CONSTRAINT analysis_candidate_fk FOREIGN KEY (candidate_id) REFERENCES public.candidate(id) ON DELETE CASCADE ON UPDATE CASCADE,
	CONSTRAINT analysis_role_fk FOREIGN KEY (role_id) REFERENCES public."role"(id)
);

CREATE TABLE public.criteria_analysis (
	id int8 GENERATED ALWAYS AS IDENTITY( INCREMENT BY 1 MINVALUE 1 MAXVALUE 9223372036854775807 START 1 CACHE 1 NO CYCLE) NOT NULL,
	analysis_id int8 NOT NULL,
	score numeric NOT NULL,
	max_score int4 NOT NULL,
	description varchar NOT NULL,
	user_criteria_id int8 NOT NULL,
	criteria_request varchar NOT NULL,
	CONSTRAINT criteria_analysis_pk PRIMARY KEY (id),
	CONSTRAINT criteria_analysis_analysis_fk FOREIGN KEY (analysis_id) REFERENCES public.analysis(id) ON DELETE CASCADE ON UPDATE CASCADE,
	CONSTRAINT criteria_analysis_user_criteria_fk FOREIGN KEY (user_criteria_id) REFERENCES public.user_criteria(id) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE public.coupon (
	id int8 GENERATED ALWAYS AS IDENTITY( INCREMENT BY 1 MINVALUE 1 MAXVALUE 9223372036854775807 START 1 CACHE 1 NO CYCLE) NOT NULL,
	user_id int8 NOT NULL,
	applied bool DEFAULT false NOT NULL,
	expired bool DEFAULT false NOT NULL,
	value int4 NOT NULL,
	code varchar NOT NULL,
	CONSTRAINT coupon_pk PRIMARY KEY (id),
	CONSTRAINT coupon_unique UNIQUE (code)
);

ALTER TABLE public.coupon ADD CONSTRAINT coupon_credential_fk FOREIGN KEY (user_id) REFERENCES public.credential(id);
