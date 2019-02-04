#include <avr/io.h>
#include <inttypes.h>
#include <avr/pgmspace.h>


#include "lcd2x20.h"
#include "hardware.h"

// Makra upraszczaj¶ce dost‡p do port°w
// *** Port
//#define PORT(x) XPORT(x)
//#define XPORT(x) (PORT##x)
// *** Pin
//#define PIN(x) XPIN(x)
//#define XPIN(x) (PIN##x)
// *** DDR
//#define DDR(x) XDDR(x)
//#define XDDR(x) (DDR##x)


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



typedef struct {
   unsigned char   b0:1;
   unsigned char   b1:1;
   unsigned char   b2:1;
   unsigned char   b3:1;
   unsigned char   b4:1;
   unsigned char   b5:1;
   unsigned char   b6:1;
   unsigned char   b7:1;
} bit_field;


typedef struct {
   unsigned char   data:4;
   unsigned char   nm:4;
   
} bit_field40;

typedef struct {
   unsigned char   nm:4;
   unsigned char   data:4;
} bit_field44;


typedef union {
   unsigned char   byte;
   bit_field      bit;
   bit_field40	bit40;
   bit_field44	bit44;

} io_reg;


/*
#define      ioPORTB    ((volatile io_reg*)&PORTB)->byte
#define      ioPORTB0   ((volatile io_reg*)&PORTB)->bit.b0
#define      ioPORTB1   ((volatile io_reg*)&PORTB)->bit.b1
#define      ioPORTB2   ((volatile io_reg*)&PORTB)->bit.b2
#define      ioPORTB3   ((volatile io_reg*)&PORTB)->bit.b3
#define      ioPORTB4   ((volatile io_reg*)&PORTB)->bit.b4
#define      ioPORTB5   ((volatile io_reg*)&PORTB)->bit.b5
#define      ioPORTB6   ((volatile io_reg*)&PORTB)->bit.b6
#define      ioPORTB7   ((volatile io_reg*)&PORTB)->bit.b7 


#define      ioPINB    ((volatile io_reg*)&PINB)->byte
#define      ioPINB0   ((volatile io_reg*)&PINB)->bit.b0
#define      ioPINB1   ((volatile io_reg*)&PINB)->bit.b1
#define      ioPINB2   ((volatile io_reg*)&PINB)->bit.b2
#define      ioPINB3   ((volatile io_reg*)&PINB)->bit.b3
#define      ioPINB4   ((volatile io_reg*)&PINB)->bit.b4
#define      ioPINB5   ((volatile io_reg*)&PINB)->bit.b5
#define      ioPINB6   ((volatile io_reg*)&PINB)->bit.b6
#define      ioPINB7   ((volatile io_reg*)&PINB)->bit.b7 

#define      ioDDRB    ((volatile io_reg*)&DDRB)->byte
#define      ioDDRB0   ((volatile io_reg*)&DDRB)->bit.b0
#define      ioDDRB1   ((volatile io_reg*)&DDRB)->bit.b1
#define      ioDDRB2   ((volatile io_reg*)&DDRB)->bit.b2
#define      ioDDRB3   ((volatile io_reg*)&DDRB)->bit.b3
#define      ioDDRB4   ((volatile io_reg*)&DDRB)->bit.b4
#define      ioDDRB5   ((volatile io_reg*)&DDRB)->bit.b5
#define      ioDDRB6   ((volatile io_reg*)&DDRB)->bit.b6
#define      ioDDRB7   ((volatile io_reg*)&DDRB)->bit.b7 

*/
#define _ioPORT(x,y) __ioPORT(x,y)
#define __ioPORT(x,y)  ((volatile io_reg*)&PORT##x)->bit.b##y


#define _ioPORT4(x,y) __ioPORT4(x,y)
#define __ioPORT4(x,y)  ((volatile io_reg*)&PORT##x)->bit4##y.data

#define _ioDDR4(x,y) __ioDDR4(x,y)
#define __ioDDR4(x,y)  ((volatile io_reg*)&DDR##x)->bit4##y.data


#define _ioPIN(x,y) __ioPIN(x,y)
#define __ioPIN(x,y) ((volatile io_reg*)&PIN##x)->bit.b##y

#define _ioDDR(x,y) __ioDDR(x,y)
#define __ioDDR(x,y) ((volatile io_reg*)&DDR##x)->bit.b##y


#define sigE _ioPORT(LCD_EPORT,LCD_E)
#define DDRsigE _ioDDR(LCD_EPORT,LCD_E)

#define sigRS _ioPORT(LCD_RSPORT,LCD_RS)
#define DDRsigRS _ioDDR(LCD_RSPORT,LCD_RS)

#define sigRW _ioPORT(LCD_RWPORT,LCD_RW)
#define DDRsigRW _ioDDR(LCD_RWPORT,LCD_RW)


#define sigDA	_ioPORT4(LCD_DPORT,LCD_D4)
#define DDRsigDA _ioDDR4(LCD_DPORT,LCD_D4)


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




void lcd2x20::init()
{
//ustaw odp. portz jajko wzjciowe
	DDRsigE = 1;
	DDRsigRS = 1;
	DDRsigRW = 1;
	DDRsigDA = 0x0F;

//ustaw wartosci portow
	sigE = 0;
	sigRW = 0;
	sigRS = 0;
	sigDA = 0x0a;


	delay100us8(150);
	delay100us8(0);
	delay100us8(0);
	delay100us8(0);


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

}



#define LCD_EPULSE \
	sigE =1; \
	delay250ns(); \
	sigE = 0; 

void lcd2x20::sendHalf(uint8_t data)
{
	sigDA = data;
	LCD_EPULSE
}

void lcd2x20::send(uint8_t data)
{
	// Starsza cz‡ùä
	sendHalf(data>>4);
	// M-odsza cz‡ùä
	sendHalf(data);
	delayus8(120);
	delayus8(120);
}

// Funkcje interfejsu
void lcd2x20::command(uint8_t command)
{
	sigRS = 0;
	send(command);
}

void lcd2x20::cls()
{
	command(LCDC_CLS);
	delay100us8(48);
}

void lcd2x20::putc(char c )
{
	sigRS = 1;
	send(c);
}

void lcd2x20::puts(const char *s )
{
	char c;
	while( (c=*s++) != 0  )
	{
		putc(c);
	}
}


#ifdef HUJ


//--------------------------------------------------
// Generowanie op°ËnieΩ
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
		delayus8(100); 
		delayus8(100); 
		--t; 
	}
}
// Koniec op°ËnieΩ
//--------------------------------------------------

//--------------------------------------------------
// Obs-uga wyùwietlacza
// Funkcje niskiego poziomu
#define LCD_EPULSE() \
	{PORT(LCD_EPORT) |= 1<<LCD_E; \
	delay250ns(); \
	PORT(LCD_EPORT) &= ~(1<<LCD_E);}

void LCDsendHalf(uint8_t data)
{
	data = (data & 0x0F) << LCD_D4; 
	//data |= 0xF0;
	//data &= 0x0F;
	//data <<= LCD_D4; 
	PORT(LCD_DPORT) = 
		(PORT(LCD_DPORT) & ~(0x0F<<LCD_D4)) | data;
	//PORT(LCD_DPORT) &= ~(0x0F<<LCD_D4); 
	//PORT(LCD_DPORT) |= data; 
	LCD_EPULSE();
}

void LCDsend(uint8_t data)
{
	// Starsza cz‡ùä
	LCDsendHalf(data>>4);
	// M-odsza cz‡ùä
	LCDsendHalf(data);
	delayus8(120);
	delayus8(120);
}

// Funkcje interfejsu
void LCDcommand(uint8_t command)
{
	PORT(LCD_RSPORT) &= ~(1<<LCD_RS);
	LCDsend(command);
}

void LCDdata(uint8_t data)
{
	PORT(LCD_RSPORT) |= 1<<LCD_RS;
	LCDsend(data);
}

void LCDcls(void)
{
	LCDcommand(LCDC_CLS);
	delay100us8(48);
}

void LCDhome(void)
{
	LCDcommand(LCDC_HOME);
	delay100us8(48);
}

void LCDinit(void)
{
	delay100us8(150);
	PORT(LCD_RSPORT) &= ~(1<<LCD_RS);
	LCDsendHalf(LCDC_FUNC|LCDC_FUNC8b); 
	delay100us8(41);
	LCDsendHalf(LCDC_FUNC|LCDC_FUNC8b);
	delay100us8(2);
	LCDsendHalf(LCDC_FUNC|LCDC_FUNC4b);
	delay100us8(2);
	// Teraz jest ju¨ 4b. Koniec korzystania z sendHalf
	LCDcommand(LCDC_FUNC|LCDC_FUNC4b|LCDC_FUNC2L|LCDC_FUNC5x7);
	LCDcommand(LCDC_ON);
	LCDcls();
	LCDcommand(LCDC_MODE|LCDC_MODER);
	LCDcommand(LCDC_ON|LCDC_ONDISPLAY|LCDC_ONCURSOR);
}

void LCDstr_P(prog_char* str)
{
	char znak;
	while( 0 != (znak = pgm_read_byte(str++)) )
		LCDdata(znak);
}
void LCDstr(char* str)
{
	char znak;
	while( 0 != (znak = *str++) )
		LCDdata(znak);
}

// Koniec obs-ugi wyùwietlacza
//--------------------------------------------------
//prog_char g_napispgm[] = "Lancuch z FLASHa"; // globalna

// START
int main(void)
{
	//----------------------------------------------
	// Inicjacja - uwaga: uproszczona, mo¨e wymagaä 
	// zmian przy zmianie przyporz¶dkowania port°w
	
	
	
	LCDinit();
	// Koniec inicjacji
	//----------------------------------------------
	
	// \/ Bez tego, czasami zerowanie z poziomu programatora
	// \/ powoduje ùmieci na wyùwietlaczu. 
	LCDcls(); 

	LCDstr(/*(prog_char*)*/("hujh!"));
//	LCDcommand(LCDC_DDA|64); 
//	LCDstr_P((prog_char*)PSTR("Lancuch z FLASHa"));
	//LCDstr_P(g_napispgm);
	DDR(LCD_RSPORT) &= ~(1<<LCD_E | 1<<LCD_RW);
	return 0;
}
#endif
