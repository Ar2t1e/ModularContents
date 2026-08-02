const fs = require('fs');

const files = [
    'src/main/java/modularcontents/custom/item/CustomBlockInfo.java',
    'src/main/java/modularcontents/custom/item/CustomItemInfo.java'
];

for (let file of files) {
    let content = fs.readFileSync(file, 'utf8');
    if (!content.includes("burnTime")) {
        content = content.replace(/}\s*$/, '    @SerializedName("burn_time")\n    public int burnTime = 0;\n}\n');
        fs.writeFileSync(file, content);
    }
}
console.log("Added burnTime");
