/*
 * SS_RX is digital pin 9 (connect to TX of RN2483)
 * SS_TX is digital pin 10 (connect to RX of RN2483)
 * Pin 14 (A0) (connect to RESET of RN2483)
 */

#include <SoftwareSerial.h>
#define LED_PIN 13

SoftwareSerial loraSerial(9, 10);

String str;

void setup() {
  //output LED pin
  pinMode(LED_PIN, OUTPUT);
  led_off();
  
  led_on();
  delay(1000);
  led_off();

//____________________________________________________________  

  pinMode(14, INPUT);//Pin 14 is the A0 pin used to reset RN2483
  
  // Open serial communications and wait for port to open:
  Serial.begin(57600);
  while (!Serial) {
    ; // wait for serial port to connect. Needed for native USB port only?
  }

  //Initiating Software Serial
  Serial.println("Initiating TX Lora");
  Serial.println("Wait ...");
  loraSerial.begin(57600);
  delay(1000);
  
  //Hard resetting RN2483
  pinMode(14, OUTPUT);
  digitalWrite(14, LOW);
  delay(500);
  digitalWrite(14, HIGH);
  pinMode(14, INPUT);
  delay(1000);
  Serial.println("Tx Lora initiated...");
  Serial.println("");
//____________________________________________________________
  loraSerial.println("mac pause");
  str = loraSerial.readStringUntil('\n');
  Serial.println("<mp"+str+'>');
  str = loraSerial.readStringUntil('\n');
  Serial.println("<.."+str+'>');
  
  loraSerial.println("radio set freq 868000000");
  str = loraSerial.readStringUntil('\n');
  Serial.println("<868m-"+str+'>');
  loraSerial.println("radio set pwr 14");
  str = loraSerial.readStringUntil('\n');
  Serial.println("pwr14-"+str);
  loraSerial.println("radio set wdt 0");
  str = loraSerial.readStringUntil('\n');
  Serial.println("wdt-"+str);
  
  Serial.println("starting loop");
}

void loop() {
  led_on();
  loraSerial.println("radio tx 35");
  str = loraSerial.readStringUntil('\n');
  Serial.print("Tx : <"+str+'>');
  str = loraSerial.readStringUntil('\n');
  Serial.print('<'+str+'>');
  led_off();

  Serial.print("<Whats_the_craic?");
    loraSerial.println("sys get ver");
  Serial.print("<v"+loraSerial.readStringUntil('\n')+'>');
  
  loraSerial.println("radio get freq");
  Serial.print("<f"+loraSerial.readStringUntil('\n')+'>');
  
  loraSerial.println("radio get bt");
  Serial.print("<b"+loraSerial.readStringUntil('\n')+'>');
   
  loraSerial.println("radio get mod");
  Serial.print("<m"+loraSerial.readStringUntil('\n')+'>');
    
  loraSerial.println("radio get pwr");
  Serial.print("<p"+loraSerial.readStringUntil('\n')+'>');
    
  loraSerial.println("radio get sf");
  Serial.print("<s"+loraSerial.readStringUntil('\n')+'>');
  
  loraSerial.println("radio get afcbw");
  Serial.print("<a"+loraSerial.readStringUntil('\n')+'>');
   
  loraSerial.println("radio get rxbw");
  Serial.print("<r"+loraSerial.readStringUntil('\n')+'>');
  
  loraSerial.println("radio get bitrate");
  Serial.print("<b"+loraSerial.readStringUntil('\n')+'>');
  
  loraSerial.println("radio get fdev");
  Serial.print("<f"+loraSerial.readStringUntil('\n')+'>');
    
  loraSerial.println("radio get prlen");
  Serial.print("<p"+loraSerial.readStringUntil('\n')+'>');
    
  loraSerial.println("radio get crc");
  Serial.print("<c"+loraSerial.readStringUntil('\n')+'>');
    
  loraSerial.println("radio get iqi");
  Serial.print("<i"+loraSerial.readStringUntil('\n')+'>');
    
  loraSerial.println("radio get cr");
  Serial.print("<c"+loraSerial.readStringUntil('\n')+'>');
  
  loraSerial.println("radio get wdt");
  Serial.print("<w"+loraSerial.readStringUntil('\n')+'>');
   
  loraSerial.println("radio get sync");
  Serial.print("<s"+loraSerial.readStringUntil('\n')+'>');
  
  loraSerial.println("radio get bw");
  Serial.print("<b"+loraSerial.readStringUntil('\n')+">\n");
  delay(3000);
}

void led_on(){
  digitalWrite(LED_PIN, 1);
}
void led_off(){
  digitalWrite(LED_PIN, 0);
}
