package net.abumarkub.midi;

import javax.sound.midi.MidiMessage;
import javax.sound.midi.Receiver;
import javax.sound.midi.ShortMessage;

public class MidiReceiver implements Receiver
{
    private IMidiDataConsumer _listener;

    public MidiReceiver()
    {
    }

    public void addListener(IMidiDataConsumer listener)
    {
        _listener = listener;
    }

    public void close()
    {
        _listener = null;
    }

    public void send(MidiMessage message, long timeStamp)
    {
        StringBuffer msg = new StringBuffer();

        if(message instanceof ShortMessage)
        {
            ShortMessage sm = (ShortMessage) message;

            msg.append("<midi-data channel='");
            msg.append(sm.getChannel());
            msg.append(" ' command='");
            msg.append(sm.getCommand());
            msg.append(" ' status='");
            msg.append(sm.getStatus());
            msg.append(" ' data1='");
            msg.append(sm.getData1());
            msg.append(" ' data2='");
            msg.append(sm.getData2());
            msg.append("' />");

        }

        _listener.consumeMidiData(msg.toString());


        /*
         *  note 		= -112
         *  pitch bend 		= -32
         *  modulation  	= -80
         *  rotation knob	= -80
         *  fader 		= -80
         *  sustain pedal	= -80
         *  press button	= -64
         */

    }
}
