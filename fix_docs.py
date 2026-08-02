import re

with open('ModularContents_Documentation.md', 'r', encoding='utf-8') as f:
    doc = f.read()

addition = """
---

## 10. Переплавка (Smelting) и Топливо (Fuel)

Мод поддерживает интеграцию с ванильными печами. Вы можете добавлять новые рецепты переплавки и делать любые свои предметы топливом.

### Топливо (Fuel)
Чтобы любой ваш блок или предмет стал гореть в печке (как уголь или доски), добавьте в его JSON-файл параметр `burn_time`.
* `burn_time: 200` означает, что предмет горит 10 секунд (переплавляет ровно 1 предмет в обычной печке).
* Уголь имеет `burn_time: 1600` (80 секунд, хватает на 8 предметов).
* Ведро лавы: `20000`.

**Пример (В предмет или блок):**
```json
{
  "id": "compressed_coal",
  "display_name": "Сжатый Уголь",
  "max_stack_size": 64,
  "burn_time": 16000
}
```

### Рецепты переплавки (Smelting)
Рецепты переплавки должны лежать в папке `recipes/smelting/`.
Мод найдет их и добавит во все печи (ванильные и из других модов).

**Пример `recipes/smelting/melt_iron.json`:**
```json
{
  "id": "melt_iron_dust",
  "input": {
    "item": "modularcontents:iron_dust",
    "count": 1
  },
  "output": {
    "item": "minecraft:iron_ingot",
    "count": 1
  },
  "xp": 0.7
}
```
* `xp` — количество опыта, которое игрок получает, забирая предмет из печки. (У железной руды `xp: 0.7`, у алмазов `xp: 1.0`, у булыжника `xp: 0.1`).
"""

with open('ModularContents_Documentation.md', 'w', encoding='utf-8') as f:
    f.write(doc + addition)
