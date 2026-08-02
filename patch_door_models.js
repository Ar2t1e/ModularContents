const fs = require('fs');
let file = 'src/main/java/modularcontents/custom/client/ModularResourcePack.java';
let content = fs.readFileSync(file, 'utf8');

const target = `                } else if ("trapdoor".equals(info.blockType)) {`;

const doorGen = `                } else if ("door".equals(info.blockType)) {
                    if (blockId.endsWith("_bottom")) {
                        generatedJson = "{\n  \\"parent\\": \\"block/door_bottom\\",\n  \\"textures\\": {\n    \\"bottom\\": \\"" + tBottom + "\\",\n    \\"top\\": \\"" + tTop + "\\"\n  }\n}";
                    } else if (blockId.endsWith("_bottom_rh")) {
                        generatedJson = "{\n  \\"parent\\": \\"block/door_bottom_rh\\",\n  \\"textures\\": {\n    \\"bottom\\": \\"" + tBottom + "\\",\n    \\"top\\": \\"" + tTop + "\\"\n  }\n}";
                    } else if (blockId.endsWith("_top")) {
                        generatedJson = "{\n  \\"parent\\": \\"block/door_top\\",\n  \\"textures\\": {\n    \\"bottom\\": \\"" + tBottom + "\\",\n    \\"top\\": \\"" + tTop + "\\"\n  }\n}";
                    } else if (blockId.endsWith("_top_rh")) {
                        generatedJson = "{\n  \\"parent\\": \\"block/door_top_rh\\",\n  \\"textures\\": {\n    \\"bottom\\": \\"" + tBottom + "\\",\n    \\"top\\": \\"" + tTop + "\\"\n  }\n}";
                    }
                `;

content = content.replace(target, doorGen + target);
fs.writeFileSync(file, content);
console.log("Patched door models");
