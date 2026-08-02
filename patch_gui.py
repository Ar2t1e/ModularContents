import os
import re

files = [
    'src/main/java/modularcontents/custom/gui/GuiListWorkbench.java',
    'src/main/java/modularcontents/custom/client/gui/GuiHandcraft.java'
]

for filepath in files:
    with open(filepath, 'r', encoding='utf-8') as f:
        content = f.read()

    # 1. In drawListRecipes
    content = re.sub(
        r'(this\.itemRender\.renderItemAndEffectIntoGUI\(result, \(int\) \(\(guiLeft \+ 10\) / 0\.75f\), \(int\) \(\(rowY \+ 3\) / 0\.75f\)\);)',
        r'\1\n                this.itemRender.renderItemOverlayIntoGUI(this.fontRenderer, result, (int) ((guiLeft + 10) / 0.75f), (int) ((rowY + 3) / 0.75f), null);',
        content
    )

    # 2. In drawGridRecipes
    content = re.sub(
        r'(this\.itemRender\.renderItemAndEffectIntoGUI\(result, cx \+ 2, cy \+ 2\);)',
        r'\1\n            this.itemRender.renderItemOverlayIntoGUI(this.fontRenderer, result, cx + 2, cy + 2, null);',
        content
    )

    # 3. In right panel primaryResult
    content = re.sub(
        r'(this\.itemRender\.renderItemAndEffectIntoGUI\(primaryResult, \(int\) \(rightX / 2\.0f\), \(int\) \(\(guiTop \+ 8\) / 2\.0f\)\);)',
        r'\1\n                this.itemRender.renderItemOverlayIntoGUI(this.fontRenderer, primaryResult, (int) (rightX / 2.0f), (int) ((guiTop + 8) / 2.0f), null);',
        content
    )

    # 4. In yield items (secondary results)
    content = re.sub(
        r'(this\.itemRender\.renderItemAndEffectIntoGUI\(res, \(int\) \(yieldX / 0\.75f\), \(int\) \(\(guiTop \+ 31\) / 0\.75f\)\);)',
        r'\1\n                    this.itemRender.renderItemOverlayIntoGUI(this.fontRenderer, res, (int) (yieldX / 0.75f), (int) ((guiTop + 31) / 0.75f), null);',
        content
    )

    with open(filepath, 'w', encoding='utf-8') as f:
        f.write(content)

print("GUI patched")
