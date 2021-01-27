var StateHolderDot = 'net.minecraft.state.StateHolder';
var StateHolderSlash = StateHolderDot.replaceAll("\\.", "/");
var ImmutableMap = "com/google/common/collect/ImmutableMap";
var MapName = "java/util/Map";

//TODO make this not fail when fast is disabled. Maybe using a replacement mixin?
function getReplacement(type) {
    if (ImmutableMap.equals(type)) {
        return MapName;
    } else if ("com/google/common/collect/ImmutableSet".equals(type)) {
        return "java/util/Set";
    } else if ("com/google/common/collect/UnmodifiableIterator".equals(type)) {
        return "java/util/Iterator";
    } else {
        return type;
    }
}

function isGetValuesOwner(type) {
    return type.equals(StateHolderSlash) ||
        type.equals("net/minecraft/block/AbstractBlock/AbstractBlockState") ||
        type.equals("net/minecraft/block/BlockState") ||
        type.equals("net/minecraft/fluid/FluidState");
}

function replaceImmutableMapWithMap(method) {
    var Opcodes = Java.type('org.objectweb.asm.Opcodes');
    var ASMAPI = Java.type('net.minecraftforge.coremod.api.ASMAPI');
    var MethodInsnNode = Java.type('org.objectweb.asm.tree.MethodInsnNode');
    var replaceTarget = ASMAPI.mapMethod("func_206871_b");
    for (var i = 0; i < method.instructions.size(); ++i) {
        var node = method.instructions.get(i);
        if (node.getOpcode() === Opcodes.INVOKEVIRTUAL) {
            if (
                isGetValuesOwner(node.owner) &&
                node.name.equals(replaceTarget) &&
                node.desc.equals("()L" + ImmutableMap + ";")
            ) {
                node.name = "getValues_Ferrite";
                node.desc = "()L" + MapName + ";";
            } else {
                var newOwner = getReplacement(node.owner);
                if (newOwner !== node.owner) {
                    node.owner = newOwner;
                    var splitPoint = node.desc.indexOf(")") + 1;
                    var args = node.desc.substring(0, splitPoint);
                    var result = node.desc.substring(splitPoint);
                    if (result.charAt(0) === 'L') {
                        var returnTypeOriginal = result.substring(1, result.length - 1);
                        node.desc = args + "L" + getReplacement(returnTypeOriginal) + ";";
                    }
                    method.instructions.set(node, new MethodInsnNode(
                        Opcodes.INVOKEINTERFACE, node.owner, node.name, node.desc
                    ));
                }
            }
        }
    }
    return method;
}

function addCoremodFor(owner, method, desc, list) {
    var targetDesc = owner.substring(owner.lastIndexOf(".") + 1) + "::" + method;
    list['Replace StateHolder::getValues in ' + targetDesc] = {
        'target': {
            'type': 'METHOD',
            'class': owner,
            'methodName': method,
            'methodDesc': desc
        },
        'transformer': replaceImmutableMapWithMap
    };
}

function initializeCoreMod() {
    var coremods = {};
    addCoremodFor(StateHolderDot, 'toString', '()Ljava/lang/String;', coremods);
    addCoremodFor(
        "net.minecraft.client.gui.overlay.DebugOverlayGui", 'func_175238_c', '()Ljava/util/List;', coremods
    );
    addCoremodFor(
        "net.minecraft.client.renderer.BlockModelShapes",
        "func_209553_a",
        "(Lnet/minecraft/util/ResourceLocation;Lnet/minecraft/block/BlockState;)Lnet/minecraft/client/renderer/model/ModelResourceLocation;",
        coremods
    );
    return coremods;
}