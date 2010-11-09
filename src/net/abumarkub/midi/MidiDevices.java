package net.abumarkub.midi;

import java.util.Vector;

import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Synthesizer;

public class MidiDevices
{
    private int _defInport = -1;
    private int _defOutport = -1;
    protected Vector<MidiDevice.Info> allDevices;
    protected Vector<MidiDevice.Info> synths;
    protected Vector<MidiDevice.Info> sequencers;
    protected Vector<MidiDevice.Info> midiIns;
    protected Vector<MidiDevice.Info> midiOuts;
    protected Vector<MidiDevice.Info> otherPorts;

    private StringBuffer _warnings  = new StringBuffer();
    private MidiDevice _keyboard    = null;
    private MidiDevice _synthesizer = null;

    public MidiDevices()
    {
    }

    public String getDevicesXML()
    {
        allDevices = new Vector<MidiDevice.Info>();
        synths = new Vector<MidiDevice.Info>();
        sequencers = new Vector<MidiDevice.Info>();
        midiIns = new Vector<MidiDevice.Info>();
        midiOuts = new Vector<MidiDevice.Info>();
        otherPorts = new Vector<MidiDevice.Info>();

        int id                  = 0;
        String type             = "";
        MidiDevice device       = null;
        MidiDevice.Info info    = null;
        StringBuffer xml        = new StringBuffer();
        _warnings               = new StringBuffer();
        Boolean available       = true;
        xml.append("<config>");

        MidiDevice.Info[] infos = MidiSystem.getMidiDeviceInfo();

        for(int i = 0; i < infos.length; i++)
        {
            available = true;
            
            try
            {
                device = MidiSystem.getMidiDevice(infos[i]);
//		System.out.println(device.getDeviceInfo().getName() + ":" + device.getDeviceInfo().getVendor() + ":" + device.getDeviceInfo().getDescription()+ ":" + device.getMaxReceivers());
//		System.out.println("IN:" + device.getMaxReceivers() + " OUT:" + device.getMaxTransmitters());
            }
            catch(MidiUnavailableException e)
            {
                return("<error id='getDevicesXML'><![CDATA[" + e + "]]></error>");
            }

            info = device.getDeviceInfo();
            int in = device.getMaxReceivers();
            int out = device.getMaxTransmitters();

            allDevices.add(info);

            if(in == -1 && out == -1)
            {
                if(device instanceof Synthesizer)
                {
                    type = "synth";
                    available = checkDevice(device,type);
                    if(available) synths.add(info);
                }
                else
                {
                    type = "other";
                    available = checkDevice(device,type);
                    if(available) otherPorts.add(info);
                }
            }
            else if(in >= 0)
            {
                if(_defInport == -1)
                {
                    _defInport = id;
                }
                type = "input";
                available = checkDevice(device,type);
                if(available) midiIns.add(info);
            }
            else if(out >= 0)
            {
                if(_defOutport == -1)
                {
                    _defOutport = id;
                }
                type = "output";
                available = checkDevice(device,type);
                if(available) midiOuts.add(info);
            }

            xml.append("<device id='");
            xml.append(id++);
            xml.append("' type='" + type);
            xml.append("' available='" + available + "'>");
            xml.append("<name><![CDATA[");
            xml.append(info.getName() + " | " + info.getDescription());
            xml.append("]]></name>");
            xml.append("</device>");
        }
        xml.append("</config>");
        return xml.toString() + _warnings.toString();
    }

    public MidiDevice getDevice(int port)
    {
        MidiDevice tmp = null;

        try
        {
            tmp = MidiSystem.getMidiDevice((MidiDevice.Info) allDevices.elementAt(port));
        }
        catch(NullPointerException e1)
        {
            System.out.print("<error id='getDevice'><![CDATA[" + e1 + "]]></error>");
        }
        catch(MidiUnavailableException e2)
        {
            System.out.print("<error id='getDevice'><![CDATA[" + e2 + "]]></error>");
        }
        return tmp;
    }

    private Boolean checkDevice(MidiDevice device, String type)
    {
        /**
         * _synthesizer and _keyboard are the currently connected devices, so you don't hava to check these: they are available
         */
        if(device == _synthesizer || device == _keyboard)
        {
            return true;
        }

        if(device.isOpen())
        {
            _warnings.append("<warning id='" + type + "'>device is already in use!</warning>");
            return false;
        }

        try
        {
            device.open();
        }
        catch(MidiUnavailableException e)
        {
            _warnings.append("<warning id='" + type + "'><![CDATA[" + e + "]]></warning>");
            return false;
        }

        device.close();

        return true;
    }

    public int getNumberOfDevices()
    {
        return allDevices.size();
    }

    public int getDefInport()
    {
        return _defInport;
    }

    public int getDefOutport()
    {
        return _defOutport;
    }

    public void setSynthesizer(MidiDevice device)
    {
        _synthesizer = device;
    }

    public void setKeyboard(MidiDevice device)
    {
        _keyboard = device;
    }

    public void exit()
    {
        
    }
}
