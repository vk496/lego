FROM vk496/ubuntu-lego

ENV ASTERISK_VERSION 14.6.2

RUN aria2c -x5 -s5 -d /usr/src/ https://github.com/asterisk/asterisk/archive/$ASTERISK_VERSION.tar.gz && tar --directory /usr/src/ -xvf /usr/src/asterisk-$ASTERISK_VERSION.tar.gz

# Configure
RUN cd /usr/src/asterisk-$ASTERISK_VERSION && \
        ./configure && \
        make -j $(($(getconf _NPROCESSORS_ONLN)+1)) menuselect.makeopts && \
        menuselect/menuselect \
        --disable BUILD_NATIVE \
        --enable cdr_csv \
        --enable res_snmp \
        --enable res_http_websocket \
        --enable res_hep_pjsip \
        --enable res_hep_rtcp \
        --enable res_sorcery_astdb \
        --enable res_sorcery_config \
        --enable res_sorcery_memory \
        --enable res_sorcery_memory_cache \
        --enable res_pjproject \
        --enable res_rtp_asterisk \
        --enable res_ari \
        --enable res_ari_applications \
        --enable res_ari_asterisk \
        --enable res_ari_bridges \
        --enable res_ari_channels \
        --enable res_ari_device_states \
        --enable res_ari_endpoints \
        --enable res_ari_events \
        --enable res_ari_mailboxes \
        --enable res_ari_model \
        --enable res_ari_playbacks \
        --enable res_ari_recordings \
        --enable res_ari_sounds \
        --enable res_pjsip \
        --enable res_pjsip_acl \
        --enable res_pjsip_authenticator_digest \
        --enable res_pjsip_caller_id \
        --enable res_pjsip_config_wizard \
        --enable res_pjsip_dialog_info_body_generator \
        --enable res_pjsip_diversion \
        --enable res_pjsip_dlg_options \
        --enable res_pjsip_dtmf_info \
        --enable res_pjsip_empty_info \
        --enable res_pjsip_endpoint_identifier_anonymous \
        --enable res_pjsip_endpoint_identifier_ip \
        --enable res_pjsip_endpoint_identifier_user \
        --enable res_pjsip_exten_state \
        --enable res_pjsip_header_funcs \
        --enable res_pjsip_logger \
        --enable res_pjsip_messaging \
        --enable res_pjsip_mwi \
        --enable res_pjsip_mwi_body_generator \
        --enable res_pjsip_nat \
        --enable res_pjsip_notify \
        --enable res_pjsip_one_touch_record_info \
        --enable res_pjsip_outbound_authenticator_digest \
        --enable res_pjsip_outbound_publish \
        --enable res_pjsip_outbound_registration \
        --enable res_pjsip_path \
        --enable res_pjsip_pidf_body_generator \
        --enable res_pjsip_publish_asterisk \
        --enable res_pjsip_pubsub \
        --enable res_pjsip_refer \
        --enable res_pjsip_registrar \
        --enable res_pjsip_registrar_expire \
        --enable res_pjsip_rfc3326 \
        --enable res_pjsip_sdp_rtp \
        --enable res_pjsip_send_to_voicemail \
        --enable res_pjsip_session \
        --enable res_pjsip_sips_contact \
        --enable res_pjsip_t38 \
        --enable res_pjsip_transport_management \
        --enable res_pjsip_transport_websocket \
        --enable res_pjsip_xpidf_body_generator \
        --enable res_stasis \
        --enable res_stasis_answer \
        --enable res_stasis_device_state \
        --enable res_stasis_mailbox \
        --enable res_stasis_playback \
        --enable res_stasis_recording \
        --enable res_stasis_snoop \
        --enable res_stasis_test \
        --enable res_statsd \
        --enable res_timing_timerfd \
        --enable res_config_ldap \
        menuselect.makeopts && \
        \
        make -j $(($(getconf _NPROCESSORS_ONLN)+1)) && \
        make -j $(($(getconf _NPROCESSORS_ONLN)+1)) install && \
        make -j $(($(getconf _NPROCESSORS_ONLN)+1)) samples && \
        make -j $(($(getconf _NPROCESSORS_ONLN)+1)) config && \
        \
        sed -i -e 's/# MAXFILES=/MAXFILES=/' /usr/sbin/safe_asterisk && \
        rm -Rf /usr/src/asterisk*


# Copy in default configs
COPY conf/sip.conf /etc/asterisk/sip.conf
COPY conf/users.conf /etc/asterisk/users.conf
COPY conf/extensions.conf /etc/asterisk/extensions.conf
COPY conf/extconfig.conf /etc/asterisk/extconfig.conf
COPY conf/res_ldap.conf /etc/asterisk/res_ldap.conf
#Wrapper
COPY misc/supervisord.conf /etc/supervisor/conf.d/supervisord.conf
#SNMP
COPY conf/snmpd.conf /etc/snmp/snmpd.conf

#Firewall
COPY conf/firewall.ipv4 /etc/iptables_firewall

RUN sed -i 's/.*mibs :/#&/' /etc/snmp/snmp.conf #Enable all MIBS
CMD ["/usr/bin/supervisord"]
