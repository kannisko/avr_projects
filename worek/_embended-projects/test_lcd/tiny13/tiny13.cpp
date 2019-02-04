#include <util/delay.h>
#include <avrlib/portb.hpp>
#include <fast_avr_lib/fast_pin.hpp>


using namespace avrlib;



typedef fast_pin<portb,3> ledNaPlytce;


int main(){
	bool state = true;
	ledNaPlytce::output();
	

	while(true){
		ledNaPlytce::set(state);
		state = !state;
		_delay_ms(10);

		
	}
	return 0;
}
