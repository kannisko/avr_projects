#include <util/delay.h>               
			
#include <avrlib/pin.hpp>
#include <avrlib/portb.hpp>
#include <avrlib/porte.hpp>
#include <avrlib/portg.hpp>
using namespace avrlib;

#include <HD44780.hpp>
#include <HD44780.h>

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

int main(){
	bool state = true;
	ledNaPlytce::output(true);
	
//LCD_Initalize();	
	lcd::initialize();

	
	lcd::putc('a');
	while(true){
		ledNaPlytce::set(state);
		state = !state;
		_delay_ms(500);
	}
	return 0;
}