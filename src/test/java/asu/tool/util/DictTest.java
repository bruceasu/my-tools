/*
 * Copyright (c) 2018 Suk Honzeon
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package asu.tool.util;

import static org.junit.Assert.*;

import asu.tool.util.Dict.ChildType;
import asu.tool.util.Dict.Node;
import asu.tool.util.Dict.PhraseData;
import asu.tool.util.Dict.PhraseType;
import asu.tool.util.Dict.TreeType;
import java.io.IOException;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by suk on 2018/7/30.
 *
 * @author suk
 * @version 1.0.0
 * @since 2018/7/30
 */
public class DictTest {
    Dict dict = new Dict();

    @Before
    public void setup() throws IOException {
        dict = new Dict();
        dict.readInputSrc("D:\\03_projects\\suk\\my-tools\\src\\test\\resources\\test.txt");
        dict.build();
    }

    @Test
    public void test() {
        Node nihao = dict.search("nihao");
        System.out.println("nihao = " + nihao);
        TreeType data = nihao.getData();
        System.out.println(data.childType.getBegin());
        System.out.println(data.childType.getEnd());
        System.out.println(data.phraseType.getPos());
        System.out.println(data.phraseType.getFreq());
    }
}