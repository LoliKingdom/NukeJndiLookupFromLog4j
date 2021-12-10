package zone.rong.nukejndilookupfromlog4j;

import com.google.common.eventbus.EventBus;
import net.minecraftforge.fml.common.DummyModContainer;
import net.minecraftforge.fml.common.LoadController;
import net.minecraftforge.fml.common.ModMetadata;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.impl.Log4jContextFactory;
import org.apache.logging.log4j.core.lookup.Interpolator;
import org.apache.logging.log4j.core.lookup.StrLookup;
import org.apache.logging.log4j.core.util.ShutdownCallbackRegistry;
import org.apache.logging.log4j.spi.LoggerContextFactory;

import javax.annotation.Nullable;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.util.Map;

public class NukeJndiLookupFromLog4j implements IFMLLoadingPlugin {

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
        factoryField.set(null, Proxy.newProxyInstance(LogManager.class.getClassLoader(), new Class[] { LoggerContextFactory.class, ShutdownCallbackRegistry.class }, (proxy, method, args) -> {
            Object result = method.invoke(factory, args);
            if (result instanceof LoggerContext) {
                nukeJndiLookup((LoggerContext) result);
            }
            return result;
        }));
    }

    @Override
    public String[] getASMTransformerClass() {
        return new String[0];
    }

    @Override
    public String getModContainerClass() {
        return "zone.rong.nukejndilookupfromlog4j.NukeJndiLookupFromLog4j$Container";
    }

    @Nullable
    @Override
    public String getSetupClass() {
        return null;
    }

    @Override
    public void injectData(Map<String, Object> data) {}

    @Override
    public String getAccessTransformerClass() {
        return null;
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
            meta.credits = "https://github.com/apache/logging-log4j2/pull/608";
        }

        @Override
        public boolean registerBus(EventBus bus, LoadController controller) {
            bus.register(this);
            return true;
        }
    }
}
