package asu.tool.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.Deflater;
import java.util.zip.DeflaterInputStream;
import java.util.zip.DeflaterOutputStream;
import org.nutz.lang.Streams;

/**
 * Deflate Utils
 *
 * @author <a href="mailto:victor.su@gwtsz.net">Victor Su&lt;victor.su@gwtsz.net&gt;</a>
 * @version 1.0.0
 * @date 2016/7/13 10:37
 * @copyright 2016 Victor All rights reserved.
 * @since ${since}
 */
public class DeflaterUtils {
    public static void compressBest(InputStream in, OutputStream out) {
        Deflater compressor = new Deflater();
        compressor.setLevel(Deflater.BEST_COMPRESSION);
        DeflaterOutputStream dout = new DeflaterOutputStream(out, compressor);
        Streams.writeAndClose(dout, in);
    }

    public static void compressFast(InputStream in, OutputStream out) {
        Deflater compressor = new Deflater();
        compressor.setLevel(Deflater.BEST_SPEED);
        DeflaterOutputStream dout = new DeflaterOutputStream(out, compressor);
        Streams.writeAndClose(dout, in);
    }

    public static void uncompress(InputStream in, OutputStream out) {
        Deflater compressor = new Deflater();
        compressor.setLevel(Deflater.BEST_SPEED);
        DeflaterInputStream din = new DeflaterInputStream(in, compressor);
        Streams.writeAndClose(out, din);
    }

    public static byte[] compressBest(byte[] data) {
        ByteArrayInputStream in = new ByteArrayInputStream(data);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        compressBest(in, out);
        return out.toByteArray();
    }

    public static byte[] compressFast(byte[] data) {
        ByteArrayInputStream in = new ByteArrayInputStream(data);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        compressFast(in, out);
        return out.toByteArray();
    }

    public static byte[] uncompress(byte[] data) {
        ByteArrayInputStream in = new ByteArrayInputStream(data);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        uncompress(in, out);
        return out.toByteArray();
    }
}
