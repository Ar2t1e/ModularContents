package modularcontents.custom.item;

import com.google.gson.annotations.SerializedName;

public class CustomItemInfo {
    public String id;

    @SerializedName("display_name")
    public String displayName = "Custom Item";

    @SerializedName("max_stack_size")
    public int maxStackSize = 64;

    @SerializedName("creative_tab")
    public String creativeTab = "misc"; // currently defaults to misc or custom tab

    @SerializedName("max_damage")
    public int maxDamage = 0; // if > 0, it acts as a tool with durability

    // In the future we can add tool types, food stats, etc.
}