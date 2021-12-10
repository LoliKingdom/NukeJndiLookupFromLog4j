package zone.rong.nukejndilookupfromlog4j.tweaker;

import net.minecraft.launchwrapper.ITweaker;
import net.minecraft.launchwrapper.Launch;
import net.minecraft.launchwrapper.LaunchClassLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.impl.Log4jContextFactory;
import org.apache.logging.log4j.core.lookup.Interpolator;
import org.apache.logging.log4j.core.lookup.StrLookup;
import org.apache.logging.log4j.core.util.ShutdownCallbackRegistry;
import org.apache.logging.log4j.spi.LoggerContextFactory;

import java.io.File;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

@SuppressWarnings("unused") // used by reflection
public class NukeJndiLookupFromLog4j implements ITweaker {
    private static final MethodHandle lookupsGetter;

    static {
        MethodHandles.Lookup lookup = MethodHandles.lookup();
        MethodHandle lookups = null;
        try {
            for (Field field : Interpolator.class.getDeclaredFields()) {
                Type type = field.getGenericType();
                if (type instanceof ParameterizedType) {
                    ParameterizedType paramType = (ParameterizedType) type;
                    Type[] paramTypes = paramType.getActualTypeArguments();
                    if (paramTypes.length == 2) {
                        Type firstType = paramTypes[0];
                        Type secondType = paramTypes[1];
                        if (firstType == String.class && secondType == StrLookup.class) {
                            field.setAccessible(true);
                            lookups = lookup.unreflectGetter(field);
                        }
                    }
                }
            }
        } catch (Throwable ignored) {}
        lookupsGetter = lookups;
    }

    @SuppressWarnings("all")
    private static void nukeJndiLookup(LoggerContext currentCtx) {
        try {
            StrLookup lookup = currentCtx.getConfiguration().getStrSubstitutor().getVariableResolver();
            if (lookup instanceof Interpolator) {
                Interpolator interpolator = (Interpolator) lookup;
                ((Map<String, StrLookup>) lookupsGetter.invokeExact(interpolator)).remove("jndi");
            }
        } catch (Throwable ignored) { }
    }

    public NukeJndiLookupFromLog4j() throws ReflectiveOperationException {
        // Deal with initial LoggerContexts
        final LoggerContextFactory factory = LogManager.getFactory();
        ((Log4jContextFactory) factory).getSelector().getLoggerContexts().forEach(NukeJndiLookupFromLog4j::nukeJndiLookup);
        // Capture any LoggerContexts created afterwards via proxying
        Field factoryField = LogManager.class.getDeclaredField("factory");
        factoryField.setAccessible(true);
        factoryField.set(null, Proxy.newProxyInstance(LogManager.class.getClassLoader(), factory.getClass().getInterfaces(), (proxy, method, args) -> {
            Object result = method.invoke(factory, args);
            if (result instanceof LoggerContext) {
                nukeJndiLookup((LoggerContext) result);
            }
            return result;
        }));
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
