#include <pin.h>
#include <portb.hpp>
using namespace avrlib;
#define LED1 5
#define LED2 0

typedef pin<portb,5> led1;
typedef pin<portb,0> led2;

void setup() {
 led1::output(true);
 led2::output(true);
}

void loop() {

  led1::set();
  led2::set();
  delay(500);
  delay(500);
  led1::clear();
  delay(500);
  led2::clear();
  delay(500);

}
