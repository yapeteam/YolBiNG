package cn.yapeteam.yolbi.module.impl.misc;

import cn.yapeteam.yolbi.YolBi;
import cn.yapeteam.yolbi.event.Listener;
import cn.yapeteam.yolbi.event.impl.network.PacketSendEvent;
import cn.yapeteam.yolbi.event.impl.player.MotionEvent;
import cn.yapeteam.yolbi.module.Module;
import cn.yapeteam.yolbi.module.ModuleCategory;
import cn.yapeteam.yolbi.module.ModuleInfo;
import cn.yapeteam.yolbi.module.impl.visual.Notification;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.client.C02PacketUseEntity;

import javax.sound.sampled.*;
import java.io.BufferedInputStream;
import java.io.IOException;

@ModuleInfo(name = "KillEffect", category = ModuleCategory.MISC)
public class KillEffect extends Module {
    private static SourceDataLine play;

    private static void play(AudioInputStream audioInputStream) throws IOException, LineUnavailableException {
        AudioFormat audioFormat = audioInputStream.getFormat();
        // 转换文件编码
        if (audioFormat.getEncoding() != AudioFormat.Encoding.PCM_SIGNED) {
            System.out.println(audioFormat.getEncoding());
            audioFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, audioFormat.getSampleRate(), 16, audioFormat.getChannels(), audioFormat.getChannels() * 2, audioFormat.getSampleRate(), false);
            audioInputStream = AudioSystem.getAudioInputStream(audioFormat, audioInputStream);
        }

        DataLine.Info dataLineInfo = new DataLine.Info(SourceDataLine.class, audioFormat, AudioSystem.NOT_SPECIFIED);
        play = (SourceDataLine) AudioSystem.getLine(dataLineInfo);
        play.open(audioFormat);

        play.start();

        int count;
        byte[] tempBuff = new byte[(int) audioFormat.getSampleRate() * audioFormat.getFrameSize()];

        while ((count = audioInputStream.read(tempBuff, 0, tempBuff.length)) != -1) {
            play.write(tempBuff, 0, count);
        }
    }

    /**
     * 设置音量增益
     *
     * @param volume 音量百分比
     */
    public void volumeSet(int volume) {
        if (volume < 0) {
            volume = 0;
        }

        if (volume > 100) {
            volume = 100;
        }

        if (play == null) {
            return;
        }

        FloatControl gainControl = (FloatControl) play.getControl(FloatControl.Type.MASTER_GAIN);
        gainControl.setValue((float) (0.86 * volume - 80));
    }

    /**
     * 通过文件对象播放歌曲
     */
    private void play() {
        try {
            //noinspection DataFlowIssue
            play(AudioSystem.getAudioInputStream(new BufferedInputStream(KillEffect.class.getResourceAsStream("/assets/minecraft/yolbi/sound/kill.wav"))));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private Entity lastAttacked = null;


    @Override
    protected void onEnable() {
        lastAttacked = null;
        wait = false;
    }

    private volatile boolean wait = false;

    @Listener
    private void onUpdate(MotionEvent e) {
        for (Entity entity : Minecraft.getMinecraft().theWorld.loadedEntityList) {
            if (!wait && entity instanceof EntityPlayer && ((EntityPlayer) entity).getHealth() == 0 && lastAttacked != null && entity.getDisplayName().getUnformattedTextForChat().equals(lastAttacked.getDisplayName().getUnformattedTextForChat())) {
                wait = true;
                lastAttacked = null;
                volumeSet(100);
                new Thread(() -> {
                    play();
                    wait = false;
                }).start();
                YolBi.instance.getModuleManager().getModule(Notification.class).add(new cn.yapeteam.yolbi.ui.noti.Notification("Killed 1 Player"));
            }
        }
    }

    @Listener
    private void onAttack(PacketSendEvent e) {
        if (e.getPacket() instanceof C02PacketUseEntity) {
            C02PacketUseEntity packet = e.getPacket();
            if (packet.getAction() == C02PacketUseEntity.Action.ATTACK && !packet.getEntity().isDead) {
                lastAttacked = packet.getEntity();
            }
        }
    }
}
