#include "SPI.h"
#include "Ethernet.h"
#include "WebServer.h"

static uint8_t mac[6] = { 0x02, 0xAA, 0xBB, 0xCC, 0x00, 0x22 };

static uint8_t ip[4] = { 192, 168, 1, 33 };

#define PREFIX "/light"

uint8_t lights[5] = {0,255,255,255,255};
uint8_t lightsNow[5] = {0,255,255,255,255};
//uint8_t lightPins[5] = {0, 6, 9, 10, 11};
uint8_t lightPins[5] = {0, 3, 5, 6, 9};

WebServer webserver(PREFIX, 80);

void lightCmd(WebServer &server, WebServer::ConnectionType type, char *, bool)
{
  if (type == WebServer::POST)
  {
    bool repeat;
    char name[32], value[32];
    do
    {
      repeat = server.readPOSTparam(name, 32, value, 32);

       Serial.println(name);
       if (strncmp(name, "light_", 6) == 0) {
         int light = (int)(name[6] - '0');         
         int level = strtoul(value, NULL, 10);
         Serial.println(light);
         Serial.println(level);
         if (light >= 1 && light <=5 && level >= 0 && level <= 255)
           lights[light] = level;
       }

    } while (repeat);
    
    server.httpSeeOther(PREFIX);

    return;
  }

  server.httpSuccess("text/html", "Access-Control-Allow-Origin: *\r\n");

  if (type == WebServer::GET)
  {
    char current[128];
    char tmp[16];    
    current[0] = '\0';
    
    for (int i=1;i<5;i++) {
      sprintf(tmp, "light_%d=%d", i, lights[i]);
      strcat(current, tmp);
      if (i < 4) 
        strcat(current, "&");
    }
    server.print(current);
  }

}

void setup()
{
  for (int pin=1;pin<5;pin++)
    pinMode(pin, OUTPUT); 
      
  Serial.begin(9600);

  Ethernet.begin(mac, ip);

  webserver.setDefaultCommand(&lightCmd);

  webserver.begin();
}

void loop()
{
  webserver.processConnection();

  for (int j=1;j<5;j++) {
    
    // gradually fade values
    if (lights[j] > lightsNow[j])
      lightsNow[j]++;
    else if (lights[j] < lightsNow[j])
      lightsNow[j]--;
       
    //
    analogWrite(lightPins[j], lightsNow[j]);              
  }     
  delay(2);
}
