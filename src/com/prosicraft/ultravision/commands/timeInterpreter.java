/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.prosicraft.ultravision.commands;

/**
 *
 * @author passi
 */
public class timeInterpreter {

    public static boolean isIntNumber(String num){
        try{
            Integer.parseInt(num);
        } catch(NumberFormatException nfe) {
            return false;
        }
        return true;
    }

    public static String getText (long time) {

        String res = "";

        long c = time;

        int d = (int)Math.floor ( c / (1000 * 60 * 60 * 24) );
        int h = (int)Math.floor ( (c - (d * 1000 * 60 * 60 * 24) ) / (1000 * 60 * 60) );
        int m = (int)Math.floor ( (c - (h * 1000 * 60 * 60) - (d * 1000 * 60 * 60 * 24) ) / (1000 * 60) );
        int s = (int)Math.floor ( (c - (d * 1000 * 60 * 60 * 24) - (h * 1000 * 60 * 60) - (m * 1000 * 60)) / (1000) );

        res += (d > 0) ? String.valueOf(d) + "d " : "";
        res += ((h) > 0) ? String.valueOf(h) + "h " : "";
        res += ((m) > 0) ? String.valueOf(m) + "min " : "";
        res += ((s) > 0) ? String.valueOf(s) + "sec" : "";

        return res.trim();

    }

    public static long getTime (String time) {

        if ( time == null || time.trim().equalsIgnoreCase("") )
            return -1;

        long c = 0;
        String val = "";
        String typ = "";
        //c.set(Calendar.MINUTE, 10);

        for ( int n=0; n < time.length(); n++ ) {

            if ( timeInterpreter.isIntNumber(String.valueOf(time.charAt(n))) ) {

                val += String.valueOf(time.charAt(n));

            } else {

                typ += String.valueOf(time.charAt(n));
                if ( typ.equalsIgnoreCase("min") || typ.equalsIgnoreCase("m") ) {
                    c += Integer.parseInt(val) * 1000 * 60; val = ""; typ = "";
                } else if ( typ.equalsIgnoreCase("d") ) {
                    c += Integer.parseInt(val) * 1000 * 60 * 60 * 24; val = ""; typ = "";
                } else if ( typ.equalsIgnoreCase("sec") || typ.equalsIgnoreCase("s") ) {
                    c += Integer.parseInt(val) * 1000; val = ""; typ = "";
                } else if ( typ.equalsIgnoreCase("h") ) {
                    c += Integer.parseInt(val) * 1000 * 60 * 60;
                }

            }

        }

        return c;

    }

}
