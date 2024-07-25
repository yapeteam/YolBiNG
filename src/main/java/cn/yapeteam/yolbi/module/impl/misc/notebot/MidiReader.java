package cn.yapeteam.yolbi.module.impl.misc.notebot;

import javax.sound.midi.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Victor on 7/24/2015.
 * Ported by humboldt123 on 6/27/2020
 */

public class MidiReader {
    static final int NOTE_ON = 0x90;
    static int SET_TEMPO = 0x51;
    static int TIME_SIGNATURE = 0x58;

    static Sequence seq;
    static int[] channelprograms;

    public static Music read(File file) throws InvalidMidiDataException, IOException {
        seq = MidiSystem.getSequence(file);
        channelprograms = new int[30];
        ArrayList<MusicNote> notes = new ArrayList<>();

        long maxStamp = 0;
        int timeSignum;
        int timeSigden;
        int mspqn = 500000;

        int res = seq.getResolution();

        int trackN = 0;
        for (Track track : seq.getTracks()) {
            for (int eventIdx = 0; eventIdx < track.size(); eventIdx++) {
                MidiEvent event = track.get(eventIdx);

                if (IsTimeSignature(event)) {
                    MetaMessage mMsg = (MetaMessage) event.getMessage();

                    byte[] timeSig = mMsg.getMessage();

                    timeSignum = timeSig[2];
                    timeSigden = timeSig[3];

                    System.out.println("TIME_SIGNATURE Track: " + trackN + " msg: num: " + timeSignum + " den: " + timeSigden + " Tick: " + event.getTick());
                } else if (IsTempoChange(event)) {
                    MetaMessage mMsg = (MetaMessage) event.getMessage();

                    //500000
                    mspqn = new BigInteger(mMsg.getData()).intValue();

                    System.out.println("SET_TEMPO Track: " + trackN + " msg: " + mspqn + " Tick: " + event.getTick());
                } else if (IsNoteOn(event)) {
                    ShortMessage smsg = (ShortMessage) event.getMessage();

                    int note = smsg.getData1();
                    long tick = event.getTick();
                    int channel = smsg.getChannel();

                    long time = (long) (((tick * (mspqn / res)) / 1000.0) + 0.5);

                    note = note % 24;

                    if (time >= maxStamp)
                        maxStamp = time;

                    System.out.println("NOTE_ON Track: " + trackN + " Note: " + note + " Instrument: " + getInst(channelprograms[channel]) + " Channel: " + channel + " Tick: " + tick + " Time: " + time);
                    notes.add(new MusicNote(note, (int) time, getInst(channelprograms[channel])));
                } else if (IsPatchChange(event)) {
                    ShortMessage sMsg = (ShortMessage) event.getMessage();
                    long tick = event.getTick();
                    int channel = sMsg.getChannel();
                    int program = sMsg.getData1();

                    channelprograms[channel] = program + 1;

                    System.out.println("PATCH_CHANGE Track: " + trackN + " Program: " + program + " Channel: " + channel + " Tick: " + tick);
                }
            }
            trackN++;
        }
        return new Music(file.getName().substring(0, file.getName().length() - 4), notes);
    }

    @SuppressWarnings("DuplicateBranchesInSwitch")
    private static int getInst(int id) {
        switch (id) {
            case 0:
                return 0;
            case 1:
                return 0;
            case 2:
                return 0;
            case 3:
                return 0;
            case 4:
                return 0;
            case 5:
                return 0;
            case 6:
                return 0;
            case 7:
                return 0;
            case 8:
                return 0;
            case 9:
                return 2;
            case 10:
                return 3;
            case 11:
                return 4;
            case 12:
                return 2;
            case 13:
                return 0;
            case 14:
                return 4;
            case 15:
                return 0;
            case 16:
                return 0;
            case 17:
                return 0;
            case 18:
                return 0;
            case 19:
                return 0;
            case 20:
                return 0;
            case 21:
                return 0;
            case 22:
                return 2;
            case 23:
                return 0;
            case 24:
                return 2;
            case 25:
                return 2;
            case 26:
                return 2;
            case 27:
                return 2;
            case 28:
                return 2;
            case 29:
                return 2;
            case 30:
                return 2;
            case 31:
                return 2;
            case 32:
                return 4;
            case 33:
                return 4;
            case 34:
                return 4;
            case 35:
                return 4;
            case 36:
                return 4;
            case 37:
                return 4;
            case 38:
                return 4;
            case 39:
                return 4;
            case 40:
                return 0;
            case 41:
                return 0;
            case 42:
                return 0;
            case 43:
                return 0;
            case 44:
                return 0;
            case 45:
                return 0;
            case 46:
                return 0;
            case 47:
                return 0;
            case 48:
                return 0;
            case 49:
                return 0;
            case 50:
                return 0;
            case 51:
                return 0;
            case 52:
                return 0;
            case 53:
                return 0;
            case 54:
                return 0;
            case 55:
                return 0;
            case 56:
                return 2;
            case 57:
                return 2;
            case 58:
                return 2;
            case 59:
                return 2;
            case 60:
                return 2;
            case 61:
                return 2;
            case 62:
                return 2;
            case 63:
                return 2;
            case 64:
                return 4;
            case 65:
                return 4;
            case 66:
                return 4;
            case 67:
                return 4;
            case 68:
                return 4;
            case 69:
                return 4;
            case 70:
                return 4;
            case 71:
                return 4;
            case 72:
                return 3;
            case 73:
                return 3;
            case 74:
                return 3;
            case 75:
                return 3;
            case 76:
                return 3;
            case 77:
                return 3;
            case 78:
                return 3;
            case 79:
                return 3;
            case 80:
                return 0;
            case 81:
                return 0;
            case 82:
                return 0;
            case 83:
                return 0;
            case 84:
                return 0;
            case 85:
                return 0;
            case 86:
                return 0;
            case 87:
                return 4;
            case 88:
                return 0;
            case 89:
                return 0;
            case 90:
                return 0;
            case 91:
                return 0;
            case 92:
                return 0;
            case 93:
                return 0;
            case 94:
                return 0;
            case 95:
                return 0;
            case 96:
                return 0;
            case 97:
                return 0;
            case 98:
                return 0;
            case 99:
                return 0;
            case 100:
                return 0;
            case 101:
                return 0;
            case 102:
                return 0;
            case 103:
                return 0;
            case 104:
                return 0;
            case 105:
                return 0;
            case 106:
                return 0;
            case 107:
                return 0;
            case 108:
                return 0;
            case 109:
                return 0;
            case 110:
                return 0;
            case 111:
                return 0;
            case 112:
                return 3;
            case 113:
                return 0;
            case 114:
                return 2;
            case 115:
                return 3;
            case 116:
                return 2;
            case 117:
                return 2;
            case 118:
                return 2;
            case 119:
                return 3;
            case 120:
                return 2;
            case 121:
                return 0;
            case 122:
                return 3;
            case 123:
                return 0;
            case 124:
                return 2;
            case 125:
                return 1;
            case 126:
                return 2;
            case 127:
                return 2;
            default:
                return 0;
        }
    }

    // Reads a little endian short
    private static short readShort(InputStream input) throws IOException {
        return (short) (input.read() & 0xFF | input.read() << 8);
    }

    // Reads a little endian int
    private static int readInt(InputStream input) throws IOException {
        return input.read() | input.read() << 8 | input.read() << 16 | input.read() << 24;
    }

    private static String readString(InputStream input) throws IOException {
        return new String(readNBytes(input, readInt(input)));
    }

    private static final int MAX_BUFFER_SIZE = Integer.MAX_VALUE - 8;
    private static final int DEFAULT_BUFFER_SIZE = 8192;

    /*Code from InputStream.class (JDK11)*/
    public static byte[] readNBytes(InputStream inputStream, int len) throws IOException {
        if (len < 0) {
            throw new IllegalArgumentException("len < 0");
        }

        List<byte[]> bufs = null;
        byte[] result = null;
        int total = 0;
        int remaining = len;
        int n;
        do {
            byte[] buf = new byte[Math.min(remaining, DEFAULT_BUFFER_SIZE)];
            int nread = 0;

            // read to EOF which may read more or less than buffer size
            while ((n = inputStream.read(buf, nread,
                    Math.min(buf.length - nread, remaining))) > 0) {
                nread += n;
                remaining -= n;
            }

            if (nread > 0) {
                if (MAX_BUFFER_SIZE - total < nread) {
                    throw new OutOfMemoryError("Required array size too large");
                }
                total += nread;
                if (result == null) {
                    result = buf;
                } else {
                    if (bufs == null) {
                        bufs = new ArrayList<>();
                        bufs.add(result);
                    }
                    bufs.add(buf);
                }
            }
            // if the last call to read returned -1 or the number of bytes
            // requested have been read then break
        } while (n >= 0 && remaining > 0);

        if (bufs == null) {
            if (result == null) {
                return new byte[0];
            }
            return result.length == total ?
                    result : Arrays.copyOf(result, total);
        }

        result = new byte[total];
        int offset = 0;
        remaining = total;
        for (byte[] b : bufs) {
            int count = Math.min(b.length, remaining);
            System.arraycopy(b, 0, result, offset, count);
            offset += count;
            remaining -= count;
        }

        return result;
    }

    private static boolean IsPatchChange(MidiEvent event) {
        if (!(event.getMessage() instanceof ShortMessage))
            return false;

        ShortMessage smsg = (ShortMessage) event.getMessage();

        return smsg.getStatus() >= 0xC0 && smsg.getStatus() <= 0xCF;
    }

    private static boolean IsTempoChange(MidiEvent event) {
        if (!(event.getMessage() instanceof MetaMessage))
            return false;

        MetaMessage mmsg = (MetaMessage) event.getMessage();

        return mmsg.getType() == SET_TEMPO;
    }

    private static boolean IsTimeSignature(MidiEvent event) {
        if (!(event.getMessage() instanceof MetaMessage))
            return false;

        MetaMessage mmsg = (MetaMessage) event.getMessage();

        return mmsg.getType() == TIME_SIGNATURE;
    }

    private static boolean IsNoteOn(MidiEvent event) {
        if (!(event.getMessage() instanceof ShortMessage))
            return false;

        ShortMessage smsg = (ShortMessage) event.getMessage();

        return smsg.getCommand() == NOTE_ON;
    }
}