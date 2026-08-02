const fs = require('fs');
let file = 'src/main/java/modularcontents/ModularcontentsMod.java';
let content = fs.readFileSync(file, 'utf8');

const t = `            } else if ("wall".equalsIgnoreCase(info.blockType)) {
                block = new modularcontents.custom.block.BlockCustomWall(info);
            } else {`;
const r = `            } else if ("wall".equalsIgnoreCase(info.blockType)) {
                block = new modularcontents.custom.block.BlockCustomWall(info);
            } else if ("trapdoor".equalsIgnoreCase(info.blockType)) {
                block = new modularcontents.custom.block.BlockCustomTrapDoor(info);
            } else if ("button".equalsIgnoreCase(info.blockType)) {
                block = new modularcontents.custom.block.BlockCustomButton(info);
            } else if ("pressure_plate".equalsIgnoreCase(info.blockType)) {
                block = new modularcontents.custom.block.BlockCustomPressurePlate(info);
            } else if ("door".equalsIgnoreCase(info.blockType)) {
                block = new modularcontents.custom.block.BlockCustomDoor(info);
            } else {`;
content = content.replace(t, r);

const t2 = `                } else {
                    itemBlock = new net.minecraft.item.ItemBlock(block);
                }`;
const r2 = `                } else if (block instanceof modularcontents.custom.block.BlockCustomDoor) {
                    itemBlock = new net.minecraft.item.ItemDoor(block);
                    ((modularcontents.custom.block.BlockCustomDoor) block).setDropItem(itemBlock);
                } else {
                    itemBlock = new net.minecraft.item.ItemBlock(block);
                }`;
content = content.replace(t2, r2);

fs.writeFileSync(file, content);
console.log("Patched ModularcontentsMod");
