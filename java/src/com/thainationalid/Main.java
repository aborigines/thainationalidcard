/*
 * source https://doc.ubuntu-fr.org/smartcards
*/

package com.thainationalid;

import java.util.List;
import javax.smartcardio.Card;
import javax.smartcardio.CardChannel;
import javax.smartcardio.CardException;
import javax.smartcardio.CardTerminal;
import javax.smartcardio.CommandAPDU;
import javax.smartcardio.ResponseAPDU;
import javax.smartcardio.TerminalFactory;

public class Main {
    
    public static String toString(byte[] bytes) {
        StringBuilder sbTmp = new StringBuilder();
        for(byte b : bytes){
                sbTmp.append(String.format("%X", b));
        }
        return sbTmp.toString();
    }
    
    public static void main(String[] args) {
        try {
                // Show the list of available terminals
                // On Windows see HKEY_LOCAL_MACHINE\SOFTWARE\Microsoft\Cryptography\Calais\Readers
                TerminalFactory factory = TerminalFactory.getDefault();
                List terminals = factory.terminals().list();
                System.out.println("Terminals count: " + terminals.size());
                System.out.println("Terminals: " + terminals);

                // Get the first terminal in the list
                CardTerminal terminal = (CardTerminal) terminals.get(0);

                // Establish a connection with the card using
                // "T=0", "T=1", "T=CL" or "*"
                Card card = terminal.connect("*");
                System.out.println("Card: " + card);

                // Get ATR
                byte[] baATR = card.getATR().getBytes();
                System.out.println("ATR: " + Main.toString(baATR) );

                // Select Card Manager
                // - Establish channel to exchange APDU
                // - Send SELECT Command APDU
                // - Show Response APDU
                CardChannel channel = card.getBasicChannel();

                //SELECT Command
                // See GlobalPlatform Card Specification (e.g. 2.2, section 11.9)
                // CLA: 00
                // INS: A4
                // P1: 04 i.e. b3 is set to 1, means select by name
                // P2: 00 i.e. first or only occurence
                // Lc: 08 i.e. length of AID see below
                // Data: A0 00 00 00 03 00 00 00
                // AID of the card manager,
                // in the future should change to A0 00 00 01 51 00 00

                byte[] baCommandAPDU = {
                    (byte) 0x00, (byte) 0xA4, (byte) 0x04, 
                    (byte) 0x00, (byte) 0x08, (byte) 0xA0, 
                    (byte) 0x00, (byte) 0x00, (byte) 0x00, 
                    (byte) 0x54, (byte) 0x48, (byte) 0x00, 
                    (byte) 0x01
                };
                System.out.println("APDU <<<: " + Main.toString(baCommandAPDU));

                ResponseAPDU r = channel.transmit(new CommandAPDU(baCommandAPDU));
                System.out.println("APDU >>>: " + Main.toString(r.getBytes()));

                byte[] command_idcard = {
                    (byte) 0x80, (byte) 0xb0, (byte) 0x00,
                    (byte) 0x04, (byte) 0x02, (byte) 0x00,
                    (byte) 0x0d
                };
                System.out.println("APDU <<<:: " + Main.toString(command_idcard));

                ResponseAPDU response_command_idcard = channel.transmit(new CommandAPDU(command_idcard));
                System.out.println("APDU >>>: " + Main.toString(response_command_idcard.getBytes()));

                byte response[] = response_command_idcard.getData();
                for (int i=0; i < response.length; i++)
                System.out.print((char)response[i]);
                System.out.println();

                // Disconnect
                // true: reset the card after disconnecting card.
                card.disconnect(true);

        } 
        catch(CardException ex)  {
        }
    }
    
}
