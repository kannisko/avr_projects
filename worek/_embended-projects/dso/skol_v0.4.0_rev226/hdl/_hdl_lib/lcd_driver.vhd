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
-- Create Date:    16:55:25 09/26/2008 
-- Design Name: 
-- Module Name:    lc_driver - Behavioral 
-- Project Name: 
-- Target Devices: 
-- Tool versions: 
-- Description: 
--This is a LCD Driver Module for a Spartan 3A StarterKit Board
--A 8 bit interface is used.
--
--The 2x16 character LCD has an internal Sitronix ST7066U graphics controller that is 
--functionally equivalent with the following devices. 
--• Samsung S6A0069X  or KS0066U 
--• Hitachi HD44780 
--• SMOS SED1278 
-- Dependencies: 
--
-- Revision: 
-- Revision 1.01 released
-- Revision 1.02 comments
-- Revision 1.03 Reset Port Modified
-- Additional Comments: 
--
-- KNOWN ISSUES
-- o If Linebreak occurs - the cursor does not appear in the next line at the first char. 
----------------------------------------------------------------------------------
library IEEE;
use IEEE.STD_LOGIC_1164.ALL;
use IEEE.STD_LOGIC_ARITH.ALL;
use IEEE.STD_LOGIC_UNSIGNED.ALL;

---- Uncomment the following library declaration if instantiating
---- any Xilinx primitives in this code.
--library UNISIM;
--use UNISIM.VComponents.all;

entity lcd_driver is
	Port ( 	-- LCD
				CLOCK 		: IN  STD_LOGIC;
				RESET       : IN  STD_LOGIC;
				ENABLE		: IN  STD_LOGIC;
				LCD_DB_0    : out STD_LOGIC;
				LCD_DB_1  	: out STD_LOGIC;
				LCD_DB_2  	: out STD_LOGIC;
				LCD_DB_3   	: out STD_LOGIC;
				LCD_DB_4  	: out STD_LOGIC;
				LCD_DB_5    : out STD_LOGIC;
				LCD_DB_6  	: out STD_LOGIC;
				LCD_DB_7   	: out STD_LOGIC;
				LCD_E   		: out STD_LOGIC;
				LCD_RS  		: out STD_LOGIC;
				LCD_RW   	: out STD_LOGIC;
				-- div 
				init_done					: out  STD_LOGIC;
				WriteDataFirst     		: in  STD_LOGIC_VECTOR(7 downto 0);
				WriteDataSecond    		: in  STD_LOGIC_VECTOR(7 downto 0); -- optional
				WriteDataOutFirstOnly  	: in  STD_LOGIC ;
				WriteDataOutAll    		: in  STD_LOGIC ; -- use this to write out byte
				WriteDataDone 				: out STD_LOGIC  -- goes high when finished
				);
end lcd_driver;



architecture Behavioral1 of lcd_driver is

----CONSTANT character_a : STD_LOGIC_VECTOR(7 downto 0) := x"61";
----CONSTANT character_b : STD_LOGIC_VECTOR(7 downto 0) := x"62";
----CONSTANT character_c : STD_LOGIC_VECTOR(7 downto 0) := x"63";
----CONSTANT character_d : STD_LOGIC_VECTOR(7 downto 0) := x"64";
----CONSTANT character_e : STD_LOGIC_VECTOR(7 downto 0) := x"65";
----CONSTANT character_f : STD_LOGIC_VECTOR(7 downto 0) := x"66";
--
----CONSTANT character_g : STD_LOGIC_VECTOR(7 downto 0) := x"67";
----CONSTANT character_h : STD_LOGIC_VECTOR(7 downto 0) := x"68";
----CONSTANT character_i : STD_LOGIC_VECTOR(7 downto 0) := x"69";
----CONSTANT character_j : STD_LOGIC_VECTOR(7 downto 0) := x"6A";
----CONSTANT character_k : STD_LOGIC_VECTOR(7 downto 0) := x"6B";
----CONSTANT character_l : STD_LOGIC_VECTOR(7 downto 0) := x"6C";
----CONSTANT character_m : STD_LOGIC_VECTOR(7 downto 0) := x"6D";
----CONSTANT character_n : STD_LOGIC_VECTOR(7 downto 0) := x"6E";
----CONSTANT character_o : STD_LOGIC_VECTOR(7 downto 0) := x"6F";
----CONSTANT character_p : STD_LOGIC_VECTOR(7 downto 0) := x"70";
----CONSTANT character_q : STD_LOGIC_VECTOR(7 downto 0) := x"71";
----CONSTANT character_r : STD_LOGIC_VECTOR(7 downto 0) := x"72";
----CONSTANT character_s : STD_LOGIC_VECTOR(7 downto 0) := x"73";
----CONSTANT character_t : STD_LOGIC_VECTOR(7 downto 0) := x"74";
----CONSTANT character_u : STD_LOGIC_VECTOR(7 downto 0) := x"75";
----CONSTANT character_v : STD_LOGIC_VECTOR(7 downto 0) := x"76";
----CONSTANT character_w : STD_LOGIC_VECTOR(7 downto 0) := x"77";
----CONSTANT character_x : STD_LOGIC_VECTOR(7 downto 0) := x"78";
----CONSTANT character_y : STD_LOGIC_VECTOR(7 downto 0) := x"79";
----CONSTANT character_z : STD_LOGIC_VECTOR(7 downto 0) := x"7A";
--CONSTANT character_A : STD_LOGIC_VECTOR(7 downto 0) := x"41";
--CONSTANT character_B : STD_LOGIC_VECTOR(7 downto 0) := x"42";
--CONSTANT character_C : STD_LOGIC_VECTOR(7 downto 0) := x"43";
--CONSTANT character_D : STD_LOGIC_VECTOR(7 downto 0) := x"44";
--CONSTANT character_E : STD_LOGIC_VECTOR(7 downto 0) := x"45";
--CONSTANT character_F : STD_LOGIC_VECTOR(7 downto 0) := x"46";
--CONSTANT character_G : STD_LOGIC_VECTOR(7 downto 0) := x"47";
--CONSTANT character_H : STD_LOGIC_VECTOR(7 downto 0) := x"48";
--CONSTANT character_I : STD_LOGIC_VECTOR(7 downto 0) := x"49";
--CONSTANT character_J : STD_LOGIC_VECTOR(7 downto 0) := x"4A";
--CONSTANT character_K : STD_LOGIC_VECTOR(7 downto 0) := x"4B";
--CONSTANT character_L : STD_LOGIC_VECTOR(7 downto 0) := x"4C";
--CONSTANT character_M : STD_LOGIC_VECTOR(7 downto 0) := x"4D";
--CONSTANT character_N : STD_LOGIC_VECTOR(7 downto 0) := x"4E";
--CONSTANT character_O : STD_LOGIC_VECTOR(7 downto 0) := x"4F";
--CONSTANT character_P : STD_LOGIC_VECTOR(7 downto 0) := x"50";
--CONSTANT character_Q : STD_LOGIC_VECTOR(7 downto 0) := x"51";
--CONSTANT character_R : STD_LOGIC_VECTOR(7 downto 0) := x"52";
--CONSTANT character_S : STD_LOGIC_VECTOR(7 downto 0) := x"53";
--CONSTANT character_T : STD_LOGIC_VECTOR(7 downto 0) := x"54";
--CONSTANT character_U : STD_LOGIC_VECTOR(7 downto 0) := x"55";
--CONSTANT character_V : STD_LOGIC_VECTOR(7 downto 0) := x"56";
--CONSTANT character_W : STD_LOGIC_VECTOR(7 downto 0) := x"57";
--CONSTANT character_X : STD_LOGIC_VECTOR(7 downto 0) := x"58";
--CONSTANT character_Y : STD_LOGIC_VECTOR(7 downto 0) := x"59";
--CONSTANT character_Z : STD_LOGIC_VECTOR(7 downto 0) := x"5A";
--CONSTANT character_0 : STD_LOGIC_VECTOR(7 downto 0) := x"30";
--CONSTANT character_1 : STD_LOGIC_VECTOR(7 downto 0) := x"31";
--CONSTANT character_2 : STD_LOGIC_VECTOR(7 downto 0) := x"32";
--CONSTANT character_3 : STD_LOGIC_VECTOR(7 downto 0) := x"33";
--CONSTANT character_4 : STD_LOGIC_VECTOR(7 downto 0) := x"34";
--CONSTANT character_5 : STD_LOGIC_VECTOR(7 downto 0) := x"35";
--CONSTANT character_6 : STD_LOGIC_VECTOR(7 downto 0) := x"36";
--CONSTANT character_7 : STD_LOGIC_VECTOR(7 downto 0) := x"37";
--CONSTANT character_8 : STD_LOGIC_VECTOR(7 downto 0) := x"38";
--CONSTANT character_9 : STD_LOGIC_VECTOR(7 downto 0) := x"39";
--CONSTANT character_colon : STD_LOGIC_VECTOR(7 downto 0) := x"3A";
--CONSTANT character_stop : STD_LOGIC_VECTOR(7 downto 0) := x"2E";
--CONSTANT character_semi_colon : STD_LOGIC_VECTOR(7 downto 0) := x"3B";
--CONSTANT character_minus : STD_LOGIC_VECTOR(7 downto 0) := x"2D";
--CONSTANT character_divide : STD_LOGIC_VECTOR(7 downto 0) := x"2F";--       ;'/'
--CONSTANT character_plus : STD_LOGIC_VECTOR(7 downto 0) := x"2B";--
--CONSTANT character_comma : STD_LOGIC_VECTOR(7 downto 0) := x"2C";--
--CONSTANT character_less_than : STD_LOGIC_VECTOR(7 downto 0) := x"3C";--
--CONSTANT character_greater_than : STD_LOGIC_VECTOR(7 downto 0) := x"3E";--
--CONSTANT character_equals : STD_LOGIC_VECTOR(7 downto 0) := x"3D";--
--CONSTANT character_space : STD_LOGIC_VECTOR(7 downto 0) := x"20";--
--CONSTANT character_CR : STD_LOGIC_VECTOR(7 downto 0) := x"0D";--           ;carriage return
--CONSTANT character_question : STD_LOGIC_VECTOR(7 downto 0) := x"3F";--     ;'?'
--CONSTANT character_dollar : STD_LOGIC_VECTOR(7 downto 0) := x"24";--
--CONSTANT character_exclaim : STD_LOGIC_VECTOR(7 downto 0) := x"21";--      ;'!'
--CONSTANT character_BS : STD_LOGIC_VECTOR(7 downto 0) := x"08";--           ;Back Space command character

	signal 	timer 			: Integer := 0;
	signal   lcd_dataOut_init 			: STD_LOGIC_VECTOR(7 downto 0):= (others => '0');
	signal   lcd_dataOut_write 			: STD_LOGIC_VECTOR(7 downto 0):= (others => '0');
	signal 	init_done_sig 	: STD_LOGIC := '0' ;
--------------------------------------------------------------------------------------	
	signal lcd_e_sig_init	  : STD_LOGIC := '0';
	signal lcd_e_sig_write	  : STD_LOGIC := '0';
	--signal lcd_rs_sig	  : STD_LOGIC := '0';
	
	type state_type is (nop, st1_wait, st2_copy); 
   signal state, next_state : state_type := nop; 
--------------------------------------------------------------------------------------	


	constant factorInit : Integer := 10000;
	constant factor : Integer := 1;
	constant timeInit0 						:Integer :=  75*factorInit; 
	constant timeInit1 						:Integer :=  timeInit0 + 20*factorInit ;-- +  5000
	constant timeInit2 						:Integer :=  timeInit1 + 20*factorInit ;
	constant timeInit3 						:Integer :=  timeInit2 + 20*factorInit ;
	constant timeInit4 						:Integer :=  timeInit3 + 20*factorInit ;

	constant timeInit_initDone 			:Integer :=  timeInit4 + 20*factorInit ;
	constant timePreEHightTime 			:Integer :=  5;
	constant timeEHighTime 					:Integer :=  12;
--------------------------------------------------------------------------------------	
	
--	-- testbench debug settings
--	constant factor : Integer := 10;
--	constant timeInit0 						:Integer :=  1; 
--	constant timeInit1 						:Integer :=  timeInit0 + 1 ;-- +  5000
--	constant timeInit2 						:Integer :=  timeInit1 + 1 ;
--	constant timeInit3 						:Integer :=  timeInit2 + 1 ;
--	constant timeInit4 						:Integer :=  timeInit3 + 1 ;
--
--	constant timeInit_initDone 			:Integer :=  timeInit4 + 1 ;
--	constant timePreEHightTime 			:Integer :=  2;
--	constant timeEHighTime 					:Integer :=  3;
--------------------------------------------------------------------------------------	

--	-- LED debug settings        mmmttthhh           
--	constant factor : Integer := 100000000;
--	constant timeInit0 						:Integer :=  1; -- +205000
--	constant timeInit1 						:Integer :=  2; -- +  5000
--	constant timeInit2 						:Integer :=  3; -- +  2000
--	constant timeInit3 						:Integer :=  4; -- +  2000
--	constant timeInit4 						:Integer :=  5; -- +  2000 // FUNCTION SET
--	constant timeInit5			 			:Integer :=  6;
--	
--	--constant timeInit5_EntryMode :Integer := 980000;
--	constant timeInit6_DisplayControl	:Integer :=  7;
--	constant timeInit7_DisplayClear 		:Integer :=  8;
--	constant timeInit8_initDone 			:Integer :=  9;
--	
--	constant timePreEHightTime 			:Integer :=  5;
--	constant timeEHighTime 					:Integer :=  12;
begin



   

   INIT_TIMER_PROC: process (CLOCK, RESET)
   begin
		if(RESET = '1') then
			timer <= 0;
      elsif (CLOCK'event and CLOCK = '1') then
			if (ENABLE = '1' and init_done_sig = '0' ) then
				timer <= timer + 1;    
			end if;
      end if;
   end process;
	

 
   INIT_TIMER_DECODE: process ( timer ) is
	variable lcd_dataOut_var : STD_LOGIC_VECTOR(7 downto 0);
   begin
	
		if(timer < factor*timeInit0) then
			init_done_sig <= '0';
			lcd_e_sig_init 	<= '0';
			lcd_dataOut_init <= x"00";
		elsif((timer >= factor*timeInit0) and (timer < factor*timeInit1)) then
			lcd_dataOut_init <= x"38";
			if((timer >= factor*timeInit0 + timePreEHightTime) and (timer < factor*timeInit0 + timePreEHightTime + timeEHighTime)) then
				lcd_e_sig_init <= '1';
			elsif(timer > factor*timeInit0 + timePreEHightTime + timeEHighTime) then
				lcd_e_sig_init <= '0';
			end if;
		elsif((timer >= factor*timeInit1) and (timer < factor*timeInit2)) then 
			lcd_dataOut_init <= x"38";
			if((timer >= factor*timeInit1 + timePreEHightTime) and (timer < factor*timeInit1 + timePreEHightTime + timeEHighTime)) then
				lcd_e_sig_init <= '1';
			elsif(timer > factor*timeInit1 + timePreEHightTime + timeEHighTime) then
				lcd_e_sig_init <= '0';
			end if;
		elsif((timer >= factor*timeInit2) and (timer < factor*timeInit3)) then 
			--1 Display Control<0000_1 Displ(OnOff) Cursor(OnOff) BlinkCursor(onOff)>
         lcd_dataOut_init <= b"0000_1111";
			if((timer >= factor*timeInit2 + timePreEHightTime) and (timer < factor*timeInit2 + timePreEHightTime + timeEHighTime)) then
				lcd_e_sig_init <= '1';
			elsif(timer > factor*timeInit2 + timePreEHightTime + timeEHighTime) then
				lcd_e_sig_init <= '0';
			end if;
		elsif((timer >= factor*timeInit3) and (timer < factor*timeInit4)) then 
			-- Display Clear
         lcd_dataOut_init <= x"01";
			if((timer >= factor*timeInit3 + timePreEHightTime) and (timer < factor*timeInit3 + timePreEHightTime + timeEHighTime)) then
				lcd_e_sig_init <= '1';
			elsif(timer > factor*timeInit3 + timePreEHightTime + timeEHighTime) then
				lcd_e_sig_init <= '0';
			end if;
		elsif((timer >= factor*timeInit4) and (timer < factor*timeInit_initDone)) then 
			-- Entry Mode Set
			--		Sets the cursor move direction and specifies whether or not to shift the display. 
			--		0 Auto-decrement address counter. Cursor/blink moves to left.
			--		1 Auto-increment address counter. Cursor/blink moves to right
			--		0 0 0 0_0 1 I/D S
			--	   S ... Display Shift -> only 0 needed
         lcd_dataOut_init <= b"0000_0110";
			if((timer >= factor*timeInit4 + timePreEHightTime) and (timer < factor*timeInit4 + timePreEHightTime + timeEHighTime)) then
				lcd_e_sig_init <= '1';
			elsif(timer > factor*timeInit4 + timePreEHightTime + timeEHighTime) then
				lcd_e_sig_init <= '0';
			end if;
			
			
			----debug temp
--			
--		elsif((timer > factor*timeInit_initDone) and (timer < factor*timeInit_initDone + 100000)) then 
--			
--         lcd_dataOut_init <= b"0000_0110";
--			if((timer >= factor*timeInit4 + timePreEHightTime) and (timer < factor*timeInit4 + timePreEHightTime + timeEHighTime)) then
--				lcd_e_sig_init <= '1';
--			elsif(timer > factor*timeInit4 + timePreEHightTime + timeEHighTime) then
--				lcd_e_sig_init <= '0';
--			end if;
--			
			--end debug temp
			
		elsif((timer = factor*timeInit_initDone)) then 
			init_done_sig 	<= '1';
		else
			null;
		end if;

   end process;

-- no protection against to fast write attempts
WRITE_DATA_OUT_PROC: process (CLOCK, WriteDataOutFirstOnly,WriteDataOutAll)
	variable running0 : STD_LOGIC := '0';
	variable dualDataMode : STD_LOGIC := '0';
	variable counter : integer := 0;
	variable counterLineBreak : integer := 0;
	variable data 			: STD_LOGIC_VECTOR(7 downto 0);
	variable dataSecond 	: STD_LOGIC_VECTOR(7 downto 0);
	variable dataTemp 			: STD_LOGIC_VECTOR(7 downto 0);
	variable dataSecondTemp: STD_LOGIC_VECTOR(7 downto 0);
	variable charCounter : integer := 0;
	variable didLineBreak : bit := '0';
   begin
      if (CLOCK'event and CLOCK = '1') then
         if ( ((WriteDataOutFirstOnly = '1')OR(WriteDataOutAll = '1')) and running0 = '0') then
            running0 := '1';
			counter := 0;
			didLineBreak := '0';
			if(WriteDataOutAll = '1') then
				dualDataMode := '1';
			else
				dualDataMode := '0';
			end if;
         elsif(running0 = '1') then
				
				if(charCounter = 16) then -- set ddRam address to line 2
						WriteDataDone <= '0';
						if(counterLineBreak = 0) then
						
							dataTemp := WriteDataFirst;
							if(dualDataMode = '1') then
								dataSecondTemp := WriteDataSecond;
							end if;
							didLineBreak := '1';
							data := b"1100_0000";--set ddram addr
							counterLineBreak := counterLineBreak +1;
						elsif(counterLineBreak = 1) then
							lcd_dataOut_write <= data;
							LCD_RS  <= '0';
							lcd_e_sig_write <= '0';
							counterLineBreak := counterLineBreak +1;
						elsif(counterLineBreak >= 2 and counterLineBreak < 15) then
						--elsif(counterLineBreak >= 2 and counterLineBreak < 4) then
							lcd_dataOut_write <= data;
							LCD_RS  <= '0';
							lcd_e_sig_write <= '1';
							counterLineBreak := counterLineBreak +1;
						elsif(counterLineBreak >= 15 and counterLineBreak < 16) then
						--elsif(counterLineBreak >= 4 and counterLineBreak < 6) then
							lcd_dataOut_write <= data;
							LCD_RS  <= '0';
							lcd_e_sig_write <= '0';
							counterLineBreak := counterLineBreak +1;
						elsif(counterLineBreak >= 16 and counterLineBreak < 2020) then
						--elsif(counterLineBreak >= 6 and counterLineBreak < 8) then
							lcd_dataOut_write <= x"00";
							LCD_RS  <= '0';
							lcd_e_sig_write <= '0';
							counterLineBreak := counterLineBreak +1;
						else 
							counterLineBreak := 0;
							charCounter := charCounter +1;
						end if;
				elsif(charCounter = 32+1) then -- set DDram address to line 1
						WriteDataDone <= '0';
						if(counterLineBreak = 0) then
						
							dataTemp := WriteDataFirst;
							if(dualDataMode = '1') then
								dataSecondTemp := WriteDataSecond;
							end if;
							didLineBreak := '1';
							data := b"1000_0000";--set ddram addr
							counterLineBreak := counterLineBreak +1;
						elsif(counterLineBreak = 1) then
							lcd_dataOut_write <= data;
							LCD_RS  <= '0';
							lcd_e_sig_write <= '0';
							counterLineBreak := counterLineBreak +1;
						elsif(counterLineBreak >= 2 and counterLineBreak < 15) then
						--elsif(counterLineBreak >= 2 and counterLineBreak < 4) then
							lcd_dataOut_write <= data;
							LCD_RS  <= '0';
							lcd_e_sig_write <= '1';
							counterLineBreak := counterLineBreak +1;
						elsif(counterLineBreak >= 15 and counterLineBreak < 16) then
						--elsif(counterLineBreak >= 4 and counterLineBreak < 6) then
							lcd_dataOut_write <= data;
							LCD_RS  <= '0';
							lcd_e_sig_write <= '0';
							counterLineBreak := counterLineBreak +1;
						elsif(counterLineBreak >= 16 and counterLineBreak < 2020) then
						--elsif(counterLineBreak >= 6 and counterLineBreak < 8) then
							lcd_dataOut_write <= x"00";
							LCD_RS  <= '0';
							lcd_e_sig_write <= '0';
							counterLineBreak := counterLineBreak +1;
						else 
							counterLineBreak := 0;
							charCounter := 0;
						end if;
				elsif(counter = 0) then -- normal write behaviour
					
					WriteDataDone <= '0';
					if(didLineBreak = '1') then
						data := dataTemp;
						dataSecond := dataSecondTemp;
						didLineBreak := '0';
					else
						data := WriteDataFirst;
						dataSecond := WriteDataSecond;
					end if;
					counter := counter +1;
				elsif(counter = 1) then
					WriteDataDone <= '0';
					lcd_dataOut_write <= data;
					LCD_RS  <= '1';
					lcd_e_sig_write <= '0';
					counter := counter +1;
				elsif(counter >= 2 and counter < 15) then
					WriteDataDone <= '0';
					lcd_dataOut_write <= data;
					LCD_RS  <= '1';
					lcd_e_sig_write <= '1';
					counter := counter +1;
				elsif(counter >= 15 and counter < 16) then
					WriteDataDone <= '0';
					lcd_dataOut_write <= data;
					LCD_RS  <= '1';
					lcd_e_sig_write <= '0';
					counter := counter +1;
				elsif(counter >= 16 and counter < 2019) then
					WriteDataDone <= '0';
					lcd_dataOut_write <= x"00";
					LCD_RS  <= '0';
					lcd_e_sig_write <= '0';
					counter := counter +1;
				elsif(counter = 2019) then
					if( dualDataMode = '0') then
						WriteDataDone <= '1';
						counter := 0;
						running0 := '0';
						charCounter := charCounter +1;
					else
						counter := counter +1;
						charCounter := charCounter +1;
					end if;
						
				-- dualDataMode
				elsif(counter = 2020+0) then -- normal write behaviour
					WriteDataDone <= '0';
					data := dataSecond;
					counter := counter +1;
				elsif(counter = 2020+1) then
					WriteDataDone <= '0';
					lcd_dataOut_write <= data;
					LCD_RS  <= '1';
					lcd_e_sig_write <= '0';
					counter := counter +1;
				elsif(counter >= (2020+2) and counter < 2020+15) then
					WriteDataDone <= '0';
					lcd_dataOut_write <= data;
					LCD_RS  <= '1';
					lcd_e_sig_write <= '1';
					counter := counter +1;
				elsif(counter >= (2020+15) and counter < (2020+16)) then
					WriteDataDone <= '0';
					lcd_dataOut_write <= data;
					LCD_RS  <= '1';
					lcd_e_sig_write <= '0';
					counter := counter +1;
				elsif(counter >= (2020+16) and counter < (2020+2020)) then
					WriteDataDone <= '0';
					lcd_dataOut_write <= x"00";
					LCD_RS  <= '0';
					lcd_e_sig_write <= '0';
					counter := counter +1;
				else 
					WriteDataDone <= '1';
					counter := 0;
					running0 := '0';
					charCounter := charCounter +1;
				end if;
		 else
				WriteDataDone <= '1';
				LCD_RS  <= '0';
		 end if;--if ( ((WriteDataOut = '1')OR(WriteDataByteOut = '1')) and running0 = '0') then
      end if;--if (CLOCK'event and CLOCK = '1') then
   end process;
 

initE_sig : process(lcd_e_sig_init,lcd_e_sig_write,init_done_sig,lcd_dataOut_init, lcd_dataOut_write)
begin
	if(init_done_sig = '0') then
		LCD_E <= lcd_e_sig_init ;
		LCD_DB_0 <= lcd_dataOut_init(0);
		LCD_DB_1 <= lcd_dataOut_init(1);
		LCD_DB_2 <= lcd_dataOut_init(2);
		LCD_DB_3 <= lcd_dataOut_init(3);
		LCD_DB_4 <= lcd_dataOut_init(4);
		LCD_DB_5 <= lcd_dataOut_init(5);
		LCD_DB_6 <= lcd_dataOut_init(6);
		LCD_DB_7 <= lcd_dataOut_init(7);
	else
		LCD_E <= lcd_e_sig_write ;
		LCD_DB_0 <= lcd_dataOut_write(0);
		LCD_DB_1 <= lcd_dataOut_write(1);
		LCD_DB_2 <= lcd_dataOut_write(2);
		LCD_DB_3 <= lcd_dataOut_write(3);
		LCD_DB_4 <= lcd_dataOut_write(4);
		LCD_DB_5 <= lcd_dataOut_write(5);
		LCD_DB_6 <= lcd_dataOut_write(6);
		LCD_DB_7 <= lcd_dataOut_write(7);
	end if;	
end process;


LCD_RW <= '0';		
init_done <= init_done_sig;
	
end Behavioral1;

