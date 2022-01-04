#include <FirebaseArduino.h>
#include <ArduinoJson.h>
#include <ESP8266WiFi.h>
#include <dht.h>

dht DHT;
#define DHT11_PIN 2
const char* ssid =  " ";     // Enter your WiFi Network's SSID
const char* pass =  "  "; // Enter your WiFi Network's Password
#define FIREBASE_HOST "smart-farming-84881-default-rtdb.firebaseio.com"
//#define FIREBASE_AUTH "Your firebase key"
int WET= 16; // Wet Indicator at Digital PIN D0
int DRY= 2;  // Dry Indicator at Digital PIN D4
float temp,mois,hum;
int sense_Pin= 0; // Soil Sensor input at Analog PIN A0
int value= 0;

void setup() {
  // put your setup code here, to run once:
Serial.begin(9600);
   WiFi.begin(ssid, pass);
   Serial.println("........");
 while (WiFi.status() != WL_CONNECTED) {
   Serial.print(".");
   delay(1000);
 }
  Serial.println("WiFi connected");
 Serial.println("IP address: ");
 Serial.println(WiFi.localIP());
  Firebase.begin(FIREBASE_HOST);
   
   delay(2000);
}

void loop() {
 temp=5;
  Serial.print("MOISTURE LEVEL : ");
   value= analogRead(sense_Pin);
   value= value/10;
   Serial.println(value);
   if(value<50)
   {
      digitalWrite(WET, HIGH);
   }
   else
   {
      digitalWrite(DRY,HIGH);
   }
  int chk = DHT.read11(DHT11_PIN);
  Serial.print("Temperature = ");
  Serial.println(DHT.temperature);
  temp=DHT.temperature;
  mois=value;
  hum=DHT.humidity;
  Firebase.setFloat("/Members/UMleEiWKHCdI1oAnMfHQM0P8d7H2/temperature",temp);
   Firebase.setFloat("/Members/UMleEiWKHCdI1oAnMfHQM0P8d7H2/moisture",mois);
    Firebase.setFloat("/Members/UMleEiWKHCdI1oAnMfHQM0P8d7H2/humidity",hum);
   Serial.print("Humidity = ");
  Serial.println(DHT.humidity);

  if(Firebase.failed()){
    Serial.println(Firebase.error());
    return;
  }

   delay(1000);
}
