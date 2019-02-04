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
-- Create Date:    14:32:38 10/24/2008 
-- Design Name: 
-- Module Name:    adc_controller - Behavioral 
-- Project Name: 
-- Target Devices: 
-- Tool versions: 
-- Description: 
--
-- Dependencies: 
--
-- Revision: 
-- Revision 0.01  - File Created
-- Revision 0.05  - Functionality tested for both channels for singleData Aquisition
-- Revision 0.051 - Async Reset Added
-- Revision 0.052 - Registered Output
-- Additional Comments: 
-- Aquired Data is always the old data ! -> see Manual for starter kit.
--  If you want actual value, aquire 2 times, and discard the first one.
--
-- KNOWN ISSUES
-- AquireDataStream needs o be implemented
-- --------------------------------------------------------------------------------
library IEEE;
use IEEE.STD_LOGIC_1164.ALL;
use IEEE.STD_LOGIC_ARITH.ALL;
use IEEE.STD_LOGIC_UNSIGNED.ALL;

---- Uncomment the following library declaration if instantiating
---- any Xilinx primitives in this code.
--library UNISIM;
--use UNISIM.VComponents.all;

entity adc_controller is
    Port ( CLOCK : in  STD_LOGIC;
    	   RESET : in  STD_LOGIC;
           AD_CONV_O : out  STD_LOGIC;
           AD_CLK_O : out  STD_LOGIC;
           ADC_OUT_I : in  STD_LOGIC;
           AQUIRED_DATA_O : out  STD_LOGIC_VECTOR (13 downto 0);
           AQUIRED_DATA_O_HIGHBYTE : out  STD_LOGIC_VECTOR (7 downto 0); -- zero padded 
           AQUIRED_DATA_O_LOWBYTE: out  STD_LOGIC_VECTOR (7 downto 0);
           AQUIRED_DATA_CH2_O : out  STD_LOGIC_VECTOR (13 downto 0);
           AQUIRED_DATA_CH2_O_HIGHBYTE : out  STD_LOGIC_VECTOR (7 downto 0); -- zero padded 
           AQUIRED_DATA_CH2_O_LOWBYTE: out  STD_LOGIC_VECTOR (7 downto 0);
           AQUIRED_DATA_READY_INTERRUPT_O : out  STD_LOGIC;
           AQUIRE_SINGLE_DATA_FRAME_I : in  STD_LOGIC;
           AQUIRE_DATA_STREAM_I : in  STD_LOGIC;
		   DEBUG_O : out  STD_LOGIC_VECTOR (7 downto 0));
end adc_controller;
--+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
architecture Behavioral of adc_controller is
--+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	type state_singleDataMode_type is (st_wait,  
						st2_read0, st2_read1, st2_read2, st2_read3, st2_read4, st2_read5, st2_read6, 
						st2_read7, st2_read8, st2_read9, st2_read10, st2_read11, st2_read12, st2_read13, 
						st3_wait,
						st3_read0, st3_read1, st3_read2, st3_read3, st3_read4, st3_read5, st3_read6, 
						st3_read7, st3_read8, st3_read9, st3_read10, st3_read11, st3_read12, st3_read13,
						st4_readDone); 
	signal state_singleDataMode, next_state_singleDataMode : state_singleDataMode_type := st_wait; 
	
	type state_type0 is ( st_wait,
					 st_singleDataMode0,st_singleDataMode1,st_singleDataMode2,st_singleDataMode3
					 ,st_singleDataMode4,st_singleDataMode5
					, st_dataStreamMode,st_Finish); 
	signal state0, next_state0 : state_type0 := st_wait;  
   
   
	--signal ad_clock : std_logic := '0';
	signal enableSingleDataMode, enableDataStreamMode,resetSingleDataFrameProcess,resetDataStreamProcess : std_logic := '0';
	signal ReceivedData0,ReceivedData1    : std_logic_vector(13 downto 0); -- receive register  
	signal singleDataFrameProcessReady,dataStreamProcessReady : std_logic := '0';
--+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
begin
-- --------------------------------------------------------------------


	AD_CLK_O <= CLOCK ;
-- --------------------------------------------------------------------

 
	SYNC_PROC: process (CLOCK,RESET)
	begin
		if(RESET = '1') then
			state0 <= st_wait;
		elsif (CLOCK'event and CLOCK = '1') then
			state0 <= next_state0;     
		end if;
	end process;

 
   NEXT_STATE_DECODE: process (state0 ,AQUIRE_SINGLE_DATA_FRAME_I,AQUIRE_DATA_STREAM_I,
								singleDataFrameProcessReady,dataStreamProcessReady) -- inputs also - sush
   begin
      --declare default state for next_state to avoid latches
      next_state0 <= state0;  --default is to stay in current state
      AQUIRED_DATA_READY_INTERRUPT_O <= '0';
	  AD_CONV_O <= '0';
	  enableSingleDataMode <= '0';
	  enableDataStreamMode <= '0';
	  resetSingleDataFrameProcess <= '0';
	  resetDataStreamProcess <= '0';
	  DEBUG_O(3 downto 0) <= x"0";
      case (state0) is
         when st_wait =>
			if (AQUIRE_SINGLE_DATA_FRAME_I) = '1' then
				next_state0 <= st_singleDataMode0;
				resetSingleDataFrameProcess <= '1';
				AD_CONV_O <= '1';
			elsif(AQUIRE_DATA_STREAM_I = '1') then
				next_state0 <= st_dataStreamMode;
			end if;
         
         
         when st_singleDataMode0 =>
			resetSingleDataFrameProcess <= '1';
			AD_CONV_O <= '1';
			next_state0 <= st_singleDataMode1;
         when st_singleDataMode1 =>
			enableSingleDataMode <= '1' ;
			if(singleDataFrameProcessReady = '1') then
				next_state0 <= st_Finish;
			end if;
			DEBUG_O(3 downto 0) <= x"3";
        
         
         when st_dataStreamMode =>
			enableDataStreamMode <= '1' ;
			if(dataStreamProcessReady = '1') then
				next_state0 <= st_Finish;
			end if;
         
         
         when st_Finish =>
			AQUIRED_DATA_READY_INTERRUPT_O <= '1' ;
			next_state0 <= st_wait;
			DEBUG_O(3 downto 0) <= x"5";
         when others =>
            next_state0 <= st_wait;
			DEBUG_O(3 downto 0) <= x"6";
      end case;      
   end process;

OUTPUT_SYNC_DECODE: process (CLOCK) -- inputs also - sush
   begin
	if (CLOCK'event and CLOCK = '1') then
	case (state0) is
        when st_wait =>
         	null;
         
         
        when st_singleDataMode0 =>
         	null;
        when st_singleDataMode1 =>
			if(singleDataFrameProcessReady = '1') then
				AQUIRED_DATA_O <= ReceivedData0;
				AQUIRED_DATA_O_HIGHBYTE(7 downto 6) <= b"00"; 
				AQUIRED_DATA_O_HIGHBYTE(5 downto 0) <= ReceivedData0(13 downto 8);
				AQUIRED_DATA_O_LOWBYTE <= ReceivedData0(7 downto 0);AQUIRED_DATA_CH2_O <= ReceivedData1;
				AQUIRED_DATA_CH2_O_HIGHBYTE(7 downto 6) <= b"00"; 
				AQUIRED_DATA_CH2_O_HIGHBYTE(5 downto 0) <= ReceivedData1(13 downto 8);
				AQUIRED_DATA_CH2_O_LOWBYTE <= ReceivedData1(7 downto 0);
			end if;

        when st_dataStreamMode =>
         	null;
         
        when st_Finish =>
         	null;
        when others =>
         	null;
	end case; 
	end if;    
end process;




-- --------------------------------------------------------------------

	SingleDataFrameProcess : process(CLOCK)
	variable BitPos : INTEGER range 0 to 33;   -- Position of the bit in the frame
	begin
		if Rising_Edge(CLOCK) then
		
			DEBUG_O(7 downto 4) <= x"1";
			singleDataFrameProcessReady <= '0'; 
			if (resetSingleDataFrameProcess = '1') then
				BitPos := 0;
			elsif enableSingleDataMode = '1' then
				singleDataFrameProcessReady <= '0';
				if(BitPos = 0) then
					 ReceivedData0(13) <= NOT ADC_OUT_I; -- Deserialisation
				elsif(BitPos >= 0 AND BitPos <= 13) then
					ReceivedData0(13 - BitPos) <= ADC_OUT_I; -- Deserialisation
				
				elsif(BitPos = 16) then
					ReceivedData1(13) <= NOT ADC_OUT_I; -- Deserialisation
				elsif(BitPos >= 17 AND BitPos <= 28) then
					ReceivedData1(29 - BitPos) <= ADC_OUT_I; -- Deserialisation
				elsif(BitPos = 29) then
					ReceivedData1(29 - BitPos) <= ADC_OUT_I; -- Deserialisation
					singleDataFrameProcessReady <= '1';
				end if;
				
				BitPos := BitPos + 1;
				

			end if;
		end if;
	end process;


end Behavioral;

-- --------------------------------------------------------------------