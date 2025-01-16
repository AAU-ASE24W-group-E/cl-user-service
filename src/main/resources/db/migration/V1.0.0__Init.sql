create table users (
  id uuid primary key not null,
  initial_login_pending boolean,
  latitude double precision,
  longitude double precision,
  email character varying(255) not null,
  password character varying(255) not null,
  role character varying(255) not null,
  username character varying(255) not null
);
create unique index users_email_key on users using btree (email);
create unique index users_username_key on users using btree (username);

