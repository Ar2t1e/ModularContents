const fs = require('fs');
let content = fs.readFileSync('src/main/java/modularcontents/custom/client/ModularResourcePack.java', 'utf8');

const t1 = 'generatedJson = "{\n  \\"parent\\": \\" + ((info.biomeTint != null && !info.biomeTint.isEmpty()) ? \\"modularcontents:block/tinted_inner_stairs\\" : \\"block/inner_stairs\\") + \\",\n  \\"textures\\": {\n    \\"bottom\\": \\"" + tBottom + "\\",\n    \\"top\\": \\"" + tTop + "\\",\n    \\"side\\": \\"" + tSide + "\\"\n  }\n}";';
const r1 = 'generatedJson = "{\n  \\"parent\\": \\"" + ((info.biomeTint != null && !info.biomeTint.isEmpty()) ? "modularcontents:block/tinted_inner_stairs" : "block/inner_stairs") + "\\",\n  \\"textures\\": {\n    \\"bottom\\": \\"" + tBottom + "\\",\n    \\"top\\": \\"" + tTop + "\\",\n    \\"side\\": \\"" + tSide + "\\"\n  }\n}";';
content = content.replace(t1, r1);

const t2 = 'generatedJson = "{\n  \\"parent\\": \\" + ((info.biomeTint != null && !info.biomeTint.isEmpty()) ? \\"modularcontents:block/tinted_outer_stairs\\" : \\"block/outer_stairs\\") + \\",\n  \\"textures\\": {\n    \\"bottom\\": \\"" + tBottom + "\\",\n    \\"top\\": \\"" + tTop + "\\",\n    \\"side\\": \\"" + tSide + "\\"\n  }\n}";';
const r2 = 'generatedJson = "{\n  \\"parent\\": \\"" + ((info.biomeTint != null && !info.biomeTint.isEmpty()) ? "modularcontents:block/tinted_outer_stairs" : "block/outer_stairs") + "\\",\n  \\"textures\\": {\n    \\"bottom\\": \\"" + tBottom + "\\",\n    \\"top\\": \\"" + tTop + "\\",\n    \\"side\\": \\"" + tSide + "\\"\n  }\n}";';
content = content.replace(t2, r2);

const t3 = 'generatedJson = "{\n  \\"parent\\": \\" + ((info.biomeTint != null && !info.biomeTint.isEmpty()) ? \\"modularcontents:block/tinted_upper_slab\\" : \\"block/upper_slab\\") + \\",\n  \\"textures\\": {\n    \\"bottom\\": \\"" + tBottom + "\\",\n    \\"top\\": \\"" + tTop + "\\",\n    \\"side\\": \\"" + tSide + "\\"\n  }\n}";';
const r3 = 'generatedJson = "{\n  \\"parent\\": \\"" + ((info.biomeTint != null && !info.biomeTint.isEmpty()) ? "modularcontents:block/tinted_upper_slab" : "block/upper_slab") + "\\",\n  \\"textures\\": {\n    \\"bottom\\": \\"" + tBottom + "\\",\n    \\"top\\": \\"" + tTop + "\\",\n    \\"side\\": \\"" + tSide + "\\"\n  }\n}";';
content = content.replace(t3, r3);

const t4 = 'generatedJson = "{\n  \\"parent\\": \\" + ((info.biomeTint != null && !info.biomeTint.isEmpty()) ? \\"modularcontents:block/tinted_stairs\\" : \\"block/stairs\\") + \\",\n  \\"textures\\": {\n    \\"bottom\\": \\"" + tBottom + "\\",\n    \\"top\\": \\"" + tTop + "\\",\n    \\"side\\": \\"" + tSide + "\\"\n  }\n}";';
const r4 = 'generatedJson = "{\n  \\"parent\\": \\"" + ((info.biomeTint != null && !info.biomeTint.isEmpty()) ? "modularcontents:block/tinted_stairs" : "block/stairs") + "\\",\n  \\"textures\\": {\n    \\"bottom\\": \\"" + tBottom + "\\",\n    \\"top\\": \\"" + tTop + "\\",\n    \\"side\\": \\"" + tSide + "\\"\n  }\n}";';
content = content.replace(t4, r4);

const t5 = 'generatedJson = "{\n  \\"parent\\": \\" + ((info.biomeTint != null && !info.biomeTint.isEmpty()) ? \\"modularcontents:block/tinted_half_slab\\" : \\"block/half_slab\\") + \\",\n  \\"textures\\": {\n    \\"bottom\\": \\"" + tBottom + "\\",\n    \\"top\\": \\"" + tTop + "\\",\n    \\"side\\": \\"" + tSide + "\\"\n  }\n}";';
const r5 = 'generatedJson = "{\n  \\"parent\\": \\"" + ((info.biomeTint != null && !info.biomeTint.isEmpty()) ? "modularcontents:block/tinted_half_slab" : "block/half_slab") + "\\",\n  \\"textures\\": {\n    \\"bottom\\": \\"" + tBottom + "\\",\n    \\"top\\": \\"" + tTop + "\\",\n    \\"side\\": \\"" + tSide + "\\"\n  }\n}";';
content = content.replace(t5, r5);

fs.writeFileSync('src/main/java/modularcontents/custom/client/ModularResourcePack.java', content);
