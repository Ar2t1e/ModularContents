const fs = require('fs');
let content = fs.readFileSync('src/main/java/modularcontents/custom/item/CustomContentManager.java', 'utf8');

const t = `        clone.biomeTint = original.biomeTint;
        clone.drops = original.drops;
        clone.transparent = original.transparent;
        clone.opacity = original.opacity;
        clone.fullBlock = original.fullBlock;
        clone.lightOpacity = original.lightOpacity;
        clone.canSilkTouch = original.canSilkTouch;
        clone.rotationType = original.rotationType;`;
const r = `        clone.biomeTint = original.biomeTint;
        clone.rotationType = original.rotationType;`;
        
content = content.replace(t, r);
fs.writeFileSync('src/main/java/modularcontents/custom/item/CustomContentManager.java', content);
