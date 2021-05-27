package dev.gwm.spongeplugin.cosmetics;

import dev.gwm.spongeplugin.cosmetics.superobject.effect.*;
import dev.gwm.spongeplugin.cosmetics.superobject.effect.base.CosmeticEffect;
import dev.gwm.spongeplugin.cosmetics.util.CosmeticsCommandUtils;
import dev.gwm.spongeplugin.cosmetics.util.CosmeticsSuperObjectCategories;
import dev.gwm.spongeplugin.cosmetics.util.CosmeticsUtils;
import dev.gwm.spongeplugin.library.event.SuperObjectCategoriesRegistrationEvent;
import dev.gwm.spongeplugin.library.event.SuperObjectIdentifiersRegistrationEvent;
import dev.gwm.spongeplugin.library.event.SuperObjectsRegistrationEvent;
import dev.gwm.spongeplugin.library.util.*;
import dev.gwm.spongeplugin.library.util.service.SuperObjectService;
import ninja.leaping.configurate.ConfigurationNode;
import org.slf4j.Logger;
import org.spongepowered.api.Game;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.EventContext;
import org.spongepowered.api.event.game.GameReloadEvent;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.event.game.state.GameStoppingServerEvent;
import org.spongepowered.api.plugin.Dependency;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;

import javax.inject.Inject;
import java.io.File;
import java.nio.file.Files;
import java.util.concurrent.atomic.AtomicInteger;

@Plugin(id = "cosmetics",
		name = "Cosmetics",
		version = "1.5.3",
		description = "Fancy cosmetic effects",
		authors = {"GWM"/* My contacts:
		                 * E-Mail(nazark@tutanota.com),
		                 * Telegram(@gwmdev)*/},
		dependencies = {
				@Dependency(id = "gwm_library")
		})
public class Cosmetics extends SpongePlugin {

	public static final Version VERSION = new Version(1, 5, 3);

	private static Cosmetics instance = null;

	public static Cosmetics getInstance() {
	    if (instance == null) {
	        throw new RuntimeException("Cosmetics is not initialized!");
        }
		return instance;
	}

	private final Game game;
	private final Cause cause;

	private final Logger logger;
	private final PluginContainer container;

    private final File configDirectory;

    private final File effectsDirectory;

	private final Config config;
	private final Config languageConfig;

	private final Language language;

	private boolean logLoadedEffects = true;

	@Inject
	public Cosmetics(Game game,
					  Logger logger,
					  PluginContainer container,
					  @ConfigDir(sharedRoot = false) File configDirectory) {
		Cosmetics.instance = this;
		this.game = game;
		cause = Cause.of(EventContext.empty(), container);
		this.logger = logger;
		this.container = container;
		this.configDirectory = configDirectory;
		effectsDirectory = new File(configDirectory, "effects");
		if (!configDirectory.exists()) {
			logger.info("Config directory does not exist! Trying to create it...");
			try {
				if (configDirectory.mkdirs()) {
					logger.info("Config directory successfully created!");
				} else {
					logger.error("Failed to create config directory!");
				}
			} catch (Exception e) {
				logger.warn("Failed to create config directory!", e);
			}
		}
		if (!effectsDirectory.exists()) {
			logger.info("Cosmetic Effects directory does not exist! Trying to create it...");
			try {
				if (effectsDirectory.mkdirs()) {
					logger.info("Cosmetic Effects directory successfully created!");
				} else {
					logger.error("Failed to create Cosmetic Effects directory!");
				}
			} catch (Exception e) {
				logger.warn("Failed to create Cosmetic Effects config directory!", e);
			}
		}
		config = new Config.Builder(this, new File(configDirectory, "config.conf")).
				loadDefaults("config.conf").
				build();
		languageConfig = new Config.Builder(this, new File(configDirectory, "language.conf")).
				loadDefaults(getDefaultTranslationPath()).
				build();
		language = new Language(this);
		logger.info("Construction completed!");
	}

	@Listener
	public void onPreInitialization(GamePreInitializationEvent event) {
		loadConfigValues();
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
        event.register(new SuperObjectIdentifier<>(CosmeticsSuperObjectCategories.COSMETIC_EFFECT, Symbols2dCosmeticEffects.TYPE),
                Symbols2dCosmeticEffects.class);
        event.register(new SuperObjectIdentifier<>(CosmeticsSuperObjectCategories.COSMETIC_EFFECT, Figlet2dCosmeticEffect.TYPE),
                Figlet2dCosmeticEffect.class);
        event.register(new SuperObjectIdentifier<>(CosmeticsSuperObjectCategories.COSMETIC_EFFECT, Image2dCosmeticEffect.TYPE),
                Image2dCosmeticEffect.class);
	}

	@Listener
	public void onSuperObjectsRegistration(SuperObjectsRegistrationEvent event) {
		loadCosmeticEffects();
	}

	@Listener
	public void reloadListener(GameReloadEvent event) {
		reload();
		logger.info("Reload completed!");
	}

	@Listener
    public void onStopping(GameStoppingServerEvent event) {
        logger.info("Stopping completed!");
    }

    public void reload() {
        config.reload();
        languageConfig.reload();
        loadConfigValues();
        unloadCosmeticEffects();
		loadCosmeticEffects();
        logger.info("Plugin has been reloaded.");
    }

    private void loadConfigValues() {
		try {
			ConfigurationNode logLoadedEffectsNode = config.getNode("LOG_LOADED_EFFECTS");
			logLoadedEffects = logLoadedEffectsNode.getBoolean(true);
		} catch (Exception e) {
			logger.error("Failed to load config values!", e);
		}
    }

	private void loadCosmeticEffects() {
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

	private void unloadCosmeticEffects() {
		game.getServiceManager().provide(SuperObjectService.class).get().
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

	public Logger getLogger() {
		return logger;
	}

	@Override
	public PluginContainer getContainer() {
		return container;
	}

	@Override
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

	@Override
	public Config getLanguageConfig() {
		return languageConfig;
	}

    @Override
	public Language getLanguage() {
		return language;
	}

	public boolean isLogLoadedEffects() {
		return logLoadedEffects;
	}
}

