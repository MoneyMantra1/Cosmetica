# Cosmetica NeoForge 1.21.1 Porting Notes

## Build System
- Fabric Loom was replaced with NeoForge ModDev (NeoGradle) for Minecraft **1.21.1** and Java **21**.
- Fabric metadata and access wideners were removed in favor of `META-INF/mods.toml` and an access transformer at `META-INF/accesstransformer.cfg`.
- CosmeticaDotJava is bundled via NeoForge jarJar.

## Fabric → NeoForge Mappings
| Fabric Concept | NeoForge Equivalent | Location |
| --- | --- | --- |
| `ClientModInitializer` | `@Mod("cosmetica")` + `FMLClientSetupEvent` | `cc.cosmetica.cosmetica.CosmeticaNeoForge` |
| `FabricLoader.getConfigDir()` | `FMLPaths.CONFIGDIR` | `Cosmetica` / `DebugMode` |
| `FabricLoader.getGameDir()` | `FMLPaths.GAMEDIR` | `Cosmetica` |
| `FabricLoader.isDevelopmentEnvironment()` | `!FMLLoader.isProduction()` | `DebugMode`, `ModelSprite` |
| `FabricLoader.isModLoaded("essential")` | `ModList.get().isLoaded("essential")` | `TitleScreenMixin` |
| Fabric resource reload listener | `RegisterClientReloadListenersEvent` | `CosmeticaNeoForge` |
| Access Widener | Access Transformer | `META-INF/accesstransformer.cfg` |

## UI (Sulphate)
- The needed Sulphate UI helpers were vendored into `benzenestudios.sulphate.*` to keep existing screens intact without a Fabric dependency.

## Commands
- Build: `./gradlew build`
- Run client: `./gradlew runClient`

## Known Caveats
- NeoForge mixin and access transformer behavior relies on the current Mojang mappings; if mappings change, the AT entries may need updates.
