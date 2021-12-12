package zone.rong.nukejndilookupfromlog4j.supports;

import com.google.common.eventbus.EventBus;
import cpw.mods.fml.common.DummyModContainer;
import cpw.mods.fml.common.LoadController;
import cpw.mods.fml.common.ModMetadata;
import cpw.mods.fml.relauncher.FMLInjectionData;

@SuppressWarnings("unused") // used by reflection
public class Support1710 {
    public static void runSupport() {
        FMLInjectionData.containers.add(Container.class.getName());
    }

    public static class Container extends DummyModContainer {
        public Container() {
            super(new ModMetadata());
            ModMetadata meta = this.getMetadata();
            meta.modId = "nukejndilookupfromlog4j";
            meta.name = "NukeJndiLookupFromLog4j";
            meta.description = "Prevents a major vulnerability introduced by log4j from being abused.";
            meta.version = "1.0.0";
            meta.authorList.add("Rongmario");
            meta.authorList.add("anatawa12");
            meta.credits = "https://github.com/apache/logging-log4j2/pull/608";
        }

        @Override
        public boolean registerBus(EventBus bus, LoadController controller) {
            bus.register(this);
            return true;
        }
    }
}
