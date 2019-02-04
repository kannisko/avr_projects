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
-- Revisions       : 0.01 init
-- Revisions       : 0.02 some timing fixes
-- Revisions       : 1.0  state machine rebuilt / verification with buttonReset and DebugO to LED's
-- Revisions       : 1.0.1  removed unused signals
-- Revision Number :
-- Version         :
-- Date    :
-- Modifier        : name <email>
-- Description     :
--
-- ----------------------------------------------------------------------------

library ieee;
   use ieee.std_logic_1164.all;

entity SPI_initializer is
  port (
		CLOCK   		: in  std_logic;  -- clock
		RESET_I 		: in  std_logic;  -- Reset input
		SPI_CLOCK_O  	: out  std_logic;   
		SPI_MOSI_O 	: out  std_logic;
		SPI_MISO_I 	: in   std_logic; -- ampOutput
		AMP_CS_O		: out  std_logic;
		INIT_ADC_DONE_O : out std_logic;
		DEBUG_O		: out std_logic_vector(7 downto 0));     
end SPI_initializer;

--+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
architecture Behaviour of SPI_initializer is


	Signal spiClock	: std_logic := '0';
	Signal initAdcDone	: std_logic := '0';
	
	signal counter 	: integer range 0 to 35 :=0;
	signal bitCounter : integer range 0 to 7 := 7;
	
	Signal ampCsO 	: std_logic := '1';	

	
	type state_type is (init, low0, low1, high0, high1, finished); 
   	signal state, next_state : state_type := init; 
--+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
  begin

-- ------------------------------------------------------------------------------------
	process(CLOCK,RESET_I)
	begin
		if(RESET_I = '1') then
			state <= init;
		elsif CLOCK'event and CLOCK ='1' then
			state <= next_state;
		end if;
	end process;
-- ------------------------------------------------------------------------------------

	STATE_DECODE_NEXT_STATE_PROCESS : process(state,counter,bitCounter) is
	begin
		next_state <= state ;
		case state is 
			when init =>
				next_state <= low0;
			when low0 =>
				if(counter = 5) then
					next_state <= low1;
				end if;
			when low1 =>
				if(counter = 10) then
					next_state <= high0;
				end if;
			when high0 =>		
				if(counter = 19) then
					next_state <= high1;	
				end if;	
			when high1 =>
				--if(counter = 20) then
					if(bitCounter = 0) then
						next_state <= finished;
					else
						next_state <= low0 ;
					end if;
				--end if;
			when finished =>
				next_state <= finished ;
			when others =>
				null;
		end case;
	end process;
	
-- ------------------------------------------------------------------------------------
	STATE_DECODE_OUTPUTS_PROCESS : process(CLOCK) is
	variable amplifierSetting : std_logic_vector(7 downto 0 ) := b"00010001";
	begin
	if CLOCK'event and CLOCK ='1' then
		amplifierSetting := b"00010001"; 
		initAdcDone <= '0';
		case state is 
			when init =>
				counter 	<= 0;
				ampCsO 	<= '1';
				spiClock 	<= '0' ;
				bitCounter <= 7;
			
			when low0 =>
				ampCsO 	<= '0';
				spiClock 	<= '0' ;
				SPI_MOSI_O 	<= amplifierSetting(bitCounter);
				if(counter > 20)then
					counter <= 1;
				else
					counter	<= counter + 1 ;
				end if;
					
			when low1 =>
				ampCsO 	<= '0';
				spiClock 	<= '0' ;
				SPI_MOSI_O 	<= amplifierSetting(bitCounter);
				counter		<= counter + 1 ;
			when high0 =>		
				ampCsO 	<= '0';
				spiClock 	<= '1' ;
				SPI_MOSI_O 	<= amplifierSetting(bitCounter);
				counter		<= counter + 1 ;
			when high1 =>
				ampCsO 	<= '0';
				spiClock 	<= '1' ;
				SPI_MOSI_O 	<= amplifierSetting(bitCounter);
				counter		<= counter + 1 ;
				bitCounter	<= bitCounter -1 ;
				DEBUG_O(bitCounter) <= SPI_MISO_I;
			when finished =>
				ampCsO 	<= '1';
				initAdcDone <= '1';
			when others =>
				ampCsO <= '1';
		end case;
	end if;
	end process;

-- ------------------------------------------------------------------------------------

 	
-- ------------------------------------------------------------------------------------



  	INIT_ADC_DONE_O <= initAdcDone;
	SPI_CLOCK_O <= spiClock;
	AMP_CS_O <= ampCsO;
end Behaviour;
