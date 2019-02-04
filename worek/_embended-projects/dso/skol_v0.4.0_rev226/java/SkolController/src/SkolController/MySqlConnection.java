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


import SushUtil.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;

import java.util.logging.Level;
import java.util.logging.Logger;


/**
 *
 * @author sushi
 */
public class MySqlConnection
{
    
    public Boolean keepRunning ;
    private static Connection mySqlConnection = null;
    private String tableName;
    
    public MySqlConnection(String dbHost, String dbPort,String dbName, String dbUser, String dbPass) 
    {
        if(mySqlConnection != null)
        {
            logger.logError("Error connecting to mySql Server - RESTART Programm !!!");
        }
        try 
        {
            Class.forName("com.mysql.jdbc.Driver");
        } 
        catch (ClassNotFoundException e) 
        {
           logger.logError("Could not load jdbc class", e);
                   
        }

        
        try 
        {
            mySqlConnection = DriverManager.getConnection("jdbc:mysql://" +dbHost + ":" + dbPort + "/" + dbName, dbUser, dbPass);
            logger.logInfo("Established Database connection to " + mySqlConnection.getMetaData().getURL());
            createTable();
        } 
        catch (SQLException e) 
        {
            logger.logError("Could not connect to database", e);
        }
    }
    
    public void createTable()
    {
        java.util.Date dt = new java.util.Date();

        SimpleDateFormat df = new SimpleDateFormat( "yyyyMMddHHmmss" );
        df.setTimeZone( TimeZone.getDefault() );                  // nicht mehr unbedingt notwendig seit JDK 1.2
        // Formatierung zu String:
        String myDate =  "sb" + df.format( dt ) ;        // z.B. '2001-01-26 19:03:56.731'
        tableName = myDate;

        String exec = "CREATE TABLE `" + tableName + "` (  `myIndex` int(11) unsigned NOT NULL auto_increment,  `rowCountPerDatastream` int(11) NOT NULL default '1',`data` varchar(65000) NOT NULL,  PRIMARY KEY  (`myIndex`)) ENGINE=MyISAM AUTO_INCREMENT=1 DEFAULT CHARSET=latin1";
        Statement statement = null;
        
        try 
        {
            statement = (Statement) mySqlConnection.createStatement();
            statement.execute(exec);
        } catch (SQLException ex) 
        {
           logger.logError("Could not create table : " + exec, ex);
        }
    }
    
    public  void insertToDb(int[] intArray)
    {
        if(mySqlConnection == null)
            return;
        
        
        String myString = "";
        String execString = "";
        for(int i =0; i < intArray.length ; i++)
        {
            myString += intArray[i];
            if(i != (intArray.length-1))
                myString += ";";    
        }
              
        if(myString.length() > 65000)
        {
            logger.logError("Too much data for Database Row > is a open issue / a rowCountPerDataStream Field is prepared in the DB, which says how much rows are used for one DataStream.");
            logger.logError("Insert to DB Dropped");
            return;
        }
        try 
        {
            Statement statement = null;
            statement = (Statement) mySqlConnection.createStatement();
            execString = "INSERT INTO " + tableName + " (rowCountPerDatastream,data) VALUES ( '1' ,'" + myString + "');";
            //logger.logDebug(execString);
            statement.execute(execString);
            
            
        } catch (SQLException ex) 
        {
            logger.logError("Could not put data to database : " + execString, ex);
        }
    }
    

   
    
}
