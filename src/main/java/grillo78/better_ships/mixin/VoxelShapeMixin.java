package grillo78.better_ships.mixin;

import grillo78.better_ships.phys.OrientedBoundingBox;
import net.minecraft.core.AxisCycle;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.DiscreteVoxelShape;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(VoxelShape.class)
public abstract class VoxelShapeMixin {

    @Shadow public abstract boolean isEmpty();

    @Shadow protected abstract int findIndex(Direction.Axis pAxis, double pPosition);

    @Shadow @Final protected DiscreteVoxelShape shape;

    @Shadow protected abstract double get(Direction.Axis pAxis, int pIndex);

    @Inject(method = "collideX", at = @At("HEAD"), cancellable = true)
    public void onCollideX(AxisCycle pMovementAxis, AABB pCollisionBox, double pDesiredOffset, CallbackInfoReturnable<Double> cir) {
        if (pCollisionBox instanceof OrientedBoundingBox) {
            cir.setReturnValue(collideOBB(pMovementAxis, (OrientedBoundingBox) pCollisionBox, pDesiredOffset));
        }
    }
    protected double collideOBB(AxisCycle pMovementAxis, OrientedBoundingBox pCollisionBox, double pDesiredOffset) {
        if (this.isEmpty()) return pDesiredOffset;
        if (Math.abs(pDesiredOffset) < 1.0E-7D) return 0.0D;

        AxisCycle axiscycle = pMovementAxis.inverse();
        Direction.Axis direction$axis  = axiscycle.cycle(Direction.Axis.X); // eje de movimiento
        Direction.Axis direction$axis1 = axiscycle.cycle(Direction.Axis.Y);
        Direction.Axis direction$axis2 = axiscycle.cycle(Direction.Axis.Z);

        double d0 = pCollisionBox.supportMax(direction$axis); // frente del OBB en dirección de movimiento
        double d1 = pCollisionBox.supportMin(direction$axis); // trasera del OBB

        // i y j apuntan a la posición ACTUAL del OBB — igual que el original AABB
        int i = this.findIndex(direction$axis, d1 + 1.0E-7D);
        int j = this.findIndex(direction$axis, d0 - 1.0E-7D);

        // Para los ejes perpendiculares usamos supportMin/supportMax del OBB
        int k  = Math.max(0,
                this.findIndex(direction$axis1, pCollisionBox.supportMin(direction$axis1) + 1.0E-7D));
        int l  = Math.min(this.shape.getSize(direction$axis1),
                this.findIndex(direction$axis1, pCollisionBox.supportMax(direction$axis1) - 1.0E-7D) + 1);
        int i1 = Math.max(0,
                this.findIndex(direction$axis2, pCollisionBox.supportMin(direction$axis2) + 1.0E-7D));
        int j1 = Math.min(this.shape.getSize(direction$axis2),
                this.findIndex(direction$axis2, pCollisionBox.supportMax(direction$axis2) - 1.0E-7D) + 1);

        int k1 = this.shape.getSize(direction$axis);

        if (pDesiredOffset > 0.0D) {
            for (int l1 = j + 1; l1 < k1; ++l1) {
                for (int i2 = k; i2 < l; ++i2) {
                    for (int j2 = i1; j2 < j1; ++j2) {
                        if (this.shape.isFullWide(axiscycle, l1, i2, j2)) {
                            AABB cellAABB = getCellAABB(axiscycle, l1, i2, j2);
                            if (!pCollisionBox.intersectsOBB(OrientedBoundingBox.fromAABB(cellAABB))) {
                                continue;
                            }
                            double d2 = this.get(direction$axis, l1) - d0;
                            if (d2 >= -1.0E-7D) {
                                pDesiredOffset = Math.min(pDesiredOffset, d2);
                            }
                            return pDesiredOffset;
                        }
                    }
                }
            }
        } else if (pDesiredOffset < 0.0D) {
            System.out.println("[OBB collide -] axis=" + direction$axis
                    + " d1(supportMin)=" + d1 + " offset=" + pDesiredOffset
                    + " i=" + i + " k=" + k + " l=" + l + " i1=" + i1 + " j1=" + j1);
            for (int k2 = i - 1; k2 >= 0; --k2) {
                for (int l2 = k; l2 < l; ++l2) {
                    for (int i3 = i1; i3 < j1; ++i3) {
                        if (this.shape.isFullWide(axiscycle, k2, l2, i3)) {
                            AABB cellAABB = getCellAABB(axiscycle, k2, l2, i3);
                            boolean hits = pCollisionBox.intersectsOBB(OrientedBoundingBox.fromAABB(cellAABB));
                            System.out.println("  cell[" + k2 + "," + l2 + "," + i3 + "] "
                                    + cellAABB + " → intersectsOBB=" + hits);
                            if (!hits) continue;
                            double d3 = this.get(direction$axis, k2 + 1) - d1;
                            System.out.println("  → HIT d3=" + d3 + " finalOffset=" + Math.max(pDesiredOffset, d3));
                            if (d3 <= 1.0E-7D) {
                                pDesiredOffset = Math.max(pDesiredOffset, d3);
                            }
                            return pDesiredOffset;
                        }
                    }
                }
            }
            System.out.println("  → NO cell found, returning offset=" + pDesiredOffset);
        }

        return pDesiredOffset;
    }

    // Helper: reconstruye el AABB mundo de una celda a partir de sus índices ciclados
    private AABB getCellAABB(AxisCycle cycle, int a, int b, int c) {
        Direction.Axis ax0 = cycle.inverse().cycle(Direction.Axis.X);
        Direction.Axis ax1 = cycle.inverse().cycle(Direction.Axis.Y);
        Direction.Axis ax2 = cycle.inverse().cycle(Direction.Axis.Z);

        double minA = this.get(ax0, a),   maxA = this.get(ax0, a + 1);
        double minB = this.get(ax1, b),   maxB = this.get(ax1, b + 1);
        double minC = this.get(ax2, c),   maxC = this.get(ax2, c + 1);

        double[] mins = new double[3], maxs = new double[3];
        mins[ax0.ordinal()] = minA; maxs[ax0.ordinal()] = maxA;
        mins[ax1.ordinal()] = minB; maxs[ax1.ordinal()] = maxB;
        mins[ax2.ordinal()] = minC; maxs[ax2.ordinal()] = maxC;

        return new AABB(mins[0], mins[1], mins[2], maxs[0], maxs[1], maxs[2]);
    }

}
