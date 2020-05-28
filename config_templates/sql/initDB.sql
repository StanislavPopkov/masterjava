DROP TABLE IF EXISTS cross_table;
DROP Table IF EXISTS groups;
DROP TYPE IF EXISTS group_type;
DROP TABLE IF EXISTS projects;
DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS cities;
DROP SEQUENCE IF EXISTS user_seq;
DROP TYPE IF EXISTS user_flag;

CREATE TYPE user_flag AS ENUM ('active', 'deleted', 'superuser');
CREATE TYPE group_type AS ENUM ('registering', 'current', 'finished');

CREATE SEQUENCE user_seq START 100000;

CREATE TABLE cities(
    id        INTEGER PRIMARY KEY DEFAULT nextval('user_seq'),
    short_name TEXT NOT NULL,
    city_name TEXT NOT NULL
);

CREATE TABLE users (
  id        INTEGER PRIMARY KEY DEFAULT nextval('user_seq'),
  full_name TEXT NOT NULL,
  email     TEXT NOT NULL,
  flag      user_flag NOT NULL,
  city_id   INTEGER NOT NULL,
  FOREIGN KEY (city_id) REFERENCES cities (id) ON DELETE SET NULL
);
CREATE UNIQUE INDEX users_unique_email_idx ON users (email);

CREATE TABLE groups(
  id        INTEGER PRIMARY KEY DEFAULT nextval('user_seq'),
  group_name TEXT NOT NULL,
  type      group_type NOT NULL
);

CREATE TABLE projects(
  id        INTEGER PRIMARY KEY DEFAULT nextval('user_seq'),
  project_name TEXT NOT NULL,
  description TEXT NOT NULL
);

CREATE TABLE cross_project_group(
  id1   INTEGER REFERENCES projects (id) ON DELETE SET NULL ,
  id2     INTEGER REFERENCES groups (id) ON DELETE SET NULL ,
  UNIQUE (id1, id2)
);

CREATE TABLE cross_user_group(
    id1    INTEGER REFERENCES users (id) ON DELETE SET NULL ,
    id2   INTEGER REFERENCES groups (id) ON DELETE SET NULL ,
    UNIQUE (id1, id2)
);