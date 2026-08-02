const fs = require('fs');
const files = [
    'src/main/java/modularcontents/custom/gui/GuiListWorkbench.java',
    'src/main/java/modularcontents/custom/client/gui/GuiHandcraft.java'
];

for (let filepath of files) {
    let content = fs.readFileSync(filepath, 'utf8');

    content = content.replace(
        /(this\.itemRender\.renderItemAndEffectIntoGUI\(result, \(int\) \(\(guiLeft \+ 10\) \/ 0\.75f\), \(int\) \(\(rowY \+ 3\) \/ 0\.75f\)\);)/g,
        '$1\n                this.itemRender.renderItemOverlayIntoGUI(this.fontRenderer, result, (int) ((guiLeft + 10) / 0.75f), (int) ((rowY + 3) / 0.75f), null);'
    );

    content = content.replace(
        /(this\.itemRender\.renderItemAndEffectIntoGUI\(result, cx \+ 2, cy \+ 2\);)/g,
        '$1\n            this.itemRender.renderItemOverlayIntoGUI(this.fontRenderer, result, cx + 2, cy + 2, null);'
    );

    content = content.replace(
        /(this\.itemRender\.renderItemAndEffectIntoGUI\(primaryResult, \(int\) \(rightX \/ 2\.0f\), \(int\) \(\(guiTop \+ 8\) \/ 2\.0f\)\);)/g,
        '$1\n                this.itemRender.renderItemOverlayIntoGUI(this.fontRenderer, primaryResult, (int) (rightX / 2.0f), (int) ((guiTop + 8) / 2.0f), null);'
    );

    content = content.replace(
        /(this\.itemRender\.renderItemAndEffectIntoGUI\(res, \(int\) \(yieldX \/ 0\.75f\), \(int\) \(\(guiTop \+ 31\) \/ 0\.75f\)\);)/g,
        '$1\n                    this.itemRender.renderItemOverlayIntoGUI(this.fontRenderer, res, (int) (yieldX / 0.75f), (int) ((guiTop + 31) / 0.75f), null);'
    );

    fs.writeFileSync(filepath, content);
}
console.log("Patched GUI");
