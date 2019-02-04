#ifndef FASTAVRLIB_FASTPIN_HPP
#define FASTAVRLIB_FASTPIN_HPP

namespace avrlib {

template <typename Port, int Pin>
struct fast_pin
{
	static void set(bool value) {
		if (value) {
			set();
			}else{
			clear();
		}
	}

	inline static void set() {
		Port::port(Port::port() | (1<<Pin));
	}

	inline static void clear() {
		Port::port(Port::port() & ~(1<<Pin));
	}


	inline static bool get() { return (Port::pin() & (1<<Pin)) != 0; }


	inline static void output(bool value) {
		setAsOutput(value);
	}
	
	inline static void output() {
		setAsOutput();
	}
	
	static void setAsOutput(bool value) {
		if (value){
			setAsOutput();
		}else{
			setAsInput();
		}
	}
	
	inline static void setAsOutput()	{
		Port::dir(Port::dir() | (1<<Pin));
	}
	
	inline static void setAsInput(){
		Port::dir(Port::dir() & ~(1<<Pin));
	}


	inline static bool isOutput() { return (Port::dir() & (1<<Pin)) != 0; }

	inline static void make_input() { setAsInput(); clear(); }
	
	inline static void make_low() { clear(); setAsOutput(); }
	inline static void make_high() { set(); setAsOutput(); }

};

}
#endif
