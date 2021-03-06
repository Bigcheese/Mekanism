package mekanism.common.tileentity;

import java.util.ArrayList;

import mekanism.api.IStorageTank;
import mekanism.api.Object3D;
import mekanism.api.gas.EnumGas;
import mekanism.api.gas.GasTransmission;
import mekanism.api.gas.IGasAcceptor;
import mekanism.api.gas.IGasStorage;
import mekanism.api.gas.ITubeConnection;
import mekanism.common.IRedstoneControl;
import mekanism.common.IRedstoneControl.RedstoneControl;
import mekanism.common.util.MekanismUtils;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeDirection;

import com.google.common.io.ByteArrayDataInput;

public class TileEntityGasTank extends TileEntityContainerBlock implements IGasStorage, IGasAcceptor, ITubeConnection, IRedstoneControl
{
	/** The type of gas stored in this tank. */
	public EnumGas gasType;
	
	/** The maximum amount of gas this tank can hold. */
	public int MAX_GAS = 96000;
	
	/** How much gas this tank is currently storing. */
	public int gasStored;
	
	/** How fast this tank can output gas. */
	public int output = 16;
	
	/** This machine's current RedstoneControl type. */
	public RedstoneControl controlType;
	
	public TileEntityGasTank()
	{
		super("Gas Tank");
		gasType = EnumGas.NONE;
		inventory = new ItemStack[2];
		controlType = RedstoneControl.DISABLED;
	}
	
	@Override
	public void onUpdate()
	{
		if(inventory[0] != null && gasStored > 0)
		{
			if(inventory[0].getItem() instanceof IStorageTank)
			{
				if(((IStorageTank)inventory[0].getItem()).getGasType(inventory[0]) == gasType || ((IStorageTank)inventory[0].getItem()).getGasType(inventory[0]) == EnumGas.NONE)
				{
					IStorageTank item = (IStorageTank)inventory[0].getItem();
					
					if(gasType == EnumGas.NONE)
					{
						gasType = item.getGasType(inventory[0]);
					}
					
					if(item.canReceiveGas(inventory[0], gasType))
					{
						int sendingGas = 0;
						
						if(item.getRate() <= gasStored)
						{
							sendingGas = item.getRate();
						}
						else if(item.getRate() > gasStored)
						{
							sendingGas = gasStored;
						}
						
						int rejects = item.addGas(inventory[0], gasType, sendingGas);
						setGas(gasType, gasStored - (sendingGas - rejects));
					}
				}
			}
		}
		
		if(inventory[1] != null && gasStored < MAX_GAS)
		{
			if(inventory[1].getItem() instanceof IStorageTank)
			{
				if(((IStorageTank)inventory[1].getItem()).getGasType(inventory[1]) == gasType || gasType == EnumGas.NONE)
				{
					IStorageTank item = (IStorageTank)inventory[1].getItem();
					
					if(gasType == EnumGas.NONE)
					{
						gasType = item.getGasType(inventory[1]);
					}
					
					if(item.canProvideGas(inventory[1], gasType))
					{
						int received = 0;
						int gasNeeded = MAX_GAS - gasStored;
						
						if(item.getRate() <= gasNeeded)
						{
							received = item.removeGas(inventory[1], gasType, item.getRate());
						}
						else if(item.getRate() > gasNeeded)
						{
							received = item.removeGas(inventory[1], gasType, gasNeeded);
						}
						
						setGas(gasType, gasStored + received);
					}
				}
			}
		}
		
		if(gasStored == 0)
		{
			gasType = EnumGas.NONE;
		}
		
		if(!worldObj.isRemote && gasStored > 0 && MekanismUtils.canFunction(this))
		{
			setGas(gasType, gasStored - (Math.min(gasStored, output) - GasTransmission.emitGasToNetwork(gasType, Math.min(gasStored, output), this, ForgeDirection.getOrientation(facing))));
			
			TileEntity tileEntity = Object3D.get(this).getFromSide(ForgeDirection.getOrientation(facing)).getTileEntity(worldObj);
			
			if(tileEntity instanceof IGasAcceptor)
			{
				if(((IGasAcceptor)tileEntity).canReceiveGas(ForgeDirection.getOrientation(facing).getOpposite(), gasType))
				{
					int sendingGas = 0;
					
					if(getGas(gasType) >= output)
					{
						sendingGas = output;
					}
					else if(getGas(gasType) < output)
					{
						sendingGas = getGas(gasType);
					}
					
					int rejects = ((IGasAcceptor)tileEntity).transferGasToAcceptor(sendingGas, gasType);
					
					setGas(gasType, getGas(gasType) - (sendingGas - rejects));
				}
			}
		}
	}
	
	@Override
	public boolean canExtractItem(int slotID, ItemStack itemstack, int side)
	{
		if(slotID == 1)
		{
			return (itemstack.getItem() instanceof IStorageTank && ((IStorageTank)itemstack.getItem()).getGas(EnumGas.NONE, itemstack) == 0);
		}
		else if(slotID == 0)
		{
			return (itemstack.getItem() instanceof IStorageTank && 
					((IStorageTank)itemstack.getItem()).getGas(EnumGas.NONE, itemstack) == ((IStorageTank)itemstack.getItem()).getMaxGas(EnumGas.NONE, itemstack));
		}
		
		return false;
	}
	
	@Override
	public boolean isItemValidForSlot(int slotID, ItemStack itemstack)
	{
		if(slotID == 0)
		{
			return itemstack.getItem() instanceof IStorageTank && (gasType == EnumGas.NONE || ((IStorageTank)itemstack.getItem()).canReceiveGas(itemstack, gasType));
		}
		else if(slotID == 1)
		{
			return itemstack.getItem() instanceof IStorageTank && (gasType == EnumGas.NONE || ((IStorageTank)itemstack.getItem()).canProvideGas(itemstack, gasType));
		}
		
		return true;
	}
	
	@Override
	public int[] getAccessibleSlotsFromSide(int side)
	{
		return side == 1 ? new int[] {0} : new int[] {1};
	}

	@Override
	public int getGas(EnumGas type, Object... data) 
	{
		if(type == gasType)
		{
			return gasStored;
		}
		
		return 0;
	}

	@Override
	public void setGas(EnumGas type, int amount, Object... data) 
	{
		if(type == gasType)
		{
			gasStored = Math.max(Math.min(amount, MAX_GAS), 0);
		}
	}
	
	@Override
	public int getMaxGas(EnumGas type, Object... data)
	{
		return MAX_GAS;
	}

	@Override
	public int transferGasToAcceptor(int amount, EnumGas type) 
	{
		if(type == gasType || gasType == EnumGas.NONE)
		{
			if(gasType == EnumGas.NONE)
			{
				gasType = type;
			}
			
	    	int rejects = 0;
	    	int neededGas = MAX_GAS-gasStored;
	    	
	    	if(amount <= neededGas)
	    	{
	    		gasStored += amount;
	    	}
	    	else if(amount > neededGas)
	    	{
	    		gasStored += neededGas;
	    		rejects = amount-neededGas;
	    	}
	    	
	    	return rejects;
		}
		
		return amount;
	}

	@Override
	public boolean canReceiveGas(ForgeDirection side, EnumGas type) 
	{
		return (type == gasType || gasType == EnumGas.NONE) && side != ForgeDirection.getOrientation(facing);
	}
	
	@Override
	public void handlePacketData(ByteArrayDataInput dataStream)
	{
		super.handlePacketData(dataStream);
		
		gasStored = dataStream.readInt();
		gasType = EnumGas.getFromName(dataStream.readUTF());
		controlType = RedstoneControl.values()[dataStream.readInt()];
		
		MekanismUtils.updateBlock(worldObj, xCoord, yCoord, zCoord);
	}
	
	@Override
    public void readFromNBT(NBTTagCompound nbtTags)
    {
        super.readFromNBT(nbtTags);

        gasStored = nbtTags.getInteger("gasStored");
        gasType = EnumGas.getFromName(nbtTags.getString("gasType"));
        controlType = RedstoneControl.values()[nbtTags.getInteger("controlType")];
    }

	@Override
    public void writeToNBT(NBTTagCompound nbtTags)
    {
        super.writeToNBT(nbtTags);
        
        nbtTags.setInteger("gasStored", gasStored);
        nbtTags.setString("gasType", gasType.name);
        nbtTags.setInteger("controlType", controlType.ordinal());
    }
	
	@Override
	public ArrayList getNetworkedData(ArrayList data)
	{
		super.getNetworkedData(data);
		
		data.add(gasStored);
		data.add(gasType.name);
		data.add(controlType.ordinal());
		
		return data;
	}

	@Override
	public boolean canTubeConnect(ForgeDirection side) 
	{
		return true;
	}
	
	@Override
	public RedstoneControl getControlType() 
	{
		return controlType;
	}

	@Override
	public void setControlType(RedstoneControl type) 
	{
		controlType = type;
	}
}
