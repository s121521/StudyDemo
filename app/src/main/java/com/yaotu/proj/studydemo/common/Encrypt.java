package com.yaotu.proj.studydemo.common;

public class Encrypt {
    /**
     * 对用户的密码进行加密
     *
     * @param sPlainText－密码
     * @throws Exception
     * @return－加密后的密码
     */
    public static String encryptPassword(String sPlainText) throws java.io.UnsupportedEncodingException {
        StringBuffer st = new StringBuffer();
        byte[] aryByte = {0, 0};
        byte[] aryBytePlainText = null;
        try {
            for (int i = sPlainText.length() - 1; i >= 0; i--) {
                aryBytePlainText = sPlainText.substring(i, i + 1).getBytes("GBK");
                aryByte[0] = (byte) (((aryBytePlainText[0] & 0x0F)) | 0xB0);
                aryByte[1] = (byte) (((aryBytePlainText[0] & 0xF0) >>> 4) | 0xA0);
                st.insert(0, new String(aryByte, "GBK"));
            }
        } catch (java.io.UnsupportedEncodingException e) {
            System.out.println("Encrypt.encryptPassword():" + e.getMessage());
        }
        return st.toString();
    }
}
