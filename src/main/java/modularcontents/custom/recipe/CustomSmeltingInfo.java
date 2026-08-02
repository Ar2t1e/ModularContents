package modularcontents.custom.recipe;

import com.google.gson.annotations.SerializedName;

public class CustomSmeltingInfo {
    public String id;
    
    @SerializedName("input")
    public IngredientStack input;
    
    @SerializedName("output")
    public IngredientStack output;
    
    @SerializedName("xp")
    public float xp = 0.0f;
}
