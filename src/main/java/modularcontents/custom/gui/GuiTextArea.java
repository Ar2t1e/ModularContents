package modularcontents.custom.gui;

import modularcontents.custom.client.GuiTheme;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import org.lwjgl.input.Keyboard;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GuiTextArea extends Gui {


    private static final int PAD = 4;
    private static final int SCROLLBAR_W = 5;

    private final FontRenderer fontRenderer;
    private final List<String> lines = new ArrayList<>();

    public int x;
    public int y;
    public int width;
    public int height;

    private int cursorLine;
    private int cursorColumn;
    private int cursorCounter;
    private int scrollLine;
    private int scrollColumnPx;
    private boolean focused;
    private boolean draggingThumb;
    private boolean changed;

    public GuiTextArea(FontRenderer fontRenderer, int x, int y, int width, int height) {
        this.fontRenderer = fontRenderer;
        setBounds(x, y, width, height);
        lines.add("");
    }

    public void setBounds(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        clampScroll();
    }

    private int lineHeight() {
        return fontRenderer.FONT_HEIGHT + 1;
    }

    private int visibleLines() {
        return Math.max(1, (height - PAD * 2) / lineHeight());
    }

    private int textWidth() {
        return Math.max(10, width - PAD * 2 - SCROLLBAR_W);
    }

    public void setText(String text) {
        lines.clear();
        lines.addAll(Arrays.asList((text == null ? "" : text).replace("\r\n", "\n").replace("\r", "\n").split("\n", -1)));
        if (lines.isEmpty()) lines.add("");
        cursorLine = 0;
        cursorColumn = 0;
        scrollLine = 0;
        scrollColumnPx = 0;
        changed = false;
    }

    public String getText() {
        return String.join("\n", lines);
    }

    public boolean isFocused() {
        return focused;
    }

    public void setFocused(boolean focused) {
        this.focused = focused;
        if (focused) cursorCounter = 0;
    }

    public boolean consumeChanged() {
        boolean value = changed;
        changed = false;
        return value;
    }

    public void updateCursorCounter() {
        cursorCounter++;
    }

    public void draw(int mouseX, int mouseY) {
        drawRect(x, y, x + width, y + height, focused ? GuiTheme.ACCENT : GuiTheme.BORDER);
        drawRect(x + 1, y + 1, x + width - 1, y + height - 1, GuiTheme.EDITOR_BG);

        int visible = visibleLines();
        int lh = lineHeight();
        int textLeft = x + PAD;
        int textTop = y + PAD;
        int clipRight = textLeft + textWidth();

        for (int i = 0; i < visible; i++) {
            int index = scrollLine + i;
            if (index >= lines.size()) break;
            String line = lines.get(index);
            String visiblePart = clipLine(line);
            fontRenderer.drawString(visiblePart, textLeft, textTop + i * lh, GuiTheme.TEXT);
        }

        if (focused && cursorCounter / 6 % 2 == 0) {
            int row = cursorLine - scrollLine;
            if (row >= 0 && row < visible) {
                String prefix = lines.get(cursorLine).substring(0, cursorColumn);
                int cx = textLeft + fontRenderer.getStringWidth(prefix) - scrollColumnPx;
                if (cx >= textLeft - 1 && cx <= clipRight) {
                    drawRect(cx, textTop + row * lh - 1, cx + 1, textTop + row * lh + lh - 1, GuiTheme.ACCENT);
                }
            }
        }

        drawScrollBar();
    }

    private String clipLine(String line) {
        if (scrollColumnPx <= 0) return fontRenderer.trimStringToWidth(line, textWidth());
        int start = 0;
        while (start < line.length() && fontRenderer.getStringWidth(line.substring(0, start)) < scrollColumnPx) {
            start++;
        }
        return fontRenderer.trimStringToWidth(line.substring(start), textWidth());
    }

    private void drawScrollBar() {
        int visible = visibleLines();
        if (lines.size() <= visible) return;

        int trackX = x + width - SCROLLBAR_W - 2;
        int trackTop = y + 2;
        int trackBottom = y + height - 2;
        int trackHeight = trackBottom - trackTop;

        drawRect(trackX, trackTop, trackX + SCROLLBAR_W, trackBottom, GuiTheme.SCROLL_TRACK);

        int thumbHeight = Math.max(10, trackHeight * visible / lines.size());
        int maxScroll = lines.size() - visible;
        int thumbTop = trackTop + (trackHeight - thumbHeight) * scrollLine / Math.max(1, maxScroll);
        drawRect(trackX, thumbTop, trackX + SCROLLBAR_W, thumbTop + thumbHeight, GuiTheme.SCROLL_THUMB);
    }

    public boolean isInside(int mouseX, int mouseY) {
        return mouseX >= x && mouseX < x + width && mouseY >= y && mouseY < y + height;
    }

    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (mouseButton != 0) return;
        if (!isInside(mouseX, mouseY)) {
            setFocused(false);
            return;
        }
        setFocused(true);

        int trackX = x + width - SCROLLBAR_W - 2;
        if (mouseX >= trackX && lines.size() > visibleLines()) {
            draggingThumb = true;
            dragScrollTo(mouseY);
            return;
        }

        int row = (mouseY - y - PAD) / lineHeight();
        cursorLine = Math.max(0, Math.min(lines.size() - 1, scrollLine + row));

        String line = lines.get(cursorLine);
        int targetPx = mouseX - (x + PAD) + scrollColumnPx;
        int column = 0;
        while (column < line.length() && fontRenderer.getStringWidth(line.substring(0, column + 1)) <= targetPx) {
            column++;
        }
        cursorColumn = column;
        cursorCounter = 0;
        scrollToCursor();
    }

    public void mouseReleased() {
        draggingThumb = false;
    }

    public void mouseDragged(int mouseY) {
        if (draggingThumb) dragScrollTo(mouseY);
    }

    private void dragScrollTo(int mouseY) {
        int visible = visibleLines();
        int maxScroll = Math.max(0, lines.size() - visible);
        if (maxScroll == 0) return;
        float fraction = (float) (mouseY - y - 2) / Math.max(1, height - 4);
        scrollLine = Math.max(0, Math.min(maxScroll, Math.round(fraction * maxScroll)));
    }

    public void scroll(int notches) {
        scrollLine -= notches * 3;
        clampScroll();
    }

    private void clampScroll() {
        int maxScroll = Math.max(0, lines.size() - visibleLines());
        scrollLine = Math.max(0, Math.min(maxScroll, scrollLine));
    }

    private void scrollToCursor() {
        int visible = visibleLines();
        if (cursorLine < scrollLine) scrollLine = cursorLine;
        else if (cursorLine >= scrollLine + visible) scrollLine = cursorLine - visible + 1;
        clampScroll();

        int cursorPx = fontRenderer.getStringWidth(lines.get(cursorLine).substring(0, cursorColumn));
        if (cursorPx < scrollColumnPx) scrollColumnPx = Math.max(0, cursorPx - 20);
        else if (cursorPx > scrollColumnPx + textWidth() - 6) scrollColumnPx = cursorPx - textWidth() + 6;
    }

    public boolean keyTyped(char typedChar, int keyCode) {
        if (!focused) return false;

        if (GuiScreen.isKeyComboCtrlV(keyCode)) {
            insert(GuiScreen.getClipboardString());
            return true;
        }
        if (GuiScreen.isKeyComboCtrlC(keyCode)) {
            GuiScreen.setClipboardString(getText());
            return true;
        }

        switch (keyCode) {
            case Keyboard.KEY_LEFT:
                moveLeft();
                return true;
            case Keyboard.KEY_RIGHT:
                moveRight();
                return true;
            case Keyboard.KEY_UP:
                if (cursorLine > 0) {
                    cursorLine--;
                    cursorColumn = Math.min(cursorColumn, lines.get(cursorLine).length());
                }
                cursorCounter = 0;
                scrollToCursor();
                return true;
            case Keyboard.KEY_DOWN:
                if (cursorLine < lines.size() - 1) {
                    cursorLine++;
                    cursorColumn = Math.min(cursorColumn, lines.get(cursorLine).length());
                }
                cursorCounter = 0;
                scrollToCursor();
                return true;
            case Keyboard.KEY_HOME:
                cursorColumn = 0;
                scrollToCursor();
                return true;
            case Keyboard.KEY_END:
                cursorColumn = lines.get(cursorLine).length();
                scrollToCursor();
                return true;
            case Keyboard.KEY_PRIOR:
                cursorLine = Math.max(0, cursorLine - visibleLines());
                cursorColumn = Math.min(cursorColumn, lines.get(cursorLine).length());
                scrollToCursor();
                return true;
            case Keyboard.KEY_NEXT:
                cursorLine = Math.min(lines.size() - 1, cursorLine + visibleLines());
                cursorColumn = Math.min(cursorColumn, lines.get(cursorLine).length());
                scrollToCursor();
                return true;
            case Keyboard.KEY_BACK:
                backspace();
                return true;
            case Keyboard.KEY_DELETE:
                delete();
                return true;
            case Keyboard.KEY_RETURN:
            case Keyboard.KEY_NUMPADENTER:
                insert("\n");
                return true;
            case Keyboard.KEY_TAB:
                insert("  ");
                return true;
            default:
                break;
        }

        if (typedChar >= 32 && typedChar != 127) {
            insert(String.valueOf(typedChar));
            return true;
        }
        return false;
    }

    private void moveLeft() {
        if (cursorColumn > 0) {
            cursorColumn--;
        } else if (cursorLine > 0) {
            cursorLine--;
            cursorColumn = lines.get(cursorLine).length();
        }
        cursorCounter = 0;
        scrollToCursor();
    }

    private void moveRight() {
        if (cursorColumn < lines.get(cursorLine).length()) {
            cursorColumn++;
        } else if (cursorLine < lines.size() - 1) {
            cursorLine++;
            cursorColumn = 0;
        }
        cursorCounter = 0;
        scrollToCursor();
    }

    private void insert(String text) {
        if (text == null || text.isEmpty()) return;
        String normalized = text.replace("\r\n", "\n").replace("\r", "\n").replace("\t", "  ");
        String line = lines.get(cursorLine);
        String head = line.substring(0, cursorColumn);
        String tail = line.substring(cursorColumn);

        String[] parts = normalized.split("\n", -1);
        if (parts.length == 1) {
            lines.set(cursorLine, head + parts[0] + tail);
            cursorColumn += parts[0].length();
        } else {
            lines.set(cursorLine, head + parts[0]);
            for (int i = 1; i < parts.length; i++) {
                lines.add(cursorLine + i, parts[i]);
            }
            cursorLine += parts.length - 1;
            cursorColumn = parts[parts.length - 1].length();
            lines.set(cursorLine, lines.get(cursorLine) + tail);
        }
        markChanged();
    }

    private void backspace() {
        String line = lines.get(cursorLine);
        if (cursorColumn > 0) {
            lines.set(cursorLine, line.substring(0, cursorColumn - 1) + line.substring(cursorColumn));
            cursorColumn--;
        } else if (cursorLine > 0) {
            String previous = lines.get(cursorLine - 1);
            lines.set(cursorLine - 1, previous + line);
            lines.remove(cursorLine);
            cursorLine--;
            cursorColumn = previous.length();
        } else {
            return;
        }
        markChanged();
    }

    private void delete() {
        String line = lines.get(cursorLine);
        if (cursorColumn < line.length()) {
            lines.set(cursorLine, line.substring(0, cursorColumn) + line.substring(cursorColumn + 1));
        } else if (cursorLine < lines.size() - 1) {
            lines.set(cursorLine, line + lines.get(cursorLine + 1));
            lines.remove(cursorLine + 1);
        } else {
            return;
        }
        markChanged();
    }

    private void markChanged() {
        changed = true;
        cursorCounter = 0;
        scrollToCursor();
    }
}
