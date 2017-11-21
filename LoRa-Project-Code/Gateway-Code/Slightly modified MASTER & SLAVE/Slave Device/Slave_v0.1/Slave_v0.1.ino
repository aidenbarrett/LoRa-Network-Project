/* SLAVE waits to receive the Master No. (M001) and Node Number (1) from Master
 *  On receipt, transmits the three values of SensorValue0, SensorValue1 and SensorValue2.
*/

//Header files
#include <SoftwareSerial.h>
#include "LoraFunctions.h"
#include "SensorFunctions.h"

//Main pin definitions
#define LED_PIN 13
#define RESET_PIN 14

//Set-up SS with RN2483
SoftwareSerial loraSerial(9, 10); //Rx,Tx

//Global variable definitions
String str, res, toRasPi, sampleStr="4d304d304d304d304d304d30";
int resInt, theTime, s0Val=0, s1Val=0, s2Val=5;

void setup() {
  pinMode(LED_PIN, OUTPUT);
  led_off();

  //Setting RESET PIN of SLAVE RN2483
  pinMode(RESET_PIN, INPUT);//Pin 14 is the A0 pin used to reset RN2483
  
  // Open serial communications and wait for PC port to open:
  Serial.begin(57600);
  while (!Serial) {;}

  Serial.println("Initiating SLAVE Lora");

  //Initiating SS with SLAVE RN2483 at 57600 bauds
  Serial.println("Initiating SLAVE Lora Software Serial");
  Serial.println("Wait ...");
  loraSerial.begin(57600);
  delay(1000);
  
  //Initiating SLAVE RN2483
  pinMode(RESET_PIN, OUTPUT);
  digitalWrite(RESET_PIN, LOW);
  delay(500);
  digitalWrite(RESET_PIN, HIGH);
  pinMode(RESET_PIN, INPUT);
  delay(1000);
  Serial.println("Lora initiated...");
  Serial.println("");

  //Configuring SLAVE RN2483 - pausing mac
  loraSerial.println("mac pause");
  str = loraSerial.readStringUntil('\n');
  Serial.println(str);

  //Configuring SLAVE RN2483 - setting frequency
  loraSerial.println("radio set freq 866700000");
  str = loraSerial.readStringUntil('\n');
  Serial.println(str);
  
  Serial.println("starting loop");
}

void loop() {  
  //Variable initialization
  res="";
  str="";

  //Beginning to wait for Master No. + Node No. from MASTER eg. HEX version of M001:1
  loraSerial.println("radio rx 0");
  led_off();
  str = loraSerial.readStringUntil('\n');
  
  //On successful setting of receiving mode then ...
  if (str.indexOf("ok") == 0){
    str = String("");
    while(str==""){
      str = loraSerial.readStringUntil('\n');
    }
    //On successful receipt of message from MASTER then ...
    if ( str.indexOf("radio_rx") == 0 ){
      toggle_led();
      Serial.print("\n\nSLAVE :\n---> ");
      res = str.substring(10); //Extracting received HEX message
      Serial.println(res);
      Serial.println("Received from MASTER "+hexStringToString(res)); 
    }
    else{
      Serial.println("Received nothing");
    }
  }    
  else{
    Serial.println("radio not going into receive mode");
    delay(1000);
  }
  //Reading sensor values
  led_on();
  s0Val=readSensor0();
  s1Val=readSensor1();
  s2Val=readSensor2();
  sampleStr="";
  Serial.println("Sending to MASTER "+String(s0Val)+":"+String(s1Val)+":"+String(s2Val)+":");
  
  //Preparing the Data String as 'value1:value2:value3:'
  //Then converting it to HEX also to get the HEX Data String which will be transmitted eg. 43:22:56:
  //Note the de-limiter is ':' which is "3A" in HEX
  sampleStr = intToHexString(s0Val)+stringToHexString(":")+intToHexString(s1Val)+stringToHexString(":")+intToHexString(s2Val)+stringToHexString(":");
  loraSerial.print("radio tx "); //Transmitting the HEX Data String eg. HEX version of 43:22:56:
  loraSerial.println(sampleStr);
  delay(200);
  str = loraSerial.readStringUntil('\n');
  Serial.println(sampleStr+" --->");
  //Serial.println("Tx 1>"+str+':'+sampleStr);
  str = loraSerial.readStringUntil('\n');
  //Serial.println("Tx 2>"+str);
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
