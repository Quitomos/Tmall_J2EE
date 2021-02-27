package util;

import java.sql.Timestamp;
import java.util.Date;

public class DateUtil {
    public static Timestamp date2timestamp(Date d) {
        if (null == d) return null;
        return new Timestamp(d.getTime());
    }

    public static Date timestamp2date(Timestamp t) {
        if (null == t) return null;
        return new Date(t.getTime());
    }
}
