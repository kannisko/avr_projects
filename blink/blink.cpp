#include <avr/io.h>
#include <util/delay.h>

int main(void)
{
    DDRB  |= 255;



    while (1)
    {
        PORTB = 0;
        _delay_ms(500);
        PORTB = 255;
        _delay_ms(1500);


    }
}
