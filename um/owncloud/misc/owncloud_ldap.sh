#!/bin/sh
set -e

supervisorctl start owncloud

su - www-data -s /bin/bash -c "cd /var/www/owncloud/ && \
                                                        php occ app:enable user_ldap &&\
                                                        php occ ldap:create-empty-config &&\
                                                        php occ ldap:set-config '' ldapHost ldap.um.es &&\
                                                        php occ ldap:set-config '' ldapPort 389 &&\
                                                        php occ ldap:set-config '' ldapAgentName cn=admin,dc=um,dc=es &&\
                                                        php occ ldap:set-config '' ldapAgentPassword um_password &&\
                                                        php occ ldap:set-config '' ldapBase dc=um,dc=es &&\
                                                        php occ ldap:set-config '' ldapUserFilter '(|(objectclass=inetOrgPerson))' &&\
                                                        php occ ldap:set-config '' ldapLoginFilter '(&(|(objectclass=inetOrgPerson))(|(uid=%uid)(|(mailPrimaryAddress=%uid)(mail=%uid))))' &&\
                                                        php occ ldap:set-config '' ldapLoginFilterEmail 1 &&\
                                                        php occ ldap:set-config '' ldapUserDisplayName cn &&\
                                                        php occ ldap:set-config '' ldapUserFilterObjectclass inetOrgPerson &&\
                                                        php occ ldap:set-config '' ldapConfigurationActive 1"
