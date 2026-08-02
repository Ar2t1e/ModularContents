const fs = require('fs');
let file = 'src/main/java/modularcontents/custom/client/ModularResourcePack.java';
let content = fs.readFileSync(file, 'utf8');

const t = `                } else if (type.equals("fence")) {
                    generatedJson = "{\n  \\"multipart\\": [\n" +
                        "    { \\"apply\\": { \\"model\\": \\"modularcontents:" + blockId + "_post\\" } },\n" +
                        "    { \\"when\\": { \\"north\\": \\"true\\" }, \\"apply\\": { \\"model\\": \\"modularcontents:" + blockId + "_side\\", \\"uvlock\\": true } },\n" +
                        "    { \\"when\\": { \\"east\\": \\"true\\" }, \\"apply\\": { \\"model\\": \\"modularcontents:" + blockId + "_side\\", \\"y\\": 90, \\"uvlock\\": true } },\n" +
                        "    { \\"when\\": { \\"south\\": \\"true\\" }, \\"apply\\": { \\"model\\": \\"modularcontents:" + blockId + "_side\\", \\"y\\": 180, \\"uvlock\\": true } },\n" +
                        "    { \\"when\\": { \\"west\\": \\"true\\" }, \\"apply\\": { \\"model\\": \\"modularcontents:" + blockId + "_side\\", \\"y\\": 270, \\"uvlock\\": true } }\n" +
                        "  ]\n}";`;

// Let's verify the fence rotations. In vanilla, y=90 is east. y=180 is south. y=270 is west. 
// Wait, wait... the model is generated from `tinted_fence_side`, let's check its UVs and position.
