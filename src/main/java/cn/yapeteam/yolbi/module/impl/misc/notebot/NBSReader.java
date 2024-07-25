package cn.yapeteam.yolbi.module.impl.misc.notebot;

import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.util.ArrayList;

public class NBSReader {
    @NotNull
    public static Music read(File file) {
        InputStream stream;
        try {
            stream = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        Music song = new Music();
        song.setNotes(new ArrayList<>());
        try {
            DataInputStream dataInputStream = new DataInputStream(new BufferedInputStream(stream));
            HeaderData header = readHeader(dataInputStream);

            readShort(dataInputStream);//LayersCount

            /*Metadata*/
            readString(dataInputStream);
            readString(dataInputStream);
            readString(dataInputStream);
            readString(dataInputStream);
            readShort(dataInputStream);
            dataInputStream.readBoolean();
            dataInputStream.readByte();
            dataInputStream.readByte();
            readInt(dataInputStream);
            readInt(dataInputStream);
            readInt(dataInputStream);
            readInt(dataInputStream);
            readInt(dataInputStream);
            readString(dataInputStream);
            if (header.Version >= 4) {
                dataInputStream.readBoolean();
                dataInputStream.readByte();
                readShort(dataInputStream);
            }

            readNotes(song, header, dataInputStream);
            song.setName(readString(dataInputStream));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return song;
    }

    private static short readShort(@NotNull DataInputStream dataInputStream) throws IOException {
        int byte1 = dataInputStream.readUnsignedByte();
        int byte2 = dataInputStream.readUnsignedByte();
        return (short) (byte1 + (byte2 << 8));
    }

    private static int readInt(@NotNull DataInputStream dataInputStream) throws IOException {
        int byte1 = dataInputStream.readUnsignedByte();
        int byte2 = dataInputStream.readUnsignedByte();
        int byte3 = dataInputStream.readUnsignedByte();
        int byte4 = dataInputStream.readUnsignedByte();
        return (byte1 + (byte2 << 8) + (byte3 << 16) + (byte4 << 24));
    }

    @NotNull
    private static String readString(@NotNull DataInputStream dataInputStream) throws IOException {
        int length = readInt(dataInputStream);
        StringBuilder builder = new StringBuilder(length);
        for (; length > 0; --length) {
            char c = (char) dataInputStream.readByte();
            if (c == (char) 0x0D) {
                c = ' ';
            }
            builder.append(c);
        }
        return builder.toString();
    }

    @NotNull
    private static HeaderData readHeader(@NotNull DataInputStream stream) throws IOException {
        HeaderData data = new HeaderData();

        short length = readShort(stream);
        if (length == 0) { // New nbs format
            data.Version = stream.readByte();
            data.FirstCustomInstrumentIndex = stream.readByte();
        }

        return data;
    }

    private static void readNotes(@NotNull Music music, @NotNull HeaderData header, @NotNull DataInputStream stream) throws IOException {
        short tick = -1;
        while (true) {
            short jumpTicks = readShort(stream); // jumps till next tick

            if (jumpTicks == 0) {
                break;
            }
            tick += jumpTicks;

            while (true) {
                short jumpLayers = readShort(stream); // jumps till next layer
                if (jumpLayers == 0) {
                    break;
                }

                byte instrument = stream.readByte();

                byte key = stream.readByte();
                byte volume;
                if (header.Version >= 4) {
                    volume = stream.readByte();
                    stream.readUnsignedByte(); // 0 is 2 blocks right in nbs format, we want -100 to be left and 100 to be right
                    readShort(stream);
                } else {
                    volume = 100;
                }

                System.out.println("Note:" + Math.round(key / 3.52) + " Time:" + tick * 100 + " Inst:" + getInst(instrument));
                if (volume < 20) return;
                music.getNotes().add(new MusicNote((int) Math.round(key / 3.52), tick * 100, getInst(instrument)));
            }
        }
    }

    private static int getInst(int id) {
        switch (id) {
            case 0://PIANO | HARP
                return 0;
            case 1://BASS
                return 4;
            case 2://BASS_DRUM
                return 1;
            case 3://SNARE_DRUM
                return 2;
            case 4://CLICK
                return 3;
            case 5://GUITAR X
                return 4;
            case 6://FLUTE X
                return 3;
            case 7://BELL X
                return 0;
            case 8://CHIME X
                return 0;
            case 9://XYLOPHONE X
                return 4;
            case 10://IRON_XYLOPHONE X
                return 0;
            case 11://COW_BELL X
                return 3;
            case 12://DIDGERIDOO X
                return 4;
            case 13://BIT X
                return 3;
            case 14://BANJO X
                return 4;
            case 15://PLING X
                return 2;
            default:
                return 0;
        }
    }

    private static class HeaderData {
        public int Version = 0;
        public int FirstCustomInstrumentIndex = 10; //Backward compatibility - most of the songs with old structure are from 1.12
    }
}
