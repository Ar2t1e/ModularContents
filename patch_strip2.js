const fs = require('fs');
let file = 'src/main/java/modularcontents/custom/client/ModularResourcePack.java';
let content = fs.readFileSync(file, 'utf8');

const t = `            boolean isDoubleSlab = blockId.endsWith("_double");
            String searchId = isDoubleSlab ? blockId.substring(0, blockId.length() - 7) : blockId;
            if (CustomContentManager.CUSTOM_BLOCKS.containsKey(searchId) || modularcontents.custom.pack.CustomWorkbenchManager.getWorkbench(searchId) != null) return true;`;
const r = `            String searchId = blockId.replace("_double", "");
            if (CustomContentManager.CUSTOM_BLOCKS.containsKey(searchId) || modularcontents.custom.pack.CustomWorkbenchManager.getWorkbench(searchId) != null) return true;`;

content = content.replace(t, r);
fs.writeFileSync(file, content);
