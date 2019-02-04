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

import SushUtil.hexUtil;
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
public class messageTest {

    public messageTest() {
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

    @Test
    public void testMessageGeneration_dataStreamStart() throws Exception
    {
        byte opCode = opCodes.dataAquCh0;
        int[] intData = new int[]{2};
        byte blockSize = (byte)hexUtil.howMuchBytesAreNeededToRepresentTheHighestIntegerInThisArray(intData);
        byte[] data = hexUtil.intToByteArray(intData, blockSize);
        message m = new message(opCode, data, blockSize);
        byte[] expResult = new byte[]{opCode,constants.magicByte,0,1,blockSize,2,0};
        testByteArrays(expResult,m.rawMessage);
        
            message m2;
            m2 = new message(m.rawMessage);
            assertEquals(opCode, m2.opCode);
            assertEquals(constants.magicByte,m2.magicByte  );
            assertEquals( blockSize*intData.length,  m2.dataByte.length);
            assertEquals(intData.length, m2.dataInt.length);
            testIntArrays(intData, m2.dataInt);
            testByteArrays(data, m2.dataByte);

        
        
        opCode = opCodes.dataAquCh0;
        intData = new int[]{2,0};
        blockSize = (byte)hexUtil.howMuchBytesAreNeededToRepresentTheHighestIntegerInThisArray(intData);
        data = hexUtil.intToByteArray(intData, blockSize);
        m = new message(opCode, data, blockSize);
        expResult = new byte[]{opCode,constants.magicByte,0,2,blockSize,2,0,0};
        testByteArrays(expResult,m.rawMessage);
                
            m2 = new message(m.rawMessage);
            assertEquals(opCode, m2.opCode);
            assertEquals(constants.magicByte,m2.magicByte  );
            assertEquals( blockSize*intData.length,  m2.dataByte.length);
            assertEquals(intData.length, m2.dataInt.length);
            testIntArrays(intData, m2.dataInt);
            testByteArrays(data, m2.dataByte);
        
        opCode = opCodes.dataAquCh0;
        intData = new int[]{256};
        blockSize = (byte)hexUtil.howMuchBytesAreNeededToRepresentTheHighestIntegerInThisArray(intData);
        data = hexUtil.intToByteArray(intData, blockSize);
        m = new message(opCode, data, blockSize);
        expResult = new byte[]{opCode,constants.magicByte,0,1,blockSize,1,0,0};
        testByteArrays(expResult,m.rawMessage); 
                
            m2 = new message(m.rawMessage);
            assertEquals(opCode, m2.opCode);
            assertEquals(constants.magicByte,m2.magicByte  );
            assertEquals( blockSize*intData.length,  m2.dataByte.length);
            assertEquals(intData.length, m2.dataInt.length);
            testIntArrays(intData, m2.dataInt);
            testByteArrays(data, m2.dataByte);
        
        opCode = opCodes.dataAquCh0;
        intData = new int[]{256,0};
        blockSize = (byte)hexUtil.howMuchBytesAreNeededToRepresentTheHighestIntegerInThisArray(intData);
        data = hexUtil.intToByteArray(intData, blockSize);
        m = new message(opCode, data, blockSize);
        expResult = new byte[]{opCode,constants.magicByte,0,2,blockSize,1,0,0,0,0};
        testByteArrays(expResult,m.rawMessage); 
                
            m2 = new message(m.rawMessage);
            assertEquals(opCode, m2.opCode);
            assertEquals(constants.magicByte,m2.magicByte  );
            assertEquals( blockSize*intData.length,  m2.dataByte.length);
            assertEquals(intData.length, m2.dataInt.length);
            testIntArrays(intData, m2.dataInt);
            testByteArrays(data, m2.dataByte);       
        
        opCode = opCodes.dataAquCh0;
        intData = new int[]{256,256*5+6};
        blockSize = (byte)hexUtil.howMuchBytesAreNeededToRepresentTheHighestIntegerInThisArray(intData);
        data = hexUtil.intToByteArray(intData, blockSize);
        m = new message(opCode, data, blockSize);
        expResult = new byte[]{opCode,constants.magicByte,0,2,blockSize,1,0,5,6,0};
        testByteArrays(expResult,m.rawMessage);
                
            m2 = new message(m.rawMessage);
            assertEquals(opCode, m2.opCode);
            assertEquals(constants.magicByte,m2.magicByte  );
            assertEquals( blockSize*intData.length,  m2.dataByte.length);
            assertEquals(intData.length, m2.dataInt.length);
            testIntArrays(intData, m2.dataInt);
            testByteArrays(data, m2.dataByte);
        
        
        opCode = opCodes.dataAquCh0;
        intData = new int[]{1,256*5+6};
        blockSize = (byte)hexUtil.howMuchBytesAreNeededToRepresentTheHighestIntegerInThisArray(intData);
        data = hexUtil.intToByteArray(intData, blockSize);
        m = new message(opCode, data, blockSize);
        expResult = new byte[]{opCode,constants.magicByte,0,2,blockSize,0,1,5,6,0};
        testByteArrays(expResult,m.rawMessage);
                
            m2 = new message(m.rawMessage);
            assertEquals(opCode, m2.opCode);
            assertEquals(constants.magicByte,m2.magicByte  );
            assertEquals( blockSize*intData.length,  m2.dataByte.length);
            assertEquals(intData.length, m2.dataInt.length);
            testIntArrays(intData, m2.dataInt);
            testByteArrays(data, m2.dataByte);
        
 
                
        opCode = opCodes.getTimeDividerCh0;
        intData = new int[]{};
        blockSize = (byte)hexUtil.howMuchBytesAreNeededToRepresentTheHighestIntegerInThisArray(intData);
        data = hexUtil.intToByteArray(new int[]{0}, blockSize);
        m = new message(opCode, data, blockSize);
        expResult = new byte[]{opCode,constants.magicByte,0,1,blockSize,0,0};
        testByteArrays(expResult,m.rawMessage); 
                
            m2 = new message(m.rawMessage);
            assertEquals(opCode, m2.opCode);
            assertEquals(constants.magicByte,m2.magicByte  );
            assertEquals( blockSize*intData.length+1,  m2.dataByte.length);
            assertEquals(intData.length+1, m2.dataInt.length);
            testIntArrays(new int[]{0}, m2.dataInt);
            testByteArrays(data, m2.dataByte);
                
        opCode = opCodes.dataAquCh0;
        intData = new int[]{};
        blockSize = (byte)hexUtil.howMuchBytesAreNeededToRepresentTheHighestIntegerInThisArray(intData);
        data = hexUtil.intToByteArray(new int[]{0}, blockSize);
        m = new message(opCode, data, blockSize);
        expResult = new byte[]{opCode,constants.magicByte,0,1,blockSize,0,0};
        testByteArrays(expResult,m.rawMessage);
                
            m2 = new message(m.rawMessage);
            assertEquals(opCode, m2.opCode);
            assertEquals(constants.magicByte,m2.magicByte  );
            assertEquals( blockSize*intData.length+1,  m2.dataByte.length);
            assertEquals(intData.length+1, m2.dataInt.length);
            testIntArrays(new int[]{0}, m2.dataInt);
            testByteArrays(data, m2.dataByte);
                
                
        opCode = opCodes.setTimeDividerCh0;
        intData = new int[]{8};
        blockSize = (byte)hexUtil.howMuchBytesAreNeededToRepresentTheHighestIntegerInThisArray(intData);
        data = hexUtil.intToByteArray(intData, blockSize);
        m = new message(opCode, data, blockSize);
        expResult = new byte[]{opCode,constants.magicByte,0,1,blockSize,8,0};
        testByteArrays(expResult,m.rawMessage);
                
            m2 = new message(m.rawMessage);
            assertEquals(opCode, m2.opCode);
            assertEquals(constants.magicByte,m2.magicByte  );
            assertEquals( blockSize*intData.length,  m2.dataByte.length);
            assertEquals(intData.length, m2.dataInt.length);
            testIntArrays(intData, m2.dataInt);
            testByteArrays(data, m2.dataByte);
    }
    
    @Test
    public void test_isComplete() throws Exception
    {
        byte opCode = opCodes.dataAquCh0;
        byte[] tmp;
        int[] intData = new int[]{2};
        byte blockSize = (byte)hexUtil.howMuchBytesAreNeededToRepresentTheHighestIntegerInThisArray(intData);
        byte[] data = hexUtil.intToByteArray(intData, blockSize);
        message m = new message(opCode, data, blockSize);
        m = message.JUNIT_ONLY_isComplete(m.rawMessage);
        assertTrue(m.isValid);
        testByteArrays(new byte[]{},m.JUNIT_ONLY_getTail());

        
        opCode = opCodes.dataAquCh0;
        intData = new int[]{2,3,4};
        blockSize = (byte)hexUtil.howMuchBytesAreNeededToRepresentTheHighestIntegerInThisArray(intData);
        data = hexUtil.intToByteArray(intData, blockSize);
        m = new message(opCode, data, blockSize);
        m = message.JUNIT_ONLY_isComplete(m.rawMessage);
        assertTrue(m.isValid);
        testByteArrays(new byte[]{},m.JUNIT_ONLY_getTail());

        opCode = opCodes.dataAquCh0;
        intData = new int[]{2,3,4};
        blockSize = (byte)hexUtil.howMuchBytesAreNeededToRepresentTheHighestIntegerInThisArray(intData);
        data = hexUtil.intToByteArray(intData, blockSize);
        m = new message(opCode, data, blockSize);
        tmp = new byte[m.rawMessage.length - 1];
        for(int i = 0; i < (m.rawMessage.length - 1);i++)
            tmp[i] = m.rawMessage[i];
        m = message.JUNIT_ONLY_isComplete(tmp);
        assertFalse(m.isValid);
        testByteArrays(new byte[]{},m.JUNIT_ONLY_getTail());
        
        opCode = opCodes.dataAquCh0;
        intData = new int[]{2,3,4};
        blockSize = (byte)hexUtil.howMuchBytesAreNeededToRepresentTheHighestIntegerInThisArray(intData);
        data = hexUtil.intToByteArray(intData, blockSize);
        m = new message(opCode, data, blockSize);
        tmp = new byte[m.rawMessage.length - 4];
        for(int i = 0; i < (m.rawMessage.length - 4);i++)
            tmp[i] = m.rawMessage[i];
        m = message.JUNIT_ONLY_isComplete(tmp);
        assertFalse(m.isValid);
        testByteArrays(new byte[]{},m.JUNIT_ONLY_getTail());
        
        opCode = opCodes.dataAquCh0;
        intData = new int[]{2,3,4};
        blockSize = (byte)hexUtil.howMuchBytesAreNeededToRepresentTheHighestIntegerInThisArray(intData);
        data = hexUtil.intToByteArray(intData, blockSize);
        m = new message(opCode, data, blockSize);
        tmp = new byte[m.rawMessage.length +1];
        for(int i = 0; i < (m.rawMessage.length );i++)
            tmp[i] = m.rawMessage[i];
        tmp[tmp.length-1] = 2;
        m = message.JUNIT_ONLY_isComplete(tmp);
        assertTrue(m.isValid);
        testByteArrays(new byte[]{2},m.JUNIT_ONLY_getTail());
        
        opCode = opCodes.dataAquCh0;
        intData = new int[]{2,3,4};
        blockSize = (byte)hexUtil.howMuchBytesAreNeededToRepresentTheHighestIntegerInThisArray(intData);
        data = hexUtil.intToByteArray(intData, blockSize);
        m = new message(opCode, data, blockSize);
        tmp = new byte[m.rawMessage.length +1];
        for(int i = 0; i < (m.rawMessage.length );i++)
            tmp[i] = m.rawMessage[i];
        tmp[tmp.length-1] = opCodes.getTimeDividerCh0;
        m = message.JUNIT_ONLY_isComplete(tmp);
        assertTrue(m.isValid);
        testByteArrays(new byte[]{opCodes.getTimeDividerCh0},m.JUNIT_ONLY_getTail());
        
        opCode = opCodes.dataAquCh0;
        intData = new int[]{2,3,4};
        blockSize = (byte)hexUtil.howMuchBytesAreNeededToRepresentTheHighestIntegerInThisArray(intData);
        data = hexUtil.intToByteArray(intData, blockSize);
        m = new message(opCode, data, blockSize);
        tmp = new byte[m.rawMessage.length +2];
        for(int i = 0; i < (m.rawMessage.length );i++)
            tmp[i] = m.rawMessage[i];
        tmp[tmp.length-2] = opCodes.getTimeDividerCh0;
        tmp[tmp.length-1] = constants.magicByte;
        m = message.JUNIT_ONLY_isComplete(tmp);
        assertTrue(m.isValid);
        testByteArrays(new byte[]{opCodes.getTimeDividerCh0,constants.magicByte},m.JUNIT_ONLY_getTail());
        
        opCode = opCodes.dataAquCh0;
        intData = new int[]{2,3,4};
        blockSize = (byte)hexUtil.howMuchBytesAreNeededToRepresentTheHighestIntegerInThisArray(intData);
        data = hexUtil.intToByteArray(intData, blockSize);
        m = new message(opCode, data, blockSize);
        tmp = new byte[m.rawMessage.length +2];
        for(int i = 0; i < (m.rawMessage.length);i++)
            tmp[i] = m.rawMessage[i];
        tmp[tmp.length-2] = opCodes.getTimeDividerCh0;
        tmp[tmp.length-1] = constants.magicByte -1;
        m = message.JUNIT_ONLY_isComplete(tmp);
        assertTrue(m.isValid);
        testByteArrays(new byte[]{opCodes.getTimeDividerCh0,constants.magicByte-1},m.JUNIT_ONLY_getTail());

    }
 
    @Test
    public void test_isValidMessageStart() throws Exception
    {
        byte opCode = opCodes.dataAquCh0;
        byte[] tmp;
        int[] intData = new int[]{2};
        byte blockSize = (byte)hexUtil.howMuchBytesAreNeededToRepresentTheHighestIntegerInThisArray(intData);
        byte[] data = hexUtil.intToByteArray(intData, blockSize);
        message m = new message(opCode, data, blockSize);
        Boolean expResult = true;
        assertEquals(expResult,message.JUNIT_ONLY_isValidMessageStart(m.rawMessage) );
        
        opCode = opCodes.dataAquCh0;
        intData = new int[]{2,3,4};
        blockSize = (byte)hexUtil.howMuchBytesAreNeededToRepresentTheHighestIntegerInThisArray(intData);
        data = hexUtil.intToByteArray(intData, blockSize);
        m = new message(opCode, data, blockSize);
        expResult = true;
        assertEquals(expResult,message.JUNIT_ONLY_isValidMessageStart(m.rawMessage) );
        
        opCode = opCodes.dataAquCh0;
        intData = new int[]{2};
        blockSize = (byte)hexUtil.howMuchBytesAreNeededToRepresentTheHighestIntegerInThisArray(intData);
        data = hexUtil.intToByteArray(intData, blockSize);
        m = new message(opCode, data, blockSize);
        expResult = true;
        assertEquals(expResult,message.JUNIT_ONLY_isValidMessageStart(m.rawMessage) );
        
        opCode = opCodes.dataAquCh0;
        tmp = new byte[]{opCode};
        expResult = true;
        assertEquals(expResult,message.JUNIT_ONLY_isValidMessageStart(tmp) );
        
        opCode = opCodes.dataAquCh0;
        tmp = new byte[]{(byte)255};
        expResult = false;
        assertEquals(expResult,message.JUNIT_ONLY_isValidMessageStart(tmp) );
        
        opCode = opCodes.dataAquCh0;
        tmp = new byte[]{(byte)05};
        expResult = false;
        assertEquals(expResult,message.JUNIT_ONLY_isValidMessageStart(tmp) );
        
        opCode = opCodes.dataAquCh0;
        tmp = new byte[]{opCode, constants.magicByte};
        expResult = true;
        assertEquals(expResult,message.JUNIT_ONLY_isValidMessageStart(tmp) );
        
        opCode = opCodes.dataAquCh0;
        tmp = new byte[]{opCode, (constants.magicByte -1)};
        expResult = false;
        assertEquals(expResult,message.JUNIT_ONLY_isValidMessageStart(tmp) );
        
        opCode = opCodes.dataAquCh0;
        tmp = new byte[]{opCode, constants.magicByte,0,0,0,0};
        expResult = true;
        assertEquals(expResult,message.JUNIT_ONLY_isValidMessageStart(tmp) );
        
        opCode = opCodes.dataAquCh0;
        tmp = new byte[]{opCode, constants.magicByte,0,0,0,0,0,0,0,0};
        expResult = true;
        assertEquals(expResult,message.JUNIT_ONLY_isValidMessageStart(tmp) );

        

    }
     @Test
    public void test_splitMessages() throws Exception
    {
        byte opCode = opCodes.dataAquCh0;
        byte[] tmp;
        byte[] m2Cut;
        int[] intData = new int[]{2};
        byte blockSize = (byte)hexUtil.howMuchBytesAreNeededToRepresentTheHighestIntegerInThisArray(intData);
        byte[] data = hexUtil.intToByteArray(intData, blockSize);
        message m = new message(opCode, data, blockSize);
        message m2;
        ArrayList<message> arrayList = message.extractMessages(m.rawMessage,m.rawMessage.length);
        testByteArrays(m.rawMessage, arrayList.get(0).rawMessage);
        assertEquals(1, arrayList.size());
        
        opCode = opCodes.dataAquCh0;
        intData = new int[]{2};
        blockSize = (byte)hexUtil.howMuchBytesAreNeededToRepresentTheHighestIntegerInThisArray(intData);
        data = hexUtil.intToByteArray(intData, blockSize);
        m = new message(opCode, data, blockSize);
        m2 = new message(opCodes.getTriggerMode, data, blockSize);
        tmp = hexUtil.concatenateBytes(m.rawMessage, m2.rawMessage);
        arrayList = message.extractMessages(tmp,tmp.length);
        assertEquals(2, arrayList.size());
        testByteArrays(m.rawMessage, arrayList.get(0).rawMessage);
        testByteArrays(m2.rawMessage, arrayList.get(1).rawMessage);
        assertTrue(arrayList.get(0).isValid);
        assertTrue(arrayList.get(1).isValid);
        
        opCode = opCodes.dataAquCh0;
        intData = new int[]{2};
        blockSize = (byte)hexUtil.howMuchBytesAreNeededToRepresentTheHighestIntegerInThisArray(intData);
        data = hexUtil.intToByteArray(intData, blockSize);
        m = new message(opCode, data, blockSize);
        m2 = new message(opCodes.getTriggerMode, data, blockSize);
        int cutSize = 2;
        m2Cut = new byte[cutSize];
        for(int i  = 0 ; i < cutSize; i ++)
            m2Cut[i] = m2.rawMessage[i];
        tmp = hexUtil.concatenateBytes(m.rawMessage, m2Cut);
        arrayList = message.extractMessages(tmp,tmp.length);
        assertEquals(2, arrayList.size());
        testByteArrays(m.rawMessage , arrayList.get(0).rawMessage);
        testByteArrays(m2Cut        , arrayList.get(1).rawMessage);
        assertTrue(arrayList.get(0).isValid);
        assertFalse(arrayList.get(1).isValid);
        
        opCode = opCodes.dataAquCh0;
        intData = new int[]{2};
        blockSize = (byte)hexUtil.howMuchBytesAreNeededToRepresentTheHighestIntegerInThisArray(intData);
        data = hexUtil.intToByteArray(intData, blockSize);
        m = new message(opCode, data, blockSize);
        m2 = new message(opCodes.getTriggerMode, data, blockSize);
        cutSize = 1;
        m2Cut = new byte[cutSize];
        for(int i  = 0 ; i < cutSize; i ++)
            m2Cut[i] = m2.rawMessage[i];
        tmp = hexUtil.concatenateBytes(m.rawMessage, m2Cut);
        arrayList = message.extractMessages(tmp,tmp.length);
        assertEquals(2, arrayList.size());
        testByteArrays(m.rawMessage , arrayList.get(0).rawMessage);
        testByteArrays(m2Cut        , arrayList.get(1).rawMessage);
        assertTrue(arrayList.get(0).isValid);
        assertFalse(arrayList.get(1).isValid);
        
        opCode = opCodes.dataAquCh0;
        intData = new int[]{2};
        blockSize = (byte)hexUtil.howMuchBytesAreNeededToRepresentTheHighestIntegerInThisArray(intData);
        data = hexUtil.intToByteArray(intData, blockSize);
        m = new message(opCode, data, blockSize);
        m2 = new message(opCodes.getTriggerMode, data, blockSize);
        cutSize = 5;
        m2Cut = new byte[cutSize];
        for(int i  = 0 ; i < cutSize; i ++)
            m2Cut[i] = m2.rawMessage[i];
        tmp = hexUtil.concatenateBytes(m.rawMessage, m2Cut);
        arrayList = message.extractMessages(tmp,tmp.length);
        assertEquals(2, arrayList.size());
        testByteArrays(m.rawMessage , arrayList.get(0).rawMessage);
        testByteArrays(m2Cut        , arrayList.get(1).rawMessage);
        assertTrue(arrayList.get(0).isValid);
        assertFalse(arrayList.get(1).isValid);

        
        opCode = opCodes.dataAquCh0;
        intData = new int[]{2};
        blockSize = (byte)hexUtil.howMuchBytesAreNeededToRepresentTheHighestIntegerInThisArray(intData);
        data = hexUtil.intToByteArray(intData, blockSize);
        m = new message(opCode, data, blockSize);
        m2 = new message(opCodes.getTriggerMode, data, blockSize);
        message m3 = new message(opCodes.getTimeDividerCh0, data, blockSize);
        cutSize = 5;
        m2Cut = new byte[cutSize];
        for(int i  = 0 ; i < cutSize; i ++)
            m2Cut[i] = m2.rawMessage[i];
        tmp = hexUtil.concatenateBytes(m.rawMessage, m3.rawMessage);
        tmp = hexUtil.concatenateBytes(tmp, m2Cut);
        arrayList = message.extractMessages(tmp,tmp.length);
        assertEquals(3, arrayList.size());
        testByteArrays(m.rawMessage , arrayList.get(0).rawMessage);
        testByteArrays(m3.rawMessage, arrayList.get(1).rawMessage);
        testByteArrays(m2Cut        , arrayList.get(2).rawMessage);
        assertTrue(arrayList.get(0).isValid);
        assertTrue(arrayList.get(1).isValid);
        assertFalse(arrayList.get(2).isValid);

        



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
    
  
    
    private void testIntArrays(int[] a , int[] b)
    {
        //System.out.println("a:" + hexUtil.byteToHexString(a));
        //System.out.println("b:" + hexUtil.byteToHexString(b));
        assertEquals(a.length, b.length);
        for(int i = 0 ; i < a.length ; i++)
        {

             assertEquals(a[i], b[i]);
        }
           
    
    }
}