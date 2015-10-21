package com.mucommander.commons.file.impl.sevenzip.provider.Common;

public class CRC {
    static public int[] Table = new int[256];
    
    static {
        for (int i = 0; i < 256; i++) {
            int r = i;
            for (int j = 0; j < 8; j++)
                if ((r & 1) != 0)
                    r = (r >>> 1) ^ 0xEDB88320;
                else
                    r >>>= 1;
            Table[i] = r;
        }
    }
    
    int _value = -1;
    
    public void Init() {
        _value = -1;
    }
    
    public void updateByte(int b) {
        _value = Table[(_value ^ b) & 0xFF] ^ (_value >>> 8);
    }
    
    public void updateUInt32(int v) {
        for (int i = 0; i < 4; i++)
            updateByte((v >> (8 * i)) & 0xFF);
    }
    
    public void updateUInt64(long v) {
        for (int i = 0; i < 8; i++)
            updateByte((int) ((v >> (8 * i))) & 0xFF);
    }
    
    public int getDigest() {
        return ~_value;
    }
    
    public void Update(byte[] data, int size) {
        for (int i = 0; i < size; i++)
            _value = Table[(_value ^ data[i]) & 0xFF] ^ (_value >>> 8);
    }
    
    public void Update(byte[] data) {
        for (byte aData : data) _value = Table[(_value ^ aData) & 0xFF] ^ (_value >>> 8);
    }
    
    public void Update(byte[] data, int offset, int size) {
        for (int i = 0; i < size; i++)
            _value = Table[(_value ^ data[offset + i]) & 0xFF] ^ (_value >>> 8);
    }
    
    public static int calculateDigest(byte[] data, int size) {
        CRC crc = new CRC();
        crc.Update(data, size);
        return crc.getDigest();
    }
    
    static public boolean verifyDigest(int digest, byte[] data, int size) {
        return (calculateDigest(data, size) == digest);
    }
}
