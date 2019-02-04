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

package SkolController;

import SkolUtil.message;
import SkolUtil.opCodes;
import SushUtil.hexUtil;
import SushUtil.hexUtilException;
import SushUtil.logger;
import gnu.io.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.TooManyListenersException;

/**
 *
 * @author sushi
 */
public class rs232Thread extends communicationThread 
{
    
    
    private OutputStream outputStream ;
    private SerialPort serialPort;
    private Object senderLock = new Object();
    public void init(String portName)
    {
 
        keepRunning =  true;
        CommPortIdentifier portIdentifier = null;
        try 
        {
            portIdentifier = CommPortIdentifier.getPortIdentifier(portName);

            if ( portIdentifier.isCurrentlyOwned() )
            {
                logger.logWarn("Error: Port is currently in use");
            }
            else
            {
                CommPort commPort = portIdentifier.open("CommUtil",2000);

                if ( commPort instanceof SerialPort )
                {
                    serialPort = (SerialPort) commPort;
                    serialPort.setSerialPortParams(115200, SerialPort.DATABITS_8, 
                                SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
                   

                    inputStream = serialPort.getInputStream();
                    outputStream = serialPort.getOutputStream();

                    serialPort.addEventListener(new SerialReader(inputStream));
                    serialPort.notifyOnDataAvailable(true);


                }
                else
                {
                    logger.logError("Only serial ports allowed.");
                }
                logger.logInfo("Connected to Serial Port: " + portName);
            }    
        } 
        catch (NoSuchPortException ex) 
        {
            logger.logError("NoSuchPortException: " + portName, ex);
            keepRunning =  false;
        }
        catch (PortInUseException ex) 
        {
            logger.logError("PortInUseException: " + portName, ex);
            keepRunning =  false;
        }
        catch (UnsupportedCommOperationException ex) 
        {
            logger.logError("UnsupportedCommOperationException: " + portName, ex);
            keepRunning =  false;
        }
        catch (IOException ex) 
        {
            logger.logError("IOException: " + portName, ex);
            keepRunning =  false;
        }
        catch(TooManyListenersException ex)
        {
            logger.logError("RXTX TooManyListenersException: " + portName, ex);
            keepRunning =  false;
        }
    }
       
    
     
    /**
     * Main Function 
     * Responsible for receiving data - and delivering it to Main
     */
    @Override
    public  void run() 
    {
        logger.logInfo("Serial Communication started");
        
        while(keepRunning) 
        {
            synchronized(this)
            {
                try 
                {
                    wait();               


                } 
                catch (InterruptedException ex) 
                {
                   logger.logDebug("RS 232 Thread InterruptedException " );
                }
            }
            
        }
        try
        {
            if(serialPort != null)
                serialPort.close();
        } 
        catch(Exception  ex)
        {
            logger.logError("Exception " , ex);
        }   
        

        
        logger.logInfo("Serial Communication Stopped");
    }

    
    
    @Override
    public void sendMessage(byte[] rawMessage)
    {
        synchronized(senderLock)
        {
            try
            {     
                if(Main.myGui.jCheckBox_showSerialDataStream.isSelected())
                    logger.logDebug("Will send " + hexUtil.byteToHexString(rawMessage));
                this.outputStream.write(rawMessage);    
            }
            catch ( IOException e )
            {
                logger.logError("Sending Error " , e);
            }  
        }
       
    }


    
    /**
     * returns 
     * @return HASHSET containing all avaiable Ports
     */
    public static HashSet<CommPortIdentifier> getAvailableSerialPorts() 
    {
        HashSet<CommPortIdentifier> h = new HashSet<CommPortIdentifier>();
        Enumeration thePorts = CommPortIdentifier.getPortIdentifiers();
        while (thePorts.hasMoreElements()) 
        {
            CommPortIdentifier com = (CommPortIdentifier) thePorts.nextElement();
            switch (com.getPortType()) {
            case CommPortIdentifier.PORT_SERIAL:
                try 
                {
                    CommPort thePort = com.open("CommUtil", 50);
                    thePort.close();
                    h.add(com);
                } 
                catch (PortInUseException e) 
                {
                    logger.logDebug("Port, "  + com.getName() + ", is in use.");
                    //System.out.println("Port, "  + com.getName() + ", is in use.");
                } 
                catch (Exception e) 
                {
                    logger.logError("Failed to open port " +  com.getName(), e);
                    //System.err.println();
                    //e.printStackTrace();
                }
            }
        }
        return h;
    }


}

    /**
     * Handles the input coming from the serial port. A new line character
     * is treated as the end of a block in this example. 
     */
    class SerialReader implements SerialPortEventListener 
    {
        //private byte[] buffer = new byte[1024];
        private InputStream inputStream;
        
        public SerialReader ( InputStream in )
        {
            this.inputStream = in;
        }
        
        private static int bufferSize = 2*2048;
        public void serialEvent(SerialPortEvent arg0) 
        {
            
            byte[] buffer = new byte[bufferSize];
            byte[] incompleteMessageBuffer = new byte[bufferSize];
            int incompleteMessageSize = 0;
            int len = -1;
            Boolean parseMsg = true;
            try
            {  
                
                while ( ( len = this.inputStream.read(buffer)) > 0 )
                {
                    for(int i = 0; i < len ; i ++)
                    {
                        incompleteMessageBuffer[i + incompleteMessageSize] = buffer[i];
                    }
                    if(Main.showSerialDataStream)
                        logger.logDebug("Got DataPart: " + hexUtil.byteToHexString(hexUtil.splitAt(buffer, len).get(0)));

                    incompleteMessageSize += len;

                    parseMsg = true;
                    if(incompleteMessageSize >=1) // Quick Check to avoid timeConsuming split
                    {
                        byte opCode = incompleteMessageBuffer[0];
                        if(opCode == opCodes.dataAquCh0  || opCode == opCodes.dataAquCh1) 
                        {
                            int dataBytes = Main.AdcSamplesPerFrameValue;
                            if(Main.AdcBitValue > 8)
                                dataBytes *= 2;
                            dataBytes *= Main.ResolutionMultiplier;
                            dataBytes += 3;
                            if(incompleteMessageSize <= dataBytes)
                                parseMsg = false;
                        }
                    }
                    if(parseMsg)
                    {
                        ArrayList<message> arrayList = message.extractMessages(incompleteMessageBuffer, incompleteMessageSize);

                        for(int i = 0 ; i < arrayList.size(); i ++)
                        {
                            message msg = arrayList.get(i);
                            if(i+1 == arrayList.size() && !msg.isValid)
                            {   // last element is not a valid element
                                if(arrayList.size() != 1) // if only one - everything is ok
                                {   // else - copy rest into buffer..
                                    for(int j = 0; j < msg.rawMessage.length; j++)
                                        incompleteMessageBuffer[j] = msg.rawMessage[j];
                                    incompleteMessageSize = msg.rawMessage.length;
                                }
                            }
                            else if(i+1 == arrayList.size() && msg.isValid)
                            {   // last elemnt is valid
                                if(Main.showSerialDataStream)
                                    logger.logDebug("Got Data: " + hexUtil.byteToHexString(msg.rawMessage));
                                Main.dispatchReceivedMessage(msg);
                                incompleteMessageSize = 0;
                            }
                            else
                            {   // valid elemnt
                                if(Main.showSerialDataStream)
                                    logger.logDebug("Got Data: " + hexUtil.byteToHexString(msg.rawMessage));
                                Main.dispatchReceivedMessage(msg);
                            }
                        }
                    } // if(parseMsg)
                } // while ( ( len = this.inputStream.read(buffer)) > 0 )
            }
            catch ( IOException e )
            {
                logger.logError("IOException " , e);
            }
            catch(Exception  ex)
            {
                logger.logError("Exception " , ex);
            }     
           
        }
        
        


    }

