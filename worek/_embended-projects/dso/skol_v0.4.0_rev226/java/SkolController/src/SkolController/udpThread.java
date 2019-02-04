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

import SkolUtil.*;
import SushUtil.*;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

/**
 * Specific Impl for Networking with UDP
 * @author sushi
 */
public   class udpThread extends communicationThread 
{
    private int localPort;
    private int remotePort;
    private String remoteHost;
    private DatagramSocket datagrammSocket;
    private InetAddress inetAddress;
    
    /**
     * Opens Network Channel etc.
     * @param myLocalPort
     * @param myRemotePort
     * @param myRemoteHost
     */
    public void init(int myLocalPort, int myRemotePort, String myRemoteHost)
    {
        localPort = myLocalPort;
        remotePort = myRemotePort;
        remoteHost = myRemoteHost;
        keepRunning =  true;

        try 
        {
            datagrammSocket = new DatagramSocket(localPort);
            datagrammSocket.setSoTimeout(100);
            inetAddress = (InetAddress.getByName(myRemoteHost));
        } 
        catch (SocketException ex) 
        {
            logger.logError("SocketException in Connect - cannot aquire UDP Port:" + localPort, ex);
            keepRunning =  false;
        }
        catch (UnknownHostException ex) 
        {
            logger.logError("UnknownHostException in Connect - cannot resolve Device HostName:" + myRemoteHost, ex);
            keepRunning =  false;
        }
    }
    
    /**
     * Main Function 
     * Responsible for receiving data - and delivering it to Main
     */
    public  void run() 
    {
        logger.logDebug("Network Communication started");
        DatagramPacket myPaket =  new DatagramPacket(new byte[Main.paketLength], Main.paketLength);
        
        while(keepRunning) 
        {

            try 
            {
                
                datagrammSocket.receive(myPaket);
                message currentMsg = new message(myPaket.getData(),myPaket.getLength());
                if(Main.showSerialDataStream)
                    logger.logDebug("Got DataPart: " + hexUtil.byteToHexString(currentMsg.rawMessage));

                Main.dispatchReceivedMessage(currentMsg);
            } 
            catch (SocketTimeoutException ex) 
            {
                //logger.logDebug("SocketTimeoutException");
                continue;
            }
            catch (IOException ex) 
            {
                logger.logError("ERROR IOException while trying to receive a packet", ex);
            }
            catch (Exception ex) 
            {
                logger.logError("ERROR Exception while trying to receive a packet: " , ex);
            }
            
            
        }
        datagrammSocket.close();
        logger.logInfo("UDP Communication Stopped");
    }
    
    /**
     * can be called to send Message to receiver
     * @param rawMessage  can be obtained from message.RawMessage
     */
    public void sendMessage(byte[] rawMessage)
    {
        DatagramPacket paket = new DatagramPacket(rawMessage , rawMessage.length);
        paket.setAddress(inetAddress);
        paket.setPort(remotePort);
        logger.logDebug("Sending Message: " + hexUtil.byteToHexString(rawMessage));
        try 
        {
            datagrammSocket.send(paket);
        } 
        catch (IOException ex) 
        {
           logger.logError("ERROR in sendig paket: " + rawMessage.toString(), ex);
        }
    }

}
