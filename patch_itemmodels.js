const fs = require('fs');
let file = 'src/main/java/modularcontents/custom/client/ModularResourcePack.java';
let content = fs.readFileSync(file, 'utf8');

const t = `                             if ("fence".equalsIgnoreCase(bInfo.blockType) || "wall".equalsIgnoreCase(bInfo.blockType)) {
                                 generatedJson = "{\n  \\"parent\\": \\"modularcontents:block/" + itemId + "_inventory\\"\n}";
                             }
                         }
                    }`;

const r = `                             if ("fence".equalsIgnoreCase(bInfo.blockType) || "wall".equalsIgnoreCase(bInfo.blockType)) {
                                 generatedJson = "{\n  \\"parent\\": \\"modularcontents:block/" + itemId + "_inventory\\"\n}";
                             } else if ("button".equalsIgnoreCase(bInfo.blockType)) {
                                 generatedJson = "{\n  \\"parent\\": \\"modularcontents:block/" + itemId + "_inventory\\"\n}";
                             } else if ("door".equalsIgnoreCase(bInfo.blockType)) {
                                 generatedJson = "{\n  \\"parent\\": \\"item/generated\\",\n  \\"textures\\": {\n    \\"layer0\\": \\"modularcontents:items/" + itemId + "\\"\n  }\n}";
                             }
                         }
                    }`;

content = content.replace(t, r);
fs.writeFileSync(file, content);
console.log("Patched item models");
