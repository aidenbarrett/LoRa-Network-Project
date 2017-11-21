#include <OneWire.h>

/* This is Slave One
*/

//Header files
#include <SoftwareSerial.h>
#include "LoraFunctions.h"
#include "SensorFunctions.h"

 //<DS18B20>
#include <DallasTemperature.h>
//</DS18B20>  
//<DS1620>
#include "ds1620.h"
//</DS1620>

//Main pin definitions
#define LED_PIN 19
#define LED_RTX 12
#define RESET_PIN 14

//<DS18B20>
// Data wire is plugged into pin 0 on the Arduino
#define ONE_WIRE_BUS 2
//</DS18B20>

//Set-up SS with RN2483
SoftwareSerial loraSerial(9, 10); //Rx,Tx
// Setup a oneWire instance to communicate with any OneWire devices 
// (not just Maxim/Dallas temperature ICs)
//<DS18B20>
OneWire oneWire(ONE_WIRE_BUS);
// Pass our oneWire reference to Dallas Temperature.
DallasTemperature sensors(&oneWire);
//</DS18B20>
//<DS1620>
Ds1620 ds1620 = Ds1620(5/*rst*/,6/*clk*/,7/*dq*/);
//</DS1620>

//Global variable definitions
String str, res, toRasPi, sampleStr="4d304d304d304d304d304d30", onOffCommand="";
int resInt, theTime, s0Val=0, s1Val=0, s2Val=5;
char onOffCommandChar=' ';

void setup() {
  pinMode(LED_PIN, OUTPUT);

  pinMode(LED_RTX, OUTPUT);
  led_off_rtx();
  
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

  //Configuring SLAVE RN2483 - set watchdog timer 0 - endless loop
  loraSerial.println("radio set wdt 0");
  str = loraSerial.readStringUntil('\n');
  Serial.println(str);

  Serial.println("starting loop");
    //<DS18B20>
  // Start up the library
  Serial.println("Dallas Temperature IC Control Library Demo");
  sensors.begin();
  //<DS18B20>
  //<DS1620>
  // Sets cpu mode as 1 shot
  ds1620.config();
  //</DS1620>
}

void loop() {  
  //Variable initialization
  res="";
  str="";

  //Beginning to wait for Master No. + Node No. from MASTER eg. HEX version of M001:1
  loraSerial.println("radio rx 0");
  led_off_rtx();
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
      onOffCommand = hexStringToString(res);
      Serial.println("Received from MASTER "+onOffCommand); 
    }
    else{
      Serial.println("Received nothing");
    }
  }    
  else{
    Serial.println("radio not going into receive mode");
    delay(1000);
  }
  
  if(onOffCommand.charAt(5)=='1'){ //'1' is the NodeNumber for Slave 1
    onOffCommandChar = onOffCommand.charAt(0);
    if(onOffCommandChar == 'A') led_on();
    if(onOffCommandChar == 'Z') led_off();
  
    
    //Reading sensor values
    led_on_rtx();
  //<DS18B20>
    sensors.requestTemperatures(); // Send the command to get temperatures
    s0Val=(int)sensors.getTempCByIndex(0); // Why "byIndex"? 
      // You can have more than one IC on the same bus. 
      // 0 refers to the first IC on the wire
    delay(1000);
    //s0Val=readSensor0();
    //</DS18B20>
    //<DS1620>
    // Start temperature converion in 1 shot mode
    ds1620.start_conv();
    // Read the last temperature converson
    int raw_data = ds1620.read_data(); 
    // Stop conversion, not really needed since we are in 1 shot mode
    ds1620.stop_conv();
    s1Val = (int)(raw_data / 2.0); 
    //s1Val=readSensor1();
    //<DS1620>
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
}

void toggle_led(){
  digitalWrite(12, !digitalRead(12));
}

void led_on(){
  digitalWrite(LED_PIN, 1);
}

void led_off(){
  digitalWrite(LED_PIN, 0);
}

void led_on_rtx(){
  digitalWrite(LED_RTX, 1);
}

void led_off_rtx(){
  digitalWrite(LED_RTX, 0);
}
