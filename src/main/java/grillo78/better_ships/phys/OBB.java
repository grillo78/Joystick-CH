package grillo78.better_ships.phys;

import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix3f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class OBB extends AABB {

    private Vector3f center;      // Position of the OBB
    private Vector3f halfExtents; // Half-widths (scale / 2)
    private Vector3f[] axes;      // 3 orthonormal axes (computed from rotation)
    private Quaternionf rotation;      // 3 orthonormal axes (computed from rotation)

    public OBB(Vector3f center, Vector3f scale) {
        super(new Vec3(center.sub(scale.mul(0.5F))), new Vec3(scale.mul(2F).add(center)));
        this.center = center;
        this.halfExtents = scale.mul(0.5f); // Half extents
        rotate(0,0,0);
    }

    public OBB(AABB pOther) {
        this(pOther.getCenter().toVector3f(), new Vector3f((float) (pOther.maxX-pOther.minX), (float) (pOther.maxY-pOther.minY), (float) (pOther.maxZ-pOther.minZ)));
    }

    public Quaternionf getRotation() {
        return rotation;
    }

    @Override
    public Vec3 getCenter() {
        return new Vec3(center);
    }

    @Override
    public AABB move(BlockPos pPos) {
        return move(new Vec3(pPos.getX(), pPos.getY(), pPos.getZ()));
    }

    @Override
    public AABB move(Vec3 pVec) {
        OBB obb = new OBB(center.add(pVec.toVector3f()), halfExtents.mul(2));
        obb.axes = axes;
        obb.rotation = rotation;
        return obb;
    }

    @Override
    public AABB move(double pX, double pY, double pZ) {
        return move(new Vec3(pX, pY, pZ));
    }

    public void rotate(float yaw, float pitch, float roll){
        // Compute rotation matrix
        rotation = new Quaternionf().rotateY(yaw).rotateX(pitch).rotateZ(roll);
        updateAxes(rotation);
    }

    public void updateAxes(Quaternionf rotations) {
        Matrix3f rotationMatrix = rotations.get(new Matrix3f());
        this.rotation = rotations;
        // Extract rotated axes
        this.axes = new Vector3f[]{
                rotationMatrix.getColumn(0, new Vector3f()), // X-axis
                rotationMatrix.getColumn(1, new Vector3f()), // Y-axis
                rotationMatrix.getColumn(2, new Vector3f())  // Z-axis
        };
    }

    @Override
    public boolean intersects(AABB pOther) {
        return checkOBBCollision(pOther instanceof OBB? (OBB) pOther : new OBB(pOther));
    }

    @Override
    public boolean intersects(Vec3 pMin, Vec3 pMax) {
        return intersects(new AABB(pMin, pMax));
    }

    @Override
    public boolean intersects(double pX1, double pY1, double pZ1, double pX2, double pY2, double pZ2) {
        return intersects(new AABB(pX1, pY1, pZ1, pX2, pY2, pZ2));
    }

    public boolean checkOBBCollision(OBB b) {
        float[][] R = new float[3][3];
        float[][] AbsR = new float[3][3];

        // Compute rotation matrix from A to B
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                R[i][j] = this.axes[i].dot(b.axes[j]);
                AbsR[i][j] = Math.abs(R[i][j]) + 1e-6f;
            }
        }
        // Compute the translation vector
        Vector3f t = b.center.sub(this.center, new Vector3f());
        t.set(t.dot(this.axes[0]), t.dot(this.axes[1]), t.dot(this.axes[2])); // Convert to OBB A's space
        // Check each axis of OBB A
        for (int i = 0; i < 3; i++) {
            float ra = this.halfExtents.get(i);
            float rb = b.halfExtents.x * AbsR[i][0] + b.halfExtents.y * AbsR[i][1] + b.halfExtents.z * AbsR[i][2];
            if (Math.abs(t.get(i)) > ra + rb) return false;
        }
        // Check each axis of OBB B
        for (int i = 0; i < 3; i++) {
            float ra = this.halfExtents.x * AbsR[0][i] + this.halfExtents.y * AbsR[1][i] + this.halfExtents.z * AbsR[2][i];
            float rb = b.halfExtents.get(i);
            if (Math.abs(t.x * R[0][i] + t.y * R[1][i] + t.z * R[2][i]) > ra + rb) return false;
        }
        return true; // No separating axis found, OBBs are colliding
    }
}
