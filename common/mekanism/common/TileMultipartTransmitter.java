package mekanism.common;

import codechicken.multipart.TileMultipart;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mekanism.api.ITransmitter;
import mekanism.api.TransmitterNetworkRegistry;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;

public abstract class TileMultipartTransmitter<N> extends TileMultipart implements ITransmitter<N>
{
	public N theNetwork;
	
	@Override
	@SideOnly(Side.CLIENT)
	public AxisAlignedBB getRenderBoundingBox()
	{
		return INFINITE_EXTENT_AABB;
	}
	
	@Override
	public void onChunkUnload() 
	{
		invalidate();
		TransmitterNetworkRegistry.getInstance().pruneEmptyNetworks();
	}
	
	@Override
	public void setNetwork(N network)
	{
		if(network != theNetwork)
		{
			removeFromNetwork();
			theNetwork = network;
		}
	}
	
	@Override
	public boolean areNetworksEqual(TileEntity tileEntity)
	{
		return tileEntity instanceof ITransmitter && getNetwork().getClass() == ((ITransmitter)tileEntity).getNetwork().getClass();
	}
	
	@Override
	public N getNetwork()
	{
		return getNetwork(true);
	}
}
