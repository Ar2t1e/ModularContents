const fs = require('fs');
let file = 'src/main/java/modularcontents/custom/item/CustomBlockInfo.java';
let content = fs.readFileSync(file, 'utf8');

const fields = `
    @SerializedName("has_trapdoor")
    public boolean hasTrapdoor = false;

    @SerializedName("has_door")
    public boolean hasDoor = false;

    @SerializedName("has_button")
    public boolean hasButton = false;

    @SerializedName("has_pressure_plate")
    public boolean hasPressurePlate = false;
`;

if (!content.includes("hasTrapdoor")) {
    content = content.replace(/    @SerializedName\("has_wall"\)\n    public boolean hasWall = false;/g, 
        '    @SerializedName("has_wall")\n    public boolean hasWall = false;\n' + fields);
    fs.writeFileSync(file, content);
}
console.log("Updated CustomBlockInfo");
