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
-- Create Date:    11:30:00 10/29/2008 
-- Design Name: 
-- Module Name:    sram - Behavioral 
-- Project Name: 
-- Target Devices: 
-- Tool versions: 
-- Description: 
--
-- Dependencies: 
--
-- Revision: 
-- Revision 0.01 - File Created
-- Revision 1.00 - Released
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

entity sram is
generic(dataWidth:			integer:=4;
		dataDepth:			integer:=4;
		addressBusWidth:	integer:=2);
port(CLOCK:			in std_logic;	
	ENABLE_I:		in std_logic;
	READ_I:			in std_logic;
	WRITE_I:		in std_logic;
	READ_ADDR_I:	in std_logic_vector(addressBusWidth-1 downto 0);
	WRITE_ADDR_I: 	in std_logic_vector(addressBusWidth-1 downto 0); 
	DATA_IN_I: 		in std_logic_vector(dataWidth-1 downto 0);
	DATA_OUT_O: 	out std_logic_vector(dataWidth-1 downto 0)
);

end sram;

architecture Behavioral of sram is


type ramType is array (0 to dataDepth-1) of std_logic_vector(dataWidth-1 downto 0);
signal tmpRam: ramType;

begin	
			   
    -- READ
    process(CLOCK, READ_I)
    begin
	if (CLOCK'event and CLOCK='1') then
	    if ENABLE_I='1' then
		if READ_I='1' then
		    DATA_OUT_O <= tmpRam(conv_integer(READ_ADDR_I)); 
		else
		    DATA_OUT_O <= (DATA_OUT_O'range => 'Z');
		end if;
	    end if;
	end if;
    end process;
	
    -- WRITE
    process(CLOCK, WRITE_I)
    begin
	if (CLOCK'event and CLOCK='1') then
	    if ENABLE_I='1' then
		if WRITE_I='1' then
		    tmpRam(conv_integer(WRITE_ADDR_I)) <= DATA_IN_I;
		end if;
	    end if;
	end if;
    end process;



end Behavioral;

