#!/bin/sh
set -e

#CRLs
mkdir /etc/apache2/crl
wget http://crl.um.es/um.crl -P /etc/apache2/crl


supervisorctl start owncloud
sleep 3
su - www-data -s /bin/bash -c "cd /var/www/owncloud/ && \
                                                        php occ app:enable user_ldap &&\
                                                        php occ ldap:create-empty-config &&\
                                                        php occ ldap:set-config '' ldapHost ldap.um.es &&\
                                                        php occ ldap:set-config '' ldapPort 389 &&\
                                                        php occ ldap:set-config '' ldapAgentName cn=admin,dc=um,dc=es &&\
                                                        php occ ldap:set-config '' ldapAgentPassword um_password &&\
                                                        php occ ldap:set-config '' ldapBase dc=um,dc=es &&\
                                                        php occ ldap:set-config '' ldapUserFilter '(&(|(objectclass=inetOrgPerson))(|(memberof=cn=owncloud,ou=Servicios,dc=um,dc=es)))' &&\
                                                        php occ ldap:set-config '' ldapLoginFilter '(&(&(|(objectclass=inetOrgPerson))(|(memberof=cn=owncloud,ou=Servicios,dc=um,dc=es)))(|(cn=%uid)(|(mailPrimaryAddress=%uid)(mail=%uid))))' &&\
                                                        php occ ldap:set-config '' ldapLoginFilterEmail 1 &&\
                                                        php occ ldap:set-config '' ldapUserDisplayName cn &&\
                                                        php occ ldap:set-config '' ldapUserFilterObjectclass inetOrgPerson &&\
                                                        php occ ldap:set-config '' ldapConfigurationActive 1 &&\
                                                        php occ ldap:set-config '' hasMemberOfFilterSupport 1 &&\
                                                        php occ ldap:set-config '' ldapUserFilterGroups owncloud &&\
                                                        php occ ldap:set-config '' ldapBaseGroups dc=um,dc=es &&\
                                                        php occ ldap:set-config '' ldapBaseUsers dc=um,dc=es"

