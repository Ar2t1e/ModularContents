import re

files = [
    'src/main/java/modularcontents/custom/item/CustomBlockInfo.java',
    'src/main/java/modularcontents/custom/item/CustomItemInfo.java'
]

for file in files:
    with open(file, 'r', encoding='utf-8') as f:
        content = f.read()
    
    if "burnTime" not in content:
        content = re.sub(r'}\s*$', '    @SerializedName("burn_time")\n    public int burnTime = 0;\n}', content)
        with open(file, 'w', encoding='utf-8') as f:
            f.write(content)

