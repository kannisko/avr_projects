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

package SushUtil;

import java.awt.Color;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import javax.swing.JTextArea;

/**
 *
 * @author sushi
 */
public class logger 
{
    public static JTextArea jtextArea = null;
    public static JTextArea jtextArea2 = null;
    public static javax.swing.JTextPane jtextPane= null;
    private static String newline = System.getProperty("line.separator");


    
    public static void logDebug(String msg)
    {
        log("Debug:" + msg, "<font color=gray>" + loggerTime() + " Debug:" + msg +  "</font><br>");
//        if(jtextArea != null)
//            jtextArea.setBackground(Color.WHITE);
    }    
    
    public static void logInfo(String msg)
    {
        log("Info :"+ msg, " <font color=green> " + loggerTime() + " Info:" + msg +  " </font> <br> ");
        if(jtextArea != null)
            jtextArea.setBackground(Color.WHITE);
    }
    
    public static void logWarn(String msg)
    {
        log("Warn :" + msg, " <font color=fuchsia> " + loggerTime() + " Warn:" + msg +  " </font> <br> ");
        if(jtextArea != null)
            jtextArea.setBackground(Color.ORANGE);
    }
        
    public static void logError(String msg, Exception e)
    {
        
        log("Error:" + msg + e.getLocalizedMessage(), " <font color=red> " + loggerTime() + " Error:" + msg +  "</font><br>");
        e.printStackTrace();
        if(jtextArea != null)
            jtextArea.setBackground(Color.RED);
    }
    public static void logError(String msg)
    {
        
        log("Error:" + msg, " <font color=red> " + loggerTime() + " Error:" + msg +  " </font> <br> ");
        if(jtextArea != null)
            jtextArea.setBackground(Color.RED);
    }

    
    private static void log(String msg, String html)
    {
        if(jtextArea != null)
        {
            jtextArea.append(loggerTime() + " " +  msg + newline);
            jtextArea.setCaretPosition( jtextArea.getText().length() );
        }
        if(jtextArea2 != null)
        {
            jtextArea2.append(loggerTime() + " " +  msg + newline);
            jtextArea2.setCaretPosition( jtextArea2.getText().length() );
        }
        if(jtextPane != null && !msg.startsWith("Debug"))
        {
            
           
            //String tmp = jtextPane.getText();
            //System.out.println(tmp);
            //tmp = tmp.replace("</body>", html + " </body> ");
            
            //System.out.println(tmp);
//            jtextPane.setText(new String(tmp));
            //jtextPane.scrollToReference("</html>");

        }    
        System.out.println(msg);
    }
    
    private static String loggerTime() 
    {
        String DATE_FORMAT_NOW = "HH:mm:ss:SSS";
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_NOW);
        return sdf.format(cal.getTime());

    }
}
