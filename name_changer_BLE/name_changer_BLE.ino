/*********************************************************************
// UW EEP 523. SPRING 2020
//Mobile Applications for Sensing and control
/*********************************************************************


/*********************************************************************
Use this script to change the name of your BLE module
Make sure to use this SAME name in  your Android App 
*********************************************************************/

#include <Arduino.h>
#include <SPI.h>
#include "Adafruit_BLE.h"
#include "Adafruit_BluefruitLE_SPI.h"
#include "Adafruit_BluefruitLE_UART.h"

#include "BluefruitConfig.h"

#if SOFTWARE_SERIAL_AVAILABLE
 #include <SoftwareSerial.h>
#endif

#define FACTORYRESET_ENABLE      0

#define NEW_DEVICE_NAME "bluet" //THIS IS THE NAME YOU WANT TO GIVE TO YOUR BLE MODULE
#define CMD "AT+GAPDEVNAME="
#define CHANGE_CMD CMD NEW_DEVICE_NAME
boolean changed = false;

Adafruit_BluefruitLE_UART ble(BLUEFRUIT_HWSERIAL_NAME, BLUEFRUIT_UART_MODE_PIN);


// A small helper
void error(const __FlashStringHelper*err) {
  Serial.println(err);
  while (1);
}

/**************************************************************************/
/*!
    Sets up the HW an the BLE module 
*/
/**************************************************************************/
void setup(void)
{
  while (!Serial);  // required for Flora & Micro
  delay(500);

  Serial.begin(115200);
  Serial.println(F("Adafruit Bluefruit Name Changer"));
  Serial.println(F("-------------------------------------"));

  /* Initialise the module */
  Serial.print(F("Initialising the Bluefruit LE module: "));

  if ( !ble.begin(VERBOSE_MODE) )
  {
    error(F("Couldn't find Bluefruit, make sure it's in data mode & check wiring?"));
  }
  Serial.println( F("OK!") );

  if ( FACTORYRESET_ENABLE )
  {
    /* Perform a factory reset to make sure everything is in a known state */
    Serial.println(F("Performing a factory reset: "));
    if ( ! ble.factoryReset() ){
      error(F("Couldn't factory reset"));
    }
  }

  /* Disable command echo from Bluefruit */
  ble.echo(false);

  Serial.println("Requesting Bluefruit info:");
  /* Print Bluefruit information */
  ble.info(); 

  
 Serial.println("CURRENT NAME");
  ble.println("AT+GAPDEVNAME");
   // Check response status
  ble.waitForOK();

  Serial.println("NEW NAME");
  ble.println(CHANGE_CMD);
  
  // Check response status
  ble.waitForOK();
  changed = true;

  // Check response status
  ble.waitForOK();

 }

/**************************************************************************/
/*!
  Constantly poll for new command or response data
*/
/**************************************************************************/
void loop(void)
{
  if (changed == true) {
    changed = false;
     // ble.println("AT+GAPDEVNAME");
    Serial.print("Name changed correctly!");

  }
  

}
