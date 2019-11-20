package com.asto.recyclingbins.util;

/**
 * 循环冗余校验工具类(CRC-8/MAXIM)
 */
public final class Crc8 {

    private static Crc8 instance;
    private final byte initial = 0x00;
    private final byte finalXor = 0x00;
    private final boolean inputReflected = true;
    private final boolean resultReflected = true;

    private final int[] crcTable = new int[]{0, 49, 98, 83, 196, 245, 166, 151, 185, 136, 219, 234, 125, 76, 31, 46, 67, 114, 33, 16, 135, 182, 229, 212, 250, 203, 152, 169, 62, 15, 92, 109, 134, 183, 228, 213, 66, 115, 32, 17, 63, 14, 93, 108, 251, 202, 153, 168, 197, 244, 167, 150, 1, 48, 99, 82, 124, 77, 30, 47, 184, 137, 218, 235, 61, 12, 95, 110, 249, 200, 155, 170, 132, 181, 230, 215, 64, 113, 34, 19, 126, 79, 28, 45, 186, 139, 216, 233, 199, 246, 165, 148, 3, 50, 97, 80, 187, 138, 217, 232, 127, 78, 29, 44, 2, 51, 96, 81, 198, 247, 164, 149, 248, 201, 154, 171, 60, 13, 94, 111, 65, 112, 35, 18, 133, 180, 231, 214, 122, 75, 24, 41, 190, 143, 220, 237, 195, 242, 161, 144, 7, 54, 101, 84, 57, 8, 91, 106, 253, 204, 159, 174, 128, 177, 226, 211, 68, 117, 38, 23, 252, 205, 158, 175, 56, 9, 90, 107, 69, 116, 39, 22, 129, 176, 227, 210, 191, 142, 221, 236, 123, 74, 25, 40, 6, 55, 100, 85, 194, 243, 160, 145, 71, 118, 37, 20, 131, 178, 225, 208, 254, 207, 156, 173, 58, 11, 88, 105, 4, 53, 102, 87, 192, 241, 162, 147, 189, 140, 223, 238, 121, 72, 27, 42, 193, 240, 163, 146, 5, 52, 103, 86, 120, 73, 26, 43, 188, 141, 222, 239, 130, 179, 224, 209, 70, 119, 36, 21, 59, 10, 89, 104, 255, 206, 157, 172};

    private Crc8() {
    }

    public static Crc8 getInstance() {
        return instance == null ? instance = new Crc8() : instance;
    }

    /**
     * 生成CRC
     *
     * @param string
     * @return
     */
    public String compute(String string) {
        String[] stringArray = string.split(" ");
        if (stringArray.length <= 1) {
            stringArray = new String[string.length() / 2];
            char[] charArray = string.toCharArray();
            String s16 = "";
            int position = 0;
            for (char c : charArray) {
                s16 += c;
                if (s16.length() == 2) {
                    stringArray[position] = s16;
                    position++;
                    s16 = "";
                }
            }
        }

        int[] data = new int[stringArray.length];
        for (int i = 0; i < stringArray.length; i++) {
            data[i] = Integer.parseInt(stringArray[i], 16);
        }
        int crc = initial;
        for (int b : data) {
            int curByte = (inputReflected ? reflect8(b) : b);
            int i = (int) (curByte ^ crc);
            crc = (int) (crcTable[i]);
        }
        crc = (resultReflected ? reflect8(crc) : crc);
        String text = Integer.toHexString((int) (crc ^ finalXor));
        if (text.length() == 1)
            text = "0" + text;
        return text.toLowerCase();
    }

    private int reflect8(int val) {
        int resByte = 0;
        for (int i = 0; i < 8; i++) {
            if ((val & (1 << i)) != 0) {
                resByte |= (int) (1 << (7 - i));
            }
        }
        return resByte;
    }
}