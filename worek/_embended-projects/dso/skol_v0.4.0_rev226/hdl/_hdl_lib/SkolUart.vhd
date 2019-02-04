--
-- Copyright (C) [2008] [Schuster Andreas]
-- 
-- This program is free software; you can redistribute it and/or modify it 
-- under the terms of the GNU General Public License as published by the Free 
-- Software Foundation; either version 3 of the License, or (at your option) 
-- any later version.
-- 
-- This program is distributed in the hope that it will be useful, but WITHOUT 
-- ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS 
-- FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
-- 
-- You should have received a copy of the GNU General Public License along with 
-- this program; if not, see <http://www.gnu.org/licenses/>.
-- 
----------------------------------------------------------------------------------
-- Company: 
-- Engineer: 
-- 
-- Create Date:    11:40:24 10/10/2008 
-- Design Name: 
-- Module Name:    SkolUart - Behavioral 
-- Project Name: 
-- Target Devices: 
-- Tool versions: 
-- Description: 
--
-- Dependencies: 
--
-- Revision: 
-- Revision 1.0.0 Released
-- Revision 1.0.1 DataIn ok
--				 2byte dataOut ok
-- Revision 1.0.2 Some Synthesizing Warnings removed
-- Revision 1.0.3 Registered Inputs
-- Revision 1.1.0 new Modes: (to send multiple data in one frame)
--					- send only start bytes
--					- send only data bytes
--					- send only crc byte
-- Revision 1.1.1 Introduced MagicByte
-- Revision 1.1.2 Comments changed
-- Revision 1.1.3 Reset for whole system added
-- Revision 1.1.4 Async Reset added
-- Revision 1.1.5 removed unused signals
-- Revision 1.1.6 removed unused generics
--
--
-- Additional Comments: 
-- If 0xFF is received at OpCode Position - RESET will be performed
----------------------------------------------------------------------------------
library IEEE;
use IEEE.STD_LOGIC_1164.ALL;
use IEEE.STD_LOGIC_ARITH.ALL;
use IEEE.STD_LOGIC_UNSIGNED.ALL;

---- Uncomment the following library declaration if instantiating
---- any Xilinx primitives in this code.
--library UNISIM;
--use UNISIM.VComponents.all;

entity SkolUart is
port (
     CLOCK 			: in  std_logic;  -- clock
     RST_I 			: in  std_logic;  -- Reset input     
     OPCODE_I 		: in  std_logic_vector(7 downto 0);
     DAT0_I 		: in  std_logic_vector(7 downto 0); -- DataIn Bus
	 DAT1_I 		: in  std_logic_vector(7 downto 0); -- DataIn Bus /those two bytes are interpreted as one 16 bit value !
     DAT_O_OpCode 	: out std_logic_vector(7 downto 0); -- DataOut Bus
	 DAT_O_Data 	: out std_logic_vector(7 downto 0); -- DataOut Bus
     WE_ONEBYTE_I  	: in  std_logic;  -- Write Enable
     WE_TWOBYTES_I  : in  std_logic;  -- Write Enable  / those two bytes are interpreted as one 16 bit value ! 
     IntTx_O  		: out std_logic;  -- Transmit interrupt: goes High for one cycle !
     IntRx_O  		: out std_logic;  -- Receive interrupt: indicate Data received
     TxD_PAD_O		: out std_logic;  -- Tx RS232 Line
     RxD_PAD_I		: in  std_logic;  -- Rx RS232 Line       
	 WE_START_FRAME : in  std_logic;
	 WE_DATA_FRAME 	: in  std_logic;
	 WE_CRC_FRAME 	: in  std_logic;
	 DATA_BLOCK_COUNT_HIGH_I 		: in  std_logic_vector(7 downto 0);
	 DATA_BLOCK_COUNT_LOW_I 		: in  std_logic_vector(7 downto 0);
	 DATA_BLOCK_SIZE_I 				: in  std_logic_vector(7 downto 0);
     debugOut : out std_logic_vector(7 downto 0)
     );

end SkolUart;

architecture Behavioral of SkolUart is
--+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
component UART 
  generic(BRDIVISOR: INTEGER range 0 to 65535 := 130); -- Baud rate divisor
  port (
	-- Wishbone signals
     WB_CLK_I : in  std_logic;  -- clock
     WB_RST_I : in  std_logic;  -- Reset input
     WB_ADR_I : in  std_logic_vector(1 downto 0); -- Adress bus          
     WB_DAT_I : in  std_logic_vector(7 downto 0); -- DataIn Bus
     WB_DAT_O : out std_logic_vector(7 downto 0); -- DataOut Bus
     WB_WE_I  : in  std_logic;  -- Write Enable
     WB_STB_I : in  std_logic;  -- Strobe
     WB_ACK_O : out std_logic;	-- Acknowledge
	-- process signals     
     IntTx_O  : out std_logic;  -- Transmit interrupt: indicate waiting for Byte
     IntRx_O  : out std_logic;  -- Receive interrupt: indicate data received
     BR_Clk_I : in  std_logic;  -- Clock used for Transmit/Receive
     TxD_PAD_O: out std_logic;  -- Tx RS232 Line
     RxD_PAD_I: in  std_logic   -- Rx RS232 Line 
     );  
end component;
	
----Signals+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
type state_type is (st_reset,st_init, st_wait, 
					st_readData0 , st_readData1, st_readData2, st_readData3,          
							st_perform_reset); 
					
signal state, next_state : state_type := st_reset; 
   -- uart
	Signal UartWB_RST_I  :  std_logic := '0' ; -- Reset input
	Signal UartWB_ADR_I  :  std_logic_vector(1 downto 0) := (others => '0'); -- Adress bus          
	Signal UartWB_DAT_I  :  std_logic_vector(7 downto 0):= (others => '0'); -- DataIn Bus
	Signal UartWB_DAT_O  :  std_logic_vector(7 downto 0); -- DataOut Bus
	Signal UartWB_WE_I   :  std_logic := '0';  -- Write Enable

	Signal UartWB_ACK_O  :  std_logic;  -- Acknowledge
	Signal UartIntTx_O   :  std_logic;  -- Transmit interrupt: indicate waiting for Byte
	Signal UartIntRx_O   :  std_logic;  -- Receive interrupt: indicate Byte received
	
	Signal counter0Pulse 	: std_logic := '0';
	Signal counter0Reset 	: std_logic := '0';
	Signal counter0			: std_logic_vector(7 downto 0):= (others => '0'); 
	
	Signal opcode_i_sig 	:  std_logic_vector(7 downto 0):= (others => '0'); 
	Signal dat0_i_sig 	 	:  std_logic_vector(7 downto 0):= (others => '0'); 
	Signal dat1_i_sig 	 	:  std_logic_vector(7 downto 0):= (others => '0'); 

	-- sending
	type sendingStateType is(st_reset,st_wait,st_finished,
							st_opcode0,			st_opcode1,			st_opcode2,         st_opcode3,        
							st_flowcount0,		st_flowcount1,		st_flowcount2,      st_flowcount3,     
							st_datablockcount0,	st_datablockcount1,	st_datablockcount2, st_datablockcount3,
							st_datablockcount4,	st_datablockcount5,	st_datablockcount6, st_datablockcount7,
							st_datablocksize0,	st_datablocksize1,	st_datablocksize2,  st_datablocksize3, 
							st_data0_0,			st_data0_1,			st_data0_2,         st_data0_3,        
							st_data1_0,			st_data1_1,			st_data1_2,         st_data1_3,        
							st_crc0,			st_crc1,			st_crc2,            st_crc3);
							
	Signal sendingState, next_sendingState : sendingStateType := st_reset ;
	Signal twoByteMode : std_logic := '0';
	
	
	Signal UartWB_STB_I_recv, UartWB_STB_I_send,UartWB_STB_I_signal : std_logic := '0';

	Signal data_block_count_high_sig 	:  std_logic_vector(7 downto 0):= (others => '0'); 
	Signal data_block_count_low_sig  	:  std_logic_vector(7 downto 0):= (others => '0'); 
	Signal data_block_size_sig     		:  std_logic_vector(7 downto 0):= (others => '0'); 
	Signal we_start_frame_sig 	: std_logic := '0';
	Signal we_data_frame_sig 	: std_logic := '0';


	CONSTANT CONST_magicByte       	: STD_LOGIC_VECTOR(7 downto 0) := x"EF";  
--+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 

--+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
begin
---------------------------------------------------------------------

myUart : UART 
	--  (50 000 000 / (baudrate * 4))
  --generic map (BRDIVISOR => 1302) -- 9600   bps->1302 at 50MHz
  --generic map (BRDIVISOR => 98)   -- 128000 bps->98 at 50MHz
  generic map (BRDIVISOR => 108)   -- 115200 bps->98 at 50MHz
  port map(
	-- Wishbone signals
     WB_CLK_I => CLOCK,  -- clock
     WB_RST_I => UartWB_RST_I,  -- Reset input
     WB_ADR_I => UartWB_ADR_I,-- in  std_logic_vector(1 downto 0); -- Adress bus          
     WB_DAT_I => UartWB_DAT_I,-- : in  std_logic_vector(7 downto 0); -- DataIn Bus
     WB_DAT_O => UartWB_DAT_O,-- : out std_logic_vector(7 downto 0); -- DataOut Bus
     WB_WE_I  => UartWB_WE_I,-- : in  std_logic;  -- Write Enable
     WB_STB_I => UartWB_STB_I_signal,-- : in  std_logic;  -- Strobe
     WB_ACK_O => UartWB_ACK_O,-- : out std_logic;	-- Acknowledge
	-- process signals     
     IntTx_O   => UartIntTx_O,-- : out std_logic;  -- Transmit interrupt: indicate waiting for Byte
     IntRx_O   => UartIntRx_O,-- : out std_logic;  -- Receive interrupt: indicate Byte received
     BR_Clk_I  => CLOCK,-- : in  std_logic;  -- Clock used for Transmit/Receive
     TxD_PAD_O => TxD_PAD_O,-- : out std_logic;  -- Tx RS232 Line
     RxD_PAD_I => RxD_PAD_I-- : in  std_logic);  -- Rx RS232 Line  
	 );
--------------------------------------------------------------------------------------

	counter0Process: process(counter0Pulse,counter0Reset,CLOCK)
	begin
		--if (counter0Pulse'event and counter0Pulse = '1') then
		if (CLOCK'event and CLOCK = '1') then
			if (counter0Reset = '1') then
				counter0 <= x"00";
			--else
			elsif(counter0Pulse = '1') then
				counter0 <= counter0 + x"01";
			end if;   
			debugOut <= counter0;     
		end if;
	end process;
	
	RECEIVER_SYNC_PROC: process (CLOCK,RST_I)
	begin
		if (RST_I = '1') then
			state <= st_init;
			UartWB_RST_I <= '1';
		elsif (CLOCK'event and CLOCK = '1') then
			UartWB_RST_I <= '0';
			state <= next_state;
		end if;
	end process;
 

 
	RECEIVER_NEXT_STATE_DECODE: process (state,UartIntRx_O,UARTIntTx_O,
													counter0, UartWB_DAT_O)-- inputs also fixme sush
	begin
		UartWB_STB_I_recv <= '0';
		UartWB_ADR_I  	<= b"00";
		counter0Reset 	<= '0';
		counter0Pulse 	<= '0';		
		next_state <= state;

		
		case (state) is

			when st_reset =>
				next_state <= st_init;
				counter0Reset <= '1';
				counter0Pulse <= '1';
--				DAT_O_OpCode  	<= x"00";
--				DAT_O_Data 		<= x"00";
			when st_init =>
				next_state <= st_wait;
				counter0Reset <= '1';
			when st_wait => -- read from input / nothing received so far
				if(UartIntRx_O = '1') then
					next_state <= st_readData1;
				else
					next_state <= st_wait;
				end if;
			when st_readData0 => -- read from input / wait for next packet
				if(UartIntRx_O = '1') then
					next_state <= st_readData1;
				else
					next_state <= st_readData0;
				end if;
			when st_readData1 => -- prepare for read data
				UartWB_STB_I_recv <= '1';	
				next_state <= st_readData2;
			when st_readData2 => -- read  data / prepare for write 
				case (counter0) is
					when x"00" =>-- packet number 1 (1 of 7) 
--						DAT_O_OpCode <= UartWB_DAT_O;
						if (UartWB_DAT_O = x"FF") then -- reset request 
							counter0Pulse <= '0';
							counter0Reset <= '1';
							next_state <= st_perform_reset;
						else
							counter0Pulse <= '1';
							next_state <= st_readData0;
						end if;
					when x"05" => -- packet6 
--						DAT_O_Data <= UartWB_DAT_O;
						counter0Pulse <= '1';
						next_state <= st_readData0;
					when x"06" => -- packet7 crc - drop for now
						next_state <= st_readData3;
					when others =>
						counter0Pulse <= '1';
						next_state <= st_readData0;
				end case;  
			when st_readData3 =>
				next_state <= st_wait;
				counter0Reset <= '1'; -- do not reset earlier than here - will cause troubles
			when st_perform_reset =>
				next_state <= st_reset;
			when others =>
				next_state <= st_init;
		end case;  
	end process;
	
RECEIVER_SYNC_OUTPUT_DECODE: process (CLOCK)-- inputs also fixme sush
	begin
		
	if (CLOCK'event and CLOCK = '1') then
		
		case (state) is

			when st_reset =>

			when st_init =>
				null;
			when st_wait => 
				null;
			when st_readData0 => 

				null;
			when st_readData1 => 
				null;
			when st_readData2 => -- read  data / prepare for write 
				case (counter0) is
					when x"00" =>-- packet number 1 (1 of 7) 
						DAT_O_OpCode <= UartWB_DAT_O;
					when x"05" => -- packet6 
						DAT_O_Data <= UartWB_DAT_O;
					when x"06" => -- packet7 crc - drop for now
						null;
					when others =>
						null;
				end case;  
			when st_readData3 =>
				null;
			when st_perform_reset =>
				DAT_O_OpCode 	<= x"FF";
				DAT_O_Data 		<= x"FF";
			when others =>
				null;
		end case;  
	end if;
end process;
	
	
	OUTPUT_INTERRUPT_STATE_DECODE: process (state)
	begin
		if (state = st_readData3 OR state = st_perform_reset) then
			IntRx_O <= '1';
		else 
			IntRx_O <= '0';
		end if;
	end process;


-- -------------------------------------------------------------
	SENDER_SYNC_PROC: process (CLOCK, RST_I)
	begin
		if (RST_I = '1') then
			sendingState <= st_reset;
		elsif(CLOCK'event and CLOCK = '1') then
			sendingState <= next_sendingState;
		end if;
	end process;
 
	
 
	SENDERR_NEXT_STATE_DECODE: process (WE_ONEBYTE_I,WE_TWOBYTES_I ,UartIntTx_O,
											sendingState, twoByteMode,
											WE_START_FRAME, WE_DATA_FRAME, WE_CRC_FRAME, 
											we_start_frame_sig, we_data_frame_sig)
	
	begin
		next_sendingState <= sendingState;
		case (sendingState) is
    
			when st_reset =>
				next_sendingState <= st_wait;
			when st_wait => 
				if(WE_ONEBYTE_I = '1') then
					next_sendingState <= st_opcode0;
				elsif(WE_TWOBYTES_I = '1') then
					next_sendingState <= st_opcode0;
				elsif(WE_START_FRAME= '1') then
					next_sendingState <= st_opcode0;
				elsif(WE_DATA_FRAME = '1') then
					next_sendingState <= st_data0_0;
				elsif(WE_CRC_FRAME = '1') then
					next_sendingState <= st_crc0;
				end if;    
			  
			-- -----------------------		
			when st_opcode0 =>
				next_sendingState <= st_opcode1;
			when st_opcode1 =>
				next_sendingState <= st_opcode2;
			when st_opcode2=>
				next_sendingState <= st_opcode3;
			when st_opcode3 =>
				if(UartIntTx_O  = '1') then
					next_sendingState <= st_flowcount0;
				end if;
			-- -----------------------
			when st_flowcount0=>
				next_sendingState <= st_flowcount1;
			when st_flowcount1=>
				next_sendingState <= st_flowcount2;
			when st_flowcount2=>
				next_sendingState <= st_flowcount3;
			when st_flowcount3 =>
				if(UartIntTx_O  = '1') then
					next_sendingState <= st_datablockcount0;
				end if;
			-- -----------------------
			when st_datablockcount0=>
				next_sendingState <= st_datablockcount1;
			when st_datablockcount1=>
				next_sendingState <= st_datablockcount2;
			when st_datablockcount2=>
				next_sendingState <= st_datablockcount3;
			when st_datablockcount3 =>
				if(UartIntTx_O  = '1') then
					next_sendingState <= st_datablockcount4;
				end if;
			-- -----------------------
			when st_datablockcount4=>
				next_sendingState <= st_datablockcount5;
			when st_datablockcount5=>
				next_sendingState <= st_datablockcount6;
			when st_datablockcount6=>
				next_sendingState <= st_datablockcount7;
			when st_datablockcount7 =>
				if(UartIntTx_O  = '1') then
					next_sendingState <= st_datablocksize0;
				end if;
			-- -----------------------
			when st_datablocksize0=>
				next_sendingState <= st_datablocksize1;
			when st_datablocksize1=>
				next_sendingState <= st_datablocksize2;
			when st_datablocksize2=>
				next_sendingState <= st_datablocksize3;
			when st_datablocksize3 =>
				if(UartIntTx_O  = '1') then
					if(we_start_frame_sig = '1') then
						next_sendingState <= st_finished;
					else
						next_sendingState <= st_data0_0;
					end if;
				end if;
			-- -----------------------
			when st_data0_0=>
				next_sendingState <= st_data0_1;
			when st_data0_1=>
				next_sendingState <= st_data0_2;
			when st_data0_2=>
				next_sendingState <= st_data0_3;
			when st_data0_3=>
				if(UartIntTx_O  = '1') then
					if(twoByteMode = '1' or we_data_frame_sig = '1' ) then
						next_sendingState <= st_data1_0;
					else
						next_sendingState <= st_crc0;
					end if;
				end if;
			-- -----------------------
			when st_data1_0=>
				next_sendingState <= st_data1_1;
			when st_data1_1=>
				next_sendingState <= st_data1_2;
			when st_data1_2=>
				next_sendingState <= st_data1_3;
			when st_data1_3=>
				if(UartIntTx_O  = '1') then
					if(we_data_frame_sig = '1') then
						next_sendingState <= st_finished;
					else
						next_sendingState <= st_crc0;
					end if;
				end if;
			-- -----------------------
			when st_crc0=>
				next_sendingState <= st_crc1;
			when st_crc1=>
				next_sendingState <= st_crc2;
			when st_crc2=>
				next_sendingState <= st_crc3;
			when st_crc3=>
				if(UartIntTx_O  = '1') then
					next_sendingState <= st_finished;
				end if;
			-- -----------------------
			when st_finished => 
				next_sendingState <= st_wait;
				
			when others =>
				next_sendingState <= st_reset;
		end case;  

	end process;

SENDER_SYNC_PROCESS: process (CLOCK)
	
	begin
	if(CLOCK'event and CLOCK = '1') then
		case (sendingState) is

 
			when st_reset =>
				null;    
			when st_wait =>   
				twoByteMode 	   <= '0';
				we_start_frame_sig <= '0';
				we_data_frame_sig  <= '0';


				
				data_block_count_high_sig  	<= DATA_BLOCK_COUNT_HIGH_I;
				data_block_count_low_sig    <= DATA_BLOCK_COUNT_LOW_I;
				data_block_size_sig     	<= DATA_BLOCK_SIZE_I;
				opcode_i_sig     <= OPCODE_I ;                        
				dat0_i_sig 	     <= DAT0_I ;                         
				dat1_i_sig       <= DAT1_I ;
				
				if(WE_ONEBYTE_I = '1') then
					null ;
				elsif(WE_TWOBYTES_I = '1') then
					twoByteMode 	 <= '1';
				elsif(WE_START_FRAME = '1') then
					we_start_frame_sig <= '1';
				elsif(WE_DATA_FRAME = '1') then
					we_data_frame_sig <= '1';
				elsif(WE_CRC_FRAME = '1') then
					null;
				end if; 
			-- -----------------------		
			when st_opcode0 =>
				null;    
			when st_opcode1 =>
				null;    
			when st_opcode2=>
				null;    
			when st_opcode3 =>
				null;    
			-- -----------------------
			when st_flowcount0=>
				null;    
			when st_flowcount1=>
				null;    
			when st_flowcount2=>
				null;    
			when st_flowcount3 =>
				null;    
			-- -----------------------
			when st_datablockcount0=>
				null;    
			when st_datablockcount1=>
				null;    
			when st_datablockcount2=>
				null;    
			when st_datablockcount3 =>
				null;    
			-- -----------------------
			when st_datablockcount4=>
				null;    
			when st_datablockcount5=>
				null;    
			when st_datablockcount6=>
				null;    
			when st_datablockcount7 =>
				null;    
			-- -----------------------
			when st_datablocksize0=>
				null;    
			when st_datablocksize1=>
				null;    
			when st_datablocksize2=>
				null;    
			when st_datablocksize3 =>
				null;    
			-- -----------------------
			when st_data0_0=>
				null;    
			when st_data0_1=>
				null;    
			when st_data0_2=>
				null;    
			when st_data0_3=>
				null;    
			-- -----------------------
			when st_data1_0=>
				null;    
			when st_data1_1=>
				null;    
			when st_data1_2=>
				null;    
			when st_data1_3=>
				null;    
			-- -----------------------
			when st_crc0=>
				null;    
			when st_crc1=>
				null;    
			when st_crc2=>
				null;    
			when st_crc3=>
				null;    
			-- -----------------------
			when st_finished => 
				null;    
				
			when others =>
				null;    
		end case;  
	end if;
	end process;

	SENDERR_OUTPUT_DECODE: process (sendingState )
	begin
	UartWB_STB_I_send <= '0'; 
	UartWB_WE_I  <= '0';
	IntTx_O		<= '0';
	UartWB_ADR_I <= b"00";
	UartWB_DAT_I <= x"00";
	case (sendingState) is

		when st_reset =>
			null;
		when st_wait => 
		-- -----------------------
		when st_opcode0 =>
			UartWB_DAT_I <= opcode_i_sig;
			UartWB_WE_I  <= '1';
		when st_opcode1 =>
			UartWB_DAT_I <= opcode_i_sig;
			UartWB_STB_I_send <= '1'; 
			UartWB_WE_I  <= '1';
		when st_opcode2=>
			null;
		-- -----------------------
		when st_flowcount0=>
			UartWB_DAT_I <= CONST_magicByte;
			UartWB_WE_I  <= '1';
		when st_flowcount1=>
			UartWB_DAT_I <= CONST_magicByte;
			UartWB_STB_I_send <= '1'; 
			UartWB_WE_I  <= '1';
		when st_flowcount2=>
			null;
		-- -----------------------
		when st_datablockcount0=>
			if(we_start_frame_sig = '1' ) then
				UartWB_DAT_I <= data_block_count_high_sig;
			else
				UartWB_DAT_I <= x"00";
			end if;
			UartWB_WE_I  <= '1';
		when st_datablockcount1=>
			if(we_start_frame_sig = '1' ) then
				UartWB_DAT_I <= data_block_count_high_sig;
			else
				UartWB_DAT_I <= x"00";
			end if;
			UartWB_STB_I_send <= '1'; 
			UartWB_WE_I  <= '1';
		when st_datablockcount2=>
			null;
		-- -----------------------
		when st_datablockcount3=>
			if(we_start_frame_sig = '1' ) then
				UartWB_DAT_I <= data_block_count_low_sig;
			else
				UartWB_DAT_I <= x"01";
			end if;
			UartWB_WE_I  <= '1';
		when st_datablockcount4=>
			if(we_start_frame_sig = '1' ) then
				UartWB_DAT_I <= data_block_count_low_sig;
			else
				UartWB_DAT_I <= x"01";
			end if;
			UartWB_STB_I_send <= '1'; 
			UartWB_WE_I  <= '1';
		when st_datablockcount5=>
			null;
		-- -----------------------
		when st_datablocksize0=>
			if(twoByteMode = '1') then
				UartWB_DAT_I <= x"02";
			elsif(we_start_frame_sig = '1' ) then
				UartWB_DAT_I <= data_block_size_sig;
			else
				UartWB_DAT_I <= x"01";
			end if;
			UartWB_WE_I  <= '1';
		when st_datablocksize1=>
			if(twoByteMode = '1') then
				UartWB_DAT_I <= x"02";
			elsif(we_start_frame_sig = '1' ) then
				UartWB_DAT_I <= data_block_size_sig;
			else
				UartWB_DAT_I <= x"01";
			end if;
			UartWB_STB_I_send <= '1'; 
			UartWB_WE_I  <= '1';
		when st_datablocksize2=>
			null;
		-- -----------------------
		when st_data0_0=>
			UartWB_DAT_I <= dat0_i_sig;
			UartWB_WE_I <= '1';
		when st_data0_1=>
			UartWB_DAT_I <= dat0_i_sig;
			UartWB_STB_I_send <= '1'; 
			UartWB_WE_I <= '1';
		when st_data0_2=>
			null;
		-- -----------------------
		when st_data1_0=>
			UartWB_DAT_I <= dat1_i_sig;
			UartWB_WE_I <= '1';
		when st_data1_1=>
			UartWB_DAT_I <= dat1_i_sig;
			UartWB_STB_I_send <= '1'; 
			UartWB_WE_I <= '1';
		when st_data1_2=>
			null;
		-- -----------------------
		when st_crc0=>
			UartWB_DAT_I <= x"ff";
			UartWB_WE_I <= '1';
		when st_crc1=>
			UartWB_DAT_I <= x"ff";
			UartWB_STB_I_send <= '1'; 
			UartWB_WE_I <= '1';
		when st_crc2=>
			null;
		-- -----------------------
		when st_finished =>
			IntTx_O		<= '1';
		when others =>
			null;
	end case;  
	end process;


	UartWB_STB_PROCESS : process(UartWB_STB_I_recv,UartWB_STB_I_send,sendingState)
	begin
		if(sendingState /= st_wait) then
			UartWB_STB_I_signal <= UartWB_STB_I_send;
		else
			UartWB_STB_I_signal <= UartWB_STB_I_recv;
		end if;
	end process;

end Behavioral;
