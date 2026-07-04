package modularcontents.custom.gui;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import modularcontents.custom.inventory.ContainerContentCreator;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import org.lwjgl.input.Keyboard;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class GuiContentCreator extends GuiContainer {

    private static final int COL_ACCENT = 0xFFFFAA00;
    private static final int COL_BORDER = 0xFF4A4A4A;
    private static final int COL_PANEL = 0xFF151515;
    private static final int COL_TEXT = 0xFFDDDDDD;
    private static final int COL_TEXT_DIM = 0xFF888888;
    private static final int COL_SLOT_BG = 0xFF111111;

    private GuiButton btnTabLoot;
    private GuiButton btnTabItems;
    private GuiButton btnTabRecipes;
    private GuiButton btnGenerate;

    // Shared text fields
    private GuiTextField txtFileName;

    // Loot Table fields
    private GuiTextField txtWeight;
    private GuiTextField txtItemMin;
    private GuiTextField txtItemMax;
    private GuiTextField txtItemChance;

    // Item fields
    private GuiTextField txtItemName;
    private GuiTextField txtMaxStack;
    private GuiTextField txtCreativeTab;
    private GuiTextField txtMaxDamage;

    // Recipe fields
    private GuiTextField txtRecipeCat;
    private GuiTextField txtCraftTime;
    private GuiTextField txtMinDrops;

    // Tabs fields
    private GuiTextField txtTabName;
    private GuiTextField txtTabIcon;

    private GuiButton btnTabTabs;

    private final ContainerContentCreator container;
    private int selectedSlot = -1;
    private final ItemSettings[] slotSettings = new ItemSettings[27];

    private static class ItemSettings {
        double chance = 0.5;
        int min = 1;
        int max = 1;
        boolean customized = false;
    }

    public GuiContentCreator(InventoryPlayer playerInv) {
        super(new ContainerContentCreator(playerInv));
        this.container = (ContainerContentCreator) this.inventorySlots;
        this.xSize = 176;
        this.ySize = 207;
        for (int i = 0; i < 27; i++) {
            slotSettings[i] = new ItemSettings();
        }
    }

    @Override
    public void initGui() {
        super.initGui();
        this.buttonList.clear();

        Keyboard.enableRepeatEvents(true);

        int tabW = 40;
        this.btnTabLoot = new GuiLaptop.FlatButton(0, guiLeft + 5, guiTop + 5, tabW, 20, "Loot");
        this.btnTabItems = new GuiLaptop.FlatButton(1, guiLeft + 47, guiTop + 5, tabW, 20, "Items");
        this.btnTabRecipes = new GuiLaptop.FlatButton(2, guiLeft + 89, guiTop + 5, tabW, 20, "Recipes");
        this.btnTabTabs = new GuiLaptop.FlatButton(4, guiLeft + 131, guiTop + 5, tabW, 20, "Tabs");

        this.btnGenerate = new GuiLaptop.FlatButton(3, guiLeft + 7, guiTop + 112, 162, 12, "Generate JSON");

        this.buttonList.add(btnTabLoot);
        this.buttonList.add(btnTabItems);
        this.buttonList.add(btnTabRecipes);
        this.buttonList.add(btnTabTabs);
        this.buttonList.add(btnGenerate);

        // Common
        this.txtFileName = new GuiTextField(10, fontRenderer, guiLeft + 7, guiTop + 38, 90, 12);
        this.txtFileName.setMaxStringLength(32);
        this.txtFileName.setText("my_file");

        // Loot
        this.txtWeight = new GuiTextField(11, fontRenderer, guiLeft + 135, guiTop + 38, 32, 12);
        this.txtWeight.setMaxStringLength(4);
        this.txtWeight.setText("50");
        this.txtItemMin = new GuiTextField(12, fontRenderer, guiLeft + 185, guiTop + 54, 25, 12);
        this.txtItemMin.setMaxStringLength(3);
        this.txtItemMax = new GuiTextField(13, fontRenderer, guiLeft + 225, guiTop + 54, 25, 12);
        this.txtItemMax.setMaxStringLength(3);
        this.txtItemChance = new GuiTextField(14, fontRenderer, guiLeft + 185, guiTop + 85, 65, 12);
        this.txtItemChance.setMaxStringLength(5);

        // Items
        this.txtItemName = new GuiTextField(15, fontRenderer, guiLeft + 7, guiTop + 65, 160, 12);
        this.txtItemName.setMaxStringLength(64);
        this.txtItemName.setText("My Custom Item");
        this.txtMaxStack = new GuiTextField(16, fontRenderer, guiLeft + 7, guiTop + 90, 40, 12);
        this.txtMaxStack.setMaxStringLength(2);
        this.txtMaxStack.setText("64");
        this.txtCreativeTab = new GuiTextField(17, fontRenderer, guiLeft + 60, guiTop + 90, 60, 12);
        this.txtCreativeTab.setMaxStringLength(32);
        this.txtCreativeTab.setText("misc");
        this.txtMaxDamage = new GuiTextField(18, fontRenderer, guiLeft + 125, guiTop + 90, 42, 12);
        this.txtMaxDamage.setMaxStringLength(5);
        this.txtMaxDamage.setText("0");

        // Recipes
        this.txtRecipeCat = new GuiTextField(19, fontRenderer, guiLeft + 105, guiTop + 38, 62, 12);
        this.txtRecipeCat.setMaxStringLength(32);
        this.txtRecipeCat.setText("general");
        this.txtCraftTime = new GuiTextField(20, fontRenderer, guiLeft + 185, guiTop + 54, 30, 12);
        this.txtCraftTime.setMaxStringLength(5);
        this.txtCraftTime.setText("200");
        this.txtMinDrops = new GuiTextField(21, fontRenderer, guiLeft + 220, guiTop + 54, 30, 12);
        this.txtMinDrops.setMaxStringLength(5);
        this.txtMinDrops.setText("1");

        // Tabs
        this.txtTabName = new GuiTextField(22, fontRenderer, guiLeft + 7, guiTop + 65, 160, 12);
        this.txtTabName.setMaxStringLength(32);
        this.txtTabName.setText("My Custom Tab");
        this.txtTabIcon = new GuiTextField(23, fontRenderer, guiLeft + 7, guiTop + 90, 160, 12);
        this.txtTabIcon.setMaxStringLength(64);
        this.txtTabIcon.setText("minecraft:diamond_sword");

        updateTabState();
        updateSidePanel();
    }

    @Override
    public void onGuiClosed() {
        super.onGuiClosed();
        Keyboard.enableRepeatEvents(false);
    }

    private void updateTabState() {
        btnTabLoot.enabled = container.activeTab != 0;
        btnTabItems.enabled = container.activeTab != 1;
        btnTabRecipes.enabled = container.activeTab != 2;
        btnTabTabs.enabled = container.activeTab != 4;

        boolean isLoot = container.activeTab == 0;
        boolean isItem = container.activeTab == 1;
        boolean isRecp = container.activeTab == 2;
        boolean isTabs = container.activeTab == 4;

        txtFileName.setVisible(true);

        txtWeight.setVisible(isLoot);

        txtItemName.setVisible(isItem);
        txtMaxStack.setVisible(isItem);
        txtCreativeTab.setVisible(isItem);
        txtMaxDamage.setVisible(isItem);

        txtRecipeCat.setVisible(isRecp);

        txtTabName.setVisible(isTabs);
        txtTabIcon.setVisible(isTabs);

        // Hide slots based on tab
        for (int i = 0; i < 27; i++) {
            Slot slot = container.inventorySlots.get(i);
            if (isLoot || isRecp) {
                slot.yPos = 55 + (i / 9) * 18;
            } else {
                slot.yPos = -9999;
            }
        }
        updateSidePanel();
    }

    private void updateSidePanel() {
        boolean showLootPanel = container.activeTab == 0 && selectedSlot != -1;
        txtItemMin.setVisible(showLootPanel);
        txtItemMax.setVisible(showLootPanel);
        txtItemChance.setVisible(showLootPanel);

        if (showLootPanel) {
            ItemSettings set = slotSettings[selectedSlot];
            txtItemChance.setText(String.valueOf(set.chance));
            txtItemMin.setText(String.valueOf(set.min));

            Slot slot = container.inventorySlots.get(selectedSlot);
            if (slot.getHasStack() && !set.customized) {
                txtItemMax.setText(String.valueOf(slot.getStack().getCount()));
            } else {
                txtItemMax.setText(String.valueOf(set.max));
            }
        }

        boolean showRecpPanel = container.activeTab == 2;
        txtCraftTime.setVisible(showRecpPanel);
        txtMinDrops.setVisible(showRecpPanel);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        drawRect(guiLeft, guiTop, guiLeft + xSize, guiTop + ySize, COL_BORDER);
        drawRect(guiLeft + 1, guiTop + 1, guiLeft + xSize - 1, guiTop + ySize - 1, COL_PANEL);
        drawRect(guiLeft, guiTop + 28, guiLeft + xSize, guiTop + 29, COL_BORDER);

        txtFileName.drawTextBox();
        fontRenderer.drawString(container.activeTab == 1 ? "Item ID" : "File Name", guiLeft + 7, guiTop + 29, COL_TEXT_DIM);

        if (container.activeTab == 0) {
            txtWeight.drawTextBox();
            fontRenderer.drawString("Weight", guiLeft + 135, guiTop + 29, COL_TEXT_DIM);

            for (int i = 0; i < 27; ++i) {
                int x = guiLeft + 7 + (i % 9) * 18;
                int y = guiTop + 54 + (i / 9) * 18;
                int bCol = (i == selectedSlot) ? COL_ACCENT : COL_BORDER;
                drawRect(x, y, x + 18, y + 18, bCol);
                drawRect(x + 1, y + 1, x + 17, y + 17, COL_SLOT_BG);
            }

            if (selectedSlot != -1) {
                int px = guiLeft + xSize;
                int py = guiTop + 38;
                drawRect(px, py, px + 85, py + 70, COL_BORDER);
                drawRect(px + 1, py + 1, px + 84, py + 69, COL_PANEL);
                fontRenderer.drawString("Slot " + selectedSlot, px + 5, py + 5, COL_ACCENT);
                fontRenderer.drawString("Min", px + 5, py + 16, COL_TEXT_DIM);
                fontRenderer.drawString("Max", px + 45, py + 16, COL_TEXT_DIM);
                txtItemMin.drawTextBox();
                txtItemMax.drawTextBox();
                fontRenderer.drawString("Chance (0.0-1.0)", px + 5, py + 37, COL_TEXT_DIM);
                txtItemChance.drawTextBox();
            }

        } else if (container.activeTab == 1) {
            fontRenderer.drawString("Display Name", guiLeft + 7, guiTop + 55, COL_TEXT_DIM);
            txtItemName.drawTextBox();
            fontRenderer.drawString("Max Stack", guiLeft + 7, guiTop + 80, COL_TEXT_DIM);
            txtMaxStack.drawTextBox();
            fontRenderer.drawString("Tab", guiLeft + 60, guiTop + 80, COL_TEXT_DIM);
            txtCreativeTab.drawTextBox();
            fontRenderer.drawString("Durability", guiLeft + 125, guiTop + 80, COL_TEXT_DIM);
            txtMaxDamage.drawTextBox();
        } else if (container.activeTab == 2) {
            txtRecipeCat.drawTextBox();
            fontRenderer.drawString("Category", guiLeft + 105, guiTop + 29, COL_TEXT_DIM);

            fontRenderer.drawString("Outputs (Chance based)", guiLeft + 7, guiTop + 45, COL_ACCENT);
            for (int i = 0; i < 9; ++i) {
                int x = guiLeft + 7 + i * 18;
                int y = guiTop + 54;
                drawRect(x, y, x + 18, y + 18, COL_BORDER);
                drawRect(x + 1, y + 1, x + 17, y + 17, 0xFF221111); // Reddish tint for outputs
            }
            fontRenderer.drawString("Inputs (Required)", guiLeft + 7, guiTop + 73, COL_ACCENT);
            for (int i = 9; i < 27; ++i) {
                int x = guiLeft + 7 + (i % 9) * 18;
                int y = guiTop + 54 + (i / 9) * 18;
                drawRect(x, y, x + 18, y + 18, COL_BORDER);
                drawRect(x + 1, y + 1, x + 17, y + 17, COL_SLOT_BG);
            }

            int px = guiLeft + xSize;
            int py = guiTop + 38;
            drawRect(px, py, px + 85, py + 40, COL_BORDER);
            drawRect(px + 1, py + 1, px + 84, py + 39, COL_PANEL);
            fontRenderer.drawString("Craft Ticks", px + 5, py + 5, COL_TEXT_DIM);
            fontRenderer.drawString("Min Drops", px + 40, py + 5, COL_TEXT_DIM);
            txtCraftTime.drawTextBox();
            txtMinDrops.drawTextBox();
        } else if (container.activeTab == 4) {
            fontRenderer.drawString("Display Name", guiLeft + 7, guiTop + 55, COL_TEXT_DIM);
            txtTabName.drawTextBox();
            fontRenderer.drawString("Icon Item (e.g. minecraft:apple)", guiLeft + 7, guiTop + 80, COL_TEXT_DIM);
            txtTabIcon.drawTextBox();
        }
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {}

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        txtFileName.mouseClicked(mouseX, mouseY, mouseButton);

        if (container.activeTab == 0) {
            txtWeight.mouseClicked(mouseX, mouseY, mouseButton);
            if (selectedSlot != -1) {
                txtItemMin.mouseClicked(mouseX, mouseY, mouseButton);
                txtItemMax.mouseClicked(mouseX, mouseY, mouseButton);
                txtItemChance.mouseClicked(mouseX, mouseY, mouseButton);
            }
            for (int i = 0; i < 27; i++) {
                int sx = guiLeft + 7 + (i % 9) * 18;
                int sy = guiTop + 54 + (i / 9) * 18;
                if (mouseX >= sx && mouseX < sx + 18 && mouseY >= sy && mouseY < sy + 18) {
                    if (selectedSlot != i) {
                        selectedSlot = i;
                        updateSidePanel();
                    }
                    break;
                }
            }
        } else if (container.activeTab == 1) {
            txtItemName.mouseClicked(mouseX, mouseY, mouseButton);
            txtMaxStack.mouseClicked(mouseX, mouseY, mouseButton);
            txtCreativeTab.mouseClicked(mouseX, mouseY, mouseButton);
            txtMaxDamage.mouseClicked(mouseX, mouseY, mouseButton);
        } else if (container.activeTab == 2) {
            txtRecipeCat.mouseClicked(mouseX, mouseY, mouseButton);
            txtCraftTime.mouseClicked(mouseX, mouseY, mouseButton);
            txtMinDrops.mouseClicked(mouseX, mouseY, mouseButton);
        } else if (container.activeTab == 4) {
            txtTabName.mouseClicked(mouseX, mouseY, mouseButton);
            txtTabIcon.mouseClicked(mouseX, mouseY, mouseButton);
        }
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if (txtFileName.textboxKeyTyped(typedChar, keyCode)) return;

        if (container.activeTab == 0) {
            if (txtWeight.textboxKeyTyped(typedChar, keyCode)) return;
            if (selectedSlot != -1) {
                boolean changed = false;
                if (txtItemMin.textboxKeyTyped(typedChar, keyCode)) changed = true;
                else if (txtItemMax.textboxKeyTyped(typedChar, keyCode)) changed = true;
                else if (txtItemChance.textboxKeyTyped(typedChar, keyCode)) changed = true;

                if (changed) {
                    ItemSettings set = slotSettings[selectedSlot];
                    set.customized = true;
                    try { set.min = Integer.parseInt(txtItemMin.getText()); } catch (Exception ignored) {}
                    try { set.max = Integer.parseInt(txtItemMax.getText()); } catch (Exception ignored) {}
                    try { set.chance = Double.parseDouble(txtItemChance.getText()); } catch (Exception ignored) {}
                    return;
                }
            }
        } else if (container.activeTab == 1) {
            if (txtItemName.textboxKeyTyped(typedChar, keyCode)) return;
            if (txtMaxStack.textboxKeyTyped(typedChar, keyCode)) return;
            if (txtCreativeTab.textboxKeyTyped(typedChar, keyCode)) return;
            if (txtMaxDamage.textboxKeyTyped(typedChar, keyCode)) return;
        } else if (container.activeTab == 2) {
            if (txtRecipeCat.textboxKeyTyped(typedChar, keyCode)) return;
            if (txtCraftTime.textboxKeyTyped(typedChar, keyCode)) return;
            if (txtMinDrops.textboxKeyTyped(typedChar, keyCode)) return;
        } else if (container.activeTab == 4) {
            if (txtTabName.textboxKeyTyped(typedChar, keyCode)) return;
            if (txtTabIcon.textboxKeyTyped(typedChar, keyCode)) return;
        }
        super.keyTyped(typedChar, keyCode);
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        if (button.id == 0 || button.id == 1 || button.id == 2 || button.id == 4) {
            container.activeTab = button.id;
            updateTabState();
        } else if (button.id == 3) {
            if (container.activeTab == 0) generateLootTable();
            else if (container.activeTab == 1) generateItem();
            else if (container.activeTab == 2) generateRecipe();
            else if (container.activeTab == 4) generateTab();
        }
    }

    private void generateLootTable() {
        String fileName = txtFileName.getText().trim();
        if (fileName.isEmpty()) fileName = "custom_loot";
        if (!fileName.endsWith(".json")) fileName += ".json";

        int weight = 10;
        try { weight = Integer.parseInt(txtWeight.getText()); } catch (Exception ignored) {}

        JsonArray itemsArray = new JsonArray();
        for (int i = 0; i < 27; i++) {
            Slot slot = container.inventorySlots.get(i);
            if (slot != null && slot.getHasStack()) {
                ItemStack stack = slot.getStack();
                ItemSettings set = slotSettings[i];

                int finalMax = set.customized ? set.max : stack.getCount();
                int finalMin = set.min;
                if (finalMax < finalMin) finalMax = finalMin;

                JsonObject itemObj = new JsonObject();
                itemObj.addProperty("item", stack.getItem().getRegistryName().toString());
                if (stack.getMetadata() > 0) itemObj.addProperty("meta", stack.getMetadata());
                itemObj.addProperty("min", finalMin);
                itemObj.addProperty("max", finalMax);
                itemObj.addProperty("chance", set.chance);
                itemsArray.add(itemObj);
            }
        }

        if (itemsArray.size() == 0) {
            mc.player.sendMessage(new TextComponentString(TextFormatting.RED + "Cannot generate empty loot table!"));
            return;
        }

        JsonObject root = new JsonObject();
        root.addProperty("weight", weight);
        root.add("items", itemsArray);
        saveJsonFile(root, "loot_tables/airdrops", fileName);
    }

    private void generateItem() {
        String id = txtFileName.getText().trim();
        if (id.isEmpty()) id = "custom_item";
        String fileName = id;
        if (!fileName.endsWith(".json")) fileName += ".json";
        else id = id.substring(0, id.length() - 5); // remove .json for id

        JsonObject root = new JsonObject();
        root.addProperty("id", id);
        root.addProperty("display_name", txtItemName.getText().trim());

        try { root.addProperty("max_stack_size", Integer.parseInt(txtMaxStack.getText())); }
        catch (Exception e) { root.addProperty("max_stack_size", 64); }

        root.addProperty("creative_tab", txtCreativeTab.getText().trim());

        try { root.addProperty("max_damage", Integer.parseInt(txtMaxDamage.getText())); }
        catch (Exception e) { root.addProperty("max_damage", 0); }

        saveJsonFile(root, "items", fileName);
    }

    private void generateRecipe() {
        String fileName = txtFileName.getText().trim();
        if (fileName.isEmpty()) fileName = "custom_recipe";
        String id = fileName;
        if (!fileName.endsWith(".json")) fileName += ".json";
        else id = id.substring(0, id.length() - 5);

        JsonObject root = new JsonObject();
        root.addProperty("id", id);
        root.addProperty("category", txtRecipeCat.getText().trim());

        JsonArray outputsArray = new JsonArray();
        for (int i = 0; i < 9; i++) {
            Slot slot = container.inventorySlots.get(i);
            if (slot != null && slot.getHasStack()) {
                ItemStack stack = slot.getStack();
                JsonObject itemObj = new JsonObject();
                itemObj.addProperty("item", stack.getItem().getRegistryName().toString());
                itemObj.addProperty("count", stack.getCount());
                if (stack.getMetadata() > 0) itemObj.addProperty("meta", stack.getMetadata());
                itemObj.addProperty("chance", 100.0); // Simple default chance for UI
                outputsArray.add(itemObj);
            }
        }

        JsonArray inputsArray = new JsonArray();
        for (int i = 9; i < 27; i++) {
            Slot slot = container.inventorySlots.get(i);
            if (slot != null && slot.getHasStack()) {
                ItemStack stack = slot.getStack();
                JsonObject itemObj = new JsonObject();
                itemObj.addProperty("item", stack.getItem().getRegistryName().toString());
                itemObj.addProperty("count", stack.getCount());
                if (stack.getMetadata() > 0) itemObj.addProperty("meta", stack.getMetadata());
                inputsArray.add(itemObj);
            }
        }

        if (inputsArray.size() == 0 || outputsArray.size() == 0) {
            mc.player.sendMessage(new TextComponentString(TextFormatting.RED + "Recipe must have at least 1 input and 1 output!"));
            return;
        }

        root.add("outputs", outputsArray);
        root.add("inputs", inputsArray);

        try { root.addProperty("craftingTime", Integer.parseInt(txtCraftTime.getText())); }
        catch (Exception e) { root.addProperty("craftingTime", 200); }

        try { root.addProperty("minDrops", Integer.parseInt(txtMinDrops.getText())); }
        catch (Exception e) { root.addProperty("minDrops", 1); }

        saveJsonFile(root, "recipes", fileName);
    }

    private void generateTab() {
        String id = txtFileName.getText().trim();
        if (id.isEmpty()) id = "custom_tab";
        String fileName = id;
        if (!fileName.endsWith(".json")) fileName += ".json";
        else id = id.substring(0, id.length() - 5);

        JsonObject root = new JsonObject();
        root.addProperty("id", id);
        root.addProperty("display_name", txtTabName.getText().trim());
        root.addProperty("icon", txtTabIcon.getText().trim());

        saveJsonFile(root, "tabs", fileName);
    }

    private void saveJsonFile(JsonObject root, String subDir, String fileName) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String json = gson.toJson(root);

        File gameDir = mc.mcDataDir;
        File genDir = new File(gameDir, "ModularContents/generated/" + subDir);
        genDir.mkdirs();
        File outFile = new File(genDir, fileName);

        try (FileWriter writer = new FileWriter(outFile)) {
            writer.write(json);
            mc.player.sendMessage(new TextComponentString(TextFormatting.GREEN + "Generated JSON: " + outFile.getAbsolutePath()));
        } catch (IOException e) {
            mc.player.sendMessage(new TextComponentString(TextFormatting.RED + "Failed to write JSON: " + e.getMessage()));
        }
    }
}