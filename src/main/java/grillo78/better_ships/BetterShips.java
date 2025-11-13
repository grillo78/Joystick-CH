package grillo78.better_ships;

import com.mojang.blaze3d.vertex.VertexConsumer;
import grillo78.better_ships.capability.JoystickControllerProvider;
import grillo78.better_ships.capability.ShipRotationsProvider;
import grillo78.better_ships.client.control.InputController;
import grillo78.better_ships.client.control.KeyMappings;
import grillo78.better_ships.client.entity.ModModelLayers;
import grillo78.better_ships.client.entity.SpaceshipRenderer;
import grillo78.better_ships.client.entity.models.JawModel;
import grillo78.better_ships.client.screen.JoystickConfigurationScreen;
import grillo78.better_ships.entity.Jaw;
import grillo78.better_ships.entity.ModEntities;
import grillo78.better_ships.entity.Spaceship;
import grillo78.better_ships.item.ModItems;
import grillo78.better_ships.network.PacketHandler;
import grillo78.better_ships.network.messages.SendJoystickInput;
import grillo78.better_ships.network.messages.ToggleAutoland;
import grillo78.better_ships.network.messages.ToggleSpaceHyperspeed;
import grillo78.better_ships.util.JoystickConfig;
import net.lointain.cosmos.CosmosMod;
import net.lointain.cosmos.entity.RocketSeatEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.*;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingBreatheEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.joml.Quaternionf;
import org.lwjgl.glfw.GLFW;

@Mod(BetterShips.MOD_ID)
public class BetterShips {

    public static final String MOD_ID = "better_ships";
    public static long DEBUG_TICK = 20;
    private boolean rolling = false;
    private InputController inputController = new InputController();

    public BetterShips(FMLJavaModLoadingContext context) {
        context.registerConfig(ModConfig.Type.CLIENT, JoystickConfig.clientSpec);
        context.getModEventBus().addListener(this::commonSetup);
        context.getModEventBus().addListener(this::entityAttributesCreation);
        MinecraftForge.EVENT_BUS.addGenericListener(Entity.class, this::attachCapabilities);
        MinecraftForge.EVENT_BUS.addListener(this::breath);
        MinecraftForge.EVENT_BUS.addListener(this::entityDamage);
        MinecraftForge.EVENT_BUS.addListener(this::playerTick);
        ModEntities.ENTITIES.register(context.getModEventBus());
        ModItems.ITEMS.register(context.getModEventBus());
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
            context.getModEventBus().addListener(this::registerKeys);
            context.getModEventBus().addListener(this::registerRenderers);
            context.getModEventBus().addListener(this::registryModels);
            context.getModEventBus().addListener(this::registerLayerDefinitions);
            MinecraftForge.EVENT_BUS.addListener(this::preRenderEntity);
            MinecraftForge.EVENT_BUS.addListener(this::postRenderEntity);
            MinecraftForge.EVENT_BUS.addListener(this::inputTick);
            MinecraftForge.EVENT_BUS.addListener(this::keyInput);
            MinecraftForge.EVENT_BUS.addListener(this::onCameraAnglesUpdate);
        });
    }

    private void entityAttributesCreation(EntityAttributeCreationEvent event) {
        event.put(ModEntities.JAW.get(), Jaw.registerMonsterAttributes().build());
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        PacketHandler.init();
    }

    public void breath(LivingBreatheEvent event) {
        if (event.getEntity() instanceof Player && event.getEntity().getVehicle() != null) {
            event.getEntity().getVehicle().getCapability(ShipRotationsProvider.CAPABILITY).ifPresent(shipRotations -> {
                event.setCanBreathe(true);
                event.setCanRefillAir(true);
                event.setConsumeAirAmount(0);
                event.setRefillAirAmount(1);
            });
        }
    }

    private void entityDamage(LivingAttackEvent event) {
        if (event.getEntity() instanceof Player && event.getEntity().getVehicle() != null) {
            event.getEntity().getVehicle().getCapability(ShipRotationsProvider.CAPABILITY).ifPresent(shipRotations -> {
                if (event.getSource().is(DamageTypes.FALL))
                    event.setCanceled(true);
            });
        }
    }

    private void playerTick(LivingEvent.LivingTickEvent event) {
        if (event.getEntity() instanceof Player) {
            if (event.getEntity().getVehicle() != null)
                event.getEntity().getVehicle().getCapability(ShipRotationsProvider.CAPABILITY).ifPresent(shipRotations -> {
                    event.getEntity().resetFallDistance();
                });
            event.getEntity().getCapability(JoystickControllerProvider.CONTROLLER).ifPresent(joystickController -> {
                joystickController.tick();
            });
        }
    }

    @OnlyIn(Dist.CLIENT)
    public void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntities.JAW.get(), pContext -> new SpaceshipRenderer(pContext, new JawModel(pContext.bakeLayer(ModModelLayers.JAW))));
    }

    @OnlyIn(Dist.CLIENT)
    private void registerKeys(RegisterKeyMappingsEvent event) {
        event.register(KeyMappings.OPEN_CONFIG_SCREEN);
        event.register(KeyMappings.TOGGLE_HYPERSPEED);
        event.register(KeyMappings.TOGGLE_INPUT);
        event.register(KeyMappings.TOGGLE_ROLL);
    }

    @OnlyIn(Dist.CLIENT)
    private void keyInput(InputEvent.Key event) {
        if (Minecraft.getInstance().screen == null) {
            if (event.getKey() == KeyMappings.OPEN_CONFIG_SCREEN.getKey().getValue() && event.getAction() == GLFW.GLFW_PRESS) {
                Minecraft.getInstance().setScreen(new JoystickConfigurationScreen());
            }
            if (event.getKey() == KeyMappings.TOGGLE_HYPERSPEED.getKey().getValue() && event.getAction() == GLFW.GLFW_PRESS) {
                PacketHandler.INSTANCE.sendToServer(new ToggleSpaceHyperspeed());
            }
            if (event.getKey() == KeyMappings.TOGGLE_INPUT.getKey().getValue() && event.getAction() == GLFW.GLFW_PRESS) {
                JoystickConfig.CLIENT.usingJoystick.set(!JoystickConfig.CLIENT.usingJoystick.get());
            }
            if (event.getKey() == KeyMappings.TOGGLE_ROLL.getKey().getValue() && event.getAction() == GLFW.GLFW_PRESS) {
                rolling = !rolling;
            }
        }
    }

    @OnlyIn(Dist.CLIENT)
    private void onCameraAnglesUpdate(ViewportEvent.ComputeCameraAngles event) {
        if (Minecraft.getInstance().player != null && Minecraft.getInstance().player.getVehicle() != null && !Minecraft.getInstance().gameRenderer.getMainCamera().isDetached()) {
            Minecraft.getInstance().player.getVehicle().getCapability(ShipRotationsProvider.CAPABILITY).ifPresent(shipRotations -> {
                event.setYaw(0);
                event.setPitch(0);
                event.setRoll(0);
            });
            Minecraft.getInstance().level.entitiesForRendering().forEach(entity -> {
                if (entity instanceof Spaceship)
                    ((Spaceship) entity).setPartialTick(event.getPartialTick());
            });
        }
    }

    @OnlyIn(Dist.CLIENT)
    private void inputTick(MovementInputUpdateEvent event) {
        double thrust = 0;
        double roll = 0;
        double pitch = 0;
        double yaw = 0;
        if (Minecraft.getInstance().screen == null) {
            if (JoystickConfig.CLIENT.usingJoystick.get() && JoystickConfig.CLIENT.joystickSelectedIndex.get() >= 0 && GLFW.glfwJoystickPresent(JoystickConfig.CLIENT.joystickSelectedIndex.get()) && GLFW.glfwGetJoystickAxes(JoystickConfig.CLIENT.joystickSelectedIndex.get()).limit() >= 4) {
                inputController.tick();
                thrust = inputController.getThrust();
                roll = inputController.getRoll();
                pitch = inputController.getPitch();
                yaw = inputController.getYaw();

                if (inputController.isHyperspeed() && !inputController.isoHyperspeed()) {
                    PacketHandler.INSTANCE.sendToServer(new ToggleSpaceHyperspeed());
                }
                if (inputController.isAutoland() && !inputController.isoAutoland()) {
                    PacketHandler.INSTANCE.sendToServer(new ToggleAutoland());
                }
            } else {
                thrust = ((Minecraft.getInstance().options.keySprint.isDown() ? 1 : 0) + (event.getInput().jumping ? -1 : 0)) * (JoystickConfig.CLIENT.thrustAxisInverted.get() ? -1 : 1);
                pitch = ((event.getInput().down ? -0.5 : 0) + (event.getInput().up ? 0.5 : 0)) * (JoystickConfig.CLIENT.pitchAxisInverted.get() ? -0.5 : 0.5);
                if (rolling)
                    roll = ((event.getInput().left ? 0.5 : 0) + (event.getInput().right ? -0.5 : 0)) * (JoystickConfig.CLIENT.rollAxisInverted.get() ? -0.5 : 0.5);
                else
                    yaw = ((event.getInput().left ? 0.5 : 0) + (event.getInput().right ? -0.5 : 0)) * (JoystickConfig.CLIENT.yawAxisInverted.get() ? -0.5 : 0.5);
            }
        }
        setJoystickData(thrust, yaw, pitch, roll);
    }

    @OnlyIn(Dist.CLIENT)
    private void setJoystickData(double thrust, double yaw, double pitch, double roll) {

        double deadzone = 0.05;
        PacketHandler.INSTANCE.sendToServer(new SendJoystickInput(thrust < -deadzone || thrust > deadzone ? thrust : 0, yaw < -deadzone || yaw > deadzone ? yaw : 0, pitch < -deadzone || pitch > deadzone ? pitch : 0, roll < -deadzone || roll > deadzone ? roll : 0));
        Minecraft.getInstance().player.getCapability(JoystickControllerProvider.CONTROLLER).ifPresent(joystickController -> {
            joystickController.setThrust(thrust < -deadzone || thrust > deadzone ? thrust : 0);
            joystickController.setYaw(yaw < -deadzone || yaw > deadzone ? yaw : 0);
            joystickController.setPitch(pitch < -deadzone || pitch > deadzone ? pitch : 0);
            joystickController.setRoll(roll < -deadzone || roll > deadzone ? roll : 0);
        });
    }

    @OnlyIn(Dist.CLIENT)
    private void registryModels(ModelEvent.RegisterAdditional event) {
        event.register(new ResourceLocation(CosmosMod.MODID, "block/steelspaceship"));
        event.register(new ResourceLocation(CosmosMod.MODID, "block/titaniumspaceship"));
        event.register(new ResourceLocation(CosmosMod.MODID, "block/nickelspaceship"));
    }

    @OnlyIn(Dist.CLIENT)
    private void preRenderEntity(RenderLivingEvent.Pre event) {
        if (event.getEntity() instanceof RocketSeatEntity) {
            event.getPoseStack().pushPose();
            event.getEntity().getCapability(ShipRotationsProvider.CAPABILITY).ifPresent(shipRotations -> {
                event.getPoseStack().translate(0, 0.6, 0);
//                event.getPoseStack().mulPose(new Quaternionf(shipRotations.getRotations()));
                event.getPoseStack().mulPose(shipRotations.getRotationsO().nlerp(shipRotations.getRotations(), event.getPartialTick(), new Quaternionf()));

                event.getPoseStack().translate(-0.6, -0.45, -0.5);
                event.getPoseStack().scale(1.2F, 1.2F, 1.2F);
            });
            String engine;
            switch (event.getEntity().getEntityData().get(RocketSeatEntity.DATA_engine)) {
                default:
                case 0:
                    engine = "steel";
                    break;
                case 1:
                    engine = "titanium";
                    break;
                case 3:
                    engine = "nickel";
                    break;
            }
            BakedModel model = Minecraft.getInstance().getModelManager().getModel(new ResourceLocation(CosmosMod.MODID, "block/" + engine + "spaceship"));

            VertexConsumer vertexConsumer = event.getMultiBufferSource()
                    .getBuffer(RenderType.entityTranslucentCull(InventoryMenu.BLOCK_ATLAS));
            for (BakedQuad quad : model.getQuads(null, null, RandomSource.create(), ModelData.EMPTY, null)) {
                vertexConsumer.putBulkData(event.getPoseStack().last(), quad, 1, 1, 1, event.getPackedLight(), OverlayTexture.NO_OVERLAY);
            }
            event.getPoseStack().popPose();
        }
        if (event.getEntity() instanceof Player) {
            event.getPoseStack().pushPose();
            if (event.getEntity().getVehicle() != null)
                event.getEntity().getVehicle().getCapability(ShipRotationsProvider.CAPABILITY).ifPresent(shipRotations -> {
                    shipRotations.rotatePlayer(event.getPoseStack(), event.getEntity().getVehicle());
                });

        }
    }

    @OnlyIn(Dist.CLIENT)
    private void postRenderEntity(RenderLivingEvent.Post event) {
        if (event.getEntity() instanceof Player)
            event.getPoseStack().popPose();
    }

    @OnlyIn(Dist.CLIENT)
    public void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(ModModelLayers.JAW, () -> JawModel.createBodyLayer());
    }

    private void attachCapabilities(final AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof Player) {
            event.addCapability(new ResourceLocation(MOD_ID, "joystick_controller"), new JoystickControllerProvider(event.getObject()));
        }
        if (event.getObject() instanceof RocketSeatEntity || event.getObject() instanceof Spaceship) {
            event.addCapability(new ResourceLocation(MOD_ID, "ship_rotations"), new ShipRotationsProvider());
        }
    }
}
