#!/bin/bash
set -e

#CRLs
mkdir /etc/apache2/crl
wget http://distribution.upm.es/upm.crl -P /etc/apache2/crl


supervisorctl start owncloud

echo "Waiting owncloud to launch on 80..."

while ! nc -z localhost 80; do   
  sleep 0.1 # wait for 1/10 of the second before check again
done

echo "Owncloud launched"

su - www-data -s /bin/bash -c "php /var/www/owncloud/occ app:enable user_ldap"

#Delete previous configs
while read -r conf; do
    su - www-data -s /bin/bash -c "php /var/www/owncloud/occ ldap:delete-config '$conf'"
done <<< "$(su - www-data -s /bin/bash -c "php /var/www/owncloud/occ ldap:show-config | grep '| Configuration' | tr -d ' ' | cut -d'|' -f3")"


su - www-data -s /bin/bash -c "cd /var/www/owncloud/ && \
                                                        php occ app:enable user_ldap &&\
                                                        php occ ldap:create-empty-config &&\
                                                        php occ ldap:set-config '' ldapHost ldap.upm.es &&\
                                                        php occ ldap:set-config '' ldapPort 389 &&\
                                                        php occ ldap:set-config '' ldapAgentName cn=admin,dc=upm,dc=es &&\
                                                        php occ ldap:set-config '' ldapAgentPassword upm_password &&\
                                                        php occ ldap:set-config '' ldapBase dc=upm,dc=es &&\
                                                        php occ ldap:set-config '' ldapUserFilter '(&(|(objectclass=inetOrgPerson))(|(memberof=cn=owncloud,ou=Servicios,dc=upm,dc=es)))' &&\
                                                        php occ ldap:set-config '' ldapLoginFilter '(&(&(|(objectclass=inetOrgPerson))(|(memberof=cn=owncloud,ou=Servicios,dc=upm,dc=es)))(|(cn=%uid)(|(mailPrimaryAddress=%uid)(mail=%uid))))' &&\
                                                        php occ ldap:set-config '' ldapLoginFilterEmail 1 &&\
                                                        php occ ldap:set-config '' ldapUserDisplayName cn &&\
                                                        php occ ldap:set-config '' ldapUserFilterObjectclass inetOrgPerson &&\
                                                        php occ ldap:set-config '' ldapConfigurationActive 1 &&\
                                                        php occ ldap:set-config '' hasMemberOfFilterSupport 1 &&\
                                                        php occ ldap:set-config '' ldapUserFilterGroups owncloud &&\
                                                        php occ ldap:set-config '' ldapBaseGroups dc=upm,dc=es &&\
                                                        php occ ldap:set-config '' ldapBaseUsers dc=upm,dc=es &&\
                                                        php occ ldap:set-config '' ldapQuotaAttribute postOfficeBox &&\
                                                        php occ ldap:set-config '' ldapQuotaDefault 1073741824"

