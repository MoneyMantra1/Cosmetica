/*
 * Copyright 2022, 2023 EyezahMC
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

package cc.cosmetica.cosmetica.mixin.screen;

import benzenestudios.sulphate.ExtendedScreen;
import cc.cosmetica.cosmetica.Cosmetica;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Screen.class)
public class ScreenMixin implements ExtendedScreen {
	@Shadow @Nullable protected Minecraft minecraft;
	@Shadow @Final protected java.util.List<Renderable> renderables;
	@Shadow @Final protected java.util.List<GuiEventListener> children;
	// Note: title field is final in 1.21.1+, cannot be shadowed as mutable
	// @Shadow protected Component title;

	@Inject(at = @At("HEAD"), method = "handleComponentClicked", cancellable = true)
	private void onHandleClick(Style style, CallbackInfoReturnable<Boolean> info) {
		if (Cosmetica.handleComponentClicked(this.minecraft, style)) {
			info.setReturnValue(true);
		}
	}

	@Override
	public java.util.List<? extends GuiEventListener> getChildren() {
		return this.children;
	}

	@Override
	public java.util.List<? extends Renderable> getWidgets() {
		return this.renderables;
	}

	@Override
	@Deprecated
	public void setTitle(Component title) {
		// No-op: Screen.title is final in 1.21.1+
		// Use a separate field for dynamic titles instead
	}
}
