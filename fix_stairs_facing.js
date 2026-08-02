const fs = require('fs');
let file = 'src/main/java/modularcontents/custom/client/ModularResourcePack.java';
let content = fs.readFileSync(file, 'utf8');

const t = `    \\"facing=east,half=bottom,shape=straight\\":  { \\"model\\": \\"modularcontents:" + blockId + "\\" },\n" +
    \\"facing=west,half=bottom,shape=straight\\":  { \\"model\\": \\"modularcontents:" + blockId + "\\", \\"y\\": 180 },\n" +
    \\"facing=south,half=bottom,shape=straight\\": { \\"model\\": \\"modularcontents:" + blockId + "\\", \\"y\\": 90 },\n" +
    \\"facing=north,half=bottom,shape=straight\\": { \\"model\\": \\"modularcontents:" + blockId + "\\", \\"y\\": 270 },\n"`;

content = content.replace(
    /\} else if \(type.equals\("stair"\)\) \{\n                    generatedJson = "{\n  \\"variants\\": \{\n    \\"facing=east,half=bottom,shape=straight\\":  \{ \\"model\\": \\"modularcontents:" \+ blockId \+ "\\" \},\n    \\"facing=west,half=bottom,shape=straight\\":  \{ \\"model\\": \\"modularcontents:" \+ blockId \+ "\\", \\"y\\": 180 \},\n    \\"facing=south,half=bottom,shape=straight\\": \{ \\"model\\": \\"modularcontents:" \+ blockId \+ "\\", \\"y\\": 90 \},\n    \\"facing=north,half=bottom,shape=straight\\": \{ \\"model\\": \\"modularcontents:" \+ blockId \+ "\\", \\"y\\": 270 \}/g,
    `} else if (type.equals("stair") || type.equals("stairs")) {\n                    generatedJson = "{\n  \\"variants\\": {\n    \\"facing=east,half=bottom,shape=straight\\":  { \\"model\\": \\"modularcontents:" + blockId + "\\" },\n    \\"facing=west,half=bottom,shape=straight\\":  { \\"model\\": \\"modularcontents:" + blockId + "\\", \\"y\\": 180 },\n    \\"facing=south,half=bottom,shape=straight\\": { \\"model\\": \\"modularcontents:" + blockId + "\\", \\"y\\": 90 },\n    \\"facing=north,half=bottom,shape=straight\\": { \\"model\\": \\"modularcontents:" + blockId + "\\", \\"y\\": 270 }`
);

fs.writeFileSync(file, content);
console.log("Patched stairs facing");
