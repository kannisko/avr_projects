#include <usart0.hpp>
#include <usart_base.hpp>
#include <pin.hpp>
#include <portb.hpp>
#include <timer1.hpp>
using namespace avrlib;


extern void initPWM();
extern void initADC();
extern void startADC();

#ifndef cbi
#define cbi(sfr, bit) (_SFR_BYTE(sfr) &= ~_BV(bit))
#endif
#ifndef sbi
#define sbi(sfr, bit) (_SFR_BYTE(sfr) |= _BV(bit))
#endif

extern uint8_t freqDivisor;
typedef pin<portb,5> pwmOut;

//#define ADCPIN		0
//#define ADCBUFFERSIZE 1280
//uint8_t ADCBuffer[ADCBUFFERSIZE];
//uint16_t ADCCounter;
//uint16_t stopIndex;
//bool freeze;

//uint8_t triggerLevel = 120;
//bool triggerSlopeRising = true;
//volatile bool triggered;
//volatile bool valid_prev;
//volatile uint8_t prevValue;
//
//
//void startADC( void );
//void initADC(void);
//void initPWM(void);
//void sendByte(uint8_t val);
//void sendBuffer(uint8_t* buffer, int length);
//
//
//
//uint8_t seIdx,seStopIdx;
//char seBuffer[32];
//
//char rdBuffer[32];
//uint8_t rdIdx;
