package net.abumarkub.midi.service;

import java.util.Timer;
import java.util.TimerTask;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import net.abumarkub.midi.IMidiDataConsumer;
import net.abumarkub.midi.MidiSystem;

public class MidiService extends TimerTask implements IMidiDataConsumer
{

    private final int TIME_OUT = 10000;
    private String _input;
    private Timer _kill;

    public MidiService()
    {
        _input = new String();
        _kill = new Timer();
        //_kill.schedule(this, TIME_OUT);
    }

    public void run()
    {
        System.out.print("<msg>time out, closing midiservice</msg>");
        System.exit(0);
    }

    public static void main(String[] args)
    {
        MidiService service     = new MidiService();
        MidiSystem midiSystem   = new MidiSystem(service);
        System.out.print("<midi-connection-started/>");

        try
        {
            BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

            while(true)//(!_input.equalsIgnoreCase("quit"))
            {
                service.setInput(in.readLine());

                if(service.getInput().equalsIgnoreCase("get-devices"))
                {
                    System.out.print(midiSystem.getDevicesXML());
                }
                else if(service.getInput().equalsIgnoreCase("stop"))
                {
                    System.exit(0);
                }
                else if(service.getInput().equalsIgnoreCase("midi-in-out"))
                {
                    String[] ids = service.getInput().substring(12).split(",");
                    System.out.print(midiSystem.setMidiInOut(Integer.parseInt(ids[0]), Integer.parseInt(ids[1])));
                }
                else if(service.getInput().startsWith("midi-in"))
                {
                    int inport = Integer.parseInt(service.getInput().substring(8));
                    System.out.print(midiSystem.setMidiIn(inport));
                }
                else if(service.getInput().startsWith("midi-out"))
                {
                    int outport = Integer.parseInt(service.getInput().substring(9));
                    System.out.print(midiSystem.setMidiOut(outport));
                }
                else if(service.getInput().startsWith("as3-midi-event"))
                {
                    String result = midiSystem.processMidiEvent(service.getInput().substring(15));
                    if(!result.equals(""))
                    {
                        System.out.print(result);
                    }
                }
                else if(service.getInput().startsWith("keep-alive"))
                {
                    service.resetTimer();
                }
            }
        }
        catch(IOException e)
        {
            System.out.print("<error id='main'>" + e + "</error>");
        }
    }

    public void resetTimer()
    {
        try
        {
            _kill.cancel();
        }
        catch(IllegalStateException e)
        {
            System.out.print("<warning id='resetTimer'>" + e + "</warning>");
        }

        try
        {
            _kill.schedule(this, TIME_OUT);
        }
        catch(IllegalStateException e)
        {
            System.out.print("<warning id='resetTimer'>" + e + "</warning>");
        }
    }

    public void setInput(String s)
    {
        _input = s;
    }

    public String getInput()
    {
        return _input;
    }

    /**
     * called by MidiConfiguration
     */
    public void consumeMidiData(String data)
    {
        System.out.print(data);
        System.out.flush();
    }
}
