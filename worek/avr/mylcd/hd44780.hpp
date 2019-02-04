
// Komendy steruj¶ce wyùwietlaczem
#define LCDC_CLS		0x01
#define LCDC_HOME		0x02
#define LCDC_MODE		0x04
	#define LCDC_MODER		0x02
	#define LCDC_MODEL		0
	#define LCDC_MODEMOVE	0x01
#define LCDC_ON 		0x08
	#define LCDC_ONDISPLAY	0x04
	#define LCDC_ONCURSOR	0x02
	#define LCDC_ONBLINK	0x01
#define LCDC_SHIFT		0x10
	#define LCDC_SHIFTDISP	0x08
	#define LCDC_SHIFTR		0x04
	#define LCDC_SHIFTL		0
#define LCDC_FUNC		0x20
	#define LCDC_FUNC8b		0x10
	#define LCDC_FUNC4b		0
	#define LCDC_FUNC2L		0x08
	#define LCDC_FUNC1L		0
	#define LCDC_FUNC5x10	0x4
	#define LCDC_FUNC5x7	0
#define LCDC_CGA		0x40
#define LCDC_DDA		0x80



#define HD44780_CLEAR					0x01

#define HD44780_HOME					0x02

#define HD44780_ENTRY_MODE				0x04
	#define HD44780_EM_SHIFT_CURSOR		0
	#define HD44780_EM_SHIFT_DISPLAY	1
	#define HD44780_EM_DECREMENT		0
	#define HD44780_EM_INCREMENT		2

#define HD44780_DISPLAY_ONOFF			0x08
	#define HD44780_DISPLAY_OFF			0
	#define HD44780_DISPLAY_ON			4
	#define HD44780_CURSOR_OFF			0
	#define HD44780_CURSOR_ON			2
	#define HD44780_CURSOR_NOBLINK		0
	#define HD44780_CURSOR_BLINK		1

#define HD44780_DISPLAY_CURSOR_SHIFT	0x10
	#define HD44780_SHIFT_CURSOR		0
	#define HD44780_SHIFT_DISPLAY		8
	#define HD44780_SHIFT_LEFT			0
	#define HD44780_SHIFT_RIGHT			4

#define HD44780_FUNCTION_SET			0x20
	#define HD44780_FONT5x7				0
	#define HD44780_FONT5x10			4
	#define HD44780_ONE_LINE			0
	#define HD44780_TWO_LINE			8
	#define HD44780_4_BIT				0
	#define HD44780_8_BIT				16

#define HD44780_CGRAM_SET				0x40

#define HD44780_DDRAM_SET				0x80



#define delay250ns() {asm volatile("nop"::);}

#define delayus8(t)\
	{asm volatile( \
		"delayus8_loop%=: \n\t"\
			"nop \n\t"\
			"dec %[ticks] \n\t"\
			"brne delayus8_loop%= \n\t"\
	: :[ticks]"r"(t) );}
	// DEC - 1 cykl, BRNE 2 cykle, + 1xnop. Zegar 4MHz

void delay100us8(uint8_t t)
{
	while(t>0)
	{
//		delayus8(100); 
		delayus8(100); 
		--t; 
	}
}
	
	
template <typename RS, typename Enable, typename DB4, typename DB5, typename DB6, typename DB7> class HD44780{
public:

static void initialize(){
	//ustaw odpowednie we/wy
	RS::output(true);
	Enable::output(true);
	DB4::output(true);
	DB5::output(true);
	DB6::output(true);
	DB7::output(true);
	
_delay_ms(15); // oczekiwanie na ustalibizowanie siÍ napiecia zasilajacego
	
	RS::set(false);
	Enable::set(false);
	
	
	
	sendHalf(0x03);	
	_delay_ms(5); // czekaj 5ms

	sendHalf(0x03);	
	_delay_ms(5); // czekaj 5ms

	sendHalf(0x03);	
	_delay_ms(5); // czekaj 5ms

	sendHalf(0x02);	
	_delay_ms(1); // czekaj 5ms


command(HD44780_FUNCTION_SET | HD44780_FONT5x7 | HD44780_TWO_LINE | HD44780_4_BIT); // interfejs 4-bity, 2-linie, znak 5x7
command(HD44780_DISPLAY_ONOFF | HD44780_DISPLAY_OFF); // wy≥πczenie wyswietlacza
command(HD44780_CLEAR); // czyszczenie zawartosÊi pamieci DDRAM
_delay_ms(2);
command(HD44780_ENTRY_MODE | HD44780_EM_SHIFT_CURSOR | HD44780_EM_INCREMENT);// inkrementaja adresu i przesuwanie kursora
command(HD44780_DISPLAY_ONOFF | HD44780_DISPLAY_ON | HD44780_CURSOR_ON | HD44780_CURSOR_BLINK); // w≥πcz LCD, bez kursora i mrugania	
	
	
/*	

	sendHalf(LCDC_FUNC|LCDC_FUNC8b); 
	delay100us8(41);

	sendHalf(LCDC_FUNC|LCDC_FUNC8b);
	delay100us8(2);

	sendHalf(LCDC_FUNC|LCDC_FUNC4b);
	delay100us8(2);

	// Teraz jest ju¨ 4b. Koniec korzystania z sendHalf
	command(LCDC_FUNC|LCDC_FUNC4b|LCDC_FUNC2L|LCDC_FUNC5x7);
	command(LCDC_ON);
	cls();
	command(LCDC_MODE|LCDC_MODER);
	command(LCDC_ON|LCDC_ONDISPLAY|LCDC_ONCURSOR);
*/	
}



static void sendHalf(uint8_t data)
{
	Enable::set(true);
	DB4::set(data&0x01);
	DB5::set(data&0x02);
	DB6::set(data&0x04);
	DB7::set(data&0x08);
	Enable::set(false); 
}

static void send(uint8_t data)
{
	// Starsza cz‡ùä
	sendHalf(data>>4);
	// M-odsza cz‡ùä
	sendHalf(data);
	_delay_us(50);
}

// Funkcje interfejsu
static void command(uint8_t command)
{
	RS::set(0);
	send(command);
}

static void cls()
{
	command(LCDC_CLS);
	delay100us8(48);
}

static void putc(char c )
{
	RS::set(true);
	send(c);
}

};       