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
-- x"0098" ); -- d00.152
-- x"02F8" ); -- d00.152*5 = 760
-- x"05F0" ); -- d01.520
-- x"3b60" ); -- d15.200
entity top is
generic(adcRamDataWidth:				integer:=14;
		adcRamDataDepth:				std_logic_vector(15 downto 0):= x"05F0";
		adcRamAddressBusWidth:			integer:=16;
		defaultSamplesPerFrameMode01:	std_logic_vector(15 downto 0):= x"02F8";  -- d00.760 = 152*5 
		defaultSamplesPerFrameMode02:	std_logic_vector(15 downto 0):= x"05F0"; -- d01.520 = 152*10
		-- after reset -> this values will be set
		defaultAdcTriggerLevel 	:  		std_logic_vector(15 downto 0):= b"0010000000000000";
		defaultSamplesPerFrameMode00:	std_logic_vector(15 downto 0):= x"0098");  -- d00.152 = 152*1
	Port ( 	CLK_50M 	: in STD_LOGIC;
				E_TX_CLK  : in STD_LOGIC; -- ethernet
				E_RX_CLK  : in STD_LOGIC; -- ethernet
				-- start inserting top.vhd ports here now

				-- RS 232
				RS232_DCE_RXD : in  STD_LOGIC; -- female
				RS232_DCE_TXD : out STD_LOGIC;
				--RS232_DTE_RXD : in  STD_LOGIC; -- male
				--RS232_DTE_TXD : out STD_LOGIC;
				
				-- div
				BTN_NORTH 	: in  STD_LOGIC;
				BTN_EAST 	: in  STD_LOGIC;
				BTN_SOUTH	: in  STD_LOGIC;
				BTN_WEST	: in  STD_LOGIC;
				ROT_CENTER	: in  STD_LOGIC;
--				ROT_A		: in  STD_LOGIC;
--				ROT_B		: in  STD_LOGIC;

				J20_IO 		: out std_logic_vector(3 downto 0);

				-- spi
				SPI_SCK   	: out STD_LOGIC;
				SPI_MOSI   	: out STD_LOGIC;
				--SPI_MISO   	: out STD_LOGIC; -- not used
				
				-- adc preAmp
				AMP_CS   	: out STD_LOGIC;
				AMP_OUT   	: in STD_LOGIC; -- Private MISO for AMP    -- used for debug
				
				-- adc
				ADC_OUT  	: in STD_LOGIC; -- adc't data
				AD_CONV   	: out STD_LOGIC; -- triggers adc
				
				-- LED
				LED   	: out  std_logic_vector(7 downto 0)
				);
end top;

architecture Behavioral of top is



--+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
component SkolUart
port (
     CLOCK 			: in  std_logic;  -- clock
     RST_I 			: in  std_logic;  -- Reset input     
     OPCODE_I 		: in  std_logic_vector(7 downto 0);
     DAT0_I 		: in  std_logic_vector(7 downto 0); -- DataIn Bus
	 DAT1_I 		: in  std_logic_vector(7 downto 0); -- DataIn Bus /those two bytes are interpreted as one 16 bit value !
     DAT_O_OpCode 	: out std_logic_vector(7 downto 0); -- DataOut Bus
	 DAT_O_Data 	: out std_logic_vector(7 downto 0); -- DataOut Bus
     WE_ONEBYTE_I  	: in  std_logic;  -- Write Enable
     WE_TWOBYTES_I  : in  std_logic;  -- Write Enable  / those two bytes are interpreted as one 16 bit value ! 
     IntTx_O  		: out std_logic;  -- Transmit interrupt: is like NOT BUSY
     IntRx_O  		: out std_logic;  -- Receive interrupt: indicate Data received
     TxD_PAD_O		: out std_logic;  -- Tx RS232 Line
     RxD_PAD_I		: in  std_logic;  -- Rx RS232 Line       
	 WE_START_FRAME : in  std_logic;
	 WE_DATA_FRAME 	: in  std_logic;
	 WE_CRC_FRAME 	: in  std_logic;
	 DATA_BLOCK_COUNT_HIGH_I 		: in  std_logic_vector(7 downto 0);
	 DATA_BLOCK_COUNT_LOW_I 		: in  std_logic_vector(7 downto 0);
	 DATA_BLOCK_SIZE_I 				: in  std_logic_vector(7 downto 0);
     debugOut : out std_logic_vector(7 downto 0)
     	);
end component;
--+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
component SPI_initializer
  port (
		CLOCK   		: in  std_logic;  -- clock
		RESET_I 		: in  std_logic;  -- Reset input
		SPI_CLOCK_O  	: out  std_logic;   
		SPI_MOSI_O 	: out  std_logic;
		SPI_MISO_I 	: in   std_logic; -- ampOutput
		AMP_CS_O		: out  std_logic;
		INIT_ADC_DONE_O : out std_logic;
		DEBUG_O		: out std_logic_vector(7 downto 0));     
end component;
--+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
component adc_controller 
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
end component;
--+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

component sram 
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
end component;
component Counter
  generic(COUNT: INTEGER range 0 to 65535); -- Count revolution
  port (
     Clk      : in  std_logic;  -- Clock
     Reset    : in  std_logic;  -- Reset input
     CE       : in  std_logic;  -- Chip Enable
     O        : out std_logic); -- Output  
  end component;
-----Signals +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	
	-- kernel
	type state_type is (st_idle,st_init, st_wait, st_reset,
						st_sendAdcFsmStart); 
	type adc_state_type is (st_adc_reset,st_adc_idle, st_adc_wait,
						st_adc_singleShotCh0_Volt_0,st_adc_singleShotCh0_Volt_1,st_adc_singleShotCh0_Volt_2,st_adc_singleShotCh0_Volt_3,
						st_adc_singleShotCh0_Volt_4, st_adc_writeToRam1, st_adc_writeToRam2,
						st_adc_singleShotCh0_Volt_send0,st_adc_singleShotCh0_Volt_send1,st_adc_singleShotCh0_Volt_send2,
						st_adc_singleShotCh0_Volt_send3,st_adc_singleShotCh0_Volt_send4,
						st_adc_singleShotCh1_Volt_send0,st_adc_singleShotCh1_Volt_send1,st_adc_singleShotCh1_Volt_send2,
						st_adc_singleShotCh1_Volt_send3,st_adc_singleShotCh1_Volt_send4,
						st_adc_aquireAdcData0_FillRam_0,st_adc_aquireAdcData0_FillRam_1,
						st_adc_aquireAdcData0_FillRam_1_armTrigger0,st_adc_aquireAdcData0_FillRam_1_armTrigger1,		
						st_adc_aquireAdcData0_FillRam_2,st_adc_aquireAdcData0_FillRam_3,
						st_adc_aquireAdcData0_readFromRam_sendOnlyCrc0,st_adc_aquireAdcData0_readFromRam_sendOnlyCrc1,st_adc_aquireAdcData0_readFromRam_sendOnlyCrc2,
						st_adc_aquireAdcData0_readFromRam_sendOnlyStart0,st_adc_aquireAdcData0_readFromRam_sendOnlyStart1,st_adc_aquireAdcData0_readFromRam_sendOnlyStart2,
						st_adc_aquireAdcData0_readFromRam_sendOnlyData0,st_adc_aquireAdcData0_readFromRam_sendOnlyData1,st_adc_aquireAdcData0_readFromRam_sendOnlyData2,
						st_adc_aquireAdcData0_readFromRam_sendOnlyData3,st_adc_aquireAdcData0_readFromRam_sendOnlyData4,
						st_adc_aquireAdcData1_readFromRam_sendOnlyCrc0,st_adc_aquireAdcData1_readFromRam_sendOnlyCrc1,st_adc_aquireAdcData1_readFromRam_sendOnlyCrc2,
						st_adc_aquireAdcData1_readFromRam_sendOnlyStart0,st_adc_aquireAdcData1_readFromRam_sendOnlyStart1,st_adc_aquireAdcData1_readFromRam_sendOnlyStart2,
						st_adc_aquireAdcData1_readFromRam_sendOnlyData0,st_adc_aquireAdcData1_readFromRam_sendOnlyData1,st_adc_aquireAdcData1_readFromRam_sendOnlyData2,
						st_adc_aquireAdcData1_readFromRam_sendOnlyData3,st_adc_aquireAdcData1_readFromRam_sendOnlyData4,
						st_adc_setTriggerLevelHighByte, st_adc_setTriggerLevelLowByte, st_adc_setTriggerMode,
						st_adc_decodeOpCode,
						st_adc_setTimeDivideCh0,
						st_adc_getTimeDividerCh0,st_adc_getTimeDividerCh0_0,st_adc_getTimeDividerCh0_1,
						st_adc_setTimeDivideCh1,
						st_adc_getTimeDividerCh1,st_adc_getTimeDividerCh1_0,st_adc_getTimeDividerCh1_1,
						st_adc_getTriggerMode,st_adc_getTriggerMode0,st_adc_getTriggerMode1,
						st_adc_getTriggerLevel,st_adc_getTriggerLevel0,st_adc_getTriggerLevel1,
						st_adc_setSamplesPerFrameMode,
						st_adc_getSamplesPerFrameMode0,st_adc_getSamplesPerFrameMode1,st_adc_getSamplesPerFrameMode2
						); 
	signal state, next_state : state_type := st_reset; 
	signal adc_state, next_adc_state : adc_state_type := st_adc_reset; 
   
	-- skolUart
	Signal skolUartReset_i : std_logic := '0' ;
	Signal skolUartData0_i : std_logic_vector(7 downto 0) := x"00" ;
	Signal skolUartData1_i : std_logic_vector(7 downto 0) := x"00" ;
	Signal skolUartOpCode_o : std_logic_vector(7 downto 0)  ;
	Signal skolUartData_o : std_logic_vector(7 downto 0)  ;
	Signal skolUartWE_OneByte_i : std_logic := '0' ;
	Signal skolUartWE_TwoBytes_i : std_logic := '0' ;
	Signal skolUartInterruptReceivedData_o : std_logic ;
	Signal skolUartInterruptTransmitDataDone_o : std_logic ; 
	Signal skolUartOpcode_i : STD_LOGIC_VECTOR(7 downto 0);
	Signal skolUartdebugOut   : STD_LOGIC_VECTOR(7 downto 0);   
	Signal skolUartWE_StartFrame_i: std_logic := '0' ;            
	Signal skolUartWE_DataFrame_i: std_logic := '0' ;                
	Signal skolUartWE_CrcFrame_i: std_logic := '0' ;                
	Signal skolUart_dataBlockCountHigh_i: STD_LOGIC_VECTOR(7 downto 0);            
	Signal skolUart_dataBlockCountLow_i: STD_LOGIC_VECTOR(7 downto 0);             
	Signal skolUart_dataBlockSize_i: STD_LOGIC_VECTOR(7 downto 0);                 
    Signal skolUartReceivedMsgCounter :    std_logic_vector(7 downto 0) := x"00" ;
       
       
       
       

	-- spi
	signal spi_reset		: std_logic := '0' ;
	signal initAdcDone	: std_logic ;
	signal spiClockForInitAmp : std_logic ;
	Signal debugOutputSpiInitializer		: std_logic_vector(7 downto 0) ;
		
	-- ADC
	Signal adcAquiredDataReadyInterrupt :  std_logic ;
	Signal adcClock :  std_logic ;
	Signal adcAquireSingleDataFrame 		: std_logic := '0';
	Signal adcAquireDataStream		 		: std_logic := '0';
	Signal debugOutputADC					: std_logic_vector(7 downto 0) ;
	Signal adcAquiredData_CH0 					: STD_LOGIC_VECTOR (13 downto 0);
	Signal adcAquiredData_CH0_HighByte,adcAquiredData_CH0_LowByte	: std_logic_vector(7 downto 0) ;
	Signal adcAquiredData_CH1 										: STD_LOGIC_VECTOR (13 downto 0);
	Signal adcAquiredData_CH1_HighByte,adcAquiredData_CH1_LowByte	: std_logic_vector(7 downto 0) ;
	Signal adcReset				: std_logic := '0';
	Signal adcTriggerLevel 		:  STD_LOGIC_VECTOR (15 downto 0) := defaultAdcTriggerLevel;
	Signal adcTriggerAtPosEdge	:  STD_LOGIC:= '1';       
	Signal adcTriggerOn			:  STD_LOGIC:= '1';                  
	Signal adcTriggerArmed 		:  STD_LOGIC:= '0';  
	Signal adcFsmStart : std_logic := '0' ; 
	Signal adcFsmReset : std_logic := '0' ;
	Signal adcTimeDividerCounterCh0   		: STD_LOGIC_VECTOR(23 downto 0) := x"000000";   
	Signal adcTimeDividerConstantCh0  	: STD_LOGIC_VECTOR(23 downto 0) := x"000000";
	Signal adcTimeDividerAsReceivedCh0	: std_logic_vector(7 downto 0) ;
	Signal adcTimeDividerCounterCh1   		: STD_LOGIC_VECTOR(23 downto 0) := x"000000"; 
	Signal adcTimeDividerConstantCh1  	: STD_LOGIC_VECTOR(23 downto 0) := x"000000";
	Signal adcTimeDividerAsReceivedCh1	: std_logic_vector(7 downto 0) ;
	Signal adcAquiredDataTmp0, adcAquiredDataTmp1 : STD_LOGIC_VECTOR (13 downto 0);
	Signal adcSamplesPerFrameModeReceiveMode	: std_logic_vector(7 downto 0) ;	
	Signal adcCyclesTempCounter		: std_logic_vector(adcRamDataWidth-1 downto 0);
		
		
	-- adcRam
	Signal adcRamEnableI_ch0 							: STD_LOGIC := '1' ;
	Signal adcRamReadI_ch0		, adcRamWriteI_ch0 		: STD_LOGIC := '1' ;
	Signal adcRamReadAddrI_ch0	, adcRamWriteAddrI_ch0 	: std_logic_vector(adcRamAddressBusWidth-1 downto 0);
	Signal adcRamDataInI_ch0	, adcRamDataOutO_ch0 	: std_logic_vector(adcRamDataWidth-1 downto 0);
	Signal adcRamAddrCounterCh0 : std_logic_vector(adcRamAddressBusWidth-1 downto 0) := (others => '0');		
	Signal adcRamEnableI_ch1 							: STD_LOGIC := '1' ;
	Signal adcRamReadI_ch1		, adcRamWriteI_ch1 		: STD_LOGIC := '1' ;
	Signal adcRamReadAddrI_ch1	, adcRamWriteAddrI_ch1 	: std_logic_vector(adcRamAddressBusWidth-1 downto 0);
	Signal adcRamDataInI_ch1	, adcRamDataOutO_ch1 	: std_logic_vector(adcRamDataWidth-1 downto 0);
	Signal adcRamAddrCounterCh1 : std_logic_vector(adcRamAddressBusWidth-1 downto 0) := (others => '0');
	Signal adcSamplesPerFrame	:	std_logic_vector(15 downto 0) := defaultSamplesPerFrameMode00;

		
	signal Sig0   : std_logic;  -- gnd signal
  	signal Sig1   : std_logic;  -- vcc signal  
  	signal tempOutSignal : std_logic; 
  	signal led7Tmp : std_logic; 


---keep in sync with opcodes.java
	CONSTANT CONST_setTimeDividerCh1       		: STD_LOGIC_VECTOR(7 downto 0) := x"00";
    CONSTANT CONST_setTimeDividerCh0       		: STD_LOGIC_VECTOR(7 downto 0) := x"01"; 
    CONSTANT CONST_setTriggerMode				: STD_LOGIC_VECTOR(7 downto 0) := x"02"; 
    CONSTANT CONST_setSamplesPerFrameMode		: STD_LOGIC_VECTOR(7 downto 0) := x"03";  
    CONSTANT CONST_setTriggerLevelHighByte		: STD_LOGIC_VECTOR(7 downto 0) := x"04";  
    CONSTANT CONST_setTriggerLevelLowByte		: STD_LOGIC_VECTOR(7 downto 0) := x"05"; 
    	    	
    CONSTANT CONST_getTimeDividerCh0      		: STD_LOGIC_VECTOR(7 downto 0) := x"B1";
    CONSTANT CONST_getTimeDividerCh1      		: STD_LOGIC_VECTOR(7 downto 0) := x"B2";
	CONSTANT CONST_dataAquCh0     				: STD_LOGIC_VECTOR(7 downto 0) := x"BE"; 
	CONSTANT CONST_dataAquCh1     				: STD_LOGIC_VECTOR(7 downto 0) := x"B3";   
    CONSTANT CONST_singleVoltValueCh0     		: STD_LOGIC_VECTOR(7 downto 0) := x"B4";   
    CONSTANT CONST_singleVoltValueCh1    		: STD_LOGIC_VECTOR(7 downto 0) := x"B5"; 

    CONSTANT CONST_getTriggerLevel  			: STD_LOGIC_VECTOR(7 downto 0) := x"B8";
    CONSTANT CONST_getSamplesPerFrameMode      	: STD_LOGIC_VECTOR(7 downto 0) := x"B9";
    CONSTANT CONST_getTriggerMode   			: STD_LOGIC_VECTOR(7 downto 0) := x"B0";	
    	
    	
---keep in sync with constants.java
    CONSTANT CONST_TriggerOff  						: STD_LOGIC_VECTOR(7 downto 0) := x"01";
    CONSTANT CONST_TriggerPosEdge  					: STD_LOGIC_VECTOR(7 downto 0) := x"02";
    CONSTANT CONST_TriggerNegEdge  					: STD_LOGIC_VECTOR(7 downto 0) := x"04";
    
    CONSTANT CONST_SamplesPerFrameMode00  			: STD_LOGIC_VECTOR(7 downto 0) := x"01";
    CONSTANT CONST_SamplesPerFrameMode01  			: STD_LOGIC_VECTOR(7 downto 0) := x"02";
    CONSTANT CONST_SamplesPerFrameMode02  			: STD_LOGIC_VECTOR(7 downto 0) := x"04";
    	
--+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
begin

-- --------------------------------------------------------------------

  
MySkolUart: SkolUart
port map(
     CLOCK => CLK_50M,
     RST_I => skolUartReset_i,
     OPCODE_I => skolUartOpcode_i,
     DAT0_I => skolUartData0_i, -- DataIn Bus
     DAT1_I => skolUartData1_i,
     DAT_O_OpCode => skolUartOpCode_o, -- DataOut Bus
	 DAT_O_Data 	 => skolUartData_o, -- DataOut Bus
	 WE_ONEBYTE_I    => skolUartWE_OneByte_i,
	 WE_TWOBYTES_I   => skolUartWE_TwoBytes_i,
-- process signals     
     IntTx_O => skolUartInterruptTransmitDataDone_o,  -- Transmit interrupt: ????
     IntRx_O  => skolUartInterruptReceivedData_o,  -- Receive interrupt: indicate Data received
     TxD_PAD_O => RS232_DCE_TXD, -- Tx RS232 Line
     RxD_PAD_I => RS232_DCE_RXD,  -- Rx RS232 Line        
	 WE_START_FRAME 		 => skolUartWE_StartFrame_i,
	 WE_DATA_FRAME 			 => skolUartWE_DataFrame_i,
	 WE_CRC_FRAME 			 => skolUartWE_CrcFrame_i,
	 DATA_BLOCK_COUNT_HIGH_I => skolUart_dataBlockCountHigh_i,
	 DATA_BLOCK_COUNT_LOW_I  => skolUart_dataBlockCountLow_i,
	 DATA_BLOCK_SIZE_I 		 => skolUart_dataBlockSize_i,
     debugOut 				 => skolUartdebugOut
     );
       
-- -- ----------------------------------------------------------------------------------

 MySpiAdcInitializer : SPI_initializer 
  port map(
		CLOCK   		=> CLK_50M,  -- clock
		RESET_I 		=> spi_reset,  -- Reset input
		SPI_CLOCK_O  	=> spiClockForInitAmp,  
		SPI_MOSI_O 	=> SPI_MOSI,
		SPI_MISO_I	=> AMP_OUT,
		AMP_CS_O		=> AMP_CS,
		INIT_ADC_DONE_O => initAdcDone,
		DEBUG_O		=> debugOutputSpiInitializer);  

-- -- ----------------------------------------------------------------------------------
MyAdcController : adc_controller 
    Port map( CLOCK => CLK_50M,  -- clock
    	   RESET 	=> adcReset,
           AD_CONV_O => AD_CONV,
           AD_CLK_O	=> adcClock,
           ADC_OUT_I => ADC_OUT,
           AQUIRED_DATA_O => adcAquiredData_CH0,
           AQUIRED_DATA_O_HIGHBYTE => adcAquiredData_CH0_HighByte,
           AQUIRED_DATA_O_LOWBYTE => adcAquiredData_CH0_LowByte,
           AQUIRED_DATA_CH2_O => adcAquiredData_CH1,
           AQUIRED_DATA_CH2_O_HIGHBYTE => adcAquiredData_CH1_HighByte,
           AQUIRED_DATA_CH2_O_LOWBYTE => adcAquiredData_CH1_LowByte,
           AQUIRED_DATA_READY_INTERRUPT_O => adcAquiredDataReadyInterrupt,
           AQUIRE_SINGLE_DATA_FRAME_I => adcAquireSingleDataFrame,
           AQUIRE_DATA_STREAM_I => adcAquireDataStream,
		   DEBUG_O	=> debugOutputADC);
-- -- ----------------------------------------------------------------------------------

MyAdcRam0 : sram 
generic map (	dataWidth 		=> adcRamDataWidth,
				dataDepth 		=> conv_integer(adcRamDataDepth),
				addressBusWidth => adcRamAddressBusWidth)
port map (CLOCK 	=> CLK_50M,  -- clock
	ENABLE_I 		=> adcRamEnableI_ch0,
	READ_I 			=> adcRamReadI_ch0,
	WRITE_I 		=> adcRamWriteI_ch0,
	READ_ADDR_I 	=> adcRamReadAddrI_ch0,	
	WRITE_ADDR_I  	=> adcRamWriteAddrI_ch0,
	DATA_IN_I  		=> adcRamDataInI_ch0,	
	DATA_OUT_O  	=> adcRamDataOutO_ch0	
	);
MyAdcRam1 : sram
generic map (	dataWidth 		=> adcRamDataWidth,
				dataDepth 		=> conv_integer(adcRamDataDepth),
				addressBusWidth => adcRamAddressBusWidth)
port map (CLOCK 	=> CLK_50M,  -- clock
	ENABLE_I 		=> adcRamEnableI_ch1,
	READ_I 			=> adcRamReadI_ch1,
	WRITE_I 		=> adcRamWriteI_ch1,
	READ_ADDR_I 	=> adcRamReadAddrI_ch1,	
	WRITE_ADDR_I  	=> adcRamWriteAddrI_ch1,
	DATA_IN_I  		=> adcRamDataInI_ch1,	
	DATA_OUT_O  	=> adcRamDataOutO_ch1	
	);
-- -- ----------------------------------------------------------------------------------
	
-- outputFrequency = BaseFrequencyInput / (2*COUNT)
-- 	25kHz = 50 Mhz / (2*1000)
outputGenerator : Counter 
     generic map (COUNT => 1000) 
     port map (CLK_50M, sig0, sig1, tempOutSignal); 

-- -- ----------------------------------------------------------------------------------
	CONTROLLER_SYNC_PROC: process (CLK_50M,BTN_NORTH,skolUartInterruptReceivedData_o)
	begin
		if (BTN_NORTH = '1') then
			state <= st_reset;
		elsif(CLK_50M'event and CLK_50M = '1') then
			state <= next_state;
		end if;
	end process;
 

 
	CONTROLLER_NEXT_STATE_DECODE: process (state,
											initAdcDone,skolUartInterruptReceivedData_o,skolUartOpCode_o)

	
	begin
	next_state <= state;
		case (state) is
			when st_idle =>
				null;    
			when st_reset =>
				next_state <= st_init;
			when st_init =>
				if(initAdcDone = '1') then 
					next_state <= st_wait;
				end if;
			when st_wait => 
				if(skolUartInterruptReceivedData_o= '1') then
					if(skolUartOpCode_o = x"FF") then 
						next_state <= st_reset;
					else
						next_state <= st_sendAdcFsmStart;
					end if;	
				end if;
			when st_sendAdcFsmStart =>
				next_state <= st_wait;
			when others =>
				next_state <= st_idle;
		end case;  

	end process;
	



	CONTROLLER_SYNCHRONOUS_PROCESS: process (CLK_50M)
	begin
		if(CLK_50M'event and CLK_50M = '1') then
			spi_reset 		<= '0';
			skolUartReset_i <= '0';
			adcReset		<= '0';
			adcFsmReset		<= '0';
			adcFsmStart 	<= '0';

			--adcRamReadI_ch0 			<= '0' ;

			case (state) is
				when st_reset =>
					spi_reset 		<= '1';
					skolUartReset_i <= '1';
					adcReset		<= '1';
					adcFsmReset		<= '1';						
					skolUartReceivedMsgCounter  <= x"00";
					
				when st_wait => 
					null;	
						
				when st_sendAdcFsmStart =>
					adcFsmStart <= '1';
					skolUartReceivedMsgCounter  <= skolUartReceivedMsgCounter + 1 ;

				-- ---------------------------------------	
				when others =>
					null;
			end case;  
				
			--ledTempOutput1(7 downto 4) <=	adcRamAddrCounter1;
		end if;
	end process;
-- -------------------------------------------------------------


-- -- ----------------------------------------------------------------------------------
	ADC_SYNC_NEXT_STATE_AND_RESET_PROC: process (CLK_50M,adcFsmReset)
	begin
		if (adcFsmReset = '1') then
			adc_State <= st_adc_reset;
--		elsif (adcFsmStart = '1') then
--			adc_State <= st_adc_wait;
		elsif(CLK_50M'event and CLK_50M = '1') then
			adc_State <= next_adc_State;
		end if;
	end process;
 

 
	ADC_NEXT_STATE_DECODE: process (adc_State,
											initAdcDone,skolUartInterruptReceivedData_o,
											skolUartInterruptTransmitDataDone_o,
											adcAquiredDataReadyInterrupt,
											skolUartOpCode_o,
											adcRamAddrCounterCh0, adcTriggerAtPosEdge, 
											adcTriggerArmed, adcAquiredData_CH0, adcTriggerLevel,
											adcRamAddrCounterCh1,
											adcSamplesPerFrame, adcTriggerOn,adcFsmStart)

	
	begin
		next_adc_State <= adc_State;
		case (adc_State) is
			when st_adc_reset =>
				next_adc_state <= st_adc_idle;
				
			when st_adc_idle =>
				if (adcFsmStart = '1') then
					next_adc_state <= 	 st_adc_wait;
				end if;

			when st_adc_wait => 


					if(skolUartOpCode_o = 	 CONST_singleVoltValueCh0) then 
						next_adc_state <= 	 st_adc_singleShotCh0_Volt_0;
--					elsif(skolUartOpCode_o = CONST_singleFrameCh0) then 
--						next_adc_state 	<= 	 st_adc_singleFrame_fillRam0;
					elsif(skolUartOpCode_o = CONST_dataAquCh0   ) then 
						next_adc_state <= 	 st_adc_aquireAdcData0_FillRam_0;
					elsif(skolUartOpCode_o = CONST_setTriggerLevelHighByte) then 
						next_adc_state <= 	 st_adc_setTriggerLevelHighByte;
					elsif(skolUartOpCode_o = CONST_setTriggerLevelLowByte) then 
						next_adc_state <= 	 st_adc_setTriggerLevelLowByte;
					elsif(skolUartOpCode_o = CONST_setTriggerMode  ) then
						next_adc_state <= 	 st_adc_setTriggerMode;
					elsif(skolUartOpCode_o = CONST_setTimeDividerCh0   ) then
						next_adc_state <= 	 st_adc_setTimeDivideCh0;
					elsif(skolUartOpCode_o = CONST_setTimeDividerCh1   ) then
						next_adc_state <= 	 st_adc_setTimeDivideCh1;
					elsif(skolUartOpCode_o = CONST_setSamplesPerFrameMode  ) then
						next_adc_state <= 	 st_adc_setSamplesPerFrameMode;
						
					elsif(skolUartOpCode_o = CONST_getTimeDividerCh0   ) then
						next_adc_state <= 	 st_adc_getTimeDividerCh0;
					elsif(skolUartOpCode_o = CONST_getTimeDividerCh1   ) then
						next_adc_state <= 	 st_adc_getTimeDividerCh1;
					elsif(skolUartOpCode_o = CONST_getTriggerMode   ) then
						next_adc_state <= 	 st_adc_getTriggerMode;
					elsif(skolUartOpCode_o = CONST_getTriggerLevel  ) then
						next_adc_state <= 	 st_adc_getTriggerLevel;
					elsif(skolUartOpCode_o = CONST_getSamplesPerFrameMode ) then
						next_adc_state <= 	 st_adc_getSamplesPerFrameMode0;
					else
						next_adc_state <= st_adc_wait;
					end if;	
						

			when st_adc_getTimeDividerCh0 =>
				next_adc_state <= st_adc_getTimeDividerCh0_0;
			when st_adc_getTimeDividerCh0_0 =>
				next_adc_state <= st_adc_getTimeDividerCh0_1;
			when st_adc_getTimeDividerCh0_1 =>
				next_adc_state <= st_adc_idle;
				
			when st_adc_getTimeDividerCh1 =>
				next_adc_state <= st_adc_getTimeDividerCh1_0;
			when st_adc_getTimeDividerCh1_0 =>
				next_adc_state <= st_adc_getTimeDividerCh1_1;
			when st_adc_getTimeDividerCh1_1 =>
				next_adc_state <= st_adc_idle;
				
				
			when st_adc_getTriggerMode =>
				next_adc_state <= st_adc_getTriggerMode0;
			when st_adc_getTriggerMode0 =>
				next_adc_state <= st_adc_getTriggerMode1;
			when st_adc_getTriggerMode1 =>
				next_adc_state <= st_adc_idle;
				
				
			when st_adc_getTriggerLevel =>
				next_adc_state <= st_adc_getTriggerLevel0;
			when st_adc_getTriggerLevel0 =>
				next_adc_state <= st_adc_getTriggerLevel1;
			when st_adc_getTriggerLevel1 =>
				next_adc_state <= st_adc_idle;
				

			--	------------------------------------------	
							
			when st_adc_getSamplesPerFrameMode0=>
				next_adc_state <= st_adc_getSamplesPerFrameMode1;
			when st_adc_getSamplesPerFrameMode1 =>
				next_adc_state <= st_adc_getSamplesPerFrameMode2;
			when st_adc_getSamplesPerFrameMode2 =>
				next_adc_state <= st_adc_idle;
				

			--	------------------------------------------	
			

			when st_adc_setTimeDivideCh0 =>
				next_adc_state <=st_adc_idle ;
				
			when st_adc_setTimeDivideCh1 =>
				next_adc_state <=st_adc_idle ;
				
			when st_adc_setSamplesPerFrameMode =>
				next_adc_state <=st_adc_idle ;
			--	------------------------------------------	
			-- 2 reads because first read aquires old value - see spartan starter kit docu
			when st_adc_singleShotCh0_Volt_0 => 
				next_adc_state <= st_adc_singleShotCh0_Volt_1;
			when st_adc_singleShotCh0_Volt_1 => 
				if(adcAquiredDataReadyInterrupt = '1') then
					next_adc_state <= st_adc_singleShotCh0_Volt_2;
				end if;
			when st_adc_singleShotCh0_Volt_2 => 
				next_adc_state <= st_adc_singleShotCh0_Volt_3;
			when st_adc_singleShotCh0_Volt_3 => 
				if(adcAquiredDataReadyInterrupt = '1') then
					next_adc_state <= st_adc_singleShotCh0_Volt_4;
				end if;
			when st_adc_singleShotCh0_Volt_4 =>
				next_adc_state <= st_adc_singleShotCh0_Volt_send0;
							
			when st_adc_singleShotCh0_Volt_send0 =>
				next_adc_state <=st_adc_singleShotCh0_Volt_send1 ;
			when st_adc_singleShotCh0_Volt_send1 =>
				next_adc_state <=st_adc_singleShotCh0_Volt_send2 ;
  			when st_adc_singleShotCh0_Volt_send2 =>
				next_adc_state <=st_adc_singleShotCh0_Volt_send3 ;
			when st_adc_singleShotCh0_Volt_send3 =>
				next_adc_state <=st_adc_singleShotCh0_Volt_send4 ;
			when st_adc_singleShotCh0_Volt_send4 =>
				if(skolUartInterruptTransmitDataDone_o ='1') then
					next_adc_state <=st_adc_singleShotCh1_Volt_send0 ;
				end if;	
			when st_adc_singleShotCh1_Volt_send0 =>
				next_adc_state <=st_adc_singleShotCh1_Volt_send1 ;
			when st_adc_singleShotCh1_Volt_send1 =>
				next_adc_state <=st_adc_singleShotCh1_Volt_send2 ;
  			when st_adc_singleShotCh1_Volt_send2 =>
				next_adc_state <=st_adc_singleShotCh1_Volt_send3 ;
			when st_adc_singleShotCh1_Volt_send3 =>
				next_adc_state <=st_adc_singleShotCh1_Volt_send4 ;
			when st_adc_singleShotCh1_Volt_send4 =>
				if(skolUartInterruptTransmitDataDone_o ='1') then
					next_adc_state <=st_adc_idle ;
				end if;
			
			--	------------------------------------------	
			when st_adc_aquireAdcData0_FillRam_0 => 
					next_adc_state <= st_adc_aquireAdcData0_FillRam_1;
			when st_adc_aquireAdcData0_FillRam_1 => 
				if(adcAquiredDataReadyInterrupt = '1') then
					if(adcRamAddrCounterCh0 = x"00") then
						next_adc_state <= st_adc_aquireAdcData0_FillRam_1_armTrigger0;
					else
						next_adc_state <= st_adc_aquireAdcData0_FillRam_2;
					end if;
				end if;
			when st_adc_aquireAdcData0_FillRam_1_armTrigger0 => 
				next_adc_state <= st_adc_aquireAdcData0_FillRam_0;
				if(skolUartInterruptReceivedData_o = '1') then
					next_adc_state <= st_adc_idle;
				elsif(adcTriggerOn ='0') then
					next_adc_state <= st_adc_aquireAdcData0_FillRam_2;
				elsif(adcTriggerAtPosEdge = '1') then
					if(adcTriggerArmed = '0' )then
						if(adcAquiredData_CH0 < adcTriggerLevel(13 downto 0)) then
							next_adc_state <= st_adc_aquireAdcData0_FillRam_1_armTrigger1;
						end if;
					elsif(adcTriggerArmed = '1' )then
						if(adcAquiredData_CH0 > adcTriggerLevel(13 downto 0)) then
							next_adc_state <= st_adc_aquireAdcData0_FillRam_2;
						end if;
					end if;
				elsif(adcTriggerAtPosEdge = '0' ) then
					if(adcTriggerArmed = '0' )then
						if(adcAquiredData_CH0 > adcTriggerLevel(13 downto 0)) then
							next_adc_state <= st_adc_aquireAdcData0_FillRam_1_armTrigger1;
						end if;
					elsif(adcTriggerArmed = '1' )then
						if(adcAquiredData_CH0 < adcTriggerLevel(13 downto 0)) then
							next_adc_state <= st_adc_aquireAdcData0_FillRam_2;
						end if;
					end if;
				end if;	

			when st_adc_aquireAdcData0_FillRam_1_armTrigger1 => 
				next_adc_state <= st_adc_aquireAdcData0_FillRam_0;
				
			when st_adc_aquireAdcData0_FillRam_2 =>
				next_adc_state <= st_adc_aquireAdcData0_FillRam_3;
			when st_adc_aquireAdcData0_FillRam_3 => 
				if((adcRamAddrCounterCh0 = adcSamplesPerFrame) AND (adcRamAddrCounterCh1 = adcSamplesPerFrame) ) then
					next_adc_state <= st_adc_aquireAdcData0_readFromRam_sendOnlyStart0;
				else
					next_adc_state <= st_adc_aquireAdcData0_FillRam_1;	
				end if;			

			when st_adc_aquireAdcData0_readFromRam_sendOnlyStart0=>
				next_adc_state <= st_adc_aquireAdcData0_readFromRam_sendOnlyStart1 ;
			when st_adc_aquireAdcData0_readFromRam_sendOnlyStart1 =>
				next_adc_state <= st_adc_aquireAdcData0_readFromRam_sendOnlyStart2 ;
			when st_adc_aquireAdcData0_readFromRam_sendOnlyStart2 =>
				if(skolUartInterruptTransmitDataDone_o ='1') then
					next_adc_state <=st_adc_aquireAdcData0_readFromRam_sendOnlyData0 ;
				end if;
					
			
			when st_adc_aquireAdcData0_readFromRam_sendOnlyData0=>
				next_adc_state <= st_adc_aquireAdcData0_readFromRam_sendOnlyData1 ;
			when st_adc_aquireAdcData0_readFromRam_sendOnlyData1=>
				next_adc_state <= st_adc_aquireAdcData0_readFromRam_sendOnlyData2 ;
			when st_adc_aquireAdcData0_readFromRam_sendOnlyData2=>
				next_adc_state <= st_adc_aquireAdcData0_readFromRam_sendOnlyData3 ;
			when st_adc_aquireAdcData0_readFromRam_sendOnlyData3 =>
				next_adc_state <= st_adc_aquireAdcData0_readFromRam_sendOnlyData4 ;
			when st_adc_aquireAdcData0_readFromRam_sendOnlyData4 =>
				if(skolUartInterruptTransmitDataDone_o ='1') then
					if(adcRamAddrCounterCh0 = adcSamplesPerFrame ) then
						next_adc_state <= st_adc_aquireAdcData0_readFromRam_sendOnlyCrc0 ;
					else
						next_adc_state <= st_adc_aquireAdcData0_readFromRam_sendOnlyData0;	
					end if;	
				end if;	
			
			when st_adc_aquireAdcData0_readFromRam_sendOnlyCrc0 =>
				next_adc_state <= st_adc_aquireAdcData0_readFromRam_sendOnlyCrc1 ;
			when st_adc_aquireAdcData0_readFromRam_sendOnlyCrc1 =>
				next_adc_state <= st_adc_aquireAdcData0_readFromRam_sendOnlyCrc2 ;
			when st_adc_aquireAdcData0_readFromRam_sendOnlyCrc2 =>
				if(skolUartInterruptTransmitDataDone_o ='1') then
					next_adc_state <=st_adc_aquireAdcData1_readFromRam_sendOnlyStart0 ;
				end if;				
				
				--ch1
			when st_adc_aquireAdcData1_readFromRam_sendOnlyStart0=>
				next_adc_state <= st_adc_aquireAdcData1_readFromRam_sendOnlyStart1 ;
			when st_adc_aquireAdcData1_readFromRam_sendOnlyStart1 =>
				next_adc_state <= st_adc_aquireAdcData1_readFromRam_sendOnlyStart2 ;
			when st_adc_aquireAdcData1_readFromRam_sendOnlyStart2 =>
				if(skolUartInterruptTransmitDataDone_o ='1') then
					next_adc_state <=st_adc_aquireAdcData1_readFromRam_sendOnlyData0 ;
				end if;
					
			
			when st_adc_aquireAdcData1_readFromRam_sendOnlyData0=>
				next_adc_state <= st_adc_aquireAdcData1_readFromRam_sendOnlyData1 ;
			when st_adc_aquireAdcData1_readFromRam_sendOnlyData1=>
				next_adc_state <= st_adc_aquireAdcData1_readFromRam_sendOnlyData2 ;
			when st_adc_aquireAdcData1_readFromRam_sendOnlyData2=>
				next_adc_state <= st_adc_aquireAdcData1_readFromRam_sendOnlyData3 ;
			when st_adc_aquireAdcData1_readFromRam_sendOnlyData3 =>
				next_adc_state <= st_adc_aquireAdcData1_readFromRam_sendOnlyData4 ;
			when st_adc_aquireAdcData1_readFromRam_sendOnlyData4 =>
				if(skolUartInterruptTransmitDataDone_o ='1') then
					if(adcRamAddrCounterCh1 = adcSamplesPerFrame ) then
						next_adc_state <= st_adc_aquireAdcData1_readFromRam_sendOnlyCrc0 ;
					else
						next_adc_state <= st_adc_aquireAdcData1_readFromRam_sendOnlyData0;	
					end if;	
				end if;	
			
			when st_adc_aquireAdcData1_readFromRam_sendOnlyCrc0 =>
				next_adc_state <= st_adc_aquireAdcData1_readFromRam_sendOnlyCrc1 ;
			when st_adc_aquireAdcData1_readFromRam_sendOnlyCrc1 =>
				next_adc_state <= st_adc_aquireAdcData1_readFromRam_sendOnlyCrc2 ;
			when st_adc_aquireAdcData1_readFromRam_sendOnlyCrc2 =>
				if(skolUartInterruptTransmitDataDone_o ='1') then
					next_adc_state <=st_adc_idle ;
				end if;				

			
			-- --------------------------------------	
			when st_adc_setTriggerLevelHighByte =>
				next_adc_state <=st_adc_idle ;
			when st_adc_setTriggerLevelLowByte =>
				next_adc_state <=st_adc_idle ;
			when st_adc_setTriggerMode =>
				next_adc_state <=st_adc_idle ;
			-- ---------------------------------------
			when others =>
				next_adc_state <= st_adc_idle;
		end case;  

	end process;
	
 
	



	ADC_SYNCHRONOUS_PROCESS: process (CLK_50M)
	begin
		if(CLK_50M'event and CLK_50M = '1') then
			
			adcRamWriteI_ch0 			<= '0' ;
			--adcRamReadI_ch0 			<= '0' ;
			skolUartWE_TwoBytes_i 	<= '0' ;   					
			skolUartWE_OneByte_i	<= '0' ;   
			

			case (adc_state) is
				when st_adc_reset =>
					adcTimeDividerConstantCh0 	<= (others => '0');
					adcTimeDividerConstantCh1 	<= (others => '0');
					adcTriggerLevel 			<=  defaultAdcTriggerLevel;
					adcTriggerAtPosEdge 		<= '1';
					adcTriggerOn				<= '1';
					adcSamplesPerFrame			<= defaultSamplesPerFrameMode00;
					adcSamplesPerFrameModeReceiveMode	<= CONST_SamplesPerFrameMode00;
					adcCyclesTempCounter		<= (others => '0');
				when st_adc_idle =>
					null;
				
				when st_adc_wait => 
					adcTriggerArmed 	<= '0';
					adcRamAddrCounterCh0 	<= (others => '0');
					adcRamAddrCounterCh1 	<= (others => '0');
					adcTimeDividerCounterCh0 <= x"000000";
					adcTimeDividerCounterCh1 <= x"000000";
				-- ---------------------------------------	
				when st_adc_getTimeDividerCh0 =>
					skolUartOpcode_i				<= CONST_getTimeDividerCh0;
					skolUart_dataBlockCountHigh_i	<= x"00";
					skolUart_dataBlockCountLow_i	<= x"01";
					skolUart_dataBlockSize_i  		<= x"01";
					skolUartData0_i 				<= adcTimeDividerAsReceivedCh0;
					skolUartWE_OneByte_i <= '1' ;
				when st_adc_getTimeDividerCh0_0 =>
					skolUartWE_OneByte_i <= '0' ;
				when st_adc_getTimeDividerCh0_1 =>
					null;
					
				when st_adc_getTimeDividerCh1 =>
					skolUartOpcode_i				<= CONST_getTimeDividerCh1;
					skolUart_dataBlockCountHigh_i	<= x"00";
					skolUart_dataBlockCountLow_i	<= x"01";
					skolUart_dataBlockSize_i  		<= x"01";
					skolUartData0_i 				<= adcTimeDividerAsReceivedCh1;
					skolUartWE_OneByte_i <= '1' ;
				when st_adc_getTimeDividerCh1_0 =>
					skolUartWE_OneByte_i <= '0' ;
				when st_adc_getTimeDividerCh1_1 =>
					null;
					
					
				when st_adc_getTriggerMode =>
					skolUartOpcode_i				<= CONST_getTriggerMode;
					skolUart_dataBlockCountHigh_i	<= x"00";
					skolUart_dataBlockCountLow_i	<= x"01";
					skolUart_dataBlockSize_i  		<= x"01";
					if(adcTriggerOn = '0') then
						skolUartData0_i	<= x"00";
					elsif(adcTriggerAtPosEdge = '1') then
						skolUartData0_i	<= x"01";
					elsif(adcTriggerAtPosEdge = '0') then
						skolUartData0_i	<= x"02";
					end if;
					skolUartWE_OneByte_i <= '1' ;
				when st_adc_getTriggerMode0 =>
					skolUartWE_OneByte_i <= '0' ;
				when st_adc_getTriggerMode1 =>
					null;
					
					
				when st_adc_getTriggerLevel =>
					skolUartOpcode_i				<= CONST_getTriggerLevel;
					skolUart_dataBlockCountHigh_i	<= x"00";
					skolUart_dataBlockCountLow_i	<= x"01";
					skolUart_dataBlockSize_i  		<= x"02";
					skolUartData0_i 				<= adcTriggerLevel(15 downto 8 );
					skolUartData1_i 				<= adcTriggerLevel(7  downto 0 );
					skolUartWE_TwoBytes_i <= '1' ;
				when st_adc_getTriggerLevel0 =>
					skolUartWE_TwoBytes_i <= '0' ;
				when st_adc_getTriggerLevel1 =>
					null;
				-- ---------------------------------------	
				
				when st_adc_setSamplesPerFrameMode =>
					if(skolUartData_o = CONST_SamplesPerFrameMode00) then
						adcSamplesPerFrame			<= defaultSamplesPerFrameMode00;
					elsif(skolUartData_o = CONST_SamplesPerFrameMode01) then
						adcSamplesPerFrame			<= defaultSamplesPerFrameMode01;
					elsif(skolUartData_o = CONST_SamplesPerFrameMode02) then
						adcSamplesPerFrame			<= defaultSamplesPerFrameMode02;
					end if;
					
					adcSamplesPerFrameModeReceiveMode <= skolUartData_o;
				-- ---------------------------------------						
				when st_adc_getSamplesPerFrameMode0 =>
					skolUartOpcode_i				<= CONST_getSamplesPerFrameMode;
					skolUart_dataBlockCountHigh_i	<= x"00";
					skolUart_dataBlockCountLow_i	<= x"01";
					skolUart_dataBlockSize_i  		<= x"01";
					skolUartData0_i 				<= adcSamplesPerFrameModeReceiveMode;
					skolUartWE_OneByte_i 			<= '1' ;
				when st_adc_getSamplesPerFrameMode1 =>
					skolUartWE_OneByte_i 			<= '0' ;
				when st_adc_getSamplesPerFrameMode2 =>
					null;

				-- ---------------------------------------	
				when st_adc_setTimeDivideCh0 =>
					adcTimeDividerAsReceivedCh0 <= skolUartData_o;
					case(skolUartData_o) is
						when x"00" =>   
							adcTimeDividerConstantCh0 <= x"000000"; --1       
						when x"01" =>                                   
							adcTimeDividerConstantCh0 <= x"000001"; --2       
						when x"02" =>                            
							adcTimeDividerConstantCh0 <= x"000004"; --5   
						when x"03" =>                        
							adcTimeDividerConstantCh0 <= x"000009"; --10       
						when x"04" =>                            
							adcTimeDividerConstantCh0 <= x"000013"; --20       
						when x"05" =>                          
							adcTimeDividerConstantCh0 <= x"000031";
						when x"06" =>                          
							adcTimeDividerConstantCh0 <= x"000063";
						when x"07" =>                          
							adcTimeDividerConstantCh0 <= x"0000C7";
						when x"08" =>                          
							adcTimeDividerConstantCh0 <= x"0001F3";
						when x"09" =>                          
							adcTimeDividerConstantCh0 <= x"0003E7";
						when x"0a" =>
							adcTimeDividerConstantCh0 <= x"0007CF";
						when x"0b" =>
							adcTimeDividerConstantCh0 <= x"001387";
						when x"0c" =>
							adcTimeDividerConstantCh0 <= x"00270F";
						when x"0d" =>
							adcTimeDividerConstantCh0 <= x"004E1F";
						when x"0e" =>
							adcTimeDividerConstantCh0 <= x"00C34F";
						when others =>
							adcTimeDividerAsReceivedCh0 <= x"ff";
					end case;
				when st_adc_setTimeDivideCh1 =>
					adcTimeDividerAsReceivedCh1 <= skolUartData_o;
					case(skolUartData_o) is
						when x"00" =>   
							adcTimeDividerConstantCh1 <= x"000000"; --1       
						when x"01" =>                                   
							adcTimeDividerConstantCh1 <= x"000001"; --2       
						when x"02" =>                            
							adcTimeDividerConstantCh1 <= x"000004"; --5   
						when x"03" =>                        
							adcTimeDividerConstantCh1 <= x"000009"; --10       
						when x"04" =>                            
							adcTimeDividerConstantCh1 <= x"000013"; --20       
						when x"05" =>                          
							adcTimeDividerConstantCh1 <= x"000031";
						when x"06" =>                          
							adcTimeDividerConstantCh1 <= x"000063";
						when x"07" =>                          
							adcTimeDividerConstantCh1 <= x"0000C7";
						when x"08" =>                          
							adcTimeDividerConstantCh1 <= x"0001F3";
						when x"09" =>                          
							adcTimeDividerConstantCh1 <= x"0003E7";
						when x"0a" =>
							adcTimeDividerConstantCh1 <= x"0007CF";
						when x"0b" =>
							adcTimeDividerConstantCh1 <= x"001387";
						when x"0c" =>
							adcTimeDividerConstantCh1 <= x"00270F";
						when x"0d" =>
							adcTimeDividerConstantCh1 <= x"004E1F";
						when x"0e" =>
							adcTimeDividerConstantCh1 <= x"00C34F";
						when others =>
							adcTimeDividerAsReceivedCh1 <= x"ff";
					end case;
						
						

				-- ---------------------------------------	
				
				-- 2 reads because first read aquires old value - see spartan starter kit docu
				when st_adc_singleShotCh0_Volt_0 => -- read data
					adcAquireSingleDataFrame <= '1';
				when st_adc_singleShotCh0_Volt_1 => -- wait if data ready
					null;
				when st_adc_singleShotCh0_Volt_2 => -- read data
					adcAquireSingleDataFrame <= '1';
				when st_adc_singleShotCh0_Volt_3 => -- wait if data ready
					null;			
				when st_adc_singleShotCh0_Volt_4 =>
					adcAquiredDataTmp0 		<= adcAquiredData_CH0;
					adcAquiredDataTmp1 		<= adcAquiredData_CH1;

				when st_adc_singleShotCh0_Volt_send0 =>

					
				when st_adc_singleShotCh0_Volt_send1 => 
					null;
					
				when st_adc_singleShotCh0_Volt_send2 =>
					skolUartOpcode_i 			<= CONST_singleVoltValueCh0;
					skolUartData0_i(7 downto 6)	<= b"00" ;
					skolUartData0_i(5 downto 0)	<= adcAquiredDataTmp0(13 downto 8) ;
					skolUartData1_i   			<= adcAquiredDataTmp0( 7 downto 0) ;
				    skolUartWE_TwoBytes_i 			<= '1' ;  
				
				when st_adc_singleShotCh0_Volt_send3 => 
					skolUartWE_TwoBytes_i 			<= '0' ;   
					
				when st_adc_singleShotCh0_Volt_send4 => 
					null;
				
				when st_adc_singleShotCh1_Volt_send0 =>

					
				when st_adc_singleShotCh1_Volt_send1 => 
					null;
					
				when st_adc_singleShotCh1_Volt_send2 =>
					skolUartOpcode_i 			<= CONST_singleVoltValueCh1;
					skolUartData0_i(7 downto 6)	<= b"00" ;
					skolUartData0_i(5 downto 0)	<= adcAquiredDataTmp1(13 downto 8) ;
					skolUartData1_i   			<= adcAquiredDataTmp1( 7 downto 0) ;
				    skolUartWE_TwoBytes_i 			<= '1' ;  
				
				when st_adc_singleShotCh1_Volt_send3 => 
					skolUartWE_TwoBytes_i 			<= '0' ;   
					
				when st_adc_singleShotCh1_Volt_send4 => 
					null;	

				-- ---------------------------------------
			    
				
				-- ---------------------------------------	
				when st_adc_aquireAdcData0_FillRam_0 => -- read data
					adcAquireSingleDataFrame <= '1';
					adcRamAddrCounterCh0 	<= (OTHERS => '0');
					adcRamAddrCounterCh1 	<= (OTHERS => '0');
					adcCyclesTempCounter		<=	adcCyclesTempCounter + 1 ;

				when st_adc_aquireAdcData0_FillRam_1 => -- wait if data ready
					adcCyclesTempCounter		<=	adcCyclesTempCounter + 1 ;
					--null;			
				when st_adc_aquireAdcData0_FillRam_1_armTrigger0 =>
					adcCyclesTempCounter		<=	adcCyclesTempCounter + 1 ;
					--null; 
				when st_adc_aquireAdcData0_FillRam_1_armTrigger1 => 
					adcTriggerArmed <= '1';
					adcCyclesTempCounter		<=	adcCyclesTempCounter + 1 ;
				when st_adc_aquireAdcData0_FillRam_2 =>
					if(adcRamAddrCounterCh0 /= adcSamplesPerFrame) then
						if(adcTimeDividerCounterCh0 = x"000000") then
							adcRamDataInI_ch0 		<= adcAquiredData_CH0;
							adcRamWriteAddrI_ch0 	<= adcRamAddrCounterCh0;
							adcRamWriteI_ch0 		<= '1' ;
						
							adcRamAddrCounterCh0 		<= adcRamAddrCounterCh0 + 1;
							adcTimeDividerCounterCh0 	<= adcTimeDividerConstantCh0;
						else
							adcTimeDividerCounterCh0 	<= adcTimeDividerCounterCh0 -1 ;
						end if;
					end if;
					if(adcRamAddrCounterCh1 /= adcSamplesPerFrame) then
						if(adcTimeDividerCounterCh1 = x"000000") then
							-- use following line to count adcCycles for a single Aquisition
							--adcRamDataInI_ch1 		<= adcCyclesTempCounter; -- counter for adcCycles
							adcRamDataInI_ch1 		<= adcAquiredData_CH1; -- default operation mode
							adcRamWriteAddrI_ch1 	<= adcRamAddrCounterCh1;
							adcRamWriteI_ch1 		<= '1' ;
						

							
							adcRamAddrCounterCh1 		<= adcRamAddrCounterCh1 + 1;
							adcTimeDividerCounterCh1 	<= adcTimeDividerConstantCh1;
							adcCyclesTempCounter(adcRamDataWidth-1 downto 1)	<= (others => '0');
							adcCyclesTempCounter(0)								<= '1';
						else
							adcTimeDividerCounterCh1 	<= adcTimeDividerCounterCh1 -1 ;
							adcCyclesTempCounter		<=	adcCyclesTempCounter + 1 ;
						end if;
					end if;
					
					if(adcTimeDividerCounterCh0 = x"000000" OR adcTimeDividerCounterCh1 = x"000000") then
						adcAquireSingleDataFrame 	<= '1';
					end if;
				when st_adc_aquireAdcData0_FillRam_3 => -- check if enough data points
					null;		
					-- send ch0
				when st_adc_aquireAdcData0_readFromRam_sendOnlyStart0 =>
					skolUartOpcode_i 				<= CONST_dataAquCh0;
					skolUart_dataBlockCountHigh_i	<= adcSamplesPerFrame(15 downto 8);
					skolUart_dataBlockCountLow_i	<= adcSamplesPerFrame( 7 downto 0);
					skolUart_dataBlockSize_i  		<= x"02";
					skolUartWE_StartFrame_i <= '1' ;
				when st_adc_aquireAdcData0_readFromRam_sendOnlyStart1 =>
					skolUartWE_StartFrame_i <= '0' ;
				when st_adc_aquireAdcData0_readFromRam_sendOnlyStart2 =>
					adcRamAddrCounterCh0 	<= (OTHERS => '0');
					null;
					
				
							    
				when st_adc_aquireAdcData0_readFromRam_sendOnlyData0 =>
					adcRamReadAddrI_ch0 	<= adcRamAddrCounterCh0; 
					adcRamReadI_ch0 		<= '1' ;
					adcRamAddrCounterCh0 	<= adcRamAddrCounterCh0 + 1;
				when st_adc_aquireAdcData0_readFromRam_sendOnlyData1 =>
					null;
				when st_adc_aquireAdcData0_readFromRam_sendOnlyData2 =>
					skolUartData0_i(7 downto 6)	<= b"00" ;
					skolUartData0_i(5 downto 0)	<= adcRamDataOutO_ch0(13 downto 8) ;
					skolUartData1_i   			<= adcRamDataOutO_ch0( 7 downto 0) ;
					skolUartWE_DataFrame_i <= '1' ;
				when st_adc_aquireAdcData0_readFromRam_sendOnlyData3 =>
					skolUartWE_DataFrame_i <= '0' ;
				when st_adc_aquireAdcData0_readFromRam_sendOnlyData4 =>
					null;
				
				when st_adc_aquireAdcData0_readFromRam_sendOnlyCrc0 =>
					skolUartWE_CrcFrame_i <= '1' ;
				when st_adc_aquireAdcData0_readFromRam_sendOnlyCrc1 =>
					skolUartWE_CrcFrame_i <= '0' ;
				when st_adc_aquireAdcData0_readFromRam_sendOnlyCrc2 =>
					null;
					-- channel 1
				when st_adc_aquireAdcData1_readFromRam_sendOnlyStart0 =>
					skolUartOpcode_i 				<= CONST_dataAquCh1;
					skolUart_dataBlockCountHigh_i	<= adcSamplesPerFrame(15 downto 8);
					skolUart_dataBlockCountLow_i	<= adcSamplesPerFrame( 7 downto 0);
					skolUart_dataBlockSize_i  		<= x"02";
					skolUartWE_StartFrame_i <= '1' ;
				when st_adc_aquireAdcData1_readFromRam_sendOnlyStart1 =>
					skolUartWE_StartFrame_i <= '0' ;
				when st_adc_aquireAdcData1_readFromRam_sendOnlyStart2 =>
					adcRamAddrCounterCh1 	<= (OTHERS => '0');
					null;
					
				
							    
				when st_adc_aquireAdcData1_readFromRam_sendOnlyData0 =>
					adcRamReadAddrI_ch1 	<= adcRamAddrCounterCh1; 
					adcRamReadI_ch1 		<= '1' ;
					adcRamAddrCounterCh1 	<= adcRamAddrCounterCh1 + 1;
				when st_adc_aquireAdcData1_readFromRam_sendOnlyData1 =>
					null;
				when st_adc_aquireAdcData1_readFromRam_sendOnlyData2 =>
					skolUartData0_i(7 downto 6)	<= b"00" ;
					skolUartData0_i(5 downto 0)	<= adcRamDataOutO_ch1(13 downto 8) ;
					skolUartData1_i   			<= adcRamDataOutO_ch1( 7 downto 0) ;
					skolUartWE_DataFrame_i <= '1' ;
				when st_adc_aquireAdcData1_readFromRam_sendOnlyData3 =>
					skolUartWE_DataFrame_i <= '0' ;
				when st_adc_aquireAdcData1_readFromRam_sendOnlyData4 =>
					null;
				
				when st_adc_aquireAdcData1_readFromRam_sendOnlyCrc0 =>
					skolUartWE_CrcFrame_i <= '1' ;
				when st_adc_aquireAdcData1_readFromRam_sendOnlyCrc1 =>
					skolUartWE_CrcFrame_i <= '0' ;
				when st_adc_aquireAdcData1_readFromRam_sendOnlyCrc2 =>
					null;
				-- --------------------------------------	
				when st_adc_setTriggerLevelHighByte =>
					adcTriggerLevel(15 downto 8 ) <= skolUartData_o ;	
				when st_adc_setTriggerLevelLowByte =>
					adcTriggerLevel(7 downto 0 ) <= skolUartData_o ;	
				when st_adc_setTriggerMode =>
					if(skolUartData_o = CONST_TriggerOff) then
						adcTriggerOn 		<= '0';
					elsif(skolUartData_o = CONST_TriggerPosEdge) then
						adcTriggerOn 		<= '1';
						adcTriggerAtPosEdge <= '1';
					elsif(skolUartData_o = CONST_TriggerNegEdge) then
						adcTriggerOn 		<= '1';
						adcTriggerAtPosEdge <= '0';
					else
						null;
					end if;

				-- ---------------------------------------	
				when others =>
					null;
			end case;  
				
			--ledTempOutput1(7 downto 4) <=	adcRamAddrCounter1;
		end if;
	end process;
-- -------------------------------------------------------------
	controllSpiClock : process(initAdcDone,spiClockForInitAmp,CLK_50M,adcClock)
	
	begin
	if(initAdcDone = '0') then
		SPI_SCK <= spiClockForInitAmp;
	else
		SPI_SCK <= adcClock;
	end if;
	end process;



-- -------------------------------------------------------------
	--LED_0 <= BTN_SOUTH;


	adcRamEnableI_ch0 	<= '1' ;
	adcRamEnableI_ch1 	<= '1' ;
	adcAquireDataStream	<= '0' ;


	


	
		-- led's
	LED(6 downto 0) <= skolUartReceivedMsgCounter(6 downto 0);
	LED(7) <= led7Tmp;
		
		
	sig0 <= '0';
  	sig1 <= '1';	
  	
  	
	
-- -------------------------------------------------------------				
					
---single_turnOnLed : process(skolUartInterruptReceivedData_o)
--	variable temp : boolean := false ;
--	begin
--			if(skolUartInterruptReceivedData_o'event and skolUartInterruptReceivedData_o = '1') then
--				temp := true;
--			end if;
--			
--			if (temp = true) then
--				LED_7 <='1';
--			else
--				LED_7 <= '0';
--			end if;
--			
--	end process;

-- -------------------------------------------------------------

-- this process generates a signal which can be used to feed the osci
toggle_led : process(tempOutSignal)
	variable temp : boolean := false ;
	begin
		if(tempOutSignal'event and tempOutSignal = '1') then
			J20_IO <= "0000";
			if(temp = false)then
				temp := true;
				J20_IO(1) <='1';
			else
				temp := false;
				J20_IO(1) <='0';
			end if;
		end if;
	end process;	
	
-- -------------------------------------------------------------

-- -asdf: process(BTN_EAST)
--	begin
--		if (BTN_EAST'event and BTN_EAST = '1') then
--			spi_reset <= '1';
--		else
--			spi_reset <= '0' ;
--		end if;
--	end process;


	


	--{LED_7,LED_6,LED_5,LED_4,LED_3,LED_2,LED_1,LED_0}  <= 
	
	--BTN_SOUTH <=	ROT_A	;
	

--toggle_led22 : process(adcFsmStart)
--	variable temp : boolean := false ;
--	begin
--		if(adcFsmStart'event and adcFsmStart = '1') then
--			if(temp = false)then
--				temp := true;
--				LED_7 <='1';
--			else
--				temp := false;
--				LED_7 <='0';
--			end if;
--		end if;
--	end process;	

end Behavioral;

