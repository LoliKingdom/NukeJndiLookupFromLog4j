package zone.rong.nukejndilookupfromlog4j.tweaker;

import net.minecraft.launchwrapper.ITweaker;
import net.minecraft.launchwrapper.LaunchClassLoader;
import zone.rong.nukejndilookupfromlog4j.NukeJndiLookupFromLog4j;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

@SuppressWarnings("unused")
public class NukeJndiLookupFromLog4jTweaker implements ITweaker {
    public NukeJndiLookupFromLog4jTweaker() throws ReflectiveOperationException {
        NukeJndiLookupFromLog4j.init();
    }

    @Override
    public void acceptOptions(List<String> args, File gameDir, File assetsDir, String profile) {
    }

    @Override
    public void injectIntoClassLoader(LaunchClassLoader classLoader) {
        if (classExists(classLoader, "net.minecraftforge.fml.relauncher.CoreModManager")) {
            callSupport(classLoader, "Support1122");
        } else if (classExists(classLoader, "cpw.mods.fml.relauncher.CoreModManager")) {
            callSupport(classLoader, "Support1710");
        } else {
            throw new IllegalStateException("this version of minecraft is not supported!");
        }
    }

    private boolean classExists(LaunchClassLoader classLoader, String name) {
        try {
            classLoader.findClass(name);
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    private void callSupport(LaunchClassLoader classLoader, String name) {
        try {
            Class<?> supportClass = classLoader.findClass("zone.rong.nukejndilookupfromlog4j.supports." + name);
            supportClass.getMethod("runSupport").invoke(null);
        } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public String getLaunchTarget() {
        return null;
    }

    @Override
    public String[] getLaunchArguments() {
        return new String[0];
    }
}
