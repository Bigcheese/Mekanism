package mekanism.client.gui;

import mekanism.common.EntityRobit;
import mekanism.common.Mekanism;
import mekanism.common.PacketHandler;
import mekanism.common.PacketHandler.Transmission;
import mekanism.common.inventory.container.ContainerRobitSmelting;
import mekanism.common.network.PacketRobit;
import mekanism.common.network.PacketRobit.RobitPacketType;
import mekanism.common.util.MekanismUtils;
import mekanism.common.util.MekanismUtils.ResourceType;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;

import org.lwjgl.opengl.GL11;

public class GuiRobitSmelting extends GuiContainer
{
	public EntityRobit robit;
	
	public GuiRobitSmelting(InventoryPlayer inventory, EntityRobit entity)
	{
		super(new ContainerRobitSmelting(inventory, entity));
		xSize += 25;
		robit = entity;
	}
	
    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
    {
    	fontRenderer.drawString("Robit Smelting", 8, 6, 0x404040);
        fontRenderer.drawString("Inventory", 8, ySize - 96 + 3, 0x404040);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float par1, int mouseX, int mouseY)
    {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        mc.renderEngine.func_110577_a(MekanismUtils.getResource(ResourceType.GUI, "GuiRobitSmelting.png"));
        int guiWidth = (width - xSize) / 2;
        int guiHeight = (height - ySize) / 2;
        drawTexturedModalRect(guiWidth, guiHeight, 0, 0, xSize, ySize);
        
		int xAxis = (mouseX - (width - xSize) / 2);
		int yAxis = (mouseY - (height - ySize) / 2);
        
		if(xAxis >= 179 && xAxis <= 197 && yAxis >= 10 && yAxis <= 28)
		{
			drawTexturedModalRect(guiWidth + 179, guiHeight + 10, 176 + 25, 0, 18, 18);
		}
		else {
			drawTexturedModalRect(guiWidth + 179, guiHeight + 10, 176 + 25, 18, 18, 18);
		}
		
		if(xAxis >= 179 && xAxis <= 197 && yAxis >= 30 && yAxis <= 48)
		{
			drawTexturedModalRect(guiWidth + 179, guiHeight + 30, 176 + 25, 36, 18, 18);
		}
		else {
			drawTexturedModalRect(guiWidth + 179, guiHeight + 30, 176 + 25, 54, 18, 18);
		}
		
		if(xAxis >= 179 && xAxis <= 197 && yAxis >= 50 && yAxis <= 68)
		{
			drawTexturedModalRect(guiWidth + 179, guiHeight + 50, 176 + 25, 72, 18, 18);
		}
		else {
			drawTexturedModalRect(guiWidth + 179, guiHeight + 50, 176 + 25, 90, 18, 18);
		}
		
		if(xAxis >= 179 && xAxis <= 197 && yAxis >= 70 && yAxis <= 88)
		{
			drawTexturedModalRect(guiWidth + 179, guiHeight + 70, 176 + 25, 108, 18, 18);
		}
		else {
			drawTexturedModalRect(guiWidth + 179, guiHeight + 70, 176 + 25, 126, 18, 18);
		}
		
		if(xAxis >= 179 && xAxis <= 197 && yAxis >= 90 && yAxis <= 108)
		{
			drawTexturedModalRect(guiWidth + 179, guiHeight + 90, 176 + 25, 144, 18, 18);
		}
		else {
			drawTexturedModalRect(guiWidth + 179, guiHeight + 90, 176 + 25, 162, 18, 18);
		}
		
        int displayInt;

        if(robit.furnaceBurnTime > 0)
        {
            displayInt = getBurnTimeRemainingScaled(12);
            drawTexturedModalRect(guiWidth + 56, guiHeight + 36 + 12 - displayInt, 176 + 25 + 18, 36 + 12 - displayInt, 14, displayInt + 2);
        }

        displayInt = getCookProgressScaled(24);
        drawTexturedModalRect(guiWidth + 79, guiHeight + 34, 176 + 25 + 18, 36 + 14, displayInt + 1, 16);
    }
    
	@Override
	protected void mouseClicked(int mouseX, int mouseY, int button)
	{
		super.mouseClicked(mouseX, mouseY, button);
		
		if(button == 0)
		{
			int xAxis = (mouseX - (width - xSize) / 2);
			int yAxis = (mouseY - (height - ySize) / 2);
			
			if(xAxis >= 179 && xAxis <= 197 && yAxis >= 10 && yAxis <= 28)
			{
				mc.sndManager.playSoundFX("random.click", 1.0F, 1.0F);
				PacketHandler.sendPacket(Transmission.SERVER, new PacketRobit().setParams(RobitPacketType.GUI, 0, robit.entityId));
				mc.thePlayer.openGui(Mekanism.instance, 21, mc.theWorld, robit.entityId, 0, 0);
			}
			else if(xAxis >= 179 && xAxis <= 197 && yAxis >= 30 && yAxis <= 48)
			{
				mc.sndManager.playSoundFX("random.click", 1.0F, 1.0F);
				PacketHandler.sendPacket(Transmission.SERVER, new PacketRobit().setParams(RobitPacketType.GUI, 1, robit.entityId));
				mc.thePlayer.openGui(Mekanism.instance, 22, mc.theWorld, robit.entityId, 0, 0);
			}
			else if(xAxis >= 179 && xAxis <= 197 && yAxis >= 50 && yAxis <= 68)
			{
				mc.sndManager.playSoundFX("random.click", 1.0F, 1.0F);
				PacketHandler.sendPacket(Transmission.SERVER, new PacketRobit().setParams(RobitPacketType.GUI, 2, robit.entityId));
				mc.thePlayer.openGui(Mekanism.instance, 23, mc.theWorld, robit.entityId, 0, 0);
			}
			else if(xAxis >= 179 && xAxis <= 197 && yAxis >= 70 && yAxis <= 88)
			{
				mc.sndManager.playSoundFX("random.click", 1.0F, 1.0F);
			}
			else if(xAxis >= 179 && xAxis <= 197 && yAxis >= 90 && yAxis <= 108)
			{
				mc.sndManager.playSoundFX("random.click", 1.0F, 1.0F);
				PacketHandler.sendPacket(Transmission.SERVER, new PacketRobit().setParams(RobitPacketType.GUI, 4, robit.entityId));
				mc.thePlayer.openGui(Mekanism.instance, 25, mc.theWorld, robit.entityId, 0, 0);
			}
		}
	}
	
    private int getCookProgressScaled(int i)
    {
        return robit.furnaceCookTime * i / 200;
    }

    private int getBurnTimeRemainingScaled(int i)
    {
        if(robit.currentItemBurnTime == 0)
        {
            robit.currentItemBurnTime = 200;
        }

        return robit.furnaceBurnTime * i / robit.currentItemBurnTime;
    }
}
