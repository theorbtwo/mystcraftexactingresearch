package biz.mastros.james.mystcraftthingy;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.*;
import net.minecraftforge.fml.common.Mod.EventHandler;

import org.apache.logging.log4j.Logger;



@Mod(modid = "mystcraftthingy", dependencies = "after:mystcraft", useMetadata=true)
public class Main {
	public static Logger logger;
	
    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
    	logger = event.getModLog();
    	
    	logger.info("preInit");
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
    	logger.info("init");
    }    
}
