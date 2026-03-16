package grillo78.better_ships.phys;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

/**
 * Oriented Bounding Box (OBB) that extends AABB.
 *
 * Design notes:
 * - The inherited AABB fields represent the *world-space axis-aligned envelope*
 *   of the OBB. Use it for fast broad-phase rejection (cheap AABB test first,
 *   then precise OBB test only if needed).
 * - The OBB itself is defined by a center, three local half-extents, and a
 *   3×3 rotation matrix stored as three orthonormal axis columns (u, v, w).
 * - All inherited AABB methods (intersects, contains, etc.) work on the
 *   envelope and are intentionally NOT overridden — they remain valid for
 *   coarse checks. The precise OBB methods are additive.
 *
 * Usage example:
 *   OrientedBoundingBox obb = OrientedBoundingBox.create(center, 1, 2, 0.5, yawRad, pitchRad);
 *   if (obb.intersects(other.getEnvelope())) {       // fast AABB pre-check
 *       if (obb.intersectsOBB(other)) { ... }        // precise SAT check
 *   }
 */

public class OrientedBoundingBox extends AABB {

    // -----------------------------------------------------------------------
    // OBB state
    // -----------------------------------------------------------------------

    /** Center of the OBB in world space. */
    private final Vec3 center;

    /** Half-extents along each local axis (always >= 0). */
    private final double halfExtentU; // along axisU
    private final double halfExtentV; // along axisV
    private final double halfExtentW; // along axisW

    /**
     * Local axes of the OBB in world space (columns of the rotation matrix).
     * Must be orthonormal: unit length and mutually perpendicular.
     */
    private final Vec3 axisU; // local "right"
    private final Vec3 axisV; // local "up"
    private final Vec3 axisW; // local "forward"

    // -----------------------------------------------------------------------
    // Constructors — exactly two, cleanly chained
    // -----------------------------------------------------------------------

    /**
     * Public constructor. Computes the AABB envelope automatically.
     *
     * @param center      world-space center of the OBB
     * @param halfExtentU half-size along axisU (>= 0)
     * @param halfExtentV half-size along axisV (>= 0)
     * @param halfExtentW half-size along axisW (>= 0)
     * @param axisU       local X axis — must be unit length
     * @param axisV       local Y axis — must be unit length
     * @param axisW       local Z axis — must be unit length
     */
    public OrientedBoundingBox(Vec3 center,
                               double halfExtentU, double halfExtentV, double halfExtentW,
                               Vec3 axisU, Vec3 axisV, Vec3 axisW) {
        this(center, halfExtentU, halfExtentV, halfExtentW, axisU, axisV, axisW,
                envelopeMin(center, halfExtentU, halfExtentV, halfExtentW, axisU, axisV, axisW),
                envelopeMax(center, halfExtentU, halfExtentV, halfExtentW, axisU, axisV, axisW));
    }

    /**
     * Private base constructor — the ONLY one that calls super().
     * Receives precomputed envelope corners so the call to super() is straightforward.
     */
    private OrientedBoundingBox(Vec3 center,
                                double halfExtentU, double halfExtentV, double halfExtentW,
                                Vec3 axisU, Vec3 axisV, Vec3 axisW,
                                Vec3 envelopeMin, Vec3 envelopeMax) {
        super(envelopeMin.x, envelopeMin.y, envelopeMin.z,
                envelopeMax.x, envelopeMax.y, envelopeMax.z);
        this.center      = center;
        this.halfExtentU = halfExtentU;
        this.halfExtentV = halfExtentV;
        this.halfExtentW = halfExtentW;
        this.axisU = axisU;
        this.axisV = axisV;
        this.axisW = axisW;
    }

    // -----------------------------------------------------------------------
    // Envelope helpers — static so they can be called before super()
    // -----------------------------------------------------------------------

    /**
     * For each world axis the AABB half-extent equals the sum of absolute
     * projections of the three OBB local axes, weighted by their half-extents:
     *   ex = |hu*ux| + |hv*vx| + |hw*wx|   (and same for y, z)
     */
    private static Vec3 envelopeMin(Vec3 c, double hu, double hv, double hw,
                                    Vec3 u, Vec3 v, Vec3 w) {
        double ex = hu * Math.abs(u.x) + hv * Math.abs(v.x) + hw * Math.abs(w.x);
        double ey = hu * Math.abs(u.y) + hv * Math.abs(v.y) + hw * Math.abs(w.y);
        double ez = hu * Math.abs(u.z) + hv * Math.abs(v.z) + hw * Math.abs(w.z);
        return new Vec3(c.x - ex, c.y - ey, c.z - ez);
    }

    private static Vec3 envelopeMax(Vec3 c, double hu, double hv, double hw,
                                    Vec3 u, Vec3 v, Vec3 w) {
        double ex = hu * Math.abs(u.x) + hv * Math.abs(v.x) + hw * Math.abs(w.x);
        double ey = hu * Math.abs(u.y) + hv * Math.abs(v.y) + hw * Math.abs(w.y);
        double ez = hu * Math.abs(u.z) + hv * Math.abs(v.z) + hw * Math.abs(w.z);
        return new Vec3(c.x + ex, c.y + ey, c.z + ez);
    }

    // -----------------------------------------------------------------------
    // Factory methods
    // -----------------------------------------------------------------------

    /**
     * Creates an OBB from a center, half-extents, yaw and pitch angles.
     *
     * @param center   world-space center
     * @param halfX    half-size along local X before rotation
     * @param halfY    half-size along local Y
     * @param halfZ    half-size along local Z before rotation
     * @param yawRad   rotation around the world Y axis (radians)
     * @param pitchRad rotation around the yawed local X axis (radians)
     */
    public static OrientedBoundingBox create(Vec3 center,
                                             double halfX, double halfY, double halfZ,
                                             float yawRad, float pitchRad) {
        float cy = Mth.cos(yawRad),   sy = Mth.sin(yawRad);
        float cp = Mth.cos(pitchRad), sp = Mth.sin(pitchRad);

        Vec3 u = new Vec3( cy,        0,   sy);         // local right  (yaw only)
        Vec3 v = new Vec3( sy * sp,  cp,  -cy * sp);    // local up     (yaw + pitch)
        Vec3 w = new Vec3(-sy * cp,  sp,   cy * cp);    // local forward

        return new OrientedBoundingBox(center, halfX, halfY, halfZ, u, v, w);
    }

    /**
     * Wraps a plain AABB as an OBB with identity rotation.
     * Useful to run OBB code paths uniformly against both OBBs and AABBs.
     */
    public static OrientedBoundingBox fromAABB(AABB aabb) {
        return new OrientedBoundingBox(
                aabb.getCenter(),
                aabb.getXsize() / 2.0,
                aabb.getYsize() / 2.0,
                aabb.getZsize() / 2.0,
                new Vec3(1, 0, 0),
                new Vec3(0, 1, 0),
                new Vec3(0, 0, 1));
    }

    // -----------------------------------------------------------------------
    // Overridden AABB intersection / containment — now OBB-precise
    // -----------------------------------------------------------------------

    /**
     * OBB-precise test against a plain AABB.
     * Converts the AABB to an axis-aligned OBB and runs the SAT.
     * Replaces the inherited envelope-vs-envelope test.
     */
    @Override
    public boolean intersects(AABB other) {
        return intersectsOBB(fromAABB(other));
    }

    /**
     * OBB-precise test against a box defined by six raw coordinates.
     * Matches the signature of {@link AABB#intersects(double, double, double, double, double, double)}.
     */
    @Override
    public boolean intersects(double x1, double y1, double z1,
                              double x2, double y2, double z2) {
        return intersects(new AABB(x1, y1, z1, x2, y2, z2));
    }

    /**
     * OBB-precise test against a box defined by two corner vectors.
     * Matches the signature of {@link AABB#intersects(Vec3, Vec3)}.
     */
    @Override
    public boolean intersects(Vec3 min, Vec3 max) {
        return intersects(new AABB(min, max));
    }

    /**
     * OBB-precise point containment.
     * Replaces the inherited axis-aligned check.
     */
    @Override
    public boolean contains(Vec3 point) {
        return containsPoint(point);
    }

    /**
     * OBB-precise point containment by raw coordinates.
     * Matches the signature of {@link AABB#contains(double, double, double)}.
     */
    @Override
    public boolean contains(double x, double y, double z) {
        return containsPoint(new Vec3(x, y, z));
    }

    // -----------------------------------------------------------------------
    // Immutable transformations — same pattern as AABB
    // -----------------------------------------------------------------------

    /** Returns a new OBB translated by (dx, dy, dz). */
    public OrientedBoundingBox moveOBB(double dx, double dy, double dz) {
        return new OrientedBoundingBox(
                new Vec3(center.x + dx, center.y + dy, center.z + dz),
                halfExtentU, halfExtentV, halfExtentW,
                axisU, axisV, axisW);
    }

    /** Returns a new OBB rotated by an extra yaw angle around world Y. */
    public OrientedBoundingBox rotateYaw(float extraYawRad) {
        float cy = Mth.cos(extraYawRad), sy = Mth.sin(extraYawRad);
        return new OrientedBoundingBox(
                center,
                halfExtentU, halfExtentV, halfExtentW,
                rotateAroundY(axisU, cy, sy),
                rotateAroundY(axisV, cy, sy),
                rotateAroundY(axisW, cy, sy));
    }

    private static Vec3 rotateAroundY(Vec3 v, float cy, float sy) {
        return new Vec3(v.x * cy + v.z * sy, v.y, -v.x * sy + v.z * cy);
    }

    /**
     * Returns a new OBB with all three local axes rotated by the given unit quaternion.
     * The center and half-extents are preserved; only the orientation changes.
     *
     * <p>The quaternion must be unit length (|q| = 1).
     *
     * @param qx x component of the unit quaternion
     * @param qy y component of the unit quaternion
     * @param qz z component of the unit quaternion
     * @param qw w (scalar) component of the unit quaternion
     */
    public OrientedBoundingBox rotateByQuaternion(double qx, double qy, double qz, double qw) {
        return new OrientedBoundingBox(
                center,
                halfExtentU, halfExtentV, halfExtentW,
                rotateVecByQuaternion(axisU, qx, qy, qz, qw),
                rotateVecByQuaternion(axisV, qx, qy, qz, qw),
                rotateVecByQuaternion(axisW, qx, qy, qz, qw));
    }

    /**
     * Creates an OBB oriented by a unit quaternion instead of yaw/pitch angles.
     * The quaternion columns directly define the three local axes.
     *
     * @param center world-space center
     * @param halfX  half-size along local X
     * @param halfY  half-size along local Y
     * @param halfZ  half-size along local Z
     * @param qx     x component of the unit quaternion
     * @param qy     y component of the unit quaternion
     * @param qz     z component of the unit quaternion
     * @param qw     w (scalar) component of the unit quaternion
     */
    public static OrientedBoundingBox createFromQuaternion(Vec3 center,
                                                           double halfX, double halfY, double halfZ,
                                                           double qx, double qy, double qz, double qw) {
        Vec3 u = rotateVecByQuaternion(new Vec3(1, 0, 0), qx, qy, qz, qw);
        Vec3 v = rotateVecByQuaternion(new Vec3(0, 1, 0), qx, qy, qz, qw);
        Vec3 w = rotateVecByQuaternion(new Vec3(0, 0, 1), qx, qy, qz, qw);
        return new OrientedBoundingBox(center, halfX, halfY, halfZ, u, v, w);
    }

    /**
     * Builds a unit quaternion [qx, qy, qz, qw] from yaw then pitch.
     *
     * <p>q = q_yaw * q_pitch  (Hamilton product; yaw around world Y applied first,
     * pitch around the resulting local X applied second).
     *
     * <p>Derivation: q_yaw = (0, sin(y/2), 0, cos(y/2)),
     *                q_pitch = (sin(p/2), 0, 0, cos(p/2)).
     * Cross terms with zero components cancel, leaving the four values below.
     *
     * @param yawRad   rotation around world Y (radians)
     * @param pitchRad rotation around local X (radians)
     * @return unit quaternion as [qx, qy, qz, qw]
     */
    public static double[] quaternionFromYawPitch(float yawRad, float pitchRad) {
        double sy = Math.sin(yawRad   / 2.0), cy = Math.cos(yawRad   / 2.0);
        double sp = Math.sin(pitchRad / 2.0), cp = Math.cos(pitchRad / 2.0);
        return new double[] {
                cy * sp,   // qx
                sy * cp,   // qy
                -sy * sp,   // qz
                cy * cp    // qw
        };
    }

    /**
     * Rotates a vector by a unit quaternion using the optimised sandwich product:
     *   t  = 2 * (q.xyz x v)
     *   v' = v + q.w * t + (q.xyz x t)
     *
     * <p>Equivalent to v' = q * (0,v) * q^-1 but avoids intermediate quaternion allocations.
     */
    private static Vec3 rotateVecByQuaternion(Vec3 v, double qx, double qy, double qz, double qw) {
        double tx = 2.0 * (qy * v.z - qz * v.y);
        double ty = 2.0 * (qz * v.x - qx * v.z);
        double tz = 2.0 * (qx * v.y - qy * v.x);
        return new Vec3(
                v.x + qw * tx + (qy * tz - qz * ty),
                v.y + qw * ty + (qz * tx - qx * tz),
                v.z + qw * tz + (qx * ty - qy * tx));
    }

    // -----------------------------------------------------------------------
    // Precise OBB queries — Separating Axis Theorem (15 axes)
    // -----------------------------------------------------------------------

    /**
     * Precise OBB vs OBB intersection using the Separating Axis Theorem.
     * Tests 15 candidate axes: 3 from this OBB + 3 from other + 9 cross-products.
     *
     * @param other the other OBB
     * @return true if the two OBBs overlap
     */
    public boolean intersectsOBB(OrientedBoundingBox other) {
        Vec3 t = other.center.subtract(this.center);

        // R[i][j] = dot(this.axis_i, other.axis_j)
        double[][] R = {
                { dot(axisU, other.axisU), dot(axisU, other.axisV), dot(axisU, other.axisW) },
                { dot(axisV, other.axisU), dot(axisV, other.axisV), dot(axisV, other.axisW) },
                { dot(axisW, other.axisU), dot(axisW, other.axisV), dot(axisW, other.axisW) }
        };
        // Absolute values + epsilon to handle parallel-edge degenerate cases
        double[][] AbsR = new double[3][3];
        for (int i = 0; i < 3; i++)
            for (int j = 0; j < 3; j++)
                AbsR[i][j] = Math.abs(R[i][j]) + 1e-7;

        double[] eA = { halfExtentU,       halfExtentV,       halfExtentW       };
        double[] eB = { other.halfExtentU, other.halfExtentV, other.halfExtentW };

        // Translation projected onto this OBB's and the other OBB's local frames
        double[] tA = { dot(t, axisU),       dot(t, axisV),       dot(t, axisW)       };
        double[] tB = { dot(t, other.axisU), dot(t, other.axisV), dot(t, other.axisW) };

        // --- Axes A0, A1, A2 (this OBB's local axes) ---
        for (int i = 0; i < 3; i++) {
            double rb = eB[0]*AbsR[i][0] + eB[1]*AbsR[i][1] + eB[2]*AbsR[i][2];
            if (Math.abs(tA[i]) > eA[i] + rb) return false;
        }

        // --- Axes B0, B1, B2 (other OBB's local axes) ---
        for (int j = 0; j < 3; j++) {
            double ra = eA[0]*AbsR[0][j] + eA[1]*AbsR[1][j] + eA[2]*AbsR[2][j];
            if (Math.abs(tB[j]) > ra + eB[j]) return false;
        }

        // --- 9 cross-product axes A_i x B_j ---
        if (sat(tA[2]*R[1][0]-tA[1]*R[2][0], eA[1]*AbsR[2][0]+eA[2]*AbsR[1][0], eB[1]*AbsR[0][2]+eB[2]*AbsR[0][1])) return false; // A0xB0
        if (sat(tA[2]*R[1][1]-tA[1]*R[2][1], eA[1]*AbsR[2][1]+eA[2]*AbsR[1][1], eB[0]*AbsR[0][2]+eB[2]*AbsR[0][0])) return false; // A0xB1
        if (sat(tA[2]*R[1][2]-tA[1]*R[2][2], eA[1]*AbsR[2][2]+eA[2]*AbsR[1][2], eB[0]*AbsR[0][1]+eB[1]*AbsR[0][0])) return false; // A0xB2
        if (sat(tA[0]*R[2][0]-tA[2]*R[0][0], eA[0]*AbsR[2][0]+eA[2]*AbsR[0][0], eB[1]*AbsR[1][2]+eB[2]*AbsR[1][1])) return false; // A1xB0
        if (sat(tA[0]*R[2][1]-tA[2]*R[0][1], eA[0]*AbsR[2][1]+eA[2]*AbsR[0][1], eB[0]*AbsR[1][2]+eB[2]*AbsR[1][0])) return false; // A1xB1
        if (sat(tA[0]*R[2][2]-tA[2]*R[0][2], eA[0]*AbsR[2][2]+eA[2]*AbsR[0][2], eB[0]*AbsR[1][1]+eB[1]*AbsR[1][0])) return false; // A1xB2
        if (sat(tA[1]*R[0][0]-tA[0]*R[1][0], eA[0]*AbsR[1][0]+eA[1]*AbsR[0][0], eB[1]*AbsR[2][2]+eB[2]*AbsR[2][1])) return false; // A2xB0
        if (sat(tA[1]*R[0][1]-tA[0]*R[1][1], eA[0]*AbsR[1][1]+eA[1]*AbsR[0][1], eB[0]*AbsR[2][2]+eB[2]*AbsR[2][0])) return false; // A2xB1
        if (sat(tA[1]*R[0][2]-tA[0]*R[1][2], eA[0]*AbsR[1][2]+eA[1]*AbsR[0][2], eB[0]*AbsR[2][1]+eB[1]*AbsR[2][0])) return false; // A2xB2

        return true; // no separating axis found — OBBs overlap
    }

    /** Returns true when the given axis IS a separating axis (projections don't overlap). */
    private static boolean sat(double proj, double ra, double rb) {
        return Math.abs(proj) > ra + rb;
    }

    /**
     * Returns true if the given world-space point lies inside this OBB.
     * Projects the displacement from center onto each local axis and
     * checks it against the corresponding half-extent.
     */
    public boolean containsPoint(Vec3 point) {
        Vec3 d = point.subtract(center);
        return Math.abs(dot(d, axisU)) <= halfExtentU
                && Math.abs(dot(d, axisV)) <= halfExtentV
                && Math.abs(dot(d, axisW)) <= halfExtentW;
    }

    /**
     * Returns the closest point on (or inside) this OBB to the given world-space point.
     * Useful for distance queries and physics contact generation.
     */
    public Vec3 closestPoint(Vec3 point) {
        Vec3 d = point.subtract(center);
        return center
                .add(scale(axisU, Mth.clamp(dot(d, axisU), -halfExtentU, halfExtentU)))
                .add(scale(axisV, Mth.clamp(dot(d, axisV), -halfExtentV, halfExtentV)))
                .add(scale(axisW, Mth.clamp(dot(d, axisW), -halfExtentW, halfExtentW)));
    }

    /**
     * Squared distance from this OBB to a point (0 when the point is inside).
     */
    public double distanceToSqrOBB(Vec3 point) {
        return point.distanceToSqr(closestPoint(point));
    }

    // -----------------------------------------------------------------------
    // Accessors
    // -----------------------------------------------------------------------

    /** Returns this object upcast to AABB — the world-space envelope. */
    public AABB getEnvelope()      { return this; }
    public Vec3 getOBBCenter()     { return center; }
    public Vec3 getAxisU()         { return axisU; }
    public Vec3 getAxisV()         { return axisV; }
    public Vec3 getAxisW()         { return axisW; }
    public double getHalfExtentU() { return halfExtentU; }
    public double getHalfExtentV() { return halfExtentV; }
    public double getHalfExtentW() { return halfExtentW; }

    // -----------------------------------------------------------------------
    // Corner vertices
    // -----------------------------------------------------------------------

    /**
     * Returns all 8 corners of the OBB in world space.
     *
     * <p>Each corner is computed as:
     * <pre>  center ± hu*axisU ± hv*axisV ± hw*axisW</pre>
     * giving 2³ = 8 combinations. The order is consistent and stable:
     * <pre>
     *   index  su  sv  sw     meaning
     *     0    -1  -1  -1     min corner
     *     1    +1  -1  -1
     *     2    -1  +1  -1
     *     3    +1  +1  -1
     *     4    -1  -1  +1
     *     5    +1  -1  +1
     *     6    -1  +1  +1
     *     7    +1  +1  +1     max corner (of the local frame)
     * </pre>
     *
     * @return array of 8 {@link Vec3} corners in world space
     */
    public Vec3[] getCorners() {
        // Precompute the three scaled axes to avoid repeated multiplications.
        Vec3 eu = scale(axisU, halfExtentU); // half-vector along U
        Vec3 ev = scale(axisV, halfExtentV); // half-vector along V
        Vec3 ew = scale(axisW, halfExtentW); // half-vector along W

        return new Vec3[] {
                center.subtract(eu).subtract(ev).subtract(ew), // 0: ---
                center.add(eu)     .subtract(ev).subtract(ew), // 1: +--
                center.subtract(eu).add(ev)     .subtract(ew), // 2: -+-
                center.add(eu)     .add(ev)     .subtract(ew), // 3: ++-
                center.subtract(eu).subtract(ev).add(ew),      // 4: --+
                center.add(eu)     .subtract(ev).add(ew),      // 5: +-+
                center.subtract(eu).add(ev)     .add(ew),      // 6: -++
                center.add(eu)     .add(ev)     .add(ew),      // 7: +++
        };
    }

    /**
     * Returns the 12 edges of the OBB as pairs of corner indices into
     * the array returned by {@link #getCorners()}.
     *
     * <p>Useful for rendering: iterate the pairs and draw a line segment
     * between {@code corners[edge[i][0]]} and {@code corners[edge[i][1]]}.
     *
     * <p>The 12 edges cover the 3 axis-aligned directions of the local frame:
     * 4 edges along U, 4 along V, 4 along W.
     *
     * @return 12×2 array of corner-index pairs
     */
    public static int[][] getEdgeIndices() {
        return new int[][] {
                // 4 edges along axisU  (differ only in sw/sv sign)
                {0, 1}, {2, 3}, {4, 5}, {6, 7},
                // 4 edges along axisV  (differ only in su/sw sign)
                {0, 2}, {1, 3}, {4, 6}, {5, 7},
                // 4 edges along axisW  (differ only in su/sv sign)
                {0, 4}, {1, 5}, {2, 6}, {3, 7},
        };
    }

    /**
     * Returns the 6 faces of the OBB, each as an array of 4 corner indices
     * (a quad) in counter-clockwise order when viewed from outside.
     *
     * <p>Face order: -U, +U, -V, +V, -W, +W.
     *
     * @return 6×4 array of corner-index quads
     */
    public static int[][] getFaceIndices() {
        return new int[][] {
                {0, 2, 6, 4}, // -U face
                {1, 5, 7, 3}, // +U face
                {0, 4, 5, 1}, // -V face
                {2, 3, 7, 6}, // +V face
                {0, 1, 3, 2}, // -W face
                {4, 6, 7, 5}, // +W face
        };
    }

    // -----------------------------------------------------------------------
    // Overridden AABB mutation methods — return OrientedBoundingBox, not AABB
    // -----------------------------------------------------------------------
    //
    // Strategy: every method that changes the box geometry recomputes the OBB
    // by applying the same operation to the center/half-extents, keeping the
    // existing orientation axes unchanged unless the operation is inherently
    // axis-aligned (in which case the axes are reset to identity so the result
    // is still a valid OBB).
    //
    // Methods that modify individual AABB faces (setMinX, contract, inflate…)
    // are axis-aligned operations by definition, so the returned OBB has
    // identity axes and half-extents derived from the new AABB face positions.
    // -----------------------------------------------------------------------

    // --- face setters -------------------------------------------------------

    @Override
    public OrientedBoundingBox setMinX(double v) { return fromAABB(super.setMinX(v)); }
    @Override
    public OrientedBoundingBox setMinY(double v) { return fromAABB(super.setMinY(v)); }
    @Override
    public OrientedBoundingBox setMinZ(double v) { return fromAABB(super.setMinZ(v)); }
    @Override
    public OrientedBoundingBox setMaxX(double v) { return fromAABB(super.setMaxX(v)); }
    @Override
    public OrientedBoundingBox setMaxY(double v) { return fromAABB(super.setMaxY(v)); }
    @Override
    public OrientedBoundingBox setMaxZ(double v) { return fromAABB(super.setMaxZ(v)); }

    // --- axis min/max projections -------------------------------------------

    /**
     * Returns the envelope min along the given world axis.
     * Use {@link #supportMin(Direction.Axis)} for the exact OBB surface.
     */
    @Override
    public double min(Direction.Axis axis) {
        return axis.choose(this.minX, this.minY, this.minZ);
    }

    /**
     * Returns the envelope max along the given world axis.
     * Use {@link #supportMax(Direction.Axis)} for the exact OBB surface.
     */
    @Override
    public double max(Direction.Axis axis) {
        return axis.choose(this.maxX, this.maxY, this.maxZ);
    }

    /**
     * Returns the exact minimum extent of this OBB along the given world axis
     * using the support function:
     * <pre>  center·axis − (|hu*(axisU·axis)| + |hv*(axisV·axis)| + |hw*(axisW·axis)|)</pre>
     * For an axis-aligned OBB this equals {@link #min(Direction.Axis)}.
     */
    public double supportMin(Direction.Axis axis) {
        double c = dotAxis(center, axis);
        double r = halfExtentU * Math.abs(dotAxis(axisU, axis))
                + halfExtentV * Math.abs(dotAxis(axisV, axis))
                + halfExtentW * Math.abs(dotAxis(axisW, axis));
        return c - r;
    }

    /**
     * Returns the exact maximum extent of this OBB along the given world axis
     * using the support function:
     * <pre>  center·axis + (|hu*(axisU·axis)| + |hv*(axisV·axis)| + |hw*(axisW·axis)|)</pre>
     * For an axis-aligned OBB this equals {@link #max(Direction.Axis)}.
     */
    public double supportMax(Direction.Axis axis) {
        double c = dotAxis(center, axis);
        double r = halfExtentU * Math.abs(dotAxis(axisU, axis))
                + halfExtentV * Math.abs(dotAxis(axisV, axis))
                + halfExtentW * Math.abs(dotAxis(axisW, axis));
        return c + r;
    }

    /** Projects a Vec3 onto a world axis without allocating a unit-vector object. */
    private static double dotAxis(Vec3 v, Direction.Axis axis) {
        return switch (axis) { case X -> v.x; case Y -> v.y; case Z -> v.z; };
    }

    // --- size queries -------------------------------------------------------

    /**
     * Returns the average of the three local-frame side lengths (2*halfExtent per axis),
     * matching the semantic of {@link AABB#getSize()} but measured in OBB space.
     */
    @Override
    public double getSize() {
        return (2.0 * halfExtentU + 2.0 * halfExtentV + 2.0 * halfExtentW) / 3.0;
    }

    /** Local-frame width (along axisU). */
    @Override
    public double getXsize() { return 2.0 * halfExtentU; }

    /** Local-frame height (along axisV). */
    @Override
    public double getYsize() { return 2.0 * halfExtentV; }

    /** Local-frame depth (along axisW). */
    @Override
    public double getZsize() { return 2.0 * halfExtentW; }

    // --- center -------------------------------------------------------------

    /** Returns the OBB center (same as {@link #getOBBCenter()}). */
    @Override
    public Vec3 getCenter() { return center; }

    // --- NaN check ----------------------------------------------------------

    @Override
    public boolean hasNaN() {
        return center.x != center.x || center.y != center.y || center.z != center.z
                || halfExtentU != halfExtentU || halfExtentV != halfExtentV || halfExtentW != halfExtentW
                || axisU.x != axisU.x || axisV.x != axisV.x || axisW.x != axisW.x;
    }

    // --- distance -----------------------------------------------------------

    /**
     * Squared distance from this OBB to a point (0 if the point is inside).
     * Overrides the AABB envelope-based version with the precise OBB computation.
     */
    @Override
    public double distanceToSqr(Vec3 point) { return distanceToSqrOBB(point); }

    // --- translation --------------------------------------------------------

    /**
     * Translates this OBB by (dx, dy, dz), preserving orientation.
     * Overrides {@link AABB#move(double, double, double)}.
     */
    @Override
    public OrientedBoundingBox move(double dx, double dy, double dz) {
        return moveOBB(dx, dy, dz);
    }

    @Override
    public OrientedBoundingBox move(BlockPos pos) {
        return moveOBB(pos.getX(), pos.getY(), pos.getZ());
    }

    @Override
    public OrientedBoundingBox move(Vec3 vec) {
        return moveOBB(vec.x, vec.y, vec.z);
    }

    // --- inflate / deflate / contract / expandTowards -----------------------
    //
    // These operations scale the OBB uniformly (inflate/deflate) or
    // directionally (expandTowards/contract). Directional variants expand
    // along the WORLD direction supplied, not the local frame — matching
    // the AABB contract and they reset orientation to identity. Uniform
    // variants scale the half-extents and keep the orientation.

    /**
     * Inflates the OBB uniformly when all three deltas are equal; otherwise
     * falls back to an axis-aligned OBB wrapping the inflated envelope.
     */
    @Override
    public OrientedBoundingBox inflate(double px, double py, double pz) {
        if (px == py && py == pz) {
            // Uniform — scale all half-extents, keep axes
            return new OrientedBoundingBox(center,
                    halfExtentU + px, halfExtentV + py, halfExtentW + pz,
                    axisU, axisV, axisW);
        }
        // Non-uniform inflate is axis-aligned by nature
        return fromAABB(super.inflate(px, py, pz));
    }

    /** Uniform inflate — scales all half-extents equally, keeps orientation. */
    @Override
    public OrientedBoundingBox inflate(double value) {
        return new OrientedBoundingBox(center,
                halfExtentU + value, halfExtentV + value, halfExtentW + value,
                axisU, axisV, axisW);
    }

    /** Uniform deflate — shrinks all half-extents equally, keeps orientation. */
    @Override
    public OrientedBoundingBox deflate(double value) {
        return inflate(-value);
    }

    @Override
    public OrientedBoundingBox deflate(double px, double py, double pz) {
        return inflate(-px, -py, -pz);
    }

    /**
     * Expands the OBB towards the given world-space vector.
     * The result is axis-aligned (identity rotation) because the expansion
     * direction is in world space, not local space.
     */
    @Override
    public OrientedBoundingBox expandTowards(double dx, double dy, double dz) {
        return fromAABB(super.expandTowards(dx, dy, dz));
    }

    @Override
    public OrientedBoundingBox expandTowards(Vec3 vec) {
        return expandTowards(vec.x, vec.y, vec.z);
    }

    /**
     * Contracts the OBB along world-space axes.
     * Returns an axis-aligned OBB (identity rotation).
     */
    @Override
    public OrientedBoundingBox contract(double px, double py, double pz) {
        return fromAABB(super.contract(px, py, pz));
    }

    // --- set operations on two boxes ----------------------------------------

    /**
     * Returns an axis-aligned OBB that is the intersection of the two envelopes.
     * For precise OBB intersection use {@link #intersectsOBB} instead.
     */
    @Override
    public OrientedBoundingBox intersect(AABB other) {
        return fromAABB(super.intersect(other));
    }

    /**
     * Returns an axis-aligned OBB that tightly contains both boxes (union of envelopes).
     */
    @Override
    public OrientedBoundingBox minmax(AABB other) {
        return fromAABB(super.minmax(other));
    }

    // --- ray clip -----------------------------------------------------------

    /**
     * Clips a ray against this OBB using the precise local-frame slab method.
     * Overrides the AABB envelope-based version.
     *
     * <p>The slab test projects the ray onto each local axis and intersects
     * the resulting 1-D intervals, then combines them to find the entry point.
     */
    @Override
    public java.util.Optional<Vec3> clip(Vec3 from, Vec3 to) {
        Vec3 d = to.subtract(from);           // ray direction (not normalised)
        Vec3 oc = from.subtract(center);      // ray origin relative to OBB center

        double tMin = 0.0;
        double tMax = 1.0;                    // parametric range [0,1] along the segment

        Vec3[] axes    = { axisU, axisV, axisW };
        double[] extents = { halfExtentU, halfExtentV, halfExtentW };

        for (int i = 0; i < 3; i++) {
            double e = dot(axes[i], oc);      // origin projected onto axis
            double f = dot(axes[i], d);       // direction projected onto axis

            if (Math.abs(f) > 1.0E-7) {
                double t1 = (-extents[i] - e) / f;
                double t2 = ( extents[i] - e) / f;
                if (t1 > t2) { double tmp = t1; t1 = t2; t2 = tmp; }
                tMin = Math.max(tMin, t1);
                tMax = Math.min(tMax, t2);
                if (tMin > tMax) return java.util.Optional.empty();
            } else if (Math.abs(e) > extents[i]) {
                // Ray is parallel to slab and outside it
                return java.util.Optional.empty();
            }
        }

        return java.util.Optional.of(from.add(d.scale(tMin)));
    }

    // -----------------------------------------------------------------------
    // Private math helpers
    // -----------------------------------------------------------------------

    private static double dot(Vec3 a, Vec3 b) {
        return a.x * b.x + a.y * b.y + a.z * b.z;
    }

    private static Vec3 scale(Vec3 v, double s) {
        return new Vec3(v.x * s, v.y * s, v.z * s);
    }

    // -----------------------------------------------------------------------
    // Object overrides
    // -----------------------------------------------------------------------

    @Override
    public String toString() {
        return String.format(
                "OBB[center=%s, extents=(%.3f,%.3f,%.3f), u=%s, v=%s, w=%s, envelope=%s]",
                center, halfExtentU, halfExtentV, halfExtentW,
                axisU, axisV, axisW, super.toString());
    }
}