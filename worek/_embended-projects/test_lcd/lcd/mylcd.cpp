#include <util/delay.h>               
#include <avr/interrupt.h>			

#include <avrlib/portb.hpp>
#include <avrlib/porte.hpp>
#include <avrlib/portg.hpp>
//#include <avrlib/porth.hpp>

namespace avrlib {

	struct porth
	{
		static uint8_t port() { return PORTH; }
		static void port(uint8_t v) { PORTH = v; }

		static uint8_t pin() { return PINH; }
		static void pin(uint8_t v) { PINH = v; }

		static uint8_t dir() { return DDRH; }
		static void dir(uint8_t v) { DDRH = v; }
	};

}


#define USE_FAST_PIN


#ifdef USE_FAST_PIN
#include <fast_avr_lib/fast_pin.hpp>
#else
#define fast_pin pin
#include <avrlib/pin.hpp>
#endif


#include <avrlib/usart0.hpp>
#include <avrlib/usart1.hpp>
#include <avrlib/sync_usart.hpp>
#include <avrlib/format.hpp>

#include <fast_avr_lib/encoder.hpp>


using namespace avrlib;



#include "HD44780.hpp"

#define LED2 7





typedef fast_pin<portb,LED2> ledNaPlytce;
typedef fast_pin<portb,0> encoderA;
typedef fast_pin<portb,1> encoderB;
typedef fast_pin<portb,2> encoderButton;


typedef Encoder<encoderA,encoderB> encoder;

volatile int moved = 0;
volatile int stableMoved;
uint8_t prevEncState;
static uint8_t getEncState();
int prevMoved;
char buffer[10];


uint8_t DecStates[4] = {0x1,0x3,0x0,0x2};
uint8_t IncStates[4] = {0x2,0x0,0x3,0x1};	


typedef fast_pin<portb,6> LcdRS;
typedef fast_pin<portb,5> LcdEnable;
typedef fast_pin<porte,3> LcdDB4;
typedef fast_pin<portg,5> LcdDB5;
typedef fast_pin<porte,5> LcdDB6;
typedef fast_pin<porte,4> LcdDB7;

typedef HD44780<LcdRS,LcdEnable,
	LcdDB4,LcdDB5,LcdDB6,LcdDB7> lcd;

void mitoa(int a, char *buffer);
int counter=0;
int main(){
	ledNaPlytce::output();

	
	encoderA::setAsInput();
	//encoderA::set();
	encoderB::setAsInput();
	//encoderB::set();
	
	encoderButton::setAsInput();
	encoderButton::set();
	ledNaPlytce::set(encoderButton::get());	
	
	  PCICR |= (1 << PCIE0);     // set PCIE0 to enable PCMSK0 scan
	  PCMSK0 = (1 << PCINT0) | (1 << PCINT1) | (1 << PCINT2);   // set PCINT0 to trigger an interrupt on state change

	  sei();                     // turn on interrupts

	prevEncState = getEncState();
	
	lcd::initialize();
	lcd::cursorOff();
	//lcd::puts_p(PSTR("dupa"));
	prevMoved = 32000;
	stableMoved = -1;

	while(true){
		stableMoved = moved;
		if( stableMoved != prevMoved)
		{
			prevMoved = stableMoved;
			itoa(stableMoved,buffer,10);
			
			lcd::gotoXY(0,0);
			lcd::puts((uint8_t*)buffer);
			lcd::puts_p(PSTR("    "));
			lcd::gotoXY(0,1);
			itoa(counter++,buffer,10);
			lcd::puts((uint8_t*)buffer);						
//		_delay_ms(1000);	
		}
		
		
		
	}
	return 0;
}void mitoa(int a, char *buffer){	if( a < 0 ){		*buffer++ = '-';		a = -a;	}	char *begin = buffer;	do{		char c = '0' + a%10;		a /=10;		*buffer++ = c;	}while(a);	char *end = buffer-1;	while( begin < end ){		char c = *begin;		*begin++ = *end;		*end-- = c;	}	*buffer=0;	}
static uint8_t getEncState(){
	uint8_t state;
	if( encoderA::get() ){
		state = 0x01;
	}else{
		state = 0;
	}

	if( encoderB::get() ){
		state |= 0x02;
	}
	return state;
}

ISR (PCINT0_vect)
{
	ledNaPlytce::set(encoderButton::get());

	uint8_t state = getEncState();
	if( state == DecStates[prevEncState]){
		moved--;
		
	}else{
		if( state == IncStates[prevEncState]){
			moved++;
		}
	}
	prevEncState = state;

}
