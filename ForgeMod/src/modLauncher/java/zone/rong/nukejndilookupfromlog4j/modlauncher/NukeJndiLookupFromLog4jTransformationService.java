package zone.rong.nukejndilookupfromlog4j.modlauncher;

import com.google.auto.service.AutoService;
import cpw.mods.modlauncher.api.IEnvironment;
import cpw.mods.modlauncher.api.ITransformationService;
import cpw.mods.modlauncher.api.ITransformer;
import zone.rong.nukejndilookupfromlog4j.NukeJndiLookupFromLog4j;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;
import java.util.Set;

@SuppressWarnings("unused")
@AutoService(ITransformationService.class)
public class NukeJndiLookupFromLog4jTransformationService implements ITransformationService {
    public NukeJndiLookupFromLog4jTransformationService() throws ReflectiveOperationException {
        NukeJndiLookupFromLog4j.init();
    }

    @Nonnull
    @Override
    public String name() {
        return "nukejndilookupfromlog4j";
    }

    @Override
    public void initialize(IEnvironment environment) {
    }

    @Override
    public void beginScanning(IEnvironment environment) {
    }

    @Override
    public void onLoad(IEnvironment env, Set<String> otherServices) {
    }

    @Nonnull
    @Override
    public List<ITransformer> transformers() {
        return Collections.emptyList();
    }
}
