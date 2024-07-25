package cn.yapeteam.yolbi.module.impl.misc.notebot;

public class MusicNote {
    int note;
    int time;
    int inst;

    public MusicNote(int note, int time, int inst) {
        this.note = note;
        this.time = time;
        this.inst = inst;
    }

    public int getNote() {
        return note;
    }

    public int getTime() {
        return time;
    }

    public int getInst() {
        return inst;
    }
}
