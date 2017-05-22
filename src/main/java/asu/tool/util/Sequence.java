package asu.tool.util;

import java.util.Date;
import java.util.concurrent.atomic.AtomicLong;
import org.nutz.log.Log;
import org.nutz.log.Logs;

/**
 * 序列，使用单例的地方。
 * 如果是分布式使用，使用DistributedId。
 * 使用DistributedId，保证唯一，近似有序，精确到秒，1秒内可能会在计数器发生轮转回绕时，
 * 部分后请求的id小于前面的id。
 *
 * @author <a href="mailto:victor.su@gwtsz.net">Victor Su&lt;victor.su@gwtsz.net&gt;</a>
 * @version 1.0.0
 * @date 2016/7/11 15:55
 * @copyright 2016 Victor All rights reserved.
 * @since ${since}
 */
public class Sequence {
    static Log log = Logs.get();
    static Hex hex = new Hex();
    final AtomicLong seq;


    private static int dateToTimestampSeconds(final Date time) {
        return (int) (time.getTime() / 1000);
    }

    public Sequence() {
        // 10 位
        this((System.currentTimeMillis() / 1000) * 1000000000);
    }

    public Sequence(long initial) {
        seq = new AtomicLong(initial);
    }

    public long next() {
        return seq.incrementAndGet();
    }

    public String nextToString() {
        return hex.encodeHexString(Bytes.toBytes(seq.incrementAndGet()));
    }

    public static void main(String[] args) throws DecoderException {
        Sequence seq = new Sequence();
        System.out.println(seq.next());
        System.out.println(seq.next());
        System.out.println(seq.nextToString());
        System.out.println(seq.nextToString());
    }
}
