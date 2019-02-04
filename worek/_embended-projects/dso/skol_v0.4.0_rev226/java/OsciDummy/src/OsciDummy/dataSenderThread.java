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

import SkolUtil.opCodes;
import java.net.SocketAddress;
import SushUtil.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author sushi
 */
public class dataSenderThread extends Thread
{
    public Boolean keepRunning ;
    public SocketAddress socketAddress = null;
    public gui myGui = null;
    /**
     * default constructor
     * @param receiverAddress
     */
    public void init(SocketAddress receiverAddress ) 
    {
        socketAddress = receiverAddress;
    }
    
    private static int runningCnt = 0;
    
    
    /**
     * Main Function 
     */
    @Override
    public void run() 
    {
        logger.logDebug("Thread Started");
        synchronized(this)
            {
                try 
                {
                    wait();               


                } 
                catch (InterruptedException ex) 
                {
                   logger.logError("InterruptedException " );
                }
            }
        
        logger.logDebug("Thread Stopped");
    }
    
    public void sendDataFrameCh0()
    {
    keepRunning =  true;
        int[] dataIntArray0 = null;
        int[] dataIntArray1 = null;
        int oldSleepTime = -1;
        
        
        try 
        {
            
            
            
            //while(keepRunning) 
            {
                
                int dataCnt = 152;
                int maxheightAbsolut = 16384;
                
                int baseline = (int)maxheightAbsolut/2;
                dataIntArray0 = new int[dataCnt];

                double frequencyFactor = (double)(dataCnt/(double)10);
                int myGuiSinSlider = myGui.jSlider2_sin.getValue();
                double multiplier = (double)( (myGuiSinSlider*maxheightAbsolut)/200);
                for (int i = 0; i < dataCnt; i++)
                {
                    if(i == 0)
                        dataIntArray0[i] =(int) (baseline);
                    else
                    {
                        double sinVal = Math.sin( i / frequencyFactor) ;
                        sinVal *= multiplier;
                        dataIntArray0[i] =  baseline + (int)sinVal;
                    }
                        
  
                }
                
                
                try 
                {
                    byte dataBlockSize = (byte) hexUtil.howMuchBytesAreNeededToRepresentTheHighestIntegerInThisArray(dataIntArray0);
                    
                    Main.sendDataToOscilloscopeTool(opCodes.dataAquCh0,
                                                    SkolUtil.constants.magicByte, 
                                                    hexUtil.intToByteArray(dataIntArray0,dataBlockSize ),
                                                    dataBlockSize);
                    
                }
                catch(Exception e) 
                {
                    logger.logDebug("ERROR Exception");
                }

            }
        } 
        catch (Exception ex) 
        {
            logger.logDebug("ERROR: cannot generate test stimuli");
        }
        
    }
       
    public void sendDataFrameCh1()
    {
    keepRunning =  true;
        int[] dataIntArray0 = null;
        int[] dataIntArray1 = null;
        int oldSleepTime = -1;
        
        
        try 
        {
            
            
            
            //while(keepRunning) 
            {
                
                int dataCnt = 152;
                int maxheightAbsolut = 16384;
                
                int baseline = (int)maxheightAbsolut/2;

                dataIntArray1 = new int[dataCnt];

                Boolean up = true;
                double val = baseline;
                for(int i = 0; i < dataCnt; i ++)
                {
                    if(i%((int)dataCnt/4) == 0)
                        up =  !up;

                    dataIntArray1[i] = (int)val;
                    if(up)
                        val += 10*myGui.jSlider1_ZigZag.getValue();
                    else
                        val -= 10*myGui.jSlider1_ZigZag.getValue();

                    if(val < 0)
                    {
                        val = 0;
                        up =  !up;
                    }

                    if (val >= maxheightAbsolut)
                    {
                        val = maxheightAbsolut;
                        up =  !up;
                    }
                }

               //logger.logDebug(hexUtil.intToString(dataIntArray0,"_"));
                
                try 
                {
                    byte dataBlockSize = (byte) hexUtil.howMuchBytesAreNeededToRepresentTheHighestIntegerInThisArray(dataIntArray1);

                    Main.sendDataToOscilloscopeTool(opCodes.dataAquCh1,
                                                    SkolUtil.constants.magicByte,
                                                    hexUtil.intToByteArray(dataIntArray1,dataBlockSize ),
                                                    dataBlockSize);
                    int sleepTime = myGui.jSlider1_ZigZag.getValue();
                    int factor = 2 ; // ms
                    int mySleepTime = factor + (factor * sleepTime);
                    if (sleepTime != oldSleepTime)
                    {
                        oldSleepTime = sleepTime;
                        //logger.logDebug("cahnged Intervall to: " + mySleepTime + " ms");
                    }
                    
                    Thread.sleep(mySleepTime);
                }
                catch(InterruptedException e) 
                {
                    logger.logDebug("ERROR dataSenderThread InterruptedException");
                }

            }
        } 
        catch (Exception ex) 
        {
            logger.logDebug("ERROR: cannot generate test stimuli");
        }
        
    }
    
}
