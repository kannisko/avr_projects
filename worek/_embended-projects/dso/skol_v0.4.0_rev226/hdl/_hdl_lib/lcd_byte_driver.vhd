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
-- Create Date:    11:46:26 10/08/2008 
-- Design Name: 
-- Module Name:    lcd_byte_driver - Behavioral 
-- Project Name: 
-- Target Devices: 
-- Tool versions: 
-- Description: 
-- This is a Wrapper for the lcd_driver.
-- The Data is converted to LCD Control Signals - so that the byte is displayed as its value,
-- because the lcd_driver is a raw driver module.
-- Dependencies: 
--
-- Revision: 
-- Revision 1.00 functional
-- Revision 1.01 modified ports
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

entity lcd_byte_driver is
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
				init_done_o			: out  STD_LOGIC;
				dataByte_i     		: in  STD_LOGIC_VECTOR(7 downto 0);
				writeIt_i          	: in  STD_LOGIC ;
				writeDataDone_o 		: out STD_LOGIC  -- goes high when finished
				);
end lcd_byte_driver;

architecture Behavioral of lcd_byte_driver is
component lcd_driver 
	Port ( 	-- LCD
				CLOCK 		: IN  STD_LOGIC;
				RESET		: IN  STD_LOGIC;
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
				WriteDataDone 				: out STD_LOGIC   -- goes high when finished
				);
end component;
		--LCD
	Signal lcdWriteData		: STD_LOGIC_VECTOR(7 downto 0);
	Signal lcdWriteDataSecondPart		: STD_LOGIC_VECTOR(7 downto 0);
	Signal sig0 				: STD_LOGIC := '0';
	
CONSTANT character_A : STD_LOGIC_VECTOR(7 downto 0) := x"41";
CONSTANT character_B : STD_LOGIC_VECTOR(7 downto 0) := x"42";
CONSTANT character_C : STD_LOGIC_VECTOR(7 downto 0) := x"43";
CONSTANT character_D : STD_LOGIC_VECTOR(7 downto 0) := x"44";
CONSTANT character_E : STD_LOGIC_VECTOR(7 downto 0) := x"45";
CONSTANT character_F : STD_LOGIC_VECTOR(7 downto 0) := x"46";
CONSTANT character_0 : STD_LOGIC_VECTOR(7 downto 0) := x"30";
CONSTANT character_1 : STD_LOGIC_VECTOR(7 downto 0) := x"31";
CONSTANT character_2 : STD_LOGIC_VECTOR(7 downto 0) := x"32";
CONSTANT character_3 : STD_LOGIC_VECTOR(7 downto 0) := x"33";
CONSTANT character_4 : STD_LOGIC_VECTOR(7 downto 0) := x"34";
CONSTANT character_5 : STD_LOGIC_VECTOR(7 downto 0) := x"35";
CONSTANT character_6 : STD_LOGIC_VECTOR(7 downto 0) := x"36";
CONSTANT character_7 : STD_LOGIC_VECTOR(7 downto 0) := x"37";
CONSTANT character_8 : STD_LOGIC_VECTOR(7 downto 0) := x"38";
CONSTANT character_9 : STD_LOGIC_VECTOR(7 downto 0) := x"39";
	
begin
my_lcd_driver : lcd_driver 
	port map ( -- LCD
				CLOCK 		=> CLOCK,
				RESET       => RESET,
				ENABLE 		=> ENABLE,
				LCD_DB_0  	=> LCD_DB_0,
				LCD_DB_1  	=> LCD_DB_1,
				LCD_DB_2  	=> LCD_DB_2,
				LCD_DB_3   	=> LCD_DB_3,
				LCD_DB_4  	=> LCD_DB_4,
				LCD_DB_5    => LCD_DB_5,
				LCD_DB_6  	=> LCD_DB_6,
				LCD_DB_7   	=> LCD_DB_7,
				LCD_E   		=> LCD_E,
				LCD_RS  		=> LCD_RS,
				LCD_RW   	=> LCD_RW,
				-- div
				init_done	=> init_done_o,
				WriteDataFirst					=> lcdWriteData,
				WriteDataSecond				=> lcdWriteDataSecondPart,
				WriteDataOutFirstOnly		=> sig0,
				WriteDataOutAll  				=> writeIt_i,
				WriteDataDone 					=> writeDataDone_o
	 );
	 



   TranslateProcess: process (dataByte_i)
   begin
		if(   dataByte_i(7 downto 4) = x"0") then
			lcdWriteData <= character_0;
		elsif(dataByte_i(7 downto 4) = x"1") then
			lcdWriteData <= character_1;
		elsif(dataByte_i(7 downto 4) = x"2") then
			lcdWriteData <= character_2;
		elsif(dataByte_i(7 downto 4) = x"3") then
			lcdWriteData <= character_3;
		elsif(dataByte_i(7 downto 4) = x"4") then
			lcdWriteData <= character_4;
		elsif(dataByte_i(7 downto 4) = x"5") then
			lcdWriteData <= character_5;
		elsif(dataByte_i(7 downto 4) = x"6") then
			lcdWriteData <= character_6;
		elsif(dataByte_i(7 downto 4) = x"7") then
			lcdWriteData <= character_7;
		elsif(dataByte_i(7 downto 4) = x"8") then
			lcdWriteData <= character_8;
		elsif(dataByte_i(7 downto 4) = x"9") then
			lcdWriteData <= character_9;
		elsif(dataByte_i(7 downto 4) = x"a") then
			lcdWriteData <= character_A;
		elsif(dataByte_i(7 downto 4) = x"b") then
			lcdWriteData <= character_B;
		elsif(dataByte_i(7 downto 4) = x"c") then
			lcdWriteData <= character_C;
		elsif(dataByte_i(7 downto 4) = x"d") then
			lcdWriteData <= character_D;
		elsif(dataByte_i(7 downto 4) = x"e") then
			lcdWriteData <= character_E;
		elsif(dataByte_i(7 downto 4) = x"f") then
			lcdWriteData <= character_F;
      end if;
		
		if(   dataByte_i(3 downto 0) = x"0") then
			lcdWriteDataSecondPart <= character_0;
		elsif(dataByte_i(3 downto 0) = x"1") then
			lcdWriteDataSecondPart <= character_1;
		elsif(dataByte_i(3 downto 0) = x"2") then
			lcdWriteDataSecondPart <= character_2;
		elsif(dataByte_i(3 downto 0) = x"3") then
			lcdWriteDataSecondPart <= character_3;
		elsif(dataByte_i(3 downto 0) = x"4") then
			lcdWriteDataSecondPart <= character_4;
		elsif(dataByte_i(3 downto 0) = x"5") then
			lcdWriteDataSecondPart <= character_5;
		elsif(dataByte_i(3 downto 0) = x"6") then
			lcdWriteDataSecondPart <= character_6;
		elsif(dataByte_i(3 downto 0) = x"7") then
			lcdWriteDataSecondPart <= character_7;
		elsif(dataByte_i(3 downto 0) = x"8") then
			lcdWriteDataSecondPart <= character_8;
		elsif(dataByte_i(3 downto 0) = x"9") then
			lcdWriteDataSecondPart <= character_9;
		elsif(dataByte_i(3 downto 0) = x"a") then
			lcdWriteDataSecondPart <= character_A;
		elsif(dataByte_i(3 downto 0) = x"b") then
			lcdWriteDataSecondPart <= character_B;
		elsif(dataByte_i(3 downto 0) = x"c") then
			lcdWriteDataSecondPart <= character_C;
		elsif(dataByte_i(3 downto 0) = x"d") then
			lcdWriteDataSecondPart <= character_D;
		elsif(dataByte_i(3 downto 0) = x"e") then
			lcdWriteDataSecondPart <= character_E;
		elsif(dataByte_i(3 downto 0) = x"f") then
			lcdWriteDataSecondPart <= character_F;
      end if;
   end process;

end Behavioral;





