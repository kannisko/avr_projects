

library IEEE;
use IEEE.STD_LOGIC_1164.ALL;
use IEEE.STD_LOGIC_ARITH.ALL;
use IEEE.STD_LOGIC_UNSIGNED.ALL;
--
-- The Unisim Library is used to define Xilinx primitives. It is also used during
-- simulation. The source can be viewed at %XILINX%\vhdl\src\unisims\unisim_VCOMP.vhd
--
library unisim;
use unisim.vcomponents.all;
--
--
-- ----------------------------------------------------
--
entity LcdDriverTopLevel is
    Port (             --LED : out std_logic_vector(7 downto 0);
                    LCD_DB : out  std_logic_vector(7 downto 0);
                    LCD_RS : out std_logic;
                    LCD_RW : out std_logic;
                     LCD_E : out std_logic;
                   CLK_50M : in std_logic);
    end LcdDriverTopLevel;
--
-- ----------------------------------------------------
-- Start of test architecture
--
architecture Behavioral of LcdDriverTopLevel is
--
-- ----------------------------------------------------
-- declaration of KCPSM3
--
  component kcpsm3 
    Port (      address : out std_logic_vector(9 downto 0);
            instruction : in std_logic_vector(17 downto 0);
                port_id : out std_logic_vector(7 downto 0);
           write_strobe : out std_logic;
               out_port : out std_logic_vector(7 downto 0);
            read_strobe : out std_logic;
                in_port : in std_logic_vector(7 downto 0);
              interrupt : in std_logic;
          interrupt_ack : out std_logic;
                  reset : in std_logic;
                    clk : in std_logic);
    end component;
--
-- declaration of program ROM
--
  component lcdDrv
    Port (      address : in std_logic_vector(9 downto 0);
            instruction : out std_logic_vector(17 downto 0);
             proc_reset : out std_logic;
                    clk : in std_logic);
    end component;
--
-- ----------------------------------------------------
--- Signals used to connect KCPSM3 to program ROM and I/O logic
--
signal  address          : std_logic_vector(9 downto 0);
signal  instruction      : std_logic_vector(17 downto 0);
signal  port_id          : std_logic_vector(7 downto 0);
signal  out_port         : std_logic_vector(7 downto 0);
signal  in_port          : std_logic_vector(7 downto 0);
signal  write_strobe     : std_logic;
signal  read_strobe      : std_logic;
signal  interrupt        : std_logic :='0';
signal  interrupt_ack    : std_logic;
signal  kcpsm3_reset     : std_logic;
--
--
-- Signals used to generate interrupt 
--
signal int_count         : integer range 0 to 49999999 :=0;
signal event_1hz         : std_logic;

--
--
-- Signals for LCD operation
--
-- Tri-state output requires internal signals
-- 'lcd_drive' is used to differentiate between LCD and StrataFLASH communications 
-- which share the same data bits.
--
signal    lcd_rw_control : std_logic;
signal   lcd_output_data : std_logic_vector(7 downto 0);
--
-- -------------------------------------------------------------------------------------- ----------------------------------------------------
-- Start of circuit description
--
begin


  --
  -- ---------------------------------------------- --------------------------------------------------  -- KCPSM3 and the program memory 
  -- ---------------------------------------------- --------------------------------------------------  --

  processor: kcpsm3
    port map(      address => address,
               instruction => instruction,
                   port_id => port_id,
              write_strobe => write_strobe,
                  out_port => out_port,
               read_strobe => read_strobe,
                   in_port => in_port,
                 interrupt => interrupt,
             interrupt_ack => interrupt_ack,
                     reset => kcpsm3_reset,
                       clk => CLK_50M);
 
  program_rom: lcdDrv
    port map(      address => address,
               instruction => instruction,
               proc_reset  => kcpsm3_reset,
                       clk => CLK_50M);

  --kcpsm3_reset <= '0';                       
  
  --
  -- ------------------------------------------------ --------------------------------------------------  -- Interrupt 
  -- ------------------------------------------------ --------------------------------------------------  --
  --
  -- Interrupt is used to provide a 1 second time reference.
  --
  --
  -- A simple binary counter is used to divide the 50MHz system clock and provide interrupt pulses.
  --

  interrupt_control: process(CLK_50M)
  begin
    if CLK_50M'event and CLK_50M='1' then

      --divide 50MHz by 50,000,000 to form 1Hz pulses
      if int_count=49999999 then
         int_count <= 0;
         event_1hz <= '1';
       else
         int_count <= int_count + 1;
         event_1hz <= '0';
      end if;

      -- processor interrupt waits for an acknowledgement
      if interrupt_ack='1' then
         interrupt <= '0';
        elsif event_1hz='1' then
         interrupt <= '1';
        else
         interrupt <= interrupt;
      end if;

    end if; 
  end process interrupt_control;

  --
  -- ------------------------------------------------ --------------------------------------------------  -- KCPSM3 input ports 
  -- ------------------------------------------------ --------------------------------------------------  --
  --
  -- The inputs connect via a pipelined multiplexer
  --

  input_ports: process(CLK_50M)
  begin
    if CLK_50M'event and CLK_50M='1' then

		if(port_id(0) = '1') then
			in_port <=  x"ab";
		else
			in_port <= x"0e";  
		end if;

     end if;

  end process input_ports;


  --
  -- ------------------------------------------------ --------------------------------------------------  -- KCPSM3 output ports 
  -- ------------------------------------------------ --------------------------------------------------  --
  -- adding the output registers to the processor
  --
   
  output_ports: process(CLK_50M)
  begin

    if CLK_50M'event and CLK_50M='1' then
      if write_strobe='1' then

        -- 8-bit LCD data output address 40 hex.
        if port_id(6)='1' then
          lcd_output_data <= out_port;
        end if;

        -- LCD controls at address 20 hex.
        if port_id(5)='1' then
          LCD_RS <= out_port(2);
          lcd_rw_control <= out_port(1);
          LCD_E <= out_port(0);
        end if;

      end if;

    end if; 

  end process output_ports;

  --
  -- ------------------------------------------------ --------------------------------------------------  -- LCD interface  
  -- ------------------------------------------------ --------------------------------------------------  --
  -- The LCD will be accessed using the 8-bit mode.  
  -- LCD_RW is '1' for read and '0' for write 
  --
  -- Control of read and write signal
  LCD_RW <= lcd_rw_control;

  -- use read/write control to enable output buffers.
  LCD_DB <= lcd_output_data when lcd_rw_control='0' else "ZZZZZZZZ";

  --
  -- ------------------------------------------------ --------------------------------------------------  --
  --
end Behavioral;

