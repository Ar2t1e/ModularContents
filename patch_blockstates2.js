const fs = require('fs');
let file = 'src/main/java/modularcontents/custom/client/ModularResourcePack.java';
let content = fs.readFileSync(file, 'utf8');

content = content.replace(
    /\} else if \(type.equals\("button"\)\) \{\n                    generatedJson = "{\n  \\"variants\\": {\n/,
    `} else if (type.equals("button")) {\n                    generatedJson = "{\n  \\"variants\\": {\n    \\"inventory\\": { \\"model\\": \\"modularcontents:" + blockId + "_inventory\\" },\n`
);

content = content.replace(
    /\} else if \(type.equals\("pressure_plate"\)\) \{\n                    generatedJson = "{\n  \\"variants\\": {\n/,
    `} else if (type.equals("pressure_plate")) {\n                    generatedJson = "{\n  \\"variants\\": {\n    \\"inventory\\": { \\"model\\": \\"modularcontents:" + blockId + "\\" },\n`
);

content = content.replace(
    /\} else if \(type.equals\("trapdoor"\)\) \{\n                    generatedJson = "{\n  \\"variants\\": {\n/,
    `} else if (type.equals("trapdoor")) {\n                    generatedJson = "{\n  \\"variants\\": {\n    \\"inventory\\": { \\"model\\": \\"modularcontents:" + blockId + "_bottom\\" },\n`
);

content = content.replace(
    /\} else if \("door".equals\(type\)\) \{\n/,
    `} else if (type.equals("door")) {\n                    generatedJson = "{\n  \\"variants\\": {\n    \\"inventory\\": { \\"model\\": \\"modularcontents:" + blockId + "_bottom\\" },\n  }\n}";\n                } else if ("door".equals(type)) {\n`
); // Note: I didn't add door generation yet to blockstates.

fs.writeFileSync(file, content);
console.log("Added inventory variant to blockstates");
