const fs = require('fs');
let file = 'src/main/java/modularcontents/custom/client/ModularResourcePack.java';
let content = fs.readFileSync(file, 'utf8');

const target = `} else if ("horizontal".equalsIgnoreCase(info.rotationType)) {`;

const doorGen = `} else if (type.equals("door")) {
                    generatedJson = "{\n  \\"variants\\": {\n" +
                            "    \\"facing=east,half=lower,hinge=left,open=false\\":  { \\"model\\": \\"modularcontents:" + blockId + "_bottom\\" },\n" +
                            "    \\"facing=south,half=lower,hinge=left,open=false\\": { \\"model\\": \\"modularcontents:" + blockId + "_bottom\\", \\"y\\": 90 },\n" +
                            "    \\"facing=west,half=lower,hinge=left,open=false\\":  { \\"model\\": \\"modularcontents:" + blockId + "_bottom\\", \\"y\\": 180 },\n" +
                            "    \\"facing=north,half=lower,hinge=left,open=false\\": { \\"model\\": \\"modularcontents:" + blockId + "_bottom\\", \\"y\\": 270 },\n" +
                            "    \\"facing=east,half=lower,hinge=right,open=false\\":  { \\"model\\": \\"modularcontents:" + blockId + "_bottom_rh\\" },\n" +
                            "    \\"facing=south,half=lower,hinge=right,open=false\\": { \\"model\\": \\"modularcontents:" + blockId + "_bottom_rh\\", \\"y\\": 90 },\n" +
                            "    \\"facing=west,half=lower,hinge=right,open=false\\":  { \\"model\\": \\"modularcontents:" + blockId + "_bottom_rh\\", \\"y\\": 180 },\n" +
                            "    \\"facing=north,half=lower,hinge=right,open=false\\": { \\"model\\": \\"modularcontents:" + blockId + "_bottom_rh\\", \\"y\\": 270 },\n" +
                            "    \\"facing=east,half=upper,hinge=left,open=false\\":  { \\"model\\": \\"modularcontents:" + blockId + "_top\\" },\n" +
                            "    \\"facing=south,half=upper,hinge=left,open=false\\": { \\"model\\": \\"modularcontents:" + blockId + "_top\\", \\"y\\": 90 },\n" +
                            "    \\"facing=west,half=upper,hinge=left,open=false\\":  { \\"model\\": \\"modularcontents:" + blockId + "_top\\", \\"y\\": 180 },\n" +
                            "    \\"facing=north,half=upper,hinge=left,open=false\\": { \\"model\\": \\"modularcontents:" + blockId + "_top\\", \\"y\\": 270 },\n" +
                            "    \\"facing=east,half=upper,hinge=right,open=false\\":  { \\"model\\": \\"modularcontents:" + blockId + "_top_rh\\" },\n" +
                            "    \\"facing=south,half=upper,hinge=right,open=false\\": { \\"model\\": \\"modularcontents:" + blockId + "_top_rh\\", \\"y\\": 90 },\n" +
                            "    \\"facing=west,half=upper,hinge=right,open=false\\":  { \\"model\\": \\"modularcontents:" + blockId + "_top_rh\\", \\"y\\": 180 },\n" +
                            "    \\"facing=north,half=upper,hinge=right,open=false\\": { \\"model\\": \\"modularcontents:" + blockId + "_top_rh\\", \\"y\\": 270 },\n" +
                            "    \\"facing=east,half=lower,hinge=left,open=true\\":  { \\"model\\": \\"modularcontents:" + blockId + "_bottom_rh\\", \\"y\\": 90 },\n" +
                            "    \\"facing=south,half=lower,hinge=left,open=true\\": { \\"model\\": \\"modularcontents:" + blockId + "_bottom_rh\\", \\"y\\": 180 },\n" +
                            "    \\"facing=west,half=lower,hinge=left,open=true\\":  { \\"model\\": \\"modularcontents:" + blockId + "_bottom_rh\\", \\"y\\": 270 },\n" +
                            "    \\"facing=north,half=lower,hinge=left,open=true\\": { \\"model\\": \\"modularcontents:" + blockId + "_bottom_rh\\" },\n" +
                            "    \\"facing=east,half=lower,hinge=right,open=true\\":  { \\"model\\": \\"modularcontents:" + blockId + "_bottom\\", \\"y\\": 90 },\n" +
                            "    \\"facing=south,half=lower,hinge=right,open=true\\": { \\"model\\": \\"modularcontents:" + blockId + "_bottom\\", \\"y\\": 180 },\n" +
                            "    \\"facing=west,half=lower,hinge=right,open=true\\":  { \\"model\\": \\"modularcontents:" + blockId + "_bottom\\", \\"y\\": 270 },\n" +
                            "    \\"facing=north,half=lower,hinge=right,open=true\\": { \\"model\\": \\"modularcontents:" + blockId + "_bottom\\" },\n" +
                            "    \\"facing=east,half=upper,hinge=left,open=true\\":  { \\"model\\": \\"modularcontents:" + blockId + "_top_rh\\", \\"y\\": 90 },\n" +
                            "    \\"facing=south,half=upper,hinge=left,open=true\\": { \\"model\\": \\"modularcontents:" + blockId + "_top_rh\\", \\"y\\": 180 },\n" +
                            "    \\"facing=west,half=upper,hinge=left,open=true\\":  { \\"model\\": \\"modularcontents:" + blockId + "_top_rh\\", \\"y\\": 270 },\n" +
                            "    \\"facing=north,half=upper,hinge=left,open=true\\": { \\"model\\": \\"modularcontents:" + blockId + "_top_rh\\" },\n" +
                            "    \\"facing=east,half=upper,hinge=right,open=true\\":  { \\"model\\": \\"modularcontents:" + blockId + "_top\\", \\"y\\": 90 },\n" +
                            "    \\"facing=south,half=upper,hinge=right,open=true\\": { \\"model\\": \\"modularcontents:" + blockId + "_top\\", \\"y\\": 180 },\n" +
                            "    \\"facing=west,half=upper,hinge=right,open=true\\":  { \\"model\\": \\"modularcontents:" + blockId + "_top\\", \\"y\\": 270 },\n" +
                            "    \\"facing=north,half=upper,hinge=right,open=true\\": { \\"model\\": \\"modularcontents:" + blockId + "_top\\" }\n" +
                            "  }\n}";
                `;

content = content.replace(target, doorGen + target);
fs.writeFileSync(file, content);
console.log("Patched door blockstates");
