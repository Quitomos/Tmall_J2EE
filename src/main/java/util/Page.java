package util;

public class Page {
    private int start;
    private int count;
    private int total;
    String param;

    public Page(int start, int count) {
        this.start = start;
        this.count = count;
    }

    public boolean isHasPrevious() {
        return start != 0;
    }

    public boolean isHasNext() {
        return start != getLast();
    }

    public int getTotalPage() {
        if (0 == total % count && 0 != total) return total / count;
        return total / count + 1;
    }

    public int getLast() {
        int ans;
        if (0 == total % count) ans = total - count;
        else ans = total - total % count;
        return Math.max(ans, 0);
    }

    public int getCount() {
        return count;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getStart() {
        return start;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getTotal() {
        return total;
    }

    public void setParam(String param) {
        this.param = param;
    }

    public String getParam() {
        return param;
    }

    public void setStart(int start) {
        this.start = start;
    }
}
