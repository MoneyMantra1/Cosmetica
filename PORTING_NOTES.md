# Cosmetica NeoForge 1.21.1 Porting Notes

## Build System
- Fabric Loom was replaced with NeoForge ModDev (NeoGradle) plugin `net.neoforged.moddev` v1.0.21 for Minecraft **1.21.1** and Java **21**.
- Fabric metadata (`fabric.mod.json`) and access wideners were removed in favor of `META-INF/neoforge.mods.toml` and an access transformer at `META-INF/accesstransformer.cfg`.
- CosmeticaDotJava is bundled via NeoForge `jarJar` configuration.
- The standalone Mixin Gradle plugin (`mixin {}` block) is not needed; NeoForge ModDev handles the Mixin annotation processor automatically.
- Parchment mappings (2024.07.28) are used for readable parameter names in Mojang-mapped code.

## Fabric to NeoForge Mappings
| Fabric Concept | NeoForge Equivalent | Location |
| --- | --- | --- |
| `ClientModInitializer` | `@Mod("cosmetica")` + `FMLClientSetupEvent` | `CosmeticaNeoForge` |
| `FabricLoader.getConfigDir()` | `FMLPaths.CONFIGDIR` | `Cosmetica`, `DebugMode` |
| `FabricLoader.getGameDir()` | `FMLPaths.GAMEDIR` | `Cosmetica` |
| `FabricLoader.isDevelopmentEnvironment()` | `!FMLLoader.isProduction()` | `DebugMode`, `ModelSprite` |
| `FabricLoader.isModLoaded("essential")` | `ModList.get().isLoaded("essential")` | `TitleScreenMixin` |
| `FabricLoader.getModContainer(...).getMetadata().getVersion()` | `ModList.get().getModContainerById(...)` | `Cosmetica` |
| Fabric resource reload listener | `RegisterClientReloadListenersEvent` | `CosmeticaNeoForge` |
| Access Widener (`.aw`) | Access Transformer (`META-INF/accesstransformer.cfg`) | See below |
| `fabric.mod.json` | `META-INF/neoforge.mods.toml` | Resources |

## How Mixins Are Loaded
- Mixin config is declared in `META-INF/neoforge.mods.toml` via the `[[mixins]]` section:
  ```toml
  [[mixins]]
  config="cosmetica.mixins.json"
  ```
- All mixins are listed under the `"client"` key in `cosmetica.mixins.json` (client-side only mod).
- NeoForge's bundled Mixin + MixinExtras handles annotation processing and refmap generation.
- The refmap (`cosmetica.refmap.json`) maps Mojang+Parchment dev names to SRG intermediary names for production runtime.
- `compatibilityLevel` is `JAVA_21` and `defaultRequire` is `1` (all injections must succeed).

## How Access Widener Was Replaced
Fabric access wideners were converted to NeoForge access transformers in `META-INF/accesstransformer.cfg`:
```
public net.minecraft.client.renderer.texture.SpriteContents$AnimatedTexture
public net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket$EntryBuilder
public net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket$EntryBuilder profile
public-f net.minecraft.client.renderer.texture.SpriteContents getFrameCount()I
```
**Syntax notes:**
- `public` makes a class/field/method public.
- `public-f` makes a method public AND removes the `final` modifier (needed to override `getFrameCount()` in `ModelSpriteContents`).
- Class names use dots, nested classes use `$`, method descriptors use JVM format.

## How Libraries Are Bundled
- CosmeticaDotJava is included via NeoForge's `jarJar` mechanism in `build.gradle`:
  ```groovy
  implementation "com.github.Cosmetica-cc:CosmeticaDotJava:${project.cosmetica_dot_java_version}"
  jarJar "com.github.Cosmetica-cc:CosmeticaDotJava:${project.cosmetica_dot_java_version}"
  ```
  This embeds the library inside the mod JAR so it is available at runtime.
- Sulphate UI helpers were vendored directly into `benzenestudios.sulphate.*` to avoid any Fabric dependency.

## Mod Entrypoint
- The entry class is `CosmeticaNeoForge`, annotated with `@Mod("cosmetica")`.
- The constructor receives `IEventBus` (mod event bus) via NeoForge constructor injection (not the deprecated `FMLJavaModLoadingContext.get()`).
- Client initialization (`Cosmetica.initializeClient()`) runs via `FMLClientSetupEvent.enqueueWork()`.
- Resource reload listener for clearing texture caches is registered via `RegisterClientReloadListenersEvent`.
- The `ModelBakery` instance is captured via `ModelEvent.BakingCompleted` event (replaces the old `ModelManagerMixin` which used Yarn intermediary names).

## Mixin Yarn-to-Mojang Migration
Several mixins from the original Fabric codebase used Yarn intermediary names (`method_XXXXX`, `field_XXXXX`) that do not exist in NeoForge's Mojang-mapped environment. These were resolved as follows:

| Original Target | Issue | Resolution |
| --- | --- | --- |
| `ModelManagerMixin` targeting `method_45884` | Yarn intermediary name | Replaced with `ModelEvent.BakingCompleted` NeoForge event |
| `ClientboundPlayerInfoUpdatePacketActionMixin` targeting `method_46342` | Yarn intermediary lambda name | Moved logic to `ClientPacketListenerMixin.handlePlayerInfoUpdate` |
| `ConnectionThreadMixin` shadowing `field_2416` | Yarn intermediary field name | Changed to `this$0` (standard synthetic outer-class reference) |

## Key Registration
- Keybindings are registered via a Mixin into `Options.load()` (`keys.OptionsMixin`) that appends Cosmetica's key mappings to the `keyMappings` array.
- This approach works on NeoForge but is a Fabric-era pattern. A future improvement could use NeoForge's `RegisterKeyMappingsEvent`.

## UI (Sulphate)
- The needed Sulphate UI helpers were vendored into `benzenestudios.sulphate.*` to keep existing screens intact without a Fabric dependency.
- `ExtendedScreen` interface is implemented via `screen.ScreenMixin` which provides `getChildren()`, `getWidgets()`, and `setTitle()` to all screens.

## Commands
- Build: `./gradlew build`
- Run client: `./gradlew runClient`

## Known Caveats
- NeoForge mixin and access transformer behavior relies on the current Mojang mappings; if mappings change, the AT entries and mixin targets may need updates.
- The `ConnectionThreadMixin` targets `ConnectScreen$1` (anonymous inner class). If the connect screen implementation changes in future MC versions, this mixin would break.
- Key registration via mixin (`keys.OptionsMixin`) is fragile; consider migrating to `RegisterKeyMappingsEvent` in the future.
- The `jarJar` dependency on CosmeticaDotJava uses JitPack; if JitPack is unavailable, the build will fail.
