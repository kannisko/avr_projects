//-----------------------------------------------------------------------------
// Girino.ino
//-----------------------------------------------------------------------------
// Copyright 2012 Cristiano Lino Fontana
//
// This file is part of Girino.
//
//	Girino is free software: you can redistribute it and/or modify
//	it under the terms of the GNU General Public License as published by
//	the Free Software Foundation, either version 3 of the License, or
//	(at your option) any later version.
//
//	Girino is distributed in the hope that it will be useful,
//	but WITHOUT ANY WARRANTY; without even the implied warranty of
//	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//	GNU General Public License for more details.
//
//	You should have received a copy of the GNU General Public License
//	along with Girino.  If not, see <http://www.gnu.org/licenses/>.
//
//-----------------------------------------------------------------------------
// Includes
//-----------------------------------------------------------------------------

#include "Girino.h"
//#include <TimerOne.h>

//-----------------------------------------------------------------------------
// Global Constants
//-----------------------------------------------------------------------------

//-----------------------------------------------------------------------------
// Global Variables
//-----------------------------------------------------------------------------

         uint16_t waitDuration;
volatile uint16_t stopIndex;
volatile uint16_t ADCCounter;
volatile  uint8_t ADCBuffer[ADCBUFFERSIZE];
volatile  boolean freeze;

          uint8_t prescaler;
          uint8_t triggerEvent;
          uint8_t threshold;

             char commandBuffer[COMBUFFERSIZE+1];

unsigned long pwmP=20000; //Periodo 20000us=20ms=0.02s => 50Hz
byte pwmPon=25; // % do pwmP em HIGH

//-----------------------------------------------------------------------------
// Main routines
//-----------------------------------------------------------------------------
//
// The setup function initializes registers.
//
void setup (void) {		// Setup of the microcontroller
	// Open serial port with a baud rate of BAUDRATE b/s
	Serial.begin(BAUDRATE);

  //pinMode(9,OUTPUT);
  //vou inicializar com 10Hz (100ms=100000us)
  //Timer1.initialize(pwmP); //100000us=100ms=>10Hz
  //Timer1.pwm(9,map(pwmPon,0,100,0,1023)); //pwm no pino9 com 25% duty cycle

	dshow("# setup()");
	// Clear buffers
	memset( (void *)ADCBuffer, 0, sizeof(ADCBuffer) );
	memset( (void *)commandBuffer, 0, sizeof(commandBuffer) );
	ADCCounter = 0;
	waitDuration = ADCBUFFERSIZE - 32;
	stopIndex = -1;
	freeze = false;

	prescaler = 128;
	triggerEvent = 3;

	threshold = defThreshold;

	// Activate interrupts
	sei();

	initPins();
	initADC();
	initAnalogComparator();

	Serial.println("Girino ready");
	//printStatus();
}

void loop (void) {
	dprint(ADCCounter);
	dprint(stopIndex);
	dprint(freeze);
	#if DEBUG == 1
	Serial.println( ADCSRA, BIN );
	Serial.println( ADCSRB, BIN );
	#endif

	// If freeze flag is set, then it is time to send the buffer to the serial port
	if ( freeze )
	{
		dshow("# Frozen");

		// Send buffer through serial port in the right order
		//Serial.print("Buffer: ");
		//Serial.write( ADCBuffer, ADCBUFFERSIZE );
		//Serial.print("End of Buffer");
		Serial.write( (uint8_t *)ADCBuffer + ADCCounter, ADCBUFFERSIZE - ADCCounter );
		Serial.write( (uint8_t *)ADCBuffer, ADCCounter );

		// Turn off errorPin
		//digitalWrite( errorPin, LOW );
		cbi(PORTB,PORTB5);

		stopIndex = ADCBUFFERSIZE + 1;
		freeze = false;

		// Clear buffer
		//memset( (void *)ADCBuffer, 0, sizeof(ADCBuffer) );

		// Let the ADC fill the buffer a little bit
		// [Note] uncomment the following for continuous coversions
		//startADC();
		//delay(1);
		//startAnalogComparator();

		#if DEBUG == 1
		delay(3000);
		#endif
	}

	if ( Serial.available() > 0 ) {
		// Read the incoming byte
		char theChar = Serial.read();
			// Parse character
		switch (theChar) {
			case 's':			// 's' for starting ADC conversions
				//Serial.println("ADC conversions started");

				// Clear buffer
				memset( (void *)ADCBuffer, 0, sizeof(ADCBuffer) );

				startADC();
				// Let the ADC fill the buffer a little bit
				//delay(1);
				startAnalogComparator();
				break;
			case 'S':			// 'S' for stopping ADC conversions
				//Serial.println("ADC conversions stopped");
				stopAnalogComparator();
				stopADC();
				break;
			case 'p':			// 'p' for new prescaler setting
			case 'P': {
				// Wait for COMMANDDELAY ms to be sure that the Serial buffer is filled
				delay(COMMANDDELAY);

				fillBuffer( commandBuffer, COMBUFFERSIZE );

				// Convert buffer to integer
				uint8_t newP = atoi( commandBuffer );

				// Display moving status indicator
				Serial.print("Setting prescaler to: ");
				Serial.println(newP);

				prescaler = newP;
				setADCPrescaler(newP);
				}
				break;

			case 'r':			// 'r' for new voltage reference setting
			case 'R': {
				// Wait for COMMANDDELAY ms to be sure that the Serial buffer is filled
				delay(COMMANDDELAY * 2);

				fillBuffer( commandBuffer, COMBUFFERSIZE );

				// Convert buffer to integer
				uint16_t newR = atoi( commandBuffer );

				// Display moving status indicator
				Serial.print("Setting voltage reference to: ");
				Serial.println(newR);

				setVoltageReference(newR);
				}
				break;

			case 'e':			// 'e' for new trigger event setting
			case 'E': {
				// Wait for COMMANDDELAY ms to be sure that the Serial buffer is filled
				delay(COMMANDDELAY);

				fillBuffer( commandBuffer, COMBUFFERSIZE );

				// Convert buffer to integer
				uint8_t newE = atoi( commandBuffer );

				// Display moving status indicator
				Serial.print("Setting trigger event to: ");
				Serial.println(newE);

				triggerEvent = newE;
				setTriggerEvent(newE);
				}
				break;

			case 'w':			// 'w' for new wait setting
			case 'W': {
				// Wait for COMMANDDELAY ms to be sure that the Serial buffer is filled
				delay(COMMANDDELAY*2);

				fillBuffer( commandBuffer, COMBUFFERSIZE );

				// Convert buffer to integer
				uint16_t newW = atoi( commandBuffer );

				// Display moving status indicator
				Serial.print("Setting waitDuration to: ");
				Serial.println(newW);

				waitDuration = newW;
				}
				break;

			case 't':			// 'w' for new threshold setting
			case 'T': {
				// Wait for COMMANDDELAY ms to be sure that the Serial buffer is filled
				delay(COMMANDDELAY*2);

				fillBuffer( commandBuffer, COMBUFFERSIZE );

				// Convert buffer to integer
				uint16_t newT = atoi( commandBuffer );

				// Display moving status indicator
				Serial.print("Setting threshold to: ");
				Serial.println(newT);

				threshold = newT;
				analogWrite( thresholdPin, threshold );
				}
				break;

			case 'd':			// 'd' for display status
			case 'D':
				printStatus();
				break;

			default:
				// Display error message
				Serial.print("ERROR: Command not found, it was: ");
				Serial.println(theChar);
				error();
		}
	}
}
