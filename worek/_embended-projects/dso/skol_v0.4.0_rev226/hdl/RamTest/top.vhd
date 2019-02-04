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
-- --------------------------------------------------------------------------------
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
-- --------------------------------------------------------------------------------
library IEEE;
use IEEE.STD_LOGIC_1164.ALL;
use IEEE.STD_LOGIC_ARITH.ALL;
use IEEE.STD_LOGIC_UNSIGNED.ALL;

---- Uncomment the following library declaration if instantiating
---- any Xilinx primitives in this code.
--library UNISIM;
--use UNISIM.VComponents.all;

entity top is
generic(adcRamDataWidth:			integer:=8;
		adcRamDataDepth:			integer:=8;
		adcRamAddressBusWidth:		integer:=8);
	Port ( 	CLK_50M 	: in STD_LOGIC;

				-- div
				BTN_NORTH 	: in  STD_LOGIC;

				
				-- LED
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



component sram 
generic(dataWidth:			integer:=8;
		dataDepth:			integer:=8;
		addressBusWidth:	integer:=8);
port(CLOCK:			in std_logic;	
	ENABLE_I:		in std_logic;
	READ_I:			in std_logic;
	WRITE_I:		in std_logic;
	READ_ADDR_I:	in std_logic_vector(addressBusWidth-1 downto 0);
	WRITE_ADDR_I: 	in std_logic_vector(addressBusWidth-1 downto 0); 
	DATA_IN_I: 		in std_logic_vector(dataWidth-1 downto 0);
	DATA_OUT_O: 	out std_logic_vector(dataWidth-1 downto 0)
	);
end component;
-----Signals +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	--LCD

	Signal ledTmp		: std_logic_vector(7 downto 0) ;
	Signal ledTmp0		: std_logic_vector(7 downto 0) ;
		
	-- adcRam
	Signal adcRamEnableI 						: STD_LOGIC := '1' ;
	Signal adcRamReadI		, adcRamWriteI 		: STD_LOGIC := '1' ;
	Signal adcRamReadAddrI	, adcRamWriteAddrI 	: std_logic_vector(adcRamAddressBusWidth-1 downto 0);
	Signal adcRamDataInI	, adcRamDataOutO 	: std_logic_vector(adcRamDataWidth-1 downto 0);
	
	-- Helper

--+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
begin


MyAdcRam : sram 
generic map (	dataWidth 		=> adcRamDataWidth,
				dataDepth 		=> adcRamDataDepth,
				addressBusWidth => adcRamAddressBusWidth)
port map (CLOCK 			=> CLK_50M,  -- clock
	ENABLE_I 		=> adcRamEnableI,
	READ_I 			=> adcRamReadI,
	WRITE_I 		=> adcRamWriteI,
	READ_ADDR_I 	=> adcRamReadAddrI,	
	WRITE_ADDR_I  	=> adcRamWriteAddrI,
	DATA_IN_I  		=> adcRamDataInI,	
	DATA_OUT_O  	=> adcRamDataOutO	
	);
-- -- ----------------------------------------------------------------------------------
	CONTROLLER_SYNC_PROC: process (CLK_50M,BTN_NORTH)

		variable counter : integer := 0;
	begin
		if(BTN_NORTH = '1') then
			counter := 0;
		elsif(CLK_50M'event and CLK_50M = '1') then
			
			adcRamWriteI 		<= '1';
			adcRamReadI			<= '0';
			if(counter = 0) then
					adcRamDataInI     	<= x"aa";
					adcRamWriteAddrI 	<= x"00";
					adcRamWriteI 		<=  '1';
			elsif(counter = 1) then
					adcRamDataInI     	<= x"aa";
					adcRamWriteAddrI 	<= x"00";
					adcRamWriteI 		<=  '0';	
			elsif(counter = 2) then
					adcRamDataInI     	<= x"cc";
					adcRamWriteAddrI 	<= x"02";
					adcRamWriteI 		<=  '1';	
			elsif(counter = 3) then
					adcRamDataInI     	<= x"cc";
					adcRamWriteAddrI 	<= x"02";
					adcRamWriteI 		<=  '0';	
					
					
			elsif(counter = 5) then
					adcRamReadAddrI 	<= x"02";
					adcRamReadI 		<=  '1';	
			elsif(counter = 6) then
					ledTmp0     		<= adcRamDataOutO;	
			elsif(counter = 7) then
					ledTmp     			<= adcRamDataOutO;
				
				
			elsif(counter = 13) then
					adcRamReadAddrI 	<= x"00";
					adcRamReadI 		<=  '1';
			elsif(counter = 14) then
					null;
			elsif(counter = 15) then
					ledTmp     			<= adcRamDataOutO;			


			elsif(counter = 20) then
					counter := 0;
			end if;
				
			counter := counter +1 ;
		end if;
	end process;
 




-- -------------------------------------------------------------

	adcRamEnableI 	<= '1' ;



	
		-- led's
	LED_0 <= ledTmp(0);
	LED_1 <= ledTmp(1);
	LED_2 <= ledTmp(2);
	LED_3 <= ledTmp(3);
	LED_4 <= ledTmp(4);
	LED_5 <= ledTmp(5);
	LED_6 <= ledTmp(6);
    LED_7 <= ledTmp(7);
		

end Behavioral;

