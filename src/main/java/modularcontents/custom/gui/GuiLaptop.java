package modularcontents.custom.gui;

import modularcontents.ModularcontentsMod;
import modularcontents.custom.network.PacketLaptopAirdrop;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.lwjgl.input.Mouse;

import java.io.IOException;

public class GuiLaptop extends GuiScreen {

    // Style colors
    private static final int COL_ACCENT = 0xFFFFAA00;
    private static final int COL_BORDER = 0xFF4A4A4A;
    private static final int COL_PANEL = 0xFF151515;
    private static final int COL_TEXT = 0xFFDDDDDD;
    private static final int COL_TEXT_DIM = 0xFF888888;

    private final BlockPos laptopPos;
    private final World world;
    private DynamicTexture minimapTexture;
    private int textureId = -1;

    private int selectedX = 0;
    private int selectedZ = 0;
    private boolean hasSelection = false;
    private FlatButton callButton;

    // Minimap specs
    private final int TEX_SIZE = 384;
    private final int mapRadius = TEX_SIZE / 2; // 192 blocks
    private final int DISPLAY_SIZE = 160; // smaller map display

    private float currentZoom = 1.0f;
    private float targetZoom = 1.0f;

    public GuiLaptop(World world, BlockPos pos) {
        this.world = world;
        this.laptopPos = pos;
    }

    @Override
    public void initGui() {
        super.initGui();
        this.buttonList.clear();

        int panelWidth = DISPLAY_SIZE + 20;
        int panelHeight = DISPLAY_SIZE + 60; // 20 pad top, 20 map, 40 bottom for button
        int startX = (this.width - panelWidth) / 2;
        int startY = (this.height - panelHeight) / 2;

        this.callButton = new FlatButton(0, startX + 10, startY + DISPLAY_SIZE + 25, DISPLAY_SIZE, 24, "Call Airdrop");
        this.callButton.enabled = false;
        this.buttonList.add(this.callButton);

        // Generate minimap texture
        if (this.textureId == -1) {
            generateMinimap();
        }
    }

    private void generateMinimap() {
        minimapTexture = new DynamicTexture(TEX_SIZE, TEX_SIZE);
        int[] pixels = minimapTexture.getTextureData();

        for (int i = 0; i < TEX_SIZE; i++) {
            for (int j = 0; j < TEX_SIZE; j++) {
                int wx = laptopPos.getX() - mapRadius + i;
                int wz = laptopPos.getZ() - mapRadius + j;

                int y = world.getHeight(wx, wz);
                BlockPos p = new BlockPos(wx, y > 0 ? y - 1 : 0, wz);
                int color = world.getBlockState(p).getMapColor(world, p).colorValue;

                // Make grid overlay (every 32 blocks)
                if (wx % 32 == 0 || wz % 32 == 0) {
                    color = 0x00FF00; // Bright green for radar grid
                }

                pixels[j * TEX_SIZE + i] = 0xFF000000 | color;
            }
        }
        minimapTexture.updateDynamicTexture();
        this.textureId = minimapTexture.getGlTextureId();
    }

    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        int scroll = Mouse.getEventDWheel();
        if (scroll != 0) {
            if (scroll > 0) {
                targetZoom = Math.min(4.0f, targetZoom + 0.5f);
            } else {
                targetZoom = Math.max(1.0f, targetZoom - 0.5f);
            }
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();

        // Smooth zoom interpolation
        currentZoom += (targetZoom - currentZoom) * 0.2f;

        int panelWidth = DISPLAY_SIZE + 20;
        int panelHeight = DISPLAY_SIZE + 60;
        int startX = (this.width - panelWidth) / 2;
        int startY = (this.height - panelHeight) / 2;

        // Draw Panel Background
        drawRect(startX, startY, startX + panelWidth, startY + panelHeight, COL_BORDER);
        drawRect(startX + 1, startY + 1, startX + panelWidth - 1, startY + panelHeight - 1, COL_PANEL);

        this.drawCenteredString(this.fontRenderer, "Airdrop Tactical Map", this.width / 2, startY + 6, COL_ACCENT);

        int mapX = startX + 10;
        int mapY = startY + 20;

        // Draw Map Border
        drawRect(mapX - 1, mapY - 1, mapX + DISPLAY_SIZE + 1, mapY + DISPLAY_SIZE + 1, COL_BORDER);
        drawRect(mapX, mapY, mapX + DISPLAY_SIZE, mapY + DISPLAY_SIZE, 0xFF111111);

        // Draw Map Texture with Zoom
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        if (this.textureId != -1) {
            GlStateManager.bindTexture(this.textureId);

            float visibleTexSize = TEX_SIZE / currentZoom;
            float texU = (TEX_SIZE - visibleTexSize) / 2.0f;
            float texV = (TEX_SIZE - visibleTexSize) / 2.0f;

            GuiScreen.drawScaledCustomSizeModalRect(mapX, mapY, texU, texV, (int)visibleTexSize, (int)visibleTexSize, DISPLAY_SIZE, DISPLAY_SIZE, TEX_SIZE, TEX_SIZE);
        }

        float visibleTexSize = TEX_SIZE / currentZoom;
        float texU = (TEX_SIZE - visibleTexSize) / 2.0f;
        float texV = (TEX_SIZE - visibleTexSize) / 2.0f;

        // Draw Player Location (Center)
        int centerX = mapX + DISPLAY_SIZE / 2;
        int centerY = mapY + DISPLAY_SIZE / 2;
        drawRect(centerX - 1, centerY - 1, centerX + 2, centerY + 2, 0xFFFF5555); // Red dot

        // Draw Selection
        if (hasSelection) {
            float selRelX = (selectedX - (laptopPos.getX() - mapRadius) - texU) / visibleTexSize;
            float selRelY = (selectedZ - (laptopPos.getZ() - mapRadius) - texV) / visibleTexSize;

            if (selRelX >= 0 && selRelX <= 1.0f && selRelY >= 0 && selRelY <= 1.0f) {
                int px = mapX + (int)(selRelX * DISPLAY_SIZE);
                int py = mapY + (int)(selRelY * DISPLAY_SIZE);
                drawRect(px - 3, py - 1, px + 4, py + 2, COL_ACCENT);
                drawRect(px - 1, py - 3, px + 2, py + 4, COL_ACCENT);
            }
        }

        super.drawScreen(mouseX, mouseY, partialTicks);

        // Draw Hover Coordinates Preview
        if (mouseX >= mapX && mouseX < mapX + DISPLAY_SIZE && mouseY >= mapY && mouseY < mapY + DISPLAY_SIZE) {
            float relX = (mouseX - mapX) / (float)DISPLAY_SIZE;
            float relY = (mouseY - mapY) / (float)DISPLAY_SIZE;

            float hoverTexX = texU + relX * visibleTexSize;
            float hoverTexY = texV + relY * visibleTexSize;

            int hoverBlockX = laptopPos.getX() - mapRadius + (int)hoverTexX;
            int hoverBlockZ = laptopPos.getZ() - mapRadius + (int)hoverTexY;

            String hoverStr = "X: " + hoverBlockX + " Z: " + hoverBlockZ;
            int strW = this.fontRenderer.getStringWidth(hoverStr);

            // Draw a small background for the tooltip
            drawRect(mouseX + 8, mouseY - 14, mouseX + 12 + strW, mouseY - 2, 0xCC000000);
            this.fontRenderer.drawString(hoverStr, mouseX + 10, mouseY - 12, COL_ACCENT);
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);

        int panelWidth = DISPLAY_SIZE + 20;
        int panelHeight = DISPLAY_SIZE + 60;
        int startX = (this.width - panelWidth) / 2;
        int startY = (this.height - panelHeight) / 2;

        int mapX = startX + 10;
        int mapY = startY + 20;

        if (mouseX >= mapX && mouseX < mapX + DISPLAY_SIZE && mouseY >= mapY && mouseY < mapY + DISPLAY_SIZE) {
            float visibleTexSize = TEX_SIZE / currentZoom;
            float texU = (TEX_SIZE - visibleTexSize) / 2.0f;
            float texV = (TEX_SIZE - visibleTexSize) / 2.0f;

            float relX = (mouseX - mapX) / (float)DISPLAY_SIZE;
            float relY = (mouseY - mapY) / (float)DISPLAY_SIZE;

            float clickTexX = texU + relX * visibleTexSize;
            float clickTexY = texV + relY * visibleTexSize;

            this.selectedX = laptopPos.getX() - mapRadius + (int)clickTexX;
            this.selectedZ = laptopPos.getZ() - mapRadius + (int)clickTexY;

            this.hasSelection = true;
            this.callButton.enabled = true;
            this.callButton.displayString = "Call: X=" + this.selectedX + " Z=" + this.selectedZ;
        }
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        if (button.id == 0 && hasSelection) {
            ModularcontentsMod.PACKET_HANDLER.sendToServer(new PacketLaptopAirdrop(laptopPos, selectedX, selectedZ));
            this.mc.displayGuiScreen(null);
        }
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    public static class FlatButton extends GuiButton {
        public FlatButton(int buttonId, int x, int y, int widthIn, int heightIn, String buttonText) {
            super(buttonId, x, y, widthIn, heightIn, buttonText);
        }

        @Override
        public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
            if (this.visible) {
                this.hovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;

                int borderColor = this.enabled ? (this.hovered ? COL_ACCENT : COL_BORDER) : 0xFF222222;
                int bgColor = this.enabled ? (this.hovered ? 0xFF2A2A11 : 0xFF111111) : 0xFF111111;
                int textColor = this.enabled ? (this.hovered ? COL_ACCENT : COL_TEXT) : 0xFF555555;

                drawRect(this.x, this.y, this.x + this.width, this.y + this.height, borderColor);
                drawRect(this.x + 1, this.y + 1, this.x + this.width - 1, this.y + this.height - 1, bgColor);

                this.drawCenteredString(mc.fontRenderer, this.displayString, this.x + this.width / 2, this.y + (this.height - 8) / 2, textColor);
            }
        }
    }
}