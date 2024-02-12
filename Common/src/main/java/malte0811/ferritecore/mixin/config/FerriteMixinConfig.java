package malte0811.ferritecore.mixin.config;

import com.google.common.base.Preconditions;
import malte0811.ferritecore.util.Constants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;
import org.spongepowered.asm.service.MixinService;

import java.io.IOException;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;

public abstract class FerriteMixinConfig implements IMixinConfigPlugin {
    protected static final Logger LOGGER = LogManager.getLogger("ferritecore-mixin");
    private static final boolean HAS_LITHIUM;
    private static final boolean HAS_ROADRUNNER;

    static {
        HAS_LITHIUM = hasClass("me.jellysquid.mods.lithium.common.LithiumMod");
        HAS_ROADRUNNER = hasClass("me.jellysquid.mods.lithium.common.RoadRunner");
    }

    private String prefix = null;
    private final FerriteConfig.Option enableOption;
    private final LithiumSupportState lithiumState;
    private final boolean optIn;

    protected FerriteMixinConfig(
            FerriteConfig.Option enableOption, LithiumSupportState lithiumCompat, boolean optIn
    ) {
        this.enableOption = enableOption;
        this.lithiumState = lithiumCompat;
        this.optIn = optIn;
    }

    protected FerriteMixinConfig(FerriteConfig.Option enableOption) {
        this(enableOption, LithiumSupportState.NO_CONFLICT, false);
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        Preconditions.checkState(mixinClassName.startsWith(prefix), "Unexpected prefix on " + mixinClassName);
        if (!enableOption.isEnabled()) {
            if (!optIn) {
                LOGGER.warn("Mixin " + mixinClassName + " is disabled by config");
            }
            return false;
        } else if (!this.lithiumState.shouldApply()) {
            LOGGER.warn("Mixin " + mixinClassName + " is disabled automatically as lithium is installed");
            return false;
        } else {
            if (optIn) {
                LOGGER.warn("Opt-in mixin {} is enabled by config", mixinClassName);
            }
            return true;
        }
    }

    @Override
    public void onLoad(String mixinPackage) {
        prefix = mixinPackage + ".";
    }

    @Override
    public String getRefMapperConfig() { return null; }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) { }

    @Override
    public List<String> getMixins() { return null; }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) { }

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
        if (mixinClassName.equals("malte0811.ferritecore.mixin.fastmap.FastMapStateHolderMixin")) {
            replaceStateHolderValuesType(targetClass);
        }
    }

    private void replaceStateHolderValuesType(ClassNode targetClass) {
        // Vanilla currently uses Reference2ObjectArrayMap as the field type, when Reference2ObjectMap would be more
        // sensible. Until that changes we need to change the type manually.
        final String oldType = "it/unimi/dsi/fastutil/objects/Reference2ObjectArrayMap";
        final String newType = "it/unimi/dsi/fastutil/objects/Reference2ObjectMap";
        final String fieldNameToReplace = Constants.PLATFORM_HOOKS.computeStateHolderValuesName();
        final var valuesFieldNode = getFieldNode(targetClass, fieldNameToReplace);
        valuesFieldNode.desc = valuesFieldNode.desc.replace(oldType, newType);
        if (valuesFieldNode.signature != null) {
            valuesFieldNode.signature = valuesFieldNode.signature.replace(oldType, newType);
        }
        for (final var method : targetClass.methods) {
            for (AbstractInsnNode insn : method.instructions) {
                if (insn instanceof FieldInsnNode fieldInsn && fieldInsn.name.equals(fieldNameToReplace)) {
                    fieldInsn.desc = fieldInsn.desc.replace(oldType, newType);
                } else if (insn.getOpcode() == Opcodes.INVOKEVIRTUAL && insn instanceof MethodInsnNode call) {
                    // TODO this is less specific than I'd like, but anything more specific is hard and it is unlikely
                    //  that anyone will Mixin into StateHolder and add more code involving this specific class.
                    if (call.owner.contains(oldType)) {
                        call.owner = call.owner.replace(oldType, newType);
                        call.setOpcode(Opcodes.INVOKEINTERFACE);
                        call.itf = true;
                    }
                } else if (insn.getOpcode() == Opcodes.CHECKCAST && insn instanceof TypeInsnNode cast) {
                    cast.desc = cast.desc.replace(oldType, newType);
                }
            }
        }
    }

    private FieldNode getFieldNode(ClassNode clazz, String fieldName) {
        for (final var field : clazz.fields) {
            if (field.name.equals(fieldName)) {
                return field;
            }
        }
        final var fields = clazz.fields.stream()
                .map(n -> n.name)
                .reduce((s1, s2) -> s1 + ", " + s2)
                .orElse("[None]");
        throw new RuntimeException(
                "Failed to find field with name " + fieldName + " in " + clazz.name + ", available fields are " + fields
        );
    }

    private static boolean hasClass(String name) {
        try {
            // This does *not* load the class!
            MixinService.getService().getBytecodeProvider().getClassNode(name);
            return true;
        } catch (ClassNotFoundException | IOException e) {
            return false;
        }
    }

    protected enum LithiumSupportState {
        NO_CONFLICT,
        INCOMPATIBLE,
        APPLY_IF_ROADRUNNER;

        private boolean shouldApply() {
            return switch (this) {
                case NO_CONFLICT -> true;
                case INCOMPATIBLE -> !HAS_LITHIUM;
                case APPLY_IF_ROADRUNNER -> !HAS_LITHIUM || HAS_ROADRUNNER;
            };
        }
    }

    static String test(Map<String, String> m, String k) {
        return m.get(k);
    }
}
