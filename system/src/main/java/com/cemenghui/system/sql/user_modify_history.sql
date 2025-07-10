-- "public"."user_modify_history" definition

-- Drop table

-- DROP TABLE "public"."user_modify_history";

CREATE TABLE "public"."user_modify_history" (
	"history_id" character varying(255 char) NOT NULL,
	"user_id" character varying(255 char) NOT NULL,
	"modified_field" character varying(255 char) NULL,
	"old_value" character varying(255 char) NULL,
	"new_value" character varying(255 char) NULL,
	"modify_time" timestamp without time zone NULL,
	"operator_id" character varying(255 char) NULL,
	"field_name" character varying(255 char) NULL,
	CONSTRAINT "user_modify_history_pkey" PRIMARY KEY (history_id)
)TABLESPACE sys_default;


-- "public"."user_modify_history" foreign keys

ALTER TABLE "public"."user_modify_history" ADD CONSTRAINT "fk_user_modify_history_user" FOREIGN KEY (user_id) REFERENCES enterprise_user(user_id);