package modularcontents.custom.block;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import java.util.Collection;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.util.EnumFacing;

public class CustomPropertyDirection extends PropertyEnum<EnumFacing> {
    protected CustomPropertyDirection(String name, Collection<EnumFacing> values) {
        super(name, EnumFacing.class, values);
    }

    public static CustomPropertyDirection create(String name) {
        return create(name, Predicates.alwaysTrue());
    }

    public static CustomPropertyDirection create(String name, Predicate<EnumFacing> filter) {
        return create(name, Collections2.filter(Lists.newArrayList(EnumFacing.values()), filter));
    }

    public static CustomPropertyDirection create(String name, Collection<EnumFacing> values) {
        return new CustomPropertyDirection(name, values);
    }
}
