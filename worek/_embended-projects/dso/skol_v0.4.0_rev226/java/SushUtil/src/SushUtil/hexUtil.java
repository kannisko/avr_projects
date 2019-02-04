/*--
-- Copyright (C) [2008] [Schuster Andreas]
-- 
-- This program is free software; you can redistribute it and/or modify it 
-- under the terms of the GNU General Public License as published by the Free 
-- Software Foundation; either version 3 of the License, or (at your option) 
-- any later version.
-- 
-- This program is distributed byteArray the hope that it will be useful, but WITHOUT 
-- ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS 
-- FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
-- 
-- You should have received a copy of the GNU General Public License along with 
-- this program; if not, see <http://www.gnu.org/licenses/>.
-- */
/*
 * To change this template, choose Tools | Templates
 * and open the template byteArray the editor.
 */

// Revision 1.0 Released
// Revision 1.1 hexUtilException introduced

package SushUtil;

import java.util.ArrayList;

/**
 *
 * @author sushi
 */
public  class hexUtil 
{

    /**
     * generates human readable String Representation
     * @param byteArray byte Array
     * @return  for e.g. new byte[]{10,17,2}  Result:  "0A1102"
     */
    public static String byteToHexString(byte byteArray[]) 
    {

        byte ch = 0x00;

        int i = 0; 

        if (byteArray == null || byteArray.length <= 0)

            return null;



        String pseudo[] = {"0", "1", "2",
                        "3", "4", "5", "6", "7", "8",
                        "9", "A", "B", "C", "D", "E",
                        "F"};

        StringBuffer out = new StringBuffer(byteArray.length * 2);



        while (i < byteArray.length) 
        {

            ch = (byte) (byteArray[i] & 0xF0); // Strip off high nibble

            ch = (byte) (ch >>> 4);
            // shift the bits down

            ch = (byte) (ch & 0x0F);    
            // must do this is high order bit is on!

            out.append(pseudo[ (int) ch]); // convert the nibble to a String Character

            ch = (byte) (byteArray[i] & 0x0F); // Strip off low nibble 

            out.append(pseudo[ (int) ch]); // convert the nibble to a String Character

            i++;

        }

        String rslt = new String(out);

        return rslt;

    }  
    
    /**
     * generates human readable String Representation
     * @param byteArray byte Array
     * @return  for e.g. new byte = 2  Result:  "02"
     */
    public static String byteToHexString(byte b)
    {
        return byteToHexString(new byte[]{b});
    }
    

    
    /**
     * [msb ...][...]...[... lsb]
     * lsb least significant bit
     * msb most significant bit
     * @param b byte[]  max 3 bytes ! because Integer byteArray java cannot represent more because there is only a signed implementation included byteArray java.
     * @return integer value (0...255)
     */
    public static int byteToUnsignedInt(byte[] b) throws hexUtilException
    {
        switch(b.length)
        {
            case 1:
                return (b[0] & 0xFF) ;
             case 2:
                return ((b[0] & 0xFF) << 8)
                 + ((b[1] & 0xFF));            
             case 3:
                return ((b[0] & 0xFF)<< 16)
                 + ((b[1] & 0xFF) << 8)
                 + ((b[2] & 0xFF));
             case 4:
                return (b[0] << 24)
                 + ((b[1] & 0xFF) << 16)
                 + ((b[2] & 0xFF) << 8)
                 +  (b[3] & 0xFF);
            default:
                throw new hexUtilException("Error in unsignedByteToInt - byte Array is too long!");

        
        }


    }
    /**
     * [msb ... lsb]
     * lsb least significant bit
     * msb most significant bit
     * @param b byte  
     * @return integer value (0...255)
     */
    public static int byteToUnsignedInt(byte b) 
    {
        int i = 0;
        i |= b & 0xFF;
       
        return i;

    }

    /**
     * Generates a Byte Array from an Integer Array. As a byte goes from 0 to 255 (including),
     * there can be 3 bytes grouped together to represent 1 Integer Value, Depending on the
     * highest Integer value inclueded.
     * For Example new byte[]{1,0,3} 
     * = 256^2*1 + 256^1*0 + 256^0*3
     * = 65536*1 + 256  *0 + 1    *3
     * = 65536   + 0       + 3
     * = 6553
     * In this Example the Blocksize is 3.
     * To determine the blocksize byteArray Advance, 
     * the Method 'howMuchBytesAreNeededToRepresentTheHighestIntegerInThisArray' can be used.
     * Other Example: new Integer{258}
     * needs to be represented by 2 bytes:
     * new byte[]{1,2} = 
     * = 256^1*1 + 256^0*2
     * = 256  *1 + 1    *2
     * = 25      + 3
     * = 258
     * @param intArray
     * @param blockSize
     * @return
     * @throws hexUtilException
     */
    public static byte[] intToByteArray(int[] intArray, int blockSize) throws hexUtilException
    {
        if(intArray.length == 0 && blockSize == 1)
            return new byte[]{};
         
        if(blockSize > 3)
            throw  new hexUtilException("intArrayToByteArray Error : blockSize cannot exceed 3!");
        if( blockSize < howMuchBytesAreNeededToRepresentTheHighestIntegerInThisArray(intArray))
            throw  new hexUtilException("intArrayToByteArray Error : blockSize too low use Method 'getBlockSizeForIntArrayToUseInByteArray'!");
        
        byte[] ret = new byte[intArray.length * blockSize];
        int byteArrayPointer = 0;
        for(int i = 0; i< (intArray.length); i ++)
        {
            byte[] tmp = intToByteArray(intArray[i], blockSize);
            for(int j = 0 ; j < blockSize; j++)
            {
                ret[byteArrayPointer] = tmp[j];
                byteArrayPointer++;
            }
        }
        return ret;
    
    
    }
    
   
    /**
     * Generates a Byte Array from an Integer. As a byte goes from 0 to 255 (including),
     * there can be 3 bytes grouped together to represent 1 Integer Value, Depending on the
     * highest Integer value inclueded.
     * For Example new byte[]{1,0,3} 
     * = 256^2*1 + 256^1*0 + 256^0*3
     * = 65536*1 + 256  *0 + 1    *3
     * = 65536   + 0       + 3
     * = 6553
     * In this Example the Blocksize is 3.
     * To determine the blocksize byteArray Advance, 
     * the Method 'howMuchBytesAreNeededToRepresentTheHighestIntegerInThisArray' can be used.
     * Other Example: new Integer{258}
     * needs to be represented by 2 bytes:
     * new byte[]{1,2} = 
     * = 256^1*1 + 256^0*2
     * = 256  *1 + 1    *2
     * = 25      + 3
     * = 258
     * @param integer
     * @param blockSize
     * @return
     */
    public static byte[] intToByteArray(int integer, int blockSize)
    {
        switch(blockSize)
        {
            case 1:
                return new byte[] {   
                         (byte)integer};

            case 2:
                return new byte[] {   
                         (byte)(integer >>> 8),
                         (byte)integer};

            case 3:
                return new byte[] {   
                         (byte)(integer >>> 16),
                         (byte)(integer >>> 8),
                         (byte)integer};

            case 4:
                return new byte[] {   
                         (byte)(integer >>> 24),
                         (byte)(integer >>> 16),
                         (byte)(integer >>> 8),
                         (byte)integer};

            default:
                return null;
        
        
        }
        
    }
    

    /**
     * As a byte goes from 0 to 255 (including),
     * there can be 3 bytes grouped together to represent 1 Integer Value, Depending on the
     * highest Integer value inclueded.
     * For Example new byte[]{1,0,3} 
     * = 256^2*1 + 256^1*0 + 256^0*3
     * = 65536*1 + 256  *0 + 1    *3
     * = 65536   + 0       + 3
     * = 6553
     * In this Example the Blocksize is 3.
     * To determine the blocksize byteArray Advance, 
     * this Method  can be used.
     * Other Example: new Integer{258}
     * needs to be represented by 2 bytes:
     * new byte[]{1,2} = 
     * = 256^1*1 + 256^0*2
     * = 256  *1 + 1    *2
     * = 25      + 3
     * = 258
     * @param intArray
     * @return
     */
    public static int howMuchBytesAreNeededToRepresentTheHighestIntegerInThisArray(int[] intArray) throws hexUtilException
    {
        int max = 0;
        for(int i=0; i < intArray.length;i++)
        {
            if(intArray[i] > max)
                max = intArray[i];
        }
        
        if(max < 256)
            return 1;
        else if(max < 256*256)
            return 2;
        else if(max < 256*256*256)
            return 3;
        else
        {
             throw new hexUtilException("getBlockSizeForByteArray max >= 256*256*256");
        }
    }
    
    /**
     * Produces delimited output. For Example: "1-2-3" or "1:2:3" etc.
     * @param intArray {0,1,2}
     * @param delimiterString String "-"
     * @return "0-1-2"
     */
    public static String intToString(int[] intArray, String delimiterString)
    {
        String ret = "";
        for(int i = 0; i < intArray.length;i++)
        {
            ret += i + delimiterString;
        }
        return ret;
    }
    
    /**
     * Converts hexString to byte Array containing the HEX Values from the String
     * @param hexString
     * @return
     */
    public static byte[] stringToByte(String hexString) throws hexUtilException
    {
        if(hexString.length()%2 != 0)
            throw new hexUtilException("Hexstring must contain 2chars per byte !");
        byte bArray[] = new byte[hexString.length()/2];  
        for(int i=0; i<(hexString.length()/2); i++){
            byte firstNibble  = Byte.parseByte(hexString.substring(2*i,2*i+1),16); // [x,y)
            byte secondNibble = Byte.parseByte(hexString.substring(2*i+1,2*i+2),16);
            int finalByte = (secondNibble) | (firstNibble << 4 ); // bit-operations only with numbers, not bytes.
            bArray[i] = (byte) finalByte;
        }
        return bArray;

    }

    /**
     * Concatenates two byte arrays
     * None of them is allowed to be NULL or of length 0
     * @param a
     * @param b
     * @return
     * @throws SushUtil.hexUtilException
     */
    public static byte[] concatenateBytes(byte[] a, byte[] b) throws hexUtilException
    {
        if(a == null || b == null )
            throw new hexUtilException("a or b == null");

        if(a.length == 0 || b.length == 0)
            throw new hexUtilException("a.length == 0 || b.length == 0");
        
        byte[] ret = new byte[a.length + b.length];

        for(int i = 0 ;i < a.length;i++)
            ret[i] = a[i];

        for(int i = 0 ;i < b.length;i++)
            ret[i+a.length] = b[i];

        return ret;
    }
    
    /**
     * returns count number of bytes as first entry, rest as second entry
     * @param byteArray
     * @param postion
     * @return
     * @throws SushUtil.hexUtilException
     */
    public static ArrayList<byte[]> splitAt(byte[] byteArray, int postion) throws hexUtilException
    {
        if(byteArray == null )
            throw new hexUtilException("byteArray == null");

        
        if(byteArray.length < postion)
            throw new hexUtilException("byteArray.length < postion");
        
        ArrayList<byte[]> arrayList = new ArrayList<byte[]>();
        byte[] ret0 = new byte[postion];
        byte[] ret1 = new byte[byteArray.length - postion];

        for(int i = 0 ;i < ret0.length;i++)
            ret0[i] = byteArray[i];
        for(int i = 0 ;i < ret1.length;i++)
            ret1[i] = byteArray[i+postion];

        arrayList.add(ret0);
        arrayList.add(ret1);

        return arrayList;
    }
}


