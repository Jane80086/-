-- "public"."admin_user" definition

-- Drop table

-- DROP TABLE "public"."admin_user";

CREATE TABLE "public"."admin_user" (
	"user_id" character varying(64 char) NOT NULL,
	"real_name" character varying(64 char) NULL,
	"department" character varying(64 char) NULL,
	"account" character varying(64 char) NULL,
	"password" character varying(64 char) NULL,
	"nickname" character varying(64 char) NULL,
	"phone" character varying(64 char) NULL,
	"email" character varying(64 char) NULL,
	"is_remember" boolean NULL,
	CONSTRAINT "admin_user_pkey" PRIMARY KEY (user_id)
)TABLESPACE sys_default;