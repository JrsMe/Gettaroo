package getta.gettaroo.gui.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import fi.dy.masa.malilib.gui.GuiBase;
import fi.dy.masa.malilib.gui.GuiScrollBar;
import fi.dy.masa.malilib.gui.GuiTextFieldGeneric;
import fi.dy.masa.malilib.gui.interfaces.ITextFieldListener;
import fi.dy.masa.malilib.gui.widgets.WidgetBase;
import fi.dy.masa.malilib.gui.wrappers.TextFieldWrapper;
import fi.dy.masa.malilib.render.RenderUtils;
import getta.gettaroo.features.CraftingPanelItemOutput;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.registry.Registries;  // Actualizado para 1.21.1
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.*;
import java.util.List;

public class ItemsWidget extends WidgetBase {

    private final TextFieldWrapper<GuiTextFieldGeneric> searchBar;
    private final TextFieldWrapper<GuiTextFieldGeneric> amountSelected;
    protected final GuiScrollBar scrollBar = new GuiScrollBar();
    private final List<Item> itemsWithFilter = new ArrayList<>();
    private final List<CraftingPanelItemOutput> selected = new ArrayList<>();
    private Item selectedItem;
    private boolean shouldFilter = false;
    private int filteredQuantity = 0;
    private SearchingItem searchingItem;

    public ItemsWidget(int x, int y, int width, int height) {
        super(x, y, width, height);

        TextFieldListener listener = new TextFieldListener(this);

        this.searchBar = new TextFieldWrapper<>(new GuiTextFieldGeneric(x + 1, y - 20, (width + x) / 2, 16, this.textRenderer), listener);
        this.amountSelected = new TextFieldWrapper<>(new GuiTextFieldGeneric(x + 3 + this.textRenderer.getWidth("Amount:"), (int) (height * 1.5f - 4), 50, 16, this.textRenderer), listener);

        this.scrollBar.setMaxValue(22);
        this.scrollBar.setValue(0);
    }

    @Override
    public void onMouseReleased(int mouseX, int mouseY, int mouseButton) {
        this.searchBar.getTextField().setFocused(this.searchBar.getTextField().isMouseOver(mouseX, mouseY));
        this.amountSelected.getTextField().setFocused(this.amountSelected.getTextField().isMouseOver(mouseX, mouseY));

        if (this.amountSelected.getTextField().isMouseOver(mouseX, mouseY) && this.amountSelected.getTextField().getText().equals("Fill")) {
            this.amountSelected.getTextField().setEditableColor(Color.WHITE.getRGB());
            this.amountSelected.getTextField().setText("");
        }

        this.scrollBar.setIsDragging(false);
    }

    @Override
    protected boolean onMouseClickedImpl(int mouseX, int mouseY, int mouseButton) {
        if (this.searchingItem != null) {
            this.searchingItem = null;
            this.selectedItem = null;
        }

        this.searchingItem = new SearchingItem(mouseX, mouseY);

        if (this.scrollBar.wasMouseOver()) {
            this.scrollBar.setIsDragging(true);
        }

        return true;
    }

    @Override
    protected boolean onCharTypedImpl(char charIn, int modifiers) {
        if (this.searchBar.getTextField().isFocused()) {
            return this.searchBar.onCharTyped(charIn, modifiers);
        } else if (this.amountSelected.isFocused() && Character.isDigit(charIn)) {
            return this.amountSelected.onCharTyped(charIn, modifiers);
        }

        return false;
    }

    @Override
    protected boolean onKeyTypedImpl(int keyCode, int scanCode, int modifiers) {
        if (this.searchBar.getTextField().isFocused()) {
            return this.searchBar.onKeyTyped(keyCode, scanCode, modifiers);
        } else if (this.amountSelected.isFocused()) {
            return this.amountSelected.onKeyTyped(keyCode, scanCode, modifiers);
        }

        return false;
    }

    public boolean onMouseScrolledImpl(int mouseX, int mouseY, double mouseWheelDelta) {
        if (this.isMouseOver(mouseX, mouseY)) {
            int amount = mouseWheelDelta < 0 ? 1 : -1;
            this.scrollBar.offsetValue(amount);
        }

        return false;
    }

    public void render(int mouseX, int mouseY, boolean selected, MatrixStack matrixStack) {
        RenderUtils.color(1f, 1f, 1f, 1f);
        RenderSystem.pushMatrix();
        RenderSystem.translatef(0, 0, 1);

        Screen screen = this.mc.currentScreen;

        RenderUtils.drawOutlinedBox(this.x, this.y, this.width, this.height, 0xA0000000, GuiBase.COLOR_HORIZONTAL_BAR);
        RenderUtils.drawOutlinedBox(this.width + 15, this.y, screen.width / 6, this.height + 50, 0xA0000000, GuiBase.COLOR_HORIZONTAL_BAR);

        renderSelectedItems(matrixStack);
        renderItems(matrixStack);

        this.searchBar.draw(mouseX, mouseY, matrixStack);
        this.amountSelected.draw(mouseX, mouseY, matrixStack);
        this.textRenderer.drawWithShadow(matrixStack, "Amount:", this.x + 1, this.height * 1.5f, Color.WHITE.getRGB());

        int x = this.x + this.width - this.scrollBar.getWidth() - 1;
        int y = this.y + 1;
        this.scrollBar.render(mouseX, mouseY, 0, x, y, this.scrollBar.getWidth(), this.height - 1, this.height * 4);

        RenderSystem.popMatrix();
    }

    private void renderSelectedItems(MatrixStack matrixStack) {
        if (this.selected != null && !this.selected.isEmpty()) {
            int xLayers = 0;
            int yLayers = 0;

            for (CraftingPanelItemOutput item : this.selected) {
                String identifier = "minecraft:" + item.getName().replaceAll(" ", "_").toLowerCase();
                ItemStack itemStack = Registries.ITEM.get(new Identifier(identifier)).getDefaultStack();  // Actualizado
                int amount = (int) item.getCount();

                this.mc.getItemRenderer().renderInGui(itemStack, this.width + 17 + xLayers, this.y + 7 + yLayers);
                this.textRenderer.drawWithShadow(matrixStack, "x" + amount, this.width + 35 + xLayers, this.y + 12 + yLayers, Color.WHITE.getRGB());
                yLayers += 20;

                if (yLayers + 20 >= this.height + 50) {
                    if (xLayers - 50 >= this.width / 6) {
                        break;
                    }

                    yLayers = 0;
                    xLayers += 40;
                }
            }
        }
    }

    private void renderItems(MatrixStack matrixStack) {
        int xAmount = 0;
        int yAmount = 0;
        int layer = this.scrollBar.getValue();

        int helper = 0;

        List<Item> items = shouldFilter ? this.itemsWithFilter : getFilteredItems();

        for (Item item : items) {
            if (layer > 0 || helper > 0) {
                if (helper == 0) {
                    helper = 17;
                    if (layer != 0) {
                        layer--;
                    }
                }
                if (helper != 0) {
                    helper--;
                }
                continue;
            }
            xAmount += 18;

            this.mc.getItemRenderer().renderInGui(item.getDefaultStack(), this.x - 16 + xAmount, this.y + 2 + yAmount);

            if (this.searchingItem != null && this.selectedItem == null) {
                if (this.x - 16 + xAmount <= this.searchingItem.getMouseX() && this.x - 16 + xAmount + 18 >= this.searchingItem.getMouseX() && this.y + 2 + yAmount <= this.searchingItem.getMouseY() && this.y + 2 + yAmount + 18 >= this.searchingItem.getMouseY()) {
                    this.selectedItem = item;
                }
            }

            if (item.equals(this.selectedItem)) {
                RenderUtils.drawOutline(this.x - 14 + xAmount - 4, this.y + 2 + yAmount - 2, 20, 20, Color.WHITE.getRGB());
            }

            if (xAmount - scrollBar.getWidth() + 25 >= this.width) {
                xAmount = 0;
                yAmount += 20;
            }

            if (yAmount + 18 >= this.height) {
                break;
            }
        }
    }

    private List<Item> getFilteredItems() {
        List<Item> items = new ArrayList<>();

        for (Item item : Registries.ITEM) {  // Actualizado
            if (provideCraftFromItem(item) == null || item.equals(Items.GLASS) || item.equals(Items.SPONGE) || item.getDefaultStack().toString().contains("arrow") || item.getDefaultStack().toString().contains("pattern")) {
                continue;
            }

            items.add(item);
        }

        return items;
    }

    public @NotNull List<CraftingPanelItemOutput> convertSelectionsToResults() {
        return null;
    }

    public void addToList() {
    }

    public void removeLast() {
    }

    public void clearSelected() {

    }

    private static class TextFieldListener implements ITextFieldListener<GuiTextFieldGeneric> {
        protected final ItemsWidget widget;

        protected TextFieldListener(ItemsWidget widget) {
            this.widget = widget;
        }

        @Override
        public boolean onTextChange(GuiTextFieldGeneric textField) {
            this.widget.updateFilteredEntries();
            return true;
        }
    }

    private static class SearchingItem {
        private final int mouseX;
        private final int mouseY;

        public SearchingItem(int mouseX, int mouseY) {
            this.mouseX = mouseX;
            this.mouseY = mouseY;
        }

        public int getMouseX() {
            return mouseX;
        }

        public int getMouseY() {
            return mouseY;
        }
    }
}