const fs = require('fs');
const files = [
    'src/main/java/modularcontents/custom/gui/GuiListWorkbench.java',
    'src/main/java/modularcontents/custom/client/gui/GuiHandcraft.java'
];

for (let filepath of files) {
    let content = fs.readFileSync(filepath, 'utf8');

    content = content.replace(
        /(this\.itemRender\.renderItemAndEffectIntoGUI\(icon, qx, qy\);)/g,
        '$1\n                        this.itemRender.renderItemOverlayIntoGUI(this.fontRenderer, icon, qx, qy, null);'
    );

    fs.writeFileSync(filepath, content);
}
console.log("Patched queue slots");
