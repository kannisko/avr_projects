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

import SushUtil.logger;

/**
 *
 * @author sush
 */
public class UnderSamplingCheckerThread extends Thread
{
    public boolean keepRunning = true;
    public int[]   dataArray = new int[]{0};
    public int     channel  = -1;
    @Override
    public  void run() 
    {
        while(keepRunning)
        {
            try 
            {
                synchronized(this)
                {
                    wait();
                }
                synchronized(dataArray)
                {
                    double meanVal = 0;
                    for(int i = 0;i < dataArray.length; i++)
                        meanVal += dataArray[i];
                    meanVal = meanVal / dataArray.length;
                    boolean armed = false;
                    int risingEdgeCounter = 0;
                    for(int i = 0;i < dataArray.length; i++)
                    {
                        if(armed == false)
                        {
                            if(dataArray[i] < meanVal*0.95 )
                                armed = true;
                        }
                        else
                        { 
                            if(dataArray[i] > meanVal*1.05 )
                            {    
                                armed = false;
                                risingEdgeCounter++;
                            }  
                        }
                    }
                    int dataPoints = Main.myGui.getAdcSamplesPerFrameValue();
//                    logger.logDebug("risingEdgeCounter" + risingEdgeCounter);
                    if(((double)dataPoints/5.0) < risingEdgeCounter )
                    {
                        logger.logWarn("Undersampling Warning! Decrease TimePerDiv Value on Channel " + channel);
                    }
                }
            }
            catch (InterruptedException ex) 
            {
                logger.logError("UnderSamplingCheckerThread InterruptedException", ex);
            }
        
        }
        
    }
}
