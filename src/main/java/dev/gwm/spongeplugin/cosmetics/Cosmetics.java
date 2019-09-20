package dev.gwm.spongeplugin.cosmetics;

import com.google.inject.Inject;
import dev.gwm.spongeplugin.cosmetics.superobject.effect.BlockHighlightCosmeticEffect;
import dev.gwm.spongeplugin.cosmetics.superobject.effect.CircleCosmeticEffect;
import dev.gwm.spongeplugin.cosmetics.superobject.effect.GlobeCosmeticEffect;
import dev.gwm.spongeplugin.cosmetics.superobject.effect.HelixCosmeticEffect;
import dev.gwm.spongeplugin.cosmetics.superobject.effect.base.CosmeticEffect;
import dev.gwm.spongeplugin.cosmetics.utils.CosmeticsCommandUtils;
import dev.gwm.spongeplugin.cosmetics.utils.CosmeticsSuperObjectCategories;
import dev.gwm.spongeplugin.cosmetics.utils.CosmeticsUtils;
import dev.gwm.spongeplugin.library.event.SuperObjectCategoriesRegistrationEvent;
import dev.gwm.spongeplugin.library.event.SuperObjectIdentifiersRegistrationEvent;
import dev.gwm.spongeplugin.library.event.SuperObjectsRegistrationEvent;
import dev.gwm.spongeplugin.library.utils.*;
import ninja.leaping.configurate.ConfigurationNode;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.asset.AssetManager;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.EventContext;
import org.spongepowered.api.event.game.GameReloadEvent;
import org.spongepowered.api.event.game.state.GameConstructionEvent;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.event.game.state.GameStoppingServerEvent;
import org.spongepowered.api.plugin.Dependency;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;

import java.io.File;
import java.nio.file.Files;
import java.util.concurrent.atomic.AtomicInteger;

@Plugin(id = "cosmetics",
		name = "Cosmetics",
		version = "1.1",
		description = "Fancy cosmetic effects",
		authors = {"GWM"/* My contacts:
		                 * E-Mail(nazark@tutanota.com),
		                 * Telegram(@grewema),
		                 * Discord(GWM#2192)*/},
		dependencies = {
				@Dependency(id = "gwm_library")
		})
public class Cosmetics extends SpongePlugin {

	public static final Version VERSION = new Version(null, 1, 1);

	private static Cosmetics instance = null;

	public static Cosmetics getInstance() {
	    if (instance == null) {
	        throw new RuntimeException("Cosmetics is not initialized!");
        }
		return instance;
	}

	private Cause cause;

    @Inject
    @ConfigDir(sharedRoot = false)
    private File configDirectory;
    private File effectsDirectory;

	@Inject
	private Logger logger;

	@Inject
	private PluginContainer container;

	private Config config;
	private Config languageConfig;

	private Language language;

	private boolean checkUpdates = true;
	private boolean logLoadedEffects = true;

	@Listener
	public void onConstruct(GameConstructionEvent event) {
		instance = this;
	}

	@Listener
	public void onPreInitialization(GamePreInitializationEvent event) {
	    effectsDirectory = new File(configDirectory, "effects");
        if (!configDirectory.exists()) {
            logger.info("Config directory does not exist! Trying to create it...");
            try {
                configDirectory.mkdirs();
                logger.info("Config directory successfully created!");
            } catch (Exception e) {
                logger.warn("Failed to create config directory!", e);
            }
        }
        if (!effectsDirectory.exists()) {
            logger.info("Cosmetic Effects directory does not exist! Trying to create it...");
            try {
                effectsDirectory.mkdirs();
                logger.info("Cosmetic Effects directory successfully created!");
            } catch (Exception e) {
                logger.warn("Failed to create Cosmetic Effects config directory!", e);
            }
        }
        cause = Cause.of(EventContext.empty(), container);
		AssetManager assetManager = Sponge.getAssetManager();
		config = new Config(this, new File(configDirectory, "config.conf"),
				assetManager.getAsset(this, "config.conf"), true, false);
		languageConfig = new Config(this, new File(configDirectory, "language.conf"),
				getDefaultTranslation(assetManager), true, false);
		loadConfigValues();
		language = new Language(this);
		if (checkUpdates) {
		    checkUpdates();
        }
		CosmeticsCommandUtils.registerCommands(this);
		logger.info("PreInitialization completed!");
	}

	@Listener
	public void onCategoriesRegistration(SuperObjectCategoriesRegistrationEvent event) {
		event.register(CosmeticsSuperObjectCategories.COSMETIC_EFFECT);
	}

	@Listener
	public void onIdentifiersRegistration(SuperObjectIdentifiersRegistrationEvent event) {
		event.register(new SuperObjectIdentifier<>(CosmeticsSuperObjectCategories.COSMETIC_EFFECT, BlockHighlightCosmeticEffect.TYPE),
				BlockHighlightCosmeticEffect.class);
		event.register(new SuperObjectIdentifier<>(CosmeticsSuperObjectCategories.COSMETIC_EFFECT, GlobeCosmeticEffect.TYPE),
				GlobeCosmeticEffect.class);
		event.register(new SuperObjectIdentifier<>(CosmeticsSuperObjectCategories.COSMETIC_EFFECT, HelixCosmeticEffect.TYPE),
				HelixCosmeticEffect.class);
		event.register(new SuperObjectIdentifier<>(CosmeticsSuperObjectCategories.COSMETIC_EFFECT, CircleCosmeticEffect.TYPE),
				CircleCosmeticEffect.class);
	}

	@Listener
	public void onSuperObjectsRegistration(SuperObjectsRegistrationEvent event) {
		loadCpes();
	}

	@Listener
	public void reloadListener(GameReloadEvent event) {
		reload();
		logger.info("Reload completed!");
	}

	@Listener
    public void onStopping(GameStoppingServerEvent event) {
        save();
        logger.info("Stopping completed!");
    }

    public void reload() {
        cause = Cause.of(EventContext.empty(), container);
        config.reload();
        languageConfig.reload();
        loadConfigValues();
        language = new Language(this);
        if (checkUpdates) {
            checkUpdates();
        }
        unloadCpes();
		loadCpes();
        logger.info("Plugin has been reloaded.");
    }

    private void loadConfigValues() {
		try {
			ConfigurationNode checkUpdatesNode = config.getNode("CHECK_UPDATES");
			ConfigurationNode logLoadedEffectsNode = config.getNode("LOG_LOADED_EFFECTS");
			checkUpdates = checkUpdatesNode.getBoolean(true);
			logLoadedEffects = logLoadedEffectsNode.getBoolean(true);
		} catch (Exception e) {
			logger.error("Failed to load config values!", e);
		}
    }

	private void loadCpes() {
		try {
			AtomicInteger loaded = new AtomicInteger();
			AtomicInteger skipped = new AtomicInteger();
			AtomicInteger failed = new AtomicInteger();
			Files.walk(effectsDirectory.toPath()).
					filter(path -> path.getFileName().toString().endsWith(".conf")).
					forEach(path -> {
						File cpeFile = path.toFile();
						if (cpeFile.isFile()) {
							try {
								if (CosmeticsUtils.loadCosmeticEffect(cpeFile, false)) {
									loaded.incrementAndGet();
								} else {
									skipped.incrementAndGet();
								}
							} catch (Exception e) {
								logger.warn("Failed to load Cosmetic Effect from file \"" + CosmeticsUtils.getCosmeticEffectRelativePath(cpeFile) + "\"!", e);
								failed.incrementAndGet();
							}
						}
					});
			String message = String.format("Cosmetic Effects load statistics (loaded/skipped/failed): %d/%d/%d",
					loaded.get(), skipped.get(), failed.get());
			if (loaded.get() > 0 && failed.get() == 0) {
				logger.info(message);
			} else {
				logger.warn(message);
			}
		} catch (Exception e) {
			logger.warn("Failed to load Cosmetic Effects!", e);
		}
	}

	private void unloadCpes() {
		Sponge.getServiceManager().provide(SuperObjectsService.class).get().
				shutdownCreatedSuperObjects(superObject -> superObject instanceof CosmeticEffect);
	}

	@Override
	public Version getVersion() {
		return VERSION;
	}

	@Override
	public Cause getCause() {
		return cause;
	}

	@Override
	public PluginContainer getContainer() {
		return container;
	}

	public Logger getLogger() {
		return logger;
	}

	public File getConfigDirectory() {
		return configDirectory;
	}

	public File getEffectsDirectory() {
		return effectsDirectory;
	}

	@Override
	public Config getConfig() {
		return config;
	}

	public Config getLanguageConfig() {
		return languageConfig;
	}

    @Override
	public Language getLanguage() {
		return language;
	}

	public boolean isCheckUpdates() {
		return checkUpdates;
	}

	public boolean isLogLoadedEffects() {
		return logLoadedEffects;
	}
}
