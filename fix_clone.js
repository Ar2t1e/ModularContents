const fs = require('fs');
let content = fs.readFileSync('src/main/java/modularcontents/custom/item/CustomContentManager.java', 'utf8');

const t = `        clone.harvestLevel = original.harvestLevel;
        clone.texture = (original.texture != null && !original.texture.isEmpty()) ? original.texture : original.id;
        clone.blockType = newType;
        return clone;`;
        
const r = `        clone.harvestLevel = original.harvestLevel;
        clone.texture = (original.texture != null && !original.texture.isEmpty()) ? original.texture : original.id;
        clone.blockType = newType;
        clone.biomeTint = original.biomeTint;
        clone.drops = original.drops;
        clone.transparent = original.transparent;
        clone.opacity = original.opacity;
        clone.fullBlock = original.fullBlock;
        clone.lightOpacity = original.lightOpacity;
        clone.canSilkTouch = original.canSilkTouch;
        clone.rotationType = original.rotationType;
        return clone;`;
        
content = content.replace(t, r);
fs.writeFileSync('src/main/java/modularcontents/custom/item/CustomContentManager.java', content);
