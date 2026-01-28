/*
 * Copyright 2024 EyezahMC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cc.cosmetica.cosmetica;

import cc.cosmetica.cosmetica.cosmetics.model.Models;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.client.event.RegisterClientReloadListenersEvent;

@Mod("cosmetica")
public class CosmeticaNeoForge {
	public CosmeticaNeoForge(IEventBus modEventBus) {
		modEventBus.addListener(this::onClientSetup);
		modEventBus.addListener(this::registerReloadListeners);
		modEventBus.addListener(this::onBakingCompleted);
	}

	private void onClientSetup(FMLClientSetupEvent event) {
		event.enqueueWork(Cosmetica::initializeClient);
	}

	private void registerReloadListeners(RegisterClientReloadListenersEvent event) {
		event.registerReloadListener((ResourceManagerReloadListener) resourceManager -> Models.resetTextureBasedCaches());
	}

	private void onBakingCompleted(ModelEvent.BakingCompleted event) {
		Models.thePieShopDownTheRoad = event.getModelBakery();
	}
}
