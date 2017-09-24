#!/bin/bash
set -e

supervisorctl start postgresql

# BD fix: https://stackoverflow.com/a/16737776/2757192
su - postgres -c "psql << EOF
UPDATE pg_database SET datistemplate = FALSE WHERE datname = 'template1';
DROP DATABASE template1;
CREATE DATABASE template1 WITH TEMPLATE = template0 ENCODING = 'UNICODE';
UPDATE pg_database SET datistemplate = TRUE WHERE datname = 'template1';
\c template1
VACUUM FREEZE;
\q
EOF"

${OPENNMS_HOME}/bin/runjava -s
${OPENNMS_HOME}/bin/install -dis

${OPENNMS_HOME}/bin/opennms -f start
