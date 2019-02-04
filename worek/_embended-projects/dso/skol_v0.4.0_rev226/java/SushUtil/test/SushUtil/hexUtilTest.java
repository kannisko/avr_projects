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
package SushUtil;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */



import java.util.ArrayList;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author sushi
 */
public class hexUtilTest {

    public hexUtilTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of byteToHexString method, of class hexUtil.
     */
    @Test
    public void testByteToHexString_byteArr() {
        System.out.println("byteToHexString");
        byte[] in = new byte[]{0, 1, 15, (byte)255};
        String expResult = "00010FFF";
        String result = hexUtil.byteToHexString(in);
        assertEquals(expResult, result);
        
        in = new byte[]{0};
        expResult = "00";
        result = hexUtil.byteToHexString(in);
        assertEquals(expResult, result);

        


    }

    /**
     * Test of byteToHexString method, of class hexUtil.
     */
    @Test
    public void testByteToHexString_byte() {
        System.out.println("byteToHexString");
        byte myByte = 0;
        String expResult = "00";
        String result = hexUtil.byteToHexString(myByte);
        assertEquals(expResult, result);
        
        myByte = 10;
        expResult = "0A";
        result = hexUtil.byteToHexString(myByte);
        assertEquals(expResult, result); 
        
        myByte = 16;
        expResult = "10";
        result = hexUtil.byteToHexString(myByte);
        assertEquals(expResult, result);
        
        myByte = (byte) 255;
        expResult = "FF";
        result = hexUtil.byteToHexString(myByte);
        assertEquals(expResult, result);
        
        myByte = 17;
        expResult = "11";
        result = hexUtil.byteToHexString(myByte);
        assertEquals(expResult, result);
        
    }

    /**
     * Test of byteToUnsignedInt method, of class hexUtil.
     */
    @Test
    public void testUnsignedByteToInt_byte() {
        System.out.println("unsignedByteToInt");
        byte b = (byte)255;
        int expResult = 255;
        int result = hexUtil.byteToUnsignedInt(b);
        assertEquals(expResult, result);
        
        b = (byte)0;
        expResult = 0;
        result = hexUtil.byteToUnsignedInt(b);
        assertEquals(expResult, result);
        
        b = (byte)16;
        expResult = 16;
        result = hexUtil.byteToUnsignedInt(b);
        assertEquals(expResult, result);
        
        b = (byte)200;
        expResult = 200;
        result = hexUtil.byteToUnsignedInt(b);
        assertEquals(expResult, result);

    }

    /**
     * Test of byteToUnsignedInt method, of class hexUtil.
     */
    @Test
    public void testUnsignedByteToInt_byteArr() throws Exception {
        System.out.println("unsignedByteToInt");
        byte[] b = new byte[]{0};
        int expResult = 0;
        int result = hexUtil.byteToUnsignedInt(b);
        assertEquals(expResult, result);

        b = new byte[]{4};
        expResult = 4;
        result = hexUtil.byteToUnsignedInt(b);
        assertEquals(expResult, result);

        b = new byte[]{0, 0, 0};
        expResult = 0;
        result = hexUtil.byteToUnsignedInt(b);
        assertEquals(expResult, result);

        b = new byte[]{1, 0, 0};
        expResult = 256*256;
        result = hexUtil.byteToUnsignedInt(b);
        assertEquals(expResult, result);

        b = new byte[]{0,0,1};
        expResult = 1;
        result = hexUtil.byteToUnsignedInt(b);
        assertEquals(expResult, result);

        b = new byte[]{2,1};
        expResult = 1+(256*2);
        result = hexUtil.byteToUnsignedInt(b);
        assertEquals(expResult, result);

    }

    
    
    @Test
    public void getBlockSizeForIntArrayToUseInByteArray_test() throws Exception {
        System.out.println("getBlockSizeForIntArrayToUseInByteArray_test");
        
        int blockSize = hexUtil.howMuchBytesAreNeededToRepresentTheHighestIntegerInThisArray(new int[]{0});
        assertEquals(1, blockSize);
        blockSize = hexUtil.howMuchBytesAreNeededToRepresentTheHighestIntegerInThisArray(new int[]{1});
        assertEquals(1, blockSize);
        blockSize = hexUtil.howMuchBytesAreNeededToRepresentTheHighestIntegerInThisArray(new int[]{255});
        assertEquals(1, blockSize);
        blockSize = hexUtil.howMuchBytesAreNeededToRepresentTheHighestIntegerInThisArray(new int[]{256});
        assertEquals(2, blockSize);
        blockSize = hexUtil.howMuchBytesAreNeededToRepresentTheHighestIntegerInThisArray(new int[]{256,0});
        assertEquals(2, blockSize);
        blockSize = hexUtil.howMuchBytesAreNeededToRepresentTheHighestIntegerInThisArray(new int[]{256,1,256});
        assertEquals(2, blockSize);
        blockSize = hexUtil.howMuchBytesAreNeededToRepresentTheHighestIntegerInThisArray(new int[]{256,1,1,0});
        assertEquals(2, blockSize);
        
        blockSize = hexUtil.howMuchBytesAreNeededToRepresentTheHighestIntegerInThisArray(new int[]{256*256});
        assertEquals(3, blockSize);
        blockSize = hexUtil.howMuchBytesAreNeededToRepresentTheHighestIntegerInThisArray(new int[]{256*256,0});
        assertEquals(3, blockSize);
        blockSize = hexUtil.howMuchBytesAreNeededToRepresentTheHighestIntegerInThisArray(new int[]{0,256*256});
        assertEquals(3, blockSize);

    }

    
    @Test
    public void testintToByteArray() throws Exception {
        System.out.println("intToByteArray");
        int myInt = 0;
        byte[] expResult = new byte[]{(byte)0};
        int blockSize = hexUtil.howMuchBytesAreNeededToRepresentTheHighestIntegerInThisArray(new int[]{myInt});
        byte[] result = hexUtil.intToByteArray(myInt,blockSize);
        testByteArrays(expResult, result);

        myInt = 255;
        expResult = new byte[]{(byte)255};
        blockSize = hexUtil.howMuchBytesAreNeededToRepresentTheHighestIntegerInThisArray(new int[]{myInt});
        result = hexUtil.intToByteArray(myInt,blockSize);
        testByteArrays(expResult, result);
        
        myInt = 256;
        expResult = new byte[]{1,0};
        blockSize = hexUtil.howMuchBytesAreNeededToRepresentTheHighestIntegerInThisArray(new int[]{myInt});
        result = hexUtil.intToByteArray(myInt,blockSize);
        testByteArrays(expResult, result);
        
        myInt = 256*256+258;
        expResult = new byte[]{1,1,2};
        blockSize = hexUtil.howMuchBytesAreNeededToRepresentTheHighestIntegerInThisArray(new int[]{myInt});
        result = hexUtil.intToByteArray(myInt,blockSize);
        testByteArrays(expResult, result);
                

    }
            
              
    @Test
    public void testByteFromHexString() throws Exception {
        System.out.println("intToByteArray");
        String myIn = "0f";
        byte[] expResult = new byte[]{(byte)15};
        byte[] result = hexUtil.stringToByte(myIn);
        testByteArrays(expResult, result);

        myIn = "01";
        expResult = new byte[]{(byte)1};
        result = hexUtil.stringToByte(myIn);
        testByteArrays(expResult, result);
        
        myIn = "0101";
        expResult = new byte[]{(byte)1,(byte)1};
        result = hexUtil.stringToByte(myIn);
        testByteArrays(expResult, result);
        
        myIn = "02010101010101010101"; // length 10 test
        expResult = new byte[]{(byte)2,(byte)1,(byte)1,(byte)1,(byte)1,(byte)1,(byte)1,(byte)1,(byte)1,(byte)1};
        result = hexUtil.stringToByte(myIn);
        testByteArrays(expResult, result);
        
        myIn = "10";
        expResult = new byte[]{(byte)16};
        result = hexUtil.stringToByte(myIn);
        testByteArrays(expResult, result);
        
        myIn = "FF";
        expResult = new byte[]{(byte)255};
        result = hexUtil.stringToByte(myIn);
        testByteArrays(expResult, result);
     
        myIn = "FF00ab";
        expResult = new byte[]{(byte)255,(byte)00,(byte)171};
        result = hexUtil.stringToByte(myIn);
        testByteArrays(expResult, result);

    }


     /**
     * Test of concatenateBytes method, of class hexUtil.
     */
    @Test
    public void testConcatenateBytes() throws Exception {
        System.out.println("testConcatenateBytes");
        byte[] a = new byte[]{0};
        byte[] b = new byte[]{0};
        byte[] expResult = new byte[]{0,0};
        byte[] result = hexUtil.concatenateBytes(a,b);
        testByteArrays(expResult, result);

        try
        {
            a = new byte[]{};
            b = new byte[]{};
            expResult = new byte[]{};
            result = hexUtil.concatenateBytes(a, b);
            testByteArrays(expResult, result);
            fail("no hexUtilException");
        }
        catch (hexUtilException utilException)
        {
        }

        try
        {
            a = new byte[]{};
            b = new byte[]{1};
            expResult = new byte[]{1};
            result = hexUtil.concatenateBytes(a,b);
            testByteArrays(expResult, result);
            fail("no hexUtilException");
        }
        catch (hexUtilException utilException)
        {
        }
        

        a = new byte[]{2,1};
        b = new byte[]{3};
        expResult = new byte[]{2,1,3};
        result = hexUtil.concatenateBytes(a,b);
        testByteArrays(expResult, result);

        a = new byte[]{1,2,3,4,5,6,7};
        b = new byte[]{2,3,4};
        expResult = new byte[]{1,2,3,4,5,6,7,2,3,4};
        result = hexUtil.concatenateBytes(a,b);
        testByteArrays(expResult, result);


    }


    @Test
    public void test_splitAt() throws hexUtilException {
        System.out.println("splitAt");
        int position = 2;
        byte[] in = new byte[]{0, 1, 15, 5};
        byte[] expResult0 = new byte[]{0, 1};
        byte[] expResult1 = new byte[]{ 15,5};
        ArrayList<byte[]> result = hexUtil.splitAt(in, position);
        testByteArrays(expResult0, result.get(0));
        testByteArrays(expResult1, result.get(1));
        
        
        position = 0;
        in = new byte[]{0, 1, 15, 5};
        expResult0 = new byte[]{};
        expResult1 = new byte[]{ 0, 1,15,5};
        result = hexUtil.splitAt(in, position);
        testByteArrays(expResult0, result.get(0));
        testByteArrays(expResult1, result.get(1));
        
        
        position = 4;
        in = new byte[]{0, 1, 15, 5};
        expResult0 = new byte[]{0, 1,15,5};
        expResult1 = new byte[]{ };
        result = hexUtil.splitAt(in, position);
        testByteArrays(expResult0, result.get(0));
        testByteArrays(expResult1, result.get(1));
        
        
        position = 4;
        in = new byte[]{0, 1, 15, 5};
        expResult0 = new byte[]{0, 1,15,5};
        expResult1 = new byte[]{ };
        result = hexUtil.splitAt(in, position);
        testByteArrays(expResult0, result.get(0));
        testByteArrays(expResult1, result.get(1));
        

        


    }



    
    private void testByteArrays(byte[] a , byte[] b)
    {
        
        System.out.println("a:" + hexUtil.byteToHexString(a));
        System.out.println("b:" + hexUtil.byteToHexString(b));
        assertEquals(a.length, b.length);
        for(int i = 0 ; i < a.length ; i++)
        {
            assertEquals(a[i], b[i]);
        }
           
    
    }
}