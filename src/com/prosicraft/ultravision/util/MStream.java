/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.prosicraft.ultravision.util;

import java.io.DataInputStream;
import java.io.IOException;

/**
 *
 * @author passi
 */
public class MStream {

    public static String readString ( DataInputStream in, int length ) throws IOException {
        byte[] buf = new byte[length];
        in.read(buf);
        return new String(buf);
    }

    public static byte[] readStringBuf ( DataInputStream in, int length ) throws IOException {
        byte[] buf = new byte[length];
        in.read(buf);
        return buf;
    }

    public static boolean readBool ( DataInputStream in ) throws IOException {
        return ((in.read() == 0) ? false : true);
    }

    public static boolean isZero ( byte[] buf ) {
        for ( int i=0; i < buf.length; i++ ) {
            if ( buf[i] != 0 ) return false;
        }
        return true;
    }

}
