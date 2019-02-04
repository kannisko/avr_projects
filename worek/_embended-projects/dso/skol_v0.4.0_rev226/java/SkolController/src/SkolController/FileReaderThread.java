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
import SushUtil.hexUtil;
import SushUtil.hexUtilException;
import SushUtil.logger;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author sush
 */
public class FileReaderThread extends Thread
{
    boolean keepRunning = true;
    BufferedReader bufferedReader = null;
    List list = null;
    public long numLines = 0;
    
    public FileReaderThread(String FileName) 
    {
        try 
        {
            bufferedReader = new BufferedReader(new FileReader(FileName));
            list = new ArrayList();
            list.clear();
            String line;
            do {
                    line = bufferedReader.readLine();
                    if (line != null)
                    {
                        list.add(line);
                        numLines++;
                    }
            }
            while (line != null);
            logger.logInfo("File opened : " + numLines + " Lines.");
            bufferedReader.close();
        } 
        catch (IOException ex) 
        {
            logger.logError(" Could not read from File " + FileName, ex);
        }
        
    }
    
    
    
    @Override
    public  void run() 
    {
        
        logger.logInfo("FileReader started");
        while(keepRunning) 
        {
            try 
            {
                synchronized(this)
                {
                     wait();               
                }
            } 
            catch (InterruptedException ex) 
            {
               logger.logError("InterruptedException ");
            }
        }
        logger.logDebug("FileReader Stopped");
        
    }

    void readLine(Integer selectedVal) 
    {
        String tmp = (String) list.get(selectedVal);
        byte[] tmpByteArray = null;
        message myMessage;
        try 
        {
            tmpByteArray = hexUtil.stringToByte(tmp);
            myMessage = new message(tmpByteArray);
            Main.dispatchReceivedMessage(myMessage);
        } 
        catch (hexUtilException ex) 
        {
            logger.logError("Could not create Message from Line " + selectedVal + " from File ");
        }

        
    }
    
    

}
