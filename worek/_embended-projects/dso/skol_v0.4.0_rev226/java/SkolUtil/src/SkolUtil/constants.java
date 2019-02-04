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
 *
 * @author sushi
 */


/**
 * keep in sync with top.vhd
 * @author sush
 */
public  class constants 
{
   
    public static final byte magicByte    = (byte) 0xEF;
    
    public static final byte TriggerOff         = (byte) 0x01;
    public static final byte TriggerPosEdge     = (byte) 0x02;
    public static final byte TriggerNegEdge     = (byte) 0x04;

        //                                                  
    public static final byte[] SamplesPerFrameMode  = 
            new byte[]{ (byte) 0x01, // Mode 00
                        (byte) 0x02, // Mode 01
                        (byte) 0x04  // Mode 02
                        };

}
