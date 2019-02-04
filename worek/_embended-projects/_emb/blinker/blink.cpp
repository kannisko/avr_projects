#include <avr/interrupt.h>
#include <util/delay.h>               
			
#include <avrlib/pin.hpp>
#include <avrlib/portb.hpp>
#include <avrlib/adc.hpp>
using namespace avrlib;

#define LED1 0
#define LED2 5

typedef pin<portb,LED1> led1;
typedef pin<portb,LED2> led2;

sync_adc adc(0,false,right,avcc);



int timeTot = 1000;
volatile int _v_fill = 100;
int fill;

int timeH = 100;
int timeL = 900;

void calcTimes();
int main(void)
{
	led1::output(true);
	led2::output(true);
	led2::set();
	
	ADCSRA |=_BV(ADIE);
	
	adc.start();

    while (1) 
    {
		led1::set();
		_delay_us(timeH);
		led1::set(false);
		_delay_us(timeL);
		
		cli();
		fill = _v_fill;
		sei();
		
		calcTimes();


    }
}

void calcTimes()
{

	timeH = (timeTot*(long)fill)>>10;
	timeL = timeTot-timeH;
}

  ISR(ADC_vect) {
		_v_fill = adc.value();
        adc.start();
    }