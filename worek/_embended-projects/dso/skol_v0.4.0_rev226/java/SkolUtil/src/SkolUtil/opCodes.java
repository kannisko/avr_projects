/*--
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
-- */
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package SkolUtil;

/**
 * all 0xb? values will be valid start bytes in message.java
 * public static Boolean isValidMessageStart(byte[] rawMessage, int lenSoFar)
 * throws Exception if adding more valid msgStartBytes here,
 * which can be sent to GUI, change so in isValidMessageStart....
 * @author sushi
 */
public class opCodes 
{                                                   

    /**
     * sends a index value to the fpga
     * 0 -> no divider
     * 1 -> divide by (whatever you decide in the fpga impl )
     * 2,3,....
     */
    public static final byte setTimeDividerCh1              = (byte)0x00;
   /**
     * sends a index value to the fpga
     * 0 -> no divider
     * 1 -> divide by (whatever you decide in the fpga impl )
     * 2,3,....
     */
    public static final byte setTimeDividerCh0              = (byte)0x01;

    /**
     * Turns Trigger Function on/off regarding the databyte[0]
     * databyte 0 : turn off
     * databyte 1 : turn on PosEdge
     * databyte 2 : turn on NegEdge
     */
    public static final byte setTriggerMode                 = (byte)0x02;

    /**
     * Sets SamplesPerFrameMode Mode
     * databyte determines which mode
     * is used to improve resolution of dataaquisition
     * Modes are defined in top.vhd and skol.xml
     */
    public static final byte setSamplesPerFrameMode         = (byte)0x03;

    
    
    /**
     * TriggerLevel is a value from 0 to (2 ^ ADC bit count)
     * As Adc Bit Count can be at maximum 16 bit - we have to transmit
     * a High and a LowByte
     * For a 8 bit ADC for example only a lowByte would be necessary.
     * Works only on Channel 0
     */
    public static final byte setTriggerLevelHighByte        = (byte)0x04;
    /**
     * TriggerLevel is a value from 0 to (2 ^ ADC bit count)
     * As Adc Bit Count can be at maximum 16 bit - we have to transmit
     * a High and a LowByte
     * For a 8 bit ADC for example only a lowByte would be necessary.
     * Works only on Channel 0
     */
    public static final byte setTriggerLevelLowByte         = (byte)0x05;


    /**
     * Returns 1 databyte
     * '0' if negEdge
     * '1' if posEdge
     */
    public static final byte getTriggerMode                 = (byte)0xB0;

    /**
     * returns a index which describes the TimeDivider
     */
    public static final byte getTimeDividerCh0              = (byte)0xB1;
    /**
     * returns a index which describes the TimeDivider
     */
    public static final byte getTimeDividerCh1              = (byte)0xB2;
    /**
     * get Triggered Data Frame from both Channels
     */
    public static final byte dataAquCh0                     = (byte)0xBE;
    
    /**
     * has no function when sent to device
     * is only used to declare that the message which is
     * sent from the device to the Controller that this is
     * from Channel1
     */
    public static final byte dataAquCh1                     = (byte)0xB3;

   /**
    * read single Volt Value from respective Channel
    * Has to be performed 2 times because first value the adc
    * responds is a old one - see spartan 3a starterkit doku
    */
    public static final byte singleVoltValueCh0             = (byte)0xB4;
    
    /**
     * has no function when sent to device
     * is only used to declare that the message which is
     * sent from the device to the Controller that this is
     * from Channel1
     */
    public static final byte singleVoltValueCh1             = (byte)0xB5;

    

    /**
     * returns Trigger level as a message with 2byte data
     */
    public static final byte getTriggerLevel                = (byte)0xB8;
    
    /**
     * Gets SamplesPerFrameMode Mode
     * databyte determines which mode
     * is used to improve resolution of dataaquisition
     * Modes are defined in top.vhd and skol.xml
     */
    public static final byte getSamplesPerFrameMode         = (byte)0xB9;


   

    /**
     * RESET's Device
     */
    public static final byte[] RESET            = new byte[]{(byte)255,(byte)255,(byte)255,(byte)255,(byte)255,(byte)255,(byte)255};
}

