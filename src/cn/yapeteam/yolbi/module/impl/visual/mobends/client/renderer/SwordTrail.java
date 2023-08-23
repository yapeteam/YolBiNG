package cn.yapeteam.yolbi.module.impl.visual.mobends.client.renderer;

import java.util.ArrayList;
import java.util.List;


import cn.yapeteam.yolbi.module.impl.visual.mobends.client.model.ModelRendererBends;
import cn.yapeteam.yolbi.module.impl.visual.mobends.client.model.entity.ModelBendsPlayer;
import cn.yapeteam.yolbi.module.impl.visual.mobends.util.GUtil;
import net.minecraft.client.Minecraft;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector3f;

public class SwordTrail {
	public int count=1;
	
	public List<TrailPart> trailPartList = new ArrayList<>();
	
	public SwordTrail(){
		
	}
	
	public void reset(){
		this.trailPartList.clear();
	}
	
	public class TrailPart{
		public ModelRendererBends body,
						 		  arm,
						 		  foreArm;
		
		public Vector3f renderRotation = new Vector3f();
		public Vector3f renderOffset = new Vector3f();
		public Vector3f itemRotation = new Vector3f();
		
		float ticksExisted;
		
		public TrailPart(ModelBendsPlayer argModel){
			body = new ModelRendererBends(argModel);
			arm = new ModelRendererBends(argModel);
			foreArm = new ModelRendererBends(argModel);
		}
	}
	
	public void render(ModelBendsPlayer model){
		//GlStateManager.color(HUDMod.getClientColors().getFirst().getRed(),HUDMod.getClientColors().getFirst().getGreen(),HUDMod.getClientColors().getFirst().getBlue());
		//Minecraft.getMinecraft().getTextureManager().bindTexture(MoBendsMod.texture_NULL);

		Minecraft.getMinecraft().getTextureManager().bindTexture(new ResourceLocation("mobends/textures/white.png"));

		//GL11.color
		//GlStateManager.color(HUDMod.getClientColors().getFirst().getRed(),HUDMod.getClientColors().getFirst().getGreen(),HUDMod.getClientColors().getFirst().getBlue());

		//GlStateManager.color(HUDMod.getClientColors().getFirst().getRGB(),HUDMod.getClientColors().getSecond().getRGB());

		//if ( YolBi.INSTANCE.getModuleCollection().getModule(MoBendsMod.class).useYellow.isEnabled()){
		//	Minecraft.getMinecraft().getTextureManager().bindTexture(MoBendsMod.texture_YELLOW);
		//}else {
		//	Minecraft.getMinecraft().getTextureManager().bindTexture(MoBendsMod.texture_NULL);
		//}

		
		GL11.glDepthFunc(GL11.GL_LEQUAL);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glDisable(GL11.GL_CULL_FACE);
		GL11.glDisable(GL11.GL_LIGHTING);
    	GL11.glHint(GL11.GL_PERSPECTIVE_CORRECTION_HINT, GL11.GL_NICEST);
    	GL11.glShadeModel(GL11.GL_SMOOTH);
		GL11.glPushMatrix();
		//Minecraft.getMinecraft().getTextureManager().bindTexture(new ResourceLocation("YolBi/hue2.png"));

		GL11.glBegin(GL11.GL_QUADS);
				for(int i = 0;i < this.trailPartList.size();i++){
					TrailPart part = this.trailPartList.get(i);
					
					float alpha = part.ticksExisted/5.0f;
					alpha = GUtil.max(alpha, 1.0f);
					alpha = 1.0f-alpha;
					
					GL11.glColor4f(1,1,1,alpha);
					
					Vector3f[] point = new Vector3f[]{
						new Vector3f(0,0,-8+8*alpha),
						new Vector3f(0,0,-8-8*alpha),
					};
					
					GUtil.rotateX(point, part.itemRotation.getX());
					GUtil.rotateY(point, part.itemRotation.getY());
					GUtil.rotateZ(point, part.itemRotation.getZ());
					
					GUtil.translate(point, new Vector3f(-1,-6,0));
					GUtil.rotateX(point, part.foreArm.rotateAngleX/(float)Math.PI*180.0f);
					GUtil.rotateY(point, part.foreArm.rotateAngleY/(float)Math.PI*180.0f);
					GUtil.rotateZ(point, part.foreArm.rotateAngleZ/(float)Math.PI*180.0f);
					
					GUtil.rotateX(point, part.foreArm.pre_rotation.getX());
					GUtil.rotateY(point, part.foreArm.pre_rotation.getY());
					GUtil.rotateZ(point, -part.foreArm.pre_rotation.getZ());
					
					GUtil.translate(point, new Vector3f(0,-6+2,0));
					GUtil.rotateX(point, part.arm.rotateAngleX/(float)Math.PI*180.0f);
					GUtil.rotateY(point, part.arm.rotateAngleY/(float)Math.PI*180.0f);
					GUtil.rotateZ(point, part.arm.rotateAngleZ/(float)Math.PI*180.0f);
					
					GUtil.rotateX(point, part.arm.pre_rotation.getX());
					GUtil.rotateY(point, part.arm.pre_rotation.getY());
					GUtil.rotateZ(point, -part.arm.pre_rotation.getZ());
					
					GUtil.translate(point, new Vector3f(-5,10,0));
					GUtil.rotateX(point, part.body.rotateAngleX/(float)Math.PI*180.0f);
					GUtil.rotateY(point, part.body.rotateAngleY/(float)Math.PI*180.0f);
					GUtil.rotateZ(point, part.body.rotateAngleZ/(float)Math.PI*180.0f);
					
					GUtil.rotateX(point, part.body.pre_rotation.getX());
					GUtil.rotateY(point, part.body.pre_rotation.getY());
					GUtil.rotateZ(point, part.body.pre_rotation.getZ());
					GUtil.translate(point, new Vector3f(0,12,0));
					
					GUtil.rotateX(point, part.renderRotation.getX());
					GUtil.rotateY(point, part.renderRotation.getY());
					GUtil.translate(point, part.renderOffset);
					
					if(i > 0){
						GL11.glVertex3f(point[1].x,point[1].y,point[1].z);
						GL11.glVertex3f(point[0].x,point[0].y,point[0].z);
						
						GL11.glVertex3f(point[0].x,point[0].y,point[0].z);
						GL11.glVertex3f(point[1].x,point[1].y,point[1].z);
					}else{
						GL11.glVertex3f(point[0].x,point[0].y,point[0].z);
						GL11.glVertex3f(point[1].x,point[1].y,point[1].z);
					}
					
					if(i == this.trailPartList.size()-1){
						GL11.glVertex3f(point[1].x,point[1].y,point[1].z);
						GL11.glVertex3f(point[0].x,point[0].y,point[0].z);
					}
				}
			GL11.glEnd();
		GL11.glPopMatrix();
		
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glDisable(GL11.GL_CULL_FACE);
		GL11.glEnable(GL11.GL_LIGHTING);
	}
	
	public void add(ModelBendsPlayer argModel){
			TrailPart newPart = new TrailPart(argModel);
			newPart.body.sync((ModelRendererBends)argModel.bipedBody);
				newPart.body.setPosition(argModel.bipedBody.rotationPointX,argModel.bipedBody.rotationPointY,argModel.bipedBody.rotationPointZ);
				newPart.body.setOffset(argModel.bipedBody.offsetX,argModel.bipedBody.offsetY,argModel.bipedBody.offsetZ);
			newPart.arm.sync((ModelRendererBends)argModel.bipedRightArm);
				newPart.arm.setPosition(argModel.bipedRightArm.rotationPointX,argModel.bipedRightArm.rotationPointY,argModel.bipedRightArm.rotationPointZ);
				newPart.arm.setOffset(argModel.bipedRightArm.offsetX,argModel.bipedRightArm.offsetY,argModel.bipedRightArm.offsetZ);
			newPart.foreArm.sync(argModel.bipedRightForeArm);
				newPart.foreArm.setPosition(argModel.bipedRightForeArm.rotationPointX,argModel.bipedRightForeArm.rotationPointY,argModel.bipedRightForeArm.rotationPointZ);
				newPart.foreArm.setOffset(argModel.bipedRightForeArm.offsetX,argModel.bipedRightForeArm.offsetY,argModel.bipedRightForeArm.offsetZ);
				
			newPart.renderOffset.set(argModel.renderOffset.vSmooth);
			newPart.renderRotation.set(argModel.renderRotation.vSmooth);
			newPart.itemRotation.set(argModel.renderItemRotation.vSmooth);
			this.trailPartList.add(newPart);
	}
	
	public void update(float argPartialTicks){
		for(int i = 0;i < this.trailPartList.size();i++){
			this.trailPartList.get(i).ticksExisted+=argPartialTicks;
		}
		
		for(int i = 0;i < this.trailPartList.size();i++){
			if(this.trailPartList.get(i).ticksExisted > 20){
				this.trailPartList.remove(this.trailPartList.get(i));
			}
		}
	}
}
