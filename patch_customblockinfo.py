import re

with open('src/main/java/modularcontents/custom/item/CustomBlockInfo.java', 'r', encoding='utf-8') as f:
    content = f.read()

# Add biomeTint field
if 'biomeTint' not in content:
    content = content.replace('public String rotationType = "none"; // none, horizontal, log',
                              'public String rotationType = "none"; // none, horizontal, log\n\n    @SerializedName("biome_tint")\n    public String biomeTint = ""; // grass, foliage, water')
    with open('src/main/java/modularcontents/custom/item/CustomBlockInfo.java', 'w', encoding='utf-8') as f:
        f.write(content)
    print("Patched CustomBlockInfo.java")
