package modularcontents.custom.block;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableSet;
import java.util.Collection;
import net.minecraft.block.properties.PropertyHelper;

public class CustomPropertyBool extends PropertyHelper<Boolean> {
    private final ImmutableSet<Boolean> allowedValues = ImmutableSet.of(true, false);

    protected CustomPropertyBool(String name) {
        super(name, Boolean.class);
    }

    public static CustomPropertyBool create(String name) {
        return new CustomPropertyBool(name);
    }

    @Override
    public Collection<Boolean> getAllowedValues() {
        return this.allowedValues;
    }

    @Override
    public Optional<Boolean> parseValue(String value) {
        return "true".equals(value) || "false".equals(value) ? Optional.of(Boolean.valueOf(value)) : Optional.absent();
    }

    @Override
    public String getName(Boolean value) {
        return value.toString();
    }
}
