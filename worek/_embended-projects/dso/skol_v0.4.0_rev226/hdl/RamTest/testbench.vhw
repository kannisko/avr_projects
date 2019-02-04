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
-- /___/   /\     Timestamp : Wed Oct 29 14:44:34 2008
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

    COMPONENT top
        PORT (
            CLK_50M : In std_logic;
            BTN_NORTH : In std_logic;
            LED_0 : Out std_logic;
            LED_1 : Out std_logic;
            LED_2 : Out std_logic;
            LED_3 : Out std_logic;
            LED_4 : Out std_logic;
            LED_5 : Out std_logic;
            LED_6 : Out std_logic;
            LED_7 : Out std_logic
        );
    END COMPONENT;

    SIGNAL CLK_50M : std_logic := '0';
    SIGNAL BTN_NORTH : std_logic := '0';
    SIGNAL LED_0 : std_logic := '0';
    SIGNAL LED_1 : std_logic := '0';
    SIGNAL LED_2 : std_logic := '0';
    SIGNAL LED_3 : std_logic := '0';
    SIGNAL LED_4 : std_logic := '0';
    SIGNAL LED_5 : std_logic := '0';
    SIGNAL LED_6 : std_logic := '0';
    SIGNAL LED_7 : std_logic := '0';

    constant PERIOD : time := 200 ns;
    constant DUTY_CYCLE : real := 0.5;
    constant OFFSET : time := 100 ns;

    BEGIN
        UUT : top
        PORT MAP (
            CLK_50M => CLK_50M,
            BTN_NORTH => BTN_NORTH,
            LED_0 => LED_0,
            LED_1 => LED_1,
            LED_2 => LED_2,
            LED_3 => LED_3,
            LED_4 => LED_4,
            LED_5 => LED_5,
            LED_6 => LED_6,
            LED_7 => LED_7
        );

        PROCESS    -- clock process for CLK_50M
        BEGIN
            WAIT for OFFSET;
            CLOCK_LOOP : LOOP
                CLK_50M <= '0';
                WAIT FOR (PERIOD - (PERIOD * DUTY_CYCLE));
                CLK_50M <= '1';
                WAIT FOR (PERIOD * DUTY_CYCLE);
            END LOOP CLOCK_LOOP;
        END PROCESS;

        PROCESS
            BEGIN
                -- -------------  Current Time:  385ns
                WAIT FOR 385 ns;
                BTN_NORTH <= '1';
                -- -------------------------------------
                -- -------------  Current Time:  585ns
                WAIT FOR 200 ns;
                BTN_NORTH <= '0';
                -- -------------------------------------
                WAIT FOR 9615 ns;

            END PROCESS;

    END testbench_arch;

