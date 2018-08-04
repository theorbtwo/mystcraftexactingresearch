package biz.mastros.james.mystcraftthingy;

import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

@Mod.EventBusSubscriber(modid = "mystcraftthingy")
public class BiomeResearcherTileEntity extends TileEntity {
	long last_click_time = 0;
	double progress = 0;
	
	/* Upon what changes of state should the TileEntity be deleted and created again. */
	@Override
	public boolean shouldRefresh(World newWorld, BlockPos newPos, IBlockState oldState, IBlockState newState) {
		return oldState.getBlock() != newState.getBlock();
	}

	
	/* Registration */
    @SubscribeEvent
    public static void registerEvent(Register<Item> event) {
    	Main.logger.info("got registerEvent in BiomeResearcherTileEntity");
    	
    	GameRegistry.registerTileEntity(BiomeResearcherTileEntity.class, new ResourceLocation("mystcraftthingy", "biomeResearcher"));
    }


	/* NBT round-tripping */
	@Override public NBTTagCompound writeToNBT(NBTTagCompound parent_tag) {
		super.writeToNBT(parent_tag);
		
		parent_tag.setLong("last_click_time", last_click_time);
		parent_tag.setDouble("progress", progress);
		
		return parent_tag;
	}
	
	@Override public void readFromNBT(NBTTagCompound parent_tag) {
		super.readFromNBT(parent_tag);
		
		last_click_time = parent_tag.getInteger("last_click_time");
		progress = parent_tag.getDouble("progress");
	}
	
}
