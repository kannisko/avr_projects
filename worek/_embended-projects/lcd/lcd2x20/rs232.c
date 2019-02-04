#include <avr\io.h>
#include "rs232.h"


#define RS_MAKE_UBRR(baud) (F_CPU/(baud*16l)-1)
#define RS_SET_BAUD(baud) \
	{UBRR0H = (uint8_t)(RS_MAKE_UBRR(baud)>>8); \
	 UBRR0L = (uint8_t)RS_MAKE_UBRR(baud); }


void rs232::init(uint16_t br )
{
	br =  RS_MAKE_UBRR(br);
	UBRR0H = (uint8_t)(br>>8); 
	UBRR0L = (uint8_t)br;

	UCSR0C = 1<<URSEL0 | 1<<UCSZ01 | 1<<UCSZ00; 
	UCSR0B = 1<<RXEN0 | 1<<TXEN0; 
	UCSR0A = 0;

}

int rs232::put(char znak)
{
	// Oczekiwanie a¿ bufor nadajnika jest pusty
	while(!(1<<UDRE0 & UCSR0A)) {}
	UDR0 = znak; 
	return 0;
}

int rs232::get(void)
{
	char znak; 
	// Oczekiwanie na pojawienie siê danej
	while(!(1<<RXC0 & UCSR0A)) {}
	znak = UDR0; 
	return znak;
}
