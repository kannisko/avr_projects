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


import SushUtil.hexUtil;
import SushUtil.logger;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.InvalidPropertiesFormatException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;



/**
 *
 * @author sush
 */
public class TimeDividerHelper 
{
    public int itemCount ;
    
    
    // change this to generate more or less choices, starting from the smallest one.
    private int maxItemLevels = 4;
    
    private int systemClockMhz;
    private int cyclesForSingleDaq;
    private int samplesPerFrame;
    
    private littleHelper[] myTimeDividers ;
    private double systemClockPeriodTimeNS = 0.0;

    public TimeDividerHelper(String xmlSettingsFile)
    {

        try
        {
            Properties properties = new Properties();
            FileInputStream fis = new FileInputStream(xmlSettingsFile);
            properties.loadFromXML(fis);
            //properties.list(System.out);
            itemCount = Integer.valueOf(properties.getProperty("TimePerDivEntrys"));
            myTimeDividers = new littleHelper[itemCount];
            for(int i = 0 ; i < itemCount ; i ++)
            {
                String propertyName;
                if(i < 10)
                    propertyName = "TimePerDivEntry0" + i;
                else
                    propertyName = "TimePerDivEntry" + i;

                myTimeDividers[i] = new littleHelper( properties.getProperty(propertyName));
            }
            
            fis.close();
            //myGui.setSerialPortName();
        } 
        catch (InvalidPropertiesFormatException ex)
        {
            Logger.getLogger(TimeDividerHelper.class.getName()).log(Level.SEVERE, null, ex);
        }catch (IOException ex)
        {
            logger.logError("Error Reading XML Config File: '" + xmlSettingsFile + "'", ex);
        }
    }

    public TimeDividerHelper(int systemClockMhz, int cyclesForSingleDaq, int samplesPerFrame)
    {
        this.itemCount = 3*maxItemLevels;
        this.systemClockMhz = systemClockMhz;
        this.samplesPerFrame = samplesPerFrame;
        this.cyclesForSingleDaq = cyclesForSingleDaq;
        systemClockPeriodTimeNS = 1000.0 / (systemClockMhz );
        double adcTimePerSingleAquFrameBaseNS  = (systemClockPeriodTimeNS * cyclesForSingleDaq * samplesPerFrame);

        // disabled because this should only be done through XML file from now on
        littleHelper[]  myTimeDividersTmp = new littleHelper[maxItemLevels*3];
        


        // Following Value should be very very close to some 10^n  (e.g. 10 or 100 or 10000 etc.)
        // So modify all three parameters 
        //   int systemClockMhz, int cyclesForSingleDaq, int samplesPerFrame
        // in a reasonable way so that this point is fullfilled.
        // Because of after that, all other TimePerDiv Values are generated automatically. 
        // Starting here with a value that is 10^n sec per Div as smallest value
        // Followed by some 2*10^n and 5*10^n values as on real world Oscis.
        int adcTimePerSingleAquFrameBaseNS_integer = ((int) (adcTimePerSingleAquFrameBaseNS/1000)) * 1000;
        
        
        // 
        double castError = 100*Math.abs( 1.0 - (adcTimePerSingleAquFrameBaseNS / (double)adcTimePerSingleAquFrameBaseNS_integer) );
        if(castError > 1) // in percent
            logger.logError("Error in TimeDividerHelper too big  int:" + adcTimePerSingleAquFrameBaseNS_integer + " double:" + adcTimePerSingleAquFrameBaseNS);
        
        int cnt = 0;
        logger.logDebug("id" + ": "  + " representation" +
                "\t: "  + "divider" +
                "\t: "  + "TimeNSperDiv" +
                "\t: "  + "TimeUSperDiv" +
                "\t: "  + "TimeMSperDiv" +
                "\t: "  + "wholeFrameTimeNS" +
                "\t: "  + "wholeFrameTimeUS" +
                "\t: "  + "wholeFrameTimeMS"  +
                "\t: "  + "divider" );
        for(int i = 0; i < maxItemLevels; i ++)
        {
            for (int j = 1 ; j <= 5 ; )
            {
                double tempDivider = ((java.lang.Math.pow(10, i) ) * j);
                myTimeDividersTmp[cnt] = new littleHelper( tempDivider ,(double) adcTimePerSingleAquFrameBaseNS_integer);
                logger.logDebug(cnt + ": "  + myTimeDividersTmp[cnt].representation +
                                "\t: "  + myTimeDividersTmp[cnt].divider +
                                "\t: "  + myTimeDividersTmp[cnt].TimeNSperDiv +
                                "\t: "  + myTimeDividersTmp[cnt].TimeUSperDiv +
                                "\t: "  + myTimeDividersTmp[cnt].TimeMSperDiv +
                                "\t: "  + myTimeDividersTmp[cnt].wholeFrameTimeNS +
                                "\t: "  + myTimeDividersTmp[cnt].wholeFrameTimeUS +
                                "\t: "  + myTimeDividersTmp[cnt].wholeFrameTimeMS  +
                                "\t: 0x"  + hexUtil.byteToHexString(hexUtil.intToByteArray((int)myTimeDividersTmp[cnt].divider -1, 3)));
                cnt++;
                if(j == 1)
                    j = 2;
                else if (j == 2)
                    j = 5;
                else if (j == 5)
                    j = 6;
                    
            }
        }
        
    }
    
    public String getItem(int i)
    {
        return myTimeDividers[i].representation;
    }
    public int getDividerIndex(String val) throws Exception
    {
        for(int i = 0; i < itemCount; i++)
        {
            if(myTimeDividers[i].representation.compareTo(val) == 0)
                return i;
        }
        throw new Exception("TimeDividerHelper could not Find : " + val);
    }


}
class littleHelper
{
    public double divider = -1.0;

    
    
    // calculated values
    public double wholeFrameTimeNS = 0.0;
    public double wholeFrameTimeUS = 0.0;
    public double wholeFrameTimeMS = 0.0;
    public double TimeNSperDiv = 0.0;
    public double TimeUSperDiv = 0.0;
    public double TimeMSperDiv = 0.0;
    public String representation ;

    public littleHelper(double divider, double adcTimePerSingleAquBaseNS)
    {
        this.divider = divider;
        wholeFrameTimeNS = (adcTimePerSingleAquBaseNS * divider);
        wholeFrameTimeUS = ((adcTimePerSingleAquBaseNS/1000)* divider);
        wholeFrameTimeMS = ((adcTimePerSingleAquBaseNS/1000000) * divider);


        TimeNSperDiv = wholeFrameTimeNS / 10.0;
        TimeUSperDiv = wholeFrameTimeUS / 10.0;
        TimeMSperDiv = wholeFrameTimeMS / 10.0;
        calculateRepresentationString();
    }

    public littleHelper(String representation)
    {
        this.representation = representation;

        divider = -1;
        wholeFrameTimeNS = -1;
        wholeFrameTimeUS = -1;
        wholeFrameTimeMS = -1;
        TimeNSperDiv = -1;
        TimeUSperDiv = -1;
        TimeMSperDiv = -1;
    }
    
    private void calculateRepresentationString()
    {
        double maxFrequency ;
        if(TimeNSperDiv < 1000)
        {
            maxFrequency  = (1000.0 / (wholeFrameTimeNS));
            representation = TimeNSperDiv + " ns" ;// (" + maxFrequency + " MHz/Screen)";
        }
        else if(TimeUSperDiv < 1000)
        {
            maxFrequency  = (1000.0 / (wholeFrameTimeUS));
            representation = TimeUSperDiv + " us" ;// (" + maxFrequency + " kHz/Screen)";
        }
        else if(TimeMSperDiv < 1000.0)
        { 
            maxFrequency  = (1000.0 / (wholeFrameTimeMS));
            representation = TimeMSperDiv + " ms" ;// (" + maxFrequency + " Hz/Screen)";
        }
        else
        { 
            maxFrequency  = (1000.0 / (wholeFrameTimeMS));
            representation = TimeMSperDiv*1000 + " s" ;// (" + maxFrequency + " Hz/Screen)";
        }   
    }
}