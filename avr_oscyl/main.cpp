#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <avr/interrupt.h>
#include "hardware.hpp"

#define ARDUSCOPE_VERSION "#version 1.0"

#define CMD_ACTION_RESET "#ar"
#define CMD_ACTION_ACQUIRE_DATA "#aa"


#define CMD_GET_VERSION "#gv"
#define CMD_SET_SPEED "#ss"
#define CMD_SET_TRIGGER_TYPE "#stt"
#define CMD_SET_TRIGGER_VALUE "#stv"
#define CMD_SET_TRIGGER_SLOPE "#sts"


//prejścia na !rx_empty
//iddle -> read
//read -> read
//waiting_trigger -> read
//writing -> read

//read

enum State{
	IDLE,
	READING,
	WAITING_FOR_TRIGGER,
	WAITING_FOR_DATA,
	WRITING
};

volatile State currentState;



usart0 serial0;
timer1 _timer1;

bool keepRunning;

uint8_t buffer[1300];
uint8_t rdIdx;
#define MAX_COMMAND_LENGTH 15

uint16_t wrIdx;
uint16_t wrStopIdx;

uint8_t freqDivisor=7;

void seProcedure();
void rdProcedure(char c);
void dispatchMessage();
void send( const char *msg);
void init();
void loop();

//PB5 OC1A/PCINT5 (Output Compare and PWM Output A for Timer/Counter1 or Pin Change Interrupt 5)



int main(){
	while(1){
		init();
		loop();
	}
}


void init(){
	initPWM();
	initADC();
	serial0.open_ubrr(detail::get_ubrr(115200),false);
	keepRunning = true;
	sei();
}


void setStateIdle();
void setStateRead();
void setStateWrite();
void setStateWaitTrigger();
void setStateWaitData();

void setStateIdle(){
	currentState = IDLE;
	// Disable ADC and stop Free Running Conversion Mode
	cbi( ADCSRA, ADEN );
}

void setStateRead(){
	if(currentState != READING){
		currentState = READING;
		// Disable ADC and stop Free Running Conversion Mode
		cbi( ADCSRA, ADEN );
		rdIdx = 0;
	}
}

void setStateWrite(){
	currentState = WRITING;
}
void setStateWaitTrigger(){
	startADC();
}


void wrProcedure(){
	serial0.send(buffer[wrIdx++]);
	if( wrIdx >= sizeof(buffer)){
		wrIdx = 0;
	}
	if( wrIdx == wrStopIdx){
		setStateIdle();
	}
}

void rdProcedure(uint8_t c){
	if( c == '#'){
		//reset # - begin of command
		rdIdx = 0;
	}

	if( rdIdx >= MAX_COMMAND_LENGTH){
		return;
	}
	buffer[rdIdx++] = c;
	if( c == '\n'){
		buffer[rdIdx] = 0;
		dispatchMessage();
	}
}



void send(const char* cmd){
	wrIdx=0;
	wrStopIdx = strlen(cmd);
	memcpy(buffer,cmd,wrStopIdx);
	buffer[wrStopIdx++] = '\n';
	setStateWrite();
}

void dispatchMessage(){
	if( !strcmp((const char*)buffer,CMD_ACTION_RESET) ){
		keepRunning = false;
		return;
	}

	if( !strcmp((const char*)buffer,CMD_GET_VERSION) ){
		send(ARDUSCOPE_VERSION);
		return;
	}

	if( !strcmp((const char*)buffer,CMD_ACTION_ACQUIRE_DATA)){
		setStateWaitTrigger();
		return;
	}

//	if(!strncmp((const char*)buffer,CMD_SET_SPEED,sizeof(CMD_SET_SPEED)-1)){
//		freqDivisor = atoi(rdBuffer+sizeof(CMD_SET_SPEED));
//		char tmp[5];
//		send(itoa(freqDivisor,tmp,10));
//		return;
//	}
//
//	if(!strncmp((const char*)buffer,CMD_SET_TRIGGER_VALUE,sizeof(CMD_SET_TRIGGER_VALUE)-1)){
//		triggerLevel = atoi(rdBuffer+sizeof(CMD_SET_TRIGGER_VALUE));
//		char tmp[5];
//		send(itoa(triggerLevel,tmp,10));
//		return;
//	}
//
//	if(!strncmp((const char*)buffer,CMD_SET_TRIGGER_SLOPE,sizeof(CMD_SET_TRIGGER_SLOPE)-1)){
//		char s = rdBuffer[sizeof(CMD_SET_TRIGGER_SLOPE)];
//		triggerSlopeRising = s =='r';
//		char tmp[2];
//		tmp[0] = triggerSlopeRising ? 'r' : 'f';
//		tmp[1] = 0;
//		send(tmp);
//		return;
//	}
}


void loop(){
	while(keepRunning){
		if( !serial0.rx_empty()){
			setStateRead();
			rdProcedure(serial0.recv());
		}
		if(serial0.tx_empty()&&currentState == WRITING){
			wrProcedure();
		}
	}
}



ISR(ADC_vect)
{
	// When ADCL is read, the ADC Data Register is not updated until ADCH
	// is read. Consequently, if the result is left adjusted and no more
	// than 8-bit precision is required, it is sufficient to read ADCH.
	// Otherwise, ADCL must be read first, then ADCH.
//	dummy1 = ADCL;
	uint8_t dummy = ADCH;
	buffer[wrIdx++] = dummy;
	if( wrIdx >= sizeof(buffer)){
		wrIdx=0;
	}
//	if( triggered ){
//		ADCBuffer[ADCCounter] = dummy;
//
//		if (++ADCCounter >= ADCBUFFERSIZE) ADCCounter = 0;
//
//		if ( stopIndex == ADCCounter )
//		{
//			// Freeze situation
//			// Disable ADC and stop Free Running Conversion Mode
//			cbi( ADCSRA, ADEN );
//			freeze = true;
//		}
//		return;
//	}
//
//	if( valid_prev){
//		if(prevValue == dummy){
//			return;
//		}
//		if( triggerSlopeRising){
//			if(  dummy >= prevValue && prevValue <= triggerLevel){
//				triggered = true;
//				return;
//			}
//		}else{
//			if( dummy <= prevValue && prevValue >= triggerLevel){
//				triggered = true;
//				return;
//			}
//		}
//	}
//	prevValue = dummy;
//	valid_prev = true;
}

