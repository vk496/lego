#include <LiquidCrystal_I2C.h>
#include <MFRC522.h>
#include <SPI.h>
#include <Ethernet.h>
#include <LDAPClient.h>

LiquidCrystal_I2C lcd(0x3F, 16, 2);


#define SS_PIN 8
#define RST_PIN 9

MFRC522 rfid(SS_PIN, RST_PIN); // Instance of the class
MFRC522::MIFARE_Key key;
// Enter a MAC address for your controller below.
// Newer Ethernet shields have a MAC address printed on a sticker on the shield
byte mac[] = { 0x90, 0xA2, 0xDA, 0x10, 0x81, 0xC0 };

// Set the static IP address to use if the DHCP fails to assign
IPAddress ip(192, 168, 252, 177);

// Initialize the Ethernet client library
// with the IP address and port of the server
// that you want to connect to (port 80 is default for HTTP):
EthernetClient client;

LDAPClient ldap_client;
IPAddress ldap_ip(192, 168, 252, 6);


//char uid[] = "14A79F59";
//LED PINS
int ledPins[] = {7,6,5};

int led_status = 6;

//BUZZER
int buzzerPin = 2;

int codeRead = 0;
String uidString;

void setup() {

    Serial.begin(9600);
    SPI.begin(); // Init SPI bus
    Serial.println("START initialization");
    lcd.init();

    //Serial.println("Init RFID reader");
    rfid.PCD_Init(); // Init MFRC522 

    //Serial.println("Init IP");
    // start the Ethernet connection:
    if (Ethernet.begin(mac) == 0) {
      //Serial.println("Failed to configure Ethernet using DHCP");
      // try to congifure using IP address instead of DHCP:
      Ethernet.begin(mac, ip);
    }
    
    // print your local IP address:
    printIPAddress();

    //Serial.println("Init LDAP");
    ldap_client.connect(ldap_ip, 389);

    if (ldap_client.connected()) {
      //Serial.println("LDAP connected");
      ldap_client.bind("cn=admin,dc=upm,dc=es", "upm_password");
      //ldap_client.read("ou=Personas,dc=upm,dc=es", "(&(objectClass=inetOrgPerson)(registeredAddress=14A79F59)(memberof=cn=rfid,ou=Servicios,dc=upm,dc=es))");
      //ldap_client.read("ou=Personas,dc=upm,dc=es", "14A79F59");
    } else {
      led_status = ledPins[0]; //RED
    }

    
    // Clear the buffer.
    lcd.backlight();

    printLock();

    //Serial.println("LED init");
    for(int index = 0; index < 3; index++){
      pinMode(ledPins[index],OUTPUT);
    }

    pinMode(buzzerPin, OUTPUT);
    ledSTATUS();
    
    //Serial.println("LEGO_PSER initialized");

}

void loop() {
    if (rfid.PICC_IsNewCardPresent()) {
        readRFID();
    }
    delay(100);

}

void readRFID() {

    rfid.PICC_ReadCardSerial();
    Serial.print(F("\nPICC type: "));
    MFRC522::PICC_Type piccType = rfid.PICC_GetType(rfid.uid.sak);
    Serial.println(rfid.PICC_GetTypeName(piccType));

    // Check is the PICC of Classic MIFARE type
    if (piccType != MFRC522::PICC_TYPE_MIFARE_MINI &&
            piccType != MFRC522::PICC_TYPE_MIFARE_1K &&
            piccType != MFRC522::PICC_TYPE_MIFARE_4K) {
        Serial.println(F("Your tag is not of type MIFARE Classic."));
        return;
    }

    clearUID();

    Serial.println("Scanned PICC's UID:");
    printDec(rfid.uid.uidByte, rfid.uid.size);


//    printDec(rfid.uid.uidByte, rfid.uid.size);


//    char * pos = uid;
    unsigned char uid_literal_bytes[rfid.uid.size];
//    /* WARNING: no sanitization or error-checking whatsoever */
//    for (size_t count = 0; count < sizeof uid_literal_bytes / sizeof *uid_literal_bytes; count++) { //Convert char array to literal byte array
//        sscanf(pos, "%2hhx", &uid_literal_bytes[count]);
//        pos += 2;
//    }


    char code2[rfid.uid.size*2 +1], *pos2 = code2; //Pointer where save char represenation of UID bytes array
    for (size_t count = 0; count < sizeof rfid.uid.uidByte / sizeof *rfid.uid.uidByte; count++) {
        sprintf(pos2, "%02x", rfid.uid.uidByte[count]);
        pos2 += 2;
    }
    code2[rfid.uid.size*2] = '\0'; //End propperly the String
    
//    Serial.println(code2);

//    if (false) {
    if (ldap_client.connected() && ldap_client.read("ou=Personas,dc=upm,dc=es", strupr(code2))) {
        Serial.println("\nACCESS!");

//        char code2[rfid.uid.size * 2], *pos2 = code2; //Pointer where save char represenation of UID bytes array
//        for (size_t count = 0; count < sizeof uid_literal_bytes / sizeof *uid_literal_bytes; count++) {
//            sprintf(pos2, "%02x", uid_literal_bytes[count]);
//            pos2 += 2;
//        }

        uidString = String(strupr(code2)); //Uppercase

        authGOOD();
        
    } else {
        authBAD();
        Serial.println("\nUnknown Card");
    }

    authOFF();
    
    // Halt PICC
    rfid.PICC_HaltA();

    // Stop encryption on PCD
    rfid.PCD_StopCrypto1();
}

void printDec(byte *buffer, byte bufferSize) {
    for (int i = 0; i < bufferSize; i++) {
        Serial.print(buffer[i], HEX);
    }
    Serial.println("");
}

void clearUID() {
    lcd.clear();
}

void waitTime() {
  delay(4000);
}

void printUID() {

    lcd.setCursor(0, 1);
    lcd.print("UID: ");
    lcd.setCursor(5, 1);
    lcd.print(uidString);
    lcd.display();
}

void printUnlockMessage() {


    lcd.setCursor(0, 0);
    lcd.print("                "); //clear line
    lcd.display();

    lcd.setCursor(0, 0);
    lcd.print(ldap_client.getMAIL());
    lcd.display();

}

void printLock() {
    lcd.setCursor(0, 0);
    lcd.print("RFID Lock");
    lcd.display();
}

void printBadAuth() {
    lcd.setCursor(0, 1);
    lcd.print("Bad Auth");
    lcd.display();
}


void printIPAddress()
{
  Serial.print("My IP address: ");
  for (byte thisByte = 0; thisByte < 4; thisByte++) {
    // print the value of each byte of the IP address:
    Serial.print(Ethernet.localIP()[thisByte], DEC);
    Serial.print(".");
  }

  Serial.println();
}

void ledOFF(){
  for(int index = 0; index < 3; index++){
    digitalWrite(ledPins[index], LOW);
  }
}

void ledGREEN(){
  ledOFF();
  digitalWrite(ledPins[2], HIGH);
}

void ledSTATUS(){
  ledOFF();
  digitalWrite(led_status, HIGH);
}

void ledRED(){
  ledOFF();
  digitalWrite(ledPins[0], HIGH);
}

void buzzerON(){
  digitalWrite(buzzerPin, HIGH);
}

void buzzerOFF(){
  digitalWrite(buzzerPin, LOW);
}

void authGOOD() {
  ledGREEN();
  printUID();
  printUnlockMessage();
  for (int i=0; i<3; i++){
    buzzerON();
    delay(150);
    buzzerOFF();
    delay(150);
  }
  waitTime();
}

void authBAD(){
  ledRED();
  printBadAuth();
  buzzerON();
  waitTime();
  buzzerOFF();
}

void authOFF() {
  ledSTATUS();
  buzzerOFF();
  clearUID();
  printLock();
}

