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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.nutz.lang.Strings;

/**
 * @author suk
 * @version 1.0.0
 */
@Data
@Slf4j
public class Dict {

    private static final String                  ACCEPT_KEYS = "0123456789abcdefghijklmnopqrstuvwxyz";
    private static final IntComparator           INT_CMP     = new IntComparator();
    private static final WordDataPhoneComparator WDP_CMP     = new WordDataPhoneComparator();
    private static final WordDataTextComparator  WDT_CMP     = new WordDataTextComparator();
    private static final PhraseDataComparator    PD_CMP      = new PhraseDataComparator();
    private static final char[]                  PHONES      = ACCEPT_KEYS.toCharArray();

    private static class DuplicatedPhraseException extends IllegalStateException {

        public DuplicatedPhraseException() {
            super();
        }

        public DuplicatedPhraseException(String s) {
            super(s);
        }

        public DuplicatedPhraseException(String message, Throwable cause) {
            super(message, cause);
        }

        public DuplicatedPhraseException(Throwable cause) {
            super(cause);
        }
    }

    private static class WrongInputFormatException extends IllegalStateException {

        public WrongInputFormatException() {
            super();
        }

        public WrongInputFormatException(String s) {
            super(s);
        }

        public WrongInputFormatException(String message, Throwable cause) {
            super(message, cause);
        }

        public WrongInputFormatException(Throwable cause) {
            super(cause);
        }
    }

    private static class InputNotCorrectException extends IllegalStateException {

        public InputNotCorrectException() {
            super();
        }

        public InputNotCorrectException(String s) {
            super(s);
        }

        public InputNotCorrectException(String message, Throwable cause) {
            super(message, cause);
        }

        public InputNotCorrectException(Throwable cause) {
            super(cause);
        }
    }

    private static class WordDataPhoneComparator implements Comparator<WordData> {

        @Override
        public int compare(WordData a, WordData b) {
            if (a == b) {
                return 0;
            } else if (b == null) {
                return 1;
            } else if (a == null) {
                return -1;
            } else {
                return b.getText().getPhones()[0] - a.getText().getPhones()[0];
            }
        }
    }

    private static class WordDataTextComparator implements Comparator<WordData> {

        @Override
        public int compare(WordData a, WordData b) {
            if (a == b) {
                return 0;
            } else if (b == null) {
                return 1;
            } else if (a == null) {
                return -1;
            } else {
                int ret = a.getText().getPhrase().compareTo(b.getText().getPhrase());
                if (ret != 0) {
                    return ret;
                }

                return a.getText().getPhones()[0] - b.getText().getPhones()[0];
            }
        }
    }

    private static class IntComparator implements Comparator<int[]> {

        @Override
        public int compare(int[] a, int[] b) {
            if (a == b) {
                return 0;
            } else if (a == null) {
                return -1;
            } else if (b == null) {
                return 1;
            } else {
                int i = 0;
                for (; i < a.length && i < b.length; i++) {
                    if (a[i] == b[i]) {
                        continue;
                    }
                    return a[i] - b[i];
                }
                if (i == b.length) {
                    return 1;
                } else if (i == a.length) {
                    return -1;
                } else {
                    return 0;
                }
            }
        }
    }

    private static class PhraseDataComparator implements Comparator<PhraseData> {

        @Override
        public int compare(PhraseData a, PhraseData b) {
            if (a == b) {
                return 0;
            } else if (b == null) {
                return 1;
            } else if (a == null) {
                return -1;
            } else {
                int ret = a.phrase.compareTo(b.phrase);
                if (ret == 0) {
                    return ret;
                }
                // Use Arrays.deepEquals for comparison of arrays that contain objects.
                // boolean isSame = Arrays.equals(a.phones, b.phones);
                ret = INT_CMP.compare(a.phones, b.phones);
                if (ret == 0) {
                    String msg = String.format("%s %s is the same", a, b);
                    throw new DuplicatedPhraseException();
                } else {
                    return ret;
                }
            }
        }
    }

    private static int convertKeyToPhone(char key) {
        if ('0' <= key && key <= '9' || 'a' <= key && key <= 'z') {
            int idx = Arrays.binarySearch(PHONES, key);
            if (idx == -1) {
                return -1;
            }
            return idx;
        } else {
            return -1;
        }
    }

    private static int[] EMPTY_CHARS = new int[0];

    private static int[] convertKeysToPhones(char[] keys) {
        if (keys == null) {
            return EMPTY_CHARS;
        }
        int[] ret = new int[keys.length];
        for (int i = 0; i < keys.length; i++) {
            char key = keys[i];
            int b = convertKeyToPhone(key);
            if (b == -1) {
                throw new InputNotCorrectException("Input is not correct: " + new String(keys));
            }
            ret[i] = b;
        }
        return ret;
    }

    /**
     * 读入的记录
     */
    List<PhraseData> phraseDataList = new ArrayList<>();

    /**
     * 唯一字符串
     */
    List<String> phrases = new ArrayList<>();

    /**
     * 索引树根节点
     */
    Node root;

    /**
     * 索引树存储空间
     */
    Node[] queue;
    int nodeNum;

    /**
     * 读取文件，每行一个记录 {@code word\tfreq\tcode}
     */
    public void readInputSrc(String filename) throws IOException {
        Path path = Paths.get(filename);
        Stream<String> lines = Files.lines(path);
        lines.forEach(this::storePhrase);
        Collections.sort(phraseDataList, PD_CMP);
    }

    /**
     * store a line data to phrases
     *
     * @param line data, format is {@code word\tfreq\tcode}
     */
    private void storePhrase(String line) {
        String buf = line;

        if (buf == null) {
            return;
        }
        buf = buf.trim();
        if (buf.isEmpty()) {
            return;
        }


        /* read phrase */
        String[] split = buf.split("[\\s+]");
        if (split.length != 3) {
            String msg = String.format("Error reading line %s", line);
            throw new WrongInputFormatException(msg);

        }

        String phrase = split[0];
        int freq = Integer.parseInt(split[1]);
        int[] phones = convertKeysToPhones(split[2].toCharArray());

        PhraseData phraseData = new PhraseData();
        phraseData.setPhrase(phrase);
        phraseData.setFreq(freq);
        phraseData.setPhones(phones);
        phraseDataList.add(phraseData);
    }

    /**
     * 提取唯一字符串，以及标记词的位置
     * <em>注意：此时phraseDataList已经排序。</em>
     */
    private void extractStringAndSetPosForPhraseData() {
        PhraseData curPhr = null;
        PhraseData lastPhr = null;
        for (int i = 0; i < phraseDataList.size(); lastPhr = curPhr, i++) {
            curPhr = phraseDataList.get(i);
            if (lastPhr != null) {
                if (curPhr.phrase.equals(lastPhr.phrase)) {
                    curPhr.pos = lastPhr.pos;
                    // 用位置表示，释放空间
                } else {
                    curPhr.pos = phrases.size();
                    phrases.add(curPhr.phrase.intern());
                }
                lastPhr.phrase = null;
            } else {
                curPhr.pos = phrases.size();
                phrases.add(curPhr.phrase.intern());
            }
        }
        if (lastPhr != null) {
            lastPhr.phrase = null;
        }
    }

    /**
     * 构建词树
     */
    public void constructPhraseTree() {
        Node level;
        int i;
        int j;

        createRootNode();

        /* insert phrases having length at least 2. */
        for (i = 0; i < phraseDataList.size(); ++i) {
            level = root;
            PhraseData phraseData = phraseDataList.get(i);
            for (j = 0; j <  phraseData.phones.length; ++j) {
                level = findOrInsert(level, phraseData.phones[j]);
            }
            insertLeaf(level, phraseData.pos, phraseData.freq);
        }
    }

    private void createRootNode() {
        root = new Node();
        nodeNum++;
        /* Key value of root will become tree_size later. */
        root.data.key = -1;
        root.data.childType = new ChildType();
    }

    /**
     * This function puts FindKey() and Insert() together. It first searches for the
     * specified key and performs FindKey() on hit. Otherwise, it inserts a new node
     * at proper position and returns the newly inserted child.
     */
    private Node findOrInsert(Node parent, int key) {
        Node prev = null;
        Node node = null;
        for (node = parent.firstChild; node != null && node.data.key <= key;
                prev = node, node = node.nextSibling) {
            if (node.data.key == key) {
                return node;
            }
        }

        Node newNode = new Node();
        nodeNum++;
        newNode.data.key = key;
        newNode.data.childType = new ChildType();
        newNode.nextSibling = node;
        if (prev == null) {
            parent.firstChild = newNode;
        } else {
            prev.nextSibling = newNode;
        }
        newNode.nextSibling = node;
        return newNode;
    }

    /**
     * 插入词节点
     */
    private void insertLeaf(Node parent, int phrPos, int freq) {
        Node prev = null;
        Node node;
        Node newNode;

        for (node = parent.firstChild; node != null && node.data.key == 0;
                prev = node, node = node.nextSibling) {
            // find the last
            ;
        }

        newNode = new Node();
        nodeNum++;
        newNode.data.key = 0;
        PhraseType phraseType = new PhraseType();
        newNode.data.phraseType = phraseType;
        phraseType.setFreq(freq);
        phraseType.setPos(phrPos);
        if (prev == null) {
            parent.firstChild = newNode;
        } else {
            prev.nextSibling = newNode;
        }
        newNode.nextSibling = node;
    }

    /*
     * This function performs BFS to compute child.begin and child.end of each node.
     * It sponteneously converts tree structure into a linked list. Writing the tree
     * into index file is then implemented by pure sequential traversal.
     */
    Node[] makeIndexTree() {
        /* (Circular) queue implementation is hidden within this function. */
        Node p = null;
        int head = 0, tail = 0;
        int tree_size = 1;
        int q_len = nodeNum;
        queue = new Node[q_len];
        queue[head++] = root;
        while (head != tail) {
            p = queue[tail++];
            if (tail >= q_len) {
                tail = 0;
            }
            if (p.data.key != 0) {
                // 非叶子节点
                 p.data.childType.setBegin(tree_size);

                /*
                 * The latest inserted element must have a NULL
                 * pNextSibling value, and the following code let
                 * it point to the next child list to serialize
                 * them.
                 */
                if (head == 0) {
                    queue[q_len - 1].nextSibling = p.firstChild;
                } else {
                    queue[head - 1].nextSibling = p.firstChild;
                }
                for (Node pNext = p.firstChild; pNext != null; pNext = pNext.nextSibling) {
                    queue[head++] = pNext;
                    if (head == q_len) {
                        head = 0;
                    }
                    tree_size++;
                }

                 p.data.childType.setEnd(tree_size);
            }
        }
        root.data.key = tree_size;
        return queue;
    }

    public void build() {
        extractStringAndSetPosForPhraseData();
        constructPhraseTree();
        makeIndexTree();
    }

    public String getPhrase(int i) {
        if (0 <= i && i < phrases.size()) {
            return phrases.get(i);
        }
        return "";
    }
    public Node search(String input) {
        if (Strings.isBlank(input)) {
            return null;
        }
        char[] chars = input.toCharArray();
        // or queue[0]
        Node p = root;
        for (int i = 0; i < chars.length; i++) {
            int key = convertKeyToPhone(chars[i]);
            ChildType ct =  p.data.childType;
            Node node = new Node();
            TreeType treeType = new TreeType();
            treeType.setKey(key);
            node.setData(treeType);
            int idx = Arrays
                    .binarySearch(queue, ct.getBegin(), ct.getEnd(), node, new Comparator<Node>() {
                        @Override
                        public int compare(Node o1, Node o2) {
                            if (o1 == o2) {
                                return 0;
                            } else if (o2 == null) {
                                return 1;
                            } else if (o1 == null) {
                                return -1;
                            } else {
                                return o1.getData().getKey() - o2.getData().getKey();
                            }
                        }
                    });
            if (idx < 0) {
                return null;
            }
            p = queue[idx];
        }
        return p;
    }


    public static void main(String[] args) throws IOException {

    }

    @Data
    static class PhraseData {

        String phrase;
        int[]  phones;
        int    freq;
        int    pos;
    }

    @Data
    static class WordData {

        PhraseData text;
        int        index;
    }

    @Data
    static class TreeType {

        int        key;
        ChildType childType = new ChildType();
        PhraseType phraseType = new PhraseType();
    }

    @Data
    static class TwoIntPair {

        int first;
        int second;
    }

    @Data
    static class ChildType extends TwoIntPair {

        public int getBegin() {
            return getFirst();
        }

        public int getEnd() {
            return getSecond();
        }

        public void setBegin(int begin) {
            setFirst(begin);
        }

        public void setEnd(int end) {
            setSecond(end);
        }
    }

    @Data
    static class PhraseType extends TwoIntPair {

        public int getPos() {
            return getFirst();
        }

        public int getFreq() {
            return getSecond();
        }

        public void setPos(int pos) {
            setFirst(pos);
        }

        public void setFreq(int freq) {
            setSecond(freq);
        }
    }

    @Data
    static class Node {

        TreeType data = new TreeType();
        Node     firstChild;
        Node     nextSibling;
    }


}
