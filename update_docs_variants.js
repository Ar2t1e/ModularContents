const fs = require('fs');
let doc = fs.readFileSync('ModularContents_Documentation.md', 'utf8');

doc = doc.replace(
    /`has_slab`, `has_stairs`, `has_fence`, `has_wall` \(boolean\) — если `true`, мод \*\*автоматически сгенерирует\*\* все полублоки, ступеньки, заборы и стены для этого блока!/g,
    `\`has_slab\`, \`has_stairs\`, \`has_fence\`, \`has_wall\`, \`has_door\`, \`has_trapdoor\`, \`has_button\`, \`has_pressure_plate\` (boolean) — если \`true\`, мод **автоматически сгенерирует** все полублоки, ступеньки, заборы, стены, двери, люки, кнопки и нажимные плиты для этого блока!`
);

doc = doc.replace(
    /"has_wall": true\n}/g,
    `"has_wall": true,\n  "has_door": true,\n  "has_trapdoor": true,\n  "has_button": true,\n  "has_pressure_plate": true\n}`
);

fs.writeFileSync('ModularContents_Documentation.md', doc);
