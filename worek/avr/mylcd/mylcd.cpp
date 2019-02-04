#include <util/delay.h>               
			
#include <avrlib/pin.hpp>
#include <avrlib/portb.hpp>
#include <avrlib/porte.hpp>
#include <avrlib/portg.hpp>
using namespace avrlib;

#include "hd44780.hpp"
#include "hd44780.h"

#define LED2 7


typedef pin<portb,LED2> ledNaPlytce;

typedef pin<portb,6> LcdRS;
typedef pin<portb,5> LcdEnable;
typedef pin<porte,3> LcdDB4;
typedef pin<portg,5> LcdDB5;
typedef pin<porte,5> LcdDB6;
typedef pin<porte,4> LcdDB7;

typedef HD44780<LcdRS,LcdEnable,
	LcdDB4,LcdDB5,LcdDB6,LcdDB7> lcd;

#define onOff(on,off) \
		ledNaPlytce::set(true);\
		_delay_ms(on);\
		ledNaPlytce::set(false);\
		_delay_ms(off);


#define BSIZE 1280
int cnt1=5;
int cnt2=6; 
int main(){

cnt1 = (cnt1+1) % BSIZE;
if( cnt2++ >= BSIZE ) {
cnt2=0;
}

	bool state = true;
	ledNaPlytce::output(true);
	
//LCD_Initalize();	
	lcd::initialize();

	
	lcd::putc('a');
	while(true){
onOff(2000,2000);
onOff(2000,2000);
onOff(500,500);
onOff(500,500);
onOff(500,500);
onOff(2000,2000);
onOff(2000,2000);
	}
	return 0;
}

