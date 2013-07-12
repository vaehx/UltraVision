/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.prosicraft.ultravision.chat;

import com.prosicraft.ultravision.packet.Packet1Login;
import com.prosicraft.ultravision.packet.Packet2Handshake;
import com.prosicraft.ultravision.packet.Packet3Chat;
import com.prosicraft.ultravision.util.MLog;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.StringStack;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Stack;

/**
 *
 * @author prosicraft
 */
public class UVClientHandler extends Thread {

    private UVClientHandler[] stock = null;
    private Socket socket = null;
    private UVServer server = null;
    private Stack sendStack = null;
    private String sendmsg = "";

    private BufferedReader in = null;
    private PrintWriter out = null;

    //private String username = "";

    public UVClientHandler (Socket s, UVClientHandler[] stock, UVServer server) {
        this.stock = stock;
        this.socket = s;
        this.server = server;
        this.sendStack = new StringStack ();
    }

    public void sendMessage (String txt) {

        final String theTxt = txt;

        (new Thread () {

            @Override
            public void run() {
                MLog.d("Inside sendMessage()");

                Packet3Chat pc = new Packet3Chat(theTxt);
                pc.send(out);

                MLog.d("Sent message: '" + theTxt + "'");
            }

        }).start();

    }

    @Override
    public void run() {
        try {
            in = new BufferedReader ( new InputStreamReader ( socket.getInputStream() ) );
            out = new PrintWriter ( socket.getOutputStream(), true );
        } catch (IOException ex) {
            MLog.e("Can't create Client Streams.");
            ex.printStackTrace();
        }

        int packID = -1;

        try {
            packID = in.read();
            MLog.d("Read packet id: " + packID);
        } catch (IOException ioex) {
            MLog.e(" Can't read next package: " + ioex.getMessage());
            ioex.printStackTrace();
        }

        if ( packID != 1 ) {  // ... for login
            MLog.e("Bad packet id. Disconnect.");
            try {
                socket.close();
            } catch (IOException ex) {
                MLog.e("Can't close connection to client (" + socket.getInetAddress().getHostAddress() + ")");
            }
            return;
        }

        Packet1Login lp = new Packet1Login ();
        if ( lp.eval(packID, in) ) {
            MLog.d("Received correct protocol.");

            Packet2Handshake ph = new Packet2Handshake(true);
            ph.send(out);

            server.raiseOnLoginEvent(lp.getUsername());

            Packet3Chat pc = new Packet3Chat();

            // now go into the loop
            while (true) {

                try {

                    // get new packet id
                    packID = in.read();

                    if ( pc.eval(packID, in) ) {
                        server.raiseOnMessageEvent(pc.getMessage());
                    } else {
                        MLog.e("Got wrong packet id: " + packID);
                        break;
                    }

                    // send
                    if ( sendmsg.equals("") )
                        continue;

                    MLog.d("(ClientHandler) sendStack not empty!");

                    Packet3Chat pcb = new Packet3Chat (sendmsg);
                    pcb.send(out);

                } catch (IOException ioex) {
                    ioex.printStackTrace();
                    break;
                }

            }

            server.raiseOnLeaveEvent(lp.getUsername());

        } else {
           MLog.e("Bad protocol. Disconnect.");
        }

        try {
            socket.close();
            MLog.i("Client disconnected: " + socket.getInetAddress().getHostAddress());
        } catch (IOException ex) {
            MLog.e("Can't close connection to client (" + socket.getInetAddress().getHostAddress() + ")");
        }

        for ( int i=0; i < stock.length; i++ )
            if ( stock[i] == this ) stock[i] = null;

        return;
    }

}
