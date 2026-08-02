const fs = require('fs');

let content = fs.readFileSync('src/main/java/modularcontents/custom/client/ModularResourcePack.java', 'utf8');

content = content.replace(/"\{\n  \\"parent\\": \\" \+ \(\(info\.biomeTint != null && !info\.biomeTint\.isEmpty\(\)\) \? \\"modularcontents:block\/tinted_inner_stairs\\" : \\"block\/inner_stairs\\"\) \+ \\",\n/g,
    `"{\n  \\"parent\\": \\"" + ((info.biomeTint != null && !info.biomeTint.isEmpty()) ? "modularcontents:block/tinted_inner_stairs" : "block/inner_stairs") + "\\",\n`);

content = content.replace(/"\{\n  \\"parent\\": \\" \+ \(\(info\.biomeTint != null && !info\.biomeTint\.isEmpty\(\)\) \? \\"modularcontents:block\/tinted_outer_stairs\\" : \\"block\/outer_stairs\\"\) \+ \\",\n/g,
    `"{\n  \\"parent\\": \\"" + ((info.biomeTint != null && !info.biomeTint.isEmpty()) ? "modularcontents:block/tinted_outer_stairs" : "block/outer_stairs") + "\\",\n`);

content = content.replace(/"\{\n  \\"parent\\": \\" \+ \(\(info\.biomeTint != null && !info\.biomeTint\.isEmpty\(\)\) \? \\"modularcontents:block\/tinted_upper_slab\\" : \\"block\/upper_slab\\"\) \+ \\",\n/g,
    `"{\n  \\"parent\\": \\"" + ((info.biomeTint != null && !info.biomeTint.isEmpty()) ? "modularcontents:block/tinted_upper_slab" : "block/upper_slab") + "\\",\n`);

content = content.replace(/"\{\n  \\"parent\\": \\" \+ \(\(info\.biomeTint != null && !info\.biomeTint\.isEmpty\(\)\) \? \\"modularcontents:block\/tinted_stairs\\" : \\"block\/stairs\\"\) \+ \\",\n/g,
    `"{\n  \\"parent\\": \\"" + ((info.biomeTint != null && !info.biomeTint.isEmpty()) ? "modularcontents:block/tinted_stairs" : "block/stairs") + "\\",\n`);

content = content.replace(/"\{\n  \\"parent\\": \\" \+ \(\(info\.biomeTint != null && !info\.biomeTint\.isEmpty\(\)\) \? \\"modularcontents:block\/tinted_half_slab\\" : \\"block\/half_slab\\"\) \+ \\",\n/g,
    `"{\n  \\"parent\\": \\"" + ((info.biomeTint != null && !info.biomeTint.isEmpty()) ? "modularcontents:block/tinted_half_slab" : "block/half_slab") + "\\",\n`);


fs.writeFileSync('src/main/java/modularcontents/custom/client/ModularResourcePack.java', content);
