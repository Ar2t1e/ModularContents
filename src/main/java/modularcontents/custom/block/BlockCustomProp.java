package modularcontents.custom.block;

import modularcontents.custom.item.CustomBlockInfo;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class BlockCustomProp extends BlockCustomHorizontal {
    private final CustomBlockInfo info;
    public static final double PIXEL = 0.0625;

    public BlockCustomProp(CustomBlockInfo info) {
        super(info);
        this.info = info;
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    @Override
    public void addCollisionBoxToList(IBlockState state, World worldIn, BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes, @Nullable Entity entityIn, boolean isActualState) {
        if (info.boundingBoxes != null && info.boundingBoxes.length > 0) {
            EnumFacing facing = state.getValue(FACING);
            for (double[] box : info.boundingBoxes) {
                if (box != null && box.length >= 6) {
                    AxisAlignedBB aabb = getBlockBounds(facing, box[0], box[1], box[2], box[3], box[4], box[5]);
                    addCollisionBoxToList(pos, entityBox, collidingBoxes, aabb);
                }
            }
        } else {
            super.addCollisionBoxToList(state, worldIn, pos, entityBox, collidingBoxes, entityIn, isActualState);
        }
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        if (info.boundingBoxes != null && info.boundingBoxes.length > 0) {
            EnumFacing facing = state.getValue(FACING);
            AxisAlignedBB combined = null;
            for (double[] box : info.boundingBoxes) {
                if (box != null && box.length >= 6) {
                    AxisAlignedBB aabb = getBlockBounds(facing, box[0], box[1], box[2], box[3], box[4], box[5]);
                    if (combined == null) {
                        combined = aabb;
                    } else {
                        combined = combined.union(aabb);
                    }
                }
            }
            if (combined != null) {
                return combined;
            }
        }
        return super.getBoundingBox(state, source, pos);
    }

    public static AxisAlignedBB getBlockBounds(EnumFacing facing, double x1, double y1, double z1, double x2, double y2, double z2) {
        double[] bounds = fixRotation(facing, x1 * PIXEL, z1 * PIXEL, x2 * PIXEL, z2 * PIXEL);
        // Correct min/max ordering in case rotation flipped them
        double minX = Math.min(bounds[0], bounds[2]);
        double minZ = Math.min(bounds[1], bounds[3]);
        double maxX = Math.max(bounds[0], bounds[2]);
        double maxZ = Math.max(bounds[1], bounds[3]);
        double minY = Math.min(y1 * PIXEL, y2 * PIXEL);
        double maxY = Math.max(y1 * PIXEL, y2 * PIXEL);

        return new AxisAlignedBB(minX, minY, minZ, maxX, maxY, maxZ);
    }

    private static double[] fixRotation(EnumFacing facing, double var1, double var2, double var3, double var4) {
        switch (facing) {
            case WEST:
                return new double[]{1.0F - var3, 1.0F - var4, 1.0F - var1, 1.0F - var2};
            case NORTH:
                return new double[]{var2, 1.0F - var3, var4, 1.0F - var1};
            case SOUTH:
                return new double[]{1.0F - var4, var1, 1.0F - var2, var3};
            default: // EAST
                return new double[]{var1, var2, var3, var4};
        }
    }
}