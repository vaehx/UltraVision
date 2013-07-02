/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.prosicraft.ultravision.local;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 *
 * @author prosicraft
 */
public class UVPlayerInfoChunk {
        public String label = "nochnk";
        
        public UVPlayerInfoChunk () {                
        }       
        
        public UVPlayerInfoChunk (String lbl) {
                if ( lbl.length() > 6 ) {
                        label = lbl.substring(0, 5);
                }
                else if ( lbl.length() < 6 ) {
                        label = lbl;
                        while ( lbl.length() < 6 ) {
                                label += "A";
                        }                        
                }        
                else
                        label = lbl;
        }
        
        public void setLabel (String lbl) {
                if ( lbl.length() > 6 ) {
                        label = lbl.substring(0, 5);
                }
                else if ( lbl.length() < 6 ) {
                        label = lbl;
                        while ( lbl.length() < 6 ) {
                                label += "A";
                        }                        
                }        
                else
                        label = lbl;
        }
        
        public void write (DataOutputStream out) throws IOException {
                // Overwrite this
        }
        
        /*
         * returns: false if chunk label not recognized
         */
        public boolean read (DataInputStream in, String lbl) throws IOException {
                return false;// Overwrite this
        }
        
}
