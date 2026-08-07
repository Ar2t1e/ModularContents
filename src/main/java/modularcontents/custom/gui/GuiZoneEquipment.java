package modularcontents.custom.gui;

import modularcontents.custom.client.GuiTheme;
import modularcontents.ModularcontentsMod;
import modularcontents.custom.loot.EquipmentManager;
import modularcontents.custom.network.PacketOpenCreator;
import modularcontents.custom.network.PacketRequestZones;
import modularcontents.custom.network.PacketSyncZones;
import modularcontents.custom.network.PacketZoneLoot;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.inventory.IInventory;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class GuiZoneEquipment extends GuiScreen {

    private static final int TEX_SIZE = 256;
    private static final int MAP_RADIUS = TEX_SIZE / 2;
    private static final int DISPLAY_SIZE = 240;
    private static final int PRESET_PANEL_WIDTH = 158;
    private static final int ROW_HEIGHT = 20;
    private static final int SCROLLBAR_W = 4;
    private static final int MAX_ZONE_SIZE = 128;

    private static final float ZOOM_MIN = 0.35f;
    private static final float ZOOM_MAX = 8.0f;

    private int texCX;
    private int texCZ;
    private int texStep = 1;

    private double viewCX;
    private double viewCZ;
    private double viewTargetX;
    private double viewTargetZ;
    private float zoom = 1.5f;
    private float zoomTarget = zoom;

    private boolean panning = false;
    private float panLastX;
    private float panLastY;
    private double zoomAnchorWorldX;
    private double zoomAnchorWorldZ;
    private int zoomAnchorScreenX;
    private int zoomAnchorScreenY;

    private DynamicTexture minimapTexture;
    private int textureId = -1;

    private boolean hasSelection = false;
    private boolean dragging = false;
    private int startWorldX;
    private int startWorldZ;
    private int selMinX;
    private int selMinZ;
    private int selMaxX;
    private int selMaxZ;

    private final List<PresetRow> presetRows = new ArrayList<>();
    private final List<BlockPos> containerPositions = new ArrayList<>();

    private List<PacketSyncZones.ClientZoneInfo> existingZones = new ArrayList<>();
    private UUID editId = null;
    private GuiTextField nameField;
    private GuiTextField colorField;
    private GuiTextField respawnField;

    private float scroll = 0.0f;
    private float targetScroll = 0.0f;
    private long lastFrameTime = 0L;
    private int draggingSlider = -1;
    private boolean draggingScrollbar = false;
    private float scrollGrabOffset = 0.0f;
    private PresetRow hoveredSlider = null;
    private BlockPos hoveredContainerPos = null;

    private GuiLaptop.FlatButton fillButton;
    private GuiLaptop.FlatButton clearButton;
    private GuiLaptop.FlatButton backButton;

    private static class PresetRow {
        final String name;
        boolean enabled = false;
        float chance = 100.0f;

        PresetRow(String name) {
            this.name = name;
        }
    }

    @Override
    public void initGui() {
        super.initGui();
        this.buttonList.clear();
        Keyboard.enableRepeatEvents(true);

        if (this.mc.player != null) {
            this.viewCX = this.mc.player.posX;
            this.viewCZ = this.mc.player.posZ;
            this.viewTargetX = this.viewCX;
            this.viewTargetZ = this.viewCZ;
            this.texCX = MathHelper.floor(this.viewCX);
            this.texCZ = MathHelper.floor(this.viewCZ);
        }

        presetRows.clear();
        for (String name : EquipmentManager.getPresetNames()) {
            presetRows.add(new PresetRow(name));
        }

        int presetPanelX = presetPanelX();
        int buttonsY = firstButtonY();
        this.fillButton = new GuiLaptop.FlatButton(0, presetPanelX, buttonsY, PRESET_PANEL_WIDTH, 16, tr("fill"));
        this.clearButton = new GuiLaptop.FlatButton(1, presetPanelX, buttonsY + 20, PRESET_PANEL_WIDTH, 16, tr("clear"));
        this.backButton = new GuiLaptop.FlatButton(2, presetPanelX, buttonsY + 40, PRESET_PANEL_WIDTH, 16, tr("back"));

        this.buttonList.add(this.fillButton);
        this.buttonList.add(this.clearButton);
        this.buttonList.add(this.backButton);

        this.nameField = new GuiTextField(10, this.fontRenderer, presetPanelX, buttonsY - 80, PRESET_PANEL_WIDTH, 14);
        this.nameField.setMaxStringLength(32);
        this.nameField.setText("Zone");
        this.colorField = new GuiTextField(11, this.fontRenderer, presetPanelX, buttonsY - 52, PRESET_PANEL_WIDTH, 14);
        this.colorField.setMaxStringLength(8);
        this.colorField.setText("88FFAA00");
        this.respawnField = new GuiTextField(12, this.fontRenderer, presetPanelX, buttonsY - 24, PRESET_PANEL_WIDTH, 14);
        this.respawnField.setMaxStringLength(10);
        this.respawnField.setText("6000");

        if (this.textureId == -1) {
            generateMinimap();
        }

        ModularcontentsMod.PACKET_HANDLER.sendToServer(new PacketRequestZones());

        refreshContainers();
        updateButtonState();
    }

    private static String tr(String key, Object... args) {
        return I18n.format("modularcontents.zone." + key, args);
    }

    private int panelWidth() {
        return 10 + DISPLAY_SIZE + 12 + PRESET_PANEL_WIDTH + 10;
    }

    private int panelHeight() {
        return 40 + DISPLAY_SIZE + 12;
    }

    private int getMapX() {
        return (this.width - panelWidth()) / 2 + 10;
    }

    private int getMapY() {
        return (this.height - panelHeight()) / 2 + 40;
    }

    private int firstButtonY() {
        return getMapY() + DISPLAY_SIZE - 56;
    }

    private int presetPanelX() {
        return getMapX() + DISPLAY_SIZE + 12;
    }

    private int presetPanelTop() {
        return (this.height - panelHeight()) / 2 + 24;
    }

    private int presetListTop() {
        return presetPanelTop() + 14;
    }

    private int presetListBottom() {
        return firstButtonY() - 100;
    }

    private int sliderX() {
        return presetPanelX() + 2;
    }

    private int sliderW() {
        return PRESET_PANEL_WIDTH - SCROLLBAR_W - 4 - 30;
    }

    private int contentHeight() {
        return presetRows.size() * ROW_HEIGHT;
    }

    private int maxScroll() {
        return Math.max(0, contentHeight() - (presetListBottom() - presetListTop()));
    }

    private int handleHeight() {
        int listH = presetListBottom() - presetListTop();
        int contentH = contentHeight();
        if (contentH <= listH) {
            return listH;
        }
        return Math.max(16, listH * listH / contentH);
    }

    private float getMouseYPrecise() {
        return this.height - (float) Mouse.getY() * this.height / (float) this.mc.displayHeight - 1.0f;
    }

    private float getMouseXPrecise() {
        return (float) Mouse.getX() * this.width / (float) this.mc.displayWidth;
    }

    private double halfViewSpan() {
        return DISPLAY_SIZE / (2.0 * zoom);
    }

    private int texHalfSpan() {
        return MAP_RADIUS * texStep;
    }

    private int stepForZoom(float z) {
        int needed = MathHelper.ceil(DISPLAY_SIZE * 1.3 / z / (double) TEX_SIZE);
        return Math.max(1, needed);
    }

    private void refreshMinimapIfNeeded() {
        int wanted = stepForZoom(zoom);
        double slack = texHalfSpan() - halfViewSpan();
        boolean outOfBounds = slack < 0
                || Math.abs(viewCX - texCX) > slack
                || Math.abs(viewCZ - texCZ) > slack;

        if (wanted != texStep || outOfBounds || textureId == -1) {
            texStep = wanted;
            texCX = MathHelper.floor(viewCX);
            texCZ = MathHelper.floor(viewCZ);
            generateMinimap();
        }
    }

    private void refreshContainers() {
        containerPositions.clear();
        if (this.mc.world == null) {
            return;
        }
        double reach = halfViewSpan() + 16.0;
        for (TileEntity te : new ArrayList<>(this.mc.world.loadedTileEntityList)) {
            if (!(te instanceof IInventory)) {
                continue;
            }
            BlockPos pos = te.getPos();
            if (Math.abs(pos.getX() - viewCX) <= reach && Math.abs(pos.getZ() - viewCZ) <= reach) {
                containerPositions.add(pos);
            }
        }
    }

    private void generateMinimap() {
        if (this.mc.world == null) {
            return;
        }
        if (minimapTexture == null) {
            minimapTexture = new DynamicTexture(TEX_SIZE, TEX_SIZE);
        }
        int[] pixels = minimapTexture.getTextureData();

        for (int i = 0; i < TEX_SIZE; i++) {
            for (int j = 0; j < TEX_SIZE; j++) {
                int wx = texCX - texHalfSpan() + i * texStep;
                int wz = texCZ - texHalfSpan() + j * texStep;

                int y = this.mc.world.getHeight(wx, wz);
                BlockPos p = new BlockPos(wx, y > 0 ? y - 1 : 0, wz);
                IBlockState state = this.mc.world.getBlockState(p);
                int color = state.getMapColor(this.mc.world, p).colorValue;

                boolean water = state.getMaterial() == Material.WATER;

                if (wx % 16 == 0 || wz % 16 == 0) {
                    int or = (color >> 16) & 0xFF;
                    int og = (color >> 8) & 0xFF;
                    int ob = color & 0xFF;
                    color = ((or * 60 / 100) << 16) | ((og * 60 / 100) << 8) | (ob * 60 / 100);
                }

                if (water) {
                    int or = (color >> 16) & 0xFF;
                    int og = (color >> 8) & 0xFF;
                    int ob = color & 0xFF;
                    color = (Math.min(255, or / 2 + 60) << 16) | ((og / 2 + 60) << 8) | (Math.min(255, ob / 2 + 130));
                }

                pixels[j * TEX_SIZE + i] = 0xFF000000 | color;
            }
        }
        minimapTexture.updateDynamicTexture();
        this.textureId = minimapTexture.getGlTextureId();
    }

    private double worldXAt(double screenX) {
        return viewCX + (screenX - (getMapX() + DISPLAY_SIZE / 2.0)) / zoom;
    }

    private double worldZAt(double screenY) {
        return viewCZ + (screenY - (getMapY() + DISPLAY_SIZE / 2.0)) / zoom;
    }

    private int worldXFromMouse(int mouseX) {
        int mapX = getMapX();
        return MathHelper.floor(worldXAt(MathHelper.clamp(mouseX, mapX, mapX + DISPLAY_SIZE - 1)));
    }

    private int worldZFromMouse(int mouseY) {
        int mapY = getMapY();
        return MathHelper.floor(worldZAt(MathHelper.clamp(mouseY, mapY, mapY + DISPLAY_SIZE - 1)));
    }

    private int screenXFromWorld(int worldX) {
        return getMapX() + DISPLAY_SIZE / 2 + (int) Math.round((worldX - viewCX) * zoom);
    }

    private int screenYFromWorld(int worldZ) {
        return getMapY() + DISPLAY_SIZE / 2 + (int) Math.round((worldZ - viewCZ) * zoom);
    }

    private boolean isInsideMap(int mouseX, int mouseY) {
        int mapX = getMapX();
        int mapY = getMapY();
        return mouseX >= mapX && mouseX < mapX + DISPLAY_SIZE && mouseY >= mapY && mouseY < mapY + DISPLAY_SIZE;
    }

    public void receiveZones(List<PacketSyncZones.ClientZoneInfo> zones) {
        this.existingZones = zones;
    }

    private void updateButtonState() {
        boolean anyPreset = false;
        for (PresetRow row : presetRows) {
            if (row.enabled) {
                anyPreset = true;
                break;
            }
        }
        this.fillButton.enabled = hasSelection && anyPreset;
        this.clearButton.enabled = hasSelection;
    }

    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        int dwheel = Mouse.getEventDWheel();
        if (dwheel != 0) {
            int mx = Mouse.getX() * this.width / this.mc.displayWidth;
            int my = this.height - Mouse.getY() * this.height / this.mc.displayHeight - 1;
            int panelX = presetPanelX();
            int listTop = presetListTop();
            int listBottom = presetListBottom();
            if (isInsideMap(mx, my)) {
                float factor = dwheel > 0 ? 1.25f : 1.0f / 1.25f;
                float next = MathHelper.clamp(zoomTarget * factor, ZOOM_MIN, ZOOM_MAX);
                if (next != zoomTarget) {
                    zoomAnchorWorldX = worldXAt(mx);
                    zoomAnchorWorldZ = worldZAt(my);
                    zoomAnchorScreenX = mx;
                    zoomAnchorScreenY = my;
                    zoomTarget = next;
                }
            } else if (mx >= panelX - 2 && mx < panelX + PRESET_PANEL_WIDTH + 2 && my >= listTop && my < listBottom) {
                int notches = dwheel / 120;
                if (notches == 0) {
                    notches = dwheel > 0 ? 1 : -1;
                }
                PresetRow hovered = sliderUnderMouse(mx, my);
                if (hovered != null) {
                    hovered.chance = MathHelper.clamp(hovered.chance + notches * 5.0f, 0.0f, 100.0f);
                } else {
                    targetScroll = MathHelper.clamp(targetScroll - notches * (ROW_HEIGHT * 1.5f), 0.0f, maxScroll());
                }
            }
        }
    }

    private PresetRow sliderUnderMouse(int mouseX, int mouseY) {
        int listTop = presetListTop();
        int listBottom = presetListBottom();
        int sx = sliderX();
        int sw = sliderW();
        int off = (int) scroll;
        for (int idx = 0; idx < presetRows.size(); idx++) {
            int rowY = listTop + idx * ROW_HEIGHT - off;
            if (rowY + ROW_HEIGHT < listTop || rowY > listBottom) {
                continue;
            }
            int barY = rowY + 13;
            if (mouseX >= sx - 2 && mouseX <= sx + sw + 2 && mouseY >= barY - 4 && mouseY <= barY + 7) {
                return presetRows.get(idx);
            }
        }
        return null;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();

        if (dragging) {
            int cx = worldXFromMouse(mouseX);
            int cz = worldZFromMouse(mouseY);
            selMinX = Math.min(startWorldX, cx);
            selMaxX = Math.max(startWorldX, cx);
            selMinZ = Math.min(startWorldZ, cz);
            selMaxZ = Math.max(startWorldZ, cz);
        }

        long now = System.nanoTime();
        float dt = lastFrameTime == 0L ? 0.016f : Math.min(0.1f, (now - lastFrameTime) / 1.0e9f);
        lastFrameTime = now;
        float blend = 1.0f - (float) Math.exp(-16.0f * dt);

        int maxScroll = maxScroll();
        if (draggingScrollbar && maxScroll > 0) {
            int listTop = presetListTop();
            int travel = (presetListBottom() - listTop) - handleHeight();
            if (travel > 0) {
                float thumbTop = getMouseYPrecise() - scrollGrabOffset;
                float frac = MathHelper.clamp((thumbTop - listTop) / (float) travel, 0.0f, 1.0f);
                targetScroll = frac * maxScroll;
                scroll = targetScroll;
            }
        }
        targetScroll = MathHelper.clamp(targetScroll, 0.0f, maxScroll);
        scroll += (targetScroll - scroll) * blend;
        if (Math.abs(targetScroll - scroll) < 0.1f) {
            scroll = targetScroll;
        }
        scroll = MathHelper.clamp(scroll, 0.0f, maxScroll);

        if (Math.abs(zoomTarget - zoom) > 0.0001f) {
            zoom += (zoomTarget - zoom) * blend;
            if (Math.abs(zoomTarget - zoom) < 0.002f) {
                zoom = zoomTarget;
            }
            viewTargetX = zoomAnchorWorldX - (zoomAnchorScreenX - (getMapX() + DISPLAY_SIZE / 2.0)) / zoom;
            viewTargetZ = zoomAnchorWorldZ - (zoomAnchorScreenY - (getMapY() + DISPLAY_SIZE / 2.0)) / zoom;
            viewCX = viewTargetX;
            viewCZ = viewTargetZ;
        } else {
            viewCX += (viewTargetX - viewCX) * blend;
            viewCZ += (viewTargetZ - viewCZ) * blend;
            if (Math.abs(viewTargetX - viewCX) < 0.01) viewCX = viewTargetX;
            if (Math.abs(viewTargetZ - viewCZ) < 0.01) viewCZ = viewTargetZ;
        }
        refreshMinimapIfNeeded();

        int startX = (this.width - panelWidth()) / 2;
        int startY = (this.height - panelHeight()) / 2;
        int panelW = panelWidth();
        int panelH = panelHeight();

        drawRect(startX - 2, startY - 2, startX + panelW + 2, startY + panelH + 2, GuiTheme.BORDER);
        drawRect(startX, startY, startX + panelW, startY + panelH, GuiTheme.PANEL);

        this.drawString(this.fontRenderer, tr("title"), startX + 10, startY + 12, GuiTheme.ACCENT);

        hoveredSlider = sliderUnderMouse(mouseX, mouseY);

        drawMap();
        drawPresetPanel();

        int presetPanelX = presetPanelX();
        int buttonsY = firstButtonY();
        this.drawString(this.fontRenderer, "Name:", presetPanelX, buttonsY - 92, GuiTheme.TEXT_DIM);
        this.nameField.drawTextBox();
        this.drawString(this.fontRenderer, "Color (Hex):", presetPanelX, buttonsY - 64, GuiTheme.TEXT_DIM);
        this.colorField.drawTextBox();
        this.drawString(this.fontRenderer, "Respawn (ticks):", presetPanelX, buttonsY - 36, GuiTheme.TEXT_DIM);
        this.respawnField.drawTextBox();

        super.drawScreen(mouseX, mouseY, partialTicks);

        hoveredContainerPos = null;
        if (isInsideMap(mouseX, mouseY)) {
            int mapX = getMapX();
            int mapY = getMapY();
            for (BlockPos pos : containerPositions) {
                int sx = screenXFromWorld(pos.getX());
                int sy = screenYFromWorld(pos.getZ());
                if (sx < mapX || sx >= mapX + DISPLAY_SIZE || sy < mapY || sy >= mapY + DISPLAY_SIZE) {
                    continue;
                }
                if (mouseX >= sx - 4 && mouseX <= sx + 4 && mouseY >= sy - 4 && mouseY <= sy + 4) {
                    hoveredContainerPos = pos;
                    break;
                }
            }

            if (hoveredContainerPos != null) {
                String hover = "Chest [" + hoveredContainerPos.getX() + ", " + hoveredContainerPos.getY() + ", " + hoveredContainerPos.getZ() + "] - Click to TP";
                int w = this.fontRenderer.getStringWidth(hover);
                drawRect(mouseX + 8, mouseY - 14, mouseX + 12 + w, mouseY - 2, 0xCC000000);
                this.fontRenderer.drawString(hover, mouseX + 10, mouseY - 12, GuiTheme.GREEN);
            } else {
                int hx = worldXFromMouse(mouseX);
                int hz = worldZFromMouse(mouseY);
                String hover = "X: " + hx + " Z: " + hz;
                int w = this.fontRenderer.getStringWidth(hover);
                drawRect(mouseX + 8, mouseY - 14, mouseX + 12 + w, mouseY - 2, 0xCC000000);
                this.fontRenderer.drawString(hover, mouseX + 10, mouseY - 12, GuiTheme.ACCENT);
            }
        }
    }

    private void drawMap() {
        int mapX = getMapX();
        int mapY = getMapY();

        drawRect(mapX - 1, mapY - 1, mapX + DISPLAY_SIZE + 1, mapY + DISPLAY_SIZE + 1, GuiTheme.BORDER);
        drawRect(mapX, mapY, mapX + DISPLAY_SIZE, mapY + DISPLAY_SIZE, 0xFF111111);

        ScaledResolution sr = new ScaledResolution(this.mc);
        int sf = sr.getScaleFactor();
        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        GL11.glScissor(mapX * sf, this.mc.displayHeight - (mapY + DISPLAY_SIZE) * sf, DISPLAY_SIZE * sf, DISPLAY_SIZE * sf);

        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        if (this.textureId != -1) {
            double x0 = mapX + DISPLAY_SIZE / 2.0 + (texCX - texHalfSpan() - viewCX) * zoom;
            double x1 = x0 + TEX_SIZE * texStep * zoom;
            double y0 = mapY + DISPLAY_SIZE / 2.0 + (texCZ - texHalfSpan() - viewCZ) * zoom;
            double y1 = y0 + TEX_SIZE * texStep * zoom;

            GlStateManager.bindTexture(this.textureId);
            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder buffer = tessellator.getBuffer();
            buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
            buffer.pos(x0, y1, this.zLevel).tex(0.0, 1.0).endVertex();
            buffer.pos(x1, y1, this.zLevel).tex(1.0, 1.0).endVertex();
            buffer.pos(x1, y0, this.zLevel).tex(1.0, 0.0).endVertex();
            buffer.pos(x0, y0, this.zLevel).tex(0.0, 0.0).endVertex();
            tessellator.draw();
        }

        drawContainerMarkers(mapX, mapY);

        for (PacketSyncZones.ClientZoneInfo zone : this.existingZones) {
            int zx1 = MathHelper.clamp(screenXFromWorld(zone.minX), mapX, mapX + DISPLAY_SIZE);
            int zy1 = MathHelper.clamp(screenYFromWorld(zone.minZ), mapY, mapY + DISPLAY_SIZE);
            int zx2 = MathHelper.clamp(screenXFromWorld(zone.maxX + 1), mapX, mapX + DISPLAY_SIZE);
            int zy2 = MathHelper.clamp(screenYFromWorld(zone.maxZ + 1), mapY, mapY + DISPLAY_SIZE);

            if (zx1 < zx2 && zy1 < zy2) {
                int zFillCol = zone.color;
                int zLineCol = 0xFF000000 | (zone.color & 0xFFFFFF); // Opaque version of color
                drawRect(zx1, zy1, zx2, zy2, zFillCol);
                drawRect(zx1, zy1, zx2, zy1 + 1, zLineCol);
                drawRect(zx1, zy2 - 1, zx2, zy2, zLineCol);
                drawRect(zx1, zy1, zx1 + 1, zy2, zLineCol);
                drawRect(zx2 - 1, zy1, zx2, zy2, zLineCol);
            }
        }

        if (this.mc.player != null) {
            int pcx = screenXFromWorld(MathHelper.floor(this.mc.player.posX));
            int pcy = screenYFromWorld(MathHelper.floor(this.mc.player.posZ));
            drawRect(pcx - 1, pcy - 4, pcx + 2, pcy + 5, GuiTheme.ACCENT);
            drawRect(pcx - 4, pcy - 1, pcx + 5, pcy + 2, GuiTheme.ACCENT);
        }

        if (hasSelection || dragging) {
            int x1 = MathHelper.clamp(screenXFromWorld(selMinX), mapX, mapX + DISPLAY_SIZE);
            int y1 = MathHelper.clamp(screenYFromWorld(selMinZ), mapY, mapY + DISPLAY_SIZE);
            int x2 = MathHelper.clamp(screenXFromWorld(selMaxX + 1), mapX, mapX + DISPLAY_SIZE);
            int y2 = MathHelper.clamp(screenYFromWorld(selMaxZ + 1), mapY, mapY + DISPLAY_SIZE);

            boolean valid = isSelectionValid();
            int fillCol = valid ? 0x55FFAA00 : 0x55FF3030;
            int lineCol = valid ? GuiTheme.ACCENT : 0xFFFF3030;
            drawRect(x1, y1, x2, y2, fillCol);
            drawRect(x1, y1, x2, y1 + 1, lineCol);
            drawRect(x1, y2 - 1, x2, y2, lineCol);
            drawRect(x1, y1, x1 + 1, y2, lineCol);
            drawRect(x2 - 1, y1, x2, y2, lineCol);
        }

        GL11.glDisable(GL11.GL_SCISSOR_TEST);

        String info;
        if (hasSelection || dragging) {
            int sizeX = selMaxX - selMinX + 1;
            int sizeZ = selMaxZ - selMinZ + 1;
            info = tr("zone_size", sizeX, sizeZ);
            if (!isSelectionValid()) {
                info = info + " " + tr("too_large", MAX_ZONE_SIZE);
            }
        } else {
            info = tr("drag_hint");
        }
        this.fontRenderer.drawString(info, mapX, mapY + DISPLAY_SIZE + 2, GuiTheme.TEXT_DIM);
    }

    private void drawContainerMarkers(int mapX, int mapY) {
        for (BlockPos pos : containerPositions) {
            int sx = screenXFromWorld(pos.getX());
            int sy = screenYFromWorld(pos.getZ());
            if (sx < mapX || sx >= mapX + DISPLAY_SIZE || sy < mapY || sy >= mapY + DISPLAY_SIZE) {
                continue;
            }
            drawRect(sx - 2, sy - 2, sx + 3, sy + 3, 0xFF2A1E0C);
            drawRect(sx - 1, sy - 1, sx + 2, sy + 2, 0xFFD9A441);
        }
    }

    private void drawPresetPanel() {
        int panelX = presetPanelX();
        int panelTop = presetPanelTop();
        int panelBottom = presetListBottom();

        drawRect(panelX - 2, panelTop - 2, panelX + PRESET_PANEL_WIDTH + 2, panelBottom + 2, GuiTheme.BORDER);
        drawRect(panelX - 1, panelTop - 1, panelX + PRESET_PANEL_WIDTH + 1, panelBottom + 1, GuiTheme.PANEL_ALT);

        this.fontRenderer.drawString(tr("presets"), panelX, panelTop, GuiTheme.ACCENT);

        int listTop = presetListTop();
        int listBottom = panelBottom;
        int listH = listBottom - listTop;

        if (presetRows.isEmpty()) {
            this.fontRenderer.drawString(tr("no_presets"), panelX, listTop + 4, GuiTheme.TEXT_DIM);
            return;
        }

        ScaledResolution sr = new ScaledResolution(this.mc);
        int sf = sr.getScaleFactor();
        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        GL11.glScissor((panelX - 1) * sf, this.mc.displayHeight - listBottom * sf, (PRESET_PANEL_WIDTH + 1) * sf, listH * sf);

        GlStateManager.pushMatrix();
        GlStateManager.translate(0.0f, -scroll, 0.0f);

        int firstIdx = Math.max(0, (int) (scroll / ROW_HEIGHT) - 1);
        int lastIdx = Math.min(presetRows.size(), (int) ((scroll + listH) / ROW_HEIGHT) + 2);
        for (int idx = firstIdx; idx < lastIdx; idx++) {
            int rowY = listTop + idx * ROW_HEIGHT;
            drawPresetRow(panelX, rowY, presetRows.get(idx));
        }

        GlStateManager.popMatrix();
        GL11.glDisable(GL11.GL_SCISSOR_TEST);

        int maxScroll = maxScroll();
        if (maxScroll > 0) {
            int trackX = panelX + PRESET_PANEL_WIDTH - SCROLLBAR_W;
            drawRect(trackX, listTop, trackX + SCROLLBAR_W, listBottom, 0xFF0A0A0A);
            int handleH = handleHeight();
            int handleY = listTop + (int) ((scroll / maxScroll) * (listH - handleH));
            drawRect(trackX, handleY, trackX + SCROLLBAR_W, handleY + handleH, draggingScrollbar ? GuiTheme.ACCENT : GuiTheme.BORDER);
        }
    }

    private void drawPresetRow(int panelX, int rowY, PresetRow row) {
        int cbX = panelX + 2;
        int cbY = rowY + 1;
        drawRect(cbX, cbY, cbX + 10, cbY + 10, row.enabled ? GuiTheme.ACCENT : GuiTheme.BORDER);
        drawRect(cbX + 1, cbY + 1, cbX + 9, cbY + 9, 0xFF111111);
        if (row.enabled) {
            drawRect(cbX + 3, cbY + 3, cbX + 7, cbY + 7, GuiTheme.GREEN);
        }

        String label = this.fontRenderer.trimStringToWidth(row.name, PRESET_PANEL_WIDTH - 22);
        this.fontRenderer.drawString(label, panelX + 16, rowY + 2, row.enabled ? GuiTheme.TEXT : GuiTheme.TEXT_DIM);

        int sx = sliderX();
        int sw = sliderW();
        int barY = rowY + 13;
        int barH = 3;
        drawRect(sx, barY, sx + sw, barY + barH, 0xFF000000);
        int fillW = (int) (sw * row.chance / 100.0f);
        drawRect(sx, barY, sx + fillW, barY + barH, row.enabled ? GuiTheme.ACCENT : 0xFF6E5320);
        int hx = sx + fillW;
        boolean hot = row == hoveredSlider;
        if (hot) {
            drawRect(hx - 3, barY - 4, hx + 3, barY + barH + 4, GuiTheme.ACCENT);
        }
        drawRect(hx - 2, barY - 3, hx + 2, barY + barH + 3, row.enabled ? 0xFFFFFFFF : GuiTheme.TEXT_DIM);

        String val = (int) row.chance + "%";
        this.fontRenderer.drawString(val, sx + sw + 6, rowY + 11, hot ? GuiTheme.ACCENT : GuiTheme.TEXT);
    }

    private boolean isSelectionValid() {
        if (!hasSelection && !dragging) return false;
        return (selMaxX - selMinX) <= MAX_ZONE_SIZE && (selMaxZ - selMinZ) <= MAX_ZONE_SIZE;
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);

        if (mouseButton != 0) {
            if (mouseButton == 1 && isInsideMap(mouseX, mouseY)) {
                panning = true;
                panLastX = getMouseXPrecise();
                panLastY = getMouseYPrecise();
            }
            return;
        }

        if (hoveredContainerPos != null) {
            this.mc.player.sendChatMessage("/tp " + hoveredContainerPos.getX() + " " + hoveredContainerPos.getY() + " " + hoveredContainerPos.getZ());
            this.mc.displayGuiScreen(null);
            return;
        }

        int panelX = presetPanelX();
        int listTop = presetListTop();
        int listBottom = presetListBottom();
        int listH = listBottom - listTop;

        int maxScroll = maxScroll();
        if (maxScroll > 0) {
            int trackX = panelX + PRESET_PANEL_WIDTH - SCROLLBAR_W;
            if (mouseX >= trackX - 1 && mouseX < trackX + SCROLLBAR_W + 1 && mouseY >= listTop && mouseY < listBottom) {
                int handleH = handleHeight();
                int handleY = listTop + (int) ((scroll / maxScroll) * (listH - handleH));
                if (mouseY >= handleY && mouseY < handleY + handleH) {
                    scrollGrabOffset = mouseY - handleY;
                } else {
                    scrollGrabOffset = handleH / 2.0f;
                }
                draggingScrollbar = true;
                return;
            }
        }

        if (mouseX >= panelX - 1 && mouseX < panelX + PRESET_PANEL_WIDTH && mouseY >= listTop && mouseY < listBottom) {
            int sx = sliderX();
            int sw = sliderW();
            int off = (int) scroll;
            for (int idx = 0; idx < presetRows.size(); idx++) {
                int rowY = listTop + idx * ROW_HEIGHT - off;
                if (rowY + ROW_HEIGHT < listTop || rowY > listBottom) {
                    continue;
                }
                int cbX = panelX + 2;
                int cbY = rowY + 1;
                if (mouseX >= cbX && mouseX < cbX + 10 && mouseY >= cbY && mouseY < cbY + 10) {
                    presetRows.get(idx).enabled = !presetRows.get(idx).enabled;
                    updateButtonState();
                    return;
                }
                int barY = rowY + 13;
                if (mouseX >= sx - 2 && mouseX <= sx + sw + 2 && mouseY >= barY - 4 && mouseY <= barY + 7) {
                    draggingSlider = idx;
                    updateSliderDrag(mouseX);
                    return;
                }
            }
            return;
        }

        this.nameField.mouseClicked(mouseX, mouseY, mouseButton);
        this.colorField.mouseClicked(mouseX, mouseY, mouseButton);
        this.respawnField.mouseClicked(mouseX, mouseY, mouseButton);

        if (isInsideMap(mouseX, mouseY)) {
            int wx = worldXFromMouse(mouseX);
            int wz = worldZFromMouse(mouseY);

            PacketSyncZones.ClientZoneInfo clickedZone = null;
            for (PacketSyncZones.ClientZoneInfo z : this.existingZones) {
                if (wx >= z.minX && wx <= z.maxX && wz >= z.minZ && wz <= z.maxZ) {
                    clickedZone = z;
                    break;
                }
            }

            if (clickedZone != null) {
                this.editId = clickedZone.id;
                this.nameField.setText(clickedZone.name);
                this.colorField.setText(String.format("%08X", clickedZone.color));
                this.respawnField.setText(String.valueOf(clickedZone.respawnIntervalTicks));
                this.selMinX = clickedZone.minX;
                this.selMaxX = clickedZone.maxX;
                this.selMinZ = clickedZone.minZ;
                this.selMaxZ = clickedZone.maxZ;
                this.hasSelection = true;
                if (this.fillButton != null) this.fillButton.displayString = "Update";

                for (PresetRow row : presetRows) {
                    if (clickedZone.presets != null && clickedZone.presets.containsKey(row.name)) {
                        row.enabled = true;
                        row.chance = clickedZone.presets.get(row.name);
                    } else {
                        row.enabled = false;
                        row.chance = 100.0f;
                    }
                }
                updateButtonState();
            } else {
                this.editId = null;
                if (this.fillButton != null) this.fillButton.displayString = tr("fill");
                dragging = true;
                startWorldX = wx;
                startWorldZ = wz;
                selMinX = startWorldX;
                selMaxX = startWorldX;
                selMinZ = startWorldZ;
                selMaxZ = startWorldZ;
            }
        }
    }

    @Override
    protected void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
        if (panning) {
            float px = getMouseXPrecise();
            float py = getMouseYPrecise();
            viewTargetX -= (px - panLastX) / (double) zoom;
            viewTargetZ -= (py - panLastY) / (double) zoom;
            panLastX = px;
            panLastY = py;
            return;
        }
        if (draggingSlider >= 0) {
            updateSliderDrag(mouseX);
            return;
        }
        super.mouseClickMove(mouseX, mouseY, clickedMouseButton, timeSinceLastClick);
    }

    private void updateSliderDrag(int mouseX) {
        if (draggingSlider < 0 || draggingSlider >= presetRows.size()) {
            return;
        }
        int sx = sliderX();
        int sw = sliderW();
        float rel = MathHelper.clamp((mouseX - sx) / (float) sw, 0.0f, 1.0f);
        presetRows.get(draggingSlider).chance = Math.round(rel * 100.0f);
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        super.mouseReleased(mouseX, mouseY, state);
        if (state == 1) {
            panning = false;
        }
        if (state == 0) {
            draggingScrollbar = false;
            draggingSlider = -1;
            if (dragging) {
                dragging = false;
                int cx = worldXFromMouse(mouseX);
                int cz = worldZFromMouse(mouseY);
                selMinX = Math.min(startWorldX, cx);
                selMaxX = Math.max(startWorldX, cx);
                selMinZ = Math.min(startWorldZ, cz);
                selMaxZ = Math.max(startWorldZ, cz);
                hasSelection = true;
                updateButtonState();
            }
        }
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if (this.nameField.textboxKeyTyped(typedChar, keyCode)) {
            return;
        }
        if (this.colorField.textboxKeyTyped(typedChar, keyCode)) {
            return;
        }
        if (this.respawnField.textboxKeyTyped(typedChar, keyCode)) {
            return;
        }
        super.keyTyped(typedChar, keyCode);
    }

    @Override
    public void updateScreen() {
        super.updateScreen();
        if (this.nameField != null) this.nameField.updateCursorCounter();
        if (this.colorField != null) this.colorField.updateCursorCounter();
        if (this.respawnField != null) this.respawnField.updateCursorCounter();
        refreshContainers();
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        if (button.id == 2) {
            ModularcontentsMod.PACKET_HANDLER.sendToServer(new PacketOpenCreator());
            return;
        }

        if (!hasSelection || !isSelectionValid()) {
            return;
        }

        if (button.id == 0) {
            List<PacketZoneLoot.PresetEntry> selections = new ArrayList<>();
            for (PresetRow row : presetRows) {
                if (row.enabled) {
                    selections.add(new PacketZoneLoot.PresetEntry(row.name, row.chance));
                }
            }
            if (selections.isEmpty()) {
                return;
            }

            String name = this.nameField.getText();
            int color = 0x88FFAA00;
            try {
                color = (int) Long.parseLong(this.colorField.getText(), 16);
            } catch (NumberFormatException ignored) {}

            int respawnTicks = 6000;
            try {
                respawnTicks = Integer.parseInt(this.respawnField.getText());
            } catch (NumberFormatException ignored) {}

            ModularcontentsMod.PACKET_HANDLER.sendToServer(
                    new PacketZoneLoot(selMinX, selMinZ, selMaxX, selMaxZ, false, true, name, respawnTicks, color, this.editId, selections));
        } else if (button.id == 1) {
            ModularcontentsMod.PACKET_HANDLER.sendToServer(
                    new PacketZoneLoot(selMinX, selMinZ, selMaxX, selMaxZ, true, false, null, 0, 0, null, new ArrayList<>()));
        }
    }

    @Override
    public void onGuiClosed() {
        super.onGuiClosed();
        Keyboard.enableRepeatEvents(false);
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }
}
