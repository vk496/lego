FROM ubuntu:16.04

ENV DEBIAN_FRONTEND noninteractive   

RUN echo "deb http://ppa.launchpad.net/saiarcot895/myppa/ubuntu xenial main " >> /etc/apt/sources.list.d/apt-fast.list && \
        echo "deb-src http://ppa.launchpad.net/saiarcot895/myppa/ubuntu xenial main " >> /etc/apt/sources.list.d/apt-fast.list && \
        apt-key adv --keyserver hkp://keyserver.ubuntu.com:80 --recv-keys DC058F40 && \
        apt-get update && apt-get --no-install-recommends -y install apt-fast && \
            apt-fast install --no-install-recommends -y build-essential git-core pkg-config subversion libjansson-dev sqlite autoconf \
            automake libtool libxml2-dev libncurses5-dev unixodbc unixodbc-dev libasound2-dev libogg-dev \
            libvorbis-dev libneon27-dev libspandsp-dev uuid uuid-dev sqlite3 libsqlite3-dev aria2 \
            libgnutls-dev ca-certificates wget \
            net-tools=1.60-26ubuntu1 \
            supervisor \
            snmpd snmp snmp-mibs-downloader \
            nano \
    && rm -rf /var/lib/apt/lists/*

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
        menuselect.makeopts && \
        \
        make -j $(($(getconf _NPROCESSORS_ONLN)+1)) && \
        make -j $(($(getconf _NPROCESSORS_ONLN)+1)) install && \
        make -j $(($(getconf _NPROCESSORS_ONLN)+1)) samples && \
        make -j $(($(getconf _NPROCESSORS_ONLN)+1)) config && \
        \
        sed -i -e 's/# MAXFILES=/MAXFILES=/' /usr/sbin/safe_asterisk && \
        rm -Rf /usr/src/asterisk*
        
#Enable GUI
RUN svn co http://svn.asterisk.org/svn/asterisk-gui/branches/2.0 /var/lib/asterisk/static-http
        
# Copy in default configs
COPY conf/http.conf /etc/asterisk/http.conf
COPY conf/manager.conf /etc/asterisk/manager.conf
#Wrapper
COPY misc/supervisord.conf /etc/supervisor/conf.d/supervisord.conf
#SNMP
COPY conf/snmpd.conf /etc/snmp/snmpd.conf

CMD ["/usr/bin/supervisord"]
