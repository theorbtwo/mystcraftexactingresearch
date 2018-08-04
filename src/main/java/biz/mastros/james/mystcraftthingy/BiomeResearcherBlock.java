package biz.mastros.james.mystcraftthingy;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import com.xcompwiz.mystcraft.api.hook.ItemFactory;
import com.xcompwiz.mystcraft.api.APIInstanceProvider;
import com.xcompwiz.mystcraft.api.MystObjects;
import com.xcompwiz.mystcraft.api.exception.APIUndefined;
import com.xcompwiz.mystcraft.api.exception.APIVersionRemoved;
import com.xcompwiz.mystcraft.api.exception.APIVersionUndefined;
import net.minecraft.util.text.TextComponentString;



@Mod.EventBusSubscriber(modid = "mystcraftthingy")
public class BiomeResearcherBlock extends BlockContainer {
	public static final IProperty<EnumFacing> FACING = PropertyDirection.create("facing");
	public static BiomeResearcherBlock the_block;
	public static ItemBlock the_item;
	
	/* General properties */
	
	public BiomeResearcherBlock() {
		super(Material.ROCK);
		
		this.setRegistryName("mystcraftthingy:biomeResearcher");
		
		setUnlocalizedName("biomeResearcher");
		setTickRandomly(false);
		useNeighborBrightness = false;

		setDefaultState(getBlockState().getBaseState().withProperty(FACING, EnumFacing.NORTH));
		
	}
	
	/* state management */
	
	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, FACING);
	}
	
	@Override
	public int getMetaFromState(final IBlockState state) {
		final int facingIndex = state.getValue(FACING).getIndex();
		
		return facingIndex;
	}

	@Override
	public IBlockState getStateFromMeta(final int meta) {
		final int facingIndex = meta & 7;
		final EnumFacing facing = EnumFacing.getFront(facingIndex);
		
		return getDefaultState().withProperty(FACING,  facing);
	}
	
	@Override
	public IBlockState getStateForPlacement(
			final World world,
			final BlockPos pos, final EnumFacing facing, 
			final float hitX, final float hitY, final float hitZ, 
			final int meta, final EntityLivingBase placer, final EnumHand hand) {
		Main.logger.info("in getStateForPlacement");
		
		final EnumFacing newFacing = placer.getHorizontalFacing().getOpposite();
		
		IBlockState superstate = super.getStateForPlacement(world, pos, newFacing, hitX, hitY, hitZ, meta, placer, hand);

		IBlockState mystate = superstate.withProperty(FACING, newFacing);
		
		return mystate;
	}
	
	
	/* Registration */
    @SubscribeEvent
    public static void registerBlock(Register<Block> event) {
    	Main.logger.info("got registerBlock in BiomeResearcherBlock");
    	the_block = new BiomeResearcherBlock();
    	
    	event.getRegistry().register(the_block);
    }

    @SubscribeEvent
    public static void registerItem(Register<Item> event) {
    	Main.logger.info("got registerItem in BiomeResearcherBlock");
    	the_item = new ItemBlock(the_block);
    	the_item.setRegistryName("mystcraftthingy:biomeResearcher");
    	
    	event.getRegistry().register(the_item);
    }
    
    /* TileEntity glue */
    @Override
    public boolean hasTileEntity(IBlockState state)
    {
      return true;
    }
    
	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new BiomeResearcherTileEntity();
	}
	
    @Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, 
    		EntityPlayer playerIn, EnumHand hand,
    		EnumFacing facing, float hitX, float hitY, float hitZ) {
        TileEntity tileentity_generic = worldIn.getTileEntity(pos);

        if (worldIn.isRemote) {
        	return false;
        }
        
        if (!(tileentity_generic instanceof BiomeResearcherTileEntity)) {
        	Main.logger.info("mystcraftthingy", "te at activated block isn't biome researcher, it is ", tileentity_generic);
        	return false;
        }

        BiomeResearcherTileEntity tileentity = (BiomeResearcherTileEntity) worldIn.getTileEntity(pos);
        
        long time_elapsed = worldIn.getWorldTime() - tileentity.last_click_time;

        Main.logger.info("time_elapsed: {}", time_elapsed);
        
        if (time_elapsed < ConfigValues.biomeresearcher.biomeResearchMinTime) {
        	String message = "I should observe and ponder a bit more";
        	
            playerIn.sendStatusMessage(new TextComponentString(message), false);

            return false;
        }

        double this_change = worldIn.rand.nextGaussian() * ConfigValues.biomeresearcher.biomeResearchStdDev + ConfigValues.biomeresearcher.biomeResearchAverage;
        
        
        Main.logger.info("New progress: {}", tileentity.progress);

        String message;
        
        if (this_change < 0) {
        	message = "No, this won't do at all";
        } else {
        	message = "Hmm, I think I see";
        }
        
        playerIn.sendStatusMessage(new TextComponentString(message), false);
        
        tileentity.progress += this_change;
        tileentity.last_click_time = worldIn.getWorldTime();

        Main.logger.info("biome researcher, after: {}", tileentity.progress);
        
        
        if (tileentity.progress >= 1) {
        	Biome biome = worldIn.getBiome(pos);
        	//ResourceLocation biome_name = biome.getRegistryName();
        	int biome_id = Biome.getIdForBiome(biome);
        	
        	try {
        		APIInstanceProvider provider = MystObjects.entryPoint.getProviderInstance();

        		ItemFactory apiinst = (ItemFactory)provider.getAPIInstance("itemfact-1");
        		//ItemStack page = apiinst.buildSymbolPage(biome_name);
        		ItemStack page = apiinst.buildSymbolPage(new ResourceLocation("mystcraft", "biome"+biome_id));

        		EntityItem page_entity = new EntityItem(worldIn, pos.getX(), pos.getY(), pos.getZ(), page);
        		worldIn.spawnEntity(page_entity);
        	} catch (APIUndefined | APIVersionUndefined | APIVersionRemoved e) {
        		Main.logger.info("caught exception trying to get API: {}", e);
        	}

        }
        
        return true;
    }

}
