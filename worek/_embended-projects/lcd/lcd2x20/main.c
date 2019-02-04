//////////////////////////////////////////////////////////////////////////////////////////////////
#include <avr\io.h>
#include <inttypes.h>
#include <avr\pgmspace.h>
#include <avr\eeprom.h>


#include "lcd2x20.h"
#include "rs232.h"

#define delayus8(t)\
	{asm volatile( \
		"delayus8_loop%=: \n\t"\
			"nop \n\t"\
			"dec %[ticks] \n\t"\
			"brne delayus8_loop%= \n\t"\
	: :[ticks]"r"(t) );}
	// DEC - 1 cykl, BRNE 2 cykle, + 1xnop. Zegar 4MHz

void delay100us8(uint8_t t);
void delayus16(uint16_t del );
void delayus16(uint16_t del )
{
	while( del > 255 )
	{
		delayus8(255);
		del -= 255;
	}
	delayus8(del);
}

#define PRZYCISK_R !( PINE & (1<<0) )

#define DEF_BAUD 9600


char tab[4];
int main()
{
	PORTE = 0xFF;
	DDRE = 0xFF;


	DDRA |= 0x0F;

	rs232::init(9600);

	lcd2x20::init();

	eeprom_read_block(tab, 0,4);
	char c;
	for( c=0; c<4; c++ )
	{
		lcd2x20::putc(tab[c]);
	}

	int k = 1500;
	uint8_t j;
	int8_t delta =10;
//us 1000*20	
	while(1)
	{
		PORTA &= ~0x0F;
		delayus16(500);
		PORTA |= 0x0F;
		delayus16(k);
		delayus16(k);

		PORTA &= ~0x0F;
		delayus16(500);

		PORTA |= 0x0F;
		delayus16(k);
		delayus16(k);

		PORTA &= ~0x0F;
		delayus16(500);

		PORTA |= 0x0F;
		delayus16(18000);
		delayus16(18000);
			k += delta;;
			if( k > 2500 )
			{
				delta = -10;
			}
			else
			{
				if( k < 500 )
				{
					delta = 10;
				}
			}


/*
		PORTA |= 0x0F;
		delayus16(k);
		delayus16(k);
		PORTA &= ~0x0F;
		delayus16(20000-k);
		delayus16(20000-k);
		j++;
		if( j>1 )
		{
			j = 0;
			k += delta;;
			if( k > 2500 )
			{
				delta = -10;
			}
			else
			{
				if( k < 500 )
				{
					delta = 10;
				}
			}
*/

		}


/*
		c = rs232::get();
		rs232::put(c);
		lcd2x20::putc(c);
		if( c == '#' )
		{
			eeprom_write_block(tab, 0,4);
		}
		else
		{
			tab[0] = tab[1];
			tab[1] = tab[2];
			tab[2] = tab[3];
			tab[3] = c;
		}
*/

	
	return(0);

}

