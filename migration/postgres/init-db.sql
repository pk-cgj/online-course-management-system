-- Create Keycloak database and user
CREATE DATABASE keycloak;
CREATE USER keycloak WITH ENCRYPTED PASSWORD 'keycloak_password';
GRANT ALL PRIVILEGES ON DATABASE keycloak TO keycloak;

-- Connect to keycloak database
\c keycloak

-- Create schema and grant permissions for Keycloak
CREATE SCHEMA IF NOT EXISTS public;
GRANT ALL ON SCHEMA public TO keycloak;
GRANT ALL ON ALL TABLES IN SCHEMA public TO keycloak;
GRANT ALL ON ALL SEQUENCES IN SCHEMA public TO keycloak;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON TABLES TO keycloak;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON SEQUENCES TO keycloak;

-- Set the search path for Keycloak
ALTER DATABASE keycloak SET search_path TO public;

-- Create CourseDB database and user
CREATE DATABASE coursedb;
CREATE USER coursedb_user WITH ENCRYPTED PASSWORD 'coursedb_password';
GRANT ALL PRIVILEGES ON DATABASE coursedb TO coursedb_user;

-- Connect to coursedb database
\c coursedb

-- Grant privileges to coursedb_user
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO coursedb_user;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO coursedb_user;
