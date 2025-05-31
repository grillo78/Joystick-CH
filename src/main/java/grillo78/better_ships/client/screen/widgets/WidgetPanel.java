package grillo78.better_ships.client.screen.widgets;

import grillo78.better_ships.BetterShips;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

public class WidgetPanel extends AbstractWidget {

    private List<AbstractWidget> children = new ArrayList<>();

    public WidgetPanel(int pX, int pY, int pWidth, int pHeight, Component pMessage) {
        super(pX, pY, pWidth, pHeight, pMessage);
    }

    public List<AbstractWidget> getChildren() {
        return children;
    }

    public void tick(){
    }

    public void addRenderableWidget(AbstractWidget pitchButton) {
        children.add(pitchButton);
    }

    @Override
    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
        for (int i = 0; i < children.size(); i++) {
            children.get(i).mouseClicked(pMouseX, pMouseY, pButton);
        }
        return super.mouseClicked(pMouseX, pMouseY, pButton);
    }

    @Override
    public boolean mouseDragged(double pMouseX, double pMouseY, int pButton, double pDragX, double pDragY) {
        for (int i = 0; i < children.size(); i++) {
            children.get(i).mouseDragged(pMouseX, pMouseY, pButton, pDragX, pDragY);
        }
        return super.mouseDragged(pMouseX, pMouseY, pButton, pDragX, pDragY);
    }

    @Override
    public boolean mouseReleased(double pMouseX, double pMouseY, int pButton) {
        for (int i = 0; i < children.size(); i++) {
            children.get(i).mouseReleased(pMouseX, pMouseY, pButton);
        }
        return super.mouseReleased(pMouseX, pMouseY, pButton);
    }

    @Override
    public boolean mouseScrolled(double pMouseX, double pMouseY, double pDelta) {
        for (int i = 0; i < children.size(); i++) {
            children.get(i).mouseScrolled(pMouseX, pMouseY, pDelta);
        }
        return super.mouseScrolled(pMouseX, pMouseY, pDelta);
    }

    @Override
    protected void renderWidget(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        for (int i = 0; i < children.size(); i++) {
            children.get(i).render(pGuiGraphics,pMouseX, pMouseY, pPartialTick);
        }
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput pNarrationElementOutput) {
        for (int i = 0; i < children.size(); i++) {
            children.get(i).updateNarration(pNarrationElementOutput);
        }
    }

    public void resetPanel(){
    }

    public ResourceLocation getBackgroundTexture() {
        return new ResourceLocation(BetterShips.MOD_ID, "textures/gui/joystick_panel_screen.png");
    }
}
