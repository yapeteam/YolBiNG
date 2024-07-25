package cn.yapeteam.yolbi.font;

/**
 * @author TIMER_err
 */
public class HeightUtil {
    private static final char[] ups = new char[]{'b', 'd', 'f', 'h', 'k', 'l', 'i', 't'};//3/4
    private static final char[] middles = new char[]{'a', 'c', 'e', 'm', 'n', 'o', 'r', 's', 'u', 'v', 'w', 'x', 'z'};//2/4
    private static final char[] downs = new char[]{'g', 'j', 'p', 'q', 'y'};//3/4
    private static final char[] uppers = new char[]{'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'};//3/4

    private static boolean hasUp(String str) {
        for (char c : str.toCharArray())
            for (char up : ups)
                if (c == up) return true;
        return hasUpperCase(str);
    }

    private static boolean hasDown(String str) {
        for (char c : str.toCharArray())
            for (char down : downs)
                if (c == down) return true;
        return false;
    }

    private static boolean hasNotIn26(String str) {
        for (char c : str.toCharArray()) {
            boolean is = true;
            for (char up : ups)
                if (c == up) {
                    is = false;
                    break;
                }
            for (char down : downs)
                if (c == down) {
                    is = false;
                    break;
                }
            for (char middle : middles)
                if (c == middle) {
                    is = false;
                    break;
                }
            if (is) return true;
        }
        return false;
    }

    private static boolean allMiddle(String str) {
        for (char c : str.toCharArray()) {
            boolean is = false;
            for (char middle : middles) {
                if (c == middle) {
                    is = true;
                    break;
                }
            }
            if (!is) return false;
        }
        return true;
    }

    private static boolean hasUpperCase(String str) {
        for (char c : str.toCharArray())
            for (char upper : uppers)
                if (c == upper) return true;
        return false;
    }

    public static float getHeight(String str, float fullHeight) {
        int n0 = 0, n1 = 0, n2 = 0;
        for (char c : str.toCharArray())
            switch (getMostSuitable(c)) {
                case 0:
                    n0++;
                    break;
                case 1:
                    n1++;
                    break;
                case 2:
                    n2++;
                    break;
            }
        int max = Math.max(Math.max(n0, n1), n2);
        if (n0 == max)
            return fullHeight;
        if (n1 == max)
            return fullHeight / 3 * 2;
        return fullHeight / 3 * 4;
    }

    private static int getMostSuitable(char c) {
        String str = String.valueOf(c);
        if (allMiddle(str))
            return 1;//(fullHeight / 3 * 2);

        if ((hasUp(str) || hasUpperCase(str) || hasNotIn26(str)) && hasDown(str))
            return 2;//(fullHeight / 3 * 4);

        if (hasUp(str) || hasDown(str) || hasUpperCase(str))
            return 0;//(fullHeight);

        if (hasNotIn26(str))
            return 0;//(fullHeight);
        return 2;//(fullHeight / 3 * 4);
    }
}
