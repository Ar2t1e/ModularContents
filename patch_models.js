const fs = require('fs');
let file = 'src/main/java/modularcontents/custom/client/ModularResourcePack.java';
let content = fs.readFileSync(file, 'utf8');

const t = `                } else if (blockId.endsWith("_top")) {
                    generatedJson = "{\n  \\"parent\\": \\"" + ((info.biomeTint != null && !info.biomeTint.isEmpty()) ? "modularcontents:block/tinted_upper_slab" : "block/upper_slab") + "\\",\n  \\"textures\\": {\n    \\"bottom\\": \\"" + tBottom + "\\",\n    \\"top\\": \\"" + tTop + "\\",\n    \\"side\\": \\"" + tSide + "\\"\n  }\n}";
                } else if (blockId.endsWith("_post")) {`;

const r = `                } else if (blockId.endsWith("_top")) {
                    generatedJson = "{\n  \\"parent\\": \\"" + ((info.biomeTint != null && !info.biomeTint.isEmpty()) ? "modularcontents:block/tinted_upper_slab" : "block/upper_slab") + "\\",\n  \\"textures\\": {\n    \\"bottom\\": \\"" + tBottom + "\\",\n    \\"top\\": \\"" + tTop + "\\",\n    \\"side\\": \\"" + tSide + "\\"\n  }\n}";
                } else if ("button".equals(info.blockType)) {
                    if (blockId.endsWith("_pressed")) {
                        generatedJson = "{\n  \\"parent\\": \\"block/button_pressed\\",\n  \\"textures\\": {\n    \\"texture\\": \\"" + texPath + "\\"\n  }\n}";
                    } else if (blockId.endsWith("_inventory")) {
                        generatedJson = "{\n  \\"parent\\": \\"block/button_inventory\\",\n  \\"textures\\": {\n    \\"texture\\": \\"" + texPath + "\\"\n  }\n}";
                    } else {
                        generatedJson = "{\n  \\"parent\\": \\"block/button\\",\n  \\"textures\\": {\n    \\"texture\\": \\"" + texPath + "\\"\n  }\n}";
                    }
                } else if ("pressure_plate".equals(info.blockType)) {
                    if (blockId.endsWith("_down")) {
                        generatedJson = "{\n  \\"parent\\": \\"block/pressure_plate_down\\",\n  \\"textures\\": {\n    \\"texture\\": \\"" + texPath + "\\"\n  }\n}";
                    } else {
                        generatedJson = "{\n  \\"parent\\": \\"block/pressure_plate_up\\",\n  \\"textures\\": {\n    \\"texture\\": \\"" + texPath + "\\"\n  }\n}";
                    }
                } else if ("trapdoor".equals(info.blockType)) {
                    if (blockId.endsWith("_top")) {
                        generatedJson = "{\n  \\"parent\\": \\"block/trapdoor_top\\",\n  \\"textures\\": {\n    \\"texture\\": \\"" + texPath + "\\"\n  }\n}";
                    } else if (blockId.endsWith("_open")) {
                        generatedJson = "{\n  \\"parent\\": \\"block/trapdoor_open\\",\n  \\"textures\\": {\n    \\"texture\\": \\"" + texPath + "\\"\n  }\n}";
                    } else {
                        generatedJson = "{\n  \\"parent\\": \\"block/trapdoor_bottom\\",\n  \\"textures\\": {\n    \\"texture\\": \\"" + texPath + "\\"\n  }\n}";
                    }
                } else if (blockId.endsWith("_post")) {`;

content = content.replace(t, r);
fs.writeFileSync(file, content);
console.log("Patched models");
