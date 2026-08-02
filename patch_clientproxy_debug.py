import re

with open('src/main/java/modularcontents/proxy/ClientProxy.java', 'r', encoding='utf-8') as f:
    content = f.read()

content = content.replace('if (block != null) {', 'if (block != null) {\n                    System.out.println("[ModularContents ClientProxy] Registering block color for: " + info.id + " (block: " + block.getRegistryName() + ")");')
content = content.replace('if (!coloredBlocks.isEmpty()) {', 'System.out.println("[ModularContents ClientProxy] Total colored blocks registered: " + coloredBlocks.size());\n        if (!coloredBlocks.isEmpty()) {')

with open('src/main/java/modularcontents/proxy/ClientProxy.java', 'w', encoding='utf-8') as f:
    f.write(content)
print("Patched ClientProxy")
