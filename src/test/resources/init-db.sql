DROP TABLE IF EXISTS public.role_criteria;
DROP TABLE IF EXISTS public.criteria_analysis;
DROP TABLE IF EXISTS public.candidate;
DROP TABLE IF EXISTS public.analysis;
DROP TABLE IF EXISTS public.user_criteria;
DROP TABLE IF EXISTS public.selected_role;
DROP TABLE IF EXISTS public."role";
DROP TABLE IF EXISTS public.credit;
DROP TABLE IF EXISTS public.defined_criteria;
DROP TABLE IF EXISTS public.cv;
DROP TABLE IF EXISTS public.credential;

CREATE TABLE public.credential (
	username varchar NOT NULL,
	"password" varchar NOT NULL,
	id int8 NOT NULL,
	CONSTRAINT credential_pkey PRIMARY KEY (id)
);

CREATE TABLE public.cv (
	file_hash varchar NULL,
	condensed_text text NOT NULL,
	id int8 GENERATED ALWAYS AS IDENTITY( INCREMENT BY 1 MINVALUE 1 MAXVALUE 9223372036854775807 START 1 CACHE 1 NO CYCLE) NOT NULL,
	CONSTRAINT cv_pk PRIMARY KEY (id)
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
	id int8 GENERATED ALWAYS AS IDENTITY( INCREMENT BY 1 MINVALUE 1 MAXVALUE 9223372036854775807 START 1 CACHE 1 NO CYCLE) NOT NULL,
	defined_criteria_id int8 NOT NULL,
	value varchar NULL,
	score int8 NOT NULL,
	CONSTRAINT user_criteria_pk PRIMARY KEY (id),
	CONSTRAINT user_criteria_criteria_fk FOREIGN KEY (defined_criteria_id) REFERENCES public.defined_criteria(id) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE public.analysis (
	id int8 GENERATED ALWAYS AS IDENTITY( INCREMENT BY 1 MINVALUE 1 MAXVALUE 9223372036854775807 START 1 CACHE 1 NO CYCLE) NOT NULL,
	user_id int8 NOT NULL,
	cv_id int8 NOT NULL,
	role_id int8 NOT NULL,
	CONSTRAINT analysis_pk PRIMARY KEY (id),
	CONSTRAINT analysis_credential_fk FOREIGN KEY (user_id) REFERENCES public.credential(id) ON DELETE CASCADE ON UPDATE CASCADE,
	CONSTRAINT analysis_cv_fk FOREIGN KEY (cv_id) REFERENCES public.cv(id) ON DELETE CASCADE ON UPDATE CASCADE,
	CONSTRAINT analysis_role_fk FOREIGN KEY (role_id) REFERENCES public."role"(id)
);

CREATE TABLE public.candidate (
	"name" varchar NOT NULL,
	cv_id int8 NOT NULL,
	last_accessed date NOT NULL,
	user_id int8 NOT NULL,
	role_id int8 NOT NULL,
	currently_selected bool NOT NULL,
	id int8 GENERATED ALWAYS AS IDENTITY( INCREMENT BY 1 MINVALUE 1 MAXVALUE 9223372036854775807 START 1 CACHE 1 NO CYCLE) NOT NULL,
	CONSTRAINT candidate_pk PRIMARY KEY (id),
	CONSTRAINT candidate_credential_fk FOREIGN KEY (user_id) REFERENCES public.credential(id) ON DELETE CASCADE ON UPDATE CASCADE DEFERRABLE INITIALLY DEFERRED,
	CONSTRAINT candidate_cv_fk FOREIGN KEY (cv_id) REFERENCES public.cv(id) ON DELETE CASCADE ON UPDATE CASCADE DEFERRABLE INITIALLY DEFERRED,
	CONSTRAINT candidate_role_fk FOREIGN KEY (role_id) REFERENCES public."role"(id) ON DELETE CASCADE ON UPDATE CASCADE
);
CREATE INDEX candidate_id_idx ON public.candidate USING btree (id);

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

CREATE TABLE public.role_criteria (
	role_id int8 NOT NULL,
	user_criteria_id int8 NOT NULL,
	id int8 GENERATED ALWAYS AS IDENTITY( INCREMENT BY 1 MINVALUE 1 MAXVALUE 9223372036854775807 START 1 CACHE 1 NO CYCLE) NOT NULL,
	CONSTRAINT role_criteria_pk PRIMARY KEY (id),
	CONSTRAINT role_criteria_role_fk FOREIGN KEY (role_id) REFERENCES public."role"(id) ON DELETE CASCADE ON UPDATE CASCADE DEFERRABLE INITIALLY DEFERRED,
	CONSTRAINT role_criteria_user_criteria_fk FOREIGN KEY (user_criteria_id) REFERENCES public.user_criteria(id) ON DELETE CASCADE ON UPDATE CASCADE
);

INSERT INTO public.credential (username,"password",id) VALUES
	 ('username','{noop}password',1);
INSERT INTO public.cv (file_hash,condensed_text) VALUES
	 ('HASH','The best in the world'),
	 ('HASH2','The worst in the world');
INSERT INTO public."role" (user_id,"position") VALUES
	 (1,'role');
INSERT INTO public.selected_role (user_id,role_id) VALUES
	 (1,1);
INSERT INTO public.candidate ("name",cv_id,last_accessed,user_id,role_id,currently_selected) VALUES
	 ('Joe',1,'2024-06-26',1,1,true),
	 ('james',2,'2024-06-26',1,1,false);
