package cn.yapeteam.yolbi.module.impl.visual;

import cn.yapeteam.yolbi.Vestige;
import cn.yapeteam.yolbi.values.impl.BooleanValue;
import cn.yapeteam.yolbi.values.impl.ModeValue;
import cn.yapeteam.yolbi.values.impl.NumberValue;
import cn.yapeteam.yolbi.util.animation.Animation;
import cn.yapeteam.yolbi.util.animation.AnimationType;
import cn.yapeteam.yolbi.util.animation.AnimationUtil;
import cn.yapeteam.yolbi.util.misc.TimerUtil;
import cn.yapeteam.yolbi.util.render.DrawUtil;
import cn.yapeteam.yolbi.util.render.FontUtil;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.player.EntityPlayer;
import cn.yapeteam.yolbi.module.AlignType;
import cn.yapeteam.yolbi.module.ModuleCategory;
import cn.yapeteam.yolbi.module.HUDModule;
import cn.yapeteam.yolbi.module.impl.combat.Killaura;

public class TargetHUD extends HUDModule {

    private final ModeValue<String> mode = new ModeValue<>("Mode", "Normal", "Normal", "Outline");

    private final ModeValue<AnimationType> animationType = AnimationUtil.getAnimationType(AnimationType.POP2);
    private final NumberValue<Integer> animationDuration = AnimationUtil.getAnimationDuration(400);

    private final NumberValue<Integer> healthBarDelay = new NumberValue<>("Healh bar delay", 100, 0, 450, 25);
    private final BooleanValue roundedHealth = new BooleanValue("Rounded health", true);

    private final ModeValue<String> font = FontUtil.getFontSetting();

    private final Animation animation;

    private Killaura killauraModule;
    private ClientTheme theme;

    private EntityPlayer target;

    private final TimerUtil barTimer = new TimerUtil();

    private float renderedHealth;

    private boolean hadTarget;

    public TargetHUD() {
        super("TargetHUD", ModuleCategory.VISUAL, 0, 0, 140, 50, AlignType.LEFT);
        this.addValues(mode, font, animationType, animationDuration, healthBarDelay, roundedHealth);

        ScaledResolution sr = new ScaledResolution(mc);

        this.posX.setValue(sr.getScaledWidth() / 2 - width / 2);
        this.posY.setValue(sr.getScaledHeight() / 2 + 20);

        this.animation = new Animation();
        animation.setAnimDuration(animationDuration.getValue());
        animation.setAnimType(animationType.getValue());
    }

    @Override
    public void onClientStarted() {
        killauraModule = Vestige.instance.getModuleManager().getModule(Killaura.class);
        theme = Vestige.instance.getModuleManager().getModule(ClientTheme.class);
    }

    @Override
    protected void renderModule(boolean inChat) {
        if(inChat) {
            animation.getTimer().setTimeElapsed(animationDuration.getValue());
            renderTargetHUD(mc.thePlayer, true);

            target = null;
        } else if(this.isEnabled()) {
            boolean canRender = killauraModule.isEnabled() && killauraModule.getTarget() != null && killauraModule.getTarget() instanceof EntityPlayer;

            if(killauraModule.isEnabled() && killauraModule.getTarget() != null && killauraModule.getTarget() instanceof EntityPlayer) {
                target = (EntityPlayer) killauraModule.getTarget();
            }

            renderTargetHUD(target, canRender);
        } else {
            animation.getTimer().setTimeElapsed(0);
        }
    }

    private void renderTargetHUD(EntityPlayer entity, boolean canRender) {
        int x = posX.getValue().intValue();
        int y = posY.getValue().intValue();

        animation.updateState(canRender);

        animation.setAnimDuration(animationDuration.getValue());
        animation.setAnimType(animationType.getValue());

        if(entity == null) return;

        if(animation.isRendered() || !animation.isAnimDone()) {
            float health = roundedHealth.getValue() ? Math.round(entity.getHealth() * 10) / 10.0F : entity.getHealth();

            if(!hadTarget) {
                renderedHealth = health;
            }

            animation.render(() -> {
                double healthMult = renderedHealth / entity.getMaxHealth();

                if(health != renderedHealth) {
                    renderedHealth += (health - renderedHealth) * Math.min(1, barTimer.getTimeElapsed() / (double) (healthBarDelay.getValue()));
                } else {
                    barTimer.reset();
                }

                double endAnimX = x + 54 + 79 * healthMult;

                switch (mode.getValue()) {
                    case "Normal":
                        Gui.drawRect(x, y, x + width, y + height, 0x85000000);
                        Gui.drawRect(x + 52, y + 35, x + 54 + 79 * healthMult, y + 45, theme.getColor(0));

                        for(double i = x + 52; i < endAnimX; i++) {
                            Gui.drawRect(i, y + 35, i + 1, y + 45, theme.getColor((int) (200 + i * 5)));
                        }
                        break;
                    case "Outline":
                        int startColor = 0x50000000;

                        DrawUtil.drawGradientVerticalRect(x, y, x + width - 3, y + 3, 0, startColor);
                        DrawUtil.drawGradientVerticalRect(x, y + height - 3, x + width - 3, y + height, startColor, 0);

                        DrawUtil.drawGradientSideRect(x - 3, y + 3, x, y + height - 2, 0, startColor);
                        DrawUtil.drawGradientSideRect(x + width - 3, y + 3, x + width, y + height - 2, startColor, 0);

                        for(double i = x + 52; i < endAnimX; i++) {
                            Gui.drawRect(i, y + 35, i + 1, y + 45, theme.getColor((int) (200 + i * 5)));
                        }
                        break;
                }

                DrawUtil.drawHead(((AbstractClientPlayer) entity).getLocationSkin(), x + 5, y + 5, 40, 40);

                FontUtil.drawStringWithShadow(font.getValue(), entity.getGameProfile().getName(), x + 55, y + 9, -1);
                FontUtil.drawStringWithShadow(font.getValue(), health + " HP", x + 55, y + 20, -1);
            }, x, y, x + width, y + height);

            hadTarget = true;
        } else {
            hadTarget = false;
        }
    }

}
