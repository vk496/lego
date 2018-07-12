#ifndef LDAPClient_h
#define LDAPClient_h

#include "Arduino.h"
#include <Ethernet.h>

class LDAPClient
{
public:
  LDAPClient(int timeout = 2000) : _timeout(timeout), _connected(false), _error(0), _msgid(0) { };
  ~LDAPClient();
  bool connected() { return _client.connected(); };
  // Must be called first.
  void connect(IPAddress &host, int port = 389);
  // Authenticate.
  void bind(const char dn[], const char password[]);
  // Read a single entry.
  bool read(const char dn[], const char uid[8]);
  // LDAP error returned by last operation.
  int error();
  String getMAIL();
private:
  bool readTagLength(int *tag, int *length);
  bool decodeINTEGER(int tag, int *value);
  bool decodeSTRING(int tag, String &value);
  bool decodeResult(int *resultCode, String &matchedDN, String &diagnosticMessage);
//  void printByteArray(byte *buffer, int length);

  int _timeout;
  unsigned long _pdutime;
  int _pdupos;
  bool _connected;
  EthernetClient _client;
  int _error;
  int _msgid;
  String _mail = "N/A";
//  std::map<String, std::vector<String> >_attrs;
};

#endif
