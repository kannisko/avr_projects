

class lcd2x20
{
protected:
	static void sendHalf(uint8_t data);
	static void send(uint8_t data);
	static void command(uint8_t command);
public:
	static void init();
	static void cls();
	static void puts( const char *);
	static void puts_p(const prog_char *);
	static void putc(char);

};
