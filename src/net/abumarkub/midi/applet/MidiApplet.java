package net.abumarkub.midi.applet;

import net.abumarkub.midi.MidiSystem;
import net.abumarkub.midi.IMidiDataConsumer;
import java.applet.Applet;
import java.applet.AppletContext;
import java.net.MalformedURLException;
import java.net.URL;

public class MidiApplet extends Applet implements IMidiDataConsumer
{
    private AppletContext _context;
    private MidiSystem _midiSystem;
    private static final long serialVersionUID = 1L;

    public static void main(String args[])
    {
        MidiApplet midiCaptureApplet = new MidiApplet();
        midiCaptureApplet.init();
    }

    @Override
    public synchronized void init()
    {
        _midiSystem = new MidiSystem(this);
    }

    @Override
    public void start()
    {
        _context = getAppletContext();
        System.out.println("START " + _context.toString() + ":" + System.getProperty("java.version") + ":" + System.getProperty("java.vendor"));
        talkToJavaScript("midi-connection-started", "<midi-connection-started/>");
    }

    @Override
    public void stop()
    {
        _midiSystem.exit();
        _context = null;
        System.gc();
        System.out.println("STOP");
    }

    @Override
    public void destroy()
    {
        _midiSystem.exit();
        _context = null;
        System.gc();
        System.runFinalization();
        System.out.println("DESTROY");
    }

    /*
     * this method gets called from JavasScript
     */
    public void executeJavaMethod(String command, String params)
    {
        System.out.println("<- " + command + "(" + params + ")");
        int id = -1;
        
        if(command.equals("get-devices"))
        {
            talkToJavaScript("get-devices", _midiSystem.getDevicesXML());
        }
        else if(command.equals("midi-in"))
        {
            id = Integer.parseInt(params);
            talkToJavaScript("midi-in",_midiSystem.setMidiIn(id));
        }
        else if(command.equals("midi-out"))
        {
            id = Integer.parseInt(params);
            talkToJavaScript("midi-out", _midiSystem.setMidiOut(id));
        }
        else if(command.equals("midi-in-out"))
        {
            String[] ids = params.split(",");
            talkToJavaScript("midi-in-out", _midiSystem.setMidiInOut(Integer.parseInt(ids[0]),Integer.parseInt(ids[1])));
        }
        else if(command.equals("as3-midi-event"))
        {
            String result = _midiSystem.processMidiEvent(params);
            if(!result.equals(""))
            {
                talkToJavaScript("as3-midi-event", result);
            }
        }
    }

    /**
     * called by MidiConfiguration
     */
    public void consumeMidiData(String data)
    {
        talkToJavaScript("midi-data", data);
    }

    private void talkToJavaScript(String command, String msg)
    {
        System.out.println("-> " + command + "(" + msg + ")");

        if(_context == null)
        {
            _context = getAppletContext();
        }
        try
        {
            _context.showDocument(new URL("javascript:talkToFlash(\"" + command + "\",\"" + msg + "\")"));
        }
        catch(MalformedURLException me)
        {
            System.out.println(me);
        }
        catch(Exception e)
        {
            System.out.println(e);
        }
    }
}
