const fs = require('fs');
let file = 'src/main/java/modularcontents/custom/client/ModularResourcePack.java';
let content = fs.readFileSync(file, 'utf8');

const t1 = `            String baseId = blockId.replace("_top", "").replace("_inner", "").replace("_outer", "").replace("_double", "").replace("_post", "").replace("_side", "").replace("_inventory", "");`;
const r1 = `            String baseId = blockId.replace("_top_rh", "").replace("_bottom_rh", "").replace("_bottom", "").replace("_top", "").replace("_inner", "").replace("_outer", "").replace("_double", "").replace("_post", "").replace("_side", "").replace("_inventory", "").replace("_pressed", "").replace("_down", "").replace("_open", "");`;

content = content.replace(t1, r1);
fs.writeFileSync(file, content);
console.log("Patched stripping");
