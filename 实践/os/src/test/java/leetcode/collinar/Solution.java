package leetcode.collinar;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * TODO 文件说明
 *
 * @author errorfatal89@gmail.com
 * @date 2022/01/10
 */
public class Solution {

//    public static void main(String[] args) {
//        Solution solution = new Solution();
//        int[][] points = {{1, 1}, {2, 2}, {3, 3}};
////        int[][] points = {{0, 0}};
////        int[][] points = {{3, 3}, {1, 4}, {1, 1}, {2, 1}, {2, 2}};
//        System.out.println(solution.maxPoints(points));
//    }

    public int maxPoints(int[][] points) {
        if (points == null || points.length == 0) {
            return 0;
        }

        Point[] tmpPoints = new Point[points.length];
        for (int i = 0; i < points.length; i++) {
            if (points[i].length != 2) {
                continue;
            }

            Point point = new Point(points[i][0], points[i][1]);
            tmpPoints[i] = point;
        }

        FastCollinearPoints fastClient = new FastCollinearPoints(tmpPoints);
        fastClient.findSegments();
        return fastClient.maxPointNum;
    }

    private class FastCollinearPoints {
        private int maxPointNum = 0;
        private Point[] points;
        private List<LineSegment> segmentList;
        private final List<String> duplicateTest = new ArrayList<>();

        public FastCollinearPoints(Point[] points) {
            if (points == null) {
                throw new IllegalArgumentException();
            }

            this.points = points;
            segmentList = new ArrayList<>();
        }

        // the number of line segments
        public int numberOfSegments() {
            return maxPointNum;
        }

        public void findSegments() {
            if (points.length == 1) {
                maxPointNum = 1;
                return;
            }

            Point[] origin = Arrays.copyOf(points, points.length);
            for (int i = 0; i < points.length; i++) {
                // 对排序数组中的相同斜率进行搜索
                Arrays.sort(origin, points[i].slopeOrder());

                // 如果与原点有相同的斜率，那么就记录
                // 如果斜率相同的计数大于等于三，那就存在四个或更多点共线
                // 排序后取第一个元素为最小点和最后一个元素为最大点
                List<Point> foundPoints = new ArrayList<>();
                foundPoints.add(points[i]);
                Double startSlop = null;
                for (int j = 1; j < origin.length; j++) {
                    double slope = points[i].slopeTo(origin[j]);
                    if (startSlop == null) {
                        startSlop = slope;
                    }
                    // 检查斜率是否与origin数组中的第一个元素的斜率是否相同
                    if (startSlop == slope) {
                        foundPoints.add(origin[j]);
                    } else {
                        addSegment(foundPoints);
                        foundPoints.clear();
                        foundPoints.add(origin[i]);
                        foundPoints.add(origin[j]);
                        startSlop = slope;
                    }
                }

                addSegment(foundPoints);
            }
        }

        private void addSegment(List<Point> foundPoints) {
            if (foundPoints.size() >= 1 && foundPoints.size() > maxPointNum) {
                maxPointNum = foundPoints.size();
            }
        }
    }

    private class LineSegment {
        private final Point p;
        private final Point q;

        public LineSegment(Point p, Point q) {
            if (p == null || q == null) {
                throw new IllegalArgumentException("argument to LineSegment constructor is null");
            }
            if (p.equals(q)) {
                throw new IllegalArgumentException("both arguments to LineSegment constructor are the same point: " + p);
            }
            this.p = p;
            this.q = q;
        }

        public String toString() {
            return p + " -> " + q;
        }

        public int hashCode() {
            throw new UnsupportedOperationException("hashCode() is not supported");
        }
    }

    private class Point implements Comparable<Point> {

        private final int x;     // x-coordinate of this point
        private final int y;     // y-coordinate of this point

        public Point(int x, int y) {
            /* DO NOT MODIFY */
            this.x = x;
            this.y = y;
        }



        public double slopeTo(Point that) {
            if (this.x == that.x && this.y == that.y) {
                return Double.NEGATIVE_INFINITY;
            } else {
                if (this.y == that.y) {
                    return 0.0D;
                }

                if (this.x == that.x) {
                    return Double.POSITIVE_INFINITY;
                }
            }

            return (this.y - that.y) / (double)(this.x - that.x);
        }

        public int compareTo(Point that) {
            if (this.y > that.y) {
                return 1;
            } else if (this.y < that.y) {
                return -1;
            } else {
                if (this.x > that.x) {
                    return 1;
                } else if (this.x < that.x) {
                    return -1;
                } else {
                    return 0;
                }
            }
        }

        public Comparator<Point> slopeOrder() {
            return (o1, o2) -> {
                double thisSlop = slopeTo(o1);
                double thatSlop = slopeTo(o2);

                return thisSlop == thatSlop ? 0 : thisSlop - thatSlop > 0 ? 1 : -1 ;
            };
        }

        public String toString() {
            /* DO NOT MODIFY */
            return "(" + x + ", " + y + ")";
        }
    }
}
