const fs = require('fs');
let file = 'src/main/java/modularcontents/custom/item/CustomContentManager.java';
let content = fs.readFileSync(file, 'utf8');

const t1 = `        clone.hasSlab = false;
        clone.hasStairs = false;
        clone.hasFence = false;
        clone.hasWall = false;`;
const r1 = `        clone.hasSlab = false;
        clone.hasStairs = false;
        clone.hasFence = false;
        clone.hasWall = false;
        clone.hasDoor = false;
        clone.hasTrapdoor = false;
        clone.hasButton = false;
        clone.hasPressurePlate = false;`;

content = content.replace(t1, r1);

const t2 = `            if (original.hasWall) {
                CustomBlockInfo wall = cloneBlock(original, original.id + "_wall", "wall");
                CUSTOM_BLOCKS.put(wall.id, wall);
            }`;
const r2 = `            if (original.hasWall) {
                CustomBlockInfo wall = cloneBlock(original, original.id + "_wall", "wall");
                CUSTOM_BLOCKS.put(wall.id, wall);
            }
            if (original.hasDoor) {
                CustomBlockInfo door = cloneBlock(original, original.id + "_door", "door");
                CUSTOM_BLOCKS.put(door.id, door);
            }
            if (original.hasTrapdoor) {
                CustomBlockInfo trapdoor = cloneBlock(original, original.id + "_trapdoor", "trapdoor");
                CUSTOM_BLOCKS.put(trapdoor.id, trapdoor);
            }
            if (original.hasButton) {
                CustomBlockInfo button = cloneBlock(original, original.id + "_button", "button");
                CUSTOM_BLOCKS.put(button.id, button);
            }
            if (original.hasPressurePlate) {
                CustomBlockInfo plate = cloneBlock(original, original.id + "_pressure_plate", "pressure_plate");
                CUSTOM_BLOCKS.put(plate.id, plate);
            }`;

content = content.replace(t2, r2);

fs.writeFileSync(file, content);
console.log("Patched variants generation");
