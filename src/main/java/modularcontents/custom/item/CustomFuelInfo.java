package modularcontents.custom.item;

import com.google.gson.annotations.SerializedName;

public class CustomFuelInfo {
    public String id;

    @SerializedName("item")
    public String item = "";

    @SerializedName("meta")
    public int meta = -1;

    @SerializedName("burn_time")
    public int burnTime = 0;
}
