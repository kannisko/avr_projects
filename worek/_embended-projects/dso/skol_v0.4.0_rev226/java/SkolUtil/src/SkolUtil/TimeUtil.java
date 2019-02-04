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

package SkolUtil;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 *
 * @author sush
 */
public class TimeUtil 
{

  public static final String DATE_FORMAT_NOW = "yyyy-MM-dd HH:mm:ss:SSS";
//     System.out.println(DateUtils.now("dd MMMMM yyyy"));
//     System.out.println(DateUtils.now("yyyyMMdd"));
//     System.out.println(DateUtils.now("dd.MM.yy"));
//     System.out.println(DateUtils.now("MM/dd/yy"));
//     System.out.println(DateUtils.now("yyyy.MM.dd G 'at' hh:mm:ss z"));
//     System.out.println(DateUtils.now("EEE, MMM d, ''yy"));
//     System.out.println(DateUtils.now("h:mm a"));
//     System.out.println(DateUtils.now("H:mm:ss:SSS"));
//     System.out.println(DateUtils.now("K:mm a,z"));
//     System.out.println(DateUtils.now("yyyy.MMMMM.dd GGG hh:mm aaa"));

  public static String timingStringNow() 
  {
    Calendar cal = Calendar.getInstance();
    SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_NOW);
    return sdf.format(cal.getTime());

  }
  

}