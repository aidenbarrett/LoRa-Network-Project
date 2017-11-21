/* ESP8266 board
 * Connects to the MASTER RN2483 over SS
 * Credential details must be entered on line numbers : 16, 17, 20, 21, 22, 23
 */

//Header files
#include <ESP8266WiFi.h>
#include <PubSubClient.h> // https://github.com/knolleary/pubsubclient/releases/tag/v2.3
#include <SoftwareSerial.h> //0006
#include "LoraFunctions.h" //0008

//Main pin definitions
#define LED_PIN 5 //0007

//-------- Customise these values -----------
const char* ssid = "AidenAndroid";
const char* password = "Peachylife101";

// Enter IBM Bluemix configuration parameters for your account
#define ORG "2ixtbg"
#define DEVICE_TYPE "ESP8266"
#define DEVICE_ID "5CCF7F8012C5"
#define TOKEN "ZG1lAlKSm+RukRFhW7"
//-------- Customise the above values after updating Bluemix tokens!!! --------

char server[] = ORG ".messaging.internetofthings.ibmcloud.com";
char topic[] = "iot-2/evt/status/fmt/json";
char authMethod[] = "use-token-auth";
char token[] = TOKEN;
char clientId[] = "d:" ORG ":" DEVICE_TYPE ":" DEVICE_ID;

WiFiClient wifiClient;
PubSubClient client(server, 1883, NULL, wifiClient);

SoftwareSerial ESPSerial(12,13); //Rx,Tx //0001 - Setting-up SS with MASTER RN2483

//Global variables
String str, recd, res, recdFromMasterLora=""; //0002
int resInt; //0003

void setup() {

 //Starting serial communication
 Serial.begin(115200);
 Serial.println();

 //0004 BEGIN
 //Initiating SS with MASTER RN2483 
 ESPSerial.begin(9600);
 delay(1000);
 //0004 END

 Serial.println(String(server)+'\n'+String(clientId));
 
 Serial.print("Connecting to "); Serial.print(ssid);

 //Initiating ESP8266
 WiFi.begin(ssid, password);
 while (WiFi.status() != WL_CONNECTED) {
    delay(500);
    Serial.print(".");
 } 
 Serial.println("");

 Serial.print("WiFi connected, IP address: "); Serial.println(WiFi.localIP());
}

int counter = 0;

void loop() {
   //Connecting to IBM Bluemix  
   if (!client.connected()) {
     Serial.print("Reconnecting client to ");
     Serial.println(server);
     while (!client.connect(clientId, authMethod, token)) {
       Serial.print(".");
       delay(500);
     }
     Serial.println();
   }
  
   //0005 BEGIN
   recdFromMasterLora="";
   //Checking if MASTER has sent someting !
   if (ESPSerial.available()) {
      recdFromMasterLora = String(ESPSerial.readStringUntil('\n'));
   }

   //If MASTER has sent something then ...
   if(recdFromMasterLora!="") {
      Serial.println("GOT "+recdFromMasterLora);//This would be HEX version of M001:1:43:22:56:

      //Preparing to send to IBM Bluemix
      String payload = "{\"d\":{\"Name\":\"18FE34D81E46\"";
      payload += ",\"temperature\":";
      payload += returnValue(returnHEXDataString(recdFromMasterLora), 1, "3A"); //Extracting the first sensor value eg. 43
      payload += ",\"humidity\":";
      payload += returnValue(returnHEXDataString(recdFromMasterLora), 2, "3A"); //Extracting the first sensor value eg. 22
      payload += "}}";
        
      Serial.print("Sending payload: ");
      Serial.println(payload);
      
      if (client.publish(topic, (char*) payload.c_str())){ //Sending to IBM Bluemix
        Serial.println("Publish ok");
      } 
      else{
        Serial.println("Publish failed");
      }
   }
   //0005 END
}
