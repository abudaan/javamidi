package net.abumarkub.midi;

public class MidiData
{
    private int status, channel, command, data1, data2;
    public static final int NOTE_OFF                = 0x80;//128
    public static final int NOTE_ON                 = 0x90;//144
    public static final int POLY_PRESSURE           = 0xA0;//160
    public static final int CONTROL_CHANGE          = 0xB0;//176
    public static final int PROGRAM_CHANGE          = 0xC0;//192
    public static final int CHANNEL_PRESSURE        = 0xD0;//208
    public static final int PITCH_BEND              = 0xE0;//224
    public static final int SYSTEM_EXCLUSIVE        = 0xF0;//240
    private static int[] _commands =
    {
        NOTE_OFF, NOTE_ON, POLY_PRESSURE, CONTROL_CHANGE, PROGRAM_CHANGE, CHANNEL_PRESSURE, PITCH_BEND, SYSTEM_EXCLUSIVE
    };
    public static String NOTE_OFF_VERBOSE           = "NOTE OFF";
    public static String NOTE_ON_VERBOSE            = "NOTE ON";
    public static String POLY_PRESSURE_VERBOSE      = "POLY PRESSURE";
    public static String CONTROL_CHANGE_VERBOSE     = "CONTROL CHANGE";
    public static String PROGRAM_CHANGE_VERBOSE     = "PROGRAM CHANGE";
    public static String CHANNEL_PRESSURE_VERBOSE   = "CHANNEL PRESSURE";
    public static String PITCH_BEND_VERBOSE         = "PITCH BEND";
    public static String SYSTEM_EXCLUSIVE_VERBOSE   = "SYSTEM EXCLUSIVE";
    private static String[] _commandsVerbose =
    {
        NOTE_OFF_VERBOSE, NOTE_ON_VERBOSE, POLY_PRESSURE_VERBOSE, CONTROL_CHANGE_VERBOSE, PROGRAM_CHANGE_VERBOSE, CHANNEL_PRESSURE_VERBOSE, PITCH_BEND_VERBOSE, SYSTEM_EXCLUSIVE_VERBOSE
    };

    public MidiData(byte[] data)
    {
        command     = (int) (data[0] & 0xFF);
        data1       = (int) (data[1]);
        data2       = (int) (data[2]);

        //for(int i = 0; i < _commands.length; i++)
        for(int i = _commands.length; --i >= 0;)
        {
            status  = _commands[i];
            channel = command % status;
            if(channel < 16)
            {
                //System.out.println(channel + " : " + _commandsVerbose[i]);
                break;
            }
        }
    }

    @Override
    public String toString()
    {
        StringBuffer msg = new StringBuffer();
        msg.append("<mididata channel='");
        msg.append(channel);
        msg.append(" ' command='");
        msg.append(command);
        msg.append(" ' status='");
        msg.append(status);
        msg.append(" ' data1='");
        msg.append(data1);
        msg.append(" ' data2='");
        msg.append(data2);
        msg.append("' />");

        return msg.toString();
    }
}
