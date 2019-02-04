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

package OsciDummy;

import SkolUtil.constants;
import SkolUtil.message;
import SkolUtil.opCodes;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.SocketException;
import SushUtil.*;

/**
 *
 * @author sushi
 */
public class Main 
{

    public static int myPort;
    public static DatagramSocket datagrammSocket;
    private static int paketLength = 20;
    private static gui myGui;
    private static dataSenderThread senderThread = null;
    private static SocketAddress socketAddress = null;
    public static byte flowCounter = 0;
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) 
    {
        myGui = new gui();
        logger.jtextArea = myGui.jTextAreaMsgOutput;
        if(!parseInput(args))
            return;
        try 
        {
            datagrammSocket = new DatagramSocket(myPort);
        } 
        catch (SocketException ex) 
        {
            logger.logDebug("SocketException in main - cannot aquire UDP Port:" + myPort);
        }
        DatagramPacket myPaket =  new DatagramPacket(new byte[paketLength], paketLength);
        myGui.setVisible(true);
        
        logger.logDebug("Oscilloscope Reference started on Port: " + myPort);
        logger.logInfo("Use of TimeDivider not implementd yet !");
        logger.logInfo("Use of any Trigger Function not implementd yet !");
        logger.logInfo("Only simple Data Aquisition possible.");
        while(true)
        {
            
            try 
            {
                datagrammSocket.receive(myPaket);
                
            } 
            catch (IOException ex) 
            {
                logger.logDebug("IOException while trying to receive a packet");
            }
            InetSocketAddress add = (InetSocketAddress)myPaket.getSocketAddress();
            
            message myMessage;
            try 
            {
                myMessage = new message(myPaket.getData(), myPaket.getLength());
                byte[] dataArray = myMessage.rawMessage;

                //String text = new String(myPaket.getData(), 0, myPaket.getLength());
                logger.logDebug("received: " + hexUtil.byteToHexString(dataArray));

                if(socketAddress == null)
                    socketAddress = myPaket.getSocketAddress();

                switch(dataArray[0])
                {
                    case opCodes.getTimeDividerCh0:
                        //getTimePerDiv(dataArray);
                        break;
                    case opCodes.setTimeDividerCh0:
                        //setTimePerDiv(dataArray);
                        break;
                    case opCodes.dataAquCh0:
                        //sendDataToOscilloscopeTool(opCodes.dataAquCh0, SkolUtil.constants.magicByte, new byte[]{opCodes.dataAquCh0},(byte) 1);
                        if(senderThread == null)
                        {
                            senderThread = new dataSenderThread();
                            senderThread.myGui = Main.myGui;
                            senderThread.init(myPaket.getSocketAddress());
                            senderThread.start();
                        }
                        senderThread.sendDataFrameCh0();
                        senderThread.sendDataFrameCh1();
                        break;


                    default:
                        logger.logDebug("Main: unknown OpCode!");


                }
            } 
            catch (Exception ex) 
            {
                logger.logError(" in  while(true)");
               
            }
            
            

        }

    }
    
//    private static void getTimePerDiv(byte[] data)
//    {
//        sendDataToOscilloscopeTool(opCodes.response, SkolUtil.constants.magicByte, 
//                new byte[]{opCodes.getTimeDividerCh0,constants.s_us1 },(byte) 1);
//
//    }
//    private static void getVoltsPerDiv(byte[] data)
//    {
//        sendDataToOscilloscopeTool(opCodes.response, SkolUtil.constants.magicByte, 
//                new byte[]{opCodes.getVoltsDivider,constants.V_mv500 },(byte) 1);
//    }
//    private static void setTimePerDiv(byte[] data)
//    {
//        sendDataToOscilloscopeTool(opCodes.response, SkolUtil.constants.magicByte, 
//                new byte[]{opCodes.setTimeDividerCh0,constants.ERROR },(byte) 1);
//    }
//    private static void setVoltsPerDiv(byte[] data)
//    { 
//        sendDataToOscilloscopeTool(opCodes.response, SkolUtil.constants.magicByte, 
//                new byte[]{opCodes.setVoltsDivide,constants.ERROR },(byte) 1);
//    }


    public static void sendDataToOscilloscopeTool(byte opCode, byte myFlowCount , byte[] myData, byte dataBlockSize)
    {
        message m = null;
        try 
        {
            m = new message(opCode,  myData, dataBlockSize);
            //logger.logDebug("send: " + hexUtil.byteToHexString(m.rawMessage));
        
            DatagramPacket paket = new DatagramPacket(m.rawMessage , m.rawMessage.length);

            paket.setSocketAddress(socketAddress );
            try 
            {
                datagrammSocket.send(paket);
            } 
            catch (IOException ex) 
            {
               logger.logDebug("Error in sendig paket: " + m.rawMessage.toString());
            }
        } 
        catch (Exception ex) 
        {
            logger.logError(" in sendDataToOscilloscopeTool");
        }
        
        
    }
    
    private static Boolean parseInput(String[] args) 
    {
        if (args.length != 1)
        {
            myPort = 2000;
            return true;
        }
        
        myPort = Integer.valueOf( args[0] ).intValue();  
        
        return true;
    }

}
