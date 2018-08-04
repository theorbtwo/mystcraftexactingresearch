package biz.mastros.james.mystcraftthingy;

import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.Config.Comment;

@Config(modid="mystcraftthingy")
public class ConfigValues {
	public static BiomeResearcherConfig biomeresearcher = new BiomeResearcherConfig();
	
	
	static public class BiomeResearcherConfig {
		@Comment("Each click of the biome researcher will add a bit of knowledge.  When it's accumulated at least one knowledge, it will give you a page for the biome it is in.")
		public double biomeResearchAverage = 0.1;

		@Comment("Each click gains x knowledge, where x is chosen from a gaussian with the given average and standard deviation.")
		public double biomeResearchStdDev = 1.0;

		@Comment("How long must you wait, in ticks, between subsequent clicks on the researcher.  1200 ticks = 1 minute (on a non-lagging server)")
		public int biomeResearchMinTime = 1200;
	}
}
