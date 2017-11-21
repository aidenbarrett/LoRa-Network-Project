/* This is the MAster device for the Arduino Uno/ ATmega328P Master
 *  MASTER transmits (Tx) the MASTER_CODE + nodeNumber
 *  and waits to receive (Rx) a string of sensor values from that nodeNumber (i.e. Slave 1)
 *  It then sends this string of values over SS to the ESP8266
*/

//Header files
#include <SoftwareSerial.h>
#include "LoraFunctions.h"
//#include "SensorFunctions.h"

//Global definitions & main pin definitions
#define LED_PIN 13
#define RESET_PIN 14
#define MASTER_CODE "M001"
#define NUMBER_OF_NODES 1

SoftwareSerial loraSerial(9, 10); //SS between Arduino and MASTER RN2483
SoftwareSerial STDBSerial(15, 16); //Rx (connects to 13 of ESP8266) ,Tx (connects to 12 of ESP8266) : SS between Arduino and ESP8266

//Global variable definitions
String strToSend, strRecd, str, res, toSTDB;
int resInt, nodeNumber=0;

void setup() {
  //output LED pin
  pinMode(LED_PIN, OUTPUT);
  led_off();

  //Setting RESET PIN of MASTER RN2483
  pinMode(RESET_PIN, INPUT);//Pin 14 is the A0 pin used to reset RN2483
  
  // Open serial communications and wait for port to open:
  Serial.begin(57600);
  while (!Serial) {;}

  Serial.println("Initiating MASTER Lora");

  //Initiating Software Serial for ESP8266 
  Serial.println("Initiating STDB Connection");
  Serial.println("Wait ...");
  STDBSerial.begin(9600);
  delay(1000);

  //Initiating SS with MASTER RN2483 at 57600 bauds
  Serial.println("Initiating MASTER Lora Software Serial");
  loraSerial.begin(57600);
  delay(1000);
  
  //Initiating MASTER RN2483
  pinMode(RESET_PIN, OUTPUT);
  digitalWrite(RESET_PIN, LOW);
  delay(500);
  digitalWrite(RESET_PIN, HIGH);
  pinMode(RESET_PIN, INPUT);
  delay(1000);
  Serial.println("Lora initiated...");
  Serial.println("");

  //Configuring MASTER RN2483 - pausing mac
  loraSerial.println("mac pause");
  str = loraSerial.readStringUntil('\n');
  Serial.println(str);
    
  //Configuring MASTER RN2483 - setting frequency
  loraSerial.println("radio set freq 866700000");
  str = loraSerial.readStringUntil('\n');
  Serial.println(str);
  
  Serial.println("starting loop");
}

void loop(){
  for(nodeNumber=1;nodeNumber<(NUMBER_OF_NODES+1);nodeNumber++){//Useful for multiple nodes, for 1 node runs only once
    //Preparing to send Master No. + Node No. to SLAVE
    Serial.print("\nMASTER :\nSending to SLAVE "+String(MASTER_CODE)+":"+String(nodeNumber)+'\n');
    //Preparing the Data String as 'Master_No:Node_No' eg. M001:1
    //Then converting it to HEX also to get the HEX Data String which will be transmitted
    //Note the de-limiter is ':' which is "3A" in HEX
    strToSend=stringToHexString(MASTER_CODE)+"3A"+stringToHexString(String(nodeNumber));
    loraSerial.print("radio tx ");//Transmitting the HEX Data String - Part 1 of 2
    loraSerial.println(strToSend);//3A=':'//Transmitting the HEX Data String - Part 2 of 2
    Serial.println(strToSend+" --->");//3A=':'
    str = loraSerial.readStringUntil('\n');
    //Serial.print("Tx1>"+str);
    str = loraSerial.readStringUntil('\n');
    //Serial.println(" Tx2>"+str);


    //Beginning to wait for Sensor Values as HEX Data String from SLAVE
    loraSerial.println("radio rx 0");    
    led_off();
    str = loraSerial.readStringUntil('\n');
    
    //On successful setting of receiving mode then ...
    if ( str.indexOf("ok") == 0 ){
      str = String("");
      while(str==""){
        str = loraSerial.readStringUntil('\n');
      }
      //On successful receipt of message from SLAVE then ...
      if ( str.indexOf("radio_rx") == 0 ){
        toggle_led();
        Serial.print("---> ");
        strRecd = str.substring(10); //Extracting received HEX message
        Serial.println(strRecd); 
        Serial.print("Received from SLAVE ");
        Serial.println(decodeReceivedString(strRecd,"3A",":"));//Getting DataString from HEXDataString eg. 43:22:56:
      }
      else{
        Serial.println("Received nothing");
      }
    }    
    else{
      Serial.println("radio not going into receive mode");
      delay(1000);
    }
    toSTDB = strToSend+"3A"+strRecd;
    Serial.println("Sending to STDB "+hexStringToString(strToSend)+":"+decodeReceivedString(strRecd,"3A",":"));
    Serial.println(toSTDB+" --->");
    STDBSerial.println(toSTDB); //Sending the HEX versions of combined string to ESP8266 i.e. the HEX version of M001:1:43:22:56:
  } 
}

void toggle_led(){
  digitalWrite(13, !digitalRead(13));
}

void led_on(){
  digitalWrite(LED_PIN, 1);
}

void led_off(){
  digitalWrite(LED_PIN, 0);
}
