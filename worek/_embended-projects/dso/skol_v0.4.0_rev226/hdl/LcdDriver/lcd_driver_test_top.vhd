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
--------------------------------------------------------------------------
-- Company: 
-- Engineer: 
-- 
-- Create Date:    16:11:20 09/26/2008 
-- Design Name: 
-- Module Name:    top - Behavioral 
-- Project Name: 
-- Target Devices: 
-- Tool versions: 
-- Description: 
--
-- Dependencies: 
--
-- Revision: 
-- Revision 0.01 - File Created
-- Additional Comments: 
-- 
----------------------------------------------------------------------------------
library IEEE;
use IEEE.STD_LOGIC_1164.ALL;
use IEEE.STD_LOGIC_ARITH.ALL;
use IEEE.STD_LOGIC_UNSIGNED.ALL;

---- Uncomment the following library declaration if instantiating
---- any Xilinx primitives in this code.
--library UNISIM;
--use UNISIM.VComponents.all;

entity top is
	Port ( 	CLK_50M 	: in STD_LOGIC;
				CLK_AUX	: in STD_LOGIC;
				CLK_SMA  : in STD_LOGIC;
				E_TX_CLK  : in STD_LOGIC; -- ethernet
				E_RX_CLK  : in STD_LOGIC; -- ethernet
				-- start inserting top.vhd ports here now
				-- LCD
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
				BTN_NORTH 	: in  STD_LOGIC;
				BTN_EAST 	: in  STD_LOGIC;
				BTN_SOUTH	: in  STD_LOGIC;
				BTN_WEST		: in  STD_LOGIC;
				ROT_CENTER	: in  STD_LOGIC;
				ROT_A			: in  STD_LOGIC;
				ROT_B			: in  STD_LOGIC;

				LED_0 	: out  STD_LOGIC;
				LED_1 	: out  STD_LOGIC;
				LED_2 	: out  STD_LOGIC;
				LED_3 	: out  STD_LOGIC;
				LED_4 	: out  STD_LOGIC;
				LED_5 	: out  STD_LOGIC;
				LED_6 	: out  STD_LOGIC;
				LED_7 	: out  STD_LOGIC);
end top;

architecture Behavioral of top is

--********************************************

component lcd_driver 
	Port ( 	-- LCD
				CLOCK 		: IN  STD_LOGIC;
				RESET_AND_GO: IN  STD_LOGIC;
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
				writeDataOutFirstOnly  	: in  STD_LOGIC ;
				writeDataOutAll    		: in  STD_LOGIC ; -- use this to write out byte
				writeDataDone 				: out STD_LOGIC   -- goes high when finished
				);
end component;

--********************************************
	Signal CLOCK : STD_LOGIC;
		--LCD
	Signal initLcdDone 		: STD_LOGIC ;
	Signal lcdDriverReset	: STD_LOGIC := '0';
	Signal lcdDriverEnable  : STD_LOGIC := '0' ;
	Signal lcdWriteData		: STD_LOGIC_VECTOR(7 downto 0);
	Signal lcdWriteDataSecondPart		: STD_LOGIC_VECTOR(7 downto 0);
	Signal lcdWriteDataOut  : STD_LOGIC := '0';
	Signal lcdWriteDataFirstAndSecond  : STD_LOGIC := '0';
	Signal lcdWriteDataDone : STD_LOGIC;
	
	--Signal BTN_SOUTH :  STD_LOGIC;

	
	Signal LCD_DB_0_sig ,
			LCD_DB_1_sig ,
			LCD_DB_2_sig ,
			LCD_DB_3_sig ,
			LCD_DB_4_sig ,
			LCD_DB_5_sig ,
			LCD_DB_6_sig ,
			LCD_DB_7_sig :STD_LOGIC;
		
begin

--********************************************
my_lcd_driver : lcd_driver 
	port map ( -- LCD
				CLOCK 		=> CLOCK,
				RESET_AND_GO=> lcdDriverReset,
				ENABLE 		=> lcdDriverEnable,
				LCD_DB_0  	=> LCD_DB_0_sig,
				LCD_DB_1  	=> LCD_DB_1_sig,
				LCD_DB_2  	=> LCD_DB_2_sig,
				LCD_DB_3   	=> LCD_DB_3_sig,
				LCD_DB_4  	=> LCD_DB_4_sig,
				LCD_DB_5    => LCD_DB_5_sig,
				LCD_DB_6  	=> LCD_DB_6_sig,
				LCD_DB_7   	=> LCD_DB_7_sig,
				LCD_E   		=> LCD_E,
				LCD_RS  		=> LCD_RS,
				LCD_RW   	=> LCD_RW,
				-- div
				init_done	=> initLcdDone,
				WriteDataFirst					=> lcdWriteData,
				WriteDataSecond				=> lcdWriteDataSecondPart,
				WriteDataOutFirstOnly		=> lcdWriteDataOut,
				WriteDataOutAll  				=> lcdWriteDataFirstAndSecond,
				WriteDataDone 					=> lcdWriteDataDone
	 );
	 
--********************************************

	tempProcesss0 : process (initLcdDone, CLOCK)
	variable running1 : STD_LOGIC := '0';
	variable running2 : STD_LOGIC := '0';
	variable counter : integer := 0;
	variable data 	: STD_LOGIC_VECTOR(7 downto 0);

   begin
	--if (BTN_SOUTH'event and BTN_SOUTH = '1') then
	if (CLOCK'event and CLOCK = '1') then
      if (BTN_SOUTH = '1' and running1 = '0') then
		--if (running1 = '0') then
         running1 := '1';
			counter := 0;
		elsif(running1 = '1') then
			if(counter = 0) then
				lcdWriteData <= x"f1";
				lcdWriteDataOut <= '0';
				counter := counter +1;
			elsif(counter = 1 ) then
				lcdWriteData <= x"f5";
				lcdWriteDataOut <= '1';
				counter := counter +1;
			elsif(counter >= 2 and counter < 50000000) then -- extra big timeout for button Usage
				lcdWriteData <= x"f5";
				lcdWriteDataOut <= '0';
				counter := counter +1;
			else 
				lcdWriteDataOut <= '0';
				running1 := '0';

			end if;
      elsif (BTN_EAST = '1' and running2 = '0') then
		--if (running1 = '0') then
         running2 := '1';
			counter := 0;
		elsif(running2 = '1') then
			if(counter = 0) then
				lcdWriteData <= x"f1";
				lcdWriteDataSecondPart <= x"f1";
				counter := counter +1;
			elsif(counter = 1 ) then
				lcdWriteData <= x"f3";
				lcdWriteDataSecondPart <= x"f2";
				lcdWriteDataFirstAndSecond <= '1';
				counter := counter +1;
			elsif(counter >= 2 and counter < 50000000) then -- extra big timeout for button Usage
				lcdWriteData <= x"f3";
				lcdWriteDataSecondPart <= x"f2";
				lcdWriteDataFirstAndSecond <= '0';
				counter := counter +1;
			else 
				lcdWriteData <= x"00";
				lcdWriteDataSecondPart <= x"00";
				lcdWriteDataFirstAndSecond <= '0';
				running2 := '0';

			end if;
      end if;
	end if;
   end process;
	
	tempProcesss1 : process (lcdWriteDataDone)
	variable running1 : STD_LOGIC := '0';
	variable running2 : STD_LOGIC := '0';
	variable counter : integer := 0;
	variable data 	: STD_LOGIC_VECTOR(7 downto 0);

   begin
	--if (BTN_SOUTH'event and BTN_SOUTH = '1') then
	if (lcdWriteDataDone'event and lcdWriteDataDone = '1') then
		LED_0 <= '0';
		LED_1 <= '0';
		LED_2 <= '0';
		LED_3 <= '0';
		LED_4 <= '0';
		LED_5 <= '0';
		LED_6 <= '0';
		
		
		if( counter = 1) then
			LED_0 <= '1';
		elsif(counter = 2) then
			LED_1 <= '1';
		elsif(counter = 3) then
			LED_0 <= '1';
			LED_1 <= '1';		
		elsif(counter = 4) then
			LED_2 <= '1';
		elsif(counter = 5) then
			LED_0 <= '1';
			LED_2 <= '1';
		elsif(counter = 6) then
			LED_1 <= '1';
			LED_2 <= '1';
		elsif(counter = 7) then
			LED_0 <= '1';
			LED_1 <= '1';
			LED_2 <= '1';
		else
			counter := 0;
		end if;
		
		
		counter := counter +1;
	end if;
   end process;
	
	LED_7 <= lcdWriteDataDone;

--********************************************

	--LED_0 <= BTN_SOUTH;
	lcdDriverReset <= '0';
	--LED_0 <= initLcdDone	;
	lcdDriverEnable <= '1';
	CLOCK <= CLK_50M;
	
	LCD_DB_0  <= LCD_DB_0_sig;
	LCD_DB_1  <= LCD_DB_1_sig;
	LCD_DB_2  <= LCD_DB_2_sig;
	LCD_DB_3  <= LCD_DB_3_sig;
	LCD_DB_4  <= LCD_DB_4_sig;
	LCD_DB_5  <= LCD_DB_5_sig;
	LCD_DB_6  <= LCD_DB_6_sig;
	LCD_DB_7  <= LCD_DB_7_sig;


	
	
	--BTN_SOUTH <=	ROT_A	;
end Behavioral;
