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
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author sush
 */
public class FileWriterThread extends Thread
{
    boolean keepRunning = true;
    BufferedWriter bufferedWriter = null;
    
    
    public FileWriterThread(String FileName) 
    {
        try 
        {
            bufferedWriter = new BufferedWriter(new FileWriter(FileName));
        } 
        catch (IOException ex) 
        {
            logger.logError(" Could not open File " + FileName, ex);
        }
    }
    
    
    
    @Override
    public  void run() 
    {
        
        logger.logInfo("FileWriter started");
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
               logger.logError("InterruptedException " , ex);
            }
        }
        logger.logDebug("FileWriter Stopped");
        try 
        {
            bufferedWriter.close();
        } 
        catch (IOException ex) 
        {
            logger.logError("Could no close buffered Writer", ex);
        }
    }
    
    public void writeToFile(String myString)
    {
        try 
        {
            bufferedWriter.write(myString);
            bufferedWriter.newLine();
            bufferedWriter.flush();
        }
        catch (IOException ex) 
        {
            logger.logError("Could no write to File ", ex);
        }
    }

}
