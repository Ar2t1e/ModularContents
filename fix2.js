const fs = require('fs');
let content = fs.readFileSync('src/main/java/modularcontents/custom/client/ModularResourcePack.java', 'utf8');

const target1 = '"{\n  \\"parent\\": \\" + ((info.biomeTint != null && !info.biomeTint.isEmpty()) ? \\"modularcontents:block/tinted_inner_stairs\\" : \\"block/inner_stairs\\") + \\",\n  \\"textures\\": {\n    \\"bottom\\": \\""';
const replacement1 = '"{\n  \\"parent\\": \\"" + ((info.biomeTint != null && !info.biomeTint.isEmpty()) ? "modularcontents:block/tinted_inner_stairs" : "block/inner_stairs") + "\\",\n  \\"textures\\": {\n    \\"bottom\\": \\""';
content = content.replace(target1, replacement1);

const target2 = '"{\n  \\"parent\\": \\" + ((info.biomeTint != null && !info.biomeTint.isEmpty()) ? \\"modularcontents:block/tinted_outer_stairs\\" : \\"block/outer_stairs\\") + \\",\n  \\"textures\\": {\n    \\"bottom\\": \\""';
const replacement2 = '"{\n  \\"parent\\": \\"" + ((info.biomeTint != null && !info.biomeTint.isEmpty()) ? "modularcontents:block/tinted_outer_stairs" : "block/outer_stairs") + "\\",\n  \\"textures\\": {\n    \\"bottom\\": \\""';
content = content.replace(target2, replacement2);

const target3 = '"{\n  \\"parent\\": \\" + ((info.biomeTint != null && !info.biomeTint.isEmpty()) ? \\"modularcontents:block/tinted_upper_slab\\" : \\"block/upper_slab\\") + \\",\n  \\"textures\\": {\n    \\"bottom\\": \\""';
const replacement3 = '"{\n  \\"parent\\": \\"" + ((info.biomeTint != null && !info.biomeTint.isEmpty()) ? "modularcontents:block/tinted_upper_slab" : "block/upper_slab") + "\\",\n  \\"textures\\": {\n    \\"bottom\\": \\""';
content = content.replace(target3, replacement3);

const target4 = '"{\n  \\"parent\\": \\" + ((info.biomeTint != null && !info.biomeTint.isEmpty()) ? \\"modularcontents:block/tinted_stairs\\" : \\"block/stairs\\") + \\",\n  \\"textures\\": {\n    \\"bottom\\": \\""';
const replacement4 = '"{\n  \\"parent\\": \\"" + ((info.biomeTint != null && !info.biomeTint.isEmpty()) ? "modularcontents:block/tinted_stairs" : "block/stairs") + "\\",\n  \\"textures\\": {\n    \\"bottom\\": \\""';
content = content.replace(target4, replacement4);

const target5 = '"{\n  \\"parent\\": \\" + ((info.biomeTint != null && !info.biomeTint.isEmpty()) ? \\"modularcontents:block/tinted_half_slab\\" : \\"block/half_slab\\") + \\",\n  \\"textures\\": {\n    \\"bottom\\": \\""';
const replacement5 = '"{\n  \\"parent\\": \\"" + ((info.biomeTint != null && !info.biomeTint.isEmpty()) ? "modularcontents:block/tinted_half_slab" : "block/half_slab") + "\\",\n  \\"textures\\": {\n    \\"bottom\\": \\""';
content = content.replace(target5, replacement5);

fs.writeFileSync('src/main/java/modularcontents/custom/client/ModularResourcePack.java', content);
