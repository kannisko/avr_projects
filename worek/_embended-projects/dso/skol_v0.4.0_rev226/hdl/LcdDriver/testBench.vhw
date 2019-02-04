--------------------------------------------------------------------------------
-- Copyright (c) 1995-2007 Xilinx, Inc.
-- All Right Reserved.
--------------------------------------------------------------------------------
--   ____  ____ 
--  /   /\/   / 
-- /___/  \  /    Vendor: Xilinx 
-- \   \   \/     Version : 9.2.04i
--  \   \         Application : ISE
--  /   /         Filename : testbench.vhw
-- /___/   /\     Timestamp : Sun Nov 02 16:12:32 2008
-- \   \  /  \ 
--  \___\/\___\ 
--
--Command: 
--Design Name: testbench
--Device: Xilinx
--

library IEEE;
use IEEE.STD_LOGIC_1164.ALL;
use IEEE.STD_LOGIC_ARITH.ALL;
use IEEE.STD_LOGIC_UNSIGNED.ALL;
USE IEEE.STD_LOGIC_TEXTIO.ALL;
USE STD.TEXTIO.ALL;

ENTITY testbench IS
END testbench;

ARCHITECTURE testbench_arch OF testbench IS
    FILE RESULTS: TEXT OPEN WRITE_MODE IS "results.txt";

    COMPONENT lcd_byte_driver
        PORT (
            CLOCK : In std_logic;
            RESET : In std_logic;
            ENABLE : In std_logic;
            LCD_DB_0 : Out std_logic;
            LCD_DB_1 : Out std_logic;
            LCD_DB_2 : Out std_logic;
            LCD_DB_3 : Out std_logic;
            LCD_DB_4 : Out std_logic;
            LCD_DB_5 : Out std_logic;
            LCD_DB_6 : Out std_logic;
            LCD_DB_7 : Out std_logic;
            LCD_E : Out std_logic;
            LCD_RS : Out std_logic;
            LCD_RW : Out std_logic;
            init_done_o : Out std_logic;
            dataByte_i : In std_logic_vector (7 DownTo 0);
            writeIt_i : In std_logic;
            writeDataDone_o : Out std_logic
        );
    END COMPONENT;

    SIGNAL CLOCK : std_logic := '0';
    SIGNAL RESET : std_logic := '0';
    SIGNAL ENABLE : std_logic := '0';
    SIGNAL LCD_DB_0 : std_logic := '0';
    SIGNAL LCD_DB_1 : std_logic := '0';
    SIGNAL LCD_DB_2 : std_logic := '0';
    SIGNAL LCD_DB_3 : std_logic := '0';
    SIGNAL LCD_DB_4 : std_logic := '0';
    SIGNAL LCD_DB_5 : std_logic := '0';
    SIGNAL LCD_DB_6 : std_logic := '0';
    SIGNAL LCD_DB_7 : std_logic := '0';
    SIGNAL LCD_E : std_logic := '0';
    SIGNAL LCD_RS : std_logic := '0';
    SIGNAL LCD_RW : std_logic := '0';
    SIGNAL init_done_o : std_logic := '0';
    SIGNAL dataByte_i : std_logic_vector (7 DownTo 0) := "00000000";
    SIGNAL writeIt_i : std_logic := '0';
    SIGNAL writeDataDone_o : std_logic := '0';

    constant PERIOD : time := 20 us;
    constant DUTY_CYCLE : real := 0.5;
    constant OFFSET : time := 10 us;

    BEGIN
        UUT : lcd_byte_driver
        PORT MAP (
            CLOCK => CLOCK,
            RESET => RESET,
            ENABLE => ENABLE,
            LCD_DB_0 => LCD_DB_0,
            LCD_DB_1 => LCD_DB_1,
            LCD_DB_2 => LCD_DB_2,
            LCD_DB_3 => LCD_DB_3,
            LCD_DB_4 => LCD_DB_4,
            LCD_DB_5 => LCD_DB_5,
            LCD_DB_6 => LCD_DB_6,
            LCD_DB_7 => LCD_DB_7,
            LCD_E => LCD_E,
            LCD_RS => LCD_RS,
            LCD_RW => LCD_RW,
            init_done_o => init_done_o,
            dataByte_i => dataByte_i,
            writeIt_i => writeIt_i,
            writeDataDone_o => writeDataDone_o
        );

        PROCESS    -- clock process for CLOCK
        BEGIN
            WAIT for OFFSET;
            CLOCK_LOOP : LOOP
                CLOCK <= '0';
                WAIT FOR (PERIOD - (PERIOD * DUTY_CYCLE));
                CLOCK <= '1';
                WAIT FOR (PERIOD * DUTY_CYCLE);
            END LOOP CLOCK_LOOP;
        END PROCESS;

        PROCESS
            BEGIN
                -- -------------  Current Time:  139us
                WAIT FOR 139 us;
                ENABLE <= '1';
                -- -------------------------------------
                -- -------------  Current Time:  199us
                WAIT FOR 60 us;
                RESET <= '1';
                -- -------------------------------------
                -- -------------  Current Time:  219us
                WAIT FOR 20 us;
                RESET <= '0';
                -- -------------------------------------
                WAIT FOR 9801 us;

            END PROCESS;

    END testbench_arch;

