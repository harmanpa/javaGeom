package pl.pateman.dynamicaabbtree;

import org.joml.*;

import java.util.*;
import java.util.function.Predicate;

import static java.lang.Math.max;
import java.util.Random;
import java.util.function.BiPredicate;

/**
 * Created by pateman.
 *
 * @param <T>
 */
public final class AABBTree<T extends Boundable & Identifiable> {

    public static final float DEFAULT_FAT_AABB_MARGIN = 0.2f;

    private final List<AABBTreeNode<T>> nodes;
    private final AABBTreeHeuristicFunction<T> insertionHeuristicFunction;
    private final AABBOverlapFilter<T> defaultAABBOverlapFilter;
    private final CollisionFilter<T> defaultCollisionFilter;
    private final Map<AABBTreeObject<T>, Integer> objects;
    private final Deque<Integer> freeNodes;
    private final FrustumIntersection frustumIntersection;
    private final RayAabIntersection rayIntersection;

    private int root;
    private float fatAABBMargin;

    public AABBTree() {
        this(new AreaAABBHeuristicFunction<>(), DEFAULT_FAT_AABB_MARGIN);
    }

    public AABBTree(AABBTreeHeuristicFunction<T> insertionHeuristicFunction, float fatAABBMargin) {
        nodes = new ArrayList<>();
        root = AABBTreeNode.INVALID_NODE_INDEX;
        this.insertionHeuristicFunction = insertionHeuristicFunction;
        if (this.insertionHeuristicFunction == null) {
            throw new IllegalArgumentException("A valid insertion heuristic function is required");
        }
        objects = new HashMap<>();
        freeNodes = new ArrayDeque<>();
        defaultAABBOverlapFilter = new DefaultAABBOverlapFilter<>();
        defaultCollisionFilter = new DefaultCollisionFilter<>();
        this.fatAABBMargin = fatAABBMargin;

        frustumIntersection = new FrustumIntersection();
        rayIntersection = new RayAabIntersection();
    }

    private AABBTreeNode<T> allocateNode() {
        if (freeNodes.isEmpty()) {
            return new AABBTreeNode<>();
        }
        Integer freeIndex = freeNodes.pop();

        AABBTreeNode<T> aabbTreeNode = nodes.get(freeIndex);
        aabbTreeNode.resetForReuse();
        return aabbTreeNode;
    }

    private void deallocateNode(AABBTreeNode<T> node) {
        freeNodes.offer(node.getIndex());
    }

    private int addNodeAndGetIndex(AABBTreeNode<T> node) {
        if (node.getIndex() != AABBTreeNode.INVALID_NODE_INDEX) {
            return node.getIndex();
        }
        nodes.add(node);
        int idx = nodes.size() - 1;
        node.setIndex(idx);
        return idx;
    }

    private AABBTreeNode<T> createLeafNode(T object) {
        AABBTreeNode<T> leafNode = allocateNode();
        leafNode.setData(object);
        leafNode.computeAABBWithMargin(fatAABBMargin);
        return leafNode;
    }

    private void moveNodeToParent(AABBTreeNode<T> node, int newParentIndex) {
        int oldParentIndex = node.getParent();
        if (oldParentIndex != AABBTreeNode.INVALID_NODE_INDEX) {
            getNodeAt(oldParentIndex).replaceChild(node.getIndex(), newParentIndex);
        }
        node.setParent(newParentIndex);
    }

    private AABBTreeNode<T> createBranchNode(AABBTreeNode<T> childA, AABBTreeNode<T> childB) {
        AABBTreeNode<T> branchNode = allocateNode();
        branchNode.assignChildren(childA.getIndex(), childB.getIndex());

        int branchNodeIndex = addNodeAndGetIndex(branchNode);
        moveNodeToParent(childA, branchNodeIndex);
        moveNodeToParent(childB, branchNodeIndex);

        return branchNode;
    }

    private AABBTreeNode<T> getNodeAt(int index) {
        return nodes.get(index);
    }

    private AABBTreeNode<T> balanceRight(AABBTreeNode<T> node, AABBTreeNode<T> left, AABBTreeNode<T> right) {
        AABBTreeNode<T> rightLeftChild = getNodeAt(right.getLeftChild());
        AABBTreeNode<T> rightRightChild = getNodeAt(right.getRightChild());

        right.assignChild(AABBTreeNode.LEFT_CHILD, node.getIndex());
        right.setParent(node.getParent());
        node.setParent(right.getIndex());

        if (right.getParent() != AABBTreeNode.INVALID_NODE_INDEX) {
            AABBTreeNode<T> rightParent = getNodeAt(right.getParent());
            if (rightParent.getLeftChild() == node.getIndex()) {
                rightParent.assignChild(AABBTreeNode.LEFT_CHILD, right.getIndex());
            } else {
                rightParent.assignChild(AABBTreeNode.RIGHT_CHILD, right.getIndex());
            }
        } else {
            root = right.getIndex();
        }

        if (rightLeftChild.getHeight() > rightRightChild.getHeight()) {
            right.assignChild(AABBTreeNode.RIGHT_CHILD, rightLeftChild.getIndex());
            node.assignChild(AABBTreeNode.RIGHT_CHILD, rightRightChild.getIndex());
            rightRightChild.setParent(node.getIndex());
            left.getAABB().union(rightRightChild.getAABB(), node.getAABB());
            node.getAABB().union(rightLeftChild.getAABB(), right.getAABB());
            node.setHeight(1 + max(left.getHeight(), rightRightChild.getHeight()));
            right.setHeight(1 + max(node.getHeight(), rightLeftChild.getHeight()));
        } else {
            right.assignChild(AABBTreeNode.RIGHT_CHILD, rightRightChild.getIndex());
            node.assignChild(AABBTreeNode.RIGHT_CHILD, rightLeftChild.getIndex());
            rightLeftChild.setParent(node.getIndex());
            left.getAABB().union(rightLeftChild.getAABB(), node.getAABB());
            node.getAABB().union(rightRightChild.getAABB(), right.getAABB());
            node.setHeight(1 + max(left.getHeight(), rightLeftChild.getHeight()));
            right.setHeight(1 + max(node.getHeight(), rightRightChild.getHeight()));
        }

        return right;
    }

    private AABBTreeNode<T> balanceLeft(AABBTreeNode<T> node, AABBTreeNode<T> left, AABBTreeNode<T> right) {
        AABBTreeNode<T> leftLeftChild = getNodeAt(left.getLeftChild());
        AABBTreeNode<T> leftRightChild = getNodeAt(left.getRightChild());

        left.assignChild(AABBTreeNode.LEFT_CHILD, node.getIndex());
        left.setParent(node.getParent());
        node.setParent(left.getIndex());

        if (left.getParent() != AABBTreeNode.INVALID_NODE_INDEX) {
            AABBTreeNode<T> leftParent = getNodeAt(left.getParent());
            if (leftParent.getLeftChild() == node.getIndex()) {
                leftParent.assignChild(AABBTreeNode.LEFT_CHILD, left.getIndex());
            } else {
                leftParent.assignChild(AABBTreeNode.RIGHT_CHILD, left.getIndex());
            }
        } else {
            root = left.getIndex();
        }

        if (leftLeftChild.getHeight() > leftRightChild.getHeight()) {
            left.assignChild(AABBTreeNode.RIGHT_CHILD, leftLeftChild.getIndex());
            node.assignChild(AABBTreeNode.LEFT_CHILD, leftRightChild.getIndex());
            leftRightChild.setParent(node.getIndex());
            right.getAABB().union(leftRightChild.getAABB(), node.getAABB());
            node.getAABB().union(leftLeftChild.getAABB(), left.getAABB());
            node.setHeight(1 + max(right.getHeight(), leftRightChild.getHeight()));
            left.setHeight(1 + max(node.getHeight(), leftLeftChild.getHeight()));
        } else {
            left.assignChild(AABBTreeNode.RIGHT_CHILD, leftRightChild.getIndex());
            node.assignChild(AABBTreeNode.LEFT_CHILD, leftLeftChild.getIndex());
            leftLeftChild.setParent(node.getIndex());
            right.getAABB().union(leftLeftChild.getAABB(), node.getAABB());
            node.getAABB().union(leftRightChild.getAABB(), left.getAABB());
            node.setHeight(1 + max(right.getHeight(), leftLeftChild.getHeight()));
            left.setHeight(1 + max(node.getHeight(), leftRightChild.getHeight()));
        }

        return left;
    }

    private AABBTreeNode<T> balanceNode(AABBTreeNode<T> node) {
        if (node.isLeaf() || node.getHeight() < 2) {
            return node;
        }

        AABBTreeNode<T> left = getNodeAt(node.getLeftChild());
        AABBTreeNode<T> right = getNodeAt(node.getRightChild());

        int balance = right.getHeight() - left.getHeight();

        if (balance > 1) {
            return balanceRight(node, left, right);
        }
        if (balance < -1) {
            return balanceLeft(node, left, right);
        }
        return node;
    }

    private void syncUpHierarchy(AABBTreeNode<T> startingPoint) {
        AABBTreeNode<T> node = startingPoint.getParent() == AABBTreeNode.INVALID_NODE_INDEX ? null : getNodeAt(startingPoint.getParent());
        while (node != null && node.getIndex() != AABBTreeNode.INVALID_NODE_INDEX) {
            node = balanceNode(node);

            AABBTreeNode<T> left = getNodeAt(node.getLeftChild());
            AABBTreeNode<T> right = getNodeAt(node.getRightChild());

            node.setHeight(1 + max(left.getHeight(), right.getHeight()));
            left.getAABB().union(right.getAABB(), node.getAABB());

            node = node.getParent() == AABBTreeNode.INVALID_NODE_INDEX ? null : getNodeAt(node.getParent());
        }
    }

    private void insertNode(int node) {
        AABBTreeNode<T> nodeToAdd = getNodeAt(node);
        AABBTreeNode<T> parentNode = getNodeAt(root);

        AABBf nodeToAddAABB = nodeToAdd.getAABB();

        while (!parentNode.isLeaf()) {
            AABBTreeNode<T> leftChild = getNodeAt(parentNode.getLeftChild());
            AABBTreeNode<T> rightChild = getNodeAt(parentNode.getRightChild());
            AABBf aabbA = leftChild.getAABB();
            AABBf aabbB = rightChild.getAABB();

            AABBTreeHeuristicFunction.HeuristicResult heuristicResult = insertionHeuristicFunction
                    .getInsertionHeuristic(aabbA, aabbB, nodeToAdd.getData(), nodeToAddAABB);
            parentNode = AABBTreeHeuristicFunction.HeuristicResult.LEFT.equals(heuristicResult) ? leftChild : rightChild;
        }

        int oldParentIndex = parentNode.getParent();
        AABBTreeNode<T> newParent = createBranchNode(nodeToAdd, parentNode);
        newParent.setParent(oldParentIndex);
        newParent.setHeight(parentNode.getHeight() + 1);

        if (oldParentIndex == AABBTreeNode.INVALID_NODE_INDEX) {
            root = newParent.getIndex();
            newParent.setParent(AABBTreeNode.INVALID_NODE_INDEX);
        }

        syncUpHierarchy(nodeToAdd);
    }

    private void detectCollisionPairsWithNode(AABBTreeNode<T> nodeToTest, CollisionFilter<T> filter, Set<CollisionPair<T>> alreadyTested,
            List<CollisionPair<T>> result) {
        Deque<Integer> stack = new ArrayDeque<>();
        stack.offer(root);
        AABBf overlapWith = nodeToTest.getAABB();

        while (!stack.isEmpty()) {
            Integer nodeIndex = stack.pop();
            if (nodeIndex == AABBTreeNode.INVALID_NODE_INDEX) {
                continue;
            }

            AABBTreeNode<T> node = getNodeAt(nodeIndex);
            AABBf nodeAABB = node.getAABB();
            if (nodeAABB.testAABB(overlapWith)) {
                if (node.isLeaf() && node.getIndex() != nodeToTest.getIndex()) {
                    T nodeData = node.getData();
                    T testedData = nodeToTest.getData();
                    CollisionPair<T> collisionPair = new CollisionPair<>(testedData, nodeData);
                    if (filter.test(testedData, nodeData) && !alreadyTested.contains(collisionPair)) {
                        alreadyTested.add(collisionPair);
                        result.add(collisionPair);
                    }
                } else {
                    stack.offer(node.getLeftChild());
                    stack.offer(node.getRightChild());
                }
            }
        }
    }

    public void add(T object) {
        if (contains(object)) {
            update(object);
            return;
        }

        AABBTreeNode<T> leafNode = createLeafNode(object);

        int newNodeIndex = addNodeAndGetIndex(leafNode);
        if (root == AABBTreeNode.INVALID_NODE_INDEX) {
            root = newNodeIndex;
        } else {
            insertNode(newNodeIndex);
        }

        objects.put(AABBTreeObject.create(object), newNodeIndex);
    }

    public void clear() {
        nodes.clear();
        objects.clear();
        freeNodes.clear();
        root = AABBTreeNode.INVALID_NODE_INDEX;
    }

    public void update(T object) {
        if (!contains(object)) {
            add(object);
            return;
        }

        remove(object);
        add(object);
    }

    public void remove(T object) {
        Integer objectNodeIndex = objects.remove(AABBTreeObject.create(object));
        if (root == AABBTreeNode.INVALID_NODE_INDEX || objectNodeIndex == null) {
            return;
        }

        if (objectNodeIndex == root) {
            deallocateNode(getNodeAt(objectNodeIndex));
            root = AABBTreeNode.INVALID_NODE_INDEX;
            return;
        }

        AABBTreeNode<T> node = getNodeAt(objectNodeIndex);
        AABBTreeNode<T> nodeParent = node.getParent() == AABBTreeNode.INVALID_NODE_INDEX ? null : getNodeAt(node.getParent());
        AABBTreeNode<T> nodeGrandparent = nodeParent == null || nodeParent.getParent() == AABBTreeNode.INVALID_NODE_INDEX ? null : getNodeAt(nodeParent.getParent());
        AABBTreeNode<T> nodeSibling = null;
        if (nodeParent != null) {
            nodeSibling = nodeParent.getLeftChild() == objectNodeIndex ? getNodeAt(nodeParent.getRightChild()) : getNodeAt(nodeParent.getLeftChild());
            deallocateNode(nodeParent);
        }
        deallocateNode(node);

        if (nodeGrandparent == null) {
            root = nodeSibling.getIndex();
            nodeSibling.setParent(AABBTreeNode.INVALID_NODE_INDEX);
            return;
        }

        if (nodeGrandparent.getLeftChild() == nodeParent.getIndex()) {
            nodeGrandparent.assignChild(AABBTreeNode.LEFT_CHILD, nodeSibling.getIndex());
        } else {
            nodeGrandparent.assignChild(AABBTreeNode.RIGHT_CHILD, nodeSibling.getIndex());
        }

        nodeSibling.setParent(nodeGrandparent.getIndex());
        syncUpHierarchy(nodeGrandparent);
    }

    public void findTreeClosest(AABBTree<T> other, List<CollisionPair<T>> result) {
        traverseTreePair(this, other, (a, b) -> true, new DistanceComparator(), result);
    }

    public void detectTreeCollisions(AABBTree<T> other, List<CollisionPair<T>> result) {
        traverseTreePair(this, other, (a, b) -> a.testAABB(b), (a, b) -> 0, result);
    }

    public void detectOverlaps(AABBf overlapWith, List<T> result) {
        detectOverlaps(overlapWith, defaultAABBOverlapFilter, result);
    }

    public void detectOverlaps(AABBf overlapWith, AABBOverlapFilter<T> filter, List<T> result) {
        traverseTree(aabb -> aabb.testAABB(overlapWith), filter, result);
    }

    public void detectCollisionPairs(List<CollisionPair<T>> result) {
        detectCollisionPairs(defaultCollisionFilter, result);
    }

    public void detectCollisionPairs(CollisionFilter<T> filter, List<CollisionPair<T>> result) {
        result.clear();
        if (root == AABBTreeNode.INVALID_NODE_INDEX) {
            return;
        }

        Set<CollisionPair<T>> alreadyTested = new HashSet<>();

        for (int i = 0; i < nodes.size(); i++) {
            AABBTreeNode<T> testedNode = nodes.get(i);
            if (!testedNode.isLeaf()) {
                continue;
            }

            detectCollisionPairsWithNode(testedNode, filter, alreadyTested, result);
        }
    }

    public void detectInFrustum(Matrix4fc worldViewProjection, List<T> result) {
        detectInFrustum(worldViewProjection, defaultAABBOverlapFilter, result);
    }

    public void detectInFrustum(Matrix4fc worldViewProjection, AABBOverlapFilter<T> filter, List<T> result) {
        frustumIntersection.set(worldViewProjection, false);
        traverseTree(aabb -> frustumIntersection.testAab(aabb.minX, aabb.minY, aabb.minZ, aabb.maxX, aabb.maxY, aabb.maxZ), filter, result);
    }

    public void detectRayIntersection(Rayf ray, List<T> result) {
        detectRayIntersection(ray, defaultAABBOverlapFilter, result);
    }

    public void detectRayIntersection(Rayf ray, AABBOverlapFilter<T> filter, List<T> result) {
        rayIntersection.set(ray.oX, ray.oY, ray.oZ, ray.dX, ray.dY, ray.dZ);
        traverseTree(aabb -> rayIntersection.test(aabb.minX, aabb.minY, aabb.minZ, aabb.maxX, aabb.maxY, aabb.maxZ), filter, result);
    }

    private static <T extends Boundable & Identifiable> void traverseTreePair(
            AABBTree<T> treeA,
            AABBTree<T> treeB,
            BiPredicate<AABBf, AABBf> nodesTest,
            Comparator<AABBf[]> comparator,
            List<CollisionPair<T>> result) {
        if (treeA.root == AABBTreeNode.INVALID_NODE_INDEX || treeB.root == AABBTreeNode.INVALID_NODE_INDEX) {
            return;
        }
        Random random = new Random();
        Deque<int[]> stack = new ArrayDeque<>();
        stack.offer(new int[]{treeA.root, treeB.root});

        while (!stack.isEmpty()) {
            int[] nodeIndices = stack.pop();
            if (nodeIndices[0] == AABBTreeNode.INVALID_NODE_INDEX || nodeIndices[1] == AABBTreeNode.INVALID_NODE_INDEX) {
                continue;
            }
            AABBTreeNode<T> nodeA = treeA.getNodeAt(nodeIndices[0]);
            AABBf nodeAABBA = nodeA.getAABB();
            AABBTreeNode<T> nodeB = treeB.getNodeAt(nodeIndices[1]);
            AABBf nodeAABBB = nodeB.getAABB();
            if (nodesTest.test(nodeAABBA, nodeAABBB)) {
                if (nodeA.isLeaf()) {
                    if (nodeB.isLeaf()) {
                        result.add(new CollisionPair(nodeA.getData(), nodeB.getData()));
                    } else {
                        split(false, getNodeChildren(nodeB), treeB, nodeIndices[0], nodeAABBA, comparator, stack);
                    }
                } else if (nodeB.isLeaf()) {
                    split(true, getNodeChildren(nodeA), treeA, nodeIndices[1], nodeAABBB, comparator, stack);
                } else {
                    if (random.nextBoolean()) {
                        split(true, getNodeChildren(nodeA), treeA, nodeIndices[1], nodeAABBB, comparator, stack);
                    } else {
                        split(false, getNodeChildren(nodeB), treeB, nodeIndices[0], nodeAABBA, comparator, stack);
                    }
                }
            }
        }
    }

    private static <T extends Boundable & Identifiable> boolean split(
            boolean splitA, int[] children,
            AABBTree<T> treeSplit, int otherIndex, AABBf otherAABB,
            Comparator<AABBf[]> comparator, Deque<int[]> stack) {
        switch (children.length) {
            case 2:
                switch (comparator.compare(
                        new AABBf[]{splitA ? treeSplit.getNodeAt(children[0]).getAABB() : otherAABB, splitA ? otherAABB : treeSplit.getNodeAt(children[0]).getAABB()},
                        new AABBf[]{splitA ? treeSplit.getNodeAt(children[1]).getAABB() : otherAABB, splitA ? otherAABB : treeSplit.getNodeAt(children[1]).getAABB()})) {
                    case -1:
                        stack.offer(new int[]{splitA ? children[0] : otherIndex, splitA ? otherIndex : children[0]});
                        break;
                    case 1:
                        stack.offer(new int[]{splitA ? children[1] : otherIndex, splitA ? otherIndex : children[1]});
                        break;
                    default:
                        stack.offer(new int[]{splitA ? children[0] : otherIndex, splitA ? otherIndex : children[0]});
                        stack.offer(new int[]{splitA ? children[1] : otherIndex, splitA ? otherIndex : children[1]});
                }
                return true;
            case 1:
                stack.offer(new int[]{splitA ? children[0] : otherIndex, splitA ? otherIndex : children[0]});
                break;
            default:
            // nothing
        }
        return false;
    }

    private static <T extends Boundable & Identifiable> int[] getNodeChildren(AABBTreeNode<T> node) {
        int left = node.getLeftChild();
        int right = node.getRightChild();
        if (left == AABBTreeNode.INVALID_NODE_INDEX) {
            if (right == AABBTreeNode.INVALID_NODE_INDEX) {
                return new int[0];
            } else {
                return new int[]{right};
            }
        } else if (right == AABBTreeNode.INVALID_NODE_INDEX) {
            return new int[]{left};
        } else {
            return new int[]{left, right};
        }
    }

    private void traverseTree(Predicate<AABBf> nodeTest, AABBOverlapFilter<T> filter, List<T> result) {
        result.clear();
        if (root == AABBTreeNode.INVALID_NODE_INDEX) {
            return;
        }

        Deque<Integer> stack = new ArrayDeque<>();
        stack.offer(root);

        while (!stack.isEmpty()) {
            Integer nodeIndex = stack.pop();
            if (nodeIndex == AABBTreeNode.INVALID_NODE_INDEX) {
                continue;
            }

            AABBTreeNode<T> node = getNodeAt(nodeIndex);
            AABBf nodeAABB = node.getAABB();
            if (nodeTest.test(nodeAABB)) {
                if (node.isLeaf()) {
                    T nodeData = node.getData();
                    if (filter.test(nodeData)) {
                        result.add(nodeData);
                    }
                } else {
                    stack.offer(node.getLeftChild());
                    stack.offer(node.getRightChild());
                }
            }
        }
    }

    public boolean contains(T object) {
        return objects.containsKey(AABBTreeObject.create(object));
    }

    public int size() {
        return objects.size();
    }

    List<AABBTreeNode<T>> getNodes() {
        return nodes;
    }

    int getRoot() {
        return root;
    }

    Deque<Integer> getFreeNodes() {
        return freeNodes;
    }
}
