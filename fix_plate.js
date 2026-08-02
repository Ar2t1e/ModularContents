const fs = require('fs');
let file = 'src/main/java/modularcontents/custom/block/BlockCustomPressurePlate.java';
let content = fs.readFileSync(file, 'utf8');

const t = `        super(getMaterialFromName(info.material) == Material.WOOD ? Sensitivity.EVERYTHING : Sensitivity.MOBS, getMaterialFromName(info.material));`;
const r = `        super(getMaterialFromName(info.material), getMaterialFromName(info.material) == Material.WOOD ? Sensitivity.EVERYTHING : Sensitivity.MOBS);`;

content = content.replace(t, r);
fs.writeFileSync(file, content);
console.log("Fixed constructor order");
