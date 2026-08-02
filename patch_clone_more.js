const fs = require('fs');
let content = fs.readFileSync('src/main/java/modularcontents/custom/item/CustomContentManager.java', 'utf8');

const t = `        clone.rotationType = original.rotationType;
        return clone;`;
const r = `        clone.rotationType = original.rotationType;
        clone.textureTop = original.textureTop;
        clone.textureBottom = original.textureBottom;
        clone.textureFront = original.textureFront;
        clone.textureSide = original.textureSide;
        clone.boundingBoxes = original.boundingBoxes;
        return clone;`;
        
content = content.replace(t, r);
fs.writeFileSync('src/main/java/modularcontents/custom/item/CustomContentManager.java', content);
