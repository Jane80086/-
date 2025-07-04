-- "public"."enterprise_user" definition

-- Drop table

-- DROP TABLE "public"."enterprise_user";

CREATE TABLE "public"."enterprise_user" (
	"user_id" character varying(255 char) NOT NULL,
	"real_name" character varying(255 char) NOT NULL,
	"enterprise_id" character varying(255 char) NULL,
	"account" character varying(255 char) NULL,
	"password" character varying(255 char) NULL,
	"nickname" character varying(255 char) NULL,
	"phone" character varying(255 char) NULL,
	"email" character varying(255 char) NULL,
	"is_remember" boolean NULL,
	"status" character varying(255 char) NULL,
	"update_time" timestamp without time zone NULL,
	"create_time" timestamp without time zone NULL,
	"department" character varying(255 char) NULL,
	CONSTRAINT "enterprise_user_pkey" PRIMARY KEY (user_id)
)TABLESPACE sys_default;
CREATE INDEX idx_enterprise_user_enterprise_id ON public.enterprise_user USING btree (enterprise_id) TABLESPACE sys_default;


-- "public"."enterprise_user" foreign keys

ALTER TABLE "public"."enterprise_user" ADD CONSTRAINT "fk_enterprise_user_enterprise" FOREIGN KEY (enterprise_id) REFERENCES enterprise(enterprise_id);