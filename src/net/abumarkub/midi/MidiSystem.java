/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.abumarkub.midi;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Transmitter;

/**
 *
 * @author abudaan
 */
public class MidiSystem
{
    private MidiDevices _midiDevices;
    private MidiReceiver _midiReceiver;
    private Receiver _systemReceiver;
    private MidiDevice _keyboard;
    private MidiDevice _synthesizer;

    public MidiSystem(IMidiDataConsumer consumer)
    {
        _midiDevices        = new MidiDevices();
        _midiReceiver       = new MidiReceiver();
        _midiReceiver.addListener(consumer);

        _keyboard           = null;
        _synthesizer        = null;
    }

    public String setMidiIn(int id)
    {
        if(_keyboard != null)
        {
            _keyboard.close();
            _keyboard = null;
            _midiDevices.setKeyboard(_keyboard);
        }

        if(id == -1)
        {
            return "<midi-in>-1</midi-in>";
        }

        _keyboard = _midiDevices.getDevice(id);//hw input

        try
        {
            _keyboard.open();
        }
        catch(MidiUnavailableException e)
        {
            return "<error id='setInPort'>" + e + "</error><midi-in>-1</midi-in>";
        }

        Transmitter trans1 = null;
        Transmitter trans2 = null;

        try
        {
            trans1 = _keyboard.getTransmitter();
            trans1.setReceiver(_midiReceiver);

            if(_synthesizer != null && _synthesizer.isOpen())
            {
                trans2 = _keyboard.getTransmitter();
                trans2.setReceiver(_synthesizer.getReceiver());
            }

            //System.out.println("keyboard connected " + trans1.getClass());
        }
        catch(MidiUnavailableException e)
        {
            return "<error id='setInPort'>" + e + "</error><midi-in>-1</midi-in>";
        }
        
        _midiDevices.setKeyboard(_keyboard);
        return "<midi-in>" + id + "</midi-in>";
    }

    public String setMidiOut(int id)
    {
        if(_synthesizer != null)
        {
            _synthesizer.close();
            _synthesizer = null;
            _midiDevices.setSynthesizer(_synthesizer);
        }

        if(id > _midiDevices.getNumberOfDevices())
        {
            return "<midi-out>" + id + "</midi-out>";
        }

        if(id == -1)
        {
            return "<midi-out>" + id + "</midi-out>";
        }

        try
        {
            _synthesizer = _midiDevices.getDevice(id);//hw output
        }
        catch(ArrayIndexOutOfBoundsException e1)
        {
            return "<error id='setOutPort'>" + e1 + "</error><midi-out>-1</midi-out>";
        }

        try
        {
            _synthesizer.open();
        }
        catch(MidiUnavailableException e2)
        {
            return "<error id='setOutPort'>" + e2 + "</error><midi-out>-1</midi-out>";
        }

        if(_keyboard == null || !_keyboard.isOpen())
        {
            _midiDevices.setSynthesizer(_synthesizer);
            return "<message id='setOutPort'>no keyboard</message><midi-out>" + id + "</midi-out>";
        }

        Transmitter trans2 = null;

        try
        {
            trans2 = _keyboard.getTransmitter();
            trans2.setReceiver(_synthesizer.getReceiver());
        }
        catch(MidiUnavailableException e)
        {
            return "<error id='setOutPort'>" + e + "</error><midi-out>-1</midi-out>";
        }
        
        _midiDevices.setSynthesizer(_synthesizer);
        return "<midi-out>" + id + "</midi-out>";
    }

    public String setMidiInOut(int inId, int outId)
    {
        StringBuffer msg = new StringBuffer();
        msg.append(setMidiIn(inId));
        msg.append(setMidiOut(outId));
        return msg.toString();
    }

    public String processMidiEvent(String event)
    {
        String[] mididata = event.split(",");

        int command = Integer.parseInt(mididata[0]);
        int channel = Integer.parseInt(mididata[1]);
        int data1 = Integer.parseInt(mididata[2]);//note number
        int data2 = Integer.parseInt(mididata[3]);//velocity

        //generate Java MidiMessage
        ShortMessage midiMsg = new ShortMessage();
        try
        {
            midiMsg.setMessage(command, channel, data1, data2);
        }
        catch(InvalidMidiDataException e)
        {
            System.out.print(e);
        }
        //and send it to a receiver:
        long timeStamp = -1;

        if(_synthesizer != null)
        {
            try
            {
                _systemReceiver = _synthesizer.getReceiver();
            }
            catch(MidiUnavailableException e)
            {
                return "<error id='processMidiEvent'>" + e + "</error>";
            }
        }
        else
        {
            return "<warning id='processMidiEvent'>no midi out set</warning>";
        }

        _systemReceiver.send(midiMsg, timeStamp);
        return "";
    }

    public String getDevicesXML()
    {
        return _midiDevices.getDevicesXML();
    }

    public void exit()
    {
        setMidiInOut(-1, -1);
        _midiReceiver.close();
    }
}


/*
*/
