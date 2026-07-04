const fs = require('fs');
const path = require('path');

const refDir = "C:\\Users\\Ethan\\MCreatorWorkspaces\\modularcontents\\reference";
const outDir = "C:\\Users\\Ethan\\MCreatorWorkspaces\\modularcontents\\run\\ModularContents\\example_pack";

const itemsDir = path.join(outDir, "items");
const texturesDir = path.join(outDir, "textures", "items");

if (!fs.existsSync(itemsDir)) fs.mkdirSync(itemsDir, { recursive: true });
if (!fs.existsSync(texturesDir)) fs.mkdirSync(texturesDir, { recursive: true });

function processFolder(subfolder, isTool) {
    const fullPath = path.join(refDir, subfolder);
    if (!fs.existsSync(fullPath)) return;

    const files = fs.readdirSync(fullPath).filter(f => f.endsWith('.png'));
    for (const file of files) {
        const itemId = file.replace('.png', '');
        const displayName = itemId.replace(/_/g, ' ').replace(/\b\w/g, l => l.toUpperCase());

        // Copy texture
        fs.copyFileSync(path.join(fullPath, file), path.join(texturesDir, file));

        // Create JSON
        const itemData = {
            id: itemId,
            display_name: displayName,
            max_stack_size: isTool ? 1 : 64,
            creative_tab: isTool ? "tools" : "misc"
        };

        if (isTool) {
            itemData.max_damage = 100; // default 100 uses
        }

        fs.writeFileSync(path.join(itemsDir, `${itemId}.json`), JSON.stringify(itemData, null, 4));
        console.log(`Added ${isTool ? 'tool' : 'component'}: ${itemId}`);
    }
}

processFolder("components", false);
processFolder("tools", true);
console.log("Done!");
