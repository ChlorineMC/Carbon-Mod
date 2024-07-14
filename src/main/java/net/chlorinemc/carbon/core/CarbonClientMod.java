package net.chlorinemc.carbon.core;

import me.jellysquid.mods.sodium.client.SodiumClientMod;
import net.fabricmc.api.ClientModInitializer;

import java.util.Arrays;
import java.util.List;

public class CarbonClientMod implements ClientModInitializer {
    List<ClientModInitializer> initializers = Arrays.asList(
            new SodiumClientMod()
    );


    @Override
    public void onInitializeClient() {
        initializers.forEach(ClientModInitializer::onInitializeClient);
    }
}
