/* This is the Master Device
 *  
*/

//Header files
#include <SoftwareSerial.h>
#include "LoraFunctions.h"
#include "SensorFunctions.h"

//Global definitions & main pin definitions
#define LED_PIN 19
#define LED_RTX 12
#define RESET_PIN 14
#define MASTER_CODE "M001"
#define MASTER_CODE_ON "A001"
#define MASTER_CODE_OFF "Z001"
#define NUMBER_OF_NODES 1

SoftwareSerial loraSerial(9, 10); //SS between Arduino and MASTER RN2483

//Global variable definitions
String strToSend, strRecd, str, res, toRasPi;
int resInt, nodeNumber=0;
int readVal;

void setup() {
  //output LED pin
  pinMode(LED_PIN, OUTPUT);

  pinMode(LED_RTX, OUTPUT);
  led_off_rtx();

  //Setting RESET PIN of MASTER RN2483
  pinMode(RESET_PIN, INPUT);//Pin 14 is the A0 pin used to reset RN2483
  
  //Open serial communications and wait for port to open:
  Serial.begin(57600);
  while (!Serial) {;}

  //Serial.println("Initiating MASTER Lora");

  //Initiating SS with MASTER RN2483 at 57600 bauds
  //Serial.println("Initiating MASTER Lora Software Serial");
  loraSerial.begin(57600);
  delay(1000);
  
  //Initiating MASTER RN2483
  pinMode(RESET_PIN, OUTPUT);
  digitalWrite(RESET_PIN, LOW);
  delay(500);
  digitalWrite(RESET_PIN, HIGH);
  pinMode(RESET_PIN, INPUT);
  delay(1000);
  //Serial.println("Lora initiated...");
  //Serial.println("");

  //Configuring MASTER RN2483 - pausing mac
  loraSerial.println("mac pause");
  str = loraSerial.readStringUntil('\n');
  //Serial.println(str);
    
  //Configuring MASTER RN2483 - setting frequency
  loraSerial.println("radio set freq 866700000");
  str = loraSerial.readStringUntil('\n');
  //Serial.println(str);
  
  //Serial.println("starting loop");
}

void loop(){
  for(nodeNumber=1;nodeNumber<(NUMBER_OF_NODES+1);nodeNumber++){//Useful for multiple nodes, for 1 node runs only once
    strToSend=stringToHexString(MASTER_CODE)+"3A"+stringToHexString(String(nodeNumber));
    if (Serial.available()) {  
      readVal = (int)Serial.read()-48;      
      switch(readVal){
        case 0:
          strToSend=stringToHexString(MASTER_CODE_OFF)+"3A"+stringToHexString(String(nodeNumber));
          digitalWrite(LED_PIN, LOW);
          //Serial.println('0');
          break;
        case 1:
          strToSend=stringToHexString(MASTER_CODE_ON)+"3A"+stringToHexString(String(nodeNumber));
          digitalWrite(LED_PIN, HIGH);
          //Serial.println('1');
          break;
        default:
          //Serial.println('D');
          break;
      }
    }
    loraSerial.print("radio tx ");//Transmitting the HEX Data String - Part 1 of 2
    loraSerial.println(strToSend);//3A=':'//Transmitting the HEX Data String - Part 2 of 2
    str = loraSerial.readStringUntil('\n');
    //Serial.print("Tx1>"+str);
    str = loraSerial.readStringUntil('\n');
    //Serial.println(" Tx2>"+str);
    //Beginning to wait for Sensor Values as HEX Data String from SLAVE
    loraSerial.println("radio rx 0"); 
    led_off_rtx();   
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
        //Serial.print("---> ");
        strRecd = str.substring(10); //Extracting received HEX message
        //Serial.println(strRecd); 
        //Serial.print("Received from SLAVE ");
        //Serial.println(decodeReceivedString(strRecd,"3A",":"));//Getting DataString from HEXDataString eg. 43:22:56:
      }
      else{
        Serial.println("Received nothing");
      }
    }    
    else{
      Serial.println("radio not going into receive mode");
      delay(1000);
    }
    toRasPi = strToSend+"3A"+strRecd;
    Serial.println("Sending to RasPi3 "+hexStringToString(strToSend)+":"+decodeReceivedString(strRecd,"3A",":"));
    //Serial.println(toRasPi+" --->");
  } 
}

void toggle_led(){
  digitalWrite(12, !digitalRead(12));
}

void led_on_rtx(){
  digitalWrite(LED_RTX, 1);
}

void led_off_rtx(){
  digitalWrite(LED_RTX, 0);
}
