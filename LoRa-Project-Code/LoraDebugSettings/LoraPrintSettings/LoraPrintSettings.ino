/*
 * SS_RX is digital pin 9 (connect to TX of RN2483)
 * SS_TX is digital pin 10 (connect to RX of RN2483)
 */
 
#include <SoftwareSerial.h>

SoftwareSerial LoraSerial(9, 10); // RX, TX

String str;

void setup() {  
  
  //Initiating Hardware Serial
  Serial.begin(57600);
  while (!Serial) {
    ;
  }
  
  //Initiating Software Serial
  Serial.println("Initiating Lora");
  Serial.println("Wait ...");
  LoraSerial.begin(57600);
  delay(1000);

  Serial.println("Lora initiated...");
  Serial.println("");
  
  //________Displaying Module Settings_________________

  LoraSerial.println("sys get ver");
  str = LoraSerial.readStringUntil('\n');
  Serial.print("VERSION : ");
  Serial.println(str);
  
  LoraSerial.println("mac pause");
  str = LoraSerial.readStringUntil('\n');

  LoraSerial.println("radio set freq 866700000");
  str = LoraSerial.readStringUntil('\n');
  LoraSerial.println("radio get freq");
  str = LoraSerial.readStringUntil('\n');
  Serial.print("FREQUENCY : ");
  Serial.println(str);

  LoraSerial.println("radio get bt");
  str = LoraSerial.readStringUntil('\n');
  Serial.print("BT : ");
  Serial.println(str);
  
  LoraSerial.println("radio get mod");
  str = LoraSerial.readStringUntil('\n');
  Serial.print("MODE : ");
  Serial.println(str);
    
  LoraSerial.println("radio get pwr");
  str = LoraSerial.readStringUntil('\n');
  Serial.print("POWER : ");
  Serial.println(str);
  
  LoraSerial.println("radio get sf");
  str = LoraSerial.readStringUntil('\n');
  Serial.print("SF : ");
  Serial.println(str);
  
  LoraSerial.println("radio get afcbw");
  str = LoraSerial.readStringUntil('\n');
  Serial.print("AUTO FREQ CORR BW : ");
  Serial.println(str);
  
  LoraSerial.println("radio get rxbw");
  str = LoraSerial.readStringUntil('\n');
  Serial.print("RX SIGNAL BW : ");
  Serial.println(str);
  
  LoraSerial.println("radio get bitrate");
  str = LoraSerial.readStringUntil('\n');
  Serial.print("BIT RATE : ");
  Serial.println(str);
  
  LoraSerial.println("radio get fdev");
  str = LoraSerial.readStringUntil('\n');
  Serial.print("FREQ DEV SETTING : ");
  Serial.println(str);
    
  LoraSerial.println("radio get prlen");
  str = LoraSerial.readStringUntil('\n');
  Serial.print("PREAMBLE LENGTH : ");
  Serial.println(str);
  
  LoraSerial.println("radio get crc");
  str = LoraSerial.readStringUntil('\n');
  Serial.print("CRC HEADER STATUS : ");
  Serial.println(str);
  
  LoraSerial.println("radio get iqi");
  Serial.print("INVERT IQ STATUS : ");
  str = LoraSerial.readStringUntil('\n');
  Serial.println(str);
  
  LoraSerial.println("radio get cr");
  str = LoraSerial.readStringUntil('\n');
  Serial.print("CODING RATE : ");
  Serial.println(str);
  
  LoraSerial.println("radio get wdt");
  str = LoraSerial.readStringUntil('\n');
  Serial.print("WATCHDOG TIME OUT(MS) : ");
  Serial.println(str);
  
  LoraSerial.println("radio get sync");
  str = LoraSerial.readStringUntil('\n');
  Serial.print("SYNC WORD : ");
  Serial.println(str);
  
  LoraSerial.println("radio get bw");
  str = LoraSerial.readStringUntil('\n');
  Serial.print("RADIO BW : ");
  Serial.println(str);
  //____________________________________________________________
}

void loop() {
  if (LoraSerial.available()) {
    Serial.write(LoraSerial.read());
  }
}
