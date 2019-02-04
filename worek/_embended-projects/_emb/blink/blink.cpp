   /* Programik do testowania œrodowiska WinAVR
 uk³ad ATmega 
 PB0,PB1 - diody LED (PBx->R->LED->GND); 
 PD0 - przycisk (PD0->Button->GND)  */


#include <avr/io.h>
#include <util/delay.h>               

int main(void)
{
    DDRB  |= _BV(0)|_BV(1);
    PORTB |=  _BV(0);
    PORTB &= ~_BV(1);
    DDRD  &= ~_BV(0);
    PORTD |=  _BV(0);

    while (1) 
    {
        PORTB ^=_BV(0);
        PORTB ^=_BV(1);
        _delay_ms((PIND & _BV(0))? 1000: 200);
    }
}