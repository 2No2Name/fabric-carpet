package optionsmod.mixins;

import optionsmod.OptionsmodServer;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin
{
    //to inject right before
    // this.tickWorlds(booleanSupplier_1);
    @Inject(method = "init", at = @At(value = "RETURN")
    )
    private void onInit(CallbackInfo ci) {
        //OM start game hook
        OptionsmodServer.onGameStarted();
    }
}
