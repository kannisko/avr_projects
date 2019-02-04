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
-- Create Date:    19:30:01 09/26/2008 
-- Design Name: 
-- Module Name:    timer - Behavioral 
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

entity sushTimer is
	Port ( 	-- LCD
				CLOCK 		: IN  STD_LOGIC;
				RESET_AND_GO: IN  STD_LOGIC; -- must be set to 1 and after releasing timer is starting
				cyclesToWait: IN  STD_LOGIC_VECTOR(31 downto 0);
				-- div
				--ACTIVE		: in  STD_LOGIC;  -- neeeeded / not used now ?
				done			: out  STD_LOGIC);
end sushTimer;

architecture Behavioral of sushTimer is
   type 		state_type is (st1_INIT,st2_RUN, st3_done); 
   signal 	state, next_state : state_type; 
	--constant MHz  : Integer := 50; --> 20ns per cycle
	--constant nanoSecondsPerCycle : Integer :=  ((1/(MHz*1000*1000))*1000*1000*1000);
	signal 	counter 			: STD_LOGIC_VECTOR(31 downto 0);
	signal 	counterReset 	: STD_LOGIC := '0';
	constant simulation : boolean := false;
begin

   SYNC_PROC: process (CLOCK)
   begin
      if (CLOCK'event and CLOCK = '1' ) then
         if (RESET_AND_GO = '1') then
            state <= st1_INIT;
         else
            state <= next_state;
         end if;        
      end if;
   end process;
 
  --MOORE State-Machine - Outputs based on state only
   OUTPUT_DECODE: process (state)
   begin
      if state = st3_DONE then
         done <= '1';
      else
         done <= '0';
      end if;
   end process;
 
 
   NEXT_STATE_DECODE: process (state,CLOCK) is

   begin
      --declare default state for next_state to avoid latches
      next_state <= state;  --default is to stay in current state
      --insert statements to decode next_state
      --below is a simple example
      case (state) is
         when st1_INIT =>
				-- (sec*1000000000)/20 = cycles
				-- 1 sec 0x"02FAF080"
				-- 4 sec 0x"0BEBC200"
				--cyclesToWait <= x"0BEBC200";
				counterReset <= '1';
				
				next_state <= st2_RUN;
         when st2_RUN =>
				counterReset <= '0';
				if(simulation = false) then
					if(counter >= cyclesToWait) then
						next_state <= st3_DONE;
					end if;
				else
					if(counter >= x"00000005") then
						next_state <= st3_DONE;
					end if;
				end if;
         when st3_DONE =>
				null;
         when others =>
            null;
      end case;      
   end process;

	process (CLOCK, counterReset) 
	begin
		if counterReset='1' then 
			counter <= (others => '0');
		elsif CLOCK='1' and CLOCK'event and (state /= st3_DONE) then
			counter <= counter + 1;
		end if;
	end process;

end Behavioral;

