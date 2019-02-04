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
import SushUtil.hexUtilException;
import SushUtil.logger;
import java.util.ArrayList;


 
/**
 * see opCodes.java for Details
 * @author sushi
 */
public class message 
{
    /** opCode  */
    public byte opCode ;
    
    /** receivedData  as integer array */
    public int[] dataInt ;
    
    /** receivedData  as integer array */
    public byte[] dataByte;
    
    /** MagicByte */
    public byte magicByte ;
    
    /** crc  */
    public byte crc ;
    
    /** myRawMessage  */
    public byte[] rawMessage;

    public Boolean isValid = false;
    
    /** raw bytes wich were tailed to the origingal valid message */
    private byte[] messageTail  = new byte[]{};
    
    /**
     * takes a myRawMessage and splits it up 
     * @param myRawMessage byte[]
     */
    public message(byte[] myRawMessage) throws hexUtilException 
    {
        messageSplitter(myRawMessage);     
    }
    /**
     * takes a myRawMessage, cuts it to dataLen and splits it up
     * @param myRawMessage
     * @param dataLen
     */
    public message(byte[] myRawMessage,int dataLen) throws hexUtilException 
    {
        byte[] temp = new byte[dataLen];
        for (int i =0; i < dataLen; i ++)
            temp[i] = myRawMessage[i];
        messageSplitter(temp);
        
    }  
    /**
     * generates empty (invalid) message
     */
    public message()
    {
        isValid = false;    
    }
 
    
    /**
     * generates a new Simulated DataStreamStarts Message
     * Only for use in OsciReference
     * @param opCode MUST BE datastreamStart
     * @param myMagicByte
     * @param myData
     * @param dataBlockSize
     */
    public message(byte opCode,  byte[] myData, byte dataBlockSize) throws messageException 
    {
        byte myMagicByte = constants.magicByte;
        byte[] temp = generateDataStreamData(myData, dataBlockSize);
        
        messageBuilder(opCode, myMagicByte, temp);
    }

    private byte[] generateDataStreamData(byte[] myData, byte dataBlockSize) throws messageException 
    {
        byte[] tempByte ;
        if(myData.length == 0 )
        {
            tempByte = new byte[1] ;
            tempByte[0] = 0 ;
            dataBlockSize = 1;
        }
        else
        {
            tempByte = new byte[myData.length] ;
            for(int i = 0; i < myData.length ; i ++)
                tempByte[i] = myData[i] ;
        }
        
        
        
        
        if (dataBlockSize == 0)
            throw new messageException("dataBlockSize has at least to be 1");
        if(((int)tempByte.length)%((int)dataBlockSize) != 0)
            throw new messageException("if(tempByte%dataBlockSize != 0)");
        
        byte[] ret = new byte[2+1+tempByte.length];
        int dataBlockCnt = tempByte.length / dataBlockSize ;
        byte[] tmp = hexUtil.intToByteArray(dataBlockCnt, 2);
        ret[0] = tmp[0];
        ret[1] = tmp[1];
        ret[2] = dataBlockSize;
        for(int i = 0 ; i < tempByte.length;i++)
            ret[i+3] = tempByte[i];

        return ret;
    }
    
    /**
     * takes rawData byte Array 
     * 
     * @param rawData
     * @return
     */
    private void messageSplitter(byte[] myRawMessage) throws hexUtilException
    {
        
       if(!checkCrc(myRawMessage))
        {
            logger.logError("Message dropped because of wrong CRC");
            logger.logError(hexUtil.byteToHexString(myRawMessage));
            return;
        }
       
        rawMessage = myRawMessage;
        opCode = myRawMessage[0];
        if(opCode == (byte)255) // reset request
        {
            magicByte = 0;
            dataInt = new int[]{0};
            dataByte = new byte[]{0};
            return;
        
        
        }
        magicByte = myRawMessage[1];

       
        int dataBlockCount = hexUtil.byteToUnsignedInt(new byte[]{myRawMessage[2], myRawMessage[3]}); 
        int dataBlockSize =  hexUtil.byteToUnsignedInt(myRawMessage[4]);
        
        int dataPointerInt = 5;
        dataInt = new int[dataBlockCount];
        for(int i = 0; i < dataBlockCount; i++)
        {
            byte[] byteArray;
            switch(dataBlockSize)
            {
                case 1:
                    byteArray = new byte[]{myRawMessage[dataPointerInt]};
                    break;
                case 2:
                    byteArray = new byte[]{myRawMessage[dataPointerInt],myRawMessage[dataPointerInt+1]};
                    break;

                case 3:
                    byteArray = new byte[]{myRawMessage[dataPointerInt],myRawMessage[dataPointerInt+1],myRawMessage[dataPointerInt+2]};
                    break;
                default:
                    logger.logError("Message dropped dataBlock Size(max 3) = " + dataBlockSize);
                    logger.logError(hexUtil.byteToHexString(myRawMessage));
                    return;
            }

            dataInt[i] = hexUtil.byteToUnsignedInt(byteArray);
            dataPointerInt += dataBlockSize;
            if(i == 254)
              dataBlockSize=dataBlockSize +1-1;//fixme  

        }
        dataByte = new byte[dataBlockCount*dataBlockSize];
        
        for(int i = 0; i < dataBlockCount*dataBlockSize; i++)
        {
            dataByte[i] = myRawMessage[i+5];
        }     
            
        isValid = true;
                


    }
   
    private void messageBuilder(byte opCode,byte flowCount, byte[] rawData)
    {
        this.opCode =opCode;
        this.magicByte = flowCount;
        
        
        
        byte[] temp  = new byte[rawData.length + 2];
        temp[0] = opCode;
        temp[1] = flowCount;
        for(int i=  0 ; i < rawData.length;i++)
            temp[i+2] = rawData[i];
        
        rawMessage = appendCrc(temp);
        isValid = true;
        
    }
    
    
    
    private Boolean checkCrc(byte[] rawMessage)
    {
        return true;
        // Fixme Fake implementation
    }
    
    private byte[] appendCrc(byte[] rawMessageSoFar)
    {
        byte[] tmp = new byte[rawMessageSoFar.length + 1];
        for(int i = 0; i < rawMessageSoFar.length; i++)
            tmp[i] = rawMessageSoFar[i];
        
        crc = 0;
        tmp[tmp.length - 1 ] = crc;// CRC
        return tmp;
    }

    /**
     * 
     * @param rawMessage
     * @param lenSoFar  length int
     * @return  message (valid flag says if valid or not)
     */
    private static message isComplete(byte[] rawMessage) throws messageException, hexUtilException
    {
        int lenSoFar = rawMessage.length;

        if(lenSoFar < 7)
        {
            message m = new message(); 
            m.rawMessage = rawMessage;
            return m;
        }
        int blockCount = ( hexUtil.byteToUnsignedInt(rawMessage[2]) * 256) + hexUtil.byteToUnsignedInt(rawMessage[3]);
        int blockSize = hexUtil.byteToUnsignedInt(rawMessage[4]);
        int dataSize = blockCount * blockSize;
        if (lenSoFar < (6 + dataSize))
        {
            message m = new message(); 
            m.rawMessage = rawMessage;
            return m;
        }
        else if(lenSoFar > (6 + dataSize)) 
        {
             ArrayList<byte[]> arrayList = hexUtil.splitAt(rawMessage, (6 + dataSize));
             message m = new message(arrayList.get(0));
             m.messageTail = arrayList.get(1);
             return m;
        }
        else // message ok 
        {
            return new message(rawMessage);
        }
    }
    
    /**
     * Checks message, if it is longer than 2 bytes now, if it could be a valid message start block.
     * @param rawMessage
     * @param lenSoFar
     * @return
     * @throws 
     */
    private static Boolean isValidMessageStart(byte[] rawMessage) 
    {

        int lenSoFar = rawMessage.length;
//        System.out.println("isValidMessageStart:" + hexUtil.byteToHexString(rawMessage));
        if(lenSoFar >= 2)
            if(rawMessage[1] != constants.magicByte)
                return false;
        
        // 0xB0 = 176
        // 0xBF = 191
        int tmpInt = hexUtil.byteToUnsignedInt(rawMessage[0]);
        if( (tmpInt < 176) || (tmpInt > 191))
            return false;
        
        if(lenSoFar >4 ) // BlockSize also avaiable to check
        {
           if(hexUtil.byteToUnsignedInt(rawMessage[4]) > 3) // max BloxkSize is 3 bytes !
                return false;
        }
        
        return true;
    }
    
    /**
     * If a received Data Packet contains a valid message and the beginning 
     * of a new second message, this method can be used to split them up
     * 
     * @param rawMessage
     * @param lenSoFar
     * @return message[]   - valid or invalid messages in sorted order
     *                          if a invalid msg is there - its the last
     * 
     */
    public static ArrayList<message> extractMessages(byte[] rawMessage, int lenSoFar) throws hexUtilException, messageException
    {
        ArrayList<message> arrayList = new ArrayList<message>();

        byte[] tmpByteArray = hexUtil.splitAt(rawMessage, lenSoFar).get(0);
        int bytesToGo = tmpByteArray.length;
        while(bytesToGo > 0)
        {
            if(isValidMessageStart(tmpByteArray))
            {
                message m = isComplete(tmpByteArray);
                arrayList.add(m);
                if(m.isValid)
                {
                    tmpByteArray = m.messageTail;
                    bytesToGo = m.messageTail.length;
                }
                else
                    bytesToGo = 0;
            }
            else
                bytesToGo = 0;

        }

        return arrayList;
    }
    
    
    public static message JUNIT_ONLY_isComplete(byte[] rawMessage) throws messageException, hexUtilException
    {
        return isComplete(rawMessage);
    }
    public static Boolean JUNIT_ONLY_isValidMessageStart(byte[] rawMessage) 
    {
        return isValidMessageStart(rawMessage);
    }
    public byte[] JUNIT_ONLY_getTail()
    {
        return this.messageTail;
    }
}
